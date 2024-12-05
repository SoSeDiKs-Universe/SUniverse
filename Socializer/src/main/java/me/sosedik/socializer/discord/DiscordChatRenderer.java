package me.sosedik.socializer.discord;

import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.util.SpacingUtil;
import me.sosedik.socializer.util.MinecraftChatRenderer;
import me.sosedik.uglychatter.api.chat.FancyMessageRenderer;
import me.sosedik.uglychatter.api.chat.PlayerDisplayFormatter;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.api.message.Mini;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static me.sosedik.socializer.Socializer.socializerKey;
import static me.sosedik.utilizer.api.message.Mini.combine;
import static me.sosedik.utilizer.api.message.Mini.combined;

public class DiscordChatRenderer implements MinecraftChatRenderer {

	private static final Component DISCORD_ICON = Mini.asIcon(ResourceLib.requireFontData(socializerKey("discord")).mapping());
	private static final Component DISCORD_HOVER = combined(Component.space(), Mini.asIcon(ResourceLib.requireFontData(socializerKey("discord")).mapping()), Component.space())
			.hoverEvent(Component.text("Discord"));
	private static final Component SPACED_NEW_LINE = combined(Component.newline(), SpacingUtil.getSpacing(8));
	private static final Style BASE_STYLE = Style.style(TextColor.fromHexString("#9fb2f4"));

	@Override
	public void sendBukkitMessage(@NotNull String nickname, @NotNull String rawMessage) {
		Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
		if (onlinePlayers.isEmpty()) return;

		String[] lines = rawMessage.split("\n", -1);

		var offlinePlayer = Bukkit.getOfflinePlayerIfCached(nickname);
		if (offlinePlayer == null) offlinePlayer = Bukkit.getOfflinePlayer(nickname);
		Player onlinePlayer = offlinePlayer.getPlayer();

		Component prefix = DISCORD_HOVER.clickEvent(ClickEvent.suggestCommand("@" + nickname));
		Component suffix = Component.text(": ", TextColor.fromHexString("#dceefa"));

		List<Component> unparsedLines = new ArrayList<>();
		var chatRenderer = new FancyMessageRenderer();
		for (Player player : onlinePlayers) {
			Component displayName = Component.text().content(nickname)
				.color(TextColor.fromHexString("#7289da"))
				.hoverEvent(
					!offlinePlayer.hasPlayedBefore()
					? DISCORD_ICON
					: combine(Component.newline(),
						PlayerDisplayFormatter.getPlayTime(offlinePlayer, player),
						PlayerDisplayFormatter.getDeaths(offlinePlayer, player)
					)
				)
				.build();

			var messenger = Messenger.messenger(player);

			unparsedLines.clear();
			for (String line : lines)
				unparsedLines.add(chatRenderer.renderAndTranslate(messenger.miniMessage(), line, player, player, BASE_STYLE));

			Component parsedMessage = combine(SPACED_NEW_LINE, unparsedLines);

			prefix = onlinePlayer == null ? prefix : PlayerDisplayFormatter.getPMComponent("", NamedTextColor.WHITE, onlinePlayer, player).append(DISCORD_ICON);
			player.sendMessage(combined(prefix, displayName, suffix, parsedMessage));
		}
	}

}
