package me.sosedik.utilizer.util;

import org.jspecify.annotations.NullMarked;

import java.util.TreeMap;

@NullMarked
public class RomanNumerals {

	private RomanNumerals() {
		throw new IllegalStateException("Utility class");
	}

	private static final TreeMap<Integer, String> map = new TreeMap<>();

	static {
		map.put(1000, "M");
		map.put(900, "CM");
		map.put(500, "D");
		map.put(400, "CD");
		map.put(100, "C");
		map.put(90, "XC");
		map.put(50, "L");
		map.put(40, "XL");
		map.put(10, "X");
		map.put(9, "IX");
		map.put(5, "V");
		map.put(4, "IV");
		map.put(1, "I");
		map.put(Integer.MIN_VALUE, "");
	}

	/**
	 * Turns number into roman number
	 *
	 * @param number number
	 * @return roman number
	 */
	public static String toRoman(int number) {
		int l = map.floorKey(number);
		if (number == l)
			return map.get(number);
		return map.get(l) + toRoman(number - l);
	}

}
