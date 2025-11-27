package me.sosedik.utilizer.impl.message.tag;

import me.sosedik.utilizer.Utilizer;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.api.message.Mini;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public record DiscordResolver(Messenger messenger) implements TagResolver {

	public static final String DISCORD_URL = Objects.requireNonNull(Utilizer.instance().getConfig().getString("discord", "discord.com"));

	@Override
	public @Nullable Tag resolve(String name, ArgumentQueue args, Context ctx) {
		if (!has(name)) return null;

		if ("ds".equals(name))
			return Tag.selfClosingInserting(Component.text(DISCORD_URL));

		Component render = messenger.getMessage("placeholder.discord", Mini.raw("link", DISCORD_URL), Mini.raw("full_link", DISCORD_URL.startsWith("http") ? DISCORD_URL : "https://" + DISCORD_URL));
		return Tag.selfClosingInserting(render);
	}

	@Override
	public boolean has(String name) {
		return name.equals("discord") || name.equals("ds");
	}

	/**
	 * Constructs Discord tag
	 *
	 * @return Discord tag
	 */
	public static String discordTag() {
		return "<discord>";
	}

	/**
	 * Constructs raw Discord tag
	 *
	 * @return Discord tag
	 */
	public static String dsTag() {
		return "<ds>";
	}

}
