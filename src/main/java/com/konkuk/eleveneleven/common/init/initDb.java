package com.konkuk.eleveneleven.common.init;

import com.konkuk.eleveneleven.common.enums.Gender;
import com.konkuk.eleveneleven.src.member.Member;
import com.konkuk.eleveneleven.src.member_room.MemberRoom;
import com.konkuk.eleveneleven.src.room.Room;
import com.konkuk.eleveneleven.src.school.School;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

@Slf4j
@Component
@RequiredArgsConstructor
public class initDb {

    private final InitService initService;

    /** 샘플데이터로 DB 초기화 -> 스프링 빈 의존관계 주입이 끝난 직후 수행되는 로직 by @PostConstruct*/
    @PostConstruct
    void init(){
        initService.doInit1();
    }

    /** 실질적으로 샘플 데이터를 DB에 넣는 Service 로직 */
    @Service
    @Transactional
    @RequiredArgsConstructor
    static class InitService{

        private final EntityManager em;

        public void doInit1() {
            Member member1 = new Member(111L, "박상민", Gender.MALE, "건국대학교", "201712345", "abc@konkuk.ac.kr", "컴공");
            Member member2 = new Member(222L, "김선욱", Gender.MALE, "건국대학교", "201712345", "abc@konkuk.ac.kr", "컴공");
            Member member3 = new Member(333L, "김용준", Gender.MALE, "건국대학교", "201712345", "abc@konkuk.ac.kr", "컴공");
            em.persist(member1); em.persist(member2); em.persist(member3);
            em.flush(); em.clear();

            Room room1 = new Room(member1, "abcde");
            em.persist(room1);
            em.flush(); em.clear();

            MemberRoom memberRoom11 = new MemberRoom(room1, member1);
            MemberRoom memberRoom21 = new MemberRoom(room1, member2);
            MemberRoom memberRoom31 = new MemberRoom(room1, member3);
            em.persist(memberRoom11); em.persist(memberRoom21); em.persist(memberRoom31);
            em.flush(); em.clear();


            School school1  = new School("가톨릭대학교", "catholic.ac.kr");
            School school2  = new School("강서대학교", "gangseo.ac.kr");
            School school3  = new School("건국대학교", "konkuk.ac.kr");
            School school4  = new School("경기대학교", "kyonggi.ac.kr");
            School school5  = new School("경희대학교", "khu.ac.kr");
            School school6  = new School("고려대학교", "korea.ac.kr");
            School school7  = new School("광운대학교", "kw.ac.kr");
            School school8  = new School("국민대학교", "kookmin.ac.kr");
            School school9  = new School("동국대학교", "dongguk.edu");
            School school10 = new School("동국대학교", "dgu.ac.kr");
            School school11 = new School("명지대학교", "mju.ac.kr");
            School school12 = new School("삼육대학교", "syuin.ac.kr");
            School school13 = new School("상명대학교", "sangmyung.kr");
            School school14 = new School("서강대학교", "sogang.ac.kr");
            School school15 = new School("서경대학교", "skuniv.ac.kr");
            School school16 = new School("서울과학기술대학교", "g.seoultech.ac.kr");
            School school17 = new School("서울과학기술대학교", "seoultech.ac.kr");
            School school18 = new School("서울대학교", "snu.ac.kr");
            School school19 = new School("서울시립대학교", "uos.ac.kr");
            School school20 = new School("성공회대학교", "office.skhu.ac.kr");
            School school21 = new School("성균관대학교", "g.skku.edu");
            School school22 = new School("성균관대학교", "skku.edu");
            School school23 = new School("세종대학교", "sju.ac.kr");
            School school24 = new School("숭실대학교", "soongsil.ac.kr");
            School school25 = new School("연세대학교", "o365.yonsei.ac.kr");
            School school26 = new School("연세대학교", "yonsei.ac.kr");
            School school27 = new School("중앙대학교", "cau.ac.kr");
            School school28 = new School("총신대학교", "chongshin.ac.kr");
            School school29 = new School("추계예술대학교", "o365.chugye.ac.kr");
            School school30 = new School("한국외국어대학교", "hufs.ac.kr");
            School school31 = new School("한국체육대학교", "knsu.ac.kr");
            School school32 = new School("한성대학교", "hansung.ac.kr");
            School school33 = new School("한양대학교", "hanyang.ac.kr");
            School school34 = new School("홍익대학교", "g.hongik.ac.kr");
            School school35 = new School("홍익대학교", "mail.hongik.ac.kr");
            School school36 = new School("서울교육대학교", "student.snue.ac.kr");
            School school37 = new School("한국과학기술원", "kaist.ac.kr");
            School school38 = new School("한국예술종합학교", "karts.ac.kr");
            School school39 = new School("육군사관학교", "kma.ac.kr");
            School school40 = new School("덕성여자대학교", "duksung.ac.kr");
            School school41 = new School("동덕여자대학교", "dongduk.ac.kr");
            School school42 = new School("서울여자대학교", "swu.ac.kr");
            School school43 = new School("성신여자대학교", "sungshin.ac.kr");
            School school44 = new School("숙명여자대학교", "sm.ac.kr");
            School school45 = new School("숙명여자대학교", "sookmyung.ac.kr");
            School school46 = new School("이화여자대학교", "ewhain.net");
            School school47 = new School("감리교신학대학교", "office.mtu.ac.kr");
            School school48 = new School("감리교신학대학교", "mtu.ac.kr");
            School school49 = new School("서울기독대학교", "scu.ac.kr");
            School school50 = new School("장로회신학대학교", "puts.ac.kr");
            School school51 = new School("한국성서대학교", "bible.ac.kr");
            School school52 = new School("서울한영대학교", "hytu.ac.kr");
            School school53 = new School("동양미래대학교", "tong.dongyang.ac.kr");
            School school54 = new School("동양미래대학교", "m365.dongyang.ac.kr");
            School school55 = new School("명지전문대학", "mjc.ac.kr");
            School school56 = new School("삼육보건대학교", "o365.shu.ac.kr");
            School school57 = new School("삼육보건대학교", "shu.ac.kr");
            School school58 = new School("서일대학교", "g.seoil.ac.kr");
            School school59 = new School("서일대학교", "office.seoil.ac.kr");
            School school60 = new School("인덕대학교", "office.induk.ac.kr");
            School school61 = new School("배화여자대학교", "baewha.ac.kr");
            School school62 = new School("서울여자간호대학교", "snjc.ac.kr");
            School school63 = new School("숭의여자대학교", "sewc.ac.kr");
            School school64 = new School("국제예술대학교", "kua.ac.kr");
            School school65 = new School("백석예술대학교", "bau.ac.kr");
            School school66 = new School("정화예술대학교", "jb.ac.kr");




            em.persist(school1); em.persist(school2); em.persist(school3); em.persist(school4); em.persist(school5);
            em.persist(school6); em.persist(school7); em.persist(school8); em.persist(school9); em.persist(school10);
            em.persist(school11); em.persist(school12); em.persist(school13); em.persist(school14); em.persist(school15);
            em.persist(school16); em.persist(school17); em.persist(school18); em.persist(school19); em.persist(school20);
            em.persist(school21); em.persist(school22); em.persist(school23); em.persist(school24); em.persist(school25);
            em.persist(school26); em.persist(school27); em.persist(school28); em.persist(school29); em.persist(school30);
            em.persist(school31); em.persist(school32); em.persist(school33); em.persist(school34); em.persist(school35);
            em.persist(school36); em.persist(school37); em.persist(school38); em.persist(school39); em.persist(school40);
            em.persist(school41); em.persist(school42); em.persist(school43); em.persist(school44); em.persist(school45);
            em.persist(school46); em.persist(school47); em.persist(school48); em.persist(school49); em.persist(school50);
            em.persist(school51); em.persist(school52); em.persist(school53); em.persist(school54); em.persist(school55);
            em.persist(school56); em.persist(school57); em.persist(school58); em.persist(school59); em.persist(school60);
            em.persist(school61); em.persist(school62); em.persist(school63); em.persist(school64); em.persist(school65);
            em.persist(school66);
            em.flush(); em.clear();

        }
    }
}
