package com.example.stay.openMarket.common.dto;

import lombok.Data;

@Data
public class CondoDto {

    /**
     * DB condo
     */

    private String strItemID; // itemId
    private int intAID; // con_id
    private String strAcmName; // con_name
    private String strMainAcmName; // PNAME
    private String strTimeIn; // time_in
    private String strTimeOut; // time_out
    private String strFlag; // con_flag
    private String strZipCode; // COALESCE(C.con_zip_new, C.con_zip)
    private String strAddress; // COALESCE(C.con_addr_new, C.con_addr1 +' '+ C.con_addr2)
    private String decLat; // mapY
    private String decLng; // mapX
    private String strLoc; // location
    private String strCity; // city
    private String strGugun; // gugun
    private String strPdtDtlInfo; // SSG_INFO
    private String strSummary; // con_desc
    private String strFacilities; // con_toge
    private String strDescription; // con_sookbak
    private String strUsageNotice; // mobileWarning 이용안내 및 유의사항
    private String strTagName; // TAGNAME
    private String strLandNumberAdr; // CONCAT(C.con_addr1,' ',C.con_addr2) 지번주소
    private String strChainCode; // 숙소 체인점 분류
    private String strConAddrNew; // con_addr_new
    private String strConDisplay; // con_display 노출여부 -> strUseYN로 변경 추후 삭제
    private String strApiFlag; // API_FLAG
    private String strRegDate; // 생성일시
    private String strAccommId; // accomm_id
    private String strNation; // nation
    private String strConTel; // con_tel
    private String strConFax; // con_fax


    /**
     * test_condo
     */
    private int intCondoID;
    private String strUseYN;

    /**
     * SSGMapper
     */
    private String strCondoPhotos; // photo 리스트 (|로 구분)
    private int intGoodsCnt; // 룸타입 방 개수??

    /**
     * coupang > LodgingsMapper
     */
    private String coupangId; // OMK_PRODUCT의 coupang_id

    /**
     * eland > OrderMapper
     */
    private int moneyCost; // goods의 stend_pri
    private String tocode; // tocon의 tocode
}
