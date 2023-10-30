package com.example.stay.openMarket.gmarket.controller;

import com.example.stay.openMarket.gmarket.service.GmkCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/gmk/customer/*")
public class GmkCustomerController {

    @Autowired
    private GmkCustomerService gmkCustomerService;

    /**
     * 판매자 문의 목록 조회
     */
    @GetMapping("/getCustomerQList")
    @ResponseBody
    public String getCustomerQList(String dataType, String startDate, String endDate, HttpServletRequest httpServletRequest){
        return gmkCustomerService.getCustomerQList(dataType, startDate, endDate, httpServletRequest);
    }

    /**
     * 판매자 문의 답변
     */
    @GetMapping("/answerCustomerQ")
    @ResponseBody
    public String answerCustomerQ(String dataType, int intCSID, HttpServletRequest httpServletRequest){
        return gmkCustomerService.answerCustomerQ(dataType, intCSID, httpServletRequest);
    }

    /**
     * 긴급알리미 조회
     */
    @GetMapping("/getEmergAlarm")
    @ResponseBody
    public String getEmergAlarm(String dataType, String startDate, String endDate, HttpServletRequest httpServletRequest){
        return gmkCustomerService.getEmergAlarm(dataType, startDate, endDate, httpServletRequest);
    }

    /**
     * 긴급알리미 답변
     */
    @GetMapping("/answerEmergAlarm")
    @ResponseBody
    public String answerEmergAlarm(String dataType, int intCSID, HttpServletRequest httpServletRequest){
        return gmkCustomerService.answerEmergAlarm(dataType, intCSID, httpServletRequest);
    }

    /**
     * ESM공지사항 조회
     */
    @GetMapping("/getESMNoticeList")
    @ResponseBody
    public String getESMNoticeList(String dataType, String startDate, String endDate, HttpServletRequest httpServletRequest){
        return gmkCustomerService.getESMNoticeList(dataType, startDate, endDate, httpServletRequest);
    }


}
