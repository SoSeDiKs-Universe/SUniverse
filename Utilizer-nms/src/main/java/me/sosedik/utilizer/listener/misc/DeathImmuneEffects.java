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
 * Makes specified effects immune to death
 */
@NullMarked
public class DeathImmuneEffects implements Listener {

	private static final Set<NamespacedKey> IMMUNE_EFFECTS = new HashSet<>();

	static {
		addDeathImmune(PotionEffectType.UNLUCK);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEffect(EntityPotionEffectEvent event) {
		if (event.getCause() != EntityPotionEffectEvent.Cause.DEATH) return;
		if (event.getNewEffect() != null) return;
		if (!isDeathImmune(event.getModifiedType())) return;

		event.setCancelled(true);
	}

	/**
	 * Marks potion effect types as immune to death
	 *
	 * @param types potion effect types
	 */
	public static void addDeathImmune(PotionEffectType ... types) {
		IMMUNE_EFFECTS.addAll(Arrays.stream(types).map(Keyed::getKey).toList());
	}

	/**
	 * Checks whether this effect type is immune to death
	 *
	 * @param type potion effect type
	 * @return whether this effect is immune to death
	 */
	public static boolean isDeathImmune(PotionEffectType type) {
		return IMMUNE_EFFECTS.contains(type.getKey());
	}

}
