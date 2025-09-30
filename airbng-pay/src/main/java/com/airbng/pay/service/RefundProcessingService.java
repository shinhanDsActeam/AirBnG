package com.airbng.pay.service;

import java.time.LocalDateTime;

public interface RefundProcessingService {
    void processWindow(LocalDateTime from, LocalDateTime to);
}
