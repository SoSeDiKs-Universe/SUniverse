package me.sosedik.trappednewbie.dataset;

import me.sosedik.kiterino.util.KiterinoBootstrapMaterialInjector;
import me.sosedik.trappednewbie.TrappedNewbie;
import org.bukkit.Material;
import org.jspecify.annotations.NullMarked;

@NullMarked
@SuppressWarnings("unused")
public class TrappedNewbieItems {

	public static final Material MATERIAL_AIR = byKey("material_air");
	public static final Material HELMET_OUTLINE = byKey("helmet_outline");
	public static final Material CHESTPLATE_OUTLINE = byKey("chestplate_outline");
	public static final Material LEGGINGS_OUTLINE = byKey("leggings_outline");
	public static final Material BOOTS_OUTLINE = byKey("boots_outline");
	public static final Material GLOVES_OUTLINE = byKey("gloves_outline");
	public static final Material INVENTORY_FIRE = byKey("inventory_fire");

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
	// Unobtainable branches
	public static final Material BAMBOO_BRANCH = byKey("bamboo_branch");
	public static final Material CRIMSON_BRANCH = byKey("crimson_branch");
	public static final Material WARPED_BRANCH = byKey("warped_branch");

	// Rocks
	public static final Material ROCK = byKey("rock");
	public static final Material ANDESITE_ROCK = byKey("andesite_rock");
	public static final Material DIORITE_ROCK = byKey("diorite_rock");
	public static final Material GRANITE_ROCK = byKey("granite_rock");
	public static final Material SANDSTONE_ROCK = byKey("sandstone_rock");
	public static final Material RED_SANDSTONE_ROCK = byKey("red_sandstone_rock");
	public static final Material END_STONE_ROCK = byKey("end_stone_rock");
	public static final Material NETHERRACK_ROCK = byKey("netherrack_rock");

	// Block versions of rocks
	public static final Material PEBBLE = byKey("pebble");
	public static final Material ANDESITE_PEBBLE = byKey("andesite_pebble");
	public static final Material DIORITE_PEBBLE = byKey("diorite_pebble");
	public static final Material GRANITE_PEBBLE = byKey("granite_pebble");
	public static final Material SANDSTONE_PEBBLE = byKey("sandstone_pebble");
	public static final Material RED_SANDSTONE_PEBBLE = byKey("red_sandstone_pebble");
	public static final Material END_STONE_PEBBLE = byKey("end_stone_pebble");
	public static final Material NETHERRACK_PEBBLE = byKey("netherrack_pebble");
	public static final Material ICE_PEBBLE = byKey("ice_pebble");
	public static final Material GRAVEL_PEBBLE = byKey("gravel_pebble");
	public static final Material SOUL_SOIL_PEBBLE = byKey("soul_soil_pebble");

	public static final Material FIBER = byKey("fiber");
	public static final Material TWINE = byKey("twine");
	public static final Material ROUGH_STICK = byKey("rough_stick");
	public static final Material FLAKED_FLINT = byKey("flaked_flint");
	public static final Material HORSEHAIR = byKey("horsehair");

	public static final Material FLINT_KNIFE = byKey("flint_knife");
	public static final Material FLINT_AXE = byKey("flint_axe");
	public static final Material FLINT_SHOVEL = byKey("flint_shovel");
	public static final Material FLINT_PICKAXE = byKey("flint_pickaxe");
	public static final Material FLINT_SHEARS = byKey("flint_shears");
	public static final Material IRON_KNIFE = byKey("iron_knife");
	public static final Material GRASS_MESH = byKey("grass_mesh");
	public static final Material FIRESTRIKER = byKey("firestriker");
	public static final Material STEEL_AND_FLINT = byKey("steel_and_flint");

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

	// Barks
	public static final Material ACACIA_BARK = byKey("acacia_bark");
	public static final Material BIRCH_BARK = byKey("birch_bark");
	public static final Material CHERRY_BARK = byKey("cherry_bark");
	public static final Material DARK_OAK_BARK = byKey("dark_oak_bark");
	public static final Material JUNGLE_BARK = byKey("jungle_bark");
	public static final Material MANGROVE_BARK = byKey("mangrove_bark");
	public static final Material OAK_BARK = byKey("oak_bark");
	public static final Material PALE_OAK_BARK = byKey("pale_oak_bark");
	public static final Material SPRUCE_BARK = byKey("spruce_bark");
	public static final Material BAMBOO_BARK = byKey("bamboo_bark");
	public static final Material CRIMSON_BARK = byKey("crimson_bark");
	public static final Material WARPED_BARK = byKey("warped_bark");

