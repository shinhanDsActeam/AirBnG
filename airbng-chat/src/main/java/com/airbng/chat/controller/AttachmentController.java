package com.airbng.chat.controller;

import com.airbng.chat.domain.Attachment;
import com.airbng.chat.domain.Message;
import com.airbng.chat.dto.chat.AttachmentDto;
import com.airbng.chat.dto.chat.MessageDto;
import com.airbng.chat.repository.AttachmentRepository;
import com.airbng.chat.service.AttachmentService;
import com.airbng.platform.common.response.BaseResponse;
import com.airbng.platform.security.principal.AirbngPrincipal;
import com.airbng.platform.util.S3Utils;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/chat/attachments")
@RequiredArgsConstructor
@Validated
public class AttachmentController {

    private final AttachmentService attachmentService;
    private final SimpMessagingTemplate messagingTemplate; // 선택: 실시간 브로드캐스트

    private final S3Utils s3;
    private final AttachmentRepository attachmentRepository;

    private String sign(String key){ return s3.presignGetUrl(key, 60*60); } // 1시간 (원하면 24h)
    private String findKey(String attId){
        return attachmentRepository.findById(attId).map(Attachment::getKey).orElse(null);
    }

    /**
     * 파일 업로드 + 메시지 생성 (image|file)
     * POST /chat/attachments/conversations/{convId}?kind=image&msgId=uuid
     * multipart/form-data: file
     */
    @PostMapping(path = "/conversations/{convId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<MessageDto> upload(@PathVariable String convId,
                                           @RequestParam("file") MultipartFile file,
                                           @RequestParam("kind") @NotBlank String kind,   // image|file
                                           @RequestParam("msgId") @NotBlank String msgId,
                                           Authentication auth) {
        AirbngPrincipal p = (AirbngPrincipal) auth.getPrincipal();
        long me = p.getId();
        String name = p.getNickname();

        Message saved = attachmentService.uploadAndSend(convId, me, name, file, kind, msgId);

        MessageDto dto = MessageDto.from(saved, this::sign, this::findKey);
        messagingTemplate.convertAndSend("/topic/conversations." + convId, dto);
        return new BaseResponse<>(dto);
    }

    /**
     * 메시지별 첨부 목록 조회
     * GET /chat/attachments/by-message/{msgId}
     */
    @GetMapping("/by-message/{msgId}")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<List<AttachmentDto>> getByMessage(@PathVariable String msgId) {
        List<AttachmentDto> list = attachmentService.findByMessageId(msgId).stream()
                .map(att -> new AttachmentDto(
                        att.getId(),
                        att.getKind(),
                        att.getMime(),
                        att.getSize(),
                        att.getWidth(),
                        att.getHeight(),
                        att.getFileName(),
                        att.getKey() != null ? sign(att.getKey()) : att.getImageUrl() // key 우선, 없으면 레거시 URL
                ))
                .toList();
        return new BaseResponse<>(list);
    }

    /**
     * 첨부 1건 삭제
     * DELETE /chat/attachments/{attachmentId}
     */
    @DeleteMapping("/{attachmentId}")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<MessageDto> deleteOne(@PathVariable String attachmentId,
                                              Authentication auth) {
        AirbngPrincipal p = (AirbngPrincipal) auth.getPrincipal();
        long me = p.getId();

        Message updated = attachmentService.deleteAttachment(attachmentId, me);

        MessageDto dto = MessageDto.from(updated, this::sign, this::findKey);
        messagingTemplate.convertAndSend("/topic/conversations." + updated.getConvId(), dto);
        return new BaseResponse<>(dto);
    }
}
