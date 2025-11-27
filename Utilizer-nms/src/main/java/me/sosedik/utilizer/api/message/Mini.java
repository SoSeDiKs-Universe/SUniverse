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
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

@NullMarked
public class Mini {

	private Mini() {
		throw new IllegalStateException("Utility class");
	}

	private static final List<TagResolver> DEFAULT_RESOLVERS = new ArrayList<>();
	private static final List<Function<Messenger, @Nullable TagResolver>> DEFAULT_VIEWER_AWARE_RESOLVERS = new ArrayList<>();

	@SafeVarargs
	public static <T extends ComponentLike> List<T> combined(Collection<T>... components) {
		List<T> componentList = new ArrayList<>();
		for (Collection<T> collection : components) {
			componentList.addAll(collection);
		}
		return componentList;
	}

	public static Component combined(ComponentLike... components) {
		return Component.textOfChildren(components);
	}

	public static <T extends ComponentLike> Component combined(Collection<T> components) {
		return Component.join(JoinConfiguration.noSeparators(), components);
	}

	public static Component combine(Component separator, ComponentLike... components) {
		return Component.join(JoinConfiguration.separator(separator), components);
	}

	public static <T extends ComponentLike> Component combine(Component separator, Collection<T> components) {
		return Component.join(JoinConfiguration.separator(separator), components);
	}

	public static TagResolver.Single raw(@TagPattern String key, int value) {
		return Placeholder.unparsed(key, String.valueOf(value));
	}

	public static TagResolver.Single raw(@TagPattern String key, long value) {
		return Placeholder.unparsed(key, String.valueOf(value));
	}

	public static TagResolver.Single raw(@TagPattern String key, double value) {
		return Placeholder.unparsed(key, String.valueOf(value));
	}

	public static TagResolver.Single raw(@TagPattern String key, String value) {
		return Placeholder.parsed(key, value);
	}

	public static TagResolver.Single raw(@TagPattern String key, Component value) {
		return Placeholder.component(key, value);
	}

	public static TagResolver.Single component(@TagPattern String key, Component value) {
		return Placeholder.component(key, value);
	}

	public static Component mini(String text) {
		return mini(buildMini(), text);
	}

	public static Component mini(String text, TagResolver... resolvers) {
		return mini(buildMini(), text, resolvers);
	}

	public static Component mini(Audience viewer, String text) {
		return mini(buildMini(Messenger.messenger(viewer)), text);
	}

	public static Component mini(Audience viewer, String text, TagResolver... resolvers) {
		return mini(buildMini(Messenger.messenger(viewer)), text, resolvers);
	}

	public static Component mini(MiniMessage miniMessage, String text) {
		return miniMessage.deserialize(text);
	}

	public static Component mini(MiniMessage miniMessage, String text, TagResolver... resolvers) {
		return miniMessage.deserialize(text, TagResolver.resolver(resolvers));
	}

	private static TagResolver getPlaceholders(Messenger messenger) {
		List<TagResolver> resolvers = new ArrayList<>();
		for (Function<Messenger, @Nullable TagResolver> provider : DEFAULT_VIEWER_AWARE_RESOLVERS) {
			TagResolver resolver = provider.apply(messenger);
			if (resolver != null)
				resolvers.add(resolver);
		}
		return TagResolver.resolver(resolvers);
	}

	public static Component asIcon(Component content) {
		return content.colorIfAbsent(NamedTextColor.WHITE)
				.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
	}

	/**
	 * Builds a mini message instance with registered global placeholders
	 *
	 * @return a mini message instance
	 */
	public static MiniMessage buildMini() {
		return MiniMessage.builder().tags(TagResolver.resolver(
			TagResolver.standard(),
			TagResolver.resolver(DEFAULT_RESOLVERS)
		)).build();
	}

	/**
	 * Builds a mini message instance with registered global and viewer-aware placeholders
	 *
	 * @param messenger viewer
	 * @return a mini message instance
	 */
	public static MiniMessage buildMini(Messenger messenger) {
		return buildMini(messenger, TagResolver.standard());
	}

	/**
	 * Builds a mini message instance with registered global and viewer-aware placeholders
	 *
	 * @param messenger viewer
	 * @param standardResolver standard tag resolver
	 * @return a mini message instance
	 */
	public static MiniMessage buildMini(Messenger messenger, TagResolver standardResolver) {
		return MiniMessage.builder().tags(TagResolver.resolver(
			standardResolver,
			TagResolver.resolver(DEFAULT_RESOLVERS),
			getPlaceholders(messenger)
		)).build();
	}

	/**
	 * Adds new tag resolvers
	 *
	 * @param tagResolvers tag resolvers
	 */
	public static void registerTagResolvers(TagResolver... tagResolvers) {
		DEFAULT_RESOLVERS.addAll(List.of(tagResolvers));
	}

	/**
	 * Adds new viewer-aware tag resolvers
	 *
	 * @param tagResolvers viewer-aware tag resolvers
	 */
	@SafeVarargs
	public static void registerViewerAwareTagResolvers(Function<Messenger, @Nullable TagResolver>... tagResolvers) {
		DEFAULT_VIEWER_AWARE_RESOLVERS.addAll(List.of(tagResolvers));
	}

}
