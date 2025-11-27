package me.sosedik.trappednewbie.listener.item;

import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.listener.player.TotemRituals;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

import static me.sosedik.trappednewbie.TrappedNewbie.trappedNewbieKey;

/**
 * Plays sound when using some items
 */
@NullMarked
public class SimpleSoundItems implements Listener {

	private static final Map<Material, SoundData> SOUNDS = new HashMap<>();

	static {
		SOUNDS.put(TrappedNewbieItems.FLUTE, new SoundData(ResourceLib.getSound(trappedNewbieKey("item/flute")), 20));
		SOUNDS.put(TrappedNewbieItems.RATTLE, new SoundData(ResourceLib.getSound(trappedNewbieKey("item/rattle")), 16));
	}

	@EventHandler
	public void onUse(PlayerInteractEvent event) {
		if (event.getHand() == null) return;
		if (!event.getAction().isRightClick()) return;
		if (event.useItemInHand() == Event.Result.DENY) return;

		ItemStack item = event.getItem();
		if (item == null) return;

		Player player = event.getPlayer();
		if (player.hasCooldown(item)) return;

		SoundData soundData = SOUNDS.get(item.getType());
		if (soundData == null) return;

		player.swingHand(event.getHand());
		player.emitSound(Sound.sound(soundData.soundKey(), Sound.Source.PLAYER, 1F, 0.9F + (float) Math.random() * 0.2F));
		player.setCooldown(item, soundData.cooldown());
		TotemRituals.playedInstrument(player, item.getType(), player.getLocation());
	}

	private record SoundData(NamespacedKey soundKey, int cooldown) {}

}
