package com.example.stay.common.util;

import com.example.stay.openMarket.common.mapper.CommonMapper;
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

    @Autowired
    private CommonMapper commonMapper;


    public void sendEmail(String content, String rsvStatus){
        MimeMessage message = javaMailSender.createMimeMessage();

        try{
            String subject = "";
            if(!rsvStatus.equals("")){
                if(rsvStatus.equals("0")){
                    subject = "예약과입니다. [예약접수]";
                    message.setSubject(subject);
                } else if (rsvStatus.equals("5") || rsvStatus.equals("14")) {
                    subject = "예약과입니다. [예약취소]";
                    message.setSubject(subject);
                }
            }

            message.setText(content, "UTF-8", "html");
            message.addRecipients(MimeMessage.RecipientType.TO, "woonbeom.lee@plnd.co.kr");
            message.setFrom("sender@condo24.com");
            javaMailSender.send(message);
//            commonMapper.insertSendHistory(intRsvID, "EMAIL", "200", subject, content, "212.142.23.23", "83");
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
