package com.airbng.chat.dto.ws;

/** 특정 유저의 마지막 읽은 시퀀스 스냅샷 */
public record ReadSyncResponse(
    long userId,
    long lastReadSeq
) {}