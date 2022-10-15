package com.konkuk.eleveneleven.src.room.controller;

import com.konkuk.eleveneleven.src.room.service.RoomService;
import com.konkuk.eleveneleven.src.room.vo.WaitingRoom;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/room")
public class RoomApiController {

    private final RoomService roomService;

    //채팅방 목록 조회
    @GetMapping(value = "/list")
    public List<WaitingRoom> rooms(){

        log.info("# All Chat Rooms");

        List<WaitingRoom> allRooms = roomService.findAllRooms();

        return allRooms;
    }

    //채팅방 개설
    @PostMapping(value = "")
    public String create(@RequestParam String name, RedirectAttributes redirectAttributes){

        log.info("# Create Waiting Room , name: " + name);
        redirectAttributes.addFlashAttribute("roomName", roomService.createWaitingRoom(name));
        return "redirect:/waiting/rooms";
    }

    //채팅방 조회
    @GetMapping("")
    public WaitingRoom getRoom(String roomId){

        log.info("# Get Waiting Room, roomID : " + roomId);

        WaitingRoom roomById = roomService.findRoomById(roomId);
        return roomById;
    }


}
