package com.konkuk.eleveneleven.src.auth.dto;

import com.konkuk.eleveneleven.common.enums.Screen;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostAuthResDto {

    private Screen screen;

}
