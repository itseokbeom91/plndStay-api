package com.example.stay.accommodation.resom.mapper;

import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Repository("resom.BookingMapper")
public interface BookingMapper {

    String insertRoom(String strPackageDatas, String strRoomDatas, String strStockDatas, String strAccommDatas, String strType);

    List<Map<String, Object>> getPackageCodeAndStoreCode(String type);
}
