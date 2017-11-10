package com.my.fastjson;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class SortTest {
	public static void main(String[] args) {
		JSONObject json = new JSONObject();
		for(int i=0;i<10;i++) {
			json.put("c" + i, "ccc");
			json.put("a" + i, "aaa");
			json.put("b" + i, "bbb");
		}
		json.put("c", "ccc");
		json.put("a", "aaa");
		json.put("b", "bbb");
		System.out.println(JSONObject.toJSONString(json));
		System.out.println(JSONObject.toJSONString(json, SerializerFeature.MapSortField));
		
	}
}
