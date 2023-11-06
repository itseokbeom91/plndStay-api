package com.example.stay.accommodation.sono.mapper;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository("sono.BookingMapper")
public interface BookingMapper {
    String localInsert(String data);
    Map<String , Object> getBookingInfoFromBookingIdx(int intRsvID);
    String insertRoom(String strPackageDatas, String strRoomDatas, String strStockDatas, String strAccommDatas, String strType);

    String updatePackageStock(String strPackageStockDatas);

    List<Map<String, Object>> getRmPackageMap();

    String updateRsvState(int intRsvID, String strStatusCode, String strRsvRmNumDatas, String strPenaltyDatas);

    String updateRsvCode(String comRsvNo, int intRsvID);



}
