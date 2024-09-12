package me.sosedik.utilizer.api.message;

import me.sosedik.utilizer.api.language.LangHolder;
import me.sosedik.utilizer.api.language.LangOptions;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.language.TranslationHolder;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static me.sosedik.utilizer.api.message.Mini.buildMini;
import static me.sosedik.utilizer.api.message.Mini.combine;
import static me.sosedik.utilizer.api.message.Mini.mini;
import static net.kyori.adventure.text.Component.newline;

/**
 * Wrapper for sending translated messages
 */
public class Messenger {

	private final Audience audience;
	private final @Nullable LangHolder langHolder;
	private final LangOptions langOptions;
	private final MiniMessage miniMessage;

	private Messenger(@NotNull LangOptions langOptions) {
		this.audience = Bukkit.getServer();
		this.langHolder = null;
		this.langOptions = langOptions;
		this.miniMessage = buildMini(this);
	}

	private Messenger(@Nullable Audience audience) {
		this.audience = audience == null ? Bukkit.getServer() : audience;
		this.langHolder = audience instanceof Player player ? LangHolder.langHolder(player) : null;
		this.langOptions = LangOptionsStorage.getDefaultLangOptions();
		this.miniMessage = buildMini(this);
	}

	/**
	 * Messages receiver, mostly player
	 *
	 * @return messages receiver
	 */
	public @NotNull Audience getAudience() {
		return audience;
	}

	/**
	 * Language used for translations
	 *
	 * @return language
	 */
	public @NotNull LangOptions getLangOptions() {
		return langHolder == null ? langOptions : langHolder.getLangOptions();
	}

	/**
	 * Language holder, if present
	 *
	 * @return language holder
	 */
	public @Nullable LangHolder getLangHolder() {
		return langHolder;
	}

	/**
	 * Returns row (unparsed) message from
	 * localizations provider
	 *
	 * @param messagePath path for message
	 * @return row message
	 */
	public @NotNull String @NotNull [] getRawMessage(@NotNull String messagePath) {
		return TranslationHolder.translationHolder().getMessage(getLangOptions(), messagePath);
	}

	/**
	 * Returns row (unparsed) message from
	 * localizations provider
	 *
	 * @param messagePath path for message
	 * @return row message
	 */
	public @NotNull String @Nullable [] getRawMessageIfExists(@NotNull String messagePath) {
		return TranslationHolder.translationHolder().getMessage(getLangOptions(), messagePath, false);
	}

	/**
	 * Returns parsed message
	 *
	 * @param messagePath path for message
	 * @return parsed message
	 */
	public @NotNull Component getMessage(@NotNull String messagePath) {
		return combine(newline(), getMessages(messagePath));
	}

	/**
	 * Returns parsed message
	 *
	 * @param messagePath path for message
	 * @return parsed message
	 */
	public @NotNull Component @NotNull [] getMessages(@NotNull String messagePath) {
		String[] minis = getRawMessage(messagePath);
		if (minis.length == 1) return new Component[]{mini(miniMessage, minis[0])};

		Component[] parsed = new Component[minis.length];
		for (int i = 0; i < minis.length; i++)
			parsed[i] = mini(miniMessage, minis[i]);
		return parsed;
	}

	/**
	 * Returns parsed message
	 *
	 * @param messagePath path for message
	 * @return parsed message
	 */
	public @Nullable Component getMessageIfExists(@NotNull String messagePath) {
		Component[] messages = getMessagesIfExists(messagePath);
		return messages == null ? null : combine(newline(), messages);
	}

	/**
	 * Returns parsed message
	 *
	 * @param messagePath path for message
	 * @return parsed message
	 */
	public @NotNull Component @Nullable [] getMessagesIfExists(@NotNull String messagePath) {
		String[] minis = getRawMessageIfExists(messagePath);
		if (minis == null) return null;
		if (minis.length == 1) return new Component[]{mini(miniMessage, minis[0])};

		Component[] parsed = new Component[minis.length];
		for (int i = 0; i < minis.length; i++)
			parsed[i] = mini(miniMessage, minis[i]);
		return parsed;
	}

	/**
	 * Returns parsed message
	 *
	 * @param messagePath path for message
	 * @param resolvers   message tag resolvers
	 * @return parsed message
	 */
	public @NotNull Component getMessage(@NotNull String messagePath, @NotNull TagResolver @NotNull ... resolvers) {
		return combine(newline(), getMessages(messagePath, resolvers));
	}

	/**
	 * Returns parsed message
	 *
	 * @param messagePath path for message
	 * @param resolvers   message tag resolvers
	 * @return parsed message
	 */
	public @NotNull Component @NotNull [] getMessages(@NotNull String messagePath, @NotNull TagResolver @NotNull ... resolvers) {
		String[] minis = getRawMessage(messagePath);
		if (minis.length == 1) return new Component[]{mini(miniMessage, minis[0], resolvers)};

		Component[] parsed = new Component[minis.length];
		for (int i = 0; i < minis.length; i++)
			parsed[i] = mini(miniMessage, minis[i], resolvers);
		return parsed;
	}

	/**
	 * Returns parsed mini message
	 *
	 * @param mini mini message
	 * @return parsed message
	 */
	public @NotNull Component getMiniMessage(@NotNull String mini) {
		return mini(miniMessage, mini);
	}

	/**
	 * Returns parsed mini message
	 *
	 * @param mini      mini message
	 * @param resolvers message tag resolvers
	 * @return parsed message
	 */
	public @NotNull Component getMiniMessage(@NotNull String mini, @NotNull TagResolver... resolvers) {
		return mini(miniMessage, mini, resolvers);
	}

	/**
	 * Parses and sends message
	 *
	 * @param messagePath path for message
	 */
	public void sendMessage(@NotNull String messagePath) {
		getAudience().sendMessage(getMessage(messagePath));
	}

	/**
	 * Parses and sends message
	 *
	 * @param messagePath path for message
	 * @param resolvers   message tag resolvers
	 */
	public void sendMessage(@NotNull String messagePath, @NotNull TagResolver... resolvers) {
		getAudience().sendMessage(getMessage(messagePath, resolvers));
	}

//	public void sendActionBar(@NotNull String messagePath, @NotNull TagResolver... resolvers) {
//		if (audience instanceof Player player) HudMessenger.get(player).displayMessage(getMessage(messagePath, resolvers));
//	}

	/**
	 * Returns MiniMessage instance of this Messenger
	 *
	 * @return MiniMessage instance
	 */
	public @NotNull MiniMessage miniMessage() {
		return miniMessage;
	}

	public static @NotNull Messenger messenger(@NotNull LangOptions langOptions) {
		return new Messenger(langOptions);
	}

	public static @NotNull Messenger messenger(@NotNull Audience audience) {
		return new Messenger(audience);
	}

}
