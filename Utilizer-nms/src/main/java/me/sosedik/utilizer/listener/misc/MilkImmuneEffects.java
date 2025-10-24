package me.sosedik.utilizer.listener.misc;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Makes specified effect immune to milk
 */
@NullMarked
public class MilkImmuneEffects implements Listener {

	private static final Set<NamespacedKey> IMMUNE_EFFECTS = new HashSet<>();

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEffect(EntityPotionEffectEvent event) {
		if (event.getCause() != EntityPotionEffectEvent.Cause.MILK) return;
		if (event.getNewEffect() != null) return;
		if (!isMilkImmune(event.getModifiedType())) return;

		event.setCancelled(true);
	}

	/**
	 * Marks potion effect types as immune to milk
	 *
	 * @param types potion effect types
	 */
	public static void addMilkImmune(PotionEffectType ... types) {
		IMMUNE_EFFECTS.addAll(Arrays.stream(types).map(Keyed::getKey).toList());
	}

	/**
	 * Checks whether this effect type is immune to milk
	 *
	 * @param type potion effect type
	 * @return whether this effect is immune to milk
	 */
	public static boolean isMilkImmune(PotionEffectType type) {
		return IMMUNE_EFFECTS.contains(type.getKey());
	}

}
