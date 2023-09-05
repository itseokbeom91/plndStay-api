package com.example.stay.accommodation.resom.controller;

import com.example.stay.accommodation.resom.service.BookingService;
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

    @GetMapping("/getPackageStatus1")
    @ResponseBody
    public String getPackageStatus(String dataType, String pkgNo, String storeCd, String sDate) {
        return bookingService.getPackageStatus(dataType, pkgNo, storeCd, sDate);
    }

    @GetMapping("/getPackageStatus2")
    @ResponseBody
    public String getPackageStatus(String dataType, String pkgNo, String storeCd, String sDate, String rmTypeCd) {
        return bookingService.getPackageStatus(dataType, pkgNo, storeCd, sDate, rmTypeCd);
    }

    @GetMapping("/getPackageStatus3")
    @ResponseBody
    public String getPackageStatusMonth(String dataType, String pkgNo, String storeCd, String rmTypeCd, String sDate, String nights) {
        return bookingService.getPackageStatus(dataType, pkgNo, storeCd, rmTypeCd, sDate, nights);
    }

    @GetMapping("/getPackageAmount1")
    @ResponseBody
    public String getPackageAmount(String dataType, String pkgNo, String storeCd, String sDate) {
        return bookingService.getPackageAmount(dataType, pkgNo, storeCd, sDate);
    }

    @GetMapping("/getPackageAmount2")
    @ResponseBody
    public String getPackageAmount(String dataType, String pkgNo, String storeCd, String sDate, String rmTypeCd) {
        return bookingService.getPackageAmount(dataType, pkgNo, storeCd, sDate, rmTypeCd);
    }

    @GetMapping("/getPackageAmount3")
    @ResponseBody
    public String getPackageAmount(String dataType, String pkgNo, String storeCd, String sDate, String rmTypeCd, String nights) {
        return bookingService.getPackageAmount(dataType, pkgNo, storeCd, sDate, rmTypeCd, nights);
    }

    @GetMapping("/cancelBooking")
    @ResponseBody
    public String cancelBooking(String dataType, String roomRsvNo, String pkgSaleSeq, String roomRsvSeq, String comRsvNo) {
        return bookingService.cancelBooking(dataType, roomRsvNo, pkgSaleSeq, roomRsvSeq, comRsvNo);
    }

    @GetMapping("/updateGuest")
    @ResponseBody
    public String updateGuest(String dataType, String roomRsvSeq, String pkgSaleSeq, String guestNm, String mpNo) {
        return bookingService.updateGuest(dataType, roomRsvSeq, pkgSaleSeq, guestNm, mpNo);
    }

    @GetMapping("/getPackageBookingInfo")
    @ResponseBody
    public String getPackageBookingInfo(String dataType, String ciYmd, String roomRsvNo, String guestNm, String mpNo) {
        return bookingService.getPackageBookingInfo(dataType, ciYmd, roomRsvNo, guestNm, mpNo);
    }

    //예약 조회
    @GetMapping("/reservationList")
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

    @GetMapping("/insertRESOM")
    @ResponseBody
    public String insertRESOM(String dataType, HttpServletRequest httpServletRequest) {
        return bookingService.insertRESOM(dataType, httpServletRequest);
    }

    @GetMapping("/stockResult")
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
