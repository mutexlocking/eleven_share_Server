package com.konkuk.eleveneleven.src.room_member.controller;

import com.konkuk.eleveneleven.src.room.service.RoomService;
import com.konkuk.eleveneleven.src.room.vo.WaitingRoom;
import com.konkuk.eleveneleven.src.room_member.service.RoomMemberService;
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
@RequestMapping(value = "matched/room")
public class RoomMemberController {

}
