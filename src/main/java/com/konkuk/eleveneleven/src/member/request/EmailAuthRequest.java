package com.konkuk.eleveneleven.src.member.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailAuthRequest {

    @NotNull(message = "인증코드 값은 필수로 입력하셔야 합니다.")
    @Size(min = 6, max = 6, message = "인증코드는 숫자와 영어 소문자로 구성된 총 6자리 코드 입니다.")
    private String authCode;


}
