package me.sosedik.utilizer.impl.message.tag;

import me.sosedik.utilizer.Utilizer;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.api.message.Mini;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record DiscordResolver(@NotNull Messenger messenger) implements TagResolver {

	private static final String DISCORD_URL = Objects.requireNonNull(Utilizer.instance().getConfig().getString("discord", "discord.com"));

	@Override
	public @Nullable Tag resolve(@NotNull String name, @NotNull ArgumentQueue args, @NotNull Context ctx) {
		if (!has(name)) return null;
		Component render = messenger.getMessage("placeholder.discord", Mini.raw("link", DISCORD_URL), Mini.raw("full_link", DISCORD_URL.startsWith("http") ? DISCORD_URL : "https://" + DISCORD_URL));
		return Tag.selfClosingInserting(render);
	}

	@Override
	public boolean has(@NotNull String name) {
		return name.equals("discord");
	}

	/**
	 * Constructs Discord tag
	 *
	 * @return Discord tag
	 */
	public static @NotNull String discordTag() {
		return "<discord>";
	}

}
