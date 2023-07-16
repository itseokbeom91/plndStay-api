package com.example.stay.accommodation.yongpyong_beache.mapper;

import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface YPBMapper {

    Map<String, String> getAcmRmID(int intAID, int intRmIdx);

    Map<String, String> getPkgLcdID(int intPkgIdx);

    String insertStock(int intAID, int intRmIdx, String strPackageCode, String strStockData);
}
