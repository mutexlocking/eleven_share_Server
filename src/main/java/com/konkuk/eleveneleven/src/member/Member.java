package com.konkuk.eleveneleven.src.member;

import com.konkuk.eleveneleven.common.basic.BasicEntity;
import com.konkuk.eleveneleven.common.enums.Gender;
import com.konkuk.eleveneleven.common.enums.Status;
import com.konkuk.eleveneleven.src.member_room.MemberRoom;
import com.konkuk.eleveneleven.src.room.Room;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // protected 기본 생성자를 추가해줌 (JPA 규약 상 의무적으로 필요함)
public class Member extends BasicEntity {

    @Id
    @GeneratedValue
    @Column(name = "member_idx")
    private Long idx;

    @Column(name = "kakao_id")
    private Long kakaoId;

    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "school_name")
    private String schoolName;

    @Column(name = "student_id")
    private String studentId;

    @Column(name = "school_email")
    private String schoolEmail;

    private String major;

    private String authCode;

    @OneToOne(mappedBy = "ownerMember", fetch = FetchType.LAZY)
    private Room room = null; // 방을 만들지 않은 Member는 이 room 값이 null 이라는 점 주의 + 게다가 막 생성된 Member는 어떤 Room도 만들지 않았을테니 null로 초기화 하는것이 맞음.

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
    private MemberRoom memberRoom;

    //생성자
    public Member(Long kakaoId, String name, Gender gender, String schoolName, String studentId, String schoolEmail, String major){
        this.kakaoId = kakaoId;
        this.name = name;
        this.gender = gender;
        this.schoolName = schoolName;
        this.studentId = studentId;
        this.schoolEmail = schoolEmail;
        this.major = major;
        this.setStatus(Status.ONGOING);
    }


}
