package me.sosedik.requiem.dataset;

import me.sosedik.requiem.Requiem;
import me.sosedik.utilizer.listener.misc.MilkImmuneEffects;
import org.bukkit.Registry;
import org.bukkit.potion.PotionEffectType;

public class RequiemEffects {

	public static final PotionEffectType PARASITES = Registry.EFFECT.getOrThrow(Requiem.requiemKey("parasites"));
	public static final PotionEffectType ATTRITION = Registry.EFFECT.getOrThrow(Requiem.requiemKey("attrition"));

	static {
		MilkImmuneEffects.addMilkImmune(ATTRITION);
	}

}
