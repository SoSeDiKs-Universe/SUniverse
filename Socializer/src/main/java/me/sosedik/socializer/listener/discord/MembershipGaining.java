package me.sosedik.socializer.listener.discord;

import me.sosedik.socializer.Socializer;
import me.sosedik.socializer.discord.DiscordBot;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * Granting Member role for accepting the rules
 */
public class MembershipGaining extends ListenerAdapter {

	private final Role memberRole;

	public MembershipGaining(@NotNull Socializer plugin) {
		if (!plugin.getConfig().getBoolean("grant-memberships", false)) {
			this.memberRole = null;
			return;
		}

		long roleId = plugin.getConfig().getLong("discord.roles.member");
		this.memberRole = DiscordBot.getDiscordBot().getRoleById(roleId);
		DiscordBot.getDiscordBot().addEventListener(this);
	}

	@Override
	public void onButtonInteraction(ButtonInteractionEvent event) {
		if (!"agree_with_the_rules".equals(event.getComponentId())) return;

		Member member = event.getMember();
		if (member == null) return;

		if (member.getRoles().contains(memberRole)) {
			event.reply(":flushed: You already are a member!").setEphemeral(true).queue();
			return;
		}

		event.reply(":muscle: Big brain, wise choice! You are now a " + memberRole.getAsMention() + "!").setEphemeral(true).queue();
		DiscordBot.getGuild().addRoleToMember(member, memberRole).queue();
	}

}
