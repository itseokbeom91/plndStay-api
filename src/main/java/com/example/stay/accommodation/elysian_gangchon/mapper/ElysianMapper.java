package com.example.stay.accommodation.elysian_gangchon.mapper;

import org.springframework.stereotype.Repository;

@Repository
public interface ElysianMapper {
    String updateGoods(int intAID, int intRmIdx, String strStockDatas);

    int getIntAID(int intRmIdx);
}
