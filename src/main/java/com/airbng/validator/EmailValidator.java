package com.airbng.validator;

import org.springframework.stereotype.Component;

@Component
public class EmailValidator {
    //이메일 형식 확인
    public boolean isValidEmail(String email) {
        //@앞의 문자 1개이상, @ 뒤에 문자+ . + 2~6자(com, net, co,kr , email)
        // 점(.)이 연속되거나, @ 앞/뒤 형식이 잘못된 경우 모두 차단
        return email != null && email.matches("^[A-Za-z0-9]+([._%+-]?[A-Za-z0-9]+)@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)\\.[A-Za-z]{2,6}$");
    }
}
