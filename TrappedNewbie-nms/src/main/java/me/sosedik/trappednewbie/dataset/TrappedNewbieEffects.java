package me.sosedik.trappednewbie.dataset;

import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.utilizer.listener.misc.MilkImmuneEffects;
import org.bukkit.Registry;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class TrappedNewbieEffects {

	public static final PotionEffectType THIRST = get("thirst");
	public static final PotionEffectType QUENCHED = get("quenched");
	public static final PotionEffectType COMFORT = get("comfort");

	public static void init() {
		MilkImmuneEffects.addMilkImmune(COMFORT);
	}

	private static PotionEffectType get(String key) {
		return Registry.MOB_EFFECT.getOrThrow(TrappedNewbie.trappedNewbieKey(key));
	}

}
