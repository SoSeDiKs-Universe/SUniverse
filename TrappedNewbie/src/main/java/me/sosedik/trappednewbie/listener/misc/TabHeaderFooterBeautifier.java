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
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.List;

import static me.sosedik.utilizer.api.message.Mini.component;

/**
 * Renders custom messages in tab with Ping and TPS info
 */
public class TabHeaderFooterBeautifier implements Listener {

	private static final DecimalFormat TPS_FORMAT = new DecimalFormat("#0.0#");
	private static final NamespacedKey TAB_HEADER_RENDERER_KEY = TrappedNewbie.trappedNewbieKey("tab_header");
	private static final NamespacedKey TAB_FOOTER_RENDERER_KEY = TrappedNewbie.trappedNewbieKey("tab_footer");

	@EventHandler
	public void onJoin(@NotNull PlayerJoinEvent event) {
		Player player = event.getPlayer();
		var messenger = Messenger.messenger(player);
		var tabRenderer = TabRenderer.of(player);
		tabRenderer.addHeaderElement(TAB_HEADER_RENDERER_KEY, () -> renderHeader(messenger));
		tabRenderer.addFooterElement(TAB_FOOTER_RENDERER_KEY, () -> renderFooter(player, messenger));
	}

	private @NotNull List<Component> renderHeader(@NotNull Messenger messenger) {
		return List.of(messenger.getMessage("tab.header"));
	}

	private @NotNull List<Component> renderFooter(@NotNull Player player, @NotNull Messenger messenger) {
		var tpsPlaceholder = component("tps", getTPS());
		var pingPlaceholder = component("ping", getPing(player));
		Component footer = messenger.getMessage("tab.footer", tpsPlaceholder, pingPlaceholder);
		return List.of(footer);
	}

	private @NotNull Component getTPS() {
		double tps = Bukkit.getServer().getTPS()[0];
		if (tps > 19.9) // Prevents 19 <-> 20 jumping for very minor drops
			tps = 20;
		return Component.text(TPS_FORMAT.format(tps), getTPSColor(tps));
	}

	private @NotNull NamedTextColor getTPSColor(double tps) {
		if (tps > 18) return NamedTextColor.GREEN;
		if (tps > 16) return NamedTextColor.GOLD;
		return NamedTextColor.RED;
	}

	private @NotNull Component getPing(@NotNull Player player) {
		int ping = player.getPing();
		return Component.text(ping, getPingColor(ping));
	}

	private @NotNull NamedTextColor getPingColor(int ping) {
		if (ping < 200) return NamedTextColor.GREEN;
		if (ping < 500) return NamedTextColor.GOLD;
		return NamedTextColor.RED;
	}

}
