package com.airbng.service;

import com.airbng.common.response.status.BaseResponseStatus;

public interface ZzimService {

    boolean isExistZzim(Long memberId, Long lockerId);

    BaseResponseStatus toggleZzim(Long sessionMemberId, Long memberId, Long lockerId);

}