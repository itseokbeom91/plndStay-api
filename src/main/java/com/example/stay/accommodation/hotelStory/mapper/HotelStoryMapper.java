package com.example.stay.accommodation.hotelStory.mapper;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelStoryMapper {


    int insertAccomm(String strPropertyId, String strPropertyName, String strAddress, String strPhone, String strNumRooms, String strHomePageUrl, String strCheckInTime, String strCheckOutTime
            , String strLongitude, String strLatitude, String strPropertyDescription, String strTrafficInformation, String strRoomInformation);
}
