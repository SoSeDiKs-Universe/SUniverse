package me.sosedik.utilizer.impl.message.tag;

import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record LocaleResolver(@NotNull Messenger messenger) implements TagResolver {

	@Override
	public @Nullable Tag resolve(@NotNull String name, @NotNull ArgumentQueue args, @NotNull Context ctx) {
		if (!has(name)) return null;
		String messagePath = args.popOr("Missing message path").value();
		return Tag.selfClosingInserting(messenger.getMessage(messagePath));
	}

	@Override
	public boolean has(@NotNull String name) {
		return name.equals("locale");
	}

	/**
	 * Constructs locale tag
	 *
	 * @param key locale key
	 * @return locale tag
	 */
	public static @NotNull String localeTag(@NotNull String key) {
		return "<locale:" + key + ">";
	}

}
