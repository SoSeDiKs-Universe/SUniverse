package me.sosedik.socializer.listener.discord;

import me.sosedik.socializer.Socializer;
import me.sosedik.socializer.discord.DiscordBot;
import me.sosedik.socializer.util.DiscordUtil;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

/**
 * Deny changing nicknames for verified players
 */
@NullMarked
public class NoNicknameChange extends ListenerAdapter {

	private static final Map<Long, String> ALLOWED_NICKNAMES = new HashMap<>();

	public NoNicknameChange() {
		DiscordBot.getDiscordBot().addEventListener(this);
	}

	@Override
	public void onGuildMemberUpdateNickname(GuildMemberUpdateNicknameEvent event) {
		var member = event.getMember();
		if (!member.getRoles().contains(DiscordUtil.getVerifiedRole())) return;

		String allowedNickname = ALLOWED_NICKNAMES.remove(member.getIdLong());
		if (allowedNickname != null && allowedNickname.equals(event.getNewNickname())) return;
		if (event.getOldNickname() == null) return;

		Socializer.scheduler().async(() -> DiscordUtil.modifyNickname(member, event.getOldNickname()), 10L);
	}

	/**
	 * Temporary whitelists nickname to allow changing to it
	 *
	 * @param userId Discord user id
	 * @param nickname nickname
	 */
	public static synchronized void whitelist(long userId, String nickname) {
		ALLOWED_NICKNAMES.put(userId, nickname);
		Socializer.scheduler().sync(() -> ALLOWED_NICKNAMES.remove(userId), 30L);
	}

}
