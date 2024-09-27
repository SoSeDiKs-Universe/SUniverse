package me.sosedik.uglychatter.api.mini.tag;

import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.util.SpacingUtil;
import me.sosedik.utilizer.api.message.Mini;
import me.sosedik.utilizer.util.ChatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.Modifying;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import static me.sosedik.uglychatter.UglyChatter.uglyChatterKey;
import static me.sosedik.utilizer.api.message.Mini.combined;

public record SpoilerTag(boolean withCopy) implements Modifying {

	public static final Component SPOILER_ICON = Mini.asIcon(ResourceLib.requireFontData(uglyChatterKey("spoiler")).mapping());

	private static final SpoilerTag SPOILER_TAG = new SpoilerTag(false);
	private static final SpoilerTag SPOILER_WITH_COPY_TAG = new SpoilerTag(true);
	public static final TagResolver SPOILER = TagResolver.resolver(
		TagResolver.resolver("sp", SPOILER_TAG),
		TagResolver.resolver("spoiler", SPOILER_TAG)
	);
	public static final TagResolver SPOILER_WITH_COPY = TagResolver.resolver(
		TagResolver.resolver("spc", SPOILER_WITH_COPY_TAG),
		TagResolver.resolver("spoiler_copy", SPOILER_WITH_COPY_TAG)
	);

	@Override
	public @NotNull Component apply(@NotNull Component current, int depth) {
		if (depth != 0) return Component.empty();
		return Component.text()
			.content("")
			.hoverEvent(current)
			.clickEvent(
				withCopy ? ClickEvent.copyToClipboard(ChatUtil.getPlainText(current)) : null
			)
			.append(
				withCopy ? combined(SPOILER_ICON, CopyTag.COPY_ICON) : SPOILER_ICON,
				SpacingUtil.getSpacing(2),
				Component.text("[…?]", NamedTextColor.GRAY)
			)
			.build();
	}

}
