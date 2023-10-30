package com.example.stay.openMarket.gmarket.GmkUtil;

import com.example.stay.common.util.Base64Encoder;
import com.example.stay.common.util.Constants;
import org.apache.commons.codec.binary.Hex;
import org.json.simple.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.TimeZone;

public class HmacGenerater {
    public static String generate(String strDomain){
        String token = "";
        try {
            JSONObject header = new JSONObject();
            header.put("alg", "HS256");
            header.put("typ", "JWT");
            header.put("kid", Constants.gmkEsmMasterID);

            JSONObject payload = new JSONObject();
            payload.put("iss", "www.condo24.com");
            payload.put("sub", strDomain);
            payload.put("aud", "sa.esmplus.com");
            payload.put("ssi", "G:" + Constants.gmkEsmMasterID);

            String strHeader = Base64.getEncoder().withoutPadding().encodeToString(header.toJSONString().getBytes());
            String strPayload = Base64.getEncoder().withoutPadding().encodeToString(payload.toJSONString().getBytes());

            String message = strHeader + "." + strPayload;

            SecretKeySpec signingKey = new SecretKeySpec(Constants.gmk_secret_key.getBytes(), Constants.HMAC_SHA_256);
            Mac mac = Mac.getInstance(Constants.HMAC_SHA_256);
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(message.getBytes());
            String hash = Base64.getEncoder().encodeToString(rawHmac);
            String signature = hash.substring(0, hash.length()-1);

            token = "Bearer " + strHeader + "." + strPayload + "." + signature;

            System.out.println("token : " + token);

        }catch (Exception e){
            e.printStackTrace();
        }
        return token;
    }
}
