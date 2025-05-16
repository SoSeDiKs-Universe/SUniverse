package me.sosedik.requiem.dataset;

import me.sosedik.requiem.Requiem;
import me.sosedik.utilizer.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.Tag;

public class RequiemTags {

	public static final Tag<Material> TOMBSTONES = blockTag("tombstones");
	public static final Tag<Material> SKELETON_SPAWNING_TOMBSTONES = blockTag("skeleton_spawning_tombstones");
	public static final Tag<Material> SILVERFISH_SPAWNING_TOMBSTONES = blockTag("silverfish_spawning_tombstones");
	public static final Tag<Material> WITHER_SKELETON_TOMBSTONES = blockTag("wither_skeleton_tombstones");
	public static final Tag<Material> STRAY_SKELETON_TOMBSTONES = blockTag("stray_skeleton_tombstones");

	private static Tag<Material> blockTag(String key) {
		return ItemUtil.blockTag(Requiem.requiemKey(key));
	}

}
