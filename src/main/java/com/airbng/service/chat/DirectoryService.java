package com.airbng.service.chat;

import com.airbng.dto.ws.UserCardResponse;
import java.util.Collection;
import java.util.List;

public interface DirectoryService {
    List<UserCardResponse> getUserCards(Collection<Long> userIds);
}
