package com.example.stay.accommodation.elysian_gangchon.mapper;

import org.springframework.stereotype.Repository;

@Repository("elysian_gangchon.BookingMapper")
public interface BookingMapper {
    String updateGoods(int intAID, int intRmIdx, String strStockDatas);

    int getIntAID(int intRmIdx);
}
