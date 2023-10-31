package com.example.stay.openMarket.common.service;

import com.example.stay.common.util.Constants;
import com.example.stay.common.util.MailService;
import com.example.stay.openMarket.common.dto.AccommDto;
import com.example.stay.openMarket.common.dto.RoomTypeDto;
import com.example.stay.openMarket.common.dto.StockDto;
import com.example.stay.openMarket.common.dto.ToconDto;
import com.example.stay.accommodation.hanwha.service.HanwhaService;
import com.example.stay.accommodation.kumho.service.SpavisService;
import com.example.stay.accommodation.sono.service.BookingService;
import com.example.stay.accommodation.yongpyong_beache.service.YPBService;
import com.example.stay.openMarket.common.dto.*;
import com.example.stay.openMarket.common.mapper.CommonMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Service
public class CommonService {

    @Autowired
    private CommonMapper commonMapper;

    //시설사 예약 API
    @Autowired private BookingService sonoBoking;
    @Autowired private com.example.stay.accommodation.resom.service.BookingService resomBoking;
    @Autowired private HanwhaService hanhwaBoking;
    @Autowired private com.example.stay.accommodation.kumho.service.BookingService kumhoBoking;
    @Autowired private com.example.stay.accommodation.wellihilli.service.BookingService weliBoking;
    @Autowired private YPBService YPBooking;
    @Autowired private com.example.stay.accommodation.elysian_gangchon.service.BookingService elysianBoking;
    @Autowired private SpavisService spavisBoking;

    //공급사 예약 API
    @Autowired private com.example.stay.accommodation.hotelPass.service.BookingService hpBooking;
    @Autowired private com.example.stay.accommodation.gpension.service.BookingService gpBooking;
    @Autowired private com.example.stay.accommodation.onda.service.BookingService ondaBooking;
    @Autowired private com.example.stay.accommodation.roomio.service.RoomioService rmoBooking;

    @Autowired private MailService mailService;



    // 시설 정보 가져오기 test 중
    public AccommDto getAcmInfo(int intAID, int intOmkIdx){

        // 시설 정보
        AccommDto accommDto = commonMapper.getAcmInfo(intAID, intOmkIdx);

        //System.out.println(strStockList);
        return accommDto;
    }

    
    // 사진 리스트
    public List<String> getPhotoList(int intAID, int intCnt){

        List<String> strPhotoList = commonMapper.getPhotoList(intAID, intCnt);
        
        return strPhotoList;
    }

    // 룸타입 리스트
    public List<RoomTypeDto> getroomTypeList(int intAID, int intOmkIdx){

        List<RoomTypeDto> roomTypeDtoList = commonMapper.getRoomList(intAID, intOmkIdx);

        return roomTypeDtoList;
    }

    // 재고 리스트
    public List<StockDto> getStockList(int intAID, int intOmkIdx, String strDate){
        List<StockDto> strStockList = commonMapper.getStockList(intAID, intOmkIdx, strDate);

        return strStockList;
    }

    // 재고 최소값 가져오기
    public int getMinPrice(int intAID, String strDate, int intOmkIdx){

        int intMinPrice = commonMapper.getMinPrice(intAID, strDate, intOmkIdx);

        return intMinPrice;
    }



