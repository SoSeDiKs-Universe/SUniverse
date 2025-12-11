package me.sosedik.utilizer.util;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.util.RGBLike;
import org.bukkit.DyeColor;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@NullMarked
public class MiscUtil {

	private MiscUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Gets the closest dye color to the provided one
	 *
	 * @param color color
	 * @return dye color
	 */
	public static DyeColor closestTo(RGBLike color) {
		double distance = Double.MAX_VALUE;
		DyeColor result = DyeColor.WHITE;
		for (DyeColor dyeColor : DyeColor.values()) {
			double dis = calcColorDistance(color, dyeColor);
			if (dis < distance) {
				distance = dis;
				result = dyeColor;
			}
		}
		return result;
	}

	/**
	 * Calculates the distance between two colors
	 *
	 * @param c1 first color
	 * @param c2 second color
	 * @return distance
	 */
	// https://stackoverflow.com/questions/6334311/whats-the-best-way-to-round-a-color-object-to-the-nearest-color-constant/6334454#6334454
	public static double calcColorDistance(RGBLike c1, RGBLike c2) {
		int red1 = c1.red(), red2 = c2.red(), rmean = (red1 + red2) >> 1, r = red1 - red2, g = c1.green() - c2.green(), b = c1.blue() - c2.blue();
		return Math.sqrt((((512 + rmean) * r * r) >> 8) + 4 * g * g + (((767 - rmean) * b * b) >> 8));
	}

	/**
	 * Gets the dye color from the item
	 *
	 * @param material item type
	 * @param type string to check for
	 * @return dye color
	 */
	public static DyeColor getDyeColor(Material material, String type) {
		return parseOr(material.name().replace("_" + type, ""), DyeColor.WHITE);
	}

	/**
	 * Tries to get the enum by its name, or returns
	 * the default value if not found
	 *
	 * @param value enum name
	 * @param defaultValue default value
	 * @return parsed enum
	 * @param <T> enum
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Enum<T>> T parseOr(@Nullable String value, T defaultValue) {
		if (value == null || value.isEmpty()) return defaultValue;
		try {
			return (T) Enum.valueOf(defaultValue.getClass(), value.toUpperCase(Locale.US));
		} catch (IllegalArgumentException ignored) {
			return defaultValue;
		}
	}

	/**
	 * Tries to get the enum by its name, or returns {@code null} if not found
	 *
	 * @param value enum name
	 * @param enumClass enum class
	 * @return parsed enum
	 * @param <T> enum
	 */
	public static <T extends Enum<T>> @Nullable T parseOrNull(@Nullable String value, Class<T> enumClass) {
		if (value == null || value.isEmpty()) return null;
		try {
			return Enum.valueOf(enumClass, value.toUpperCase(Locale.US));
		} catch (IllegalArgumentException ignored) {
			return null;
		}
	}

	/**
	 * Cycles array by provided distance
	 *
	 * @param distance distance
	 * @param array array
	 * @return rotated list
	 * @param <T> type
	 */
	public static <T> @UnknownNullability T[] rotate(int distance, @Nullable T... array) {
		return rotate(distance, Arrays.asList(array)).toArray(array.clone());
	}

	/**
	 * Cycles collection by provided distance
	 *
	 * @param distance distance
	 * @param array array
	 * @return rotated list
	 * @param <T> type
	 */
	public static <T> List<@UnknownNullability T> rotate(int distance, Collection<@Nullable T> array) {
		List<@Nullable T> list = new ArrayList<>(array);
		Collections.rotate(list, distance);
		return list;
	}

	/**
	 * Combines multiple arrays into one
	 *
	 * @param arrays arrays
	 * @return combined array
	 * @param <T> object type
	 */
	@SuppressWarnings("unchecked")
	public static <T> @UnknownNullability T[] combineArrays(@Nullable T[]... arrays) {
		int totalLength = 0;
		for (T[] array : arrays) {
			totalLength += array.length;
		}

		T[] combinedArray = (T[]) Array.newInstance(arrays[0].getClass().getComponentType(), totalLength);

		int currentIndex = 0;
		for (T[] array : arrays) {
			System.arraycopy(array, 0, combinedArray, currentIndex, array.length);
			currentIndex += array.length;
		}

		return combinedArray;
	}

	/**
	 * Combines collections into list
	 *
	 * @param collections collections
	 * @return list
	 * @param <T> object type
	 */
	@SafeVarargs
	public static <T> List<@UnknownNullability T> combineToList(Collection<? extends @Nullable T>... collections) {
		List<T> newList = new ArrayList<>();
		for (Collection<? extends T> collection : collections)
			newList.addAll(collection);
		return newList;
	}

	/**
	 * Gets tag values
	 *
	 * @param tagKey tag key
	 * @return tag values
	 * @param <T> tag type
	 */
	public static <T extends Keyed> Collection<T> getTagValues(TagKey<T> tagKey) {
		RegistryKey<T> registryKey = tagKey.registryKey();
		return RegistryAccess.registryAccess().getRegistry(registryKey).getTagValues(tagKey);
	}

	/**
	 * Gets the dimension key from the environment
	 *
	 * @param environment environment
	 * @return the dimension key
	 */
	public static String getDimensionKey(World.Environment environment) {
		return switch (environment) {
			case NORMAL -> "overworld";
			case NETHER -> "the_nether";
			case THE_END -> "the_end";
			default -> throw new IllegalArgumentException("Not a valid vanilla dimension: " + environment);
		};
	}

	/**
	 * Gets the dimension key from the environment
	 *
	 * @param key dimension key
	 * @return the environment
	 */
	public static World.Environment getByDimensionKey(String key) {
		return switch (key) {
			case "overworld" -> World.Environment.NORMAL;
			case "the_nether" -> World.Environment.NETHER;
			case "the_end" -> World.Environment.THE_END;
			default -> throw new IllegalArgumentException("Not a valid vanilla dimension key: " + key);
		};
	}

}
