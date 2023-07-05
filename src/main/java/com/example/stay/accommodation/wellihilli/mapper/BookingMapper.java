package com.example.stay.accommodation.wellihilli.mapper;

import org.springframework.stereotype.Repository;

@Repository("wellihilli.BookingMapper")
public interface BookingMapper {
    String getStrRmtypeID(int intRmIdx);
    String updateGoods(String strStockDatas);
}
