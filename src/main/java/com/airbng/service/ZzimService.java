package com.airbng.service;

import com.airbng.domain.Zzim;

import java.util.List;

public interface ZzimService {

    boolean isExistZzim(Long memberId, Long lockerId);

    boolean toggleZzim(Long memberId, Long lockerId);

}