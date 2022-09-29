package com.konkuk.eleveneleven.src.school;

import com.konkuk.eleveneleven.common.basic.BasicEntity;
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
}
