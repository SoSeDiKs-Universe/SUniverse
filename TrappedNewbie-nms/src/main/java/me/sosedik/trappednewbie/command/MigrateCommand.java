package me.sosedik.trappednewbie.command;

import com.google.gson.JsonObject;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.fancymotd.Pinger;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.utilizer.Utilizer;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.util.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Migrating/resetting player's data
 */
@NullMarked
@Permission("trapped_newbie.commands.migrate")
public class MigrateCommand implements Listener {

	private static final Set<UUID> DENY_JOIN = new HashSet<>();

	@Command("migrate <old_uuid> [new_uuid]")
	public void onCommand(
		CommandSourceStack stack,
		@Argument(value = "old_uuid") UUID oldUuid,
		@Nullable @Argument(value = "new_uuid") UUID newUuid
	) {
		if (Bukkit.getPlayer(oldUuid) != null || (newUuid != null && Bukkit.getPlayer(newUuid) != null)) {
			TrappedNewbie.logger().warn("Player with provided UUID is online.");
			return;
		}

		migrate(oldUuid, newUuid, new ResetData(true, true));

		boolean delete = newUuid == null;
		TrappedNewbie.logger().info("User was successfully {}!", delete ? "deleted" : "migrated");
	}

	public record ResetData(boolean advancements, boolean stats) {}

	public static void migrate(UUID oldUuid, @Nullable UUID newUuid, ResetData resetData) {
		File worldFolder = Bukkit.getWorlds().getFirst().getWorldFolder();
		var file = new File(worldFolder, "playerdata" + File.separator + oldUuid + ".dat");
		if (!file.exists()) {
			TrappedNewbie.logger().warn("UUID's playerdata does not exist: {}", oldUuid);
			return;
		}

		DENY_JOIN.add(oldUuid);
		if (newUuid != null) DENY_JOIN.add(newUuid);

		boolean delete = newUuid == null;
		if (!delete) {
			if (Bukkit.getOfflinePlayer(newUuid).hasPlayedBefore()) {
				migrate(newUuid, null, resetData);
				DENY_JOIN.add(newUuid);
			}
		}

		try {
			// playerdata
			if (delete) file.delete();
			else Files.move(file.toPath(), new File(worldFolder, "playerdata" + File.separator + newUuid + ".dat").toPath());
			file = new File(worldFolder, "playerdata" + File.separator + oldUuid + ".dat_old");
			if (file.exists()) {
				if (delete) file.delete();
				else Files.move(file.toPath(), new File(worldFolder, "playerdata" + File.separator + newUuid + ".dat_old").toPath());
			}

			// advancements
			if (resetData.advancements()) {
				file = new File(worldFolder, "advancements" + File.separator + oldUuid + ".json");
				if (file.exists()) {
					if (delete) file.delete();
					else Files.move(file.toPath(), new File(worldFolder, "advancements" + File.separator + newUuid + ".json").toPath());
				}
			}

			// stats
			if (resetData.stats()) {
				file = new File(worldFolder, "stats" + File.separator + oldUuid + ".json");
				if (file.exists()) {
					if (delete) file.delete();
					else Files.move(file.toPath(), new File(worldFolder, "stats" + File.separator + newUuid + ".json").toPath());
				}
			}

			// player worlds
			File customWorldsFolder = new File(worldFolder.getParentFile(), "worlds-personal");
			file = new File(customWorldsFolder, oldUuid.toString());
			if (file.exists()) {
				if (delete) FileUtil.deleteFolder(file);
				else Files.move(file.toPath(), new File(customWorldsFolder, newUuid.toString()).toPath());
			}
			customWorldsFolder = new File(worldFolder.getParentFile(), "worlds-resources");
			if (customWorldsFolder.exists()) {
				for (File worldTypeFolder : Objects.requireNonNull(customWorldsFolder.listFiles())) {
					file = new File(worldTypeFolder, oldUuid.toString());
					if (file.exists()) {
						if (delete) FileUtil.deleteFolder(file);
						else Files.move(file.toPath(), new File(worldTypeFolder, newUuid.toString()).toPath());
					}
				}
			}

			// custom advancements
			if (resetData.advancements()) {
				file = new File(TrappedNewbie.instance().getDataFolder(), "advancements" + File.separator + "teams.json");
				if (file.exists()) {
					File teamsFile = file;
					JsonObject advTeams = FileUtil.readJsonObject(file);
					if (advTeams.has(oldUuid.toString())) {
						int teamId = advTeams.get(oldUuid.toString()).getAsInt();
						if (delete) {
							file = new File(TrappedNewbie.instance().getDataFolder(), "advancements" + File.separator + "storage" + File.separator + "team_" + teamId + ".json");
							if (file.exists()) file.delete();
							file = new File(TrappedNewbie.instance().getDataFolder(), "advancements" + File.separator + "storage" + File.separator + "team_" + teamId + ".json.old");
							if (file.exists()) file.delete();
						} else {
							advTeams.remove(oldUuid.toString());
							advTeams.addProperty(newUuid.toString(), teamId);
							try (var writer = new FileWriter(teamsFile)) {
								writer.write(advTeams.toString());
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}

			// misc plugin data
			file = new File(Utilizer.instance().getDataFolder(), "players" + File.separator + oldUuid + ".dat");
			if (file.exists()) {
				if (delete) file.delete();
				else Files.move(file.toPath(), new File(Utilizer.instance().getDataFolder(), "players" + File.separator + newUuid + ".dat").toPath());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			DENY_JOIN.remove(oldUuid);
			if (newUuid != null) DENY_JOIN.remove(newUuid);
		}
	}

	@EventHandler
	public void onJoin(AsyncPlayerPreLoginEvent event) {
		if (!DENY_JOIN.contains(event.getUniqueId())) return;

		String ip = event.getAddress().getHostAddress();
		var pinger = Pinger.getPinger(ip);
		Messenger messenger = Messenger.messenger(pinger.getLanguage());

		event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, messenger.getMessage("command.migrate.migrating"));
	}

}
