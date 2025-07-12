package me.sosedik.trappednewbie.api.advancement.display;

import io.papermc.paper.advancement.AdvancementDisplay;
import me.sosedik.packetadvancements.api.advancement.IAdvancement;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

import static me.sosedik.utilizer.api.message.Mini.raw;

@NullMarked
public class OpeningHolderAdvancementDisplay extends FancierAdvancementDisplay<OpeningHolderAdvancementDisplay> {

	private @Nullable String key;

	@Override
	public OpeningHolderAdvancementDisplay copyFrom(AdvancementDisplay display) {
		super.copyFrom(display);
		if (display instanceof OpeningHolderAdvancementDisplay openingHolderAdvancementDisplay)
			this.key = openingHolderAdvancementDisplay.key;
		return this;
	}

	@Override
	public void onRegister(IAdvancement advancement) {
		super.onRegister(advancement);
		this.key = advancement.getKey().value().replace('/', '.');
	}

	@Override
	public Component renderTitle(@Nullable Player viewer) {
		if (viewer == null) return super.renderTitle(null);
		if (this.key == null) return super.renderTitle(null);

		WorldData worldData = getWorldKey(viewer);
		Messenger messenger = Messenger.messenger(viewer);

		Component title;
		if (worldData.ownerName() == null)
			title = messenger.getMessageIfExists("adv." + this.key + ".title." + worldData.key());
		else
			title = messenger.getMessageIfExists("adv." + this.key + ".title." + worldData.key() + ".guest", raw("owner", worldData.ownerName()));

		return title == null ? super.renderTitle(viewer) : title;
	}

	@Override
	public Component renderDescription(@Nullable Player viewer) {
		if (viewer == null) return super.renderDescription(null);
		if (this.key == null) return super.renderDescription(null);

		WorldData worldData = getWorldKey(viewer);
		Messenger messenger = Messenger.messenger(viewer);

		Component description;
		if (worldData.ownerName() == null)
			description = messenger.getMessageIfExists("adv." + this.key + ".description." + worldData.key());
		else
			description = messenger.getMessageIfExists("adv." + this.key + ".description." + worldData.key() + ".guest", raw("owner", worldData.ownerName()));

		return description == null ? super.renderDescription(viewer) : description;
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

	@Override
	public OpeningHolderAdvancementDisplay clone() {
		return new OpeningHolderAdvancementDisplay().copyFrom(this);
	}

}
