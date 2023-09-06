package com.example.stay.accommodation.resom.controller;

import com.example.stay.accommodation.resom.service.BookingService;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller("resom.BookingController")
@RequestMapping("/resom/booking/*")
public class BookingController {

    @Autowired
    BookingService bookingService = new BookingService();


    @GetMapping("/getPackageList")
    @ResponseBody
    public String getPackageList(String dataType) {
        return  bookingService.getPackageList(dataType);
    }

    @GetMapping("/getStoreList")
    @ResponseBody
    public String getStoreList(String dataType){
        return  bookingService.getStoreList(dataType);
    }

    @GetMapping("/getPackageInfo")
    @ResponseBody
    public String getPackageInfo(String dataType, String pkgNo) {
        return bookingService.getPackageInfo(dataType, pkgNo);
    }

    @GetMapping("/getPackageStock1")
    @ResponseBody
    public String getPackageStatus(String dataType, String pkgNo, String storeCd, String sDate) {
        return bookingService.getPackageStatus(dataType, pkgNo, storeCd, sDate);
    }

    @GetMapping("/getPackageStock2")
    @ResponseBody
    public String getPackageStatus(String dataType, String pkgNo, String storeCd, String sDate, String rmTypeCd) {
        return bookingService.getPackageStatus(dataType, pkgNo, storeCd, sDate, rmTypeCd);
    }

    @GetMapping("/getPackageStock3")
    @ResponseBody
    public String getPackageStatusMonth(String dataType, String pkgNo, String storeCd, String rmTypeCd, String sDate, String nights) {
        return bookingService.getPackageStatus(dataType, pkgNo, storeCd, rmTypeCd, sDate, nights);
    }

    @GetMapping("/getPackagePrice1")
    @ResponseBody
    public String getPackageAmount(String dataType, String pkgNo, String storeCd, String sDate) {
        return bookingService.getPackageAmount(dataType, pkgNo, storeCd, sDate);
    }

    @GetMapping("/getPackagePrice2")
    @ResponseBody
    public String getPackageAmount(String dataType, String pkgNo, String storeCd, String sDate, String rmTypeCd) {
        return bookingService.getPackageAmount(dataType, pkgNo, storeCd, sDate, rmTypeCd);
    }

    @GetMapping("/getPackagePrice3")
    @ResponseBody
    public String getPackageAmount(String dataType, String pkgNo, String storeCd, String sDate, String rmTypeCd, String nights) {
        return bookingService.getPackageAmount(dataType, pkgNo, storeCd, sDate, rmTypeCd, nights);
    }

    @GetMapping("/cancelBooking")
    @ResponseBody
    public String cancelBooking(String dataType, String bookingIdx) throws ParseException {
        return bookingService.cancelBooking(dataType, bookingIdx);
    }

    @GetMapping("/updateBookingInfo")
    @ResponseBody
    public String updateGuest(String dataType, String bookingIdx, String mpNo, String guestNm) throws ParseException {
        return bookingService.updateGuest(dataType, bookingIdx, mpNo, guestNm);
    }

    @GetMapping("/getPackageBookingInfo")
    @ResponseBody
    public String getPackageBookingInfo(String dataType, String bookingIdx) {
        return bookingService.getPackageBookingInfo(dataType, bookingIdx);
    }

    //예약 조회
    @GetMapping("/getBookingList")
    @ResponseBody
    public String reservationList(String dataType, String stndDt) {
        return bookingService.reservationList(dataType, stndDt);
    }

    //연박 예약
    @GetMapping("/createBooking")
    @ResponseBody
    public String createBooking2(String dataType, String bookingIdx, HttpServletRequest httpServletRequest) {
        return bookingService.createBooking(dataType, bookingIdx, httpServletRequest);
    }

    @GetMapping("/insertAccommInfo")
    @ResponseBody
    public String insertRESOM(String dataType, HttpServletRequest httpServletRequest) {
        return bookingService.insertRESOM(dataType, httpServletRequest);
    }

    @GetMapping("/updateStock")
    @ResponseBody
    public String getStockAndInsert(String dataType, HttpServletRequest httpServletRequest) {
        return bookingService.getStockAndInsert(dataType, httpServletRequest);
    }
    @GetMapping("/test")
    @ResponseBody
    public String getPackageStatusMonth(String pkgNo, String storeCd, String sDate, String nights){
        return bookingService.getPackageStatusMonth("jsonp", pkgNo, storeCd, sDate, nights);
    }

}
