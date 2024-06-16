package me.sosedik.utilizer.api.message;

import me.sosedik.utilizer.api.language.LangHolder;
import me.sosedik.utilizer.api.language.LangKey;
import me.sosedik.utilizer.api.language.LangKeysStorage;
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
	private final LangKey langKey;
	private final MiniMessage miniMessage;

	private Messenger(@NotNull LangKey langKey) {
		this.audience = Bukkit.getServer();
		this.langHolder = null;
		this.langKey = langKey;
		this.miniMessage = buildMini(this);
	}

	private Messenger(@Nullable Audience audience) {
		this.audience = audience == null ? Bukkit.getServer() : audience;
		this.langHolder = audience instanceof Player player ? LangHolder.langHolder(player) : null;
		this.langKey = LangKeysStorage.getDefaultLangKey();
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
	@NotNull
	public LangKey getLangKey() {
		return langHolder == null ? langKey : langHolder.getLangKey();
	}

	/**
	 * Language holder, if present
	 *
	 * @return language holder
	 */
	@Nullable
	public LangHolder getLangHolder() {
		return langHolder;
	}

	/**
	 * Returns row (unparsed) message from
	 * localizations provider
	 *
	 * @param messagePath path for message
	 * @return row message
	 */
	@NotNull
	public String[] getRawMessage(@NotNull String messagePath) {
		return TranslationHolder.translationHolder().getMessage(getLangKey(), messagePath);
	}

	/**
	 * Returns row (unparsed) message from
	 * localizations provider
	 *
	 * @param messagePath path for message
	 * @return row message
	 */
	@Nullable
	public String[] getRawMessageIfExists(@NotNull String messagePath) {
		return TranslationHolder.translationHolder().getMessage(getLangKey(), messagePath, false);
	}

	/**
	 * Returns parsed message
	 *
	 * @param messagePath path for message
	 * @return parsed message
	 */
	@NotNull
	public Component getMessage(@NotNull String messagePath) {
		String[] minis = getRawMessage(messagePath);
		if (minis.length == 1) return mini(miniMessage, minis[0]);

		Component[] parsed = new Component[minis.length];
		for (int i = 0; i < minis.length; i++)
			parsed[i] = mini(miniMessage, minis[i]);
		return combine(newline(), parsed);
	}

	/**
	 * Returns parsed message
	 *
	 * @param messagePath path for message
	 * @return parsed message
	 */
	@Nullable
	public Component getMessageIfExists(@NotNull String messagePath) {
		String[] minis = getRawMessageIfExists(messagePath);
		if (minis == null) return null;
		if (minis.length == 1) return mini(miniMessage, minis[0]);

		Component[] parsed = new Component[minis.length];
		for (int i = 0; i < minis.length; i++)
			parsed[i] = mini(miniMessage, minis[i]);
		return combine(newline(), parsed);
	}

	/**
	 * Returns parsed message
	 *
	 * @param messagePath path for message
	 * @param resolvers   message tag resolvers
	 * @return parsed message
	 */
	@NotNull
	public Component getMessage(@NotNull String messagePath, @NotNull TagResolver... resolvers) {
		String[] minis = getRawMessage(messagePath);
		if (minis.length == 1) return mini(miniMessage, minis[0], resolvers);

		Component[] parsed = new Component[minis.length];
		for (int i = 0; i < minis.length; i++)
			parsed[i] = mini(miniMessage, minis[i], resolvers);
		return combine(newline(), parsed);
	}

	/**
	 * Returns parsed mini message
	 *
	 * @param mini mini message
	 * @return parsed message
	 */
	@NotNull
	public Component getMiniMessage(@NotNull String mini) {
		return mini(miniMessage, mini);
	}

	/**
	 * Returns parsed mini message
	 *
	 * @param mini      mini message
	 * @param resolvers message tag resolvers
	 * @return parsed message
	 */
	@NotNull
	public Component getMiniMessage(@NotNull String mini, @NotNull TagResolver... resolvers) {
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

	public static @NotNull Messenger messenger(@NotNull LangKey langKey) {
		return new Messenger(langKey);
	}

	public static @NotNull Messenger messenger(@NotNull Audience audience) {
		return new Messenger(audience);
	}

}