	// Chopping Blocks
	public static final Material ACACIA_CHOPPING_BLOCK = byKey("acacia_chopping_block");
	public static final Material BIRCH_CHOPPING_BLOCK = byKey("birch_chopping_block");
	public static final Material CHERRY_CHOPPING_BLOCK = byKey("cherry_chopping_block");
	public static final Material DARK_OAK_CHOPPING_BLOCK = byKey("dark_oak_chopping_block");
	public static final Material JUNGLE_CHOPPING_BLOCK = byKey("jungle_chopping_block");
	public static final Material MANGROVE_CHOPPING_BLOCK = byKey("mangrove_chopping_block");
	public static final Material OAK_CHOPPING_BLOCK = byKey("oak_chopping_block");
	public static final Material PALE_OAK_CHOPPING_BLOCK = byKey("pale_oak_chopping_block");
	public static final Material SPRUCE_CHOPPING_BLOCK = byKey("spruce_chopping_block");
	public static final Material BAMBOO_CHOPPING_BLOCK = byKey("bamboo_chopping_block");
	public static final Material CRIMSON_CHOPPING_BLOCK = byKey("crimson_chopping_block");
	public static final Material WARPED_CHOPPING_BLOCK = byKey("warped_chopping_block");

	// Work Stations
	public static final Material ACACIA_WORK_STATION = byKey("acacia_work_station");
	public static final Material BIRCH_WORK_STATION = byKey("birch_work_station");
	public static final Material CHERRY_WORK_STATION = byKey("cherry_work_station");
	public static final Material DARK_OAK_WORK_STATION = byKey("dark_oak_work_station");
	public static final Material JUNGLE_WORK_STATION = byKey("jungle_work_station");
	public static final Material MANGROVE_WORK_STATION = byKey("mangrove_work_station");
	public static final Material OAK_WORK_STATION = byKey("oak_work_station");
	public static final Material PALE_OAK_WORK_STATION = byKey("pale_oak_work_station");
	public static final Material SPRUCE_WORK_STATION = byKey("spruce_work_station");
	public static final Material BAMBOO_WORK_STATION = byKey("bamboo_work_station");
	public static final Material CRIMSON_WORK_STATION = byKey("crimson_work_station");
	public static final Material WARPED_WORK_STATION = byKey("warped_work_station");

	// Glass Shards
	public static final Material GLASS_SHARD = byKey("glass_shard");
	public static final Material BLACK_GLASS_SHARD = byKey("black_glass_shard");
	public static final Material BLUE_GLASS_SHARD = byKey("blue_glass_shard");
	public static final Material BROWN_GLASS_SHARD = byKey("brown_glass_shard");
	public static final Material CYAN_GLASS_SHARD = byKey("cyan_glass_shard");
	public static final Material GRAY_GLASS_SHARD = byKey("gray_glass_shard");
	public static final Material GREEN_GLASS_SHARD = byKey("green_glass_shard");
	public static final Material LIGHT_BLUE_GLASS_SHARD = byKey("light_blue_glass_shard");
	public static final Material LIME_GLASS_SHARD = byKey("lime_glass_shard");
	public static final Material MAGENTA_GLASS_SHARD = byKey("magenta_glass_shard");
	public static final Material ORANGE_GLASS_SHARD = byKey("orange_glass_shard");
	public static final Material PINK_GLASS_SHARD = byKey("pink_glass_shard");
	public static final Material PURPLE_GLASS_SHARD = byKey("purple_glass_shard");
	public static final Material RED_GLASS_SHARD = byKey("red_glass_shard");
	public static final Material LIGHT_GRAY_GLASS_SHARD = byKey("light_gray_glass_shard");
	public static final Material WHITE_GLASS_SHARD = byKey("white_glass_shard");
	public static final Material YELLOW_GLASS_SHARD = byKey("yellow_glass_shard");

