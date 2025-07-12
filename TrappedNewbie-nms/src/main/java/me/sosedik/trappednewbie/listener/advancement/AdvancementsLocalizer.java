package me.sosedik.trappednewbie.listener.advancement;

import me.sosedik.packetadvancements.api.event.AsyncPlayerAdvancementTabSendEvent;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import me.sosedik.utilizer.Utilizer;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLocaleChangeEvent;

/**
 * Localizes advancements :)
 */
public class AdvancementsLocalizer implements Listener {

	@EventHandler
	public void onLocaleChange(PlayerLocaleChangeEvent event) {
		Utilizer.scheduler().sync(() -> TrappedNewbieAdvancements.MANAGER.showTabs(event.getPlayer()), 1L);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onAdvancementTabSend(AsyncPlayerAdvancementTabSendEvent event) {
		Player player = event.getPlayer();

		String key = event.getTab().getKey().value().replace('/', '.');
		Component title = Messenger.messenger(player).getMessageIfExists("adv." + key + ".tab.title");
		if (title != null)
			event.setSentTitle(title);
	}

}
