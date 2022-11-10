package com.konkuk.eleveneleven.src.scheduler.advice;

import com.konkuk.eleveneleven.common.exceptions.ValidationFail;
import com.konkuk.eleveneleven.common.exceptions.ValidationFailForField;
import com.konkuk.eleveneleven.common.exceptions.ValidationFailForObject;
import com.konkuk.eleveneleven.config.BaseException;
import com.konkuk.eleveneleven.config.BaseResponse;
import com.konkuk.eleveneleven.src.scheduler.controller.SchedulerController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice(assignableTypes = SchedulerController.class)
public class SchdulerControllerAdvice {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse RoomControllerExHandler(BaseException e){
        log.error("EXCEPTION = {} , INTERNAL_MESSAGE = {}", e.getStatus(), e.getInternalMessage());
        return new BaseResponse(e.getStatus());
    }

    private ValidationFail makeValidationError(BindingResult bindingResult){
        return  ValidationFail.builder()
                .fieldList(bindingResult.getFieldErrors().stream()
                        .map(f -> new ValidationFailForField(f))
                        .collect(Collectors.toList()))
                .objectList(bindingResult.getGlobalErrors().stream()
                        .map(o -> new ValidationFailForObject(o))
                        .collect(Collectors.toList()))
                .build();

    }
}
