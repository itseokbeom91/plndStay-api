package com.example.stay.openMarket.common.dto;

import lombok.Data;

@Data
public class ToconDto {

    private String tocode;
    private String conId;
    private String tocodeText;
    private int pyongIdx;
    private int tocode_count;
    private int admin_count;
    private int new_icon;
    private int gang_icon;
    private int summer_icon;
    private int sale_icon;
    private int food_icon;
    private int water_icon;
    private int single_icon;
    private String tocod_package; // package 변수 선언 불가능
    private int standpeopleCnt;
    private int maxpeopleCnt;
    private String tocodeInfo;
    private String pkg_code;
    private String GP_ROOM_CD;
    private String HS_ROOM_ID;
    private String saleFlag;
    private int today_p_seq;
    private String syncFlag;
    private int pkg_pri;
    private int pkg_cost;
    private int pkg_seq;
    private String pkgCd;
    private String travelItemId;
    private String rateId;
    private String pyongImgs;
    private String coupang_yn;
    private int intApiPriceType;
    private String MRN_YN;
    private String strIngYN;
    private String strGmkYn;
    private String strAucYn;
    private String str11stYn;
    private String strSsgYn;
    private String strElandYn;
    private String strEzwelYn;
    private String strOrgCid;
    private String dateCreated;
    private String roomTypeId;

    /**
     * DB test_room_type
     */

    private int intCondoID;
    private int intRoomID;
}
