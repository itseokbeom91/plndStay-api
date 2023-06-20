package com.example.stay.accommodation.elysian_gangchon.controller;

import com.example.stay.accommodation.elysian_gangchon.service.BookingService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller("elysian_gangchon.BookingController")
@RequestMapping("/elysian_gangchon/booking/*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @RequestMapping("updateGoods")
    @ResponseBody
    public String updateGoods(HttpServletRequest httpServletRequest, String pcode, String pcode_seq, String sdate, String edate, String strRmtypeID){
        return bookingService.updateGoods(httpServletRequest, pcode, pcode_seq, sdate, edate, strRmtypeID);
    }
}
