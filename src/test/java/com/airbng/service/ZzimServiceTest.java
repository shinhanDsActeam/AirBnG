package com.airbng.service;

import com.airbng.common.exception.MemberException;
import com.airbng.common.exception.LockerException;
import com.airbng.common.exception.ZzimException;
import com.airbng.common.response.status.BaseResponseStatus;
import com.airbng.mappers.LockerMapper;
import com.airbng.mappers.MemberMapper;
import com.airbng.mappers.ZzimMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import static com.airbng.common.response.status.BaseResponseStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ZzimServiceTest {

    @Mock
    private ZzimMapper zzimMapper;

    @Mock
    private MemberMapper memberMapper;

    @Mock
    private LockerMapper lockerMapper;

    @InjectMocks
    private ZzimServiceImpl zzimService;

    private Long sessionMemberId;
    private Long memberId;
    private Long lockerId;

    @BeforeEach
    void setUp() {
        sessionMemberId = 1L;
        memberId = 1L;
        lockerId = 100L;
    }

    @Nested
    @DisplayName("찜 등록/취소 테스트")
    class ToggleZzim {

        @Test
        @DisplayName("아직 찜하지 않은 경우 → 등록 수행")
        void 아직_찜하지_않은_경우_등록() {
            when(memberMapper.isExistMember(memberId)).thenReturn(true);
            when(lockerMapper.isExistLocker(lockerId)).thenReturn(true);
            when(lockerMapper.isLockerKeeper(lockerId, memberId)).thenReturn(false);
            when(zzimMapper.isExistZzim(memberId, lockerId)).thenReturn(0);

            BaseResponseStatus result = zzimService.toggleZzim(sessionMemberId, memberId, lockerId);

            verify(zzimMapper).insertZzim(memberId, lockerId);
            assertEquals(SUCCESS_INSERT_ZZIM, result);
        }

        @Test
        @DisplayName("이미 찜한 경우 → 삭제 수행")
        void 이미_찜한_경우_삭제() {
            when(memberMapper.isExistMember(memberId)).thenReturn(true);
            when(lockerMapper.isExistLocker(lockerId)).thenReturn(true);
            when(lockerMapper.isLockerKeeper(lockerId, memberId)).thenReturn(false);
            when(zzimMapper.isExistZzim(memberId, lockerId)).thenReturn(1);

            BaseResponseStatus result = zzimService.toggleZzim(sessionMemberId, memberId, lockerId);

            verify(zzimMapper).deleteZzim(memberId, lockerId);
            assertEquals(SUCCESS_DELETE_ZZIM, result);
        }

        @Test
        @DisplayName("존재하지 않는 멤버")
        void 존재하지_않는_멤버() {
            when(memberMapper.isExistMember(memberId)).thenReturn(false);

            MemberException ex = assertThrows(MemberException.class,
                    () -> zzimService.toggleZzim(sessionMemberId, memberId, lockerId));

            assertEquals(NOT_FOUND_MEMBER, ex.getBaseResponseStatus());
        }

        @Test
        @DisplayName("존재하지 않는 락커")
        void 존재하지_않는_락커() {
            when(memberMapper.isExistMember(memberId)).thenReturn(true);
            when(lockerMapper.isExistLocker(lockerId)).thenReturn(false);

            LockerException ex = assertThrows(LockerException.class,
                    () -> zzimService.toggleZzim(sessionMemberId, memberId, lockerId));

            assertEquals(NOT_FOUND_LOCKER, ex.getBaseResponseStatus());
        }

        @Test
        @DisplayName("본인 락커 찜 시도")
        void 본인_락커_찜_시도() {
            when(memberMapper.isExistMember(memberId)).thenReturn(true);
            when(lockerMapper.isExistLocker(lockerId)).thenReturn(true);
            when(lockerMapper.isLockerKeeper(lockerId, memberId)).thenReturn(true);

            ZzimException ex = assertThrows(ZzimException.class,
                    () -> zzimService.toggleZzim(sessionMemberId, memberId, lockerId));

            assertEquals(SELF_LOCKER_ZZIM, ex.getBaseResponseStatus());
        }

        @Test
        @DisplayName("중복 찜 등록 시 예외 발생")
        void 중복_찜등록_예외() {
            when(memberMapper.isExistMember(memberId)).thenReturn(true);
            when(lockerMapper.isExistLocker(lockerId)).thenReturn(true);
            when(lockerMapper.isLockerKeeper(lockerId, memberId)).thenReturn(false);
            when(zzimMapper.isExistZzim(memberId, lockerId)).thenReturn(0);
            doThrow(new DuplicateKeyException("중복")).when(zzimMapper).insertZzim(memberId, lockerId);

            ZzimException ex = assertThrows(ZzimException.class,
                    () -> zzimService.toggleZzim(sessionMemberId, memberId, lockerId));

            assertEquals(DUPLICATE_ZZIM, ex.getBaseResponseStatus());
        }
    }

    @Nested
    @DisplayName("찜 여부 확인 테스트")
    class isExsistZzimTest {

        @Test
        @DisplayName("찜 존재 확인: true")
        void 찜_존재() {
            when(zzimMapper.isExistZzim(memberId, lockerId)).thenReturn(1);
            assertTrue(zzimService.isExistZzim(memberId, lockerId));
        }

        @Test
        @DisplayName("찜 존재 확인: false")
        void 찜_존재하지_않음() {
            when(zzimMapper.isExistZzim(memberId, lockerId)).thenReturn(0);
            assertFalse(zzimService.isExistZzim(memberId, lockerId));
        }
    }
}
