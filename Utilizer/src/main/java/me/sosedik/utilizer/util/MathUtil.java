package me.sosedik.utilizer.util;

import java.math.BigDecimal;

public class MathUtil {

	private MathUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Returns the absolute decimal part of the number
	 *
	 * @param value current value
	 * @return decimal part of the value
	 */
	public static double getDecimalPartAbs(double value) {
		return Math.abs(getDecimalPart(value));
	}

	/**
	 * Returns the decimal part of the number
	 *
	 * @param value current value
	 * @return decimal part of the value
	 */
	public static double getDecimalPart(double value) {
		var bigDecimal = new BigDecimal(String.valueOf(value));
		return bigDecimal.subtract(new BigDecimal(bigDecimal.intValue())).doubleValue();
	}

}
