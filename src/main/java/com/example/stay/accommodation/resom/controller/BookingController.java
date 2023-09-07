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
    public String getPackageList(@RequestParam(required = false, defaultValue="jsonp") String dataType) {
        return  bookingService.getPackageList(dataType);
    }

    @GetMapping("/getStoreList")
    @ResponseBody
    public String getStoreList(@RequestParam(required = false, defaultValue="jsonp") String dataType){
        return  bookingService.getStoreList(dataType);
    }

    @GetMapping("/getPackageInfo")
    @ResponseBody
    public String getPackageInfo(@RequestParam(required = false, defaultValue="jsonp") String dataType, String pkgNo) {
        return bookingService.getPackageInfo(dataType, pkgNo);
    }

    @GetMapping("/getPackageStock1")
    @ResponseBody
    public String getPackageStatus(@RequestParam(required = false, defaultValue="jsonp") String dataType, String pkgNo, String storeCd, String sDate) {
        return bookingService.getPackageStatus(dataType, pkgNo, storeCd, sDate);
    }

    @GetMapping("/getPackageStock2")
    @ResponseBody
    public String getPackageStatus(@RequestParam(required = false, defaultValue="jsonp") String dataType, String pkgNo, String storeCd, String sDate, String rmTypeCd) {
        return bookingService.getPackageStatus(dataType, pkgNo, storeCd, sDate, rmTypeCd);
    }

    @GetMapping("/getPackageStock3")
    @ResponseBody
    public String getPackageStatusMonth(@RequestParam(required = false, defaultValue="jsonp") String dataType, String pkgNo, String storeCd, String rmTypeCd, String sDate, String nights) {
        return bookingService.getPackageStatus(dataType, pkgNo, storeCd, rmTypeCd, sDate, nights);
    }

    @GetMapping("/getPackagePrice1")
    @ResponseBody
    public String getPackageAmount(@RequestParam(required = false, defaultValue="jsonp") String dataType, String pkgNo, String storeCd, String sDate) {
        return bookingService.getPackageAmount(dataType, pkgNo, storeCd, sDate);
    }

    @GetMapping("/getPackagePrice2")
    @ResponseBody
    public String getPackageAmount(@RequestParam(required = false, defaultValue="jsonp") String dataType, String pkgNo, String storeCd, String sDate, String rmTypeCd) {
        return bookingService.getPackageAmount(dataType, pkgNo, storeCd, sDate, rmTypeCd);
    }

    @GetMapping("/getPackagePrice3")
    @ResponseBody
    public String getPackageAmount(@RequestParam(required = false, defaultValue="jsonp") String dataType, String pkgNo, String storeCd, String sDate, String rmTypeCd, String nights) {
        return bookingService.getPackageAmount(dataType, pkgNo, storeCd, sDate, rmTypeCd, nights);
    }

    @GetMapping("/cancelBooking")
    @ResponseBody
    public String cancelBooking(@RequestParam(required = false, defaultValue="jsonp") String dataType, String intRsvID) throws ParseException {
        return bookingService.cancelBooking(dataType, intRsvID);
    }

    @GetMapping("/updateBooking")
    @ResponseBody
    public String updateGuest(@RequestParam(required = false, defaultValue="jsonp") String dataType, String intRsvID, String mpNo, String guestNm) throws ParseException {
        return bookingService.updateGuest(dataType, intRsvID, mpNo, guestNm);
    }

    @GetMapping("/getPackageBookingInfo")
    @ResponseBody
    public String getPackageBookingInfo(@RequestParam(required = false, defaultValue="jsonp") String dataType, String intRsvID) {
        return bookingService.getPackageBookingInfo(dataType, intRsvID);
    }

    //예약 조회
    @GetMapping("/getBookingList")
    @ResponseBody
    public String reservationList(@RequestParam(required = false, defaultValue="jsonp") String dataType, String stndDt) {
        return bookingService.reservationList(dataType, stndDt);
    }

    //연박 예약
    @GetMapping("/createBooking")
    @ResponseBody
    public String createBooking2(@RequestParam(required = false, defaultValue="jsonp") String dataType, String intRsvID, HttpServletRequest httpServletRequest) {
        return bookingService.createBooking(dataType, intRsvID, httpServletRequest);
    }

    @GetMapping("/insertAccommInfo")
    @ResponseBody
    public String insertRESOM(@RequestParam(required = false, defaultValue="jsonp") String dataType, HttpServletRequest httpServletRequest) {
        return bookingService.insertRESOM(dataType, httpServletRequest);
    }

    @GetMapping("/updateStock")
    @ResponseBody
    public String getStockAndInsert(@RequestParam(required = false, defaultValue="jsonp") String dataType, HttpServletRequest httpServletRequest) {
        return bookingService.getStockAndInsert(dataType, httpServletRequest);
    }
    @GetMapping("/test")
    @ResponseBody
    public String getPackageStatusMonth(String pkgNo, String storeCd, String sDate, String nights){
        return bookingService.getPackageStatusMonth("jsonp", pkgNo, storeCd, sDate, nights);
    }

}
