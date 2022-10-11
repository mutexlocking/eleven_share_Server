package com.konkuk.eleveneleven.src.room.advice;

import com.konkuk.eleveneleven.config.BaseException;
import com.konkuk.eleveneleven.config.BaseResponse;
import com.konkuk.eleveneleven.src.room.controller.RoomController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(assignableTypes = RoomController.class)
public class RoomControllerAdvice {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse roomExceptionHandler(BaseException e){
        log.error("EXCEPTION = {} , INTERNAL_MESSAGE = {}", e.getStatus(), e.getInternalMessage());
        return new BaseResponse(e.getStatus());
    }

}
