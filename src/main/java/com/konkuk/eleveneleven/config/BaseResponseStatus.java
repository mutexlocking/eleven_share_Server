package com.konkuk.eleveneleven.config;

import lombok.Getter;

/**
 * [ 1000단위 ]
 *  1000 : 요청 성공
 *  2 : Request 오류
 *  3 : Reponse 오류
 *  4 : DB, Server 오류
 *
 * [ 100단위 ]
 *  0 : 공통 오류
 *  1 : Member 오류
 *  2 : Room 오류
 *  3 : MemberRoom 오류
 *
 *
 * [10단위]
 *  0~19 : Common
 *  20~39 : GET
 *  40~59 : POST
 *  60~79 : PATCH
 *  80~99 : else
 */

@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),

    /**
     * 2000 : Request 오류
     */
    INVALID_BEAN_VALIDATION(false, 2001, "요청값에 대한 Bean Validation 처리 과정에서 오류가 있습니다."),
    NO_JWT_TOKEN(false, 2002, "인증 인가를 위한 jwt token값이 존재하지 않습니다."),
    INAVALID_JWT_TOKEN(false, 2003, "인증 인가를 위한 jwt token값이 유효하지 않습니다."),
    EXPIRED_JWT_TOKEN(false, 2004, "인증 인가를 위한 jwt token의 만료 시간이 초과되었습니다."),
    INVALID_MEMBER(false,2101, "아직 모든 인증 과정을 다 거치지 않은 사용자 입니다."),
    INVALID_AUTH_CODE(false,2121, "이메일 인증 코드가 일치하지 않아, 이메일 인증에 실패하였습니다."),
    FAIL_LOGIN(false, 2141, "로그인에 실패했습니다"),
    INVALID_EMAIL_DOMAIN(false, 2142, "서울시에 등록된 대학교의 이메일 도메인이 아닙니다."),
    FAIL_SEND_EMAIL(false,2142, "인증 메일 전송에 실패하였습니다."),
    INVALID_UNIV(false,2005, "서비스 사용이 불가한 대학입니다."),
    INVALID_USER(false,2006, "서비스 사용이 불가한 유저입니다.(대학원생)"),
    INVALID_ID_CARD(false,2007, "유효하지 않은 학생증입니다."),
    INVALID_IMG_FORMAT(false,2008, "유효하지 않은 이미지 형식입니다."),
    INVALID_OWNER_MEMBER(false, 2141, "유효하지 않은 방장 멤버 idx 값 입니다."),
    INVALID_ROOM(false, 2241, "유효하지 않은 방 idx 값 입니다."),


    /**
     * 3000 : Response 오류
     */
    NO_MONTHLY_BADGE(false, 3030, "이번 달에 획득한 뱃지가 없습니다. (PRO, LOVER, MASTER)"),

    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),
    MODIFY_WKT_FAIL(false, 4002, "WKT 변환을 실패하였습니다."),
    ENCRYPT_FAIL(false, 4003, "암호화를 실패하였습다."),
    MODIFY_OBJECT_FAIL(false, 4004, "Request 객체 변환에 실패하였습니다."),
    S3UPLOAD_ERROR(false, 4080, "파일 업로드에 실패하였습니다."),
    DETECT_TEXT_FAIL(false, 4081, "OCR 파일 분석에 실패하였습니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) { //BaseResponseStatus 에서 각 해당하는 코드를 생성자로 맵핑
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
