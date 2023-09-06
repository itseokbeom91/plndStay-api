package com.example.stay.accommodation.resom.mapper;

import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Repository("resom.BookingMapper")
public interface BookingMapper {

    Map<String , Object> getBookingInfoFromBookingIdx(String BookingIdx);
    String insertRoom(String strPackageDatas, String strRoomDatas, String strStockDatas, String strAccommDatas, String strType);

    Map<String, Object> getPackageCodeAndStoreCode(String type);
}
