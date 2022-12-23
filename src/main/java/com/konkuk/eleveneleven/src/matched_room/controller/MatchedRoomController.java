package com.konkuk.eleveneleven.src.matched_room.controller;

import com.konkuk.eleveneleven.config.BaseResponse;
import com.konkuk.eleveneleven.src.matched_room.dto.UrlDto;
import com.konkuk.eleveneleven.src.matched_room.dto.UrlRequest;
import com.konkuk.eleveneleven.src.matched_room.service.MatchedRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MatchedRoomController {
    private final MatchedRoomService matchedRoomService;


    /** [오픈채팅 url 공유 API] */
    @PatchMapping("/matched/room/url")
    public BaseResponse<UrlDto> patchUrl(@RequestAttribute Long memberIdx,
                                         @Validated @RequestBody UrlRequest urlRequest){
        return new BaseResponse<>(matchedRoomService.shareUrl(memberIdx,
                                                urlRequest.getMatchedRoomIdx(), urlRequest.getUrl()));

    }


    /** [공유된 오픈채팅 url 조회 API]*/
    @GetMapping("/matched/room/url/{matchedRoomIdx}")
    public BaseResponse<UrlDto> getUrl(@RequestAttribute Long memberIdx,
                                       @PathVariable Long matchedRoomIdx){
        return new BaseResponse<>(matchedRoomService.getUrl(memberIdx, matchedRoomIdx));
    }



}
