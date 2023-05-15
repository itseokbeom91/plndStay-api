package com.example.stay.accommodation.hotelStory.mapper;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelStoryMapper {


    String insertAccomm(String strPropertyId, String strPropertyName, String strAddress, String strPhone, String strNumRooms, String strHomePageUrl, String strCheckInTime, String strCheckOutTime
            , String strLongitude, String strLatitude, String strPropertyDescription, String strTrafficInformation, String strRoomInformation);

    String insertRoomType(String strRoomTypeName, int intAID, int intSaleRate, int intMinPersons, int intMaxPersons, String strRatePlanId, String strRoomTypeId, int intStep, String strIngYn, String strText1, String strText2);
}
