package me.sosedik.uglychatter.api.mini.tag;

import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.util.SpacingUtil;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.api.message.Mini;
import me.sosedik.utilizer.util.ChatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static me.sosedik.uglychatter.UglyChatter.uglyChatterKey;
import static me.sosedik.utilizer.api.message.Mini.combine;
import static net.kyori.adventure.text.Component.newline;

@NullMarked
public record CopyTag(Messenger messenger) implements TagResolver {

	public static final Component COPY_ICON = Mini.asIcon(ResourceLib.requireFontData(uglyChatterKey("copy")).mapping());

	@Override
	public @Nullable Tag resolve(String name, ArgumentQueue arguments, Context ctx) {
		if (!has(name)) return null;
		String display = arguments.hasNext() ? arguments.pop().value() : "<kaomoji:surprise>";
		Component parsedDisplay = ctx.deserialize(display).colorIfAbsent(NamedTextColor.GRAY);
		Component copy = Component.text()
			.content("")
			.hoverEvent(
				combine(newline(), SpacingUtil.iconize(messenger, COPY_ICON, "tag.copy.click_to_copy"))
			)
			.clickEvent(
				ClickEvent.copyToClipboard(ChatUtil.getPlainText(parsedDisplay))
			)
			.append(
				COPY_ICON,
				SpacingUtil.getSpacing(2),
				parsedDisplay
			)
			.build();
		return Tag.selfClosingInserting(copy);
	}

	@Override
	public boolean has(String name) {
		return "cp".equals(name) || "copy".equals(name);
	}

}
