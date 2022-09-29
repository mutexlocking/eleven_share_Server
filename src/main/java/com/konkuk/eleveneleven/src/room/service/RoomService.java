package com.konkuk.eleveneleven.src.room.service;

import com.konkuk.eleveneleven.src.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository repository;
}
