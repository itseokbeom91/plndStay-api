package com.example.stay.openMarket.gmarket.hmac;

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
        try {
            JSONObject header = new JSONObject();
            header.put("alg", "HS256");
            header.put("typ", "JWT");
            header.put("kid", Constants.gmkEsmMasterID);

            JSONObject payload = new JSONObject();
            payload.put("iss", "www.condo24.com");
            payload.put("sub", strDomain);
//            payload.put("sub", "sell");
            payload.put("aud", "sa.esmplus.com");
//            payload.put("iat", System.currentTimeMillis()/1000);
//            payload.put("ssi", "A:condo24auc, G:condo24gmk");
            payload.put("ssi", "G:condo24gmk");

            String strHeader = Base64Encoder.encode(header.toString().getBytes(StandardCharsets.UTF_8));
            String strPayload = Base64Encoder.encode(payload.toString().getBytes(StandardCharsets.UTF_8));

            System.out.println("strHeader : " + strHeader);
            System.out.println("strPayload : " + strPayload);
            String message = strHeader + "." + strPayload;

            SecretKeySpec signingKey = new SecretKeySpec(Constants.gmk_secret_key.getBytes(Charset.forName("UTF-8")), Constants.HMAC_SHA_256);
            Mac mac = Mac.getInstance(Constants.HMAC_SHA_256);
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(message.getBytes(Charset.forName("UTF-8")));
            String signature = Hex.encodeHexString(rawHmac);

            System.out.println("signature : " + signature);


        }catch (Exception e){
            e.printStackTrace();
        }

        String result = "";
        System.out.println("result : " + result);
        return result;
    }
}
