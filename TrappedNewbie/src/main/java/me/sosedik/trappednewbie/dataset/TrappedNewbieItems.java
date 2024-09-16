package me.sosedik.trappednewbie.dataset;

import me.sosedik.kiterino.util.KiterinoBootstrapMaterialInjector;
import me.sosedik.trappednewbie.TrappedNewbie;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class TrappedNewbieItems {

	public static final Material MATERIAL_AIR = byKey("material_air");
	public static final Material HELMET_OUTLINE = byKey("helmet_outline");
	public static final Material CHESTPLATE_OUTLINE = byKey("chestplate_outline");
	public static final Material LEGGINGS_OUTLINE = byKey("leggings_outline");
	public static final Material BOOTS_OUTLINE = byKey("boots_outline");
	public static final Material GLOVES_OUTLINE = byKey("gloves_outline");

	public static final Material OAK_BRANCH = byKey("oak_branch");

	private static @NotNull Material byKey(@NotNull String value) {
		return KiterinoBootstrapMaterialInjector.injectMaterial(TrappedNewbie.trappedNewbieKey(value));
	}

}
