package me.sosedik.trappednewbie.listener.advancement;

import me.sosedik.packetadvancements.api.display.IAdvancementDisplay;
import me.sosedik.packetadvancements.api.event.AsyncPlayerAdvancementSendEvent;
import me.sosedik.packetadvancements.api.event.AsyncPlayerAdvancementTabSendEvent;
import me.sosedik.packetadvancements.api.event.AsyncPlayerAdvancementToastSendEvent;
import me.sosedik.packetadvancements.util.ToastMessage;
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
	public void onAdvancementSend(AsyncPlayerAdvancementSendEvent event) {
		IAdvancementDisplay display = event.getSentDisplay();
		if (display.isHidden()) return;

		display = display.clone();
		Player player = event.getPlayer();
		String key = event.getAdvancement().getKey().value().replace('/', '.');
		var messenger = Messenger.messenger(player);
		display.title(messenger.getMessage("adv." + key + ".title"));
		display.description(messenger.getMessage("adv." + key  + ".description"));
		event.setSentDisplay(display);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onAdvancementSend(AsyncPlayerAdvancementToastSendEvent event) {
		ToastMessage display = event.getSentDisplay();
		Player player = event.getPlayer();
		String key = event.getAdvancement().getKey().value().replace('/', '.');
		display.message(Messenger.messenger(player).getMessage("adv." + key + ".title"));
		event.setSentDisplay(display);
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
