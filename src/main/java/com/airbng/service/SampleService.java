package com.airbng.service;

import org.springframework.stereotype.Service;

@Service
public class SampleService {

    public String getWelcomeMessage() {
        return "Welcome to AirBnG!";
    }
}
