package me.sosedik.requiem.dataset;

import me.sosedik.requiem.Requiem;
import me.sosedik.utilizer.listener.misc.MilkImmuneEffects;
import org.bukkit.Registry;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class RequiemEffects {

	public static final PotionEffectType PARASITES = get("parasites");
	public static final PotionEffectType ATTRITION = get("attrition");

	public static void init() {
		MilkImmuneEffects.addMilkImmune(ATTRITION);
	}

	private static PotionEffectType get(String key) {
		return Registry.MOB_EFFECT.getOrThrow(Requiem.requiemKey(key));
	}

}
