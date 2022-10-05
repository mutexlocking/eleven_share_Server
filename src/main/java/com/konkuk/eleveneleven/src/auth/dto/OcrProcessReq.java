package com.konkuk.eleveneleven.src.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
public class OcrProcessReq {
    private MultipartFile idCardImg;
    private String name;

    @Builder
    public OcrProcessReq(MultipartFile idCardImg, String name) {
        this.idCardImg = idCardImg;
        this.name = name;
    }
}
