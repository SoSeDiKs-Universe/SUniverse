package me.sosedik.utilizer.util;

import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@NullMarked
public class MathUtil {

	private MathUtil() {
		throw new IllegalStateException("Utility class");
	}

	private static final Random RANDOM = new Random();

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

	/**
	 * Returns random value from provided array
	 *
	 * @param values values array
	 * @param <T>    value type
	 * @return randomly picked value
	 */
	@SafeVarargs
	public static <T> @UnknownNullability T getRandom(@Nullable T... values) {
		return values[RANDOM.nextInt(values.length)];
	}

	/**
	 * Returns random value from provided collection
	 *
	 * @param values values collection
	 * @param <T>    value type
	 * @return randomly picked value
	 */
	@SuppressWarnings("unchecked")
	public static <T> @UnknownNullability T getRandom(Collection<? extends @Nullable T> values) {
		if (values.isEmpty()) return null;

		int index = RANDOM.nextInt(values.size());
		if (values instanceof List<?> list) {
			return (T) list.get(index);
		} else {
			Iterator<? extends T> iterator = values.iterator();
			for (int i = 0; i < index; i++)
				iterator.next();
			return iterator.next();
		}
	}

}
