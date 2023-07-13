package com.example.stay.accommodation.kumho.controller;

import com.example.stay.accommodation.kumho.service.BookingService;
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
    public String createBooking(String dataType, int intBookingID, HttpServletRequest httpServletRequest) {
        return bookingService.createBooking(dataType, intBookingID, httpServletRequest);
    }

    /**
     * 예약 취소
     */
    @GetMapping("cancelBooking")
    @ResponseBody
    public String cancelBooking(String dataType, int intBookingID, HttpServletRequest httpServletRequest) {
        return bookingService.cancelBooking(dataType, intBookingID, httpServletRequest);
    }

    /**
     * 예약 현황 조회
     */
    @GetMapping("getReservationStatus")
    @ResponseBody
    public String getReservationStatus(String dataType, int intRsvID, HttpServletRequest httpServletRequest) {
        return bookingService.getReservationStatus(dataType, intRsvID, httpServletRequest);
    }

    /**
     * 예약 대사자료 조회
     */
    @GetMapping("getReservations")
    @ResponseBody
    public String getReservations(String dataType, String strFromDate, String strToDate, HttpServletRequest httpServletRequest){
        return bookingService.getReservations(dataType, strFromDate, strToDate, httpServletRequest);
    }

    /**
     * 재고 등록 및 수정
     */
    @GetMapping("updateGoods")
    @ResponseBody
    public String getRemainCountList(String dataType, String strFromDate, String strToDate, int intRmIdx, HttpServletRequest httpServletRequest){
        return bookingService.updateGoods(dataType, strFromDate, strToDate, intRmIdx, httpServletRequest);
    }

//    @GetMapping("getRemainCount")
//    @ResponseBody
//    public int getRemainCount(String fr_date, String to_date, String area, String room_type){
//        int cnt = bookingService.getRemainCount(fr_date, to_date, area, room_type);
//        return cnt;
//    }
}
