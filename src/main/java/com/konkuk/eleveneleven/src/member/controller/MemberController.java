package com.konkuk.eleveneleven.src.member.controller;

import com.konkuk.eleveneleven.common.encryption.AES128;
import com.konkuk.eleveneleven.common.jwt.JwtUtil;
import com.konkuk.eleveneleven.config.BaseResponse;
import com.konkuk.eleveneleven.src.member.dto.EmailAuthDto;
import com.konkuk.eleveneleven.src.member.dto.EmailDto;
import com.konkuk.eleveneleven.src.member.dto.LoginMemberDto;
import com.konkuk.eleveneleven.src.member.request.EmailAuthRequest;
import com.konkuk.eleveneleven.src.member.request.EmailRequest;
import com.konkuk.eleveneleven.src.member.request.LoginRequest;
import com.konkuk.eleveneleven.src.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;
    private final AES128 aes128;

    @GetMapping("/aes")
    public String aes(@RequestParam String str){
        String encryptedPath = aes128.encrypt(str);
        return encryptedPath;
    }

    /**
     * [API. 2] : 로그인 API
     * */
    @PostMapping("/auth/login")
    public BaseResponse<LoginMemberDto> login(@RequestAttribute Long memberIdx){
        return new BaseResponse<>(memberService.checkLogin(memberIdx));
    }

    /**
     * [API. 3] : 학교 이메일을 받아 , 그 이메일로 인증코드 전송
     * */
    @PostMapping("/auth/email")
    public BaseResponse<EmailDto> sendEmail(@RequestAttribute Long memberIdx,
                                            @Validated @RequestBody EmailRequest emailRequest){
        return new BaseResponse<>(memberService.sendAuthMail(memberIdx, emailRequest.getEmail()));
    }


    /**
     * [API. 4] : 학교 이메일로 전송한 인증코드 일치 여부 확인
     * */
    @GetMapping("/auth/email")
    public BaseResponse<EmailAuthDto> checkAuthCode(@RequestAttribute Long memberIdx,
                                                    @Validated @ModelAttribute EmailAuthRequest emailAuthRequest){
        return new BaseResponse<>(memberService.checkAuthCode(memberIdx, emailAuthRequest.getAuthCode()));
    }


}
