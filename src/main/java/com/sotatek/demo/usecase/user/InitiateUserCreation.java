package com.sotatek.demo.usecase.user;

import com.sotatek.demo.domain.entitiy.User;
import com.sotatek.demo.domain.enums.MemberType;
import com.sotatek.demo.domain.exception.user.UserAlreadyExistsException;
import com.sotatek.demo.domain.port.UserRepository;
import com.sotatek.demo.executable.application.controller.user.dto.UserRegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RequiredArgsConstructor
public class InitiateUserCreation {

    private static final Logger logger = LoggerFactory.getLogger(InitiateUserCreation.class);

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public User execute(UserRegistrationRequest request) {
        // validate request data
        request = standardize(request);

        logger.info("action", "user_creation_started",
                "status", request,
                "email", request.getEmail(),
                "salary", request.getSalary());

        // check exist user by email
        validateIfUserExists(request.getEmail());

        // create new User base on request
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .salary(request.getSalary())
                .build();
        // set member type base on salary value
        user.setMemberType(MemberType.getBySalary(request.getSalary()));
        return userRepository.save(user);
    }

    /**
     * Standardize program and event topic request before save to db
     *
     * @param original origin user request
     * @return user request after standardized
     */
    private UserRegistrationRequest standardize(UserRegistrationRequest original) {
        // standardized email and password
        return UserRegistrationRequest.builder()
                .email(original.getEmail().toLowerCase())
                .password(original.getPassword().replaceAll("\\s",""))
                .salary(original.getSalary())
                .build();
    }

    private void validateIfUserExists(String emailStd) {
        if (userRepository.findByEmail(emailStd).isPresent()) {
            logger.error("Exist user with request email {}", emailStd);
            throw new UserAlreadyExistsException();
        }
    }
}
