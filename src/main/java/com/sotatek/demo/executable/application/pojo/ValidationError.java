package com.sotatek.demo.executable.application.pojo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidationError {
    private String attribute;
    private String errorMessage;
}
