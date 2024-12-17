package me.sosedik.delightfulfarming.dataset;

import me.sosedik.delightfulfarming.DelightfulFarming;
import me.sosedik.kiterino.util.KiterinoBootstrapMaterialInjector;
import org.bukkit.Material;
import org.jspecify.annotations.NullMarked;

@NullMarked
@SuppressWarnings("unused")
public class DelightfulFarmingItems {

	public static final Material PUMPKIN_SLICE = byKey("pumpkin_slice");

	private static Material byKey(String value) {
		return KiterinoBootstrapMaterialInjector.injectMaterial(DelightfulFarming.delightfulFarmingKey(value));
	}

}
