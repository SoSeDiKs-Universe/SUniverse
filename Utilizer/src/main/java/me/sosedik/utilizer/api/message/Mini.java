package me.sosedik.utilizer.api.message;

import me.sosedik.utilizer.Utilizer;
import me.sosedik.utilizer.impl.message.tag.KaomojiTag;
import me.sosedik.utilizer.impl.message.tag.LocaleResolver;
import me.sosedik.utilizer.impl.message.tag.RandomColorTag;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.TagPattern;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Mini {

	private Mini() {
		throw new IllegalStateException("Utility class");
	}

	private static final MiniMessage MINI_MESSAGE = buildMini();

	@SafeVarargs
	public static <T extends ComponentLike> @NotNull List<@NotNull T> combined(@NotNull Collection<T>... components) {
		List<T> componentList = new ArrayList<>();
		for (Collection<T> collection : components) {
			componentList.addAll(collection);
		}
		return componentList;
	}

	public static @NotNull Component combined(@NotNull ComponentLike... components) {
		return Component.join(JoinConfiguration.noSeparators(), components);
	}

	public static <T extends ComponentLike> @NotNull Component combined(@NotNull Collection<T> components) {
		return Component.join(JoinConfiguration.noSeparators(), components);
	}

	public static @NotNull Component combine(@NotNull Component separator, @NotNull ComponentLike... components) {
		return Component.join(JoinConfiguration.separator(separator), components);
	}

	public static <T extends ComponentLike> @NotNull Component combine(@NotNull Component separator, @NotNull Collection<T> components) {
		return Component.join(JoinConfiguration.separator(separator), components);
	}

	public static @NotNull TagResolver.Single raw(@TagPattern @NotNull String key, int value) {
		return raw(key, String.valueOf(value));
	}

	public static @NotNull TagResolver.Single raw(@TagPattern @NotNull String key, double value) {
		return raw(key, String.valueOf(value));
	}

	public static @NotNull TagResolver.Single raw(@TagPattern @NotNull String key, @NotNull String value) {
		return Placeholder.unparsed(key, value);
	}

	public static @NotNull TagResolver.Single raw(@TagPattern @NotNull String key, @NotNull Component value) {
		return Placeholder.component(key, value);
	}

	public static @NotNull TagResolver.Single component(@TagPattern @NotNull String key, @NotNull Component value) {
		return Placeholder.component(key, value);
	}

	public static @NotNull Component mini(@NotNull String text) {
		return mini(MINI_MESSAGE, text);
	}

	public static @NotNull Component mini(@NotNull String text, @NotNull TagResolver... resolvers) {
		return mini(MINI_MESSAGE, text, resolvers);
	}

	public static @NotNull Component mini(@NotNull Audience viewer, @NotNull String text) {
		return mini(buildMini(Messenger.messenger(viewer)), text);
	}

	public static @NotNull Component mini(@NotNull Audience viewer, @NotNull String text, @NotNull TagResolver... resolvers) {
		return mini(buildMini(Messenger.messenger(viewer)), text, resolvers);
	}

	public static @NotNull Component mini(@NotNull MiniMessage miniMessage, @NotNull String text) {
		return miniMessage.deserialize(text);
	}

	public static @NotNull Component mini(@NotNull MiniMessage miniMessage, @NotNull String text, @NotNull TagResolver... resolvers) {
		return miniMessage.deserialize(text, TagResolver.resolver(resolvers));
	}

	public static @NotNull MiniMessage buildMini() {
		return MiniMessage.builder().tags(TagResolver.resolver(TagResolver.standard(), getDefaultTags())).build();
	}

	public static @NotNull MiniMessage buildMini(@NotNull Messenger messenger) {
		return MiniMessage.builder().tags(TagResolver.resolver(TagResolver.standard(), getDefaultTags(), getPlaceholders(messenger))).build();
	}

	private static @NotNull TagResolver getDefaultTags() {
		return TagResolver.resolver(
				TagResolver.resolver("discord", Tag.selfClosingInserting(Component.text(Utilizer.instance().getConfig().getString("discord", "discord.com")))), // TODO unhardcode
				KaomojiTag.KAOMOJI,
				RandomColorTag.RANDOM_COLOR
//				PluralTag.PLURALS,
//				SpoilerTag.SPOILER,
//				IconTag.ICON,
//				TagResolver.resolver("ispace", Tag.selfClosingInserting(SpacingUtil.ICON_SPACE))
		);
	}

	private static @NotNull TagResolver getPlaceholders(@NotNull Messenger messenger) {
		var resolver = TagResolver.resolver(
//				new CopyResolver(messenger),
//				new ExecuteResolver(messenger),
			new LocaleResolver(messenger)
		);
//		if (messenger.getAudience() instanceof Player player && player.isOp())
//			resolver = TagResolver.resolver(resolver, SpaceTag.SPACE);
		return resolver;
	}

	public static @NotNull Component iconize(@NotNull String content) {
		return iconize(content, false);
	}

	private static final TextColor SHADOWLESS_COLOR = TextColor.fromHexString("#4e5c24");
	public static @NotNull Component iconize(@NotNull String content, boolean removeShadow) {
		return Component.text().content(content)
//				.font(ChatUtil.ICONS_FONT)
				.color(removeShadow ? SHADOWLESS_COLOR : NamedTextColor.WHITE)
				.decoration(TextDecoration.ITALIC, false)
				.build();
	}

	public static @NotNull Component iconify(@NotNull String content) {
		return Component.text().content(content)
				.color(NamedTextColor.WHITE)
				.decoration(TextDecoration.ITALIC, false)
				.build();
	}

	public static @NotNull String serialize(@NotNull Component component) {
		return buildMini().serialize(component);
	}

}
