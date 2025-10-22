package me.sosedik.trappednewbie.dataset;

import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.utilizer.listener.misc.MilkImmuneEffects;
import org.bukkit.Registry;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class TrappedNewbieEffects {

	public static final PotionEffectType BONE_BREAKING = get("bone_breaking");
	public static final PotionEffectType BOUNCY = get("bouncy");
	public static final PotionEffectType COMFORT = get("comfort");
	public static final PotionEffectType FIREFINGERS = get("firefingers");
	public static final PotionEffectType HOT_POTATO = get("hot_potato");
	public static final PotionEffectType LIFE_LEECH = get("life_leech");
	public static final PotionEffectType PARALYZED = get("paralyzed");
	public static final PotionEffectType QUENCHED = get("quenched");
	public static final PotionEffectType ROTTEN_BITE = get("rotten_bite");
	public static final PotionEffectType SCARY = get("scary");
	public static final PotionEffectType THIRST = get("thirst");
	public static final PotionEffectType WATERBOLT = get("waterbolt");

	public static void init() {
		MilkImmuneEffects.addMilkImmune(COMFORT, HOT_POTATO);
	}

	private static PotionEffectType get(String key) {
		return Registry.MOB_EFFECT.getOrThrow(TrappedNewbie.trappedNewbieKey(key));
	}

}
