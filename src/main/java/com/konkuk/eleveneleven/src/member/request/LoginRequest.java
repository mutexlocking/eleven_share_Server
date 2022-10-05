package com.konkuk.eleveneleven.src.member.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class  LoginRequest {

    @NotNull(message = "카카오 OAuth 서비스 응답 결과로 받은 Id는 필수 요청값 입니다.")
    private Long kakaoId;
}
