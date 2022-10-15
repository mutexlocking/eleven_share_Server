package com.konkuk.eleveneleven.src.room.controller;

import com.konkuk.eleveneleven.config.BaseResponse;
import com.konkuk.eleveneleven.src.room.dto.MemberDto;
import com.konkuk.eleveneleven.src.room.dto.RoomDto;
import com.konkuk.eleveneleven.src.room.dto.RoomMemberDto;
import com.konkuk.eleveneleven.src.room.request.RoomCodeRequest;
import com.konkuk.eleveneleven.src.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping("/room")
    public BaseResponse<RoomDto> createRoom(@RequestAttribute Long kakaoId){
        return new BaseResponse(roomService.createRoom(kakaoId));
    }

    /**한번 나간사람은 어떻게 하지 ?  */
    @PostMapping("/room/member")
    public BaseResponse<List<MemberDto>> participateRoom(@RequestAttribute Long kakaoId, @Validated @RequestBody RoomCodeRequest roomCodeRequest){
        return new BaseResponse<>(roomService.createRoomMember(kakaoId, roomCodeRequest.getRoomCode()));
    }

    @PatchMapping("/room")
    public BaseResponse<List<MemberDto>> goOutTheRoom(@RequestAttribute Long kakaoId, @Validated @RequestBody RoomCodeRequest roomCodeRequest){
        return new BaseResponse<>(roomService.deleteRoom(kakaoId, roomCodeRequest.getRoomCode()));
    }
}


