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

    /**한번 나간사람은 어떻게 하지 ?  -> 한번 나간 사람도 다시 그 나간 방에 들어갈 수 있게 해줘야 함*/
    @PostMapping("/room/member")
    public BaseResponse<List<MemberDto>> participateRoom(@RequestAttribute Long kakaoId, @Validated @RequestBody RoomCodeRequest roomCodeRequest){
        return new BaseResponse<>(roomService.createRoomMember(kakaoId, roomCodeRequest.getRoomCode()));
    }

    //처음엔 roomCode를 받아서 그 roomCode에 속한 방에서 나가려고 했는데 , 어차피 Member와 RoomMember - ROOM은 모두 관계를 맺고 있으므로
    // roomCodd를 받지 않고 , 그 관계에서 떼어내면 자동적으로 - 그 Member가 속한 방에서 나가는게 된다
//    @PatchMapping("/room")
//    public BaseResponse<List<MemberDto>> goOutTheRoom(@RequestAttribute Long kakaoId){
//        return new BaseResponse<>(roomService.deleteRoom(kakaoId));
//    }

    @DeleteMapping("/room")
    public BaseResponse<List<MemberDto>> deleteRoomMember(@RequestAttribute Long kakaoId){
        return new BaseResponse<>(roomService.deleteRoomMember(kakaoId));
    }
}


