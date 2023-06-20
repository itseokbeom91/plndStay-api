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
    public String createBooking(int intBookingID, HttpServletRequest httpServletRequest) {
        return bookingService.createBooking(intBookingID, httpServletRequest);
    }

    /**
     * 예약 취소
     */
    @GetMapping("cancelBooking")
    @ResponseBody
    public String cancelBooking(int intBookingID, HttpServletRequest httpServletRequest) {
        return bookingService.cancelBooking(intBookingID, httpServletRequest);
    }

    /**
     * 예약 현황 조회
     */
    @GetMapping("getReservationStatus")
    @ResponseBody
    public String getReservationStatus(int intBookingID, HttpServletRequest httpServletRequest) {
        return bookingService.getReservationStatus(intBookingID, httpServletRequest);
    }

    /**
     * 예약 대사자료 조회
     */
    @GetMapping("getReservations")
    @ResponseBody
    public String getReservations(String fr_date, String to_date, HttpServletRequest httpServletRequest){
        return bookingService.getReservations(fr_date, to_date, httpServletRequest);
    }

    /**
     * 재고 등록 및 수정
     */
    @GetMapping("updateGoods")
    @ResponseBody
    public String getRemainCountList(String fr_date, String to_date, String area, String room_type, HttpServletRequest httpServletRequest){
        return bookingService.updateGoods(fr_date, to_date, area, room_type, httpServletRequest);
    }

//    @GetMapping("getRemainCount")
//    @ResponseBody
//    public int getRemainCount(String fr_date, String to_date, String area, String room_type){
//        int cnt = bookingService.getRemainCount(fr_date, to_date, area, room_type);
//        return cnt;
//    }
}
