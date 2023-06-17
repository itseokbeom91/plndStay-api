package com.example.stay.accommodation.sono.mapper;

import org.springframework.stereotype.Repository;


@Repository("sono.BookingMapper")
public interface BookingMapper {
    String insertRoom(String strPackageDatas, String strRoomDatas, String strStockDatas, String strAccommDatas, String strType);

}
