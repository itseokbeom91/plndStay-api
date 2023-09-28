package com.example.stay.openMarket.elevenST.mapper;

import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository("elvenST.ElevenStMapper")
public interface ElevenStMapper {
    Map<String, Object> getAccomm(String accommID);
    String insertAccomm(String intAID, String strPdtSubject, String strPdtCode, String strDetailInfo);

    void updateUsg(int intAID, String usageYn);
}
