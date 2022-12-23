package com.konkuk.eleveneleven.src.member.service;

import com.konkuk.eleveneleven.common.enums.MatchingYN;
import com.konkuk.eleveneleven.common.enums.Screen;
import com.konkuk.eleveneleven.common.enums.Status;
import com.konkuk.eleveneleven.common.jwt.JwtUtil;
import com.konkuk.eleveneleven.common.mail.MailUtil;
import com.konkuk.eleveneleven.config.BaseException;
import com.konkuk.eleveneleven.config.BaseResponseStatus;
import com.konkuk.eleveneleven.src.matched_room_member.MatchedRoomMember;
import com.konkuk.eleveneleven.src.matched_room_member.repository.MatchedRoomMemberRepository;
import com.konkuk.eleveneleven.src.member.Member;
import com.konkuk.eleveneleven.src.member.dto.EmailAuthDto;
import com.konkuk.eleveneleven.src.member.dto.EmailDto;
import com.konkuk.eleveneleven.src.member.dto.LoginMemberDto;
import com.konkuk.eleveneleven.src.member.dto.LogoutDto;
import com.konkuk.eleveneleven.src.member.repository.MemberRepository;
import com.konkuk.eleveneleven.src.room.dto.MatchingYnDto;
import com.konkuk.eleveneleven.src.room_member.RoomMember;
import com.konkuk.eleveneleven.src.room_member.repository.RoomMemberRepository;
import com.konkuk.eleveneleven.src.school.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final MatchedRoomMemberRepository matchedRoomMemberRepository;
    private final SchoolRepository schoolRepository;
    private final JwtUtil jwtUtil;
    private final MailUtil mailUtil;



    private void checkActive(Long memberIdx){
        if(memberRepository.existsByIdxAndStatus(memberIdx, Status.INACTIVE)){
            throw new BaseException(BaseResponseStatus.INACTIVE_MEMBER, "해당 회원은 회원탈퇴 한 회원입니다.");
        }
    }

    private void checkOnGoing(Long memberIdx){
        if(memberRepository.existsByIdxAndStatus(memberIdx, Status.ONGOING)){
            throw new BaseException(BaseResponseStatus.FAIL_LOGIN, "로그인 시점 : 해당 사용자는 아직 인증이 다 끝나지 않은 사용자 입니다.");
        }
    }

    /**
     * 로그인 서비스
     */
    public LoginMemberDto checkLogin(Long memberIdx) {

        //0_1. 해당 사용자가 회원탈퇴 한 사용자가 아닌지 검사
        checkActive(memberIdx);

        //0_2. 해당 사용자가 아직 인증을 다 마치지 않아 ONGOING 상태인지의 여부 검사 (ACTIVE상태일 테지만 validation은 필요)
        /** 이를 통해 -> 로그인 이후는 무조건 ACTIVE 상태라는 사실이 보장됨 */
        checkOnGoing(memberIdx);

        //1. 유효한 kakaoId로 JWT를 생성
        Member member = memberRepository.findByMemberIdx(memberIdx);
        String token = jwtUtil.createToken(member.getIdx().toString());

        //2. Member를 조회하여 , 다음의 상황을 판단
        /**
         * ACTIVE일 경우
         * 1) 어떤 방도 만들지 않았거나 or 어떤 방에도 참여하지 않은 경우 -> 메인화면으로 가도록 응답을 보냄
         * 2_1) 방을 만들었거나 or 다른사람이 만든 방에 소속된 경우 && 이때 매칭버튼을 누르거나 or 누르지 않았거나 -> 해당 방 화면으로 가도록 해야함
         * 2_2) 혹은 (방장이든 일반 상요자든) 이미 매칭이 되어 MatchedRoom에 소속되어 있는 경우 -> MATCHED_ROOM_SCREEN으로 이동하도록!
         * */

        // Member의 status가 ACTIVE인 경우
        LoginMemberDto loginMemberDto = LoginMemberDto.builder()
                .token(token)
                .memberIdx(member.getIdx())
                .memberName(member.getName())
                .schoolName(member.getSchoolName())
                .status(member.getStatus()).build();

        /** 해당 Member와 대응되는 (ACTIVE한) RoomMember가 존재한다는 것은 -> 그 Member는 어느 방에 소속되어 있다는 의미!
        // 반대로 대응되는 RoomMember가 존재하지 않다는 것은
         -> 1) 그 Member는 어떤 방에도 소속되어 있지 않거나
         -> 2) 혹은 MatchedRoom에 소속되어 있을 수 있음 */

        roomMemberRepository.findByMemberIdxAndStatus(member.getIdx(), Status.ACTIVE).ifPresentOrElse(
                rm -> setLoginMemberDtoAtBelongRoom(loginMemberDto, rm),
                () -> {setLoginMemberDtoAtNotBelongRoom(loginMemberDto);}
        );


        return loginMemberDto;

    }


    //로그인한 Member가 Room을 만들었거나 or 다른사람이 만든 Room에 속한 경우 -> LoginMemberDto를 세팅하는 메소드
    private void setLoginMemberDtoAtBelongRoom(LoginMemberDto loginMemberDto, RoomMember rm){
        loginMemberDto.setScreen(
                rm.getRoom().getMatchingYN().equals(MatchingYN.N)
                ? Screen.ROOM_SCREEN : Screen.READY_SCREEN);
        loginMemberDto.setIsBelongToRoom(true);
        loginMemberDto.setIsRoomOwner(Optional.ofNullable(rm.getMember().getRoom()).isPresent());
        loginMemberDto.setRoomIdx(rm.getRoom().getIdx());
        loginMemberDto.setIsBelongToMatchedRoom(false);
        loginMemberDto.setMatchedRoomIdx(-1L);
    }

    //로그인한 Member가 Room에 속하지 않는 경우 -> LoginMemberDto를 세팅하는 메소드
    private void setLoginMemberDtoAtNotBelongRoom(LoginMemberDto loginMemberDto){

        loginMemberDto.setIsBelongToRoom(false);
        loginMemberDto.setIsRoomOwner(false);
        loginMemberDto.setRoomIdx(-1L);

        matchedRoomMemberRepository.findByMemberIdxAndStatus(loginMemberDto.getMemberIdx(), Status.ACTIVE).ifPresentOrElse(
                mrm -> setForMatchedRoomScreen(loginMemberDto, mrm),
                () -> {setForMainScreen(loginMemberDto);}
        );

    }

    private void setForMatchedRoomScreen(LoginMemberDto loginMemberDto, MatchedRoomMember matchedRoomMember){
        loginMemberDto.setScreen(Screen.MATCHED_ROOM_SCREEN);
        loginMemberDto.setIsBelongToMatchedRoom(true);
        loginMemberDto.setMatchedRoomIdx(matchedRoomMember.getMatchedRoom().getIdx());
    }

    private void setForMainScreen(LoginMemberDto loginMemberDto){
        loginMemberDto.setScreen(Screen.MAIN_SCREEN);
        loginMemberDto.setIsBelongToMatchedRoom(false);
        loginMemberDto.setMatchedRoomIdx(-1L);
    }


    /** 인증메일 보내는 서비스 */
    @Transactional
    public EmailDto sendAuthMail(Long memberIdx, String email){

        //0. 일단 등록된 서울시 내 대학교 메일 계정인지 확인 후
        checkEmailDomain(email);

        // 1. 일단 해당 메일로 인증코드를 보낸 뒤
        String authCode = mailUtil.sendEmail(email);

        //2. 그 인증 코드를 DB에 저장
        updateAuthCode(memberIdx, authCode);

        return EmailDto.builder().authCode(authCode).build();
    }

    private void checkEmailDomain(String email){

        //1. 전체 이메일에서 , @뒤의 email domain 분리
        String emailDomain = email.split("@")[1];

        //2. 이후 DB에 등록된 서울시 전체 대학교의 이메일 도메인들을 대상으로 , 해당 분리시킨 도메인과 일치하는게 하나라도 있는지 check
        // 만약 전체 도메인들 중, 일치하는게 하나도 없다면 -> 이는 학교 계정이 아닌것으로 판별하고 예외 발생
        /** 단 이를 자바 스트림으로 직접 확인하는게 아니라 , exist 쿼리를 날려 한방에 확인한다 <- 이런습관을 들여야 함 */
        if(schoolRepository.existsByEmailDomain(emailDomain)==false){
            throw new BaseException(BaseResponseStatus.INVALID_EMAIL_DOMAIN, "학교 이메일 계정이 아닙니다.");
        }

    }

    private void updateAuthCode(Long memberIdx, String authCode){
        Member member = memberRepository.findByMemberIdx(memberIdx);
        member.setAuthCode(authCode);
    }

    /** 인증메일로 보낸 인증코드의 유효성 검사 서비스 */
    public EmailAuthDto checkAuthCode(Long memberIdx, String authCode){
        Member member = memberRepository.findByMemberIdx(memberIdx);

        if(member.getAuthCode().equals(authCode)==false){
            throw new BaseException(BaseResponseStatus.INVALID_AUTH_CODE, "인증 코드가 일치하지 않아, 이메일 인증에 실패");

        }

        return EmailAuthDto.builder()
                .result("학교 메일 인증이 정상 처리되었습니다.")
                .build();
    }

    /** ------------------------------------------------------------------------------------------------------------*/


    private void checkOngoingAtLogout(Long memberIdx){
        if(memberRepository.existsByIdxAndStatus(memberIdx, Status.ONGOING)){
            throw new BaseException(BaseResponseStatus.ONGOING_MEMBER, "아직 인증중인 회원은 회원탈퇴를 할 수 없습니다.");
        }
    }

    /** [회원탈퇴 서비스] */
    @Transactional
    public LogoutDto checkQuit(Long memberIdx){

        //0. 해당 사용자가 인증을 다 마치지 않은 ONGOING상태인지 확인
        checkOngoingAtLogout(memberIdx);

        //1. 인증을 다 마 친 경우라면 -> ACTIVE아니면 INACTIVE니까 , 그냥 INACTIVE로 바꿔주면 됨
        Member member = memberRepository.findByMemberIdx(memberIdx);
        member.setStatus(Status.INACTIVE);
        memberRepository.save(member);

        return LogoutDto.builder().result(true).build();

    }

    /** -------------------------------------------------------------------------------------------------------*/

    /** [매칭 정보를 조회하는 서비스] */
    public MatchingYnDto getMatching(Long memberIdx){

        //1. 방에 속한 Member를 조회 -> 이때 그런 방에 속한 멤버가 없으면 예외 발생
        Member member = memberRepository.findWithRoomInfoOptional(memberIdx).orElseThrow(
                () -> {
                    throw new BaseException(BaseResponseStatus.NOT_BELONG_TO_ROOM, "매칭 여부 조회 시점 : 해당 사용자는 어느 방에도 속하지 않은 사용자 입니다.");
                }
        );

        //2. 조회한 Member 정보를 바탕으로 , 매칭 여부 결과를 boolean 값으로 치환
        boolean result = member.getRoomMember().getRoom().getMatchingYN()==MatchingYN.Y ? true : false;

        //3. 결과 반환
        return MatchingYnDto.builder().result(result).build();
    }


}
