package me.sosedik.utilizer.impl.message.tag;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KaomojiTag {

	private KaomojiTag() {}

	public static final TagResolver KAOMOJI = TagResolver.resolver("kaomoji", KaomojiTag::create);

	private static final Random RANDOM = new Random();

	static @NotNull Tag create(@NotNull ArgumentQueue args, @NotNull Context ctx) {
		if (!args.hasNext())
			throw ctx.newException("Can't turn " + args + " into kaomoji", args);

		List<KaomojiType> kaomojiTypes = new ArrayList<>();
		while (args.hasNext()) {
			String rawKaomojiType = args.pop().value();
			KaomojiType kaomojiType = KaomojiType.getKaomoji(rawKaomojiType);
			if (kaomojiType == null)
				throw ctx.newException("Unknown kaomoji type: " + rawKaomojiType, args);
			kaomojiTypes.add(kaomojiType);
		}

		return Tag.selfClosingInserting(Component.text(kaomojiTypes.get(RANDOM.nextInt(kaomojiTypes.size())).getKaomoji()));
	}

	public enum KaomojiType {

		PAIN("~(>_<~)", "(×﹏×)"),
		SURPRISE("(⊙_⊙)", "Σ(°ロ°)");

		private final String[] kaomojis;

		KaomojiType(@NotNull String... kaomojis) {
			this.kaomojis = kaomojis;
		}

		public @NotNull String getKaomoji() {
			return kaomojis[RANDOM.nextInt(kaomojis.length)];
		}

		public static @Nullable KaomojiType getKaomoji(@NotNull String kaomojiType) {
			kaomojiType = kaomojiType.toUpperCase();
			for (KaomojiType type : values()) {
				if (type.toString().equals(kaomojiType))
					return type;
			}
			return null;
		}

	}

}