package com.airbng.mappers;

import com.airbng.domain.base.Available;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {com.airbng.config.WebConfig.class})
@WebAppConfiguration
class LockerMapperTest {
    @Autowired
    LockerMapper lockerMapper;

    List<Available> list = List.of(Available.YES, Available.NO);


    @Test
    @DisplayName("getIsAvailableById 테스트")
    @Transactional
    void 락커_상태확인() {
        // Given : 어떠한 데이터가 주어질 때
        Long lockerId = 1L;
        // When : 어떠한 기능을 실행하면
        Available available = lockerMapper.getIsAvailableById(lockerId);

        // Then : 어떠한 결과를 기대한다
        boolean matched = list.stream().anyMatch(s -> s.equals(available));
        assertTrue(matched);
    }

    @Nested
    @DisplayName("updateLockerIsAvailable 테스트")
    class InsertJimTypeTest {
        @Test
        @DisplayName("-> YES")
        @Transactional
        void 락커_상태변경_TO_YES() {
            // Given : 어떠한 데이터가 주어질 때
            Long lockerId = 1L;
            Available avaialbe = Available.YES;

            // When : 어떠한 기능을 실행하면
            lockerMapper.updateLockerIsAvailable(lockerId, avaialbe);
            Available available = lockerMapper.getIsAvailableById(lockerId);

            // Then : 어떠한 결과를 기대한다
            assertEquals(available, Available.YES);
        }

        @Test
        @DisplayName("-> NO")
        @Transactional
        void 락커_상태변경_TO_NO() {
            // Given : 어떠한 데이터가 주어질 때
            Long lockerId = 1L;
            Available avaialbe = Available.NO;

            // When : 어떠한 기능을 실행하면
            lockerMapper.updateLockerIsAvailable(lockerId, avaialbe);
            Available available = lockerMapper.getIsAvailableById(lockerId);

            // Then : 어떠한 결과를 기대한다
            assertEquals(available, Available.NO);
        }

    }

    @Test
    @DisplayName("toggleLockerIsAvailable 테스트")
    @Transactional
    void 락커_상태전환() {
        // Given : 어떠한 데이터가 주어질 때
        Long lockerId = 1L;
        Available before = lockerMapper.getIsAvailableById(lockerId);

        // When : 어떠한 기능을 실행하면
        lockerMapper.toggleLockerIsAvailable(lockerId);
        Available after = lockerMapper.getIsAvailableById(lockerId);

        // Then : 어떠한 결과를 기대한다
        assertNotEquals(before, after);
    }

}