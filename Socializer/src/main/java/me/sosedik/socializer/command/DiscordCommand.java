package me.sosedik.socializer.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.socializer.Socializer;
import me.sosedik.socializer.discord.Discorder;
import me.sosedik.socializer.util.DiscordUserVerificationCache;
import me.sosedik.socializer.util.DiscordUtil;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Flag;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static me.sosedik.utilizer.api.message.Mini.combined;
import static me.sosedik.utilizer.api.message.Mini.raw;

/**
 * /discord command to show Discord link and display
 */
@NullMarked
public class DiscordCommand {

	@Command("discord")
	private void onCommand(
		CommandSourceStack stack,
		@Nullable @Flag(value = "I_discordId") Long discordId,
		@Flag(value = "unverify") boolean unverify,
		@Flag(value = "sure") boolean sure
	) {
		CommandSender executor = stack.getExecutor();
		if (executor == null) executor = stack.getSender();

		if (unverify && executor instanceof Player player) {
			var discorder = Discorder.getDiscorder(player);
			if (!discorder.hasDiscord()) {
				Messenger.messenger(player).sendMessage("discord.not_verified");
				return;
			}
			if (!sure) {
				Messenger.messenger(player).sendMessage("discord.unlink");
				return;
			}
			discorder.setDiscordId(null);
			Messenger.messenger(player).sendMessage("discord.unlinked");
			return;
		}

		if (discordId == null || !(executor instanceof Player player)) {
			var messenger = Messenger.messenger(executor);
			Component message = messenger.miniMessage().deserialize("<discord>");
			message = messenger.getMessage("discord.link")
				.hoverEvent(message.hoverEvent())
				.clickEvent(message.clickEvent());
			executor.sendMessage(message);
			return;
		}

		if (discordId <= 0) {
			Messenger.messenger(player).sendMessage("discord.invalid-id");
			Socializer.logger().warn("Player {} attempted to verify with invalid Discord ID: {}", player.getName(), discordId);
			return;
		}

		Long neededCode = Socializer.discordUserVerificationCache().get(player.getUniqueId());
		if (neededCode == null) {
			Messenger.messenger(player).sendMessage("discord.verify.expired");
			return;
		}
		if (!neededCode.equals(discordId)) {
			Messenger.messenger(player).sendMessage("discord.verify.mismatch");
			return;
		}

		Messenger.messenger(executor).sendMessage("discord.verify.verified");
		Discorder.getDiscorder(player).setDiscordId(discordId);
		DiscordUtil.announceVerify(discordId, player.getUniqueId().toString(), player.getName());
		Socializer.discordUserVerificationCache().remove(player.getUniqueId());
		Socializer.logger().info("Player {} successfully linked Discord account {}", player.getName(), discordId);

		Socializer.scheduler().async(DiscordUtil::updateStatus);
	}

	/**
	 * Suggests the player to link the provided Discord account
	 *
	 * @param player player
	 * @param discordId Discord user id
	 * @param discordTag Discord display tag
	 * @return whether the suggestion was sent
	 */
	public static boolean suggestVerification(Player player, long discordId, String discordTag) {
		DiscordUserVerificationCache cache = Socializer.discordUserVerificationCache();
		if (!cache.put(player.getUniqueId(), discordId)) return false;

		Socializer.scheduler().async(() -> cache.remove(player.getUniqueId()), 5 * 60 * 20L);

		var messenger = Messenger.messenger(player);
		Component message = combined(
			messenger.getMessage("discord.verify", raw("discord_tag", discordTag)),
			Component.newline(),
			messenger.getMessage("discord.verify.yes")
				.hoverEvent(messenger.getMessage("discord.verify.yes.hover"))
				.clickEvent(ClickEvent.runCommand("/discord --I_discordId " + discordId))
		);

		player.sendMessage(message);
		Socializer.logger().info("Sent verification request to {} for Discord ID {}", player.getName(), discordId);
		return true;
	}

}
