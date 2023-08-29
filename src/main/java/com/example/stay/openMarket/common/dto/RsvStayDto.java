package com.example.stay.openMarket.common.dto;

import lombok.Data;

import java.util.Date;

@Data
public class RsvStayDto {
    private int intRsvID;
    private String strDeleteYn;
    private int intSettleID;
    private String strPayType;
    private String strRsvFlag;
    private int intOMKIdx;
    private String strRsvCode;
    private int intAID;
    private int intRmIdx;
    private int intRmCnt;
    private int intQuantityA;
    private int intQuantityC;
    private int intQuantityB;
    private Date dateEnterIn;
    private Date dateEnterOut;
    private String strSeller;
    private String strSupplier;
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
    private int intProcSID;
    private int intCanceledSID;
    private int intDealIdx;
    private int intBCIdx;
    private int intVouIdx;
}
