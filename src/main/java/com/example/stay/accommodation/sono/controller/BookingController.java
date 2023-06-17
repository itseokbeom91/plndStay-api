package com.example.stay.accommodation.sono.controller;

import com.example.stay.accommodation.sono.service.BookingService;
import com.example.stay.common.util.ResponseResult;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller("sono.BookingController")
@RequestMapping("/sono/booking/*")
public class BookingController {

    @Autowired
    BookingService bookingService = new BookingService();

    @GetMapping("/getPackageList")
    @ResponseBody
    public ResponseResult getPackageList(HttpServletRequest httpServletRequest){
        System.out.println("패키지 목록 조회");
        ResponseResult responseResult = bookingService.getPackageList(httpServletRequest);
        return  responseResult;
    }

    @GetMapping("/getPackageInfo")
    @ResponseBody
    public ResponseResult getPackageInfo(String pkgNo, HttpServletRequest httpServletRequest){
        System.out.println("패키지 목록 조회");
        ResponseResult responseResult = bookingService.getPackageInfo(pkgNo, httpServletRequest);
        return  responseResult;
    }

    @GetMapping("/getPackageStatus")
    @ResponseBody
    public ResponseResult getPackageStatus(String pkgNo, String storeCd, String sDate, String rmTypeCd, String ciYmd, HttpServletRequest httpServletRequest){
        System.out.println("패키지 목록 조회");
        ResponseResult responseResult = bookingService.getPackageStatus(pkgNo, storeCd, sDate, rmTypeCd, ciYmd, httpServletRequest);
        return  responseResult;
    }

    @GetMapping("/getPackageAmount")
    @ResponseBody
    public ResponseResult getPackageAmount(String pkgNo, String storeCd, String sDate, String rmTypeCd, String ciYmd, String nights, String rmCnt, HttpServletRequest httpServletRequest){
        System.out.println("패키지 목록 조회");
        ResponseResult responseResult = bookingService.getPackageAmount(pkgNo, storeCd, sDate, rmTypeCd, ciYmd, nights, rmCnt, httpServletRequest);
        return  responseResult;
    }

    @GetMapping("/reservation")
    @ResponseBody
    public ResponseResult reservation(String pkgNo, String storeCd, String ciYmd, String rmTypeCd, String comRsvNo, String userName, String userTel, String payAmt, String adultCnt, String childCnt ,HttpServletRequest httpServletRequest){
        System.out.println("패키지 목록 조회");
        ResponseResult responseResult = bookingService.reservation(pkgNo, storeCd, ciYmd, rmTypeCd, comRsvNo, userName, userTel, payAmt, adultCnt, childCnt ,httpServletRequest);
        return  responseResult;
    }

    @GetMapping("/getRoomList")
    @ResponseBody
    public ResponseResult getRoomList(HttpServletRequest httpServletRequest){
        System.out.println("패키지 목록 조회");
        ResponseResult responseResult = bookingService.getRoomList(httpServletRequest);
        return  responseResult;
    }

    @GetMapping("/insertSONO")
    @ResponseBody
    public String insertSONO(HttpServletRequest httpServletRequest) {
        String result = bookingService.insertSONO(httpServletRequest);
        return result;
    }


}
