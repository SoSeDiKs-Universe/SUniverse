package me.sosedik.resourcelib.impl.item;

import org.bukkit.Material;
import org.bukkit.Tag;

import static me.sosedik.resourcelib.ResourceLib.resourceLibKey;
import static me.sosedik.utilizer.util.ItemUtil.itemTag;

/**
 * Item tags registered under ResourceLib's namespace
 */
public class RLibItemTags {

	/**
	 * Drinks that contain milk in them
	 */
	public static final Tag<Material> MILK_DRINKABLES = itemTag(resourceLibKey("milk_drinkables"));

}
