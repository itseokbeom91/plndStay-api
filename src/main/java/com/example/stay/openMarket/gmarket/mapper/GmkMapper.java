package com.example.stay.openMarket.gmarket.mapper;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface GmkMapper {
//    List<Map<String, String>> getBrandCodeList();
    Map<String, String> getCategories(int intAID);
}
