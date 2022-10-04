package com.konkuk.eleveneleven.src.member.dto;

import com.konkuk.eleveneleven.common.enums.Screen;
import com.konkuk.eleveneleven.common.enums.Status;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginMemberDto {

    private String token;
    private Long memberIdx;
    private String memberName;
    private String schoolName;
    private Status status;
    private Screen screen;
    private Boolean isBelongToRoom;
    private Boolean isRoomOwner;
    private Long roomIdx;

}
