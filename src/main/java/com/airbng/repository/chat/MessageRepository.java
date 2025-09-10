package com.airbng.repository.chat;

import com.airbng.domain.chat.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends MongoRepository<Message, String> {
    boolean existsByConvIdAndMsgId(String convId, String msgId);
    Optional<Message> findByConvIdAndMsgId(String convId, String msgId);

    // 가벼운 조회용 (완전한 페이징은 MongoTemplate로 아래 ServiceImpl에서 처리)
    List<Message> findTop50ByConvIdOrderBySeqDesc(String convId);

    // MessageRepository 일부에 파생쿼리 추가(선택)
    List<Message> findByConvIdOrderBySeqDesc(String convId, Pageable pageable);
    List<Message> findByConvIdAndSeqLessThanOrderBySeqDesc(String convId, Long beforeSeq, Pageable pageable);

    // 가장 최근 메시지 1개 조회
    Optional<Message> findFirstByConvIdAndDeletedFalseOrderBySeqDesc(String convId);
}