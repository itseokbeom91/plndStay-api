package com.example.stay.common.util;

import com.example.stay.common.mapper.CommonAcmMapper;
import com.example.stay.common.util.Constants;
import com.example.stay.openMarket.common.dto.BookingDto;
import com.example.stay.openMarket.common.dto.RsvStayDto;
import com.example.stay.openMarket.common.mapper.CommonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class FaxService {

    @Autowired
    private CommonMapper commonMapper;

    public String faxSend(Map<String ,Object> faxMap, RsvStayDto rsvStayDto) {
        /*
        숙소명, 팩스번호, 예약번호(어디쪽인지는 확인 못함),
        한화( 담당자, 지역, 입실일, 박수, 객실타입, 객실수, 사용자명, 사용자연락처, 예약번호, 원가, 객실요청타입)
        대명 ( 숙소명, 취소예약번호, 요청번호, 객실타입, 이용일자, 박수, 이용자, 이용자연락처, 비고, 원가, 고유번호, 예약번호
         */


        try {
            StringBuffer sb = new StringBuffer();

            sb.append("*Shtml*E*Shead*E*S/head*E*Sbody*E*Sp style=\"font-size:13px;padding:5px 5px 15px;margin:0;\"*E※ 발송시간 : 2023-10-27 13:16:20*S/p*E");
            sb.append("*Sbr*E");
            sb.append("*Sbr*E");
            sb.append("*Stable width=\"750\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"background-image:url(http://www.condo24.com/c/images/invoice/fax_bg3.gif);\"*E");
            sb.append("  *Stbody*E*Str*E*Std*E*Simg src=\"http://www.condo24.com/c/images/invoice/fax_title10.gif\" width=\"750\"*E*S/td*E*S/tr*E");
            sb.append("  *Str*E");
            sb.append("    *Std style=\"padding:10px;\" valign=\"top\"*E");
            sb.append("      *Stable width=\"100%\" border=0 cellspacing=\"0\" cellpadding=\"0\"*E");
            sb.append("        *Stbody*E*Str*E");
            sb.append("          *Std*E*Simg src=\"http://www.condo24.com/c/images/invoice/fax_write.gif\"*E*S/td*E");
            sb.append("          *Std align=\"right\" valign=\"bottom\" style=\"font-size:12px;font-weight:bold;\"*E발신일자: 2023년 10월 27일 발송시간: 13:16:10*S/td*E");
            sb.append("        *S/tr*E");
            sb.append("        *S/tbody*E*S/table*E");
            sb.append("      *Sdiv style=\"padding-top:10px;\"*E*S/div*E");
            sb.append("      *Stable width=\"100%\" border=\"0\" cellspacing=\"1\" cellpadding=\"0\" bgcolor=\"#333333\"*E");
            sb.append("        *Stbody*E*Str*E");
            sb.append("          *Std width=\"60\" rowspan=\"2\" align=\"center\" bgcolor=\"#FFFFFF\" style=\"padding:5px 10px;\"*E*Sb*E수 신*S/b*E*S/td*E");
            sb.append("          *Std width=\"100\" bgcolor=\"#FFFFFF\" style=\"padding:5px 10px;\"*E콘도/호텔명*S/td*E");
            sb.append("          *Std bgcolor=\"#FFFFFF\" style=\"padding:5px 10px;\"*E오크밸리리조트 F.0337303966*S/td*");
            sb.append("        *S/tr*E");
            sb.append("        *Str*E");
            sb.append("          *Std bgcolor=\"#FFFFFF\" style=\"padding:5px 10px;\"*E담당자*S/td*E");
            sb.append("          *Std bgcolor=\"#FFFFFF\" style=\"padding:5px 10px;\"*E♥이혜린님/이수현님/고예나님/예약담당자님*S/td*E");
            sb.append("        *S/tr*E");
            sb.append("        *Str*E");
            sb.append("          *Std rowspan=\"4\" align=\"center\" bgcolor=\"#FFFFFF\" style=\"padding:5px 10px;\"*E*Sb*E발 신*S/b*E*S/td*E");
            sb.append("          *Std bgcolor=\"#FFFFFF\" style=\"padding:5px 10px;\"*E회사명*S/td*E");
            sb.append("          *Std bgcolor=\"#FFFFFF\" style=\"padding:5px 10px;\"*E(주)동무해피데이즈  T.1588-0134 F.02-6952-2353*S/td*E");
            sb.append("        *S/tr*E");
            sb.append("        *Str*E");
            sb.append("          *Std bgcolor=\"#FFFFFF\" style=\"padding:5px 10px;\" rowspan=\"2\"*EHot Line*S/td*E");
            sb.append("          *Std bgcolor=\"#FFFFFF\" style=\"padding:5px 10px;font-size:12px;\"*E");
            sb.append("            *Sspan style=\"font-weight:bold;font-size:13px;\"*E고객만족팀(숙박) T.1588-0134*S/span*E");
            sb.append("          *S/td*E");
            sb.append("        *S/tr*E");
            sb.append("        *Str*E");
            sb.append("          *Std bgcolor=\"#FFFFFF\" style=\"padding:5px 10px;font-size:12px;padding:0px;\"*E");
            sb.append("            *Stable width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#333333\" style=\"margin:0;\"*E");
            sb.append("              *Stbody*E*Str*E");
            sb.append("                *Std bgcolor=\"#FFFFFF\" style=\"padding:5px 10px;font-size:12px;padding:0px;\"*E");
            sb.append("                  *Stable width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#333333\" style=\"margin:0;\"*E");
            sb.append("                    *Stbody*E*Str*E");
            sb.append("                      *Std bgcolor=\"#FFFFFF\" style=\"padding:5px 10px;font-size:11px;vertical-align:top;\"*E");
            sb.append("                        *Sspan style=\"font-weight:bold;font-size:12px;\"*E숙박팀*S/span*E*Sbr*E");
            sb.append("                        강미영부장 070-5101-3208*Sbr*E");
            sb.append("                        문경부부장 070-5101-3209*Sbr*E");
            sb.append("                      *S/td*E");
            sb.append("                      *Std bgcolor=\"#FFFFFF\" style=\"padding:5px 10px;font-size:11px;vertical-align:top;\"*E");
            sb.append("                        *Sspan style=\"font-weight:bold;font-size:12px;\"*E *S/span*E*Sbr*E");
            sb.append("                        문지연과장 070-5101-3201*Sbr*E");
            sb.append("                        이승우주임 070-5101-3210*Sbr*E");
            sb.append("                        이선희사원 070-5101-3207*Sbr*E");
            sb.append("                      *S/td*E");
            sb.append("                      *Std bgcolor=\"#FFFFFF\" style=\"padding:5px 10px;font-size:11px;vertical-align:top;\"*E");
            sb.append("                        *Sspan style=\"font-weight:bold;font-size:12px;\"*E경영지원팀(정산)*S/span*E*Sbr*E");
            sb.append("                        김민지대리070-4226-0558");
            sb.append("                      *S/td*E");
            sb.append("                    *S/tr*E");
            sb.append("                    *S/tbody*E*S/table*E");
            sb.append("                *S/td*E");
            sb.append("              *S/tr*E");
            sb.append("              *S/tbody*E*S/table*E");
            sb.append("          *S/td*E");
            sb.append("        *S/tr*E");
            sb.append("        *S/tbody*E*S/table*E");
            sb.append("      *Sdiv style=\"padding-top:20px;\"*E*S/div*E");
            sb.append("      *Stable width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"1\" bgcolor=\"#333333\"*E");
            sb.append("        *Stbody*E*Str align=\"center\" bgcolor=\"#F2F2F2\"*E");
            sb.append("          *Std width=\"30\" bgcolor=\"#fafafa\" style=\"font-size:13px;font-weight:bold;text-align:center;\" height=\"29\"*E번호*S/td*E");
            sb.append("          *Std bgcolor=\"#fafafa\" style=\"font-size:12px;font-weight:bold;text-align:center;\"*E상품명*S/td*E");
            sb.append("          *Std width=\"45\" bgcolor=\"#fafafa\" style=\"font-size:13px;font-weight:bold;text-align:center;\"*E입실일*S/td*E");
            sb.append("          *Std width=\"20\" bgcolor=\"#fafafa\" style=\"font-size:13px;font-weight:bold;text-align:center;\"*E박*S/td*E");
            sb.append("          *Std width=\"20\" bgcolor=\"#fafafa\" style=\"font-size:13px;font-weight:bold;text-align:center;\"*E수*S/td*E");
            sb.append("          *Std width=\"80\" bgcolor=\"#fafafa\" style=\"font-size:13px;font-weight:bold;text-align:center;\"*E평형*S/td*E");
            sb.append("          *Std width=\"50\" bgcolor=\"#fafafa\" style=\"font-size:13px;font-weight:bold;text-align:center;\"*E이용자*S/td*E");
            sb.append("          *Std width=\"90\" bgcolor=\"#fafafa\" style=\"font-size:13px;font-weight:bold;text-align:center;\"*E연락처*S/td*E");
            sb.append("          *Std width=\"40\" bgcolor=\"#fafafa\" style=\"font-size:13px;font-weight:bold;text-align:center;\"*E★구분*S/td*E");
            sb.append("          *Std width=\"65\" bgcolor=\"#fafafa\" style=\"font-size:13px;font-weight:bold;text-align:center;\"*E예약번호*S/td*E");
            sb.append("          *Std width=\"65\" bgcolor=\"#fafafa\" style=\"font-size:13px;font-weight:bold;text-align:center;\"*E숙소입금가*S/td*E");
            sb.append("        *S/tr*E");
            sb.append("        ");
            sb.append("          *Str align=\"center\" bgcolor=\"#ffffff\"*E");
            sb.append("            *Std style=\"padding:5px 2px;font-size:13px;font-weight:bold;text-align:center;\"*E1*S/td*E");
            sb.append("            *Std style=\"padding:5px 2px;font-size:13px;font-weight:bold;text-align:center;\"*E오크밸리리조트*S/td*E");
            sb.append("            *Std style=\"padding:5px 2px;font-size:13px;font-weight:bold;text-align:center;\"*E2023.11.18*S/td*E");
            sb.append("            *Std style=\"padding:5px 2px;font-size:13px;font-weight:bold;text-align:center;\"*E1*S/td*E");
            sb.append("            *Std style=\"padding:5px 2px;font-size:13px;font-weight:bold;text-align:center;\"*E1*S/td*E");
            sb.append("            *Std style=\"padding:5px 2px;font-size:13px;font-weight:bold;text-align:center;\"*E스키/C동/45 *S/td*E");
            sb.append("            *Std style=\"padding:5px 2px;font-size:13px;font-weight:bold;text-align:center;\"*E이재준*S/td*E");
            sb.append("            *Std style=\"padding:5px 2px;font-size:13px;font-weight:bold;text-align:center;\"*E010-6396-0645*S/td*E");
            sb.append("            *Std style=\"padding:5px 2px;font-size:13px;font-weight:bold;text-align:center;\"*E★변경*S/td*E");
            sb.append("            *Std style=\"padding:5px 2px;font-size:13px;font-weight:bold;text-align:center;\"*E23309512 김미회*S/td*E");
            sb.append("            *Std style=\"padding:5px 2px;font-size:13px;font-weight:bold;text-align:center;\"*E200,000*S/td*E");
            sb.append("          *S/tr*E");
            sb.append("");
            sb.append("        ");
            sb.append("        *Str*E");
            sb.append("          *Std align=\"left\" bgcolor=\"#FFFFFF\" colspan=\"11\" style=\"padding:5px;font-size:13px;font-weight:bold;\"*E- 주문번호 : 2023-1118-11248632340    -*S/td*E");
            sb.append("        *S/tr*E");
            sb.append("        *S/tbody*E*S/table*E");
            sb.append("      *Sdiv style=\"padding-top:15px;\"*E*S/div*E");
            sb.append("      *Stable width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"border:1px solid #909090;\"*E");
            sb.append("        *Stbody*E*Str*E*Std style=\"padding:10px; border-bottom:2px solid #656565;\"*E*Simg src=\"http://www.condo24.com/c/images/invoice/fax_memo.gif\"*E*S/td*E*S/tr*E");
            sb.append("        *Str*E*Std style=\"padding:5px;font-size:20px;font-weight:bold;\"*E연락처, 성함 변경 부탁드립니다.*Sbr*E김미회-&gt;이재준 010-6396-0645*Sbr*E감사합니다*S/td*E*S/tr*E");
            sb.append("        *S/tbody*E*S/table*E");
            sb.append("    *S/td*E");
            sb.append("  *S/tr*E");
            sb.append("  *Str*E");
            sb.append("    *Std valign=\"bottom\" align=\"center\" style=\"border-right:0px;border-left:0px;\"*E");
            sb.append("      *Sbr*E예약과/프런트 담당자님!! 문제 발생시 언제든지 연락 주십시오.*Sbr*E");
            sb.append("      *Sh3*E※24시간 비상연락망 010-6536-2403*S/h3*E");
            sb.append("    *S/td*E");
            sb.append("  *S/tr*E");
            sb.append("  *Str*E*Std valign=\"bottom\"*E*Simg src=\"http://www.condo24.com/c/images/invoice/fax_bottom3.gif\" width=\"750\"*E*S/td*E*S/tr*E");
            sb.append("  *S/tbody*E*S/table*E");
            sb.append("*Sp style=\"font-weight:bold;\"*E* 구분란 확인요망(신규/변경/취소/대기/재전송/보류/반납)*Sbr*E * 주문번호로 중복확인 가능*S/p*E");
            sb.append("");
            sb.append("");
            sb.append("");
            sb.append("*Sstyle*Ebody{padding:0;margin:0;}*S/style*E");
            sb.append("*S/body*E*S/html*E");

            URL url = new URL("http://biz.moashot.com/EXT/URLASP/faxsendUTF.asp?uid=" + Constants.faxId + "&pwd=" + Constants.faxPwd
            + "&sendType=1&toNumber=02-6952-2353&fromNumber=" + Constants.faxId + "&contents=" + sb.toString() +"&rtnUrl=https://dmapi.condo24.com&nType="+3);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();



        } catch (Exception e) {
            e.printStackTrace();
        }

//        commonAcmMapper.getBookingInfo(intRsvID);
        return null;
    }

    /**
     * (schedule) 예약 팩스 전송
     * @return
     */
    public String faxAutoSend() {
        try{

            return "";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
