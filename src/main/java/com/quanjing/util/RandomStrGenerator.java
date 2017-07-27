package com.quanjing.util;

import java.util.Random;

public class RandomStrGenerator {

	public static final char[] chars = new char[62];

	static {
		for (int i = 0; i < 10; i++) {
			chars[i] = (char) (48 + i);
		}

		for (int i = 0; i < 26; i++) {
			chars[10 + i] = (char) (65 + i);
		}

		for (int i = 0; i < 26; i++) {
			chars[36 + i] = (char) (97 + i);
		}
	}

	public static String generate(int length) {
		Random r = new Random();
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append(chars[r.nextInt(62)]);
		}

		return sb.toString();
	}

	public static void main(String[] args) {
		for (int i = 0; i < 1000; i++) {
			String r = generate(31);
			System.out.println(r.substring(30));
		}
	}
}
