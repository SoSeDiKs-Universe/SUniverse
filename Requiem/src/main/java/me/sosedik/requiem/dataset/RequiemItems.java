package me.sosedik.requiem.dataset;

import me.sosedik.kiterino.util.KiterinoBootstrapMaterialInjector;
import me.sosedik.requiem.Requiem;
import org.bukkit.Material;
import org.jspecify.annotations.NullMarked;

@NullMarked
@SuppressWarnings("unused")
public final class RequiemItems {

	public static final Material SADDLE_OUTLINE = byKey("saddle_outline");

	public static final Material GHOST_MOTIVATOR = byKey("ghost_motivator");
	public static final Material GHOST_RELOCATOR = byKey("ghost_relocator");
	public static final Material HOST_REVOCATOR = byKey("host_revocator");

	public static final Material CREEPER_HEART = byKey("creeper_heart");

	public static final Material HEART = byKey("heart");
	public static final Material SPINE = byKey("spine");

	public static final Material ACROBAT_SKELETON_TOMBSTONE = byKey("acrobat_skeleton_tombstone");
	public static final Material ARROWS_SKELETON_TOMBSTONE = byKey("arrows_skeleton_tombstone");
	public static final Material BASIC_SKELETON_TOMBSTONE = byKey("basic_skeleton_tombstone");
	public static final Material BOW_SKELETON_TOMBSTONE = byKey("bow_skeleton_tombstone");
	public static final Material BURIED_SKELETON_TOMBSTONE = byKey("buried_skeleton_tombstone");
	public static final Material BURNT_SKELETON_TOMBSTONE = byKey("burnt_skeleton_tombstone");
	public static final Material CACTUS_SKELETON_TOMBSTONE = byKey("cactus_skeleton_tombstone");
	public static final Material CHORUS_SKELETON_TOMBSTONE = byKey("chorus_skeleton_tombstone");
	public static final Material CHORUS_TANGLED_SKELETON_TOMBSTONE = byKey("chorus_tangled_skeleton_tombstone");
	public static final Material COMMON_SKELETON_TOMBSTONE = byKey("common_skeleton_tombstone");
	public static final Material CORRUPTED_SKELETON_TOMBSTONE = byKey("corrupted_skeleton_tombstone");
	public static final Material CRIMSON_TANGLED_SKELETON_TOMBSTONE = byKey("crimson_tangled_skeleton_tombstone");
	public static final Material CRYSTAL_SKELETON_TOMBSTONE = byKey("crystal_skeleton_tombstone");
	public static final Material DEVASTATED_SKELETON_TOMBSTONE = byKey("devastated_skeleton_tombstone");
	public static final Material DRAGON_BURNT_SKELETON_TOMBSTONE = byKey("dragon_burnt_skeleton_tombstone");
	public static final Material DRIPSTONE_SKELETON_TOMBSTONE = byKey("dripstone_skeleton_tombstone");
	public static final Material DUELIST_SKELETON_TOMBSTONE = byKey("duelist_skeleton_tombstone");
	public static final Material DUNGEON_CRAWLER_SKELETON_TOMBSTONE = byKey("dungeon_crawler_skeleton_tombstone");
	public static final Material DUSTY_SKELETON_TOMBSTONE = byKey("dusty_skeleton_tombstone");
	public static final Material EXPLORER_SKELETON_TOMBSTONE = byKey("explorer_skeleton_tombstone");
	public static final Material FROZEN_WATERDROP_SKELETON_TOMBSTONE = byKey("frozen_waterdrop_skeleton_tombstone");
	public static final Material FUNGUS_GATHERER_SKELETON_TOMBSTONE = byKey("fungus_gatherer_skeleton_tombstone");
	public static final Material HEADACHE_SKELETON_TOMBSTONE = byKey("headache_skeleton_tombstone");
	public static final Material HEADLESS_SKELETON_TOMBSTONE = byKey("headless_skeleton_tombstone");
	public static final Material HUNTED_SKELETON_TOMBSTONE = byKey("hunted_skeleton_tombstone");
	public static final Material LUCKY_SKELETON_TOMBSTONE = byKey("lucky_skeleton_tombstone");
	public static final Material MELTED_SKELETON_TOMBSTONE = byKey("melted_skeleton_tombstone");
	public static final Material MOSSY_SKELETON_TOMBSTONE = byKey("mossy_skeleton_tombstone");
	public static final Material MUSHROOMER_SKELETON_TOMBSTONE = byKey("mushroomer_skeleton_tombstone");
	public static final Material NEUTRALIZED_SKELETON_TOMBSTONE = byKey("neutralized_skeleton_tombstone");
	public static final Material PIERCED_SKELETON_TOMBSTONE = byKey("pierced_skeleton_tombstone");
	public static final Material POWDER_SNOW_SKELETON_TOMBSTONE = byKey("powder_snow_skeleton_tombstone");
	public static final Material QUICKSAND_SKELETON_TOMBSTONE = byKey("quicksand_skeleton_tombstone");
	public static final Material ROOKIE_SKELETON_TOMBSTONE = byKey("rookie_skeleton_tombstone");
	public static final Material SHOT_SKELETON_TOMBSTONE = byKey("shot_skeleton_tombstone");
	public static final Material SHROOM_SKELETON_TOMBSTONE = byKey("shroom_skeleton_tombstone");
	public static final Material SKELETON_FROM_THE_SKY_TOMBSTONE = byKey("skeleton_from_the_sky_tombstone");
	public static final Material SLAIN_SKELETON_TOMBSTONE = byKey("slain_skeleton_tombstone");
	public static final Material SNOW_SKELETON_TOMBSTONE = byKey("snow_skeleton_tombstone");
	public static final Material SPIDER_VICTIM_SKELETON_TOMBSTONE = byKey("spider_victim_skeleton_tombstone");
	public static final Material STALACTITE_SKELETON_TOMBSTONE = byKey("stalactite_skeleton_tombstone");
	public static final Material SWIMMER_SKELETON_TOMBSTONE = byKey("swimmer_skeleton_tombstone");
	public static final Material THIEF_SKELETON_TOMBSTONE = byKey("thief_skeleton_tombstone");
	public static final Material TRIDENT_SKELETON_TOMBSTONE = byKey("trident_skeleton_tombstone");
	public static final Material UNSAVED_SKELETON_TOMBSTONE = byKey("unsaved_skeleton_tombstone");
	public static final Material VINES_TANGLED_SKELETON_TOMBSTONE = byKey("vines_tangled_skeleton_tombstone");
	public static final Material WATERDROP_SKELETON_TOMBSTONE = byKey("waterdrop_skeleton_tombstone");
	public static final Material WEBBED_SKELETON_TOMBSTONE = byKey("webbed_skeleton_tombstone");
	public static final Material WINTER_SKELETON_TOMBSTONE = byKey("winter_skeleton_tombstone");

	private static Material byKey(String value) {
		return KiterinoBootstrapMaterialInjector.injectMaterial(Requiem.requiemKey(value));
	}

}
