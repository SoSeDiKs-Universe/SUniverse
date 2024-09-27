package me.sosedik.uglychatter.api.mini.tag;

import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.util.SpacingUtil;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.api.message.Mini;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static me.sosedik.uglychatter.UglyChatter.uglyChatterKey;
import static me.sosedik.utilizer.api.message.Mini.combine;
import static net.kyori.adventure.text.Component.newline;

public record ExecuteTag(@NotNull Messenger messenger) implements TagResolver {

	public static final Component COMMAND_ICON = Mini.asIcon(ResourceLib.requireFontData(uglyChatterKey("command")).mapping());

	@Override
	public @Nullable Tag resolve(@NotNull String name, @NotNull ArgumentQueue arguments, @NotNull Context ctx) {
		if (!has(name)) return null;
		String command = arguments.popOr("Command tag requires command argument").value();
		command = command.startsWith("/") ? command : "/" + command;
		Component copy = Component.text()
			.content("")
			.hoverEvent(
				combine(newline(), SpacingUtil.iconize(messenger, COMMAND_ICON, "tag.execute.click_to_run"))
			)
			.clickEvent(
					ClickEvent.runCommand(command)
			)
			.append(
				COMMAND_ICON,
				SpacingUtil.getSpacing(2),
				Component.text(command, NamedTextColor.GRAY)
			)
			.build();
		return Tag.selfClosingInserting(copy);
	}

	@Override
	public boolean has(@NotNull String name) {
		return "cd".equals(name) || "command".equals(name) || "execute".equals(name);
	}

}
