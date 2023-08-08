package com.example.stay.openMarket.gmarket.controller;

import com.example.stay.openMarket.gmarket.hmac.HmacGenerater;
import com.example.stay.openMarket.gmarket.service.GmkAccommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/gmk/accomm/*")
public class GmkAccommController {

    @Autowired
    private GmkAccommService gmkAccommService;

    /**
     * 숙박상품 생성
     */
    @GetMapping("/createAccomm")
    public String createAccomm(String dataType, int intAID, HttpServletRequest httpServletRequest){
        return gmkAccommService.createAccomm(dataType, intAID, httpServletRequest);
    }

    @GetMapping("/testHmac")
    public void testHmac(){
        HmacGenerater.generate("");
    }

}
