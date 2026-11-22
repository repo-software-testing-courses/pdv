package com.trackorjargh.javaclass;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class RandomGenerate {
	public String getRandomString(int length) {
		String[] symbols = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
		StringBuilder sb = new StringBuilder(length);

		try {
			Random random = SecureRandom.getInstanceStrong();

			for (int i = 0; i < length; i++) {
				int indexRandom = random.nextInt(symbols.length);
				sb.append(symbols[indexRandom]);
			}
		} catch (NoSuchAlgorithmException exception) {
			exception.printStackTrace();
		}

		return sb.toString();
	}
}
