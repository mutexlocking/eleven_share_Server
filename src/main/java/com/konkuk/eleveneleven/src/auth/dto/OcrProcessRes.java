package com.konkuk.eleveneleven.src.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OcrProcessRes {
    private String univ;
    private String major;

    /** 08학번과 같이 0이 앞에 오는 학번 표현 불가하여 String 자료형 선택 */
    private String student_num;

    @Builder
    public OcrProcessRes(String univ, String major, String student_num) {
        this.univ = univ;
        this.major = major;
        this.student_num = student_num;
    }
}
