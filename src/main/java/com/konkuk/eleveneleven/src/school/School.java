package com.konkuk.eleveneleven.src.school;

import com.konkuk.eleveneleven.common.basic.BasicEntity;
import com.konkuk.eleveneleven.common.enums.Status;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@Table(name = "school")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class School extends BasicEntity{

    @Id @GeneratedValue
    @Column(name = "school_idx")
    private Long idx;

    private String name;

    @Column(name = "email_domain")
    private String emailDomain;

    @Column(name = "id_regex")
    private String idRegex;

    @Column(name = "grade_position")
    private String gradePosition;

    /** [생성자] */
    public School(String name, String emailDomain, String idRegex, String gradePosition){
        this.setStatus(Status.ACTIVE);
        this.name = name;
        this.emailDomain = emailDomain;
        this.idRegex = idRegex;
        this.gradePosition = gradePosition;
    }


}
