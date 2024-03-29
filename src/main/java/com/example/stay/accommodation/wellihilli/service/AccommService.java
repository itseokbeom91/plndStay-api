package com.example.stay.accommodation.wellihilli.service;

import com.example.stay.accommodation.wellihilli.mapper.WellihilliMapper;
import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import com.example.stay.common.util.UrlResourceDownloader;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service("wellihilli.AccommService")
public class AccommService {

    @Autowired
    private WellihilliMapper wellihilliMapper;

    CommonFunction commonFunction = new CommonFunction();

    // TODO : 웰리힐리에서 주는 객실 정보에 우리가 팔지 않는 객실타입이 포함되어 있을 수 있음 -> 어떻게 구분하지?
    // 객실 정보 조회
    public String getRmtypeInfo(String dataType, HttpServletRequest httpServletRequest){
        String statusCode = "200";
        String message = "";

        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
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

                    strRoomDatas += strIngYn + "|^|" + intQuanMax + "|^|" + strSubject + "|^|" +
                                    strDescription + "|^|" + strRmtypeID + "|^|" + strRmImgDatas + "{{|}}";
                }
                strRoomDatas = strRoomDatas.substring(0, strRoomDatas.length()-5);

                String updateResult = wellihilliMapper.updateRmtype(strRoomDatas);
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
