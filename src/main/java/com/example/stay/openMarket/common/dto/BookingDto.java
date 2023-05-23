package com.example.stay.openMarket.common.dto;

import lombok.Data;

import java.util.Date;

@Data
public class BookingDto {
    /**
     * DB tbl_Booking
     */

    private String strOrderId;
    private String strCondoId;
    private String strSpace;
    private Date dateStartDay;
    private Date dateEndDay;
    private int intRoom;
    private int intCost;
    private int intSalePrice;
    private String strSaleType;
    private int intFine;
    private int intReceipts;
    private int intOutstanding;
    private int intDiscount;
    private String strAdditional;
    private String strUserId;
    private String strJumin;
    private String strOrdName;
    private String strOrdPhone;
    private String strOrdEmail;
    private String strRecvName;
    private String strRecvPhone;
    private String strRecvEmail;
    private String strPayType;
    private String strPayState;
    private String strAccName;
    private String strAccDay;
    private String strAccBank;
    private String strOrderProcess;
    private String strRoomNumber;
    private String strTrno;
    private String strChannel;
    private String strBuying;
    private String strSale;
    private String strOmkOrderId;
    private int intOmkcomplatePrice;
    private String strIp;
    private String strMemo;
//    private String strHpReceiver;
//    private String strHpSender_cd;
//    private String strKko_cd;
//    private String strMsg_body;
    private String strOmkPackNo;
    private String omkPackNo;
    private String strSt11SelectDate;
    private String strPid;

    /**
     * DB test_tbl_Booking
     */
    private String strOrderID;
    private int intCondoID;

}
