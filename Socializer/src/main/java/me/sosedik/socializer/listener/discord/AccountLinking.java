package me.sosedik.socializer.listener.discord;

import me.sosedik.socializer.Socializer;
import me.sosedik.socializer.command.DiscordCommand;
import me.sosedik.socializer.discord.DiscordBot;
import me.sosedik.socializer.discord.Discorder;
import me.sosedik.socializer.util.DiscordUtil;
import me.sosedik.utilizer.api.message.Messenger;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class AccountLinking extends ListenerAdapter {

	public AccountLinking(Socializer plugin) {
		if (!plugin.getConfig().getBoolean("discord.account-linking", false)) return;

		DiscordBot.getDiscordBot().addEventListener(this);
	}

	@Override
	public void onButtonInteraction(ButtonInteractionEvent event) {
		Member member = event.getMember();
		if (member == null) return;

		if ("link_account".equals(event.getComponentId())) {
			if (member.getRoles().contains(DiscordUtil.getVerifiedRole())) {
				event.reply(
					new MessageCreateBuilder()
						.setContent("🤚 **Wait, wait, wait, hold on a second!**\n\nYour account is already verified.\nDo you, perhaps, want to unlink it instead?\n" + DiscordUtil.LARGE_EMOJI_DISABLER)
						.addActionRow(ActionRow.of(Button.danger("unlink_account", "Unlink account").withEmoji(Emoji.fromUnicode("💀"))).getComponents())
						.build()
				).setEphemeral(true).queue();
				return;
			}

			event.replyModal(
				Modal.create("account_linking", "What's your Minecraft username?")
					.addActionRow(
						TextInput.create("account_linking_name", "Your Minecraft username", TextInputStyle.SHORT)
							.setPlaceholder("You have to be online on the server!")
							.setMinLength(3)
							.setMaxLength(16)
							.setRequired(true)
							.build())
					.build()
			).queue();
			return;
		}

		if ("unlink_account".equals(event.getComponentId())) {
			if (member.getRoles().contains(DiscordUtil.getVerifiedRole())) {
				String nickname = member.getEffectiveName();
				Player player = Bukkit.getPlayerExact(nickname);
				if (player == null) {
					DiscordBot.getGuild().removeRoleFromMember(member, DiscordUtil.getVerifiedRole()).queue();
					Discorder.save(member.getIdLong(), -1L, "");
				} else {
					Messenger.messenger(player).sendMessage("discord.unlinked");
					Discorder.getDiscorder(player).setDiscordId(-1L);
				}
			}
			event.reply(":shell: Well, that's sad. You are no longer verified.").setEphemeral(true).queue();
		}
	}

	@Override
	public void onModalInteraction(ModalInteractionEvent event) {
		if (!"account_linking".equals(event.getModalId())) return;

		Member member = event.getMember();
		if (member == null) return;

		ModalMapping modalMapping = event.getInteraction().getValue("account_linking_name");
		if (modalMapping == null) return;

		String nickname = modalMapping.getAsString();
		Player player = Bukkit.getPlayerExact(nickname);
		if (player == null) {
			event.reply(":tv: Specified player (" + nickname.replace("_", "\\_").replace("*", "\\*") + ") is not online on the server!").setEphemeral(true).queue();
			return;
		}

		if (DiscordCommand.suggestVerification(player, member.getIdLong(), member.getEffectiveName())) {
			event.reply(":grey_question: In-game message was sent to you in the server!").setEphemeral(true).queue();
		} else {
			event.reply(":grey_exclamation: Player " + nickname + " has already received a verification request within the last five minutes!").setEphemeral(true).queue();
		}
	}

}
