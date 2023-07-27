package com.example.stay.openMarket.coupang.controller;


import com.example.stay.openMarket.coupang.service.CpAccommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/coupang/accomm/*")
public class CpAccommController {

    @Autowired
    private CpAccommService cpAccommService;

    /**
     * 숙박상품 생성
     */
    @GetMapping("/createAccomm")
    @ResponseBody
    public String createAccomm(String dataType, int intAID, HttpServletRequest httpServletRequest){
        return cpAccommService.createAccomm(dataType, intAID, httpServletRequest);
    }

    /**
     * 숙박상품 수정
     */
    @GetMapping("/updateAccomm")
    @ResponseBody
    public String updateAccomm(String dataType, int intAID, HttpServletRequest httpServletRequest){
        return cpAccommService.updateAccomm(dataType, intAID, httpServletRequest);
    }

    /**
     * 숙박상품 삭제
     */
    @GetMapping("/deleteAccomm")
    @ResponseBody
    public String deleteAccomm(String dataType, int intAID, HttpServletRequest httpServletRequest){
        return cpAccommService.deleteAccomm(dataType, intAID, httpServletRequest);
    }

    /**
     * 객실 생성/수정
     */
    @GetMapping("/creUpdRoom")
    @ResponseBody
    public String creUpdRoom(String dataType, int intRmIdx, HttpServletRequest httpServletRequest){
        return cpAccommService.creUpdRoom(dataType, intRmIdx, httpServletRequest);
    }

    /**
     * 숙박상품 조회
     */
    @GetMapping("/getAccomm")
    @ResponseBody
    public String getAccomm(String dataType, int intAID, HttpServletRequest httpServletRequest){
        return cpAccommService.getAccomm(dataType, intAID, httpServletRequest);
    }

    /**
     *  숙박 객실 요금&수량 등록/수정
     */
    @GetMapping("creUpdGoods")
    @ResponseBody
    public String creUpdGoods(String dataType, int intRmIdx, String strDate, HttpServletRequest httpServletRequest){
        return cpAccommService.creUpdGoods(dataType, intRmIdx, strDate, httpServletRequest);
    }

}
