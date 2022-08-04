package com.sotatek.demo.executable.config;

import com.sotatek.demo.domain.port.UserRepository;
import com.sotatek.demo.usecase.user.InitiateUserCreation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class UserCaseConfig {

    @Bean
    public InitiateUserCreation initiateUserCreation(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        return new InitiateUserCreation(userRepository, passwordEncoder);
    }
}
