package com.konkuk.eleveneleven.src.auth.controller;

import com.konkuk.eleveneleven.config.BaseException;
import com.konkuk.eleveneleven.src.auth.service.OcrService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/ocr")
public class OcrProcessController {

    private final OcrService ocrService;

    @ResponseBody
    @PostMapping("")
    public void postOcrProcessImage(@RequestParam("multipartFile") MultipartFile multipartFile) throws IOException {

        try {
            System.out.println("multipartFile = " + multipartFile);
            ocrService.postOcrProcessImage(multipartFile);
        } catch (BaseException baseException){

        }

    }


}
