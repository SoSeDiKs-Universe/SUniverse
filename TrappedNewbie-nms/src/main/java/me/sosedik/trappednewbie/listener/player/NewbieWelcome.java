package me.sosedik.trappednewbie.listener.player;

import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import me.sosedik.trappednewbie.dataset.TrappedNewbieFonts;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.api.message.Mini;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Welcome message for newbies
 */
@NullMarked
public class NewbieWelcome implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (TrappedNewbieAdvancements.REQUIEM_ROOT.hasCriteria(player, "interact")) return;

		var messenger = Messenger.messenger(player);
		player.sendMessage(Mini.combine(Component.space(), TrappedNewbieFonts.WANDERING_TRADER_HEAD.mapping(), messenger.getMessage("limbo.welcome")));
	}

}
