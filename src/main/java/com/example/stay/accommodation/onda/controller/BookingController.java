package com.example.stay.accommodation.onda.controller;

import com.example.stay.accommodation.onda.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Controller
@RequestMapping("/onda/booking/*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    /**
     * 예약 생성
     */
    @GetMapping("createBooking")
    public void createBooking(int intBookingID) {

        bookingService.createBookingInfo(intBookingID);
    }

    /**
     * 예약 취소
     */
    @GetMapping("cancelBooking")
    public void cancelBooking(int intBookingID) {

        bookingService.cancelBookingInfo(intBookingID);
    }

}
