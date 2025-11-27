package me.sosedik.resourcelib.api.font;

import me.sosedik.resourcelib.util.SpacingUtil;
import me.sosedik.utilizer.api.message.Mini;
import me.sosedik.utilizer.util.ChatUtil;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record FontData(
	Component mapping,
	Component offsetMapping,
	Component icon,
	int width
) {

	public FontData(
		Component mapping,
		int width
	) {
		this(mapping, SpacingUtil.getOffset(0, width + 1, mapping), Mini.asIcon(mapping), width);
	}

	public Component offsetMapping(int offset) {
		return SpacingUtil.getOffset(offset, width() + 1, mapping());
	}

	public String rawMapping() {
		return ChatUtil.getPlainText(mapping());
	}

}
