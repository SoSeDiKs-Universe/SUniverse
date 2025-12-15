package me.sosedik.miscme.dataset;

import me.sosedik.miscme.MiscMe;
import me.sosedik.utilizer.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class MiscMeTags {

	public static final Tag<Material> DURABILITY_CAPACITY_TOOLTIP = itemTag("durability_capacity");
	public static final Tag<Material> FAKE_DYEABLE = itemTag("fake_dyeable");
	public static final Tag<Material> RESETTABLE_BOTTLE_ITEMS = itemTag("resettable_bottled_items");
	public static final Tag<Material> COAL_EXPLOSION_EXTRA_TOOL_TRIGGERS = itemTag("coal_explosion_extra_tool_triggers");

	private static Tag<Material> itemTag(String key) {
		return ItemUtil.itemTag(MiscMe.miscMeKey(key));
	}

}
