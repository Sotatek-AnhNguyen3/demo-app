package com.sotatek.demo.domain.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.NONE)
public class ResponseStatus {
    public static final String PASSED = "PASSED";
    public static final String FAILED = "FAILED";
    public static final String ERROR = "ERROR";
}
