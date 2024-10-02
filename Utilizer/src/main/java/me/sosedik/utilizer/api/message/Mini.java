package me.sosedik.utilizer.api.message;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.TagPattern;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class Mini {

	private Mini() {
		throw new IllegalStateException("Utility class");
	}

	private static final List<TagResolver> DEFAULT_RESOLVERS = new ArrayList<>();
	private static final List<Function<Messenger, @Nullable TagResolver>> DEFAULT_VIEWER_AWARE_RESOLVERS = new ArrayList<>();

	@SafeVarargs
	public static <T extends ComponentLike> @NotNull List<@NotNull T> combined(@NotNull Collection<T>... components) {
		List<T> componentList = new ArrayList<>();
		for (Collection<T> collection : components) {
			componentList.addAll(collection);
		}
		return componentList;
	}

	public static @NotNull Component combined(@NotNull ComponentLike... components) {
		return Component.textOfChildren(components);
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
		return mini(buildMini(), text);
	}

	public static @NotNull Component mini(@NotNull String text, @NotNull TagResolver... resolvers) {
		return mini(buildMini(), text, resolvers);
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

	private static @NotNull TagResolver getPlaceholders(@NotNull Messenger messenger) {
		List<TagResolver> resolvers = new ArrayList<>();
		for (Function<Messenger, TagResolver> provider : DEFAULT_VIEWER_AWARE_RESOLVERS) {
			TagResolver resolver = provider.apply(messenger);
			if (resolver != null)
				resolvers.add(resolver);
		}
		return TagResolver.resolver(resolvers);
	}

	public static @NotNull Component asIcon(@NotNull Component content) {
		return content.colorIfAbsent(NamedTextColor.WHITE)
				.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
	}

	/**
	 * Builds a mini message instance with registered global placeholders
	 *
	 * @return a mini message instance
	 */
	public static @NotNull MiniMessage buildMini() {
		return MiniMessage.builder().tags(TagResolver.resolver(TagResolver.standard(), TagResolver.resolver(DEFAULT_RESOLVERS))).build();
	}

	/**
	 * Builds a mini message instance with registered global and viewer-aware placeholders
	 *
	 * @param messenger viewer
	 * @return a mini message instance
	 */
	public static @NotNull MiniMessage buildMini(@NotNull Messenger messenger) {
		return MiniMessage.builder().tags(TagResolver.resolver(TagResolver.standard(), TagResolver.resolver(DEFAULT_RESOLVERS), getPlaceholders(messenger))).build();
	}

	/**
	 * Adds new tag resolvers
	 *
	 * @param tagResolvers tag resolvers
	 */
	public static void registerTagResolvers(@NotNull TagResolver... tagResolvers) {
		DEFAULT_RESOLVERS.addAll(List.of(tagResolvers));
	}

	/**
	 * Adds new viewer-aware tag resolvers
	 *
	 * @param tagResolvers viewer-aware tag resolvers
	 */
	@SafeVarargs
	public static void registerViewerAwareTagResolvers(@NotNull Function<Messenger, @Nullable TagResolver>... tagResolvers) {
		DEFAULT_VIEWER_AWARE_RESOLVERS.addAll(List.of(tagResolvers));
	}

}
