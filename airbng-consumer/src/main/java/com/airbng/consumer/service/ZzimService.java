package com.airbng.consumer.service;

import com.airbng.platform.common.response.status.BaseResponseStatus;

public interface ZzimService {

    boolean isExistZzim(Long memberId, Long lockerId);

    BaseResponseStatus toggleZzim(Long memberId, Long lockerId);

}