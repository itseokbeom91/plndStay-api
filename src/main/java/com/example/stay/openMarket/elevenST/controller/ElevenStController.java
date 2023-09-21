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

    @GetMapping("/stopDisplay")
    @ResponseBody
    public String test(String prdNo){
        return elevenStService.stopDisplay(prdNo);
    }

    @GetMapping("/getStockList")
    @ResponseBody
    public String getStockList(String prdNo){
        return elevenStService.getStockList(prdNo);
    }

    @GetMapping("/getOrderList")
    @ResponseBody
    public String getOrderList(){
        return elevenStService.getOrderList();
    }

    @GetMapping("/getOrderInfo")
    @ResponseBody
    public String getOrderInfo(String ordNo){
        return elevenStService.getOrderInfo(ordNo);
    }

    @GetMapping("/regAccomm")
    @ResponseBody
    public String regTest(String accommID, String bgnDay, String endDay){
        return elevenStService.regProduct(accommID,  bgnDay, endDay);
    }

    @GetMapping("/getQnaList")
    @ResponseBody
    public String qnaTest(){
        return elevenStService.getQnaList();
    }

    @GetMapping("/answerQna")
    @ResponseBody
    public String answerTest(String qnaNo, String prdNo, String answer) {
        return elevenStService.answerQna(qnaNo, prdNo, answer);
    }

    @GetMapping("/updateQuantity")
    @ResponseBody
    public String updateQuantity(String prdNo, String quantity){
        return elevenStService.updateStock(prdNo, quantity);
    }


}
