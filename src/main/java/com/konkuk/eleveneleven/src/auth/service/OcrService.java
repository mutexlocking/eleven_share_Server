package com.konkuk.eleveneleven.src.auth.service;


import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.konkuk.eleveneleven.common.enums.Status;
import com.konkuk.eleveneleven.config.BaseException;
import com.konkuk.eleveneleven.config.BaseResponseStatus;
import com.konkuk.eleveneleven.src.auth.dto.OcrProcessRes;
import com.konkuk.eleveneleven.src.school.School;
import com.konkuk.eleveneleven.src.school.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;

import static com.google.common.io.Files.getFileExtension;

@Slf4j
@Service
@RequiredArgsConstructor
public class OcrService {
    
    private final AwsS3Service awsS3Service;
    private final SchoolRepository schoolRepository;


    public OcrProcessRes postOcrProcessImage(MultipartFile idCardImg, String name, String univ) throws BaseException, IOException {

        String major = "";
        String student_num = "";

        System.out.println("idCardImg.getInputStream().toString() = " + idCardImg.getInputStream());
        System.out.println("idCardImg.getOriginalFilename() = " + idCardImg.getOriginalFilename());
        System.out.println("idCardImg = " + idCardImg);
        System.out.println("name = " + name);
        System.out.println("univ = " + univ);
        System.out.println("univ = " + univ.replaceAll("\n", ""));
        System.out.println("univ.equal  = " + univ.replaceAll("\n", "").equals("건국대학교"));
        
        
        /** 1. 학교 정보를 받음 */
        // DB에 저장되어 있는 각 학교별 학번 정규식 가져옴
        log.info("univ : " + univ);
        List<School> schoolInfo = schoolRepository.findByNameAndStatus(univ.replaceAll("\n", ""), Status.ACTIVE);
        System.out.println("schoolInfo = " + schoolInfo.get(0).getName());
        System.out.println("schoolInfo.isEmpty() = " + schoolInfo.isEmpty());
        
        /** 1_ex.1) DB에 저장되어있는 학교가 아닐 때 */
        if(schoolInfo.isEmpty()){
            log.info("서비스에서 제공하는 학교가 아닙니다.");
            throw new BaseException(BaseResponseStatus.INVALID_UNIV);
        }

        /** 2. 학생증 사진으로 부터 단어 정보 추출 */
        String[] words = detectText(idCardImg.getInputStream());

        /** 3. words 정보에서 학과, 학번 정보 추출
         * -> 유효한 학생증인지 검사 방법 : 학교 일치 여부 및 학교 학번 일치 여부
         * */
        // 유효힌 학생증인지 검사
        boolean isIdMatch = false;

        // 학번 조회 정규 표현식
        String studentIdRegex = schoolInfo.get(0).getIdRegex(); // 대학별 학번 정규 표현식
        Pattern studentIdPattern = Pattern.compile(studentIdRegex);

        // 학과 조회 정규 표현식
        String majorRegex = "^.*(부|과|학|전공)$";
        String majorExtraRegex = "^.*(부|과|학|전공)\\(.*\\)$"; // ~~학과(부연설명) 형태의 정규 표현식
        Pattern majorPattern = Pattern.compile(majorRegex);
        Pattern majorExtraPattern = Pattern.compile(majorExtraRegex);

        // 대학과 단과대 이름을 구분하기 위한 정규표현식
        String univRegex = "^.*(대학)$";
        Pattern univPattern = Pattern.compile(univRegex);

        // 대학원 여부 정규 표현식
        String masterRegex = "^.*(대학원).*$";
        String masterSecondRegex = "^.*(석사).*$";
        Pattern masterPattern = Pattern.compile(masterRegex);
        Pattern masterSecondPattern = Pattern.compile(masterSecondRegex);

        /** 3-0. 모든 word 순회 */
        for(String word : words){
            /** 3-1. 대학원생 판단 */
            Matcher masterMatcher = masterPattern.matcher(word);
            Matcher masterSecondMatcher = masterSecondPattern.matcher(word);
            if( masterMatcher.matches() || masterSecondMatcher.matches()){
                log.info("사용자 "+name+" 은 대학원생으로 판단. 사용이 불가합니다.");
                throw new BaseException(BaseResponseStatus.INVALID_USER);
            }

            /** 3-2. 학번 추출 */
            Matcher studentIdMatcher = studentIdPattern.matcher(word);
            if(studentIdMatcher.matches()){
                isIdMatch = true;

                // 학번 자르기 -> 17학번 형식으로 저장
                String[] gradePosition = schoolInfo.get(0).getGradePosition().split("-");
                student_num = word.substring(Integer.parseInt(gradePosition[0]),Integer.parseInt(gradePosition[1])+1);
            }


            /** 3-3. 전공 추출 */
            Matcher majorMatcher = majorPattern.matcher(word);
            Matcher majorExtraMatcher = majorExtraPattern.matcher(word);
            Matcher univExtraMatcher = univPattern.matcher(word);
            if( ( majorMatcher.matches() || majorExtraMatcher.matches() ) && !univExtraMatcher.matches() && !word.equals(name)){
                major = word;
            }
        }

        /** 4. 유효한 학생증인지 검사 후 DTO 생성
         * -> 유효한 학생증인지 검사 방법 : 학교 학번 일치 여부
         * */
        if(isIdMatch == false){
            throw new BaseException(BaseResponseStatus.INVALID_ID_CARD);
        }

        /** 5. 학생증 사진을 S3에 저장 후 URL 화 */
        // 1. 파일 나눠서 저장
        // 2. 파일 이름을 지정

        String fileName = (univ+"_"+name+"("+student_num+")"+".").concat(getFileExtension(idCardImg.getOriginalFilename()));

        String photoUrl = awsS3Service.uploadFile(idCardImg,fileName,univ);
        log.info("S3 photoURL : " + photoUrl);

        return OcrProcessRes.builder()
                .univ(univ)
                .major(major)
                .student_num(student_num)
                .build();


    }




    /** Detects text in the specified image.*/
    public String[] detectText(InputStream inputStream) throws IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();
        String[] words = new String[]{};

        ByteString imgBytes = ByteString.readFrom(inputStream);

        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        /** Initialize client that will be used to send requests. This client only needs to be created
         * once, and can be reused for multiple requests. After completing all of your requests, call
         * the "close" method on the client to safely clean up any remaining background resources.*/
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    log.error(res.getError().getMessage());
                    if(res.getError().getCode() == 3){
                        throw new BaseException(BaseResponseStatus.INVALID_IMG_FORMAT);
                    } else{
                        throw new BaseException(BaseResponseStatus.DETECT_TEXT_FAIL);
                    }
                }

                log.debug("res.getTextAnnotationsList().get(0) = " + res.getTextAnnotationsList().get(0).getDescription());
                String description = res.getTextAnnotationsList().get(0).getDescription();

                words = description.split("\\n");
            }
        } catch(RuntimeException e){
            throw new BaseException(BaseResponseStatus.DETECT_TEXT_FAIL);
        }

        return words;
    }

}
