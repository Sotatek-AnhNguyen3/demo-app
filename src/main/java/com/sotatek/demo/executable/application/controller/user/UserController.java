package com.sotatek.demo.executable.application.controller.user;

import com.sotatek.demo.adapter.mapper.UserMapper;
import com.sotatek.demo.domain.entitiy.User;
import com.sotatek.demo.domain.enums.ResponseCode;
import com.sotatek.demo.executable.application.controller.user.dto.UserRegistrationRequest;
import com.sotatek.demo.executable.application.controller.user.dto.UserRegistrationResponse;
import com.sotatek.demo.executable.application.pojo.Response;
import com.sotatek.demo.usecase.user.InitiateUserCreation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
public class UserController {

    private static final String TAG_NAME = "USER";
    private final InitiateUserCreation initiateUserCreation;

    @PostMapping("/registration")
    @Tag(name = TAG_NAME)
    @Operation(summary = "Create User", description = "Create a new User")
    @ApiResponse(content = @Content(mediaType = "application/json"))
    public ResponseEntity<Response<UserRegistrationResponse>> register(@Valid @RequestBody UserRegistrationRequest request) {
        User created = initiateUserCreation.execute(request);

        return ResponseEntity.ok().body(Response.createInstance(
                ResponseCode.SUCCESS.getCode(),
                ResponseCode.SUCCESS.getStatus(),
                "User create Successfully",
                created));
    }
}
