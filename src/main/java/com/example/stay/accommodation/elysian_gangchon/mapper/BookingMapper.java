package com.example.stay.accommodation.elysian_gangchon.mapper;

import org.springframework.stereotype.Repository;

@Repository("elysian_gangchon.BookingMapper")
public interface BookingMapper {
    String updateGoods(String strPropertyID, String strRmtypeID, String strStockDatas);

    String getStrPropertyID(String strRmtypeID);
}