    // 상세페이지 반환하기
    public String getOldDetailInfo(AccommDto accommDto, int intAID, int intOmkIdx){
        String result = "";
        if(accommDto.getStrOMKDetailInfo() != null && !accommDto.getStrOMKDetailInfo().equals("") && 1==2){ // 정보가 있을때

            result = accommDto.getStrOMKDetailInfo().replace("&#39;","\"").replace("&quot;","\"");
            System.out.println("strPdtDtlInfo 정보 보여주기 : " + System.currentTimeMillis());

        }else{ // 없을때 => update
            String[] imgList = accommDto.getStrACMPhotos().split("\\|");
            String strDescription = (accommDto.getStrDescription() != null)? accommDto.getStrDescription() : "";
            String strRsvGuide = (accommDto.getStrRsvGuide() != null)? accommDto.getStrRsvGuide() : "";
            String strFac = (accommDto.getStrFac() != null)? accommDto.getStrFac() : "";

            result +=
                    " <div style=\"width:860px\"> \n" +
                            "\t<div style=\"width:100%;\"><img src=\"http://gi.esmplus.com/condo24/a_notice_all.jpg\" /></div> \n" +
                            "\t<div style=\"width:100%;\"> \n" +
                            "\t\t\t<div style=\"width:100%;height:38px;\"><img src=\"http://www.condo24.com/_img/infoTitle.png\" style=\"width:100%;\"/></div> \n" +
                            "\t\t\t<div style=\"width:100%;height:auto;-border:1px red solid;text-align:center;margin-top:5px;\"><img src=\"http://www.condo24.com" + imgList[0] +"\" style=\"width:98%;\"/></div> \n" +
                            "\t \n" +
                            "\t \n" +
                            "\t\t\t<div style=\"width:100%;height:auto;-border:1px #dddddd solid;text-align:center;margin-top:5px;padding-bottom:10px;\"> \n" +
                            "\t\t\t\t<p style=\"font-size:200%;font-weight:bold;font-family:나눔고딕,NanumGothic,ng;\">" + accommDto.getStrSubject() + "</p> \n" +
                            "\t\t\t\t<p style=\"font-size:13px;font-weight:bold;color:#959595;font-family:나눔고딕,NanumGothic,ng;padding-left:30px;padding-right:30px;\"> \n" + strDescription.replace("\r\n","</br>") + " \t\t\t\t</p> \n" +
                            "\t\t\t</div> \n" +
                            "\t \n" +
                            "\t \n" +
                            "\t \n" +
                            "\t\t\t<Br/><br/> \n" +
                            "\t\t\t \n" +
                            "\t\t\t<div style=\"width:100%;text-align:left;\"> \n" +
                            "\t\t\t\t<img src=\"http://www.condo24.com/_img/photo.png\" /> \n" +
                            "\t\t\t</div> \n" +
                            "\t\t\t<div style=\"width:100%;text-align:center;border:1px #dddddd solid;height:auto;display:inline-block;padding:10px 0 10px 0;\">\t \n";

            for(int i=2;i<imgList.length;i++){
                result +=
                        "\t\t\t\t\t<div style=\"width:32%;float:left;margin-left:8px;margin-bottom:15px;\"><img src=\"http://www.condo24.com" + imgList[i] + "\" style=\"width:98%;height:170px;\" /></div> \n";
            }

            result +=
                    "\t\t\t</div> \n" +
                            "\t \n" +
                            "\t \n" +
                            "\t\t\t<div style=\"clear:both;\"></div> \n" +
                            "\t \n" +
                            "\t\t\t<Br/><br/> \n" +
                            "\t \n" +
                            "\t\t\t<div style=\"width:100%;text-align:left;\"> \n" +
                            "\t\t\t\t<img src=\"http://www.condo24.com/_img/type.png\" /> \n" +
                            "\t\t\t</div> \n" +
                            "\t\t\t<div style=\"width:100%;text-align:left;border:1px #dddddd solid;height:auto;padding-bottom:10px;min-height:50px;font-family:나눔고딕,NanumGothic,ng;padding-top:10px;padding-right:10px;\">\t \n" +
                            "\t \n" +
                            "\t \n" +
                            "\t \n";

            // roomType 정보 가져오기
            //List<ToconDto> toconDto = getroomType(intAID, strOmk);
            List<RoomTypeDto> roomTypeDtoList = getroomTypeList(intAID, intOmkIdx);
            for(RoomTypeDto dto : roomTypeDtoList){
                result +=
                        "\t\t\t\t<div style=\"width:100%;display:inline-block;padding:5px 5px 5px 10px;\"> \n" +
                                "\t\t\t\t\t<div style=\"width:25%;float:left;text-align:left;\"><span style=\"font-size:14px;font-weight:bold;font-family:나눔고딕,NanumGothic,ng;\">" + dto.getStrRmtypeName() + "</span></div> \n" +
                                "\t\t\t\t\t<div style=\"width:74%;float:right;text-align:left;\"><span style=\"font-size:14px;color:#626262;font-family:나눔고딕,NanumGothic,ng;\">" + dto.getStrShortDesc() + "</span></div> \n" +
                                "\t\t\t\t</div> \n";
            }

            result +=
                    "\t\t\t\t<div style=\"width:100%;text-align:left;padding:5px 5px 5px 10px;\"> \n" +
                            "\t\t\t\t\t<p style=\"color:#eb6100\">*패키지 상품은 날짜에 따라 미판매 될수 있습니다.</p> \n" +
                            "\t\t\t\t</div> \n" +
                            "\t\t\t</div> \n" +
                            "\t \n" +
                            "\t \n" +
                            "\t\t\t<Br/><br/> \n" +
                            "\t \n" +
                            "\t \n" +
                            "\t \n" +
                            "\t \n" +
                            "\t\t\t<div style=\"width:100%;text-align:left;\"> \n" +
                            "\t\t\t\t<img src=\"http://www.condo24.com/_img/Use01.png\" /> \n" +
                            "\t\t\t</div> \n" +
                            "\t\t\t<div style=\"width:100%;text-align:left;border:1px #dddddd solid;height:auto;padding-bottom:10px;min-height:50px;font-family:나눔고딕,NanumGothic,ng;padding-top:10px;padding-right:10px;\">\t \n" +
                            "\t \n<div style=\"width:100%;display:inline-block;padding:5px 5px 5px 10px;font-family:나눔고딕,NanumGothic,ng;\">" +
                            "\t\t\t\t\t" + strRsvGuide.replace("\r\n","</br>") + "\n" +
                            "\t \n</div>" +
                            "\t \n" +
                            "\t\t\t</div> \n" +
                            "\t \n" +
                            "\t \n" +
                            "\t \n" +
                            "\t\t\t<Br/><br/> \n" +
                            "\t \n" +
                            "\t \n" +
                            "\t \n" +
                            "\t \n" +
                            "\t\t\t<div style=\"width:100%;text-align:left;\"> \n" +
                            "\t\t\t\t<img src=\"http://www.condo24.com/_img/Use02.png\" /> \n" +
                            "\t\t\t</div> \n" +
                            "\t\t\t<div style=\"width:100%;text-align:left;border:1px #dddddd solid;height:auto;padding-bottom:10px;min-height:50px;font-family:나눔고딕,NanumGothic,ng;padding-top:10px;padding-right:10px;\">\t \n" +
                            "\t\t\t\t<div style=\"width:100%;display:inline-block;padding:5px 5px 5px 10px;font-family:나눔고딕,NanumGothic,ng;\"> \n" + strFac + "\t\t\t\t</div>\t\t\t \n" +
                            "\t\t\t</div> \n" +
                            "\t\t\t<br/><br/> \n" +
                            "\t\t\t<div style=\"width:100%;height:auto;-border:1px red solid;text-align:center;margin-top:5px;\"><img src=\"http://www.condo24.com" + imgList[1] + "\" style=\"width:80%;\"/></div> \n" +
                            "\t\t\t<br/><br/> \n" +
                            "\t\t\t<div style=\"width:100%;height:auto;-border:1px red solid;text-align:center;margin-top:5px;\"><img src=\"http://gi.esmplus.com/condo24/z_Information_dp02.jpg\" style=\"width:98%;\"/></div> \n" +
                            "\t\t</div> \n" +
                            "\t</div> \n" +
                            "\n" +
                            "\n";
            System.out.println("roomType 쿼리 조회해서 html 코드 작성 : " + System.currentTimeMillis());
        }
        return result;
    }

