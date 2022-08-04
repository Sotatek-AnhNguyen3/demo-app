package com.sotatek.demo.executable.application.controller.user.dto;

import com.sotatek.demo.domain.enums.MemberType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationResponse {
    private String email;
    private Double salary;
    private MemberType memberType;
}
