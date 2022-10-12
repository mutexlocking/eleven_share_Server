package com.konkuk.eleveneleven.src.room.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomCodeRequest {

    @NotNull(message = "방에 참여하기 위해서는 방 참여 코드는 필수 입니다.")
    @Size(min = 6, max = 6, message = "방참여코드는 숫자와 영어 소문자로 구성된 총 6자리 코드 입니다.")
    private String roomCode;
}