    // 상세페이지 new html
    public String getStrPdtDtlInfo(AccommDto accommDto, int intAID, int intOmkIdx){

        String result = "";

        String[] imgList = accommDto.getStrACMPhotos().split("\\|");
        String strDescription = (accommDto.getStrDescription() != null)? accommDto.getStrDescription() : "";
        String strRsvGuide = "";
        if(accommDto.getStrRsvGuide() != null){
            strRsvGuide = accommDto.getStrRsvGuide();
        }else{
            strRsvGuide =
                    "           <!-- //이용안내가 없는 경우 -->\n" +
                    "           <p style=\"font-size: 30px; paddign: 0; margin:0;\">[예약시 주의사항]</p>\n" +
                    "           - 객실배정은 당일날 프런트에서 입실순서대로 배정합니다(사전배정불가).<br/>\n" +
                    "           - 입실시간이 밤10시가 넘을 경우 프런트로 사전에 도착시간을 통보하시기 바랍니다.<br/>\n" +
                    "           - 예약번호가 한 개 이상이어도 연박으로 입실 가능합니다(단, 동일객실타입).<br/>\n" +
                    "           - 알릭톡으로 발송된 예약번호로 프런트에서 입실합니다.<br/>\n" +
                    "           - 미성년자의 입실은 보호자의 동의가 있으셔야 사용이 가능합니다.<br/>\n" +
                    "           - 미성년자의 혼숙으로 발생하는 입실거부에 대한 취소 혹은 환불은 절대 불가능합니다.<br/>\n" +
                    "           - 노쇼(NO-SHOW 사전통보 없이 숙소를 이용하지 않음)의 경우 100% 청구됩니다.<br/>\n" +
                    "           - 취소 및 변경은 근무시간 안에 요청하셔야 합니다(휴일불가).<br/>\n" +
                    "           - 객실별 기준인원을 확인하시기 바랍니다(정원초과 시 인원 추가비 필수).\n" +
                    "           <!-- 이용안내가 없는 경우// -->\n";
        }
        String strFac = (accommDto.getStrFac() != null)? accommDto.getStrFac() : "";

        result += "<div style=\"width: 860px; font-family: 'Noto Sans KR', 'Nanum Gothic', 'NanumGothic', 'gulim', 'dotum', sans-serif; padding: 0; margin: 0 auto;\">\n" +
                "   <div style=\"padding: 0; margin: 0;\">\n" +
                "       <img src=\"https://www.condo24.com/data/images/omk/txt_brand_story.png\" alt=\"BRAND STORY\" style=\"display: block; padding: 0; margin: 0;\"/>\n";

        // 첫번째 사진이 있을경우
        if(imgList[0].length() > 0){
            result +=
                    "      <div style=\"padding: 0; margin: 0;\">\n" +
                    "         <img src=\"https://www.condo24.com" + imgList[0] + "\" alt=\"앱지콘도 이미지 상단\" style=\"display: block; width: 100%; max-width: 100%; padding: 0; margin: 0;\"/>\n" +
                    "      </div>\n";
        }

        result +=
                "   </div>\n" +
                "   <div style=\"text-align: center; padding: 0; margin: 30px 0 0;\">\n" +
                "      <p style=\"color: #222; font-size: 35px; font-weight: bold; padding: 0; margin: 0;\">" + accommDto.getStrSubject() + "</p>\n";

        // 숙소 상세설명이 있을 경우
        if(strDescription.length() > 0){
            result +=
                    "      <div style=\"line-height: 1.5; color: #505050; font-size: 20px; padding: 0; margin: 0;\">\n" + strDescription.replace("\r\n","</br>") + "</div>\n";
        }

        result +=
                "   </div>\n" +
                "\n" +
                "   <div style=\"padding: 0; margin: 55px 0 0;\">\n" +
                "      <img src=\"https://www.condo24.com/data/images/omk/txt_photo_gallery.png\" alt=\"Photo Gallery\" style=\"display: block; padding: 0; margin: 0;\"/>\n" +
                "      <div style=\"display: flex; align-items: stretch; flex-wrap: wrap; padding: 5px; margin: 0;\">\n";
                for(int i=2;i<imgList.length;i++){
                    result +=
                            "<div style=\"width: calc(33.33% - 14px); width: -moz-calc(33.33% - 14px); margin: 0 7px 15px;\"><img style=\"display: block; max-width: 100%; padding: 0; margin: 0;\" alt=\"이미지 " + (i-1) + "\" src=\"https://www.condo24.com" + imgList[i] + "\"/></div>\n";
                }
                result +=
                "      </div>\n" +
                "   </div>\n" +
                "\n" +
                "   <!-- //객실옵션 -->\n" +
                "   <div style=\"padding: 0; margin: 55px 0 0;\">\n" +
                "      <img src=\"https://www.condo24.com/data/images/omk/txt_rm_info.png\" alt=\"객실 안내\" style=\"display: block; padding: 0; margin: 0;\"/>\n" +
                "      <div style=\"max-width: 800px; padding: 0; margin: 0 auto;\">\n" +
                "         <div style=\"padding: 0; margin: 55px 0 0; border-top: 1px solid #999;\">\n";

        // roomType 정보 가져오기
        //List<ToconDto> toconDto = getroomType(intAID, strOmk);
        List<RoomTypeDto> roomTypeDtoList = getroomTypeList(intAID, intOmkIdx);
        for(RoomTypeDto dto : roomTypeDtoList){
            String strShortDesc = (dto.getStrShortDesc() != null)? dto.getStrShortDesc() : "";
            result +=
                    "            <div style=\"display: flex; align-items: stretch; line-height: 1.5; color: #505050; font-size: 20px; padding: 0; margin: 0; overflow: hidden;\">\n" +
                    "               <div style=\"width: calc(33.75% - 30px); width: -moz-calc(33.75% - 30px); font-weight: bold; padding: 13px 10px 13px 20px; border-bottom: 1px solid #ddd; background: #f7f7f7;\">" + dto.getStrRmtypeName() + "</div>\n" +
                    "               <div style=\"width: calc(66.25% - 30px); width: -moz-calc(66.25% - 30px); word-break: break-all; padding: 13px 10px 13px 20px; border-bottom: 1px solid #ddd; background: #fff;\">" + strShortDesc + "</div>\n" +
                    "            </div>\n";
        }

                result +=
                "         </div>\n" +
                "      </div>\n" +
                "   </div>\n" +
                "   <!-- 객실옵션// -->\n";
        // 예약시 주의사항
        if(strRsvGuide.length() > 0){
            result +=
                    "   <div style=\"padding: 0; margin: 55px 0 0;\">\n" +
                    "      <img src=\"https://www.condo24.com/data/images/omk/txt_use_info.png\" alt=\"이용 안내\" style=\"display: block; padding: 0; margin: 0;\"/>\n" +
                    "      \n" +
                    "      <div style=\"line-height: 2.25; color: #505050; fot-size: 20px; letter-spacing: -0.05em;\">\n" +
                    strRsvGuide +
                    "      </div>\n" +
                    "   </div>\n";
        }

        // 부대시설
        if(strFac.length() > 0){
            result +=
                    "   <div style=\"padding: 0; margin: 55px 0 0;\">\n" +
                    "      <img src=\"https://www.condo24.com/data/images/omk/txt_add_fac.png\" alt=\"부대시설 이용 안내\" style=\"display: block; padding: 0; margin: 0;\"/>\n" +
                    "      <div style=\"line-height: 2.25; color: #505050; fot-size: 20px; letter-spacing: -0.05em;\">\n" +
                    strFac +
                    "      </div>\n" +
                    "   </div>\n";
        }

        result +=
                "   <div style=\"padding: 0; margin: 30px 0 0;\">\n" +
                "      <img src=\"https://www.condo24.com" + imgList[1] + "\" alt=\"앱지콘도 이미지 하단\" style=\"display: block; width: 80%; max-width: 80%; padding: 0; margin: 15px auto 0;\"/>\n" +
                "   </div>\n" +
                "</div>";

        return result;
    }

