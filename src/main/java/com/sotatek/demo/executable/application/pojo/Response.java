package com.sotatek.demo.executable.application.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sotatek.demo.executable.application.controller.user.dto.UserRegistrationResponse;
import lombok.Builder;
import lombok.Data;

@Data
public class Response<T> {
    private String code;
    private String status;
    private String description;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    @Builder
    public Response(String code, String status, String description, T data) {
        this.code = code;
        this.status = status;
        this.description = description;
        this.data = data;
    }

    public static <T> Response<UserRegistrationResponse> createInstance(String code, String status, String description, T data) {
        return new Response(code, status, description, data);
    }
}

