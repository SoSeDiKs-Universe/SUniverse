package me.sosedik.socializer.util;

import me.sosedik.utilizer.util.Scheduler;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Manages temporary verification codes with automatic expiration.
 * Uses a concurrent map with scheduled cleanup to prevent memory leaks.
 */
public class DiscordUserVerificationCache {

	private static final long DEFAULT_TTL = TimeUnit.MINUTES.toMillis(5);
	private static final long CLEANUP_INTERVAL = TimeUnit.MINUTES.toMillis(1);

	private final Map<UUID, CacheEntry> cache = new ConcurrentHashMap<>();
	private final Scheduler scheduler;
	private final long ttl;

	/**
	 * Creates a new verification cache with default TTL.
	 *
	 * @param plugin the plugin instance
	 */
	public DiscordUserVerificationCache(JavaPlugin plugin) {
		this(plugin, DEFAULT_TTL);
	}

	/**
	 * Creates a new verification cache with custom TTL.
	 *
	 * @param plugin the plugin instance
	 * @param ttlMillis time-to-live in milliseconds
	 */
	public DiscordUserVerificationCache(JavaPlugin plugin, long ttlMillis) {
		this.scheduler = new Scheduler(plugin);
		this.ttl = ttlMillis;
		startCleanupTask();
	}

	/**
	 * Puts a verification code for the given player.
	 *
	 * @param playerUUID the player's UUID
	 * @param discordId the Discord user ID
	 * @return true if the entry was added, false if an entry already exists
	 */
	public boolean put(UUID playerUUID, long discordId) {
		if (this.cache.containsKey(playerUUID))
			return false;

		this.cache.put(playerUUID, new CacheEntry(discordId, System.currentTimeMillis()));
		return true;
	}

	/**
	 * Gets the verification code for the given player.
	 *
	 * @param playerUUID the player's UUID
	 * @return the Discord ID if present and not expired, null otherwise
	 */
	public @Nullable Long get(UUID playerUUID) {
		CacheEntry entry = this.cache.get(playerUUID);
		if (entry == null) return null;

		if (isExpired(entry)) {
			this.cache.remove(playerUUID);
			return null;
		}

		return entry.value();
	}

	/**
	 * Removes a verification entry.
	 *
	 * @param playerUUID the player's UUID
	 * @return the removed value, or null if none existed
	 */
	public @Nullable Long remove(UUID playerUUID) {
		CacheEntry entry = this.cache.remove(playerUUID);
		return entry != null ? entry.value() : null;
	}

	/**
	 * Checks whether the cache contains a verification for the given player.
	 *
	 * @param playerUUID the player's UUID
	 * @return true if a valid (non-expired) entry exists
	 */
	public boolean contains(UUID playerUUID) {
		CacheEntry entry = this.cache.get(playerUUID);
		if (entry == null) return false;

		if (isExpired(entry)) {
			this.cache.remove(playerUUID);
			return false;
		}

		return true;
	}

	/**
	 * Clears all verification entries.
	 */
	public void clear() {
		this.cache.clear();
	}

	/**
	 * Gets the current size of the cache (including expired entries).
	 *
	 * @return the cache size
	 */
	public int size() {
		return this.cache.size();
	}

	private boolean isExpired(CacheEntry entry) {
		return System.currentTimeMillis() - entry.timestamp() > this.ttl;
	}

	private void startCleanupTask() {
		this.scheduler.async(this::cleanup, 0L, CLEANUP_INTERVAL);
	}

	private void cleanup() {
		long now = System.currentTimeMillis();
		this.cache.entrySet().removeIf(entry -> now - entry.getValue().timestamp() > this.ttl);
	}

	/**
	 * Represents a cache entry with value and creation timestamp.
	 *
	 * @param value the stored value
	 * @param timestamp the creation time in milliseconds since epoch
	 */
	private record CacheEntry(long value, long timestamp) {}

}
