package com.konkuk.eleveneleven.common.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konkuk.eleveneleven.config.BaseResponse;
import com.konkuk.eleveneleven.config.BaseResponseStatus;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimeInterceptor implements HandlerInterceptor{

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        /** 헤더에 isTest 값이 true 이면 인터셉터 로직을 수행하지 않고 바로 넘어가도록 */
        if(Boolean.parseBoolean(request.getHeader("isTest"))){
            return true;
        }

        /** 현재 시간을 조회하여 , 시간이 23:12 ~ 11:10 이면 -> 방생성 / 방 참여 / 방 나가기 API를 호출하지 못하도록 제어 */
        LocalTime now = LocalTime.now();
        if( now.isAfter(LocalTime.of(23, 11)) || now.isBefore(LocalTime.of(11, 11))){
            return sendErrorResponse(response, BaseResponseStatus.INVALID_ROOM_TIME);
        }

        return true;
    }

    /** 토큰이 존재하지 않거나 , 유효하지 않은 문제상황인 경우 -> 그 문제상황에 예외를 터뜨리지 않고 , direct로 JSON 응답을 보내는 경우 */
    private boolean sendErrorResponse(HttpServletResponse response, BaseResponseStatus status){

        //응답의 meta 정보를 setting한 후
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            //실질적인 응답값을 , JSON 형식의 String으로 변환화여 보낸다. (단 BaseResponse라는 공통 응답 형식을 지키면서)
            String result = objectMapper.writeValueAsString(new BaseResponse(status));
            response.getWriter().print(result);
        }
        catch (IOException e){
            log.error("방생성/방참여/방나가기 기능 수행 가능한 시간이 아니여서, 그에따른 처리를 하는 도중 IOException이 발생하였습니다.");
        }

        log.error("EXCEPTION = {}, message = {}", status, status.getMessage());
        return false;
    }
}
