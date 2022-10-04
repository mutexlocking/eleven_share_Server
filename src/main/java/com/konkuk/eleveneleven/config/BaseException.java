package com.konkuk.eleveneleven.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class BaseException extends RuntimeException {
    private final BaseResponseStatus status;
    private String internalMessage;


}
