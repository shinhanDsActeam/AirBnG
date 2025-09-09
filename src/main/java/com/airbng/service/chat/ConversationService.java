package com.airbng.service.chat;

import com.airbng.domain.chat.Conversation;
import com.airbng.domain.chat.model.LastMessage;

public interface ConversationService {

    /** convId("min:max") 계산 */
    String makeConvId(long userA, long userB);

    /** 없으면 생성 */
    Conversation getOrCreate(long userA, long userB);

    /** convId 기준으로 조회(없으면 null) */
    Conversation findById(String convId);

    /** convId에 userId가 참여중인지 보장(아니면 IllegalArgumentException) */
    void assertMember(String convId, long userId);

    /** convId에서 userId의 상대 id 반환 */
    long peerIdOf(String convId, long userId);

    /** 새 메시지 저장 이후 lastMessage/highestSeq 갱신 */
    void updateOnNewMessage(String convId, LastMessage last, long seq);
}
