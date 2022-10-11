package com.konkuk.eleveneleven.src.room.controller;

import com.konkuk.eleveneleven.config.BaseResponse;
import com.konkuk.eleveneleven.src.room.dto.RoomDto;
import com.konkuk.eleveneleven.src.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping("/room")
    public BaseResponse<RoomDto> createRoom(@RequestAttribute Long kakaoId){
        return new BaseResponse(roomService.createRoom(kakaoId));
    }
}


