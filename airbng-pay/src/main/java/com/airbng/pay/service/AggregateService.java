package com.airbng.pay.service;


import java.time.LocalDateTime;

public interface AggregateService {
    void fillForDate(LocalDateTime from, LocalDateTime to);
}
