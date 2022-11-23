package com.konkuk.eleveneleven.src.matched_room.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UrlRequest {

    @Positive(message = "matchedRoomIdx 값은 양수 입니다.")
    @NotNull(message = "어떤 매칭된 방에 오픈채팅 url을 공유할지에 대한, matchedRoomIdx 값은 필수 입니다.")
    private Long matchedRoomIdx;

    @NotNull(message = "오픈채팅 url은 필수입니다.")
    private String url;
}
