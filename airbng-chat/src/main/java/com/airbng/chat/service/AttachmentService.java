package com.airbng.chat.service;

import com.airbng.chat.domain.Attachment;
import com.airbng.chat.domain.Message;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AttachmentService {
    Attachment save(Attachment a);
    List<Attachment> findByMessageId(String messageId);

    // 채팅: 파일 업로드(S3) + Attachment 저장 + Message(type=image|file) 생성
    Message uploadAndSend(String convId, long senderId, String senderName,
                          MultipartFile file, String kind, String msgId);

    /** 첨부 1건 삭제(S3 + attachment 문서 + message 갱신 + lastMessage/inbox 반영) */
    Message deleteAttachment(String attachmentId, long requesterId);
}
