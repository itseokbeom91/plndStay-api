package com.example.stay.accommodation.kumho.mapper;

import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@ResponseBody
public interface SpavisMapper {

    List<String> couponList();

    int updateCouponDates(String datePurchase, String dateExpired, String ticketNo);
}
