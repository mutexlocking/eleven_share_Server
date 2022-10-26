package com.konkuk.eleveneleven.src.auth.dto;

import com.konkuk.eleveneleven.common.enums.Gender;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostAuthMetaReqDto {
    private Long kakaoId;
    private String name;
    private Gender gender;
    private String schoolName;
    private String studentId;
    private String schoolEmail;
    private String major;
}