    /**
     * @param intRsvID : 예약번호
     * @param rsvState : 변경코자 하는 예약상태
     */
    @Async
    public void rsvAuto(String intRsvID, HttpServletRequest httpServletRequest){
        /*
        TODO 예약 생성시 시설/공급사여부 => 예약생성(RSV_STAY_EM_NUM 기록) => 팩스,이메일
        ACCOMM 테이블에서 strType이 C면 시설 나머지는 공급사
        모든 API 호출시 로그 저장
         */

        Map<String, Object> typeFlag = commonMapper.getTypeCode(intRsvID);//플래그값 받아오기 시설일때를위해서 카테고리값까지
        String rsvResult = ""; //예약결과 저장용
        RsvStayDto rsvStayDto = commonMapper.getBookingInfo(Integer.parseInt(intRsvID));
        String rsvState = rsvStayDto.getStrStatusCode();
        //예약
        if(rsvState.equals("0")){
            if(!typeFlag.containsKey("strCateCode")){
                //공급사 예약
                createBookingSupplier(intRsvID, typeFlag.get("strApiFlag").toString(), httpServletRequest);
            }else {
                //시설사 예약
                createBookingAccomm(intRsvID, typeFlag.get("strCateCode").toString(), httpServletRequest);
            }

        } else if (rsvState.equals("5")) {
            if(!typeFlag.containsKey("strCateCode")){
                //공급사 예약
                cancelBookingSupplier(intRsvID, typeFlag.get("strApiFlag").toString(), httpServletRequest);
            }else {
                //시설사 예약
                cancelBookingAccomm(intRsvID, typeFlag.get("strCateCode").toString(), httpServletRequest);
            }

        }




    }

