package com.example.stay.openMarket.common.dto;

import lombok.Data;

import java.util.Date;

@Data
public class RsvStayDto {
    private int intRsvID;
    private String strDeleteYn;
    private int intSettleID;
    private String strPayType;
    private String strStatusCode;
    private String strRsvSite;
    private String strRsvCode;
    private int intAID;
    private int intRmIdx;
    private int intRmCnt;
    private int intQuantityA;
    private int intQuantityC;
    private int intQuantityB;
    private Date dateCheckIn;
    private Date dateCheckOut;
    private int intSeller;
    private int intSupplier;
    private String strRmtypeName;
    private String strRmOptName;
    private int intUID;
    private String strOrdName;
    private String strOrdPhone;
    private String strOrdEmail;
    private String strRcvName;
    private String strRcvPhone;
    private String strRcvEmail;
    private String strRsvRmNum;
    private Date dateAcct;
    private String strAcctName;
    private String strAcctBank;
    private String strIP;
    private String strRemark;
    private Date dateCreated;
    private int intProcSID;
    private int intCanceledSID;
    private int intDealIdx;
    private int intBCIdx;
    private int intVouIdx;

    private String strRmtypeID;
    private String strLocalCode;
    private String strPkgCode;
    private String datePurchase;
    private double moneyCostA;
    private double moneyCostC;
    private double moneySalesA;
    private double moneySalesC;

    private String strRateplanID;

    // RM_OPTION

}
