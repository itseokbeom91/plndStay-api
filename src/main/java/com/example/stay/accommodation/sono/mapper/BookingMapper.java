package com.example.stay.accommodation.sono.mapper;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository("sono.BookingMapper")
public interface BookingMapper {
    String localInsert(String data);
    Map<String , Object> getBookingInfoFromBookingIdx(String intRsvID);
    String insertRoom(String strPackageDatas, String strRoomDatas, String strStockDatas, String strAccommDatas, String strType);

    String updatePackageStock(String strPackageStockDatas);

    List<Map<String, Object>> getPackageCodeAndStoreCode(String type);

}
