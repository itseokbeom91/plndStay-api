package com.example.stay.accommodation.yongpyong_beache.mapper;

import org.springframework.stereotype.Repository;

@Repository
public interface YPBMapper {

    String insertStock(String strProertyId, String strCateCode, String strRoomTypeId, String strPackageCode, String strStockData);
}