	public static final Material BAT_HIDE = byKey("bat_hide");
	public static final Material CAMEL_HIDE = byKey("camel_hide");
	public static final Material RED_CAT_HIDE = byKey("red_cat_hide");
	public static final Material COW_HIDE = byKey("cow_hide");
	public static final Material DONKEY_HIDE = byKey("donkey_hide");
	public static final Material FOX_HIDE = byKey("fox_hide");
	public static final Material SNOW_FOX_HIDE = byKey("snow_fox_hide");
	public static final Material GOAT_HIDE = byKey("goat_hide");
	public static final Material MULE_HIDE = byKey("mule_hide");
	public static final Material OCELOT_HIDE = byKey("ocelot_hide");
	public static final Material PANDA_HIDE = byKey("panda_hide");
	public static final Material PIG_HIDE = byKey("pig_hide");
	public static final Material POLAR_BEAR_HIDE = byKey("polar_bear_hide");
	public static final Material SHEEP_HIDE = byKey("sheep_hide");
	public static final Material WOLF_HIDE = byKey("wolf_hide");
	public static final Material CREAMY_HORSE_HIDE = byKey("creamy_horse_hide");
	public static final Material BROWN_LLAMA_HIDE = byKey("brown_llama_hide");
	public static final Material CREAMY_LLAMA_HIDE = byKey("creamy_llama_hide");
	public static final Material GRAY_LLAMA_HIDE = byKey("gray_llama_hide");
	public static final Material WHITE_LLAMA_HIDE = byKey("white_llama_hide");
	public static final Material BROWN_MOOSHROOM_HIDE = byKey("brown_mooshroom_hide");
	public static final Material RED_MOOSHROOM_HIDE = byKey("red_mooshroom_hide");

	public static final Material CLAY_KILN = byKey("clay_kiln");
	public static final Material SLEEPING_BAG = byKey("sleeping_bag");

	public static final Material SCRAP = byKey("scrap");
	public static final Material ASH = byKey("ash");
	public static final Material BALL_OF_MUD = byKey("ball_of_mud");
	public static final Material ICE_CUBE = byKey("ice_cube");
	public static final Material CHARCOAL_FILTER = byKey("charcoal_filter");
	public static final Material RAW_HIDE = byKey("raw_hide");

	public static final Material LETTER = byKey("letter");
	public static final Material PAPER_PLANE = byKey("paper_plane");

	public static final Material COBBLESTONE_HAMMER = byKey("cobblestone_hammer");
	public static final Material SANDSTONE_HAMMER = byKey("sandstone_hammer");
	public static final Material RED_SANDSTONE_HAMMER = byKey("red_sandstone_hammer");
	public static final Material TRUMPET = byKey("trumpet");
	public static final Material FLOWER_BOUQUET = byKey("flower_bouquet");

	public static final Material FILLED_BOWL = byKey("filled_bowl");
	public static final Material CACTUS_BOWL = byKey("cactus_bowl");
	public static final Material FILLED_CACTUS_BOWL = byKey("filled_cactus_bowl");
	public static final Material CANTEEN = byKey("canteen");
	public static final Material REINFORCED_CANTEEN = byKey("reinforced_canteen");
	public static final Material DRAGON_FLASK = byKey("dragon_flask");

	public static final Material TOTEMIC_STAFF = byKey("totemic_staff");
	public static final Material RATTLE = byKey("rattle");
	public static final Material FLUTE = byKey("flute");
	public static final Material ACACIA_DRUM = byKey("acacia_drum");
	public static final Material BIRCH_DRUM = byKey("birch_drum");
	public static final Material CHERRY_DRUM = byKey("cherry_drum");
	public static final Material DARK_OAK_DRUM = byKey("dark_oak_drum");
	public static final Material JUNGLE_DRUM = byKey("jungle_drum");
	public static final Material MANGROVE_DRUM = byKey("mangrove_drum");
	public static final Material OAK_DRUM = byKey("oak_drum");
	public static final Material PALE_OAK_DRUM = byKey("pale_oak_drum");
	public static final Material SPRUCE_DRUM = byKey("spruce_drum");
	public static final Material BAMBOO_DRUM = byKey("bamboo_drum");
	public static final Material CRIMSON_DRUM = byKey("crimson_drum");
	public static final Material WARPED_DRUM = byKey("warped_drum");
	public static final Material ACACIA_TOTEM_BASE = byKey("acacia_totem_base");
	public static final Material BIRCH_TOTEM_BASE = byKey("birch_totem_base");
	public static final Material CHERRY_TOTEM_BASE = byKey("cherry_totem_base");
	public static final Material DARK_OAK_TOTEM_BASE = byKey("dark_oak_totem_base");
	public static final Material JUNGLE_TOTEM_BASE = byKey("jungle_totem_base");
	public static final Material MANGROVE_TOTEM_BASE = byKey("mangrove_totem_base");
	public static final Material OAK_TOTEM_BASE = byKey("oak_totem_base");
	public static final Material PALE_OAK_TOTEM_BASE = byKey("pale_oak_totem_base");
	public static final Material SPRUCE_TOTEM_BASE = byKey("spruce_totem_base");
	public static final Material CRIMSON_TOTEM_BASE = byKey("crimson_totem_base");
	public static final Material WARPED_TOTEM_BASE = byKey("warped_totem_base");

