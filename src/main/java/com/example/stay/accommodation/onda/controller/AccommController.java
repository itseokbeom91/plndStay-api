package com.example.stay.accommodation.onda.controller;

import com.example.stay.accommodation.onda.service.AccommService;
import com.example.stay.common.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/accomm/*")
public class AccommController {

    @Autowired
    private AccommService accommService;

    /**
     * 전체 숙소 목록 가져오기
     */
    @GetMapping("getAccommList")
    public void getAccommList(@RequestParam(value = "lastdate", required = false) String lastdate,
                              @RequestParam(value = "status", required = false) String status){
        String path = Constants.ondaPath + "properties";
        if(lastdate != null){
            path += "?lastdate=" + lastdate;

            if(status != null){
                path += "&status=" +  status;
            }
        }else{
            if(status != null){
                path += "?status=" +  status;
            }
        }

        accommService.getAccommListApi(path);
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

    /**
     * ONDA에서 숙소정보 가져와서 있으면 PASS, 없으면 INSERT
     */
    @GetMapping("getAccommNInsert")
    public void getAccommNInsert(@RequestParam(value = "lastdate", required = false) String lastdate,
                                 @RequestParam(value = "status", required = false) String status){
        String path = Constants.ondaPath + "properties";
        if(lastdate != null){
            path += "?lastdate=" + lastdate;

            if(status != null){
                path += "&status=" +  status;
            }
        }else{
            if(status != null){
                path += "?status=" +  status;
            }
        }

        accommService.getAccommNInsert(path);
    }

}
