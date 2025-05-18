package me.sosedik.delightfulfarming.dataset;

import me.sosedik.delightfulfarming.DelightfulFarming;
import me.sosedik.kiterino.util.KiterinoBootstrapMaterialInjector;
import org.bukkit.Material;
import org.jspecify.annotations.NullMarked;

@NullMarked
@SuppressWarnings("unused")
public class DelightfulFarmingItems {

	public static final Material PUMPKIN_SLICE = byKey("pumpkin_slice");
	public static final Material SWEET_BERRY_MINCE = byKey("sweet_berry_mince");
	public static final Material SWEET_BERRY_MEATBALLS = byKey("sweet_berry_meatballs");
	public static final Material SWEET_BERRY_PIPS = byKey("sweet_berry_pips");
	public static final Material GLOW_BERRY_PIPS = byKey("glow_berry_pips");
	public static final Material GLOWGURT = byKey("glowgurt");

	public static final Material SWEET_BERRY_BASKET = byKey("sweet_berry_basket");
	public static final Material GLOW_BERRY_BASKET = byKey("glow_berry_basket");

	public static final Material SWEET_BERRY_PIPS_BUSH = byKey("sweet_berry_pips_bush");
	public static final Material GLOW_BERRY_PIPS_BUSH = byKey("glow_berry_pips_bush");

	public static final Material CHARCOAL_BLOCK = byKey("charcoal_block");

	private static Material byKey(String value) {
		return KiterinoBootstrapMaterialInjector.injectMaterial(DelightfulFarming.delightfulFarmingKey(value));
	}

}
