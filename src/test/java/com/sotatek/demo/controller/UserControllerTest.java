package com.sotatek.demo.controller;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sotatek.demo.domain.entitiy.User;
import com.sotatek.demo.domain.enums.MemberType;
import com.sotatek.demo.domain.exception.user.UserAlreadyExistsException;
import com.sotatek.demo.exception.ErrorResponse;
import com.sotatek.demo.executable.application.controller.user.dto.UserRegistrationRequest;
import com.sotatek.demo.executable.application.controller.user.dto.UserRegistrationResponse;
import com.sotatek.demo.executable.application.pojo.Response;
import com.sotatek.demo.usecase.user.InitiateUserCreation;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private InitiateUserCreation initiateUserCreation;

	private String asJsonString(Object obj) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(obj);
	}

	private <T> ResponseEntity getResponseFromJson(String json, Class<T> data) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		JavaType type = mapper.getTypeFactory().constructParametricType(ResponseEntity.class, data);
		return mapper.<ResponseEntity<T>>readValue(json, type);
	}

	private ErrorResponse getErrorResponseFromJson(String json) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, ErrorResponse.class);
	}

	@Test
	void registerNewUserSuccessful() throws Exception {
		Mockito.when(initiateUserCreation.execute(any()))
				.thenReturn (User.builder()
						.email("hungnv@sotatek.com")
						.salary(150100.0)
						.memberType(MemberType.SLIVER)
						.build());
		String uri = "/user/registration";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
						.content(asJsonString(UserRegistrationRequest.builder()
								.email("hungnv@sotatek.com")
								.password("Aa12345")
								.salary(150100.0)
								.build()))
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
		String content = mvcResult.getResponse().getContentAsString();
		ResponseEntity<Response<UserRegistrationResponse>> response = getResponseFromJson(content, UserRegistrationResponse.class);
		assertEquals("User Registration Successful !", response.getBody().getDescription());
		assertEquals("hungnv@sotatek.com", response.getBody().getData().getEmail());
		assertEquals(150100.0, response.getBody().getData().getSalary());
		assertEquals(MemberType.SLIVER, response.getBody().getData().getMemberType());
	}

	@Test
	void registerUserFailed_WhenEmailMissing() throws Exception {
		String uri = "/user/registration";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
				.content(asJsonString(UserRegistrationRequest.builder()
						.password("Aa12345")
						.salary(150100.0)
						.build()))
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(422, status);
		String content = mvcResult.getResponse().getContentAsString();
		ErrorResponse response = getErrorResponseFromJson(content);
		assertEquals("Validation error. Check 'errors' field for details.", response.getMessage());
		assertEquals("email", response.getErrors().get(0).getField());
		assertEquals("email is required", response.getErrors().get(0).getMessage());
	}

	@Test
	void registerUserFailed_WhenEmailInvalid() throws Exception {
		String uri = "/user/registration";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
				.content(asJsonString(UserRegistrationRequest.builder()
						// register with invalid email
						.email("hungnvsotatek.com")
						.password("Aa12345")
						.salary(150100.0)
						.build()))
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(422, status);
		String content = mvcResult.getResponse().getContentAsString();
		ErrorResponse response = getErrorResponseFromJson(content);
		assertEquals("Validation error. Check 'errors' field for details.", response.getMessage());
		assertEquals("email", response.getErrors().get(0).getField());
		assertEquals("must be a well-formed email address", response.getErrors().get(0).getMessage());
	}

	@Test
	void registerUserFailed_WhenPasswordMissing() throws Exception {
		String uri = "/user/registration";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
				.content(asJsonString(UserRegistrationRequest.builder()
						.email("hungnv@sotatek.com")
						.salary(150100.0)
						.build()))
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(422, status);
		String content = mvcResult.getResponse().getContentAsString();
		ErrorResponse response = getErrorResponseFromJson(content);
		assertEquals("Validation error. Check 'errors' field for details.", response.getMessage());
		assertEquals("password", response.getErrors().get(0).getField());
		assertEquals("password is required", response.getErrors().get(0).getMessage());
	}

	@Test
	void registerUserFailed_WhenSalaryInvalid() throws Exception {
		String uri = "/user/registration";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
				.content(asJsonString(UserRegistrationRequest.builder()
						.email("hungnv@sotatek.com")
						.password("Aa12345")
						// register with invalid salary ( < 15000)
						.salary(100.0)
						.build()))
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(422, status);
		String content = mvcResult.getResponse().getContentAsString();
		ErrorResponse response = getErrorResponseFromJson(content);
		assertEquals("Validation error. Check 'errors' field for details.", response.getMessage());
		assertEquals("salary", response.getErrors().get(0).getField());
		assertEquals("salary minimum is 15000 THB/month", response.getErrors().get(0).getMessage());
	}

	@Test
	void registerUserFailed_WhenSalaryMissing() throws Exception {
		String uri = "/user/registration";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
				.content(asJsonString(UserRegistrationRequest.builder()
						.email("hungnv@sotatek.com")
						.password("Aa12345")
						.build()))
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(422, status);
		String content = mvcResult.getResponse().getContentAsString();
		ErrorResponse response = getErrorResponseFromJson(content);
		assertEquals("Validation error. Check 'errors' field for details.", response.getMessage());
		assertEquals("salary", response.getErrors().get(0).getField());
		assertEquals("salary is required", response.getErrors().get(0).getMessage());
	}

	@Test
	void registerUserFailed_WhenEmailExisted() throws Exception {
		when(initiateUserCreation.execute(any()))
				.thenThrow (new UserAlreadyExistsException());
		String uri = "/user/registration";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
				.content(asJsonString(UserRegistrationRequest.builder()
						.email("hungnv@sotatek.com")
						.password("Aa12345")
						.salary(150100.0)
						.build()))
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);
		String content = mvcResult.getResponse().getContentAsString();
		ErrorResponse response = getErrorResponseFromJson(content);
		assertEquals("Exist user with request email", response.getMessage());
	}
}
