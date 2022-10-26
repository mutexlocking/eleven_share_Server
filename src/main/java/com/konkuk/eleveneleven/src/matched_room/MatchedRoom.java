package com.konkuk.eleveneleven.src.matched_room;

import com.konkuk.eleveneleven.common.basic.BasicEntity;
import com.konkuk.eleveneleven.common.enums.Gender;
import com.konkuk.eleveneleven.common.enums.MatchingYN;
import com.konkuk.eleveneleven.common.enums.Status;
import com.konkuk.eleveneleven.src.matched_room_member.MatchedRoomMember;
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
@Table(name = "matched_room")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // protected 기본 생성자를 추가해줌 (JPA 규약 상 의무적으로 필요함)
public class MatchedRoom extends BasicEntity {

    @Id
    @GeneratedValue
    @Column(name = "matched_room_idx")
    private Long idx;

    @JoinColumn(name = "owner_member_idx")
    @OneToOne(fetch = FetchType.LAZY)
    private Member ownerMember;

    @OneToMany(mappedBy = "matchedRoom")
    private List<MatchedRoomMember> matchedRoomMemberList = new ArrayList<>();

    // 연관관계 편의 메서드
    public void setOwnerMember(Member ownerMember){
        this.ownerMember = ownerMember;
        ownerMember.setMatchedRoom(this);
    }

    //생성자
    public MatchedRoom (Member ownerMember){
        this.setOwnerMember(ownerMember);
        this.setStatus(Status.ACTIVE);
    }

    /** [변경 메서드] */
    public void update(Status status){
        this.setStatus(status);
    }

}
