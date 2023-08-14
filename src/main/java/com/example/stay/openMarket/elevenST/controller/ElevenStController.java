package com.example.stay.openMarket.elevenST.controller;

import com.example.stay.openMarket.elevenST.service.ElevenStService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("elevenST.ElevenStController")
@RequestMapping("/11st/*")
public class ElevenStController {
    @Autowired
    ElevenStService elevenStService = new ElevenStService();

    @GetMapping("/getProductList")
    @ResponseBody
    public String getProductList(){
        return elevenStService.getProductList();
    }

    @GetMapping("/test")
    @ResponseBody
    public String test(String prdNo){
        return elevenStService.stopDisplay(prdNo);
    }

    @GetMapping("/regTest")
    @ResponseBody
    public String regTest(String accommID){
        return elevenStService.regProduct(accommID);
    }

    @GetMapping("/qnaTest")
    @ResponseBody
    public String qnaTest(){
        return elevenStService.getQnaList();
    }

    @GetMapping("/answerTest")
    @ResponseBody
    public String answerTest(String qnaNo, String prdNo, String answer) {
        return elevenStService.answerQna(qnaNo, prdNo, answer);
    }

    @GetMapping("/stopDisplay")
    @ResponseBody
    public String stopDisplay(String prdNo){
        return elevenStService.stopDisplay(prdNo);
    }

    @GetMapping("/getOrderList")
    @ResponseBody
    public String getOrderList(){
        return elevenStService.getOrderList();
    }

}
