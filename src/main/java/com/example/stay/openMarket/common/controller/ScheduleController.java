package com.example.stay.openMarket.common.controller;


import com.example.stay.openMarket.ssg.service.SsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScheduleController {

    @Autowired
    private SsgService service;

    //@Scheduled(cron = "5 * * * * *")
    public void test() throws Exception{
        System.out.println("scheduler test");

        /**
         * ssg itemId 가져오는 scheduler 생성
         */

        // hotelstory API 호출
        //apiHotelStoryController.callApi("");


    }
}
