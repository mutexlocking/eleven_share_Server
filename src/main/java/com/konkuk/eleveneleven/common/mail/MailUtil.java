package com.konkuk.eleveneleven.common.mail;

import com.konkuk.eleveneleven.config.BaseException;
import com.konkuk.eleveneleven.config.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.naming.Context;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MailUtil {

    private final JavaMailSender emailSender;
    private String authCode; //인증 코드


    /** 랜덤 인증코드 생성 기능 */
    public void createCode(){
        authCode = UUID.randomUUID().toString().substring(0,6);
    }

    /** 메일 양식 설정 */
    public MimeMessage createEmailForm(String email) throws MessagingException, UnsupportedEncodingException {

        //인증 코드 생성
        createCode();

        String setForm = "kkt971229@gmaiil.com";    // 송신자 이메일 주소
        String toEmail = email;                    // 수신자 이메일
        String title = "[11시 11분] 회원가입 인증번호"; // 이메일 제목

        MimeMessage message = emailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, email); //보낼 이메일 설정
        message.setSubject(title);
        message.setFrom(setForm);
        message.setText("인증번호 : " + authCode, "utf-8");


        return message;
    }


    /** 실제 메일 전송 */
    public String sendEmail(String toEmail){

        MimeMessage emailForm;

        try{
            //메일전송에 필요한 정보 설정
            emailForm = createEmailForm(toEmail);
        } catch ( MessagingException|UnsupportedEncodingException e){
            throw new BaseException(BaseResponseStatus.FAIL_SEND_EMAIL, "인증 메일 전송에 실패하였습니다.");
        }

        //실제 메일 전송
        emailSender.send(emailForm);

        return authCode; //인증 코드 반환
    }



}
