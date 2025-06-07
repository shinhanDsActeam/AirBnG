package com.airbng.repository;

import org.springframework.stereotype.Repository;

@Repository
public class SampleRepository {

    public String fetchDummyData() {
        return "Sample Data";
    }
}
