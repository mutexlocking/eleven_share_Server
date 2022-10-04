package com.konkuk.eleveneleven.src.member.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {

    @NotNull(message = "학교 이메일을 필수로 입력해주세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;
}
