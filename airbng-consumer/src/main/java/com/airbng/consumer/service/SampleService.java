package com.airbng.consumer.service;

import org.springframework.stereotype.Service;

@Service
public class SampleService {

    public String getWelcomeMessage() {
        return "Welcome to AirBnG!";
    }
}
