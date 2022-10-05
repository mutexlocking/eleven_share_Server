package com.konkuk.eleveneleven.common.advice;

import com.konkuk.eleveneleven.config.BaseResponse;
import com.konkuk.eleveneleven.config.BaseResponseStatus;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalAdvice {

    /** jwt가 예상하는 형식과 다른 형식이거나 구성 */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse unsupportedJwtException(UnsupportedJwtException e){
        log.error("EXCEPTION = {} , INTERNAL_MESSAGE = {}", e, e.getMessage());
        return new BaseResponse(BaseResponseStatus.INAVALID_JWT_TOKEN);
    }

    /** 잘못된 jwt 구조 */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse malformedJwtException(MalformedJwtException e){
        log.error("EXCEPTION = {} , INTERNAL_MESSAGE = {}", e, e.getMessage());
        return new BaseResponse(BaseResponseStatus.INAVALID_JWT_TOKEN);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse stringIndexOutOfBoundsException(StringIndexOutOfBoundsException e){
        log.error("EXCEPTION = {} , INTERNAL_MESSAGE = {}", e, e.getMessage());
        return new BaseResponse(BaseResponseStatus.INAVALID_JWT_TOKEN);
    }


    /** JWT의 유효기간이 초과 */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse expiredJwtException(ExpiredJwtException e){
        log.error("EXCEPTION = {} , INTERNAL_MESSAGE = {}", e, e.getMessage());
        return new BaseResponse(BaseResponseStatus.EXPIRED_JWT_TOKEN);
    }

    /** JWT의 서명실패(변조 데이터) */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse signatureException(SignatureException e){
        log.error("EXCEPTION = {} , INTERNAL_MESSAGE = {}", e, e.getMessage());
        return new BaseResponse(BaseResponseStatus.EXPIRED_JWT_TOKEN);
    }


}
