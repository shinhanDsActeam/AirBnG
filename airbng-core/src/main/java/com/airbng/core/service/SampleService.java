package com.airbng.core.service;

import org.springframework.stereotype.Service;

@Service
public class SampleService {

    public String getWelcomeMessage() {
        return "Welcome to AirBnG!";
    }
}
