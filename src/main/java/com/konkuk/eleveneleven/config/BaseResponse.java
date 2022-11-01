package com.konkuk.eleveneleven.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.konkuk.eleveneleven.common.exceptions.ValidationFail;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.konkuk.eleveneleven.config.BaseResponseStatus.INVALID_BEAN_VALIDATION;
import static com.konkuk.eleveneleven.config.BaseResponseStatus.SUCCESS;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message","result"})
public class BaseResponse<T> {
    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    private final String message;
    private final int code;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    // 기본 요청에 성공한 경우
    public BaseResponse(T result) {
        this.isSuccess = SUCCESS.isSuccess();
        this.message = SUCCESS.getMessage();
        this.code = SUCCESS.getCode();
        this.result = result;
    }

    // 기본 요청에 실패한 경우
    public BaseResponse(com.konkuk.eleveneleven.config.BaseResponseStatus status) {
        this.isSuccess = status.isSuccess();
        this.message = status.getMessage();
        this.code = status.getCode();
    }


    public BaseResponse(com.konkuk.eleveneleven.config.BaseResponseStatus status, T bindResult){
        this.isSuccess = status.isSuccess();
        this.message = status.getMessage();
        this.code = status.getCode();
        this.result = bindResult;
    }

    // 요청에 실패한 경우 , 실패한 요청값을 담아서 보내는 경우
    public static <T> BaseResponse<T> failBeanValidation(T bindResult){
        return new BaseResponse<>(INVALID_BEAN_VALIDATION, bindResult);
    }

}
