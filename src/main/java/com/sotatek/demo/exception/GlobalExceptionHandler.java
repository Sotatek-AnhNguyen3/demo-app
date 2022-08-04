package com.sotatek.demo.exception;

import com.sotatek.demo.domain.constants.ResponseStatus;
import com.sotatek.demo.domain.enums.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    @Value("${reflectoring.trace:false}")
    private boolean printStackTrace;

    // Override handle exception when invalid method argument
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatus httpStatus, WebRequest request) {

        Map<String, Object> errorAttributes = new HashMap<>();

        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        errorAttributes.put(ErrorResponse.DESCRIPTION, errors);
        errorAttributes.put(ErrorResponse.CODE, ResponseCode.METHOD_ARGUMENT_INVALID.getCode());
        errorAttributes.put(ErrorResponse.STATUS, ResponseStatus.FAILED);
        return buildErrorResponse(ex, errorAttributes, HttpStatus.BAD_REQUEST, request);
    }

    // Handle all exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllUncaughtException(Exception error, WebRequest request) {
        log.error("custom_exception_occurred", error);
        Map<String, Object> errorAttributes = new LinkedHashMap<>();
        // try to lookup for config
        // default http status
        AtomicReference<HttpStatus> httpStatus = new AtomicReference<>(HttpStatus.INTERNAL_SERVER_ERROR);
        ResponseCode.lookUp(error)
                .ifPresentOrElse(responseCode -> {
                            // set code and status on Response
                            errorAttributes.put(ErrorResponse.CODE, responseCode.getCode());
                            errorAttributes.put(ErrorResponse.STATUS, ResponseStatus.FAILED);
                            // reset http status by value that define on response code
                            httpStatus.set(responseCode.getHttpStatus());
                            Optional.ofNullable(error.getMessage())
                                    .ifPresentOrElse(msg -> errorAttributes.put(ErrorResponse.DESCRIPTION, msg),
                                            // put default description
                                            () -> errorAttributes.put(ErrorResponse.DESCRIPTION, responseCode.getDescription()));
                        },
                        () -> {
                            errorAttributes.put(ErrorResponse.CODE, ResponseCode.ERROR.getCode());
                            errorAttributes.put(ErrorResponse.STATUS, ResponseCode.ERROR.getStatus());
                            errorAttributes.put(ErrorResponse.DESCRIPTION, "Internal Server Error");
                        });
        return buildErrorResponse(error, errorAttributes, httpStatus.get(), request);
    }

    private ResponseEntity<Object> buildErrorResponse(Exception exception,
                                                      Map<String, Object> errorAttributes,
                                                      HttpStatus httpStatus,
                                                      WebRequest request) {
        // add more info to trace
        if (printStackTrace) {
            errorAttributes.put(ErrorResponse.PATH, ((ServletWebRequest) request).getRequest().getRequestURI());
            errorAttributes.put(ErrorResponse.TIME, LocalDateTime.now());
            errorAttributes.put(ErrorResponse.STACK_TRACE, ExceptionUtils.getStackTrace(exception));
        }
        return ResponseEntity.status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorAttributes);
    }

    @Override
    public ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            Object body,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        return handleAllUncaughtException(ex, request);
    }

    private class ErrorResponse {
        public static final String CODE = "code";
        public static final String STATUS = "status";
        public static final String DESCRIPTION = "description";
        public static final String PATH = "path";
        public static final String TIME = "timestamp";
        public static final String STACK_TRACE = "stack_trace";

    }

}
