package com.konkuk.eleveneleven.common.enums;

public enum Gender {

    /**
     * MALE   : 남성
     * FEMALE : 여성
     *
     * 굳이 enum으로 정의한 이유는 , 나중에 이 2개의 enum값이 들어오지 않으면 Binding 예외가 터지도록 하여 , ExceptionHandler에서 일괄처리 할 수 있기 떄문이다.
     * */
    MALE , FEMALE
}
