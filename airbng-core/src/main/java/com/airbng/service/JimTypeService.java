package com.airbng.service;

import com.airbng.domain.base.BaseStatus;
import com.airbng.dto.jimType.JimTypeResponse;
import com.airbng.repository.JimTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JimTypeService {

    private final JimTypeRepository jimTypeRepository;

    public List<JimTypeResponse> findAllJimTypes() {
        return jimTypeRepository.findAllByStatus(BaseStatus.ACTIVE)
                .stream()
                .map(JimTypeResponse::from)
                .collect(Collectors.toList());
    }

}
