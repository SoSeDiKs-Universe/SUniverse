package me.sosedik.socializer.discord;

import me.sosedik.socializer.Socializer;
import me.sosedik.socializer.util.DiscordUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@NullMarked
public class Discorder {

	private static final Map<UUID, Discorder> DISCORDERS = new ConcurrentHashMap<>();
	public static final String DATABASE_NAME = "Discorders";

	private final UUID uuid;
	private long discordId;

	public Discorder(Player player) {
		this.uuid = player.getUniqueId();
		this.discordId = -1L;
		Socializer.scheduler().async(this::load);
	}

	/**
	 * Gets the player's uuid
	 *
	 * @return the player's uuid
	 */
	public UUID getUUID() {
		return uuid;
	}

	/**
	 * Gets the Discord user id
	 *
	 * @return the Discord user id
	 */
	public long getDiscordId() {
		return discordId;
	}

	/**
	 * Sets a new linked Discord user id
	 *
	 * @param discordId Discord user id
	 */
	public void setDiscordId(@Nullable Long discordId) {
		if (discordId == null || discordId <= 0L) Socializer.scheduler().async(() -> DiscordUtil.unverify(this.discordId));
		this.discordId = discordId == null ? -1L : discordId;
		Socializer.scheduler().async(this::save);
	}

	/**
	 * Checks whether this player has linked Discord
	 *
	 * @return whether this player has linked Discord
	 */
	public boolean hasDiscord() {
		return getDiscordId() > 0L;
	}

	private void load() {
		String selectSql = "SELECT `DiscordId` FROM `" + DATABASE_NAME + "` WHERE `UUID` = '" + getUUID() + "';";
		try (var con = Socializer.database().openConnection();
			 var ps = con.prepareStatement(selectSql)) {
			ResultSet rs = ps.executeQuery();
			if (rs.next())
				this.discordId = rs.getLong("DiscordId");
		} catch (SQLException e) {
			Socializer.logger().error("Could not connect to database!", e);
		}
	}

	private void save() {
		save(getDiscordId(), getDiscordId(), getUUID().toString());
	}

	/**
	 * Saves Discorder data into the database
	 *
	 * @param oldDiscordId old Discord user id
	 * @param newDiscordId new Discord user id
	 * @param uuid player's in-game uuid
	 */
	public static void save(long oldDiscordId, long newDiscordId, String uuid) {
		String updateSql;
		if (newDiscordId <= 0L) {
			if (uuid.isEmpty()) {
				updateSql = "DELETE FROM `" + DATABASE_NAME + "` WHERE `DiscordId` = '" + oldDiscordId + "'";
			} else {
				updateSql = "DELETE FROM `" + DATABASE_NAME + "` WHERE `UUID` = '" + uuid + "'";
			}
		} else {
			updateSql = "UPDATE `" + DATABASE_NAME + "` SET `DiscordId` = '" + newDiscordId + "' WHERE UUID = '" + uuid + "'";
		}
		try (var con = Socializer.database().openConnection();
			 var ps = con.prepareStatement(updateSql)) {
			ps.executeUpdate();
		} catch (SQLException ex) {
			Socializer.logger().error("Could not connect to the database!", ex);
		}
	}

	/**
	 * Gets the Discorder instance of the player
	 *
	 * @param player player
	 * @return Discorder
	 */
	public static Discorder getDiscorder(Player player) {
		return DISCORDERS.computeIfAbsent(player.getUniqueId(), k -> new Discorder(player));
	}

	/**
	 * Tries to find the Discorder instance by Discord user id
	 *
	 * @param id Discord user id
	 * @return Discorder
	 */
	public static @Nullable Discorder getDiscorder(long id) {
		for (Discorder discorder : DISCORDERS.values()) {
			if (discorder.getDiscordId() == id)
				return discorder;
		}
		return null;
	}

	/**
	 * Removes Discorder instance from cache
	 *
	 * @param player player
	 */
	public static @Nullable Discorder removePlayer(Player player) {
		return DISCORDERS.remove(player.getUniqueId());
	}

	static void setupDatabase() {
		String createTableSql = "CREATE TABLE IF NOT EXISTS " + DATABASE_NAME + "(`UUID` varchar(64) NOT NULL, `DiscordId` bigint(255), PRIMARY KEY(`UUID`));";
		try (var con = Socializer.database().openConnection();
			 var ps = con.prepareStatement(createTableSql)) {
			ps.executeUpdate();
		} catch (SQLException e) {
			Socializer.logger().error("Could not create discorders database!", e);
			Bukkit.getPluginManager().disablePlugin(Socializer.instance());
		}
	}

}
