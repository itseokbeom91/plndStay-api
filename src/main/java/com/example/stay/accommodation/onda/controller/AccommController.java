package com.example.stay.accommodation.onda.controller;

import com.example.stay.accommodation.onda.service.AccommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/accomm/*")
public class AccommController {

    @Autowired
    private AccommService accommService;

    /**
     * 전체 숙소 목록 가져오기
     */
    @GetMapping("getAccommList")
    public void getAccommList(){
        accommService.getAccommListApi();
    }

    /**
     * 특정 숙소 상세정보 가져오기
     */
    @GetMapping("getAccommDetail")
    public void getAccommDetail(String property_id){
        accommService.getAccommDetailApi(property_id);
    }

    /**
     * 특정 숙소 전체 객실 목록 가져오기
     */
    @GetMapping("getRoomtypeList")
    public void getRoomTypeList(String property_id){
        accommService.getRoomTypeListApi(property_id);
    }

    /**
     * 특정 객실 상세정보 가져오기
     */
    @GetMapping("getRoomTypeDetail")
    public void getRoomTypeDetail(String property_id, String roomtype_id){
        accommService.getRoomTypeDetail(property_id, roomtype_id);
    }

    /**
     * 특정 객실의 전체 패키지 목록 가져오기
     */
    @GetMapping("getPackageList")
    public void getPackageList(String property_id, String roomtype_id){
        accommService.getPackageList(property_id, roomtype_id);
    }

    /**
     * 특정 패키지의 상세 정보 가져오기
     */
    @GetMapping("getPackageDetail")
    public void getPackageDetail(String property_id, String roomtype_id, String rateplan_id){
        accommService.getPackageDetail(property_id, roomtype_id, rateplan_id);
    }


}
