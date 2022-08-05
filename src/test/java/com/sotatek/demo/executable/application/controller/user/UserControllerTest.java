package com.sotatek.demo.executable.application.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sotatek.demo.domain.constants.ResponseStatus;
import com.sotatek.demo.domain.entitiy.User;
import com.sotatek.demo.domain.enums.MemberType;
import com.sotatek.demo.domain.enums.ResponseCode;
import com.sotatek.demo.domain.exception.user.UserAlreadyExistsException;
import com.sotatek.demo.executable.application.controller.user.dto.UserRegistrationRequest;
import com.sotatek.demo.executable.application.pojo.Response;
import com.sotatek.demo.usecase.user.InitiateUserCreation;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private InitiateUserCreation initiateUserCreation;

    private String asJsonString(Object value) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(value);
    }

    private Response convertToResponse(String value) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(value, Response.class);
    }

    @Test
    void registerNewUserSuccessful() throws Exception {
        Mockito.when(initiateUserCreation.execute(any()))
                .thenReturn (User.builder()
                        .id(Long.valueOf(1))
                        .email("anh.nguyen3@sotatek.com")
                        .salary(150100.0)
                        .memberType(MemberType.SLIVER)
                        .build());
        String uri = "/user/registration";
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(UserRegistrationRequest.builder()
                        .email("anh.nguyen3@sotatek.com")
                        .password("Aa12345")
                        .salary(150100.0)
                        .build()));

        mvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(ResponseCode.SUCCESS.getCode())))
                .andExpect(jsonPath("$.status", is(ResponseCode.SUCCESS.getStatus())))
                .andExpect(jsonPath("$.description", is("User create Successfully")));

    }

    @Test
    void registerUserFailed_WhenEmailMissing() throws Exception {
        String uri = "/user/registration";
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(UserRegistrationRequest.builder()
                        .password("Aa12345")
                        .salary(150100.0)
                        .build()));
        mvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("MAI001")))
                .andExpect(jsonPath("$.status", is(ResponseStatus.FAILED)))
                .andExpect(jsonPath("$.description[0]", is("The email is required.")));
    }

    @Test
    void registerUserFailed_WhenEmailInvalid() throws Exception {
        String uri = "/user/registration";
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(UserRegistrationRequest.builder()
                        // register with invalid email
                        .email("anh.nguyen3sotatek.com")
                        .password("Aa12345")
                        .salary(150100.0)
                        .build()));
        mvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("MAI001")))
                .andExpect(jsonPath("$.status", is(ResponseStatus.FAILED)))
                .andExpect(jsonPath("$.description[0]", is("must be a well-formed email address")));
    }

    @Test
    void registerUserFailed_WhenPasswordMissing() throws Exception {
        String uri = "/user/registration";
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(UserRegistrationRequest.builder()
                        .email("anh.nguyen3@sotatek.com")
                        .salary(150100.0)
                        .build()));
        mvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("MAI001")))
                .andExpect(jsonPath("$.status", is(ResponseStatus.FAILED)))
                .andExpect(jsonPath("$.description[0]", is("The password is required.")));

    }

    @Test
    void registerUserFailed_WhenSalaryInvalid() throws Exception {
        String uri = "/user/registration";
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(UserRegistrationRequest.builder()
                        .email("anh.nguyen3@sotatek.com")
                        .password("Aa12345")
                        // register with invalid salary ( < 15000)
                        .salary(100.0)
                        .build()));
        mvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("MAI001")))
                .andExpect(jsonPath("$.status", is(ResponseStatus.FAILED)))
                .andExpect(jsonPath("$.description[0]", is("The salary minimum is 15000 THB/month.")));
    }

    @Test
    void registerUserFailed_WhenSalaryMissing() throws Exception {
        String uri = "/user/registration";
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(UserRegistrationRequest.builder()
                        .email("anh.nguyen3@sotatek.com")
                        .password("Aa12345")
                        .build()));
        mvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("MAI001")))
                .andExpect(jsonPath("$.status", is(ResponseStatus.FAILED)))
                .andExpect(jsonPath("$.description[0]", is("The salary is required.")));
    }

    @Test
    void registerUserFailed_WhenEmailExisted() throws Exception {
        when(initiateUserCreation.execute(any()))
                .thenThrow (new UserAlreadyExistsException());
        String uri = "/user/registration";
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(UserRegistrationRequest.builder()
                        .email("anh.nguyen3@sotatek.com")
                        .password("Aa12345")
                        .salary(150100.0)
                        .build()));
        mvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code", Matchers.is(ResponseCode.USER_ALREADY_EXISTS.getCode())))
                .andExpect(jsonPath("$.status", is(ResponseStatus.FAILED)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is(ResponseCode.USER_ALREADY_EXISTS.getDescription())));
    }

}