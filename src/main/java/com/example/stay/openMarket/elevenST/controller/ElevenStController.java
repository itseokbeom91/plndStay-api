package com.example.stay.openMarket.elevenST.controller;

import com.example.stay.openMarket.elevenST.service.ElevenStService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/updateDisplay")
    @ResponseBody
    public String updateDisplay(int intAID, @RequestParam(value = "state", required = false) String state){
        return elevenStService.updateDisplay(intAID, state);
    }

    @GetMapping("/getStockList")
    @ResponseBody
    public String getStockList(int intAID){
        return elevenStService.getStockList(intAID);
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

    @GetMapping("/modProduct")
    @ResponseBody
    public String modProduct(String prdNo){
        return elevenStService.modProduct(prdNo);
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
    public String updateQuantity(int intAID, String quantity){
        return elevenStService.updateStock(intAID, quantity);
    }

    @GetMapping("/updatePrdDesc")
    @ResponseBody
    public String updatePrdDesc(@RequestParam(value = "dataType", required = false, defaultValue = "jsonp") String dataType, int intAID){
        return elevenStService.updatePrdDesc(dataType, intAID);
    }


}
