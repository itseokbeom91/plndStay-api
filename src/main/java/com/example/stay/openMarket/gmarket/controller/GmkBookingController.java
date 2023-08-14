package com.example.stay.openMarket.gmarket.controller;

import com.example.stay.openMarket.gmarket.service.GmkBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/gmk/booking/*")
public class GmkBookingController {

    @Autowired
    private GmkBookingService gmkBookingService;

    // 결제 완료된 주문 데이터 조회 - 클레임(취소, 반품, 교환, 미수령신고) 주문은 조회 X
    // 31일 이내 기간만 조회 가능
    public String getBookingList(String dataType, String strDateFrom, String strDateTo, HttpServletRequest httpServletRequest){
        return gmkBookingService.getBookingList(dataType, strDateFrom, strDateTo, httpServletRequest);
    }
}
