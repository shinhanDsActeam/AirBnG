package com.airbng.controller.chat;

import com.airbng.common.response.BaseResponse;
import com.airbng.domain.chat.Message;
import com.airbng.dto.chat.SendTextRequest;
import com.airbng.security.domain.CustomUserDetails;
import com.airbng.service.chat.ConversationService;
import com.airbng.service.chat.MessageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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

    /**
     * 메시지 목록 조회 (최신부터 size개, beforeSeq가 있으면 그 이전으로 페이징)
     * GET /chat/conversations/{convId}/messages?beforeSeq=123&size=30
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<List<Message>> list(@PathVariable String convId,
                                            @RequestParam(required = false) Long beforeSeq,
                                            @RequestParam(defaultValue = "30") @Min(1) int size,
                                            Authentication auth) {
        long me = ((CustomUserDetails) auth.getPrincipal()).getId();
        // 데이터 노출 방지: 내가 참여한 방만 조회 허용
        conversationService.assertMember(convId, me);

        List<Message> result = messageService.getMessages(convId, beforeSeq, size);
        return new BaseResponse<>(result);
    }

    /**
     * 텍스트 전송 (멱등 msgId 필요)
     * POST /chat/conversations/{convId}/messages/text
     * Body: { "text": "...", "msgId": "uuid-..." }
     */
    @PostMapping("/text")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<Message> sendText(@PathVariable String convId,
                                          @Valid @RequestBody SendTextRequest req,
                                          Authentication auth) {

        long me = ((CustomUserDetails) auth.getPrincipal()).getId();
        String name = ((CustomUserDetails) auth.getPrincipal()).getNickname();

        Message saved = messageService.sendText(convId, me, name, req.getText(), req.getMsgId());

        // 방 구독자에게 실시간으로도 뿌리고 싶다면 사용
        // 프론트 구독 경로: /topic/conversations.{convId}
        if (messagingTemplate != null) {
            messagingTemplate.convertAndSend("/topic/conversations." + convId, saved);
        }

        return new BaseResponse<>(saved);
    }

}