	public static final Material BLAZE_GOODIE_BAG = byKey("blaze_goodie_bag");
	public static final Material CREEPER_GOODIE_BAG = byKey("creeper_goodie_bag");
	public static final Material DROWNED_GOODIE_BAG = byKey("drowned_goodie_bag");
	public static final Material ENDERMAN_GOODIE_BAG = byKey("enderman_goodie_bag");
	public static final Material GHAST_GOODIE_BAG = byKey("ghast_goodie_bag");
	public static final Material GUARDIAN_GOODIE_BAG = byKey("guardian_goodie_bag");
	public static final Material PHANTOM_GOODIE_BAG = byKey("phantom_goodie_bag");
	public static final Material SKELETON_GOODIE_BAG = byKey("skeleton_goodie_bag");
	public static final Material SLIME_GOODIE_BAG = byKey("slime_goodie_bag");
	public static final Material SPIDER_GOODIE_BAG = byKey("spider_goodie_bag");
	public static final Material ZOMBIE_GOODIE_BAG = byKey("zombie_goodie_bag");

	public static final Material BONEBREAKER_CANDY = byKey("bonebreaker_candy");
	public static final Material CHOCOLATE_SPIDER_EYE_CANDY = byKey("chocolate_spider_eye_candy");
	public static final Material DEADISH_FISH_CANDY = byKey("deadish_fish_candy");
	public static final Material EYECE_CREAM_CANDY = byKey("eyece_cream_candy");
	public static final Material FIREFINGERS_CANDY = byKey("firefingers_candy");
	public static final Material FIZZLERS_CANDY = byKey("fizzlers_candy");
	public static final Material MEMBRANE_BUTTER_CUPS_CANDY = byKey("membrane_butter_cups_candy");
	public static final Material PEARL_POP_CANDY = byKey("pearl_pop_candy");
	public static final Material SCREAMBURSTS_CANDY = byKey("screambursts_candy");
	public static final Material SLIME_GUM_CANDY = byKey("slime_gum_candy");
	public static final Material SOUR_PATCH_ZOMBIES_CANDY = byKey("sour_patch_zombies_candy");

	public static final Material WIND_IN_A_BOTTLE = byKey("wind_in_a_bottle");
	public static final Material HANG_GLIDER = byKey("hang_glider");
	public static final Material CHERRY_HANG_GLIDER = byKey("cherry_hang_glider");
	public static final Material SCULK_HANG_GLIDER = byKey("sculk_hang_glider");
	public static final Material AZALEA_HANG_GLIDER = byKey("azalea_hang_glider");
	public static final Material TATER_HANG_GLIDER = byKey("tater_hang_glider");
	public static final Material PHANTOM_HANG_GLIDER = byKey("phantom_hang_glider");

	public static final Material SLIME_BUCKET = byKey("slime_bucket");
	public static final Material MAGMA_CUBE_BUCKET = byKey("magma_cube_bucket");
	public static final Material FROG_BUCKET = byKey("frog_bucket");

	public static final Material LEATHER_GLOVES = byKey("leather_gloves");

	public static final Material DESTROY_STAGE_0 = byKey("destroy_stage_0");
	public static final Material DESTROY_STAGE_1 = byKey("destroy_stage_1");
	public static final Material DESTROY_STAGE_2 = byKey("destroy_stage_2");
	public static final Material DESTROY_STAGE_3 = byKey("destroy_stage_3");
	public static final Material DESTROY_STAGE_4 = byKey("destroy_stage_4");
	public static final Material DESTROY_STAGE_5 = byKey("destroy_stage_5");
	public static final Material DESTROY_STAGE_6 = byKey("destroy_stage_6");
	public static final Material DESTROY_STAGE_7 = byKey("destroy_stage_7");
	public static final Material DESTROY_STAGE_8 = byKey("destroy_stage_8");
	public static final Material DESTROY_STAGE_9 = byKey("destroy_stage_9");

	private static Material byKey(String value) {
		return KiterinoBootstrapMaterialInjector.injectMaterial(TrappedNewbie.trappedNewbieKey(value));
	}

}
