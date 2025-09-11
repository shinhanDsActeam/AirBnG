package com.airbng.common;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

public class ParamNameResolver {

    public static String resolveParamName(MethodParameter parameter) {
        // 1. @RequestParam(value = "xxx")
        RequestParam requestParam = parameter.getParameterAnnotation(RequestParam.class);
        if (requestParam != null && !requestParam.value().isEmpty()) {
            return requestParam.value();
        }

        // 2. @PathVariable(value = "xxx")
        PathVariable pathVariable = parameter.getParameterAnnotation(PathVariable.class);
        if (pathVariable != null && !pathVariable.value().isEmpty()) {
            return pathVariable.value();
        }

        return parameter.getParameterName();
    }
}