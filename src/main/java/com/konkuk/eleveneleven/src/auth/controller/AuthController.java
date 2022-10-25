package com.konkuk.eleveneleven.src.auth.controller;

import com.konkuk.eleveneleven.config.BaseResponse;
import com.konkuk.eleveneleven.src.auth.dto.PostAuthMetaReqDto;
import com.konkuk.eleveneleven.src.auth.dto.PostAuthReqDto;
import com.konkuk.eleveneleven.src.auth.dto.PostAuthResDto;
import com.konkuk.eleveneleven.src.auth.service.AuthService;
import com.konkuk.eleveneleven.src.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("")
    public BaseResponse<String> postAuth(@RequestBody PostAuthReqDto postAuthReqDto){

        return new BaseResponse<>(authService.postAuth(postAuthReqDto.getKakaoId()));

    }

    @PostMapping("/meta")
    public BaseResponse<String> postAuthMeta(@RequestBody PostAuthMetaReqDto postAuthMetaReqDto){

        authService.postAuthMeta(postAuthMetaReqDto);

        return new BaseResponse<>("데이터 저장에 성공했습니다.");

    }



}
