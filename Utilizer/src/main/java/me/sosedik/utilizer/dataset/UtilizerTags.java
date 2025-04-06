package me.sosedik.utilizer.dataset;

import me.sosedik.utilizer.Utilizer;
import me.sosedik.utilizer.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class UtilizerTags {

	/**
	 * @see ItemUtil#isHot(ItemStack)
	 */
	public static final Tag<Material> HOT_ITEMS = itemTag("hot_items");
	/**
	 * @see ItemUtil#isLightSource(ItemStack)
	 */
	public static final Tag<Material> LIGHT_SOURCES = itemTag("light_sources");
	public static final Tag<Material> NO_TOOLTIP_ITEMS = itemTag("no_tooltip_items");
	public static final Tag<Material> NOT_DROPPABLE = itemTag("not_droppable");
	public static final Tag<Material> NO_ARROW_USABLE = itemTag("no_arrow_usable");
	public static final Tag<Material> FLINT_AND_STEEL = itemTag("flint_and_steel");
	public static final Tag<Material> SHEARS = itemTag("shears");

	private static Tag<Material> itemTag(String key) {
		return ItemUtil.itemTag(Utilizer.utilizerKey(key));
	}

}
