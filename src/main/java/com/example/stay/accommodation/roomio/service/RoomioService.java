package com.example.stay.accommodation.roomio.service;

import com.example.stay.accommodation.roomio.mapper.RoomioMapper;
import com.example.stay.common.util.CommonFunction;
import com.example.stay.openMarket.common.dto.RsvStayDto;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class RoomioService {
    
    @Autowired
    private RoomioMapper roomioMapper;

    CommonFunction commonFunction = new CommonFunction();


    /**
     * 시설 목록조회
     * @return
     */
    public String getAccomm(String dataType){

        String statusCode = "200";
        String message = "";
        String result = "";

        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("m","getList");
            jsonObject.put("cd","7634");
            jsonObject.put("status","Y");

            JsonNode jsonNode = commonFunction.callJsonApi("roomio","", jsonObject, "http://api.roomio.co.kr/", "POST");

            JSONArray jsonArray = (JSONArray) new JSONParser().parse(jsonNode.get("list").toString());

            for(Object object : jsonArray){

                JSONObject forObject = (JSONObject) new JSONParser().parse(object.toString());
                if(!forObject.get("hotel_id").equals("4699") // 엘도라도 리조트만 사용(추후 다른 시설 생길 수 있을 가능성에 재배포 않기 위함)
                    && !forObject.get("hotel_id").equals("4704")
                    && !forObject.get("hotel_id").equals("4705")
                    && !forObject.get("hotel_id").equals("4706")
                    && !forObject.get("hotel_id").equals("5973")
                ){
                    System.out.println(forObject.toJSONString());
                    String strHotelId = forObject.get("hotel_id").toString();
                    String strHotelName = forObject.get("name").toString();

                    // 룸 정보 가져오기
                    JSONObject roomObject = (JSONObject) new JSONParser().parse(getRoom(strHotelId));
                    String strRoomDatas = roomObject.get("result").toString();

                    String strResult = roomioMapper.insertAccomm(strHotelId, strHotelName, strRoomDatas);
                    System.out.println(strResult);
                    result = forObject.toJSONString();
                }
            }
//            System.out.println(result);

        }catch (Exception e){
            message = " 실패";
            statusCode = "500";
            e.printStackTrace();
        }

        return commonFunction.makeReturn(dataType, statusCode, message, result);
    }


    /**
     * 객실 목록 조회 - 시설 조회 안에 삽입
     * @param strHotelId
     * @return
     */
    public String getRoom(String strHotelId){

        String statusCode = "200";
        String message = "";
        String result = "";

        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("m","getRoomList");
            jsonObject.put("cd","7634");
            jsonObject.put("hotel_id",strHotelId);

            JsonNode jsonNode = commonFunction.callJsonApi("roomio","", jsonObject, "http://api.roomio.co.kr/", "POST");

            JSONArray listArray = (JSONArray) new JSONParser().parse(jsonNode.get("list").toString());

            // 데이터 담기
            String resultDatas = "";

            // 리스트 반복
            for (Object object : listArray){

                JSONObject listObject = (JSONObject) new JSONParser().parse(object.toString());


                String strRoomId = listObject.get("room_id").toString();
                String strRoomName = listObject.get("room_name").toString();
                String strRoomState = listObject.get("room_state").toString();

                String strPkgDatas = "";

                System.out.println(strRoomId);

                // 패키지 정보
                JSONArray pkgArray = (JSONArray) new JSONParser().parse(listObject.get("pkg").toString());
                for(Object pkgObject : pkgArray){

                    JSONObject inJsonObject = (JSONObject) new JSONParser().parse(pkgObject.toString());

                    String strPkgState = inJsonObject.get("pkg_state").toString();
                    String strPkgCode = inJsonObject.get("pkg_code").toString();
                    String strPkgName = inJsonObject.get("pkg_name").toString();
                    String strPkgDesc = inJsonObject.get("pkg_desc").toString();

                    strPkgDatas += strPkgCode + "|^|" + strPkgName + "|^|" + strPkgDesc + "|^|" + strPkgState + "{{^}}";


                }

                if(strPkgDatas.length() > 1){
                    strPkgDatas = strPkgDatas.substring(0, strPkgDatas.length()-5);
                }

                resultDatas += strRoomId + "|~|" + strRoomName + "|~|" + strRoomState + "|~|" + strPkgDatas + "{{~}}";


            }

            if(resultDatas.length() > 1){
                resultDatas = resultDatas.substring(0, resultDatas.length()-5);
            }

            result = resultDatas;
            System.out.println(resultDatas);

        }catch (Exception e){
            message = " 실패";
            statusCode = "500";
            e.printStackTrace();
        }

        return commonFunction.makeReturn("json", statusCode, message, result);
    }


    /**
     * 예약가능 상태 조회
     * @return
     */
    public String bookingState(String strHotelId, String strRoomId, String strStartDate, String strEndDate){

        String statusCode = "200";
        String message = "";
        String result = "";

        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("m","getRoomState");
            jsonObject.put("cd","7634");
            jsonObject.put("hotel_id",strHotelId);
            jsonObject.put("room_id",strRoomId);
            jsonObject.put("st_date",strStartDate);
            jsonObject.put("ed_date",strEndDate);

            JsonNode jsonNode = commonFunction.callJsonApi("roomio","", jsonObject, "http://api.roomio.co.kr/", "POST");

            System.out.println(jsonNode);

        }catch (Exception e){
            message = " 실패";
            statusCode = "500";
            e.printStackTrace();
        }

        return commonFunction.makeReturn("json", statusCode, message);
    }

    /**
     * 일자별 가격조회
     * @param strHotelId
     * @param strRoomId
     * @param strStartDate
     * @param strEndDate
     * @return
     */
    public String getStock(int intAID, int intRmIdx, String strStartDate, String strEndDate, String dataType){

        String statusCode = "200";
        String message = "";
        String result = "";

        try {

            String strHotelId = roomioMapper.getHotelId(intAID);
            String strRoomId = roomioMapper.getRoomId(intRmIdx);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("m","getRoomPrice");
            jsonObject.put("cd","7634");
            jsonObject.put("hotel_id",strHotelId);
            jsonObject.put("room_id",strRoomId);
            jsonObject.put("st_date",strStartDate);
            jsonObject.put("ed_date",strEndDate);

            JsonNode jsonNode = commonFunction.callJsonApi("roomio","", jsonObject, "http://api.roomio.co.kr/", "POST");

            String errorCode = jsonNode.get("error").toString();

            // 0 : 성공
            if(errorCode.equals("0")){
                JSONArray listArray = (JSONArray) new JSONParser().parse(jsonNode.get("list").toString());

                String strStockDatas = "";

                for (Object object : listArray){

                    JSONObject listObject = (JSONObject) new JSONParser().parse(object.toString());

                    String strRoomType = listObject.get("room_type").toString(); // 1:룸온리, 2:패키지
                    String strPkgCode = listObject.get("pkg_code").toString();
                    String strCheckIndate = listObject.get("check_in").toString();
                    String strSeason = listObject.get("season").toString(); // 1:비수기, 2:준성수기, 3:성수기, 4:극성수기
                    String strPrice = listObject.get("a_price").toString();
                    String strRoomCnt = listObject.get("room_cnt").toString();
                    String strAddAdult = listObject.get("add_adult").toString();
                    String strAddChild = listObject.get("add_child").toString();
                    String strAddBaby = listObject.get("add_baby").toString();
                    String strFreeCancel = listObject.get("cancel_able").toString(); // 무료취소여부 1:입실 1일전 무료취소, 2:입실 2일전 무료취소
                    String strRoomState = listObject.get("room_state").toString(); // ??
                    String strSPrice = listObject.get("s_price").toString(); // ??

                    // 날짜 포맷
                    String date = strCheckIndate.trim();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                    Date dateDate = dateFormat.parse(date);
                    SimpleDateFormat formDate = new SimpleDateFormat("yyyy-MM-dd");
                    strCheckIndate = formDate.format(dateDate);

                    if(strRoomType.equals("1")){

                        strStockDatas += "roomOnly|^|" + strCheckIndate + "|^|" + strRoomCnt + "|^|" + strPrice + "|^|" + strAddAdult + "|^|" + strAddChild + "|^|" + strAddBaby  + "{{^}}";
                    }else{

                        strStockDatas += strPkgCode + "|^|" + strCheckIndate + "|^|" + strRoomCnt + "|^|" + strPrice + "|^|" + strAddAdult + "|^|" + strAddChild + "|^|" + strAddBaby + "{{^}}";
                    }

                }

                if (strStockDatas.length() > 1) {
                    strStockDatas = strStockDatas.substring(0, strStockDatas.length() - 5);
                }
                System.out.println(strStockDatas);

                String procResult = roomioMapper.insertStock(intAID, intRmIdx, strStockDatas);

                if (procResult.equals("저장완료")) {
                    message = "재고 등록 및 수정 완료";
                } else {
                    message = " 재고 등록 및 수정 실패";
                }


            }else{
                System.out.println("fail");
            }

            System.out.println(message);


        }catch (Exception e){
            message = " 실패";
            statusCode = "500";
            e.printStackTrace();
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    public String booking(int intRsvID){

        String statusCode = "200";
        String message = "";
        String result = "";

        try {

            RsvStayDto rsvStayDto = roomioMapper.getRsvInfo(intRsvID);

            System.out.println(rsvStayDto);

            String strHotelId = "";
            String strRoomId = "";
            String strPrice = "";
            String strRoomType = "";
            String strPkgCode = "";
            String strAddAdult = "";
            String strAddChild = "";
            String strAddBaby = "";
            String strAddPrice = "";
            String strAddCnt = "";
            String strCheckIn = "";
            String strCheckOut = "";
            String strName = "";
            String strPhone = "";
            String strEmail = "";
            String strRoomCnt = "";
            String strMemo = "";
            String strOptionName = "";
            String strOptionPrice = "";
            String strOptionCode = "";
            String strOptionCnt = "";

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("m","setRevReady");
            jsonObject.put("cd","7634");
            jsonObject.put("hotel_id", strHotelId);
            jsonObject.put("room_id", strRoomId);
            jsonObject.put("rev_no", intRsvID);
            jsonObject.put("s_price", strPrice);
            jsonObject.put("room_type", strRoomType);
            jsonObject.put("pkg_code", strPkgCode);
            jsonObject.put("add_adult", strAddAdult);
            jsonObject.put("add_child", strAddChild);
            jsonObject.put("add_baby", strAddBaby);
            jsonObject.put("add_man", strAddPrice);
            jsonObject.put("add_num", strAddCnt);
            jsonObject.put("check_in", strCheckIn);
            jsonObject.put("check_out", strCheckOut);
            jsonObject.put("user_name", strName);
            jsonObject.put("user_hp", strPhone);
            jsonObject.put("user_email", strEmail);
            jsonObject.put("room_cnt", strRoomCnt);
            jsonObject.put("rev_memo", strMemo);

            JSONObject optionObject = new JSONObject();
            optionObject.put("opt_name", strOptionName);
            optionObject.put("opt_price", strOptionPrice);
            optionObject.put("opt_code", strOptionCode);
            optionObject.put("opt_cnt", strOptionCnt);

            jsonObject.put("option", optionObject);


//            JsonNode jsonNode = commonFunction.callJsonApi("roomio","", jsonObject, "http://api.roomio.co.kr/", "POST");

//            System.out.println(jsonNode);

        }catch (Exception e){
            message = " 실패";
            statusCode = "500";
            e.printStackTrace();
        }

        return commonFunction.makeReturn("json", statusCode, message);
    }

    public String bookingInfo(){

        String statusCode = "200";
        String message = "";
        String result = "";

        try {

        }catch (Exception e){
            message = " 실패";
            statusCode = "500";
            e.printStackTrace();
        }

        return commonFunction.makeReturn("json", statusCode, message);
    }

    public String bookingList(){

        String statusCode = "200";
        String message = "";
        String result = "";

        try {

        }catch (Exception e){
            message = " 실패";
            statusCode = "500";
            e.printStackTrace();
        }

        return commonFunction.makeReturn("json", statusCode, message);
    }

    public String bookingCancel(){

        String statusCode = "200";
        String message = "";
        String result = "";

        try {

        }catch (Exception e){
            message = " 실패";
            statusCode = "500";
            e.printStackTrace();
        }

        return commonFunction.makeReturn("json", statusCode, message);
    }
    
}
