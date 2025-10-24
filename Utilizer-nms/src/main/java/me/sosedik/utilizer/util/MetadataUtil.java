package me.sosedik.utilizer.util;

import org.bukkit.entity.Entity;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@NullMarked
public class MetadataUtil {

	private MetadataUtil() {
		throw new IllegalStateException("Utility class");
	}

	private static final Map<UUID, Map<String, MetadataValue>> METADATA_VALUES = new ConcurrentHashMap<>();

	/**
	 * Checks whether player has metadata stored
	 * for the provided key
	 *
	 * @param metadataOwner metadata owner
	 * @param metadataKey   metadata key
	 * @return true, if player has metadata for provided key
	 */
	public static boolean hasMetadata(Entity metadataOwner, String metadataKey) {
		Map<String, MetadataValue> metadata = METADATA_VALUES.get(metadataOwner.getUniqueId());
		return metadata != null && metadata.containsKey(metadataKey);
	}

	/**
	 * Sets metadata for this player, which will
	 * be present until player's logout
	 *
	 * @param metadataOwner metadata owner
	 * @param metadataKey   metadata key
	 * @param value         metadata value
	 */
	public static void setMetadata(Entity metadataOwner, String metadataKey, Object value) {
		Map<String, MetadataValue> metadata = METADATA_VALUES.computeIfAbsent(metadataOwner.getUniqueId(), k -> new WeakHashMap<>(1));
		metadata.put(metadataKey, new MetadataValue(value));
	}

	/**
	 * Gets metadata for this player, if present
	 *
	 * @param metadataOwner metadata owner
	 * @param metadataKey   metadata key
	 * @return metadata object or null
	 */
	public static @Nullable MetadataValue getMetadata(Entity metadataOwner, String metadataKey) {
		Map<String, MetadataValue> metadata = METADATA_VALUES.get(metadataOwner.getUniqueId());
		return metadata == null ? null : metadata.get(metadataKey);
	}

	/**
	 * Gets metadata for this player or
	 * default value if missing
	 * <p>Note: if missing, the default value will be stored
	 *
	 * @param metadataOwner metadata owner
	 * @param metadataKey   metadata key
	 * @return metadata object if present, otherwise default value
	 */
	public static MetadataValue getMetadata(Entity metadataOwner, String metadataKey, Object defaultValue) {
		Map<String, MetadataValue> metadata = METADATA_VALUES.get(metadataOwner.getUniqueId());
		if (metadata == null) {
			setMetadata(metadataOwner, metadataKey, defaultValue);
			return new MetadataValue(defaultValue);
		}
		MetadataValue metadataValue = metadata.get(metadataKey);
		if (metadataValue == null) {
			setMetadata(metadataOwner, metadataKey, defaultValue);
			return new MetadataValue(defaultValue);
		}
		return metadataValue;
	}

	/**
	 * Gets metadata for this player or
	 * default value if missing
	 * <p>Note: if missing, the default value will be stored
	 *
	 * @param metadataOwner metadata owner
	 * @param metadataKey   metadata key
	 * @return metadata object if present, otherwise default value
	 */
	public static MetadataValue getMetadata(Entity metadataOwner, String metadataKey, Supplier<Object> defaultValue) {
		Map<String, MetadataValue> metadata = METADATA_VALUES.get(metadataOwner.getUniqueId());
		if (metadata == null) {
			Object value = defaultValue.get();
			setMetadata(metadataOwner, metadataKey, value);
			return new MetadataValue(value);
		}
		MetadataValue metadataValue = metadata.get(metadataKey);
		if (metadataValue == null) {
			Object value = defaultValue.get();
			setMetadata(metadataOwner, metadataKey, value);
			return new MetadataValue(value);
		}
		return metadataValue;
	}

	/**
	 * Removes specified metadata for this player
	 *
	 * @param metadataOwner metadata owner
	 * @param metadataKey   metadata key
	 */
	public static @Nullable MetadataValue removeMetadata(Entity metadataOwner, String metadataKey) {
		Map<String, MetadataValue> metadata = METADATA_VALUES.get(metadataOwner.getUniqueId());
		if (metadata != null)
			return metadata.remove(metadataKey);
		return null;
	}

	/**
	 * Removes all metadata for this player
	 *
	 * @param metadataOwner metadata owner
	 */
	public static void clearMetadata(Entity metadataOwner) {
		METADATA_VALUES.remove(metadataOwner.getUniqueId());
	}

	/**
	 * A wrapper for metadata values
	 */
	public record MetadataValue(Object metadataValue) {

		/**
		 * Get value as integer
		 *
		 * @return int
		 */
		public int asInt() {
			return (int) this.metadataValue;
		}

		/**
		 * Get value as float
		 *
		 * @return float
		 */
		public float asFloat() {
			return (float) this.metadataValue;
		}

		/**
		 * Get value as double
		 *
		 * @return double
		 */
		public double asDouble() {
			return (double) this.metadataValue;
		}

		/**
		 * Get value as boolean
		 *
		 * @return int
		 */
		public boolean asBoolean() {
			return (boolean) this.metadataValue;
		}

		/**
		 * Get value as String
		 *
		 * @return String
		 */
		public String asString() {
			return (String) this.metadataValue;
		}

		/**
		 * Get raw object value
		 *
		 * @return metadata object
		 */
		public <T> T get(Class<T> clazz) {
			return clazz.cast(this.metadataValue);
		}

	}

}
