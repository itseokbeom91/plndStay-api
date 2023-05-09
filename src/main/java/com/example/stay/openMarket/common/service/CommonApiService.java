package com.example.stay.openMarket.common.service;

import com.example.stay.openMarket.common.dto.CancelInfoDto;
import com.example.stay.openMarket.common.dto.CondoDto;
import com.example.stay.openMarket.common.dto.ToconDto;
import com.example.stay.openMarket.common.mapper.CommonApiMapper;
import com.example.stay.common.util.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

@Service
public class CommonApiService {

    @Autowired
    private CommonApiMapper commonApiMapper;

    // con_id로 정보 가져오기
    public CondoDto getInfo(int intNum, String strOmk){
        CondoDto condoDto = commonApiMapper.getInfo(intNum, strOmk);
        return condoDto;
    }

    // con_id로 roomType 정보 가져오기
    public List<ToconDto> getroomType(int intNum, String strOmk){
        List<ToconDto> toconDto = commonApiMapper.getroomType(intNum, strOmk);
        return toconDto;
    }

    // con_id로 메인사진 10장 리스트 가져오기
    public List<String> getMainPhotoList(int intNum){
        List<String> photoList = commonApiMapper.getPhotoList(intNum);
        int size = (photoList.size() < 10)? photoList.size() : 10;
        List<String> resultList = new ArrayList<>(photoList.subList(0,size));

        return resultList;
    }

    // 취소규정 생성
    public String getCancelInfo(int intNum){
        List<CancelInfoDto> cancelInfoDto = commonApiMapper.getCancelInfo(intNum);

        String cancelPolicyNotice = "";

        // strCnFlag 우선순위 1. site_ps / site_of 2. ps / of 3. cid = '0'으로 조회한 기본 취소규정
        List<CancelInfoDto> refundRates = new LinkedList<>();
        try {
            for(int i = 0; i< cancelInfoDto.size(); i++){
                // 1번이 있는 경우 strCnFlag가 site_인 것만 남기기
                if(cancelInfoDto.get(i).getStrCnFlag().matches("site_.*")){
                    if(cancelInfoDto.get(i).getStrCnFlag().contains("ps") || cancelInfoDto.get(i).getStrCnFlag().contains("of")){
                        cancelInfoDto.remove(i);
                        i -= 1 ;
                    }
                }
            }
        } catch (Exception e){ // 우선순위 1, 2번이 없을 경우
            e.printStackTrace();
            cancelInfoDto = commonApiMapper.getCancelInfo(0); // 기본 취소규정 불러오기
        }

        // 위약금이 있는 날짜+1로 '위약금 없음' 을 만들기 위해 카운트
        int psCnt = 0;
        int ofCnt = 0;
        for(int i = 0; i< cancelInfoDto.size(); i++){
            if(cancelInfoDto.get(i).getStrCnFlag().contains("ps")){
                psCnt += 1;
            }else if(cancelInfoDto.get(i).getStrCnFlag().contains("of")){
                ofCnt += 1;
            }
        }

        String ofCancelInfo = ""; // 비수기 취소규정
        String psCancelInfo = ""; // 성수기 취소규정

        int psCount = psCnt;
        int ofCount = ofCnt;
        // 비수기 취소규정 만들기
        for(int i = cancelInfoDto.size()-1; i>=0; i--){
            if(cancelInfoDto.get(i).getStrCnFlag().contains("of")){
//                System.out.println(cancelInfoDto.get(i));
                if(cancelInfoDto.get(i).getIntCnDcnt() == 0){
                    ofCancelInfo += "- 입실당일취소/No-show 시 : 위약금 100% \n";
                }else{
                    if(ofCnt == ofCount){
                        ofCancelInfo += "- 기준일 " + (cancelInfoDto.get(i).getIntCnDcnt()+1) + "일 전 : 위약금 없음 \n";
                    }
                    ofCancelInfo += "- 기준일 " + cancelInfoDto.get(i).getIntCnDcnt() + "일 전 : 위약금" + cancelInfoDto.get(i).getIntCnPer() + "% \n";
                }
                ofCount -= 1;
            } // 성수기 취소규정 만들기
            else if(cancelInfoDto.get(i).getStrCnFlag().contains("ps")){
//                System.out.println(cancelInfoDto.get(i));
                if(cancelInfoDto.get(i).getIntCnDcnt() == 0){
                    psCancelInfo += "- 입실당일취소/No-show 시 : 위약금 100% \n";
                }else{
                    if(psCnt == psCount){
                        psCancelInfo += "- 기준일 " + (cancelInfoDto.get(i).getIntCnDcnt()+1) + "일 전 : 위약금 없음 \n";
                    }
                    psCancelInfo += "- 기준일 " + cancelInfoDto.get(i).getIntCnDcnt() + "일 전 : 위약금" + cancelInfoDto.get(i).getIntCnPer() + "% \n";
                }
                psCount += 1;
            }
        }

        // Default 취소규정 생성
        cancelPolicyNotice =
                "☎ 문의전화 1588-0134 / 평일 09:00~17:00 \n" +
                        "취소수수료 안내 \n" +
                        "* 예약 확정 후 취소, 변경 신청 시 위약금이 발생합니다. \n" +
                        "위약금은 성수기, 비수기 등에 따라 다르므로 주의하시기 바랍니다. \n" +
                        "\n 비수기 - 입실일 기준 \n \n" +
                        ofCancelInfo +
                        "\n 성수기 및 연휴 - 입실일 기준 \n \n" +
                        psCancelInfo  +
                        "\n취소 수수료는 예약일기준이 아닌 체크인(숙박일) 날짜 기준으로 발생됩니다. \n" +
                        "업체 확인 후 취소가 완료됩니다. \n" +
                        "영업일(주말, 토/공휴일 제외) 17시 기준으로 취소 위약금이 부과됩니다. \n";

        return cancelPolicyNotice;
    }


