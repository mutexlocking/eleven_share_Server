package com.konkuk.eleveneleven.src.room_member;

import com.konkuk.eleveneleven.common.basic.BasicEntity;
import com.konkuk.eleveneleven.common.enums.Status;
import com.konkuk.eleveneleven.src.member.Member;
import com.konkuk.eleveneleven.src.room.Room;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "room_member")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // protected 기본 생성자를 추가해줌 (JPA 규약 상 의무적으로 필요함)
public class RoomMember extends BasicEntity {

    @Id
    @GeneratedValue
    @Column(name = "room_member_idx")
    private Long idx;

    @JoinColumn(name = "room_idx")
    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;

    @JoinColumn(name = "member_idx")
    @OneToOne(fetch = FetchType.LAZY)
    private Member member;

    // 연관관계 편의 메서드

    public void setRoom(Room room){
        this.room = room;
        room.getRoomMemberList().add(this);
    }


    public void setMember(Member member){
        this.member = member;
        member.setRoomMember(this);
    }

    // 생성자
    public RoomMember(Room room, Member member){
        this.setRoom(room);
        this.setMember(member);
        this.setStatus(Status.ACTIVE);
    }

    /** [변경메서드] */
    public void update(Status status){
        this.setStatus(status);
    }

    public void updateRoom(Room room){
        this.setRoom(room);
    }

}

