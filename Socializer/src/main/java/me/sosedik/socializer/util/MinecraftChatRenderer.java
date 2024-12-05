package me.sosedik.socializer.util;

import org.jetbrains.annotations.NotNull;

/**
 * Used to send the raw message from the messenger into the chat message
 */
public interface MinecraftChatRenderer {

	/**
	 * Sends a chat message
	 *
	 * @param nickname sender nickname
	 * @param rawMessage raw message
	 */
	void sendBukkitMessage(@NotNull String nickname, @NotNull String rawMessage);

}
