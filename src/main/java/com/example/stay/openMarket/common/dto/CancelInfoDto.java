package com.example.stay.openMarket.common.dto;

import lombok.Data;

@Data
public class CancelInfoDto {

    /**
     * DB tbl_cancel_info_row
     */

    private String strCnFlag; // cn_flag
    private int intCnDcnt; // cn_Dcnt
    private int intCnPer; // cn_per
    private String cancelPolicyNotice; // 비수기 + 성수기 조합한 취소규정

}
