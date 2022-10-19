package com.konkuk.eleveneleven.src.room;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.konkuk.eleveneleven.common.basic.BasicEntity;
import com.konkuk.eleveneleven.common.enums.Gender;
import com.konkuk.eleveneleven.common.enums.MatchingYN;
import com.konkuk.eleveneleven.common.enums.Status;
import com.konkuk.eleveneleven.src.member.Member;
import com.konkuk.eleveneleven.src.room_member.RoomMember;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "room")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // protected 기본 생성자를 추가해줌 (JPA 규약 상 의무적으로 필요함)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class Room extends BasicEntity {

    @Id
    @GeneratedValue
    @Column(name = "room_idx")
    private Long idx;

    @JoinColumn(name = "owner_member_idx")
    @OneToOne(fetch = FetchType.LAZY)
    private Member ownerMember;

    @Column(name = "room_code")
    private String roomCode;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "matching_yn")
    private MatchingYN matchingYN;

    @OneToMany(mappedBy = "room")
    private List<RoomMember> roomMemberList = new ArrayList<>();

    // 연관관계 편의 메서드
    public void setOwnerMember(Member ownerMember){
        this.ownerMember = ownerMember;
        ownerMember.setRoom(this);
    }

    //생성자
    public Room (Member ownerMember, String roomCode){
        this.setOwnerMember(ownerMember);
        this.roomCode = roomCode;
        this.gender = ownerMember.getGender();
        this.matchingYN = MatchingYN.N;
        this.setStatus(Status.ACTIVE);
    }

    /** [변경 메서드] */
    public void update(Status status){
        this.setStatus(status);
    }

}

