package com.example.stay.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.Map;

@Service
public class MailService {

    @Autowired
    private  JavaMailSender javaMailSender;


    public void sendEmail(String content, String rsvStatus){
        MimeMessage message = javaMailSender.createMimeMessage();

        try{
            if(!rsvStatus.equals("")){
                if(rsvStatus.equals("0")){
                    message.setSubject("예약과입니다. [예약접수]");
                } else if (rsvStatus.equals("5") || rsvStatus.equals("14")) {
                    message.setSubject("예약과입니다. [예약취소]");
                }
            }

            message.setText(serContext(content, rsvStatus), "UTF-8", "html");
            message.addRecipients(MimeMessage.RecipientType.TO, "woonbeom.lee@plnd.co.kr");
            message.setFrom("sender@condo24.com");
            javaMailSender.send(message);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String serContext(String content, String rsvStatus){

//        Context context = new Context();
//        context.setVariable("content", content);
//        context.setVariable("rsvStatus", rsvStatus);
//        return springTemplateEngine.process("mail", context);
        return "";
    }
}
