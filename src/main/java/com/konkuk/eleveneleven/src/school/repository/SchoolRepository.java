package com.konkuk.eleveneleven.src.school.repository;

import com.konkuk.eleveneleven.common.enums.Status;
import com.konkuk.eleveneleven.src.school.School;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SchoolRepository extends JpaRepository<School, Long> {

    List<School> findByNameAndStatus(String name, Status status);


    boolean existsByEmailDomain(String emailDomain);

}
