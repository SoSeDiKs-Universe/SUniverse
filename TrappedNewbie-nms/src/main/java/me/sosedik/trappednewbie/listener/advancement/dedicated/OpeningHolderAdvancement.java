package me.sosedik.trappednewbie.listener.advancement.dedicated;

import me.sosedik.packetadvancements.api.display.IAdvancementDisplay;
import me.sosedik.packetadvancements.api.event.AsyncPlayerAdvancementSendEvent;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

import static me.sosedik.utilizer.api.message.Mini.raw;

/**
 * Per-world welcome message in Requiem Holder
 */
public class OpeningHolderAdvancement implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onAdvancementSend(AsyncPlayerAdvancementSendEvent event) {
		if (event.getAdvancement() != TrappedNewbieAdvancements.OPENING_HOLDER) return;

		IAdvancementDisplay display = event.getSentDisplay().clone();
		String key = event.getAdvancement().getKey().value().replace('/', '.');

		Player player = event.getPlayer();
		Messenger messenger = Messenger.messenger(player);
		WorldData worldData = getWorldKey(player);

		Component title, description;
		if (worldData.ownerName() == null) {
			title = messenger.getMessageIfExists("adv." + key  + ".title." + worldData.key());
			description = messenger.getMessageIfExists("adv." + key  + ".description." + worldData.key());
		} else {
			title = messenger.getMessageIfExists("adv." + key  + ".title." + worldData.key() + ".guest", raw("owner", worldData.ownerName()));
			description = messenger.getMessageIfExists("adv." + key  + ".description." + worldData.key() + ".guest", raw("owner", worldData.ownerName()));
		}

		if (title != null) display.withTitle(title);
		if (description != null) display.withDescription(description);

		if (title != null || description != null)
			event.setSentDisplay(display);
	}

	private WorldData getWorldKey(Player player) {
		World world = player.getWorld();
		Key worldKey = world.key();
		if (!TrappedNewbie.NAMESPACE.equals(worldKey.namespace())) {
			if (Bukkit.getWorlds().getFirst() == world)
				return new WorldData("limbo", null);
			return new WorldData(worldKey.value(), null);
		}

		final String value = worldKey.value();

		if (value.startsWith("worlds-personal/")) {
			try {
				UUID uuid = UUID.fromString(value.substring("worlds-personal/".length()));
				if (player.getUniqueId().equals(uuid))
					return new WorldData("personal", null);

				OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
				return new WorldData("personal", offlinePlayer.getName());
			} catch (IllegalArgumentException ignored) {}
			return new WorldData(value, null);
		}

		if (value.startsWith("worlds-resources/")) {
			String[] split = value.split("/");
			try {
				UUID uuid = UUID.fromString(split[2]);
				if (player.getUniqueId().equals(uuid))
					return new WorldData("resource." + split[1], null);

				OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
				return new WorldData("resource." + split[1], offlinePlayer.getName());
			} catch (IndexOutOfBoundsException | IllegalArgumentException ignored) {}
			return new WorldData(value, null);
		}

		return new WorldData(value, null);
	}

	private record WorldData(String key, @Nullable String ownerName) {}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		TrappedNewbieAdvancements.OPENING_HOLDER.resend(event.getPlayer());
	}

}
