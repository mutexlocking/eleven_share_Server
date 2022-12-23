package com.konkuk.eleveneleven.src.room.controller;

import com.konkuk.eleveneleven.config.BaseResponse;
import com.konkuk.eleveneleven.src.room.dto.MatchingDto;
import com.konkuk.eleveneleven.src.room.dto.MatchingYnDto;
import com.konkuk.eleveneleven.src.room.dto.RoomDto;
import com.konkuk.eleveneleven.src.room.request.MatchingRequest;
import com.konkuk.eleveneleven.src.room.request.RoomCodeRequest;
import com.konkuk.eleveneleven.src.room.service.RoomMatchingService;
import com.konkuk.eleveneleven.src.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final RoomMatchingService roomMatchingService;

    /** [방 생성 API] */
    @PostMapping("/room")
    public BaseResponse<RoomDto> createRoom(@RequestAttribute Long memberIdx){
        return new BaseResponse(roomService.createRoom(memberIdx));
    }

    /** [방 참여 API]*/
    /** 한번 나간사람은 어떻게 하지 ?  -> 한번 나간 사람도 다시 그 나간 방에 들어갈 수 있게 해줘야 함*/
    @PostMapping("/room/member")
    public BaseResponse<MatchingDto> participateRoom(@RequestAttribute Long memberIdx,
                                                     @Validated @RequestBody RoomCodeRequest roomCodeRequest){
        return new BaseResponse<>(roomService.createRoomMember(memberIdx, roomCodeRequest.getRoomCode()));
    }

    /** [방 나가기 API] : 방장이 나갈 경우 그 방 자체가 삭제되고 , 일반 사용자가 나가면 - 그냥 나가기 효과만 나온다 */
    @DeleteMapping("/room")
    public BaseResponse<MatchingDto> deleteRoomMember(@RequestAttribute Long memberIdx){
        return new BaseResponse<>(roomService.deleteRoomMember(memberIdx));
    }


    /** [방 배칭 신청 API] : 오직 방장에 한하여 , 매칭 신청을 할 수 있도록 */
    @PatchMapping("/room/status")
    public BaseResponse<MatchingDto> matchingRoom(@RequestAttribute Long memberIdx,
                                                  @Validated @RequestBody MatchingRequest matchingRequest) {
        return new BaseResponse<>(roomService.matching(memberIdx, matchingRequest.getRoomIdx()));
    }



}


