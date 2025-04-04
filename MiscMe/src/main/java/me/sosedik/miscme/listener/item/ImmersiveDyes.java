package me.sosedik.miscme.listener.item;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

/**
 * Dyes can be applied onto blocks immersible
 */
@NullMarked
public class ImmersiveDyes implements Listener {

	public static final double DYE_REDUCE_CHANCE = 0.08D;
	public static final Material CLEARING_MATERIAL = Material.PAPER;

	/**
	 * Plays dying effect, that is: swing hand, play sound, spawn particles.
	 *
	 * @param player player
	 * @param hand hand to swing
	 * @param loc location for particles
	 * @param effect particle data
	 */
	public static void playEffect(Player player, @Nullable EquipmentSlot hand, Location loc, @Nullable BlockData effect) {
		player.clearActiveItem(); // In case dye is usable
		player.emitSound(Sound.ENTITY_LEASH_KNOT_PLACE, 1F, 2F);
		if (hand != null) player.swingHand(hand);
		if (effect != null) loc.getWorld().spawnParticle(Particle.BLOCK_CRUMBLE, loc.center(0.5), 10, 0.4, 0.4, 0.4, 0, effect);
	}

	/**
	 * Gets the dye color from the dye item
	 *
	 * @param dye dye item stack
	 * @return dye color
	 */
	public static @Nullable DyeColor getDyeColor(ItemStack dye) {
		try {
			return DyeColor.valueOf(dye.getType().getKey().getKey().replace("_dye", "").toUpperCase(Locale.US));
		} catch (IllegalArgumentException ignored) {
			return null;
		}
	}

}
