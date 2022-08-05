package com.sotatek.demo.usecase.user;

import com.sotatek.demo.domain.entitiy.User;
import com.sotatek.demo.domain.enums.MemberType;
import com.sotatek.demo.domain.exception.user.UserAlreadyExistsException;
import com.sotatek.demo.domain.port.UserRepository;
import com.sotatek.demo.executable.application.DemoAppApplication;
import com.sotatek.demo.executable.application.controller.user.dto.UserRegistrationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = DemoAppApplication.class)
class InitiateUserCreationTest {
    @Autowired
    private InitiateUserCreation initiateUserCreation;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // truncate all user before each test case
        initiateUserCreation.truncate();
    }

    @Test
    void createUserSuccessful_WithSliverMemberType() {
        User user = initiateUserCreation.execute(UserRegistrationRequest
                .builder()
                .email("anh.nguyen3@sotatek.com")
                .password("Aa@12345")
                .salary(16000.0)
                .build());
        assertEquals("anh.nguyen3@sotatek.com", user.getEmail());
        assertTrue(passwordEncoder.matches("Aa@12345", user.getPassword()));
        assertEquals(16000.0, user.getSalary());
        assertEquals(MemberType.SLIVER, user.getMemberType());
    }

    @Test
    void createUserSuccessful_WithGoldMemberType() {
        User user = initiateUserCreation.execute(UserRegistrationRequest
                .builder()
                .email("anh.nguyen3@sotatek.com")
                .password("Aa@12345")
                .salary(31000.0)
                .build());
        assertEquals("anh.nguyen3@sotatek.com", user.getEmail());
        assertTrue(passwordEncoder.matches("Aa@12345", user.getPassword()));
        assertEquals(31000.0, user.getSalary());
        assertEquals(MemberType.GOLD, user.getMemberType());
    }

    @Test
    void createUserSuccessful_WithPlatinumMemberType() {
        User user = initiateUserCreation.execute(UserRegistrationRequest
                .builder()
                .email("anh.nguyen3@sotatek.com")
                .password("Aa@12345")
                .salary(51000.0)
                .build());
        assertEquals("anh.nguyen3@sotatek.com", user.getEmail());
        assertTrue(passwordEncoder.matches("Aa@12345", user.getPassword()));
        assertEquals(51000.0, user.getSalary());
        assertEquals(MemberType.PLATINUM, user.getMemberType());
    }

    @Test
    void createUserFailed_WhenExistUserWithRequestEmail(){
        // insert user with email is anh.nguyen3@sotatek.com
        userRepository.save(User.builder()
                .email("anh.nguyen3@sotatek.com")
                .salary(16000.0)
                .password(passwordEncoder.encode("Aa@12345"))
                .memberType(MemberType.SLIVER)
                .build());
        // create new user with request email is anh.nguyen3@sotatek.com
            assertThrows(UserAlreadyExistsException.class, () -> {
            initiateUserCreation.execute(UserRegistrationRequest
                    .builder()
                    .email("anh.nguyen3@sotatek.com")
                    .password("Aa@12345")
                    .salary(51000.0)
                    .build());
        },"Failed, expected throw an UserAlreadyExistsException");
        //assertEquals("Exist user with request email", exception.);
    }
}