package com.konkuk.eleveneleven.src.member_room.service;

import com.konkuk.eleveneleven.src.member_room.repository.MemberRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberRoomService {
    private final MemberRoomRepository memberRoomRepository;

}
