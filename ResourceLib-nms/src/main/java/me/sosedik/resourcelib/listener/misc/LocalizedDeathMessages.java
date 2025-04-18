package me.sosedik.resourcelib.listener.misc;

import io.papermc.paper.event.entity.TameableDeathMessageEvent;
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

		String locale = component.key();
		if (!TranslationHolder.translationHolder().hasMessage(locale)) return;

		if (!locale.endsWith(".item") && !locale.endsWith(".player")) {
			int placeholders = component.arguments().size();
			if (placeholders > 1 && TranslationHolder.translationHolder().hasMessage(locale + ".killer"))
				locale += ".killer";
		}

		Player player = event.getPlayer();
		var messenger = Messenger.messenger(player);
		Component newMessage = messenger.getMessage(locale, tagResolvers(component));

		event.deathMessage(newMessage);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onTamedDeath(TameableDeathMessageEvent event) {
		if (!(event.deathMessage() instanceof TranslatableComponent component)) return;

		String locale = component.key();
		if (!TranslationHolder.translationHolder().hasMessage(locale)) return;

		Tameable entity = event.getEntity();
		UUID ownerId = entity.getOwnerUniqueId();
		if (ownerId == null) return;

		Player player = Bukkit.getPlayer(ownerId);
		if (player == null) return;

		if (!locale.endsWith(".item") && !locale.endsWith(".player")) {
			int placeholders = component.arguments().size();
			if (placeholders > 1 && TranslationHolder.translationHolder().hasMessage(locale + ".killer"))
				locale += ".killer";
		}

		var messenger = Messenger.messenger(player);
		Component newMessage = messenger.getMessage(locale, tagResolvers(component));

		event.deathMessage(newMessage);
	}

	private TagResolver[] tagResolvers(TranslatableComponent component) {
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
