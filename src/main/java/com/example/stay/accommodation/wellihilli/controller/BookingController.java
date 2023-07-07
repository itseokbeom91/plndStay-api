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
import org.springframework.lang.Nullable;
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
//    @GetMapping("checkAvailBooking")
//    public void checkAvailBooking(String pyung, String sDate, String sleep, String roomCount, String roomType){
//        bookingService.checkAvailBooking(pyung, sDate, sleep, roomCount, roomType);
//    }

    // 재고 등록 및 수정
    @GetMapping("updateGoods")
    @ResponseBody
    public String updateGoods(String dataType, HttpServletRequest httpServletRequest, int intRmIdx, String startDate, String endDate){
        return bookingService.updateGoods(dataType, httpServletRequest, intRmIdx, startDate, endDate);
    }

    // 체크인 날짜에 해당되는 객실 수량 및 계산된 총 요금 조회
    @GetMapping("getTotalPrice")
    public void getTotalPrice(String pyung, String sDate, String eDate, String sleep, String roomCount, String roomType, String pkgCode){
        bookingService.getTotalPrice(pyung, sDate, eDate, sleep, roomCount, roomType, pkgCode);
    }

    // 1박 이상일경우 일자별 요금 데이터 조회
    @GetMapping("getDayPrice")
    public void getDayPrice(String pyung, String sDate, String eDate, String sleep, String roomCount, String roomType, String pkgCode){
        bookingService.getDayPrice(pyung, sDate, eDate, sleep, roomCount, roomType, pkgCode);
    }

    // 예약
    @GetMapping("createBooking")
    @ResponseBody
    public String createBooking(String dataType, int intBookingIdx, HttpServletRequest httpServletRequest){
        return bookingService.createBooking(dataType, intBookingIdx, httpServletRequest);
    }

    // 예약 취소
    @GetMapping("cancelBooking")
    @ResponseBody
    public String cancelBooking(String dataType, int intBookingIdx, HttpServletRequest httpServletRequest){
        return bookingService.cancelBooking(dataType, intBookingIdx, httpServletRequest);
    }

    // 예약 수정
    @GetMapping("modifyBooking")
    @ResponseBody
    public String modifyBooking(String dataType, int intBookingIdx, HttpServletRequest httpServletRequest){
        return bookingService.modifyBooking(dataType, intBookingIdx, httpServletRequest);
    }

    // 예약 상세 조회
    @GetMapping("checkBooking")
    @ResponseBody
    public String checkBooking(String dataType, int intBookingIdx, HttpServletRequest httpServletRequest){
        return bookingService.checkBooking(dataType, intBookingIdx, httpServletRequest);
    }

    // 예약 리스트 조회(체크인날짜 기준)
    // 예약번호, 예약자 이름으로 검색 가능
    @GetMapping("checkBookingList")
    @ResponseBody
    public String checkBookingList(String dataType, HttpServletRequest httpServletRequest,
                                   @Nullable String searchFlag, @Nullable String searchData, String sDate, String eDate,
                                   @Nullable String rsvFlag){
        return bookingService.checkBookingList(dataType, httpServletRequest, searchFlag, searchData,
                                                sDate, eDate, rsvFlag);
    }


}
