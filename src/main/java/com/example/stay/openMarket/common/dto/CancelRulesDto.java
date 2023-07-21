package com.example.stay.openMarket.common.dto;

import lombok.Data;

@Data
public class CancelRulesDto {
    private int intIdx;         // idx
    private int intAID;         // 시설idx
    private String strFlag;     // 내/외부 성수기/비성수기 구분
    private int intDay;         // 며칠 전
    private int intPercent;     // 퍼센트
}
