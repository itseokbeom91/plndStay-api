package com.example.stay.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.Map;

@Service
public class MailService {

    @Autowired
    private  JavaMailSender javaMailSender;

    @Autowired
    private SpringTemplateEngine springTemplateEngine;

    public void sendEmail(String content){
        MimeMessage message = javaMailSender.createMimeMessage();

        try{
            message.setSubject("이메일 테스트중입니다~");
            message.setText(serContext(content), "UTF-8", "html");
            message.addRecipients(MimeMessage.RecipientType.TO, "woonbeom.lee@plnd.co.kr");
            message.setFrom("sender@condo24.com");
            javaMailSender.send(message);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String serContext(String content){

        Context context = new Context();
        context.setVariable("content", content);
        return springTemplateEngine.process("mail", context);
    }
}
