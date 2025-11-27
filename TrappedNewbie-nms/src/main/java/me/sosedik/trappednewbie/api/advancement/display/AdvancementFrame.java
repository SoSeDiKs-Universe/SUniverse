package me.sosedik.trappednewbie.api.advancement.display;

import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.api.font.FontData;
import me.sosedik.trappednewbie.TrappedNewbie;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

public enum AdvancementFrame {

	NONE(null),
	TASK(null),
	CHALLENGE(null),
	GOAL,
	ARROW_DOWN,
	ARROW_LEFT,
	ARROW_RIGHT,
	ARROW_UP,
	BLOCK,
	BUTTERFLY,
	CIRCLE,
	CRESTED,
	HEART,
	SHARP,
	SPEECH_BUBBLE,
	SQUIRCLE,
	STAR;

	private final @Nullable NamespacedKey obtainedKey;
	private final @Nullable NamespacedKey unobtainedKey;
	private final @Nullable FontData obtainedFontData;
	private final @Nullable FontData unobtainedFontData;

	AdvancementFrame() {
		this.obtainedKey = ResourceLib.storage().getItemModelMapping(TrappedNewbie.trappedNewbieKey("advancement/" + name().toLowerCase(Locale.US) + "_frame_obtained"));
		this.unobtainedKey = ResourceLib.storage().getItemModelMapping(TrappedNewbie.trappedNewbieKey("advancement/" + name().toLowerCase(Locale.US) + "_frame_unobtained"));
		this.obtainedFontData = ResourceLib.requireFontData(TrappedNewbie.trappedNewbieKey("advancement/" + name().toLowerCase(Locale.US) + "_frame_obtained"));
		this.unobtainedFontData = ResourceLib.requireFontData(TrappedNewbie.trappedNewbieKey("advancement/" + name().toLowerCase(Locale.US) + "_frame_unobtained"));
	}

	AdvancementFrame(@Nullable String id) {
		this.obtainedKey = id == null ? null : ResourceLib.storage().getItemModelMapping(TrappedNewbie.trappedNewbieKey("advancement/" + id + "_frame_obtained"));
		this.unobtainedKey = id == null ? null : ResourceLib.storage().getItemModelMapping(TrappedNewbie.trappedNewbieKey("advancement/" + id + "_frame_unobtained"));
		this.obtainedFontData = id == null ? null : ResourceLib.requireFontData(TrappedNewbie.trappedNewbieKey("advancement/" + id + "_frame_obtained"));
		this.unobtainedFontData = id == null ? null : ResourceLib.requireFontData(TrappedNewbie.trappedNewbieKey("advancement/" + id + "_frame_unobtained"));
	}

	public boolean requiresBackground() {
		return this.obtainedKey != null;
	}

	public @Nullable NamespacedKey getItemModelKey(boolean obtained) {
		return obtained ? this.obtainedKey : this.unobtainedKey;
	}

	public @Nullable FontData getFontData(boolean obtained) {
		return obtained ? this.obtainedFontData : this.unobtainedFontData;
	}

}
