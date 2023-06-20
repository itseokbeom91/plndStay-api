package com.example.stay.accommodation.elysian_gangchon.mapper;

import org.springframework.stereotype.Repository;

@Repository("elysian_gangchon.BookingMapper")
public interface BookingMapper {
    String updateGoods(String strRmtypeID, String strPropertyID, String dateSales, int intStock, int intOmkStock);

    String getStrPropertyID(String strRmtypeID);
}
