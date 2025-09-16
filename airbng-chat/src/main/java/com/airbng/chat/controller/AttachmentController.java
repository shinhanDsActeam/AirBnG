package com.airbng.chat.controller;

import com.airbng.chat.domain.Attachment;
import com.airbng.chat.domain.Message;
import com.airbng.chat.service.AttachmentService;
import com.airbng.platform.common.response.BaseResponse;
import com.airbng.platform.security.principal.AirbngPrincipal;
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

    /**
     * 파일 업로드 + 메시지 생성 (image|file)
     * POST /chat/attachments/conversations/{convId}?kind=image&msgId=uuid
     * multipart/form-data: file
     */
    @PostMapping(path = "/conversations/{convId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<Message> upload(@PathVariable String convId,
                                        @RequestParam("file") MultipartFile file,
                                        @RequestParam("kind") @NotBlank String kind,   // image|file
                                        @RequestParam("msgId") @NotBlank String msgId,
                                        Authentication auth) {
        AirbngPrincipal p = (AirbngPrincipal) auth.getPrincipal(); // ← 공통 인터페이스로 캐스팅
        long me = p.getId();
        String name = p.getNickname();

        Message saved = attachmentService.uploadAndSend(convId, me, name, file, kind, msgId);

        // 방 브로드캐스트 (원한다면)
        messagingTemplate.convertAndSend("/topic/conversations." + convId, saved);
        return new BaseResponse<>(saved);
    }

    /**
     * 메시지별 첨부 목록 조회
     * GET /chat/attachments/by-message/{msgId}
     */
    @GetMapping("/by-message/{msgId}")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<List<Attachment>> getByMessage(@PathVariable String msgId) {
        return new BaseResponse<>(attachmentService.findByMessageId(msgId));
    }

    /**
     * 첨부 1건 삭제
     * DELETE /chat/attachments/{attachmentId}
     */
    @DeleteMapping("/{attachmentId}")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<Message> deleteOne(@PathVariable String attachmentId,
                                           Authentication auth) {
        AirbngPrincipal p = (AirbngPrincipal) auth.getPrincipal();
        long me = p.getId();

        Message updated = attachmentService.deleteAttachment(attachmentId, me);

        // 방에 업데이트 브로드캐스트
        messagingTemplate.convertAndSend("/topic/conversations." + updated.getConvId(), updated);
        return new BaseResponse<>(updated);
    }
}
