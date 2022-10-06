package com.konkuk.eleveneleven.src.auth.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.konkuk.eleveneleven.config.BaseException;
import com.konkuk.eleveneleven.config.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsS3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;


    // 파일 하나 넣을 때
    public String uploadFile(MultipartFile oneMultipartFile, String fileName, String univ) throws BaseException {
        String photoUrl;

        log.debug("파일 이름: {}", oneMultipartFile.getOriginalFilename());
//        String OnefileName = createFileName(oneMultipartFile.getOriginalFilename());
        String OnefileName = fileName;
        log.debug("변환된 파일 이름: {}", OnefileName);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(oneMultipartFile.getSize());
        objectMetadata.setContentType(oneMultipartFile.getContentType());
        log.debug("content type: {}", objectMetadata.getContentType());

        try (InputStream inputStream = oneMultipartFile.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucket+'/'+univ, OnefileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException exception) {
            throw new BaseException(BaseResponseStatus.S3UPLOAD_ERROR);
        }

        photoUrl = amazonS3.getUrl(bucket, OnefileName).toString();

        return photoUrl;
    }

    public void deleteFile(String fileName) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }

    private String createFileName(String fileName) { // 먼저 파일 업로드 시, 파일명을 난수화하기 위해 random으로 돌립니다.
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) { // file 형식이 잘못된 경우를 확인하기 위해 만들어진 로직이며, 파일 타입과 상관없이 업로드할 수 있게 하기 위해 .의 존재 유무만 판단하였습니다.
        try {
            // filename
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일(" + fileName + ") 입니다.");
        }
    }
}