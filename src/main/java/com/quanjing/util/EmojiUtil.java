package com.quanjing.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author
 *
 */
public class EmojiUtil {

	/**
	 * 检测是否有emoji表情
	 *
	 * @param source
	 * @return
	 */
	public static boolean isContainsEmoji(String source) {
		if (StringUtils.isNullOrEmpty(source)) {
			return false;
		}
		boolean flag = false;
		Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
				Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
		Matcher emojiMatcher = emoji.matcher(source);
		if (emojiMatcher.find()) {
			flag = true;
		}
		return flag;
	}

	public static void main(String[] args) {
		boolean flag = EmojiUtil.isContainsEmoji("额(⊙o⊙)…");
		System.out.println(flag);
	}
}
