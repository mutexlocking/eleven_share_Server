package com.konkuk.eleveneleven.src.matched_room.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotNull;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UrlRequest {

    @NotNull(message = "오픈채팅 url은 필수입니다.")
    private String url;
}
