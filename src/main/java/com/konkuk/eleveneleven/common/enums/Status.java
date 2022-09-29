package com.konkuk.eleveneleven.common.enums;

public enum Status {


    /**
     * INACTIVE : 비활성화된 엔티티
     * ONGOING  : 비활성화 되지도 , 활성화 되지도 않은 엔티티 -> ex) 카카오 로그인만 하고 , 앱 자체 인증을 통과하지 못한 Member
     * ACTIVE   : 활성화된 엔티티 -> ex) 카카오 로그인도 , 앱 자체 인증도 통과한 Member
     * */

    INACTIVE, ONGOING, ACTIVE;
}
