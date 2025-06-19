package com.airbng.service;

import com.airbng.common.exception.LockerException;
import com.airbng.common.response.status.BaseResponseStatus;
import com.airbng.dto.LockerDetailResponse;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
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

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // @Mock과 @InjectMocks 초기화
    }

    @DisplayName("보관소 상세 조회")
    @Test
    @Transactional
    void findUserById_정상_조회() {
        // given
        Long lockerId = 1L;
        LockerDetailResponse mockResponse = new LockerDetailResponse();
        mockResponse.setLockerId(lockerId);
        mockResponse.setLockerName("테스트 락커");

        List<String> mockImages = List.of("img1.jpg", "img2.jpg");

        Mockito.when(lockerMapper.findUserById(lockerId)).thenReturn(mockResponse);
        Mockito.when(lockerMapper.findImageById(lockerId)).thenReturn(mockImages);

        // when
        LockerDetailResponse result = lockerService.findUserById(lockerId);

        // then
        assertNotNull(result);
        assertEquals(lockerId, result.getLockerId());
        assertEquals("테스트 락커", result.getLockerName());
        assertEquals(2, result.getImages().size());
        assertEquals("img1.jpg", result.getImages().get(0));
    }
  
    @DisplayName("보관소 상세 결과 없음 (예외발생)")
    @Test
    void findUserById_결과없음_예외발생() {
        // given
        Long lockerId = 999L;
        Mockito.when(lockerMapper.findUserById(lockerId)).thenReturn(null);

        // when
        LockerException exception = assertThrows(LockerException.class, () -> {
            lockerService.findUserById(lockerId);
        });

        assertSame(BaseResponseStatus.NOT_FOUND_LOCKERDETAILS, exception.getBaseResponseStatus());

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
        assertEquals(NOT_FOUND_LOCKER, exception.getBaseResponseStatus());
    }
}