package com.konkuk.eleveneleven.src.room_member.service;

import com.konkuk.eleveneleven.src.room_member.repository.RoomMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomMemberService {
    private final RoomMemberRepository roomMemberRepository;

}
