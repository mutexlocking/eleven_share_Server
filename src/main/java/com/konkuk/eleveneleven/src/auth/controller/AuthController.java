package com.konkuk.eleveneleven.src.auth.controller;

import com.konkuk.eleveneleven.config.BaseResponse;
import com.konkuk.eleveneleven.src.auth.dto.PostAuthMetaReqDto;
import com.konkuk.eleveneleven.src.auth.dto.PostAuthResDto;
import com.konkuk.eleveneleven.src.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    /** kakaoId 만 저장된 Member 튜플 생성
     * 해당 kakaoId가 이미 존재하는지 확인하여 분기
     * Status -> ONGOING
     * Response : 안드로이드에서 넘어가야할 화면
     * */
    @PostMapping("")
    public BaseResponse<PostAuthResDto> postAuth(@RequestAttribute Long kakaoId){

        return new BaseResponse<>(PostAuthResDto.builder()
                .screen(authService.postAuth(kakaoId))
                .build());
    }

    /** kakaoId 로 Member 를 특정하여 name, gender, schoolName, studentId, schoolEmail, major 저장
     * Stauts -> ACTIVE
     * */
    @PostMapping("/meta")
    public BaseResponse<String> postAuthMeta(@RequestAttribute Long kakaoId, @RequestBody PostAuthMetaReqDto postAuthMetaReqDto){

        authService.postAuthMeta(kakaoId,postAuthMetaReqDto);

        return new BaseResponse<>("데이터 저장에 성공했습니다.");

    }



}
