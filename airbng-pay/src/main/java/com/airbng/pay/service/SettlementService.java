package com.airbng.pay.service;

import java.time.LocalDateTime;

public interface SettlementService {
    void processWindow(LocalDateTime from, LocalDateTime to);
}
