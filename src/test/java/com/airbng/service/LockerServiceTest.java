package com.airbng.service;

import com.airbng.common.exception.ImageException;
import com.airbng.common.exception.LockerException;
import com.airbng.common.exception.MemberException;
import com.airbng.domain.base.Available;
import com.airbng.dto.LockerInsertRequest;
import com.airbng.mappers.LockerMapper;
import com.airbng.util.S3Uploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LockerServiceTest {

    @Mock
    private LockerMapper lockerMapper;

    @Mock
    private S3Uploader s3Uploader;

    @InjectMocks
    private LockerServiceImpl lockerService;

    private LockerInsertRequest request;

    @BeforeEach
    void setUp() {
        request = new LockerInsertRequest();
        request.setLockerName("Test Locker");
        request.setIsAvailable(Available.YES);
        request.setKeeperId(1L);
        request.setAddress("서울시 강남구");
        request.setAddressEnglish("Gangnam-gu, Seoul");
        request.setAddressDetail("101호");
        request.setLatitude(37.4979);
        request.setLongitude(127.0276);
        request.setJimTypeIds(Collections.emptyList());
        request.setImages(Collections.emptyList());
    }

    @Test
    @DisplayName("보관소 등록 성공: 기존 보관소 없음 & 유효한 멤버")
    void 보관소_등록_성공() throws IOException {

        when(lockerMapper.findLockerByMemberId(1L)).thenReturn(0);
        when(lockerMapper.findMemberId(1L)).thenReturn(1);

        lockerService.registerLocker(request);

        verify(lockerMapper).insertLocker(any());
    }

    @Test
    @DisplayName("보관소 등록 실패: 이미 보관소가 등록된 멤버")
    void 보관소_등록_실패_중복_보관소() throws IOException {

        when(lockerMapper.findLockerByMemberId(1L)).thenReturn(1); // 이미 등록된 보관소

        assertThrows(LockerException.class, () -> lockerService.registerLocker(request));
    }

    @Test
    @DisplayName("보관소 등록 실패: 존재하지 않는 멤버")
    void 보관소_등록_실패_멤버없음() throws IOException {
        when(lockerMapper.findLockerByMemberId(1L)).thenReturn(0);
        when(lockerMapper.findMemberId(1L)).thenReturn(0);

        assertThrows(MemberException.class, () -> lockerService.registerLocker(request));
    }

    @Test
    @DisplayName("보관소 등록 실패: 이미지 5개 초과")
    void 보관소_등록_실패_이미지초과() throws IOException {
        request.setImages(
                List.of(mock(MultipartFile.class), mock(MultipartFile.class),
                        mock(MultipartFile.class), mock(MultipartFile.class),
                        mock(MultipartFile.class), mock(MultipartFile.class)) // 6개
        );

        when(lockerMapper.findLockerByMemberId(1L)).thenReturn(0);
        when(lockerMapper.findMemberId(1L)).thenReturn(1);

        assertThrows(ImageException.class, () -> lockerService.registerLocker(request));
    }

    @Test
    @DisplayName("보관소 등록 실패: 빈 이미지 파일 포함")
    void 보관소_등록_실패_빈파일포함() throws IOException {
        MultipartFile emptyFile = mock(MultipartFile.class);
        when(emptyFile.isEmpty()).thenReturn(true);

        request.setImages(List.of(emptyFile));

        when(lockerMapper.findLockerByMemberId(1L)).thenReturn(0);
        when(lockerMapper.findMemberId(1L)).thenReturn(1);

        assertThrows(ImageException.class, () -> lockerService.registerLocker(request));
    }

    @Test
    @DisplayName("보관소 등록 실패: 존재하지 않는 짐타입 포함")
    void 보관소_등록_실패_잘못된짐타입() throws IOException {
        request.setJimTypeIds(List.of(1L, 2L));

        when(lockerMapper.findLockerByMemberId(1L)).thenReturn(0);
        when(lockerMapper.findMemberId(1L)).thenReturn(1);
        when(lockerMapper.findValidJimTypeIds(anyList())).thenReturn(List.of(1L)); // 2L은 없음

        assertThrows(LockerException.class, () -> lockerService.registerLocker(request));
    }

    @Test
    @DisplayName("보관소 등록 실패: 중복된 짐타입 존재")
    void 보관소_등록_실패_짐타입중복() throws IOException {
        request.setJimTypeIds(List.of(1L, 1L)); // 중복 ID

        when(lockerMapper.findLockerByMemberId(1L)).thenReturn(0);
        when(lockerMapper.findMemberId(1L)).thenReturn(1);

        assertThrows(LockerException.class, () -> lockerService.registerLocker(request));
    }

}
