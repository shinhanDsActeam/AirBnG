package com.airbng.service;

import com.airbng.common.exception.LockerException;
import com.airbng.common.response.status.BaseResponseStatus;
import com.airbng.dto.LockerDetailResponse;
import com.airbng.mappers.LockerMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LockerServiceTest {

    @InjectMocks
    private LockerServiceImpl lockerService;

    @Mock
    private LockerMapper lockerMapper;

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
    }
}