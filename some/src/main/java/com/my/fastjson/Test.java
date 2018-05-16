package com.my.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public class Test {

    @org.junit.Test
    public void mapTest() {
        JSONObject json = new JSONObject();
        json.put("key", "value");
        json.put("int", 1);
        Map<String, Object> map = JSON.toJavaObject(json, Map.class);
        System.out.println(map.get("int"));
    }

}
