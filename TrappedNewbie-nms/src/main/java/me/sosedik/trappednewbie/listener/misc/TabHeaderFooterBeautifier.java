package me.sosedik.trappednewbie.listener.misc;

import me.sosedik.resourcelib.feature.TabRenderer;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jspecify.annotations.NullMarked;

import java.text.DecimalFormat;
import java.util.List;

import static me.sosedik.utilizer.api.message.Mini.component;

/**
 * Renders custom messages in tab with Ping and TPS info
 */
@NullMarked
public class TabHeaderFooterBeautifier implements Listener {

	private static final DecimalFormat TPS_FORMAT = new DecimalFormat("#0.0#");
	private static final NamespacedKey TAB_HEADER_RENDERER_KEY = TrappedNewbie.trappedNewbieKey("tab_header");
	private static final NamespacedKey TAB_FOOTER_RENDERER_KEY = TrappedNewbie.trappedNewbieKey("tab_footer");

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		var messenger = Messenger.messenger(player);
		var tabRenderer = TabRenderer.of(player);
		tabRenderer.addHeaderElement(TAB_HEADER_RENDERER_KEY, () -> renderHeader(messenger));
		tabRenderer.addFooterElement(TAB_FOOTER_RENDERER_KEY, () -> renderFooter(player, messenger));
	}

	private List<Component> renderHeader(Messenger messenger) {
		return List.of(messenger.getMessage("tab.header"));
	}

	private List<Component> renderFooter(Player player, Messenger messenger) {
		var tpsPlaceholder = component("tps", getTPS());
		var pingPlaceholder = component("ping", getPing(player));
		Component footer = messenger.getMessage("tab.footer", tpsPlaceholder, pingPlaceholder);
		return List.of(footer);
	}

	private Component getTPS() {
		double tps = Bukkit.getTPS()[0];
		float tickRate = Bukkit.getServerTickManager().getTickRate();
		if (tps > tickRate)
			tps = tickRate;
		else if (tps != tickRate && tickRate - tps < 0.1) // Prevents 19 <-> 20 jumping for very minor drops
			tps = tickRate;
		return Component.text(TPS_FORMAT.format(tps), getTPSColor(tps));
	}

	private NamedTextColor getTPSColor(double tps) {
		float tickRate = Bukkit.getServerTickManager().getTickRate();
		if (tickRate == 0F) return NamedTextColor.AQUA;
		if (tps > tickRate && tps - tickRate > 0.3) return NamedTextColor.YELLOW;

		double ratio = tps / tickRate;
		if (ratio > 0.9) return NamedTextColor.GREEN;
		if (ratio > 0.8) return NamedTextColor.GOLD;
		return NamedTextColor.RED;
	}

	private Component getPing(Player player) {
		int ping = player.getPing();
		return Component.text(ping, getPingColor(ping));
	}

	private NamedTextColor getPingColor(int ping) {
		if (ping < 200) return NamedTextColor.GREEN;
		if (ping < 500) return NamedTextColor.GOLD;
		return NamedTextColor.RED;
	}

}
