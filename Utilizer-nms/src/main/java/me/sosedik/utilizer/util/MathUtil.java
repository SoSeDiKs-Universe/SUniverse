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
		var bigDecimal = BigDecimal.valueOf(value);
		return bigDecimal.subtract(new BigDecimal(bigDecimal.intValue())).doubleValue();
	}

	/**
	 * Rounds value to the specified precision
	 *
	 * @param value value
	 * @param precision precision
	 * @return rounded value
	 */
	public static double round(double value, int precision) {
		int scale = (int) Math.pow(10, precision);
		return (double) Math.round(value * scale) / scale;
	}

	/**
	 * Gets a random int value withing the specified range
	 *
	 * @param min min value
	 * @param max max value
	 * @return a random int value withing the specified range
	 */
	public static int getRandomIntInRange(int min, int max) {
		if (min == max) return min;
		return min + RANDOM.nextInt(max - min);
	}

	/**
	 * Gets a random double value withing the specified range
	 *
	 * @param min min value
	 * @param max max value
	 * @return a random double value withing the specified range
	 */
	public static double getRandomDoubleInRange(double min, double max) {
		return min + (max - min) * RANDOM.nextDouble();
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

	/**
	 * Gets the exp needed to obtain the level from 0
	 *
	 *
	 * @param level level
	 * @return exp
	 * @see <a href=http://minecraft.wiki/Experience#Leveling_up>Experience#Leveling_up</a>
	 */
	public static int getExpForLevel(int level) {
		if (level < 1) return 0;
		if (level < 17) return level * level + 6 * level;
		if (level < 32) return (int) (2.5 * level * level - 40.5 * level + 360);
		return (int) (4.5 * level * level - 162.5 * level + 2220);
	}

}
