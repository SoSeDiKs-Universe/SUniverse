package me.sosedik.fancymotd;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import me.sosedik.utilizer.Utilizer;
import me.sosedik.utilizer.api.database.Database;
import me.sosedik.utilizer.api.language.LangHolder;
import me.sosedik.utilizer.api.language.LangOptions;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * Represents a client that pings the server
 */
@NullMarked
public class Pinger {

	public static final String DATABASE_NAME = "Pingers";
	private static final Database PINGERS_DATABASE = Database.prepareDatabase(FancyMotd.instance(), DATABASE_NAME.toLowerCase());

	// Cache configuration
	private static final int CACHE_EXPIRY_MINUTES = 5;
	private static final int CLEANUP_INTERVAL_MINUTES = 10;

	private static final LoadingCache<String, Pinger> PINGERS = CacheBuilder.newBuilder()
			.expireAfterAccess(CACHE_EXPIRY_MINUTES, TimeUnit.MINUTES)
			.build(
				new CacheLoader<>() {
					public Pinger load(String ip) {
						return constructPinger(ip);
					}
				}
			);

	private static @Nullable Predicate<UUID> clockAccessor = null;

	private final LangOptions langOptions;
	private final boolean hasClock;
	private final boolean isNewbie;

	private Pinger(String ip, boolean hasClock) {
		this(LangOptionsStorage.getByAddress(ip), hasClock, true);
	}

	private Pinger(UUID uuid) {
		this(LangHolder.langHolder(uuid).getLangOptions(), clockAccessor != null && clockAccessor.test(uuid), false);
	}

	private Pinger(LangOptions langOptions, boolean hasClock) {
		this(langOptions, hasClock, false);
	}

	private Pinger(LangOptions langOptions, boolean hasClock, boolean isNewbie) {
		this.langOptions = langOptions;
		this.hasClock = hasClock;
		this.isNewbie = isNewbie;
	}

	/**
	 * Gets the language used by the client
	 *
	 * @return supported language key for messages
	 */
	public LangOptions getLanguage() {
		return langOptions;
	}

	/**
	 * Checks if this pinger has a clock in the equipment
	 *
	 * @return true, if any player with this IP has a clock
	 */
	public boolean hasClock() {
		return hasClock;
	}

	/**
	 * Checks if this user wasn't recognized as already played
	 *
	 * @return true, if a player hasn't joined the server before
	 */
	public boolean isNewbie() {
		return isNewbie;
	}

	/**
	 * Creates a Pinger instance from the provided IP
	 *
	 * @param ip client's IP address
	 * @return Pinger instance
	 */
	private static Pinger constructPinger(String ip) {
		try (var con = PINGERS_DATABASE.openConnection();
			 var ps = con.prepareStatement("SELECT * FROM " + DATABASE_NAME + " WHERE IP LIKE ?")) {
			ps.setString(1, "%" + ip + "%");
			ResultSet rs = ps.executeQuery();
			boolean hasClock = clockAccessor == null;
			LangOptions language = null;
			while (rs.next()) {
				language = LangOptionsStorage.getLangOptionsIfExist(rs.getString("LastLang"));
				if (rs.getBoolean("Clock")) {
					hasClock = true;
					break;
				}
			}
			return language == null ? new Pinger(ip, hasClock) : new Pinger(language, hasClock);
		} catch (SQLTransientConnectionException ignored) {
			FancyMotd.logger().error("Could not connect to the database!");
		} catch (SQLException ex) {
			FancyMotd.logger().error("Could not connect to the database!", ex);
		}
		return new Pinger(ip, clockAccessor == null);
	}

	/**
	 * Stores a Pinger instance from the provided Player object
	 *
	 * @param player player
	 */
	public static void addPinger(Player player) {
		InetSocketAddress address = player.getAddress();
		if (address == null) return;

		InetAddress inetAddress = address.getAddress();
		if (inetAddress == null) return;

		String ip = inetAddress.getHostAddress();
		UUID uuid = player.getUniqueId();
		Pinger pinger = new Pinger(uuid);
		PINGERS.put(ip, pinger);

		Utilizer.scheduler().async(() -> updatePingerData(pinger, uuid, ip));
	}

	/**
	 * Removes a Pinger instance from memory
	 *
	 * @param ip IP
	 */
	public static void removePinger(String ip) {
		PINGERS.invalidate(ip);
	}

	private static void updatePingerData(Pinger pinger, UUID uuid, String ip) {
		String getIpsSql = "SELECT IP FROM " + DATABASE_NAME + " WHERE UUID = ?";
		String updateDataSql = "INSERT OR REPLACE INTO " + DATABASE_NAME + "(UUID, IP, LastLang, Clock) VALUES(?, ?, ?, ?)";
		try (Connection con = PINGERS_DATABASE.openConnection();
			 PreparedStatement selectPs = con.prepareStatement(getIpsSql);
			 PreparedStatement updatePs = con.prepareStatement(updateDataSql)) {

			selectPs.setString(1, uuid.toString());
			try (ResultSet rs = selectPs.executeQuery()) {
				String finalIp = ip;
				if (rs.next()) {
					String[] oldIps = rs.getString("IP").split("\\|");
					boolean updateIps = true;
					for (String oldIp : oldIps) {
						if (ip.equals(oldIp)) {
							updateIps = false;
							break;
						}
					}
					if (updateIps) {
						finalIp = switch (oldIps.length) {
							case 1 -> oldIps[0] + "|" + ip;
							case 2 -> oldIps[0] + "|" + oldIps[1] + "|" + ip;
							default -> oldIps[1] + "|" + oldIps[2] + "|" + ip;
						};
					}
				}

				updatePs.setString(1, uuid.toString());
				updatePs.setString(2, finalIp);
				updatePs.setString(3, pinger.getLanguage().minecraftId());
				updatePs.setBoolean(4, clockAccessor != null && pinger.hasClock());
				updatePs.executeUpdate();
			}
		} catch (SQLException e) {
			FancyMotd.logger().error("Failed to update pinger data for player {}", uuid, e);
		}
	}

	/**
	 * Returns a Pinger instance for the provided IP.
	 * <br>Will create a Pinger instance if missing.
	 *
	 * @param ip IP
	 * @return Pinger instance
	 */
	public static Pinger getPinger(String ip) {
		return PINGERS.getUnchecked(ip);
	}

	/**
	 * Sets the check for displaying a clock
	 *
	 * @param clockAccessor clock check
	 */
	@SuppressWarnings("unused")
	public static void setClockAccessor(Predicate<UUID> clockAccessor) {
		Pinger.clockAccessor = clockAccessor;
	}

	static void setupDatabase() {
		String createTableSql = """
			CREATE TABLE IF NOT EXISTS %s(
				UUID varchar(64) NOT NULL,
				IP varchar(64),
				LastLang varchar(64),
				Clock BIT(1),
				PRIMARY KEY(UUID)
			)""".formatted(DATABASE_NAME);
		try (Connection con = PINGERS_DATABASE.openConnection();
		     PreparedStatement ps = con.prepareStatement(createTableSql)) {
			ps.executeUpdate();
		} catch (SQLException e) {
			FancyMotd.logger().error("Could not create pingers database!", e);
			Bukkit.getPluginManager().disablePlugin(FancyMotd.instance());
		}
	}

	static void closeDatabase() {
		PINGERS_DATABASE.close();
	}

	static void runCleanupTask() {
		long cleanupInterval = CLEANUP_INTERVAL_MINUTES * 60 * 20L; // minutes to ticks
		Utilizer.scheduler().async(PINGERS::cleanUp, cleanupInterval, cleanupInterval);
	}

}
