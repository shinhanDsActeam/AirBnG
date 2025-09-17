package com.airbng.chat.controller;

import com.airbng.chat.domain.Attachment;
import com.airbng.chat.domain.Message;
import com.airbng.chat.dto.chat.MessageDto;
import com.airbng.chat.dto.chat.SendTextRequest;
import com.airbng.chat.repository.AttachmentRepository;
import com.airbng.chat.service.ConversationService;
import com.airbng.chat.service.MessageService;
import com.airbng.platform.common.response.BaseResponse;
import com.airbng.platform.security.principal.AirbngPrincipal;
import com.airbng.platform.util.S3Utils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat/conversations/{convId}/messages")
@RequiredArgsConstructor
@Validated
public class MessageController {

    private final MessageService messageService;
    private final ConversationService conversationService;

    // REST로 보낸 후에도 방 구독자에게 실시간 반영하려면 주입해서 사용
    private final SimpMessagingTemplate messagingTemplate;

    private final S3Utils s3;
    private final AttachmentRepository attachmentRepository;

    private String sign(String key){ return s3.presignGetUrl(key, 60*60); } // 1시간 (원하면 24h)
    private String findKey(String attId){
        return attachmentRepository.findById(attId).map(Attachment::getKey).orElse(null);
    }

    /**
     * 메시지 목록 조회 (최신부터 size개, beforeSeq가 있으면 그 이전으로 페이징)
     * GET /chat/conversations/{convId}/messages?beforeSeq=123&size=30
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<List<MessageDto>> list(@PathVariable String convId,
                                               @RequestParam(required = false) Long beforeSeq,
                                               @RequestParam(defaultValue = "30") @Min(1) int size,
                                               Authentication auth) {
        long me = ((AirbngPrincipal) auth.getPrincipal()).getId();
        conversationService.assertMember(convId, me);

        var rows = messageService.getMessages(convId, beforeSeq, size);
        var dto  = rows.stream()
                .map(m -> MessageDto.from(m, this::sign, this::findKey))
                .toList();
        return new BaseResponse<>(dto);
    }

    /**
     * 텍스트 전송 (멱등 msgId 필요)
     * POST /chat/conversations/{convId}/messages/text
     * Body: { "text": "...", "msgId": "uuid-..." }
     */
    @PostMapping("/text")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<MessageDto> sendText(@PathVariable String convId,
                                             @Valid @RequestBody SendTextRequest req,
                                             Authentication auth) {
        long me = ((AirbngPrincipal) auth.getPrincipal()).getId();
        String name = ((AirbngPrincipal) auth.getPrincipal()).getNickname();

        var saved = messageService.sendText(convId, me, name, req.getText(), req.getMsgId());
        var dto   = MessageDto.from(saved, this::sign, this::findKey);

        if (messagingTemplate != null) {
            // 그대로 DTO만 내보기
             messagingTemplate.convertAndSend("/topic/conversations." + convId, dto);
        }
        return new BaseResponse<>(dto);
    }

}
