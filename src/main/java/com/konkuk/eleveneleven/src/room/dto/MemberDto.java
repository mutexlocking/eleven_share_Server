package com.konkuk.eleveneleven.src.room.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MemberDto {

    private String name;
    private String schoolName;
    private String major;
    private Boolean isOwner;
}
