package com.example.stay.openMarket.elevenST.mapper;

import com.example.stay.openMarket.common.dto.RsvStayDto;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository("elvenST.ElevenStMapper")
public interface ElevenStMapper {
    Map<String, Object> getAccomm(String accommID);
    String insertAccomm(String intAID,String intOmkIdx,String intUsageYn, String strPdtSubject, String strPdtCode, String strDetailInfo);

    String getIntAID(String prdNo);

    void updateUsg(int intAID, String usageYn);

    void updateSeq(String intAID, String strRmtypeNm, String strDate, String prdStockNo);

    int getMinPrice(String intAID, String strDate);

    String updateRsv(String intRsvNo, String rsvState, RsvStayDto rsvStayDto);

    String getUsgYn(String intAID);
}
