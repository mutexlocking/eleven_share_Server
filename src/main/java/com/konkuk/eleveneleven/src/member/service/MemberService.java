package com.konkuk.eleveneleven.src.member.service;

import com.konkuk.eleveneleven.src.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
}
