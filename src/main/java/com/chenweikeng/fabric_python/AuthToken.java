package com.chenweikeng.fabric_python;

import java.security.SecureRandom;
import java.util.Random;

public class AuthToken {
	private static final String symbols = "ABCDEFGJKLMNPRSTUVWXYZ0123456789";
	private static Random random = new SecureRandom();

	public static String randomAuthToken() {
		char[] buf = new char[8];
		for (int idx = 0; idx < buf.length; ++idx)
			buf[idx] = symbols.charAt(random.nextInt(symbols.length()));
		return new String(buf);
	}
}
