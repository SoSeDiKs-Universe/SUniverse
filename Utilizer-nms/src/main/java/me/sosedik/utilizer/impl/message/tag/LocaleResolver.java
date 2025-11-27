package me.sosedik.utilizer.impl.message.tag;

import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record LocaleResolver(Messenger messenger) implements TagResolver {

	@Override
	public @Nullable Tag resolve(String name, ArgumentQueue args, Context ctx) {
		if (!has(name)) return null;
		String messagePath = args.popOr("Missing message path").value();
		return Tag.selfClosingInserting(messenger.getMessage(messagePath));
	}

	@Override
	public boolean has(String name) {
		return name.equals("locale");
	}

	/**
	 * Constructs locale tag
	 *
	 * @param key locale key
	 * @return locale tag
	 */
	public static String localeTag(String key) {
		return "<locale:" + key + ">";
	}

}
