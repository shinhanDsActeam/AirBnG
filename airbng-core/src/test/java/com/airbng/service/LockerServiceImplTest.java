package com.airbng.service;

import com.airbng.common.exception.ImageException;
import com.airbng.common.exception.LockerException;
import com.airbng.common.exception.MemberException;
import com.airbng.domain.Locker;
import com.airbng.domain.Member;
import com.airbng.domain.base.Available;
import com.airbng.domain.base.ReservationState;
import com.airbng.domain.image.Image;
import com.airbng.domain.image.LockerImage;
import com.airbng.domain.jimtype.JimType;
import com.airbng.domain.jimtype.LockerJimType;
import com.airbng.dto.locker.*;
import com.airbng.repository.*;
import com.airbng.util.S3Utils;
import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static com.airbng.common.response.status.BaseResponseStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class LockerServiceImplTest {

    @Mock private LockerRepository lockerRepository;
    @Mock private ImageRepository imageRepository;
    @Mock private LockerImageRepository lockerImageRepository;
    @Mock private LockerJimTypeRepository lockerJimTypeRepository;
    @Mock private JimTypeRepository jimTypeRepository;
    @Mock private MemberRepository memberRepository;

    @Mock private S3Utils s3Utils;

    @Mock private RedisTemplate<String, LockerTop5Response> redisTemplate;
    @Mock private ValueOperations<String, LockerTop5Response> valueOps;
    @Mock private Cache<String, LockerTop5Response> localCache;

    @InjectMocks
    private LockerServiceImpl service; // 네 실제 LockerServiceImpl (JPA버전)

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
    }

    // ---------- helpers ----------
    private Locker dummyLocker(Long id, Long keeperId) {
        Member keeper = memberRepository.findById(keeperId)
            .orElseGet(() -> {
                Member m = new Member();
                m.setMemberId(keeperId);
                return m;
            });
        return Locker.builder()
                .lockerId(id)
                .lockerName("Locker-" + id)
                .isAvailable(Available.YES)
                .address("Seoul")
                .addressEnglish("Seoul")
                .addressDetail("Detail")
                .latitude(37.0)
                .longitude(127.0)
                .keeper(keeper)
                .reservationCount(3L)
                .lockerJimTypes(Collections.emptySet()) // NPE 방지
                .lockerImages(Collections.emptySet())   // NPE 방지
                .build();
    }

    // ================= 검색 =================
    @Test
    @DisplayName("검색: 결과 및 개수 반환")
    void search_ok() {
        LockerSearchRequest req = LockerSearchRequest.builder()
                .address("Seoul").lockerName("Locker").jimTypeId(List.of(1L,2L)).build();

        when(lockerRepository.searchForList("Seoul", "Locker", List.of(1L,2L), false))
                .thenReturn(List.of(dummyLocker(1L, 10L), dummyLocker(2L, 10L)));
        when(lockerRepository.searchCount("Seoul", "Locker", List.of(1L,2L), false))
                .thenReturn(2L);

        LockerSearchResponse res = service.findAllLockerBySearch(req);

        assertEquals(2L, res.getCount());
        assertEquals(2, res.getLockers().size());
        verify(lockerRepository).searchForList(any(), any(), anyList(), anyBoolean());
        verify(lockerRepository).searchCount(any(), any(), anyList(), anyBoolean());
    }

    @Test
    @DisplayName("검색: 결과 없으면 NOT_FOUND_LOCKER")
    void search_empty_throws() {
        LockerSearchRequest req = new LockerSearchRequest(); // all nulls
        when(lockerRepository.searchForList(any(), any(), anyList(), anyBoolean()))
                .thenReturn(List.of());

        LockerException ex = assertThrows(LockerException.class,
                () -> service.findAllLockerBySearch(req));
        assertEquals(NOT_FOUND_LOCKER, ex.getBaseResponseStatus());
    }

    // ================= 상세 =================
    @Test
    @DisplayName("상세: 정상 반환")
    void findLockerById_ok() {
        when(lockerRepository.findLockerById(9L)).thenReturn(Optional.of(dummyLocker(9L, 1L)));

        LockerDetailResponse res = service.findLockerById(9L);

        assertNotNull(res);
        assertEquals(9L, res.getLockerId());
        verify(lockerRepository).findLockerById(9L);
    }

    @Test
    @DisplayName("상세: 없으면 NOT_FOUND_LOCKERDETAILS")
    void findLockerById_notFound() {
        when(lockerRepository.findLockerById(99L)).thenReturn(Optional.empty());

        LockerException ex = assertThrows(LockerException.class,
                () -> service.findLockerById(99L));
        assertEquals(NOT_FOUND_LOCKERDETAILS, ex.getBaseResponseStatus());
    }

    // ================= 내 보관소 =================
    @Test
    @DisplayName("내 보관소: 정상 반환")
    void findMyLocker_ok() {
        when(lockerRepository.findMyLocker(7L)).thenReturn(Optional.of(dummyLocker(22L, 7L)));

        LockerDetailResponse res = service.findMyLocker(7L);

        assertEquals(22L, res.getLockerId());
        verify(lockerRepository).findMyLocker(7L);
    }

    // ================= Top5 =================
    @Test
    @DisplayName("Top5: 캐시 히트 시 바로 반환")
    void top5_cache_hit() {
        LockerTop5Response cached = mock(LockerTop5Response.class);
        when(localCache.getIfPresent("lockerTop5")).thenReturn(cached);

        LockerTop5Response res = service.findTop5Locker();

        assertSame(cached, res);
        verifyNoInteractions(valueOps);
        verifyNoInteractions(lockerRepository);
    }

    @Test
    @DisplayName("Top5: 캐시 미스 → 레디스 히트")
    void top5_redis_hit() {
        when(localCache.getIfPresent("lockerTop5")).thenReturn(null);
        LockerTop5Response fromRedis = mock(LockerTop5Response.class);
        when(valueOps.get("lockerTop5")).thenReturn(fromRedis);

        LockerTop5Response res = service.findTop5Locker();

        assertSame(fromRedis, res);
        verify(localCache).put("lockerTop5", fromRedis);
        verifyNoInteractions(lockerRepository);
    }

    @Test
    @DisplayName("Top5: 캐시/레디스 미스 → DB 조회")
    void top5_db_fallback() {
        when(localCache.getIfPresent("lockerTop5")).thenReturn(null);
        when(valueOps.get("lockerTop5")).thenReturn(null);

        List<Locker> top = List.of(
                dummyLocker(1L,1L), dummyLocker(2L,1L),
                dummyLocker(3L,1L), dummyLocker(4L,1L), dummyLocker(5L,1L)
        );
        when(lockerRepository.findTop5LockersByReservation(ReservationState.COMPLETED))
                .thenReturn(top);

        LockerTop5Response res = service.findTop5Locker();

        assertNotNull(res);
        verify(lockerRepository).findTop5LockersByReservation(eq(ReservationState.COMPLETED));
        verify(valueOps).set(eq("lockerTop5"), any(LockerTop5Response.class), eq(1L), eq(java.util.concurrent.TimeUnit.HOURS));
        verify(localCache).put(eq("lockerTop5"), any());
    }


    @Test
    @DisplayName("등록: keeper가 이미 보관소 보유 → MEMBER_ALREADY_HAS_LOCKER")
    void register_keeper_has_locker() {
        LockerInsertRequest dto = baseInsert();
        when(lockerRepository.existsByKeeper_MemberId(1L)).thenReturn(true);

        LockerException ex = assertThrows(LockerException.class, () -> service.registerLocker(dto));
        assertEquals(MEMBER_ALREADY_HAS_LOCKER, ex.getBaseResponseStatus());
    }

    @Test
    @DisplayName("등록: 멤버 없음 → NOT_FOUND_MEMBER")
    void register_member_not_found() {
        LockerInsertRequest dto = baseInsert();
        when(lockerRepository.existsByKeeper_MemberId(1L)).thenReturn(false);
        when(memberRepository.existsById(1L)).thenReturn(false);

        MemberException ex = assertThrows(MemberException.class, () -> service.registerLocker(dto));
        assertEquals(NOT_FOUND_MEMBER, ex.getBaseResponseStatus());
    }

    @Test
    @DisplayName("등록: 이미지 5개 초과 → EXCEED_IMAGE_COUNT")
    void register_too_many_images() {
        LockerInsertRequest dto = baseInsert();
        dto.setImages(List.of(
                mock(MultipartFile.class), mock(MultipartFile.class), mock(MultipartFile.class),
                mock(MultipartFile.class), mock(MultipartFile.class), mock(MultipartFile.class)
        ));
        when(lockerRepository.existsByKeeper_MemberId(1L)).thenReturn(false);
        when(memberRepository.existsById(1L)).thenReturn(true);

        ImageException ex = assertThrows(ImageException.class, () -> service.registerLocker(dto));
        assertEquals(EXCEED_IMAGE_COUNT, ex.getBaseResponseStatus());
    }

    @Test
    @DisplayName("등록: 빈 파일 포함 → EMPTY_FILE")
    void register_empty_file() throws IOException {
        LockerInsertRequest dto = baseInsert();
        MultipartFile empty = mock(MultipartFile.class);
        when(empty.isEmpty()).thenReturn(true);
        dto.setImages(List.of(empty));
        when(lockerRepository.existsByKeeper_MemberId(1L)).thenReturn(false);
        when(memberRepository.existsById(1L)).thenReturn(true);

        ImageException ex = assertThrows(ImageException.class, () -> service.registerLocker(dto));
        assertEquals(EMPTY_FILE, ex.getBaseResponseStatus());
    }

    @Test
    @DisplayName("등록: 존재하지 않는 짐타입 포함 → INVALID_JIMTYPE")
    void register_invalid_jimtypes() {
        LockerInsertRequest dto = baseInsert();
        dto.setJimTypeIds(List.of(1L, 2L));
        when(lockerRepository.existsByKeeper_MemberId(1L)).thenReturn(false);
        when(memberRepository.existsById(1L)).thenReturn(true);
        when(jimTypeRepository.findValidIds(List.of(1L,2L))).thenReturn(List.of(1L)); // 2L 없음

        LockerException ex = assertThrows(LockerException.class, () -> service.registerLocker(dto));
        assertEquals(INVALID_JIMTYPE, ex.getBaseResponseStatus());
    }

    @Test
    @DisplayName("등록: 중복 짐타입 → DUPLICATE_JIMTYPE")
    void register_duplicate_jimtypes() {
        LockerInsertRequest dto = baseInsert();
        dto.setJimTypeIds(List.of(1L, 1L));
        when(lockerRepository.existsByKeeper_MemberId(1L)).thenReturn(false);
        when(memberRepository.existsById(1L)).thenReturn(true);

        LockerException ex = assertThrows(LockerException.class, () -> service.registerLocker(dto));
        assertEquals(DUPLICATE_JIMTYPE, ex.getBaseResponseStatus());
    }

    // ================= 활성화 토글 =================
    @Test
    @DisplayName("활성화 토글: YES -> NO")
    void toggle_available() {
        Locker l = dummyLocker(5L, 1L);
        l.setIsAvailable(Available.YES);
        when(lockerRepository.findById(5L)).thenReturn(Optional.of(l));

        service.updateLockerActivation(5L);

        assertEquals(Available.NO, l.getIsAvailable());
    }

    // ================= 존재 여부 =================
    @Test
    @DisplayName("존재 여부: keeper 기준 exists")
    void exists_by_keeper() {
        when(lockerRepository.existsByKeeper_MemberId(3L)).thenReturn(true);
        assertTrue(service.isExistLocker(3L));
    }

    // ---------- private ----------
    private LockerInsertRequest baseInsert() {
        LockerInsertRequest dto = new LockerInsertRequest();
        dto.setKeeperId(1L);
        dto.setLockerName("Nice");
        dto.setIsAvailable(Available.YES);
        dto.setAddress("addr");
        dto.setAddressEnglish("eng");
        dto.setAddressDetail("det");
        dto.setLatitude(1.0);
        dto.setLongitude(2.0);
        dto.setImages(Collections.emptyList());
        dto.setJimTypeIds(Collections.emptyList());
        return dto;
    }
}