    // 상세페이지 반환하기
    public String getStrPdtDtlInfo(CondoDto condoDto, int intNum, String strOmk){
        String result = "";
        if(condoDto.getStrPdtDtlInfo() != null && !condoDto.getStrPdtDtlInfo().equals("")){ // 정보가 있을때

            result = condoDto.getStrPdtDtlInfo().replace("&#39;","\"").replace("&quot;","\"");
            System.out.println("strPdtDtlInfo 정보 보여주기 : " + System.currentTimeMillis());

        }else{ // 없을때 => update
            String[] imgList = condoDto.getStrCondoPhotos().split("\\|");
            String strSummary = (condoDto.getStrSummary() != null)? condoDto.getStrSummary() : "";

            result +=
                    " <div style=\"width:860px\"> \n" +
                            "\t<div style=\"width:100%;\"><img src=\"http://gi.esmplus.com/condo24/a_notice_all.jpg\" /></div> \n" +
                            "\t<div style=\"width:100%;\"> \n" +
                            "\t\t\t<div style=\"width:100%;height:38px;\"><img src=\"http://www.condo24.com/_img/infoTitle.png\" style=\"width:100%;\"/></div> \n" +
                            "\t\t\t<div style=\"width:100%;height:auto;-border:1px red solid;text-align:center;margin-top:5px;\"><img src=\"http://www.condo24.com/conphoto/" + imgList[0] +"\" style=\"width:98%;\"/></div> \n" +
                            "\t \n" +
                            "\t \n" +
                            "\t\t\t<div style=\"width:100%;height:auto;-border:1px #dddddd solid;text-align:center;margin-top:5px;padding-bottom:10px;\"> \n" +
                            "\t\t\t\t<p style=\"font-size:200%;font-weight:bold;font-family:나눔고딕,NanumGothic,ng;\">" + condoDto.getStrAcmName() + "</p> \n" +
                            "\t\t\t\t<p style=\"font-size:13px;font-weight:bold;color:#959595;font-family:나눔고딕,NanumGothic,ng;padding-left:30px;padding-right:30px;\"> \n" + strSummary + " \t\t\t\t</p> \n" +
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
                        "\t\t\t\t\t<div style=\"width:32%;float:left;margin-left:8px;margin-bottom:15px;\"><img src=\"http://www.condo24.com/conphoto/" + imgList[i] + "\" style=\"width:98%;height:170px;\" /></div> \n";
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
            List<ToconDto> toconDto = getroomType(intNum, strOmk);
            for(ToconDto dto : toconDto){
                result +=
                        "\t\t\t\t<div style=\"width:100%;display:inline-block;padding:5px 5px 5px 10px;\"> \n" +
                                "\t\t\t\t\t<div style=\"width:25%;float:left;text-align:left;\"><span style=\"font-size:14px;font-weight:bold;font-family:나눔고딕,NanumGothic,ng;\">" + dto.getTocode() + "</span></div> \n" +
                                "\t\t\t\t\t<div style=\"width:74%;float:right;text-align:left;\"><span style=\"font-size:14px;color:#626262;font-family:나눔고딕,NanumGothic,ng;\">" + dto.getTocodeText() + "</span></div> \n" +
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
                            "\t \n" +
                            "\t\t\t\t\t" + condoDto.getStrDescription().replace("\r\n","<br>") + "\n" +
                            "\t \n" +
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
                            "\t\t\t\t<div style=\"width:100%;display:inline-block;padding:5px 5px 5px 10px;font-family:나눔고딕,NanumGothic,ng;\"> \n" + condoDto.getStrFacilities().replace("\r\n","<br>") + "\t\t\t\t</div>\t\t\t \n" +
                            "\t\t\t</div> \n" +
                            "\t\t\t<br/><br/> \n" +
                            "\t\t\t<div style=\"width:100%;height:auto;-border:1px red solid;text-align:center;margin-top:5px;\"><img src=\"http://www.condo24.com/conphoto/" + imgList[1] + "\" style=\"width:80%;\"/></div> \n" +
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

    // json API 호출
    public JsonNode callJsonApi(String strItemId, String strOmk, String strType, JSONObject object) throws Exception{

        // API 호출 정보
        URL url = new URL(returnUrl(strItemId, strOmk, strType));
//            URL url = new URL("https://eapi.ssgadm.com/item/0.4/insertItem.ssg");

        // API 호출
        String response = ConnectionApi(url, strOmk, strType, object);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Content-Type", "application/json");
//            conn.setRequestProperty("Accept", "application/json");
//            conn.setRequestProperty("Accept-Charset", "UTF-8");
//            conn.setRequestProperty("Authorization", Constants.SsgAuthorization);
//            conn.setDoOutput(true);
//
//            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
//            writer.write(mainObject.toJSONString());
//            writer.close();

        // JSON 파싱
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response);

        return jsonNode;
    }

    // url 반환
    public String returnUrl(String strItemId, String strOmk, String strType){
        String result = "";

        try{
            if(strOmk.equals("SSG")){
                if(strType.equals("getInfo")){
                    result = "https://eapi.ssgadm.com/item/0.4/viewItem.ssg?itemId=" + strItemId;
                }else if(strType.equals("update")){
                    result = "https://eapi.ssgadm.com/item/0.4/updateItem.ssg";
                }else if(strType.equals("insert")){
                    result = "https://eapi.ssgadm.com/item/0.4/insertItem.ssg";
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    // API 호출
    public String ConnectionApi(URL url, String strOmk, String strType, JSONObject object){
        String result = "";

        try {

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("Authorization", returnAuthorization(strOmk));
            conn.setDoOutput(true);

            if(strOmk.equals("SSG") && (strType.equals("update")) || strType.equals("insert")){
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                writer.write(object.toJSONString());
                writer.close();
            }

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                Scanner scanner = new Scanner(conn.getInputStream());
                result = scanner.useDelimiter("\n").next();
            }else{
                result = conn.getResponseMessage();
            }

            conn.disconnect();

        } catch (Exception e){
            e.printStackTrace();
        }

        return result;

    }

    // Authorization 반환
    public String returnAuthorization(String strOmk){
        String result = "";

        try {
            if(strOmk.equals("SSG")){
                result = Constants.SsgAuthorization;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }



}
