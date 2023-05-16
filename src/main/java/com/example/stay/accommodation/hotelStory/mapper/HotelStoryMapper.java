package com.example.stay.accommodation.hotelStory.mapper;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelStoryMapper {


    String insertAccomm(String strPropertyId, String strPropertyName, String strAddress, String strPhone, String strNumRooms, String strLocation, String strHomePageUrl, String strCheckInTime, String strCheckOutTime
            , String strLongitude, String strLatitude, String strCity, String strPropertyDescription, String strTrafficInformation, String strRoomInformation);

    String insertRoomType(String strRoomTypeName, int intAID, int intMinPersons, int intMaxPersons, String strRoomTypeId, int intStep, String strIngYn, String strText1, String strText2);

    String insertRatePlan(int intAID, String strAccommId, int intToconIdx, String strRoomTypeId, String strRatePlanId, String strRatePlanName, String strBedTypeCode, String strMealCode, int intMinPersons, int intMaxPersons, int intPrice);
}
