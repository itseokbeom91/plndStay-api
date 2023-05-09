package com.example.stay.openMarket.common.controller;

//import com.example.stay.openMarket.hotelStory.controller.APIHotelStoryController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScheduleController {

    @Autowired
//    private APIHotelStoryController apiHotelStoryController;

    //@Scheduled(cron = "5 * * * * *")
    public void test() throws Exception{
        System.out.println("scheduler test");

        // hotelstory API 호출
        //apiHotelStoryController.callApi("");


    }
}
