package me.sosedik.trappednewbie.dataset;

import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.trappednewbie.TrappedNewbie;
import org.bukkit.NamespacedKey;

public class TrappedNewbieSoundKeys {

	public static final NamespacedKey FLAKE_SUCCESS_SOUND = sound("item/craft_flake_success");
	public static final NamespacedKey FLAKE_FAIL_SOUND = sound("item/craft_flake_fail");
	public static final NamespacedKey WOOD_CHOP_SUCCESS_SOUND = sound("block/wood_chop");
	public static final NamespacedKey WOOD_CHOP_FAIL_SOUND = sound("block/wood_chop_fail");
	public static final NamespacedKey TRUMPET_DOOT = sound("item/doot_doot");
	public static final NamespacedKey GOODIE_BAG_OPEN_SOUND = sound("item/goodie_bag_open");
	public static final NamespacedKey HAND_GLIDER_OPEN = sound("item/hand_glider_open");
	public static final NamespacedKey AMBIENT_WIND = sound("ambient/wind");

	private static NamespacedKey sound(String key) {
		return ResourceLib.getSound(TrappedNewbie.trappedNewbieKey(key));
	}

}
