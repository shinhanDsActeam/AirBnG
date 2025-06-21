package com.airbng.validator;

import org.springframework.stereotype.Component;

@Component
public class PasswordValidator {
    //패스워드 형식 확인
    public boolean isValidPassword(String password) {
        //대문자와 소문자, 하나 이상의 숫자를 포함하여 8자 이상
        return password != null && password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");
    }
}
