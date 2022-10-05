package com.konkuk.eleveneleven.src.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/univ")
public class UnivController {

    @ResponseBody
    @PostMapping("")
    public void univMajorAPI(@RequestParam("univ") String univ) throws IOException {


    }

}
