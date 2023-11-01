package com.example.stay.common.controller;

import com.example.stay.openMarket.common.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class HomeController {

    @Autowired private CommonService commonService;

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/rsvAuto")
    public void ll(String intRsvID, HttpServletRequest httpServletRequest){
        commonService.rsvAuto(intRsvID, httpServletRequest);
    }
}
