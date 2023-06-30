package com.example.stay.accommodation.hanwha.mapper;

import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface HanwhaMapper {

    String packageList(String PackageData);

    String getRmID(int intAID, int intRmIdx);

    Map<String, String> getPkgLcdID(int intPkgIdx);

    String insertStock(int intAID, int intRmIdx, String strPackageCode, String strStockData);
}
