package me.sosedik.trappednewbie.dataset;

import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.api.font.FontData;
import me.sosedik.trappednewbie.TrappedNewbie;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class TrappedNewbieFonts {

	public static final FontData WANDERING_TRADER_HEAD = font("wandering_trader_head");

	private static FontData font(String key) {
		return ResourceLib.requireFontData(TrappedNewbie.trappedNewbieKey(key));
	}

}
