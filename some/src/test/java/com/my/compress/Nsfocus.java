package com.my.compress;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Administrator on 2017/11/23.
 */
public class Nsfocus {


    public static final String apiKey = "f7aff24c16";
    public static final String apiSecret = "KJUIHnjHUHlkhUHkjlplijjjKImhUIHlHUhugyGyftrDeseswS678m==";
    public static final int apiId = 101;

    public static final String API_KEY = "api_key";
    public static final String API_SECRET = "api_secret";
    public static final String API_ID = "api_id";
    public static final String TIMESTRAP = "timestrap";

    public static void main(String[] args) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(API_KEY,apiKey);
        jsonObject.put(API_SECRET,apiSecret);
        jsonObject.put(API_ID,apiId);
        jsonObject.put(TIMESTRAP,System.currentTimeMillis() / 1000);
        jsonObject.put("p1","123");
        jsonObject.put("p2","abc");

        String json = JSONObject.toJSONString(jsonObject, SerializerFeature.MapSortField);
        System.out.println(json);


    }
}
