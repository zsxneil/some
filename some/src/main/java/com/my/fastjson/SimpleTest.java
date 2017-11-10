package com.my.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class SimpleTest {
	public static void main(String[] args) {
		Group group = new Group();
		group.setId(1L);
		group.setName("group");
		System.out.println(JSON.toJSONString(group));
		
		User admin = new User();
		admin.setId(2L);
		admin.setName("admin");
		
		System.out.println(JSON.toJSONString(admin));
		
		User guest = new User();
		guest.setId(3L);
		guest.setName("guest");
		
		group.addUser(admin);
		group.addUser(guest);
		
		String json = JSON.toJSONString(group);
		System.out.println(json);
		
		group = JSON.parseObject(json, Group.class);
		System.out.println(group);
		JSONObject jsonObject = JSON.parseObject(json);
		System.out.println("jsonObject" + jsonObject);
		group = JSON.toJavaObject(jsonObject, Group.class);
		System.out.println(group);
		
		JSONObject obj = new JSONObject();
		obj.put("key", "value");
		System.out.println(obj);
		
		JSONArray array = new JSONArray();
		array.add(admin);
		array.add(guest);
		System.out.println(array);
		
		
		
	}
}
