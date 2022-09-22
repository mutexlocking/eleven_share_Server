package com.konkuk.eleveneleven.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BaseException extends Throwable {
    private final BaseResponseStatus status;
}
