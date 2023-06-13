package com.example.stay.accommodation.hotelStory.mapper;

import com.example.stay.accommodation.hotelStory.dto.BookingDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelStoryMapper {

/*
    String insertAccomm(String strPropertyId, String strPropertyName, String strAddress, String strPhone, String strNumRooms, String strLocation, String strHomePageUrl, String strCheckInTime, String strCheckOutTime
            , String strLongitude, String strLatitude, String strCity, String strPropertyDescription, String strTrafficInformation, String strRoomInformation);

    String insertRoomType(String strRoomTypeName, int intAID, int intMinPersons, int intMaxPersons, String strRoomTypeId, int intStep, String strIngYn, String strText1, String strText2);

    String insertRatePlan(int intAID, String strAccommId, int intToconIdx, String strRoomTypeId, String strRatePlanId, String strRatePlanName, String strBedTypeCode, String strMealCode, int intMinPersons, int intMaxPersons, int intPrice);
*/
    String insertAccommtotal(String strPropertyId, String strPropertyName, String strAddress, String strPhone, String strNumRooms, String strLocation, String strHomePageUrl, String strCheckInTime, String strCheckOutTime
            , String strLongitude, String strLatitude, String strCity, String strPropertyDescription, String strTrafficInformation, String strRoomInformation, String imgData, String cancelData, String roomTypeData);

    String insertProperty(String strPropertyId, String strLocation, String strCity, String strPropertyName, String strLatitude, String strLongitude, String strStarRating, String strNumRooms, String strCheckInTime, String strCheckOutTime, String strPhone, String strAddress, String strHomePageUrl
            , String strPropertyDescription, String strTrafficInformation, String strRsvGuide, String imgData, String cancelData, String roomTypeData, String addData);

    String insertGoods(String strRatePlanID, int intStock, String strDate, int intBasicPrice, int intSalePrice);

    BookingDto getbooking(int intBookingID);

    String updateBooking(int intBookingID, String strBookingProcess, String strSpBookingId, int intRoomCount);

    String insertRefund(int intBookingID, String strRefundData);
}
