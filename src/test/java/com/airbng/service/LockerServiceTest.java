package com.airbng.service;

import com.airbng.common.exception.LockerException;
import com.airbng.dto.LockerPreviewResult;
import com.airbng.dto.LockerTop5Response;
import com.airbng.mappers.LockerMapper;
import com.airbng.util.S3Uploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.airbng.common.response.status.BaseResponseStatus.NOT_FOUND_LOCKER;
import static junit.framework.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)  // JUnit5에서 Mockito 사용 시 필수
class LockerServiceTest {

    @Mock
    private LockerMapper lockerMapper;

    @Mock
    private S3Uploader s3Uploader;

    @InjectMocks
    private LockerServiceImpl lockerService;

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

        LockerException exception = assertThrows(LockerException.class, () -> lockerService.findTop5Locker());
        assertEquals(NOT_FOUND_LOCKER.getMessage(), exception.getMessage());
    }
}