package com.airbng.pay.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountValidationServiceImpl implements AccountValidationService {

    @Override
    public boolean isValidAccountNumber(String accountNumber) {
        String regex = "\\d{10, 16}";
        return accountNumber.matches(regex);
    }
}
