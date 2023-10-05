package com.example.stay.openMarket.ebay.controller;

import com.example.stay.openMarket.ebay.service.AccommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller("ebay.AccommController")
@RequestMapping("/ebay/accomm/*")
public class AccommController {
    @Autowired
    private AccommService accommService;

    @GetMapping("/getAccommList")
    @ResponseBody
    public String getAccommList(HttpServletRequest httpServletRequest){
        return accommService.getAccommList(httpServletRequest);
    }




}
