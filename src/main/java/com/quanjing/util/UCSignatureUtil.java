package com.quanjing.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户中心，根据参数生成校验码
 *
 */
public class UCSignatureUtil {
	private static final String SECRETKEY_KEY = "secretKey";

	public static final String SIGNATURE_KEY = "signature";

	public static final String SIGNATURE_SYS = "sys";

	/**
	 * 根据参数生成校验码
	 * 
	 * @param valueMap
	 *            所有参数
	 * @param secret
	 *            密钥
	 * @return
	 */
	public static String getLocalSignature(Map<String, String[]> valueMap, String secret) {
		StringBuffer sb = new StringBuffer();
		List<String> keyList = new ArrayList<String>(valueMap.keySet());
		// 对参数的key进行排序
		Collections.sort(keyList);
		// 将参数拼成param=value&param2=value2的形式
		for (String key : keyList) {
			// 去掉map中的校验码
			if (SIGNATURE_KEY.equals(key)) {
				continue;
			}
			String[] values = valueMap.get(key);
			sb.append(key + "=");
			if (values.length > 0) {
				for (String str : values) {
					sb.append(str).append(",");
				}
				sb.deleteCharAt(sb.length() - 1);
			}
			sb.append("&");
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		// 对拼成的字符串md5
		String valueMd5 = EncryptUtils.baseMd5Encode(sb.toString());
		// 加上密钥
		valueMd5 += "&" + SECRETKEY_KEY + "=" + secret;
		// 再取md5值
		valueMd5 = EncryptUtils.baseMd5Encode(valueMd5);
		return valueMd5;
	}

	/**
	 * 根据参数生成校验码
	 * 
	 * @param valueMap
	 *            所有参数
	 * @param secret
	 *            对应的密钥
	 * @return
	 */
	public static String getSignature(Map valueMap, String secret) {
		Map<String, String[]> result = new HashMap();
		// 将字符串参数值转成字符串数组
		for (Object key : valueMap.keySet()) {
			if (key != null) {
				result.put((String) key, new String[] { (String) valueMap.get(key) });
			}
		}
		return getLocalSignature(result, secret);
	}
}
