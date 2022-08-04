package com.sotatek.demo.executable.application.controller.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class UserRegistrationRequest {
    @NotBlank(message = "email is required")
    @Email(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")
    private String email;
    @NotBlank(message = "password is required")
    private String password;
    @NotNull(message = "salary is required")
    @DecimalMin(value = "15000", message = "salary minimum is 15000 THB/month" )
    private Double salary;
}
