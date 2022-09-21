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
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;

@Slf4j
@Service
public class OcrService {
//    public static void detectText() throws IOException {
//        // TODO(developer): Replace these variables before running the sample.
//        String filePath = "C:/Users/Administrator/Desktop/test.jpg";
//        detectText(filePath);
//    }

    /** Detects text in the specified image.*/
    public void detectText(String filePath) throws IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();

//        // Base64 처리
//        String base64Image = filePath.split(",")[1];
//        byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);
//        BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));

        ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

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
                    System.out.format("Error: %s%n", res.getError().getMessage());
                    return;
                }

                // For full list of available annotations, see http://g.co/cloud/vision/docs
                for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
                    System.out.format("Text: %s%n", annotation.getDescription());
//                    System.out.format("Position : %s%n", annotation.getBoundingPoly());
                }
            }
        }
    }
}
