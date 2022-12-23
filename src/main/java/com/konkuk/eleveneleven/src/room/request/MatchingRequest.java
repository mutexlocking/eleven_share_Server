package com.konkuk.eleveneleven.src.room.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchingRequest {

    @Positive(message = "roomIdx 값은 양수여야 합니다.")
    @NotNull(message = "어느 방에 대한 매칭 신청을 할것 인지, 그 Room의 Idx 값은 필수 입니다.")
    private Long roomIdx;
}
