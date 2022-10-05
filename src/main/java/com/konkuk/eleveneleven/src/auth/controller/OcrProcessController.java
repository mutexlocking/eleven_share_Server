package com.konkuk.eleveneleven.src.auth.controller;

import com.konkuk.eleveneleven.config.BaseException;
import com.konkuk.eleveneleven.config.BaseResponse;
import com.konkuk.eleveneleven.src.auth.dto.OcrProcessRes;
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
    public BaseResponse<OcrProcessRes> postOcrProcessImage(@RequestParam("idCardImg") MultipartFile idCardImg, @RequestParam("name") String name, @RequestParam("univ") String univ) throws IOException {

        try {
            log.info("==== API POST /ocr ====");
            log.info("idCardImg = " + idCardImg);
            OcrProcessRes ocrProcessRes = ocrService.postOcrProcessImage(idCardImg, name, univ);

            return new BaseResponse<>(ocrProcessRes);
        } catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }

    }

}
