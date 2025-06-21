package com.airbng.service;

import com.airbng.common.exception.LockerException;
import com.airbng.dto.*;
import com.airbng.mappers.LockerMapper;
import com.airbng.util.S3Utils;
import com.airbng.common.exception.ImageException;
import com.airbng.common.exception.MemberException;
import com.airbng.domain.base.Available;
import com.airbng.dto.LockerInsertRequest;
import com.airbng.common.response.status.BaseResponseStatus;
import com.airbng.dto.LockerDetailResponse;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
import com.airbng.dto.LockerPreviewResult;
import com.airbng.dto.LockerTop5Response;
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
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Collections;
import static com.airbng.common.response.status.BaseResponseStatus.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)  // JUnit5에서 Mockito 사용 시 필수
class LockerServiceTest {

    @Mock
    private LockerMapper lockerMapper;
  
    @Mock
    private S3Utils s3Utils;

    @InjectMocks
    private LockerServiceImpl lockerService;
  
    private LockerInsertRequest request;

    @BeforeEach
    public void setUp() {
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

        LockerException exception = assertThrows(LockerException.class, () -> lockerService.registerLocker(request));
        assertEquals(MEMBER_ALREADY_HAS_LOCKER, exception.getBaseResponseStatus());

    }

    @Test
    @DisplayName("보관소 등록 실패: 존재하지 않는 멤버")
    void 보관소_등록_실패_멤버없음() throws IOException {
        when(lockerMapper.findLockerByMemberId(1L)).thenReturn(0);
        when(lockerMapper.findMemberId(1L)).thenReturn(0);

        MemberException exception = assertThrows(MemberException.class, () -> lockerService.registerLocker(request));
        assertEquals(MEMBER_NOT_FOUND, exception.getBaseResponseStatus());
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

        ImageException exception = assertThrows(ImageException.class, () -> lockerService.registerLocker(request));
        assertEquals(EXCEED_IMAGE_COUNT, exception.getBaseResponseStatus());
    }

    @Test
    @DisplayName("보관소 등록 실패: 빈 이미지 파일 포함")
    void 보관소_등록_실패_빈파일포함() throws IOException {
        MultipartFile emptyFile = mock(MultipartFile.class);
        when(emptyFile.isEmpty()).thenReturn(true);

        request.setImages(List.of(emptyFile));

        when(lockerMapper.findLockerByMemberId(1L)).thenReturn(0);
        when(lockerMapper.findMemberId(1L)).thenReturn(1);

        ImageException exception = assertThrows(ImageException.class, () -> lockerService.registerLocker(request));
        assertEquals(EMPTY_FILE, exception.getBaseResponseStatus());
    }

    @Test
    @DisplayName("보관소 등록 실패: 존재하지 않는 짐타입 포함")
    void 보관소_등록_실패_잘못된짐타입() throws IOException {
        request.setJimTypeIds(List.of(1L, 2L));

        when(lockerMapper.findLockerByMemberId(1L)).thenReturn(0);
        when(lockerMapper.findMemberId(1L)).thenReturn(1);
        when(lockerMapper.findValidJimTypeIds(anyList())).thenReturn(List.of(1L)); // 2L은 없음

        LockerException exception = assertThrows(LockerException.class, () -> lockerService.registerLocker(request));
        assertEquals(INVALID_JIMTYPE, exception.getBaseResponseStatus());
    }

    @Test
    @DisplayName("보관소 등록 실패: 중복된 짐타입 존재")
    void 보관소_등록_실패_짐타입중복() throws IOException {
        request.setJimTypeIds(List.of(1L, 1L)); // 중복 ID

        when(lockerMapper.findLockerByMemberId(1L)).thenReturn(0);
        when(lockerMapper.findMemberId(1L)).thenReturn(1);

        LockerException exception = assertThrows(LockerException.class, () -> lockerService.registerLocker(request));
        assertEquals(DUPLICATE_JIMTYPE, exception.getBaseResponseStatus());
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

        LockerException exception = assertThrows(LockerException.class, () -> lockerService.findTop5Locker());
        assertEquals(NOT_FOUND_LOCKER, exception.getBaseResponseStatus());
    }
}