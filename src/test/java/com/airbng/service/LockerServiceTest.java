package com.airbng.service;

import com.airbng.common.exception.LockerException;
import com.airbng.dto.*;
import com.airbng.mappers.LockerMapper;
import com.airbng.util.S3Uploader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.airbng.common.response.status.BaseResponseStatus.NOT_FOUND_LOCKER;
import static junit.framework.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LockerServiceTest {

    @Mock
    private LockerMapper lockerMapper;

    @Mock
    private S3Uploader s3Uploader;

    @InjectMocks
    private LockerServiceImpl lockerService;

    @Test
    @DisplayName("보관함 검색 결과 정상 반환 테스트")
    void 검색결과_정상_반환_테스트() {

        // 상황에 맞는 Mock 객체 생성
        LockerPreviewResult lockerPreviewResult = LockerPreviewResult.builder()
                .lockerId(1L)
                .address("서울시 강남구 역삼동")
                .lockerName("강남 보관함")
                .isAvailable("Y")
                .url("http://example.com/image.jpg")
                .jimTypeResults(List.of(JimTypeResult.builder().jimTypeId(1L).build(),
                                         JimTypeResult.builder().jimTypeId(2L).build(),
                                         JimTypeResult.builder().jimTypeId(3L).build()))
                .build();

        LockerSearchRequest request = LockerSearchRequest.builder()
                .jimTypeId(List.of(1L, 2L, 3L))
                .address("서울시 강남구 역삼동")
                .lockerName("강남 보관함")
                .build();

        // 가상의 Mock 동작 설정
        Mockito.when(lockerMapper.findAllLockerBySearch(request))
                .thenReturn(List.of(lockerPreviewResult));

        // 실제 서비스 메서드 호출
        LockerSearchResponse response = lockerService.findAllLockerBySearch(request);

        // 결과 검증(실제 서비스 메서드가 반환하는 값과 Mock 객체의 값이 일치하는지 확인)
        assertEquals(1, response.getLockers().size());
        assertEquals("강남 보관함", response.getLockers().get(0).getLockerName());
    }

    @Test
    @DisplayName("보관함 검색 결과 예외 반환 테스트")
    void 검색결과_예외_반환_테스트() {
        LockerPreviewResult lockerPreviewResult = LockerPreviewResult.builder()
                .lockerId(1L)
                .address("서울시 강남구 역삼동")
                .lockerName("강남 보관함")
                .isAvailable("Y")
                .url("http://example.com/image.jpg")
                .jimTypeResults(List.of(JimTypeResult.builder().jimTypeId(1L).build(),
                        JimTypeResult.builder().jimTypeId(2L).build(),
                        JimTypeResult.builder().jimTypeId(3L).build()))
                .build();

        LockerSearchRequest request = LockerSearchRequest.builder()
                .jimTypeId(null)
                .address(null)
                .lockerName(null)
                .build();

        LockerException exception = assertThrows(LockerException.class, ()->lockerService.findAllLockerBySearch(request) );

        assertEquals(NOT_FOUND_LOCKER,exception.getBaseResponseStatus());
    }

}