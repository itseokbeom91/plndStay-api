package com.example.stay.accommodation.wellihilli.controller;

import com.example.stay.accommodation.wellihilli.service.BookingService;
import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller("wellihilli.BookingController")
@RequestMapping("/wellihilli/booking/*")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    CommonFunction commonFunction = new CommonFunction();

    // 예약 가능 여부 조회
    @GetMapping("checkAvailBooking")
    public void checkAvailBooking(String pyung, String sDate, String sleep, String roomCount, String roomType){
        bookingService.checkAvailBooking(pyung, sDate, sleep, roomCount, roomType);
    }

}
