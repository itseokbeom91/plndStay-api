package com.example.stay.accommodation.kumho.controller;

import com.example.stay.accommodation.kumho.service.SpavisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/kumho/spavis/*")
public class SpavisController {

    @Autowired
    private SpavisService spavisService;

    // 쿠폰 사용여부 조회
    @GetMapping("checkCouponStatus")
    @ResponseBody
    public String checkCouponStatus(HttpServletRequest httpServletRequest, String couponNo){
        return spavisService.checkCouponStatus(httpServletRequest, couponNo);
    }
}
