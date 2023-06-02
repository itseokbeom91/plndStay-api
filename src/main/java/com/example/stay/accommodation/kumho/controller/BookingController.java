package com.example.stay.accommodation.kumho.controller;

import com.example.stay.accommodation.kumho.service.BookingService;
import com.example.stay.common.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller("kumho.BookingController")
@RequestMapping("/kumho/booking/*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    /**
     * 예약 생성
     */
    @GetMapping("createBooking")
    @ResponseBody
    public ResponseResult createBooking(int intBookingID, HttpServletRequest httpServletRequest) {

        ResponseResult responseResult = bookingService.createBooking(intBookingID, httpServletRequest);
        return responseResult;
    }

    /**
     * 예약 취소
     */
    @GetMapping("cancelBooking")
    @ResponseBody
    public ResponseResult cancelBooking(int intBookingID, HttpServletRequest httpServletRequest) {

        ResponseResult responseResult = bookingService.cancelBooking(intBookingID, httpServletRequest);
        return responseResult;
    }

    /**
     * 예약 현황 조회
     */
    @GetMapping("getReservationStatus")
    @ResponseBody
    public ResponseResult getReservationStatus(int intBookingID, HttpServletRequest httpServletRequest) {

        ResponseResult responseResult = bookingService.getReservationStatus(intBookingID, httpServletRequest);
        return responseResult;
    }

    /**
     * 예약 대사자료 조회
     */
    @GetMapping("getReservations")
    @ResponseBody
    public ResponseResult getReservations(String fr_date, String to_date, HttpServletRequest httpServletRequest){
        ResponseResult responseResult = bookingService.getReservations(fr_date, to_date, httpServletRequest);
        return responseResult;
    }

    /**
     * 잔여 객실 수 조회
     */
    @GetMapping("getRemainCountList")
    @ResponseBody
    public ResponseResult getRemainCountList(String fr_date, String to_date, String area, String room_type, HttpServletRequest httpServletRequest){
                ResponseResult responseResult = bookingService.getRemainCountList(fr_date, to_date, area, room_type, httpServletRequest);
        return responseResult;
    }

//    @GetMapping("getRemainCount")
//    @ResponseBody
//    public int getRemainCount(String fr_date, String to_date, String area, String room_type){
//        int cnt = bookingService.getRemainCount(fr_date, to_date, area, room_type);
//        return cnt;
//    }
}
