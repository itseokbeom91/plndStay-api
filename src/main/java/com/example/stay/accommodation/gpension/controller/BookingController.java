package com.example.stay.accommodation.gpension.controller;

import com.example.stay.accommodation.gpension.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("gpension.BookingController")
@RequestMapping("/gp/booking/*")
public class BookingController {
    @Autowired
    BookingService bookingService = new BookingService();

    /*
    1. 예약 정보 전송 (제휴사 -> 지펜션)
        join_room
    2. 결제 완료 - 무통장 입금의 입금 확인 시 (제휴사 -> 지펜션)
        confirm_room
    3. 예약 취소 (제휴사 -> 지펜션)
        cancel_room
    4. 객실 예약 가능 여부 확인 (제휴사 -> 지펜션)
        search_room
    5. 예약 상태 확인 (제휴사 -> 지펜션)
        search_order
     */

    @GetMapping("/createBooking")
    @ResponseBody
    public String createBooking(@RequestParam(required = false, defaultValue="jsonp") String dataType, int intRsvID) {
        return bookingService.createBooking(dataType, intRsvID);
    }
    @GetMapping("/checkBooking")
    @ResponseBody
    public String confirmBooking(@RequestParam(required = false, defaultValue="jsonp") String dataType, int intRsvID) {
        return bookingService.confirmBooking(dataType,intRsvID);
    }
    @GetMapping("/cancelBooking")
    @ResponseBody
    public String cancelBooking(@RequestParam(required = false, defaultValue="jsonp") String dataType, int intRsvID) {
        return bookingService.cancelBooking(dataType,intRsvID);
    }
    @GetMapping("/getRoomStock")
    @ResponseBody
    public String searchRoom(@RequestParam(required = false, defaultValue="jsonp") String dataType, String roomId, String startDate, String daytype) {
        return bookingService.searchRoom(dataType,roomId, startDate, daytype);
    }
    @GetMapping("/getBookingInfo")
    @ResponseBody
    public String searchOrder(@RequestParam(required = false, defaultValue="jsonp") String dataType, int intRsvID) {
        return bookingService.searchOrder(dataType,intRsvID);
    }
}
