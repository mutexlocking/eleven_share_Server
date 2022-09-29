package com.konkuk.eleveneleven.src.school.service;

import com.konkuk.eleveneleven.src.school.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SchoolService {

    private final SchoolRepository schoolRepository;
}
