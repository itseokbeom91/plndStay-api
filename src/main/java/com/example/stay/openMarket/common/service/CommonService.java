package com.example.stay.openMarket.common.service;

import com.example.stay.openMarket.common.dto.AccommDto;
import com.example.stay.openMarket.common.dto.RoomTypeDto;
import com.example.stay.openMarket.common.dto.StockDto;
import com.example.stay.openMarket.common.dto.ToconDto;
import com.example.stay.openMarket.common.mapper.CommonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CommonService {

    @Autowired
    private CommonMapper commonMapper;

    // 시설 정보 가져오기 test 중
    public AccommDto getAcmInfo(int intAID, int intOmkIdx){

        // 시설 정보
        AccommDto accommDto = commonMapper.getAcmInfo(intAID, intOmkIdx);

        //System.out.println(strStockList);
        return accommDto;
    }

    
    // 사진 리스트
    public List<String> getPhotoList(int intAID){

        List<String> strPhotoList = commonMapper.getPhotoList(intAID);
        
        return strPhotoList;
    }

    // 룸타입 리스트
    public List<RoomTypeDto> getroomTypeList(int intAID, int intOmkIdx){

        List<RoomTypeDto> roomTypeDtoList = commonMapper.getRoomList(intAID, intOmkIdx);

        return roomTypeDtoList;
    }

    // 재고 리스트
    public List<StockDto> getStockList(int intAID, int intOmkIdx, String strDate){
        List<StockDto> strStockList = commonMapper.getStockList(intAID, intOmkIdx, "20230701");

        return strStockList;
    }



    // 상세페이지 반환하기
    public String getStrPdtDtlInfo(AccommDto accommDto, int intAID, int intOmkIdx){
        String result = "";
        if(accommDto.getStrOMKDetailInfo() != null && !accommDto.getStrOMKDetailInfo().equals("")){ // 정보가 있을때

            result = accommDto.getStrOMKDetailInfo().replace("&#39;","\"").replace("&quot;","\"");
            System.out.println("strPdtDtlInfo 정보 보여주기 : " + System.currentTimeMillis());

        }else{ // 없을때 => update
            String[] imgList = accommDto.getStrACMPhotos().split("\\|");
            String strAround = (accommDto.getStrAround() != null)? accommDto.getStrAround() : "";

            result +=
                    " <div style=\"width:860px\"> \n" +
                            "\t<div style=\"width:100%;\"><img src=\"http://gi.esmplus.com/condo24/a_notice_all.jpg\" /></div> \n" +
                            "\t<div style=\"width:100%;\"> \n" +
                            "\t\t\t<div style=\"width:100%;height:38px;\"><img src=\"http://www.condo24.com/_img/infoTitle.png\" style=\"width:100%;\"/></div> \n" +
                            "\t\t\t<div style=\"width:100%;height:auto;-border:1px red solid;text-align:center;margin-top:5px;\"><img src=\"http://www.condo24.com/conphoto/" + imgList[0] +"\" style=\"width:98%;\"/></div> \n" +
                            "\t \n" +
                            "\t \n" +
                            "\t\t\t<div style=\"width:100%;height:auto;-border:1px #dddddd solid;text-align:center;margin-top:5px;padding-bottom:10px;\"> \n" +
                            "\t\t\t\t<p style=\"font-size:200%;font-weight:bold;font-family:나눔고딕,NanumGothic,ng;\">" + accommDto.getStrSubject() + "</p> \n" +
                            "\t\t\t\t<p style=\"font-size:13px;font-weight:bold;color:#959595;font-family:나눔고딕,NanumGothic,ng;padding-left:30px;padding-right:30px;\"> \n" + strAround + " \t\t\t\t</p> \n" +
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
            //List<ToconDto> toconDto = getroomType(intAID, strOmk);
            List<RoomTypeDto> roomTypeDtoList = getroomTypeList(intAID, intOmkIdx);
            for(RoomTypeDto dto : roomTypeDtoList){
                result +=
                        "\t\t\t\t<div style=\"width:100%;display:inline-block;padding:5px 5px 5px 10px;\"> \n" +
                                "\t\t\t\t\t<div style=\"width:25%;float:left;text-align:left;\"><span style=\"font-size:14px;font-weight:bold;font-family:나눔고딕,NanumGothic,ng;\">" + dto.getStrSubject() + "</span></div> \n" +
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
                            "\t \n" +
                            "\t\t\t\t\t" + accommDto.getStrDescription().replace("\r\n","<br>") + "\n" +
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
                            "\t\t\t\t<div style=\"width:100%;display:inline-block;padding:5px 5px 5px 10px;font-family:나눔고딕,NanumGothic,ng;\"> \n" + accommDto.getStrTraffic().replace("\r\n","<br>") + "\t\t\t\t</div>\t\t\t \n" +
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
}
