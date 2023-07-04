package com.example.stay.accommodation.hotelStory.controller;

import com.example.stay.accommodation.hotelStory.mapper.HotelStoryMapper;
import com.example.stay.accommodation.hotelStory.service.APIHotelstoryService;
import com.example.stay.common.util.XmlUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/hotelStory/*")
public class APIHotelStoryController {

    @Autowired
    private APIHotelstoryService apiHotelstoryService;


    /**
     * 시설 / 룸타입 / ratePlan 정보 insert
     * @param strAccommID
     * @return API 호출 리스트
     */
    @GetMapping("/callapi")
    public void callApi(String strAccommID) {

        apiHotelstoryService.getAccomm(strAccommID);

    }

    /**
     * 웹훅으로 xml 받아 DB에 적용시키기
     * @param strXml
     * @return
     */
    @GetMapping("/webhook")
    public void getWebHook(String strXml){

        apiHotelstoryService.getWebhook(strXml);

    }

    @GetMapping("/booking")
    public void getBooking(int intBookingID){

        apiHotelstoryService.booking(intBookingID);

    }

    @GetMapping("/bookingCheck")
    public void checkBooking(int intBookingID){

        apiHotelstoryService.bookingCheck(intBookingID);

    }

    @GetMapping("/bookingCancel")
    public void cancelBooking(int intBookingID){

        apiHotelstoryService.bookingCancel(intBookingID);

    }



}
