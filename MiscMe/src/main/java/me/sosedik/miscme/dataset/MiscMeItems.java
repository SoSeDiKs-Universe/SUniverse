package me.sosedik.miscme.dataset;

import me.sosedik.kiterino.util.KiterinoBootstrapMaterialInjector;
import me.sosedik.miscme.MiscMe;
import org.bukkit.Material;

public class MiscMeItems {

	public static final Material DEPTH_METER = byKey("depth_meter");
	public static final Material LUNAR_CLOCK = byKey("lunar_clock");
	public static final Material SPEEDOMETER = byKey("speedometer");
	public static final Material BAROMETER = byKey("barometer");

	private static Material byKey(String value) {
		return KiterinoBootstrapMaterialInjector.injectMaterial(MiscMe.miscMeKey(value));
	}

}
