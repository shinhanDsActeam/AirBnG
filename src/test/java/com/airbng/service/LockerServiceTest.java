package com.airbng.service;

import com.airbng.common.exception.LockerException;
import com.airbng.dto.LockerPreviewResult;
import com.airbng.dto.LockerTop5Response;
import com.airbng.mappers.LockerMapper;
import com.airbng.util.S3Uploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class LockerServiceTest {

    private LockerMapper lockerMapper;
    private S3Uploader s3Uploader;
    private LockerServiceImpl lockerService;

    @BeforeEach
    void setUp() {
        lockerMapper = mock(LockerMapper.class);
        s3Uploader = mock(S3Uploader.class);
        lockerService = new LockerServiceImpl(lockerMapper, s3Uploader);

    }

    @Test
    @DisplayName("정상적으로 상위 5개의 항목이 보입니다.")
    void 인기_보관_지역_조회_성공() throws Exception{
        //given
        List<LockerPreviewResult> results = List.of(
                LockerPreviewResult.builder().lockerId(1L).build(),
                LockerPreviewResult.builder().lockerId(2L).build(),
                LockerPreviewResult.builder().lockerId(3L).build(),
                LockerPreviewResult.builder().lockerId(4L).build(),
                LockerPreviewResult.builder().lockerId(5L).build()
        );

        when(lockerMapper.findTop5Lockers(Mockito.any()))
                .thenReturn(results);

        //when
        LockerTop5Response response = lockerService.findTop5Locker();

        //then
        assertEquals(response.getLockers().size(),5);
    }

    @Test
    @DisplayName("결과가 없을 시 예외처리 됩니다.")
    void 결과가_없을시_예외처리_됩니다() {
        //given
        List<LockerPreviewResult> results = List.of(
        );

        when(lockerMapper.findTop5Lockers(Mockito.any()))
                .thenReturn(results);

        assertThrows(LockerException.class, () -> lockerService.findTop5Locker(),"락커를 찾을 수 없습니다.");
    }
}