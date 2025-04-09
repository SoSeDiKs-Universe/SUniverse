package me.sosedik.uglychatter;

import me.sosedik.uglychatter.api.mini.placeholder.EmojiPlaceholder;
import me.sosedik.uglychatter.api.mini.tag.CopyTag;
import me.sosedik.uglychatter.api.mini.tag.ExecuteTag;
import me.sosedik.uglychatter.api.mini.tag.LinkTag;
import me.sosedik.uglychatter.api.mini.tag.SpoilerTag;
import me.sosedik.uglychatter.impl.item.modifier.BookContentModifier;
import me.sosedik.uglychatter.listener.item.PreviewBookFormatting;
import me.sosedik.uglychatter.listener.misc.BookBeautifier;
import me.sosedik.uglychatter.listener.misc.ChatBeautifier;
import me.sosedik.uglychatter.listener.misc.SignBeautifier;
import me.sosedik.utilizer.api.language.TranslationHolder;
import me.sosedik.utilizer.api.message.Mini;
import me.sosedik.utilizer.util.EventUtil;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class UglyChatter extends JavaPlugin {

	private static @UnknownNullability UglyChatter instance;

	@Override
	public void onLoad() {
		UglyChatter.instance = this;

		TranslationHolder.extractLocales(this);

		EmojiPlaceholder.setupEmoji(this);
	}

	@Override
	public void onEnable() {
		LinkTag.reloadLinks(this);

		Mini.registerTagResolvers(
			SpoilerTag.SPOILER,
			SpoilerTag.SPOILER_WITH_COPY
		);
		Mini.registerViewerAwareTagResolvers(
			CopyTag::new,
			ExecuteTag::new,
			LinkTag::new
		);

		new BookContentModifier(uglyChatterKey("book_content")).register();

		EventUtil.registerListeners(this,
			// item
			PreviewBookFormatting.class,
			// misc
			BookBeautifier.class,
			ChatBeautifier.class,
			SignBeautifier.class
		);
	}

	/**
	 * Gets the plugin instance
	 *
	 * @return the plugin instance
	 */
	public static UglyChatter instance() {
		return UglyChatter.instance;
	}

	/**
	 * Gets the plugin's component logger
	 *
	 * @return the plugin's component logger
	 */
	public static ComponentLogger logger() {
		return instance().getComponentLogger();
	}

	/**
	 * Makes a namespaced key with this plugin's namespace
	 *
	 * @param value value
	 * @return namespaced key
	 */
	public static NamespacedKey uglyChatterKey(String value) {
		return new NamespacedKey("ugly_chatter", value);
	}

}
