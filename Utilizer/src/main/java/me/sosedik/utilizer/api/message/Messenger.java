package me.sosedik.utilizer.api.message;

import me.sosedik.utilizer.api.language.LangHolder;
import me.sosedik.utilizer.api.language.LangOptions;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.language.TranslationHolder;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static me.sosedik.utilizer.api.message.Mini.buildMini;
import static me.sosedik.utilizer.api.message.Mini.combine;
import static me.sosedik.utilizer.api.message.Mini.mini;
import static net.kyori.adventure.text.Component.newline;

/**
 * Wrapper for getting translated messages
 */
@NullMarked
public class Messenger {

	private final @Nullable Audience audience;
	private final @Nullable LangOptions langOptions;
	private final MiniMessage miniMessage;

	private Messenger(LangOptions langOptions) {
		this.audience = null;
		this.langOptions = langOptions;
		this.miniMessage = buildMini(this);
	}

	private Messenger(Audience audience) {
		this.audience = audience;
		this.langOptions = null;
		this.miniMessage = buildMini(this);
	}

	private Messenger(Audience audience, TagResolver standardTagResolver) {
		this.audience = audience;
		this.langOptions = null;
		this.miniMessage = buildMini(this, standardTagResolver);
	}

	/**
	 * Messages receiver, mostly player
	 *
	 * @return messages receiver
	 */
	public @Nullable Audience getAudience() {
		return this.audience;
	}

	/**
	 * Language used for translations
	 *
	 * @return language
	 */
	public LangOptions getLangOptions() {
		if (this.langOptions != null) return this.langOptions;
		return this.audience instanceof Player player ? LangHolder.langHolder(player).getLangOptions() : LangOptionsStorage.getDefaultLangOptions();
	}

	/**
	 * Returns MiniMessage instance of this Messenger
	 *
	 * @return MiniMessage instance
	 */
	public MiniMessage miniMessage() {
		return this.miniMessage;
	}

	/**
	 * Returns row (unparsed) message from
	 * localizations provider
	 *
	 * @param messagePath path for message
	 * @return row message
	 */
	public String [] getRawMessage(String messagePath) {
		return TranslationHolder.translationHolder().getMessage(getLangOptions(), messagePath);
	}

	/**
	 * Returns row (unparsed) message from
	 * localizations provider
	 *
	 * @param messagePath path for message
	 * @return row message
	 */
	public String @Nullable [] getRawMessageIfExists(String messagePath) {
		return TranslationHolder.translationHolder().getMessage(getLangOptions(), messagePath, false);
	}

	/**
	 * Returns parsed message
	 *
	 * @param messagePath path for message
	 * @return parsed message
	 */
	public Component [] getMessages(String messagePath) {
		String[] minis = getRawMessage(messagePath);
		if (minis.length == 1) return new Component[]{mini(this.miniMessage, minis[0])};

		Component[] parsed = new Component[minis.length];
		for (int i = 0; i < minis.length; i++)
			parsed[i] = mini(this.miniMessage, minis[i]);
		return parsed;
	}

	/**
	 * Returns parsed message
	 *
	 * @param messagePath path for message
	 * @param resolvers   message tag resolvers
	 * @return parsed message
	 */
	public @Nullable Component getMessageIfExists(String messagePath, TagResolver... resolvers) {
		Component[] messages = getMessagesIfExists(messagePath, resolvers);
		return messages == null ? null : combine(newline(), messages);
	}

	/**
	 * Returns parsed message
	 *
	 * @param messagePath path for message
	 * @param resolvers   message tag resolvers
	 * @return parsed message
	 */
	public Component @Nullable [] getMessagesIfExists(String messagePath, TagResolver ... resolvers) {
		String[] minis = getRawMessageIfExists(messagePath);
		if (minis == null) return null;
		if (minis.length == 1) return new Component[]{mini(this.miniMessage, minis[0], resolvers)};

		Component[] parsed = new Component[minis.length];
		for (int i = 0; i < minis.length; i++)
			parsed[i] = mini(this.miniMessage, minis[i], resolvers);
		return parsed;
	}

	/**
	 * Returns parsed message
	 *
	 * @param messagePath path for message
	 * @param resolvers   message tag resolvers
	 * @return parsed message
	 */
	public Component [] getMessages(String messagePath, TagResolver... resolvers) {
		String[] minis = getRawMessage(messagePath);
		if (minis.length == 1) return new Component[]{mini(this.miniMessage, minis[0], resolvers)};

		Component[] parsed = new Component[minis.length];
		for (int i = 0; i < minis.length; i++)
			parsed[i] = mini(this.miniMessage, minis[i], resolvers);
		return parsed;
	}

	/**
	 * Returns parsed message
	 *
	 * @param messagePath path for message
	 * @return parsed message
	 */
	public Component getMessage(String messagePath) {
		return combine(newline(), getMessages(messagePath));
	}

	/**
	 * Returns parsed message
	 *
	 * @param messagePath path for message
	 * @param resolvers   message tag resolvers
	 * @return parsed message
	 */
	public Component getMessage(String messagePath, TagResolver... resolvers) {
		return combine(newline(), getMessages(messagePath, resolvers));
	}

	/**
	 * Parses and sends a chat message
	 *
	 * @param messagePath path for message
	 */
	public void sendMessage(String messagePath) {
		if (this.audience == null) return;
		this.audience.sendMessage(getMessage(messagePath));
	}

	/**
	 * Parses and sends a chat message
	 *
	 * @param messagePath path for message
	 */
	public void sendMessage(String messagePath, TagResolver... tags) {
		if (this.audience == null) return;
		this.audience.sendMessage(getMessage(messagePath, tags));
	}

	/**
	 * Parses and sends an action bar message
	 *
	 * @param messagePath path for message
	 */
	public void sendActionBar(String messagePath) {
		if (this.audience == null) return;
		this.audience.sendActionBar(getMessage(messagePath));
	}

	/**
	 * Parses and sends an action bar message
	 *
	 * @param messagePath path for message
	 */
	public void sendActionBar(String messagePath, TagResolver... tags) {
		if (this.audience == null) return;
		this.audience.sendActionBar(getMessage(messagePath, tags));
	}

	/**
	 * Constructs a new messenger
	 *
	 * @param langOptions language options
	 * @return messenger wrapper
	 */
	public static Messenger messenger(LangOptions langOptions) {
		return new Messenger(langOptions);
	}

	/**
	 * Constructs a new messenger
	 *
	 * @param audience messages viewer
	 * @return messenger wrapper
	 */
	public static Messenger messenger(Audience audience) {
		return new Messenger(audience);
	}

	/**
	 * Constructs a new messenger
	 *
	 * @param audience messages viewer
	 * @param standardResolver standard tag resolver
	 * @return messenger wrapper
	 */
	public static Messenger messenger(Audience audience, TagResolver standardResolver) {
		return new Messenger(audience, standardResolver);
	}

}
