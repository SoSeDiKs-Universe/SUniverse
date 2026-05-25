package me.sosedik.trappednewbie.api.advancement.display;

import io.papermc.paper.advancement.AdvancementDisplay;
import me.sosedik.packetadvancements.api.advancement.IAdvancement;
import me.sosedik.trappednewbie.listener.world.PerPlayerWorlds;
import me.sosedik.utilizer.Utilizer;
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
			title = messenger.getMessageIfExists("adv." + this.key + ".title." + worldData.localeKey());
		else
			title = messenger.getMessageIfExists("adv." + this.key + ".title." + worldData.localeKey() + ".guest", raw("owner", worldData.ownerName()));

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
			description = messenger.getMessageIfExists("adv." + this.key + ".description." + worldData.localeKey());
		else
			description = messenger.getMessageIfExists("adv." + this.key + ".description." + worldData.localeKey() + ".guest", raw("owner", worldData.ownerName()));

		return description == null ? super.renderDescription(viewer) : description;
	}

	private WorldData getWorldKey(Player player) {
		World world = player.getWorld();
		if (Utilizer.limboWorld() == world)
			return new WorldData("limbo", null);

		Key worldKey = world.key();
		if (PerPlayerWorlds.PERSONAL_WORLDS_NAMESPACE.equals(worldKey.namespace())) {
			try {
				UUID uuid = UUID.fromString(worldKey.value());
				if (player.getUniqueId().equals(uuid))
					return new WorldData(worldKey.namespace(), null);

				OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
				return new WorldData(worldKey.namespace(), offlinePlayer.getName());
			} catch (IllegalArgumentException _) {}
			return new WorldData(worldKey.namespace(), null);
		}

		if (worldKey.namespace().startsWith(PerPlayerWorlds.RESOURCE_WORLDS_NAMESPACE_PREFIX)) {
			try {
				UUID uuid = UUID.fromString(worldKey.value());
				if (player.getUniqueId().equals(uuid))
					return new WorldData(worldKey.namespace(), null);

				OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
				return new WorldData(worldKey.namespace(), offlinePlayer.getName());
			} catch (IndexOutOfBoundsException | IllegalArgumentException _) {}
			return new WorldData(worldKey.namespace(), null);
		}

		return new WorldData(worldKey.namespace(), null);
	}

	private record WorldData(String localeKey, @Nullable String ownerName) {}

	@Override
	public OpeningHolderAdvancementDisplay clone() {
		return new OpeningHolderAdvancementDisplay().copyFrom(this);
	}

}
