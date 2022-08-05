package com.sotatek.demo.domain.enums;

import com.sotatek.demo.domain.constants.ResponseStatus;
import com.sotatek.demo.domain.exception.user.UserAlreadyExistsException;
import com.sotatek.demo.domain.exception.common.InternalServerException;
import com.sotatek.demo.domain.exception.common.NoSuchElementFoundException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Optional;

public enum ResponseCode {
    // Common exception
    INTERNAL_SERVER_ERROR("CEX001", ResponseStatus.ERROR, "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, InternalServerException.class),
    NO_SUCH_ELEMENT_FOUND_ERROR("CEX002", ResponseStatus.FAILED, "No Such Element Found", HttpStatus.NOT_FOUND, NoSuchElementFoundException.class),
    // Argument validation
    METHOD_ARGUMENT_INVALID("MAI001", ResponseStatus.FAILED),
    // User exception
    USER_ALREADY_EXISTS("USE001", ResponseStatus.FAILED, "User Already Exists With Email", HttpStatus.BAD_REQUEST, UserAlreadyExistsException.class),
    // Constant
    SUCCESS("SUC001", ResponseStatus.PASSED),
    ERROR("ERR001", ResponseStatus.ERROR);

    @Getter
    private final String code;

    @Getter
    private final String status;

    @Getter
    private String description;

    @Getter
    private HttpStatus httpStatus;

    @Getter
    private Class<? extends Throwable>[] throwableClasses;

    @SafeVarargs
    ResponseCode(String code, String status, String description, HttpStatus httpStatus, Class<? extends Throwable>... throwableClasses) {
        this.code = code;
        this.status = status;
        this.description = description;
        this.httpStatus = httpStatus;
        this.throwableClasses = throwableClasses;
    }

    ResponseCode(String code, String status) {
        this.code = code;
        this.status = status;
    }

    public static Optional<ResponseCode> lookUp(Throwable throwable) {
        return Optional.ofNullable(throwable)
                .map(Throwable::getClass)
                .flatMap(throwableClass -> Arrays.stream(values())
                        .filter(c -> {
                            if (c.getThrowableClasses() == null) {
                                return false;
                            }
                            return Arrays.asList(c.getThrowableClasses()).contains(throwableClass);
                        })
                        .findFirst()
                );
    }
}
