package com.example.stay.openMarket.coupang.mapper;

import org.springframework.stereotype.Repository;

@Repository
public interface CoupangMapper {
    String insertCpCodes(int intAID, String strPdtCode, String strPdtSubject, String itemCodeDatas, String rateCodeDatas);

    String getStrPdtCode(int intAID);

    int updateCpCodes(String strCpItemCode, String strCpRateCode, int intRmIdx);
}
