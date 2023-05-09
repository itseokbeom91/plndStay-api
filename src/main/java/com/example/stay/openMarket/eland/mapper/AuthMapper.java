package com.example.stay.openMarket.eland.mapper;

import org.springframework.stereotype.Repository;

@Repository
public interface AuthMapper {

    // 발급받은 AccessToken DB에 INSERT
    int insertAccessToken (String token);

}
