package com.example.stay.accommodation.wellihilli.service;

import com.example.stay.accommodation.wellihilli.mapper.AccommMapper;
import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import com.example.stay.common.util.UrlResourceDownloader;
import com.fasterxml.jackson.databind.JsonNode;
import okhttp3.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("wellihilli.AccommService")
public class AccommService {

    @Autowired
    private AccommMapper accommMapper;

    CommonFunction commonFunction = new CommonFunction();


    // 객실 정보 조회
    public String insertRoomType(String dataType, HttpServletRequest httpServletRequest){
        String statusCode = "200";
        String message = "";

        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(), System.currentTimeMillis());
        try{
            String strUrl = Constants.whpUrl + ":8070/api/facilities/condo_room/list";
            String method = "GET";

            JsonNode jsonNode = commonFunction.callJsonApi("", "", new JSONObject(), strUrl, method);
            String code = jsonNode.get("status").toString();

            String strRoomDatas = "";
            if(code.equals("200")){
                JSONArray jsonArray = (JSONArray) new JSONParser().parse(jsonNode.get("data").toString());
                for(Object object : jsonArray){
                    JSONObject jsonObject = (JSONObject) JSONValue.parse(object.toString());

                    /**
                     * TODO : 웰리힐리 intAID 확인
                     */
                    int intAID = 11450;

                    String strIngYn = jsonObject.get("expsYn").toString();

                    String prsn = jsonObject.get("prsn").toString();
                    prsn = prsn.substring(0, prsn.length()-1);
                    int intQuanMax = Integer.parseInt(prsn);

                    String strSubject = jsonObject.get("nm").toString();
                    String strDescription = jsonObject.get("descPc").toString();
                    String strRmtypeID = jsonObject.get("id").toString();

                    // 이미지------------------------------------------------------------------------------------------------
                    String strRmImgDatas = "";
                    JSONArray rmListImgArr = (JSONArray) jsonObject.get("condoRoomListImages");
                    JSONArray rmDetailImgArr = (JSONArray) jsonObject.get("condoRoomDetailImages");
                    for(Object rmListImg : rmListImgArr){
                        JSONObject imgObject = (JSONObject) rmListImg;
                        String strImgName = imgObject.get("nm").toString();
                        strRmImgDatas += rmPhotoContentsReg(strImgName, strRmtypeID);
                    }
                    for(Object rmDetailImg : rmDetailImgArr){
                        JSONObject imgObject = (JSONObject) rmDetailImg;
                        String strImgName = imgObject.get("nm").toString();
                        strRmImgDatas += rmPhotoContentsReg(strImgName, strRmtypeID);
                    }
                    strRmImgDatas = strRmImgDatas.substring(0, strRmImgDatas.length()-5);

                    strRoomDatas += strIngYn + "|^|" + intAID + "|^|" + intQuanMax + "|^|" + strSubject + "|^|" +
                                    strDescription + "|^|" + strRmtypeID + "|^|" + strRmImgDatas + "{{|}}";
                }
                strRoomDatas = strRoomDatas.substring(0, strRoomDatas.length()-5);

                String updateResult = accommMapper.updateRmtype(strRoomDatas);
                if(updateResult.equals("")){
                    message = "객실 등록 및 수정 완료";
                }else{
                    message = updateResult;
                }
            }else{
                message = "객실 등록 및 수정 실패 - api 응답 코드 : " + code;
            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 재고 및 예약가능여부 조회
    public String updateGoods(String dataType, HttpServletRequest httpServletRequest, int intRmIdx, String startDate, String endDate){
        String statusCode  = "200";
        String message = "";
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(), System.currentTimeMillis());

        try{
            String strUrl = Constants.whpUrl + ":8070/api/vapi/reservation/calendar?s_vendor_code=" + Constants.whpVendorCode +
                            "&sresrm=C&s_arrday=" + startDate + "&s_today=" + endDate;
            String method = "GET";

            JsonNode jsonNode = commonFunction.callJsonApi("", "", new JSONObject(), strUrl, method);
            String code = jsonNode.get("status").toString();

            if(code.equals("200")){
                String rmtypeID = accommMapper.getStrRmtypeID(intRmIdx);

                String strStockDatas = "";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                JSONArray jsonArray = (JSONArray) new JSONParser().parse(jsonNode.get("data").toString());
                for(Object object : jsonArray){
                    JSONObject jsonObject = (JSONObject) JSONValue.parse(object.toString());

                    String strPyung = jsonObject.get("pyung").toString();
                    String roomTypeID = jsonObject.get("roomType").toString();

                    String strRmtypeID = strPyung + roomTypeID;

                    // 웰리힐리는 날짜 보내면 전체 객실타입의 재고를 주기 때문에 가져오고자하는 객실의 재고 데이터만 뽑아서 저장
                    if(strRmtypeID.equals(rmtypeID)){
                        int intStock = Integer.parseInt(jsonObject.get("vcCount").toString());
                        if(intStock < 0){
                            intStock = 0;
                        }
                        int intOmkStock = intStock;

                        int intCost = 0;
                        int intSales = 0;
                        if(jsonObject.get("roompay") != null){
                            intCost = Integer.parseInt(jsonObject.get("roompay").toString());
                            intSales = intCost;
                        }

                        String yearday = jsonObject.get("yearday").toString();
                        Date yearDate = sdf.parse(yearday);
                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                        String strDateSales = sdf2.format(yearDate);

                        double doubleOmkSales = 0;

                        int year = Integer.parseInt(strDateSales.substring(0, 4));
                        int month = Integer.parseInt(strDateSales.substring(5, 7));
                        int day = Integer.parseInt(strDateSales.substring(8,10));

                        LocalDate date = LocalDate.of(year, month, day);
                        DayOfWeek dayOfWeek = date.getDayOfWeek();

                        /**
                         * 임시
                         */
                        double weekday = 1.09; // 일~목
                        double friday = 1.09; // 금
                        double saturday = 1.1; // 토

                        if(dayOfWeek.getValue() == 7 || dayOfWeek.getValue() == 1 || dayOfWeek.getValue() == 2 ||
                                dayOfWeek.getValue() == 3 || dayOfWeek.getValue() == 4){
                            doubleOmkSales = intSales * weekday;
                        }else if(dayOfWeek.getValue() == 5){
                            doubleOmkSales = intSales * friday;
                        }else if(dayOfWeek.getValue() == 6){
                            doubleOmkSales = intSales * saturday;
                        }

                        int intExtraA = 0;
                        int intExtraC= 0;
                        int intExtraB = 0;

                        strStockDatas +=strRmtypeID + "|^|" + strDateSales + "|^|" + intStock + "|^|" + intCost + "|^|" + intSales + "|^|"
                                + intExtraA + "|^|" + intExtraC + "|^|" + intExtraB + "|^|" + intOmkStock + "|^|"  + doubleOmkSales+ "{{|}}";
                    }
                }

                if(strStockDatas.length() > 0){
                    strStockDatas = strStockDatas.substring(0, strStockDatas.length()-5);

                    String result = accommMapper.updateGoods(strStockDatas);
                    String strResult = result.substring(result.length()-4);
                    if(strResult.equals("저장완료")){
                        message = "재고 등록/수정 완료";
                    }else{
                        logWriter.add(result);
                        message = "재고 등록/수정 실패";
                    }
                }else{
                    message = "재고 등록/수정 실패";
                }

            }else{
                message = "재고 등록/수정 실패 - api 응답 코드 : " + code;
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            statusCode = "500";
            message = "재고 수정 및 등록 실패";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return commonFunction.makeReturn(dataType, statusCode, message);
    }


    // CONTENTS_PHOTO, RM_PHOTO에 INSERT
    public String rmPhotoContentsReg(String strImgName, String strRmtypeID){
        String strRmPhotoContent = "";
        try{
            /**
             * 임시로 하드코딩
             */
            int intCreatedSID = 148; // 이미지 생성한사람 148 : 손유정(employ테이블)
            int intModifiedSID = 148; // 이미지 수정한사람

            // 경로에 폴더 생성 -> 있으면 생성 안시킴
            Path directoryPath = Paths.get(Constants.whpFileDir + "\\" + strRmtypeID + "\\"); // PC에 저장할 디렉토리
            String filePath = Constants.whpFileDir + "\\" + strRmtypeID + "\\" + strImgName; // PC에 저장할 경로 + 파일명
            String strFilePath = "/wellihilli/" + strRmtypeID + "/";
            Files.createDirectories(directoryPath);

            // 파일 존재여부 체크
            File file = new File(filePath);
            if(!(file.exists())){
                // 이미지 저장
                UrlResourceDownloader downloader = new UrlResourceDownloader(filePath, Constants.whpImgUrl + URLEncoder.encode(strImgName,"UTF-8").replaceAll("\\+","%20"));
                downloader.urlFileDownload();
            }else{
                System.out.println("ALREADY EXISTS PHOTO");
            }

            strRmPhotoContent = strFilePath + "|~|" + strImgName + "|~|" + intCreatedSID + "|~|"
                    + intModifiedSID + "{{~}}";

        }catch (Exception e){
            e.printStackTrace();
        }
        return strRmPhotoContent;
    }

}
