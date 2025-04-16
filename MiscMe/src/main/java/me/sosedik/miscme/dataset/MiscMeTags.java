package me.sosedik.miscme.dataset;

import me.sosedik.miscme.MiscMe;
import me.sosedik.utilizer.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class MiscMeTags {

	public static final Tag<Material> DURABILITY_CAPACITY_TOOLTIP = itemTag("durability_capacity");

	private static Tag<Material> itemTag(String key) {
		return ItemUtil.itemTag(MiscMe.miscMeKey(key));
	}

}
