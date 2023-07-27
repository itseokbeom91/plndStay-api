package com.example.stay.openMarket.coupang.mapper;

import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface CoupangMapper {
    String insertCpCodes(int intAID, String strPdtCode, String strPdtSubject, String strDetailInfo, String itemCodeDatas, String rateCodeDatas);

    String getStrPdtCode(int intAID);

    int updateCpCodes(String strCpItemCode, String strCpRateCode, int intRmIdx);

    Map<String, Object> getIntAIDnPdtCode(int intRmIdx);

}
