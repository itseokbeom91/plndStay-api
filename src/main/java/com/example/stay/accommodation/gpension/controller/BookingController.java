package com.example.stay.accommodation.gpension.controller;

import com.example.stay.accommodation.gpension.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public String createBooking(String BookingIdx) {
        return bookingService.createBooking(BookingIdx);
    }
    @GetMapping("/checkBooking")
    @ResponseBody
    public String confirmBooking(String BookingIdx) {
        return bookingService.confirmBooking(BookingIdx);
    }
    @GetMapping("/cancelBooking")
    @ResponseBody
    public String cancelBooking(String BookingIdx) {
        return bookingService.cancelBooking(BookingIdx);
    }
    @GetMapping("/searchRoom")
    @ResponseBody
    public String searchRoom(String roomId, String startDate, String daytype) {
        return bookingService.searchRoom(roomId, startDate, daytype);
    }
    @GetMapping("/searchOrder")
    @ResponseBody
    public String searchOrder(String BookingIdx) {
        return bookingService.searchOrder(BookingIdx);
    }
}
