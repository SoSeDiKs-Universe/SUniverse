package me.sosedik.resourcelib.api.font;

import me.sosedik.resourcelib.util.SpacingUtil;
import me.sosedik.utilizer.util.ChatUtil;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public record FontData(
	@NotNull String key,
	@NotNull Component mapping,
	@NotNull Component offsetMapping,
	int width
) {

	public FontData(
		@NotNull String key,
		@NotNull Component mapping,
		int width
	) {
		this(key, mapping, SpacingUtil.getOffset(0, width + 1, mapping), width);
	}

	public @NotNull Component offsetMapping(int offset) {
		return SpacingUtil.getOffset(offset, width() + 1, mapping());
	}

	public @NotNull String rawMapping() {
		return ChatUtil.getPlainText(mapping());
	}

}