    /**
     *
     * @param intRsvID : 예약번호
     * @param accommCateCode : 시설분류코드 (CODE_SYSTEM)
     * @return
     */
    public void createBookingAccomm(String intRsvID, String accommCateCode, HttpServletRequest httpServletRequest) {
        /*
        소노 01
        리솜 RE
        용평 37
        비체 38
        금호 04
        한화 02
        엘리시안 49
        스파비스 35
        웰리힐리 18
         */
        String result = "";
        try{
            switch (accommCateCode){
                case "01":
                    //소노예약 소노는 패키지예약 룸온리 예약이 다르니 분기처리 해줘라!
                    result = sonoBoking.createBooking("json", intRsvID, "", httpServletRequest);
                    break;
                case "RE":
                    //리솜예약
                    result = resomBoking.createBooking("json", intRsvID, "", httpServletRequest);
                    break;
                case "37":
                    //용평예약
                    result = YPBooking.booking(Integer.parseInt(intRsvID), "json");
                    break;
                case "48":
                    //비체예약
                    result = YPBooking.booking(Integer.parseInt(intRsvID), "json");
                    break;
                case "04":
                    //금호예약
                    result = kumhoBoking.createBooking("json", Integer.parseInt(intRsvID), httpServletRequest);
                    break;
                case "02":
                    //한화예약
                    result = hanhwaBoking.booking(Integer.parseInt(intRsvID), "json");
                    break;
                case "49":
                    //엘리시안
                    result = elysianBoking.createBooking("json", Integer.parseInt(intRsvID), httpServletRequest);
                    break;
                case "35":
                    //스파비스
                    result = spavisBoking.orderTicket("json", httpServletRequest, Integer.parseInt(intRsvID));
                    break;
                case "18":
                    //웰리힐리
                    result = weliBoking.createBooking("json", Integer.parseInt(intRsvID), httpServletRequest);
                    break;
            }
            System.out.println(result);
            JSONParser jsonParser = new JSONParser();
            JSONObject responseJson = (JSONObject) jsonParser.parse(result);
            String rsvResult = responseJson.get("code").toString();
            if(rsvResult.equals("200")){
                //예약 성공했으니 RSV_STAY, RSV_STAY_RM_NUM 기록해주고 팩스,이메일
                commonMapper.updateRsv(intRsvID);
            } else {
                //예약 실패 실패했다는거 저장하고 알리고 끝맺음

            }

            //TODO 팩스, 이메일 DTO만들어서 안에다가 집어넣고 보낼수만 있으면 됨 두개 따로 빈을 만들던가 합쳐서 만들던가 해야함 ACCOMM_AUTO_FAX
            if(!rsvResult.equals("200")){
                createInform(intRsvID);
            }else{

            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void createBookingSupplier (String intRsvID, String typeSupplier, HttpServletRequest httpServletRequest){
        /*
        호텔패스 -- 얘는 실시간이라 안해도될지...도?
        지펜션
        루미오
        온다
        호텔스토리

         */
        switch (typeSupplier){
            case "HP":
                //호텔패스
//                hpBooking.createBooking()
                break;
            case "RMO":
                //루미오
                rmoBooking.booking(Integer.parseInt(intRsvID));
                break;
            case "OND":
                //온다
                ondaBooking.createBooking("json", Integer.parseInt(intRsvID), httpServletRequest);
                break;
            case "GP":
                //지펜션
                gpBooking.createBooking("json", intRsvID);
                break;
        }
        String result = "";

    }

    public void cancelBookingSupplier(String intRsvID, String typeSupplier, HttpServletRequest httpServletRequest){

        switch (typeSupplier){
            case "HP":
                //호텔패스
                hpBooking.cancelBooking("json", intRsvID);
                break;
            case "RMO":
                //루미오
                rmoBooking.bookingCancel();
                break;
            case "OND":
                //온다
                ondaBooking.cancelBooking("json", Integer.parseInt(intRsvID), httpServletRequest);
                break;
            case "GP":
                //지펜션
                gpBooking.cancelBooking("json", intRsvID);
                break;
        }
    }

    public void cancelBookingAccomm(String intRsvID, String accommCateCode, HttpServletRequest httpServletRequest){
        String result="";
        try {

            switch (accommCateCode){
                case "01":
                    //소노예약 소노는 패키지예약 룸온리 예약이 다르니 분기처리 해줘라!
                    result = sonoBoking.cancelBooking("json", intRsvID, httpServletRequest);
                    break;
                case "RE":
                    //리솜예약
                    result = resomBoking.cancelBooking("json", intRsvID);
                    break;
                case "37":
                    //용평예약
                    result = YPBooking.bookingCancel(Integer.parseInt(intRsvID), "json");
                    break;
                case "48":
                    //비체예약
                    result = YPBooking.bookingCancel(Integer.parseInt(intRsvID), "json");
                    break;
                case "04":
                    //금호예약
                    result = kumhoBoking.cancelBooking("json", Integer.parseInt(intRsvID), httpServletRequest);
                    break;
                case "02":
                    //한화예약
                    result = hanhwaBoking.bookingCancel(Integer.parseInt(intRsvID), "json");
                    break;
                case "49":
                    //엘리시안
                    result = elysianBoking.cancelBooking("json", Integer.parseInt(intRsvID), httpServletRequest);
                    break;
                case "35":
                    //TODO 스파비스 티켓번호 요구하는데 확인 필요
//                    result = spavisBoking.cancelTicket("json", httpServletRequest, Integer.parseInt(intRsvID));
                    break;
                case "18":
                    //웰리힐리
                    result = weliBoking.cancelBooking("json", Integer.parseInt(intRsvID), httpServletRequest);
                    break;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void createInform(String intRsvID){
        //상품명, 투숙일자, 박, 객실수, 평형(룸타입), 이용자명, 이용자연락처, 구분(변경, 예약, 취소), 예약번호, 입금가, 주문번호(시설측), 내용(변경시 변경내용), 시설담당자, 시설명, 시설팩스번호

        RsvStayDto rsvStayDto = commonMapper.getBookingInfo(Integer.parseInt(intRsvID));
        //ACCOMM_CONTACT intAID로 조회해서 strRsvMailYn=Y 인 경우에만 메일 발송
        String mailYn = commonMapper.getMailYn(String.valueOf(rsvStayDto.getIntAID()));
        //TEST 배포시 삭제
        mailYn="Y";
        String rsvStatus = rsvStayDto.getStrStatusCode();
        if(mailYn.equals("Y")){

            StringBuffer sb = new StringBuffer();
            sb.append("안녕하세요. (주)동무해피데이즈 여행사입니다.\r\n");
            if (rsvStatus.equals("0")){
                //예약
                sb.append("예약 건 확인 부탁드립니다.\r\n");
            }else if(rsvStatus.equals("5")){
                //에약취소
                sb.append("예약 취소 부탁드립니다.\r\n");
                sb.append("객실예약번호 : "+rsvStayDto.getStrRsvRmNum());
            }

            sb.append("숙소명 : "+rsvStayDto.getStrRmtypeName()+"\r\n");
            sb.append("룸타입 : "+rsvStayDto.getStrRmtypeName()+"\r\n");
            sb.append("입실일 : " + rsvStayDto.getDateCheckIn() + " ~ " + rsvStayDto.getDateCheckOut() + "/ 1박 / "+rsvStayDto.getIntRmCnt()+"실\r\n");
            sb.append("입금가 : "+rsvStayDto.getMoneyCost()+"원\r\n");
            if (rsvStayDto.getIntAID()==10015){
                //휘닉스호텔앤리조트 판매가
                sb.append("입금가 : "+rsvStayDto.getMoneySales()+"원\r\n");
            }
            sb.append("이용자 : "+rsvStayDto.getStrRcvName()+" / "+rsvStayDto.getStrRcvPhone()+"\r\n");
            sb.append(" \r\n");
            sb.append(" \r\n");
            if(!(rsvStayDto.getStrRemark() ==null)){
                sb.append("-- 별도 요청사항 --\r\n");
                sb.append(rsvStayDto.getStrRemark());
            }


            mailService.sendEmail(sb.toString(), rsvStatus);
        }


    }
}
