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
    private String strType;             // 시설 타입(호텔, 리조트...)
    private double decLat;              // 위도
    private double decLon;              // 경도
    private String strAddr1;            // 도로명주소
    private String strZipCode;          // 우편번호
    private String strWebsite;          // 사이트
    private String strPhone;            // 전화번호
    private String strCheckIn;
    private String strCheckOut;



    private String strRegionKeyword;    // 지역 키워드(도/시)
    private String strDistrict2;    // 시/구
    private String strFac;       // 제공 서비스
}
