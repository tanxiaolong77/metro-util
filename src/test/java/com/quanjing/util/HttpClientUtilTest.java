package com.quanjing.util;

import com.quanjing.util.HttpClientUtil;

public class HttpClientUtilTest {

	public static void main(String[] args) {
		String response=HttpClientUtil.sendHttpRequest("get", "http://localhost:8080/qj-api/rcUser/list", null,"utf-8",3000);
		System.out.println(response);
	}

}
