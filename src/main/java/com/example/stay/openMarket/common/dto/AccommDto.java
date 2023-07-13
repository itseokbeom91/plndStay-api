package com.example.stay.openMarket.common.dto;

import lombok.Data;

@Data
public class AccommDto {

    private int intAID;
    private String strSubject;          // 시설명
    private String strOMKDetailInfo;    // 오픈마켓 상세페이지
    private String strDescription;      // 시설 설명
    private String strRsvGuide;         // 예약 안내
    private String strTraffic;          // 주변 시설
    private String strPdtCode;          // 오픈마켓 코드
    private String strACMPhotos;        // 시설 이미지
    private String strKeywords;         // 검색어
    private String strAround;           // 주변명소

}
