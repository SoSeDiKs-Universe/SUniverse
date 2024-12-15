package me.sosedik.trappednewbie.dataset;

import me.sosedik.kiterino.util.KiterinoBootstrapMaterialInjector;
import me.sosedik.trappednewbie.TrappedNewbie;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class TrappedNewbieItems {

	public static final Material MATERIAL_AIR = byKey("material_air");
	public static final Material HELMET_OUTLINE = byKey("helmet_outline");
	public static final Material CHESTPLATE_OUTLINE = byKey("chestplate_outline");
	public static final Material LEGGINGS_OUTLINE = byKey("leggings_outline");
	public static final Material BOOTS_OUTLINE = byKey("boots_outline");
	public static final Material GLOVES_OUTLINE = byKey("gloves_outline");

	// Branches
	public static final Material ACACIA_BRANCH = byKey("acacia_branch");
	public static final Material BIRCH_BRANCH = byKey("birch_branch");
	public static final Material CHERRY_BRANCH = byKey("cherry_branch");
	public static final Material DARK_OAK_BRANCH = byKey("dark_oak_branch");
	public static final Material JUNGLE_BRANCH = byKey("jungle_branch");
	public static final Material MANGROVE_BRANCH = byKey("mangrove_branch");
	public static final Material OAK_BRANCH = byKey("oak_branch");
	public static final Material PALE_OAK_BRANCH = byKey("pale_oak_branch");
	public static final Material SPRUCE_BRANCH = byKey("spruce_branch");
	public static final Material DEAD_BRANCH = byKey("dead_branch");

	// Block versions of branches
	public static final Material ACACIA_TWIG = byKey("acacia_twig");
	public static final Material BIRCH_TWIG = byKey("birch_twig");
	public static final Material CHERRY_TWIG = byKey("cherry_twig");
	public static final Material DARK_OAK_TWIG = byKey("dark_oak_twig");
	public static final Material JUNGLE_TWIG = byKey("jungle_twig");
	public static final Material MANGROVE_TWIG = byKey("mangrove_twig");
	public static final Material OAK_TWIG = byKey("oak_twig");
	public static final Material PALE_OAK_TWIG = byKey("pale_oak_twig");
	public static final Material SPRUCE_TWIG = byKey("spruce_twig");
	public static final Material DEAD_TWIG = byKey("dead_twig");
	public static final Material BAMBOO_TWIG = byKey("bamboo_twig");
	public static final Material CRIMSON_TWIG = byKey("crimson_twig");
	public static final Material WARPED_TWIG = byKey("warped_twig");

	// Sticks
	public static final Material ACACIA_STICK = byKey("acacia_stick");
	public static final Material BIRCH_STICK = byKey("birch_stick");
	public static final Material CHERRY_STICK = byKey("cherry_stick");
	public static final Material DARK_OAK_STICK = byKey("dark_oak_stick");
	public static final Material JUNGLE_STICK = byKey("jungle_stick");
	public static final Material MANGROVE_STICK = byKey("mangrove_stick");
	public static final Material OAK_STICK = byKey("oak_stick");
	public static final Material PALE_OAK_STICK = byKey("pale_oak_stick");
	public static final Material SPRUCE_STICK = byKey("spruce_stick");
	public static final Material BAMBOO_STICK = byKey("bamboo_stick");
	public static final Material BAMBOOS_STICK = byKey("bamboos_stick");
	public static final Material CRIMSON_STICK = byKey("crimson_stick");
	public static final Material WARPED_STICK = byKey("warped_stick");

	private static @NotNull Material byKey(@NotNull String value) {
		return KiterinoBootstrapMaterialInjector.injectMaterial(TrappedNewbie.trappedNewbieKey(value));
	}

}
