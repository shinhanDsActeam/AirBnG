package com.airbng.service.chat;

import com.airbng.domain.chat.Attachment;
import com.airbng.repository.chat.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;

    @Override
    public Attachment save(Attachment a) {
        return attachmentRepository.save(a);
    }

    @Override
    public List<Attachment> findByMessageId(String messageId) {
        return attachmentRepository.findByMessageId(messageId);
    }
}
