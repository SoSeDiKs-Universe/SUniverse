package me.sosedik.resourcelib.listener.misc;

import io.papermc.paper.event.entity.TameableDeathMessageEvent;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.language.TranslationHolder;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.api.message.Mini;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.TranslationArgument;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.intellij.lang.annotations.Subst;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Handles localizations for death messages.
 * Mostly for custom death causes, but can overwrite vanilla as well.
 */
@NullMarked
public class LocalizedDeathMessages implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDeath(PlayerDeathEvent event) {
		if (!(event.deathMessage() instanceof TranslatableComponent component)) return;

		String localeKey = component.key();
		if (!TranslationHolder.translationHolder().hasMessage(localeKey)) return;

		localeKey = getLocaleKey(component);
		TagResolver[] tagResolvers = tagResolvers(component);
		for (Player player : Bukkit.getOnlinePlayers())
			Messenger.messenger(player).sendMessage(localeKey, tagResolvers);

		event.deathMessage(null);
		event.originalDeathMessage(Messenger.messenger(LangOptionsStorage.getDefaultLangOptions()).getMessage(localeKey, tagResolvers));
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onTamedDeath(TameableDeathMessageEvent event) {
		if (!(event.deathMessage() instanceof TranslatableComponent component)) return;

		Tameable entity = event.getEntity();
		UUID ownerId = entity.getOwnerUniqueId();
		if (ownerId == null) return;

		Player player = Bukkit.getPlayer(ownerId);
		if (player == null) return;

		event.deathMessage(formatDeathMessage(player.locale(), component));
	}

	public static Component formatDeathMessage(Locale locale, Component message) {
		if (!(message instanceof TranslatableComponent component)) return message;

		String localeKey = component.key();
		if (!TranslationHolder.translationHolder().hasMessage(localeKey)) return message;

		localeKey = getLocaleKey(component);
		return Messenger.messenger(LangOptionsStorage.getByLocale(locale)).getMessage(localeKey, tagResolvers(component));
	}

	private static String getLocaleKey(TranslatableComponent component) {
		String localeKey = component.key();
		if (!localeKey.endsWith(".item") && !localeKey.endsWith(".player")) {
			int placeholders = component.arguments().size();
			if (placeholders > 1 && TranslationHolder.translationHolder().hasMessage(localeKey + ".killer"))
				localeKey += ".killer";
		}
		return localeKey;
	}

	private static TagResolver[] tagResolvers(TranslatableComponent component) {
		List<TranslationArgument> arguments = component.arguments();
		TagResolver[] tagResolvers = new TagResolver[arguments.size()];
		for (int i = 0; i < arguments.size(); i++) {
			@Subst("key") String key = switch (i) {
				case 0 -> "entity";
				case 1 -> "attacker";
				case 2 -> "item";
				default -> String.valueOf(i);
			};
			Component placeholder = arguments.get(i).asComponent();
			tagResolvers[i] = Mini.raw(key, placeholder);
		}
		return tagResolvers;
	}

}
