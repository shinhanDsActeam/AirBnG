package com.airbng.service.chat;

import com.airbng.domain.chat.Inbox;
import com.airbng.domain.chat.model.LastMessage;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

public interface InboxService {

    /** 새 메시지 발생 시 인박스 upsert & unread 반영 */
    void onNewMessage(long userId, long peerId, String convId, LastMessage last, Instant at, long senderId);

    /** 읽음 처리(lastReadSeq=max, cachedUnread 갱신) */
    void markRead(long userId, String convId, long lastSeenSeq);

    /** 인박스 목록(정렬: lastMessageAt desc) */
    List<Inbox> getInbox(long userId, Pageable pageable);

    /** 인박스 단건(convId 기준) */
    Inbox getOne(long userId, String convId);

    /** cachedUnread 를 +1 하고 증가된 총합을 반환 */
    int increaseUnreadAndGet(long userId, String convId);

    int totalUnread(long userId);
}
