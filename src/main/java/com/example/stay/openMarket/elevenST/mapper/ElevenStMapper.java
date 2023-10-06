package com.example.stay.openMarket.elevenST.mapper;

import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository("elvenST.ElevenStMapper")
public interface ElevenStMapper {
    Map<String, Object> getAccomm(String accommID);
    String insertAccomm(String intAID,String intOmkIdx,String intUsageYn, String strPdtSubject, String strPdtCode, String strDetailInfo);

    String getIntAID(String prdNo);

    void updateUsg(int intAID, String usageYn);

    String insertSeq(String intAID, String strRmtypeNm, String strDate, String prdStockNo);
}
