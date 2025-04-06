package me.sosedik.socializer.util;

import org.jspecify.annotations.NullMarked;

/**
 * Used to send the raw message from the messenger into the chat message
 */
@NullMarked
public interface MinecraftChatRenderer {

	/**
	 * Sends a chat message
	 *
	 * @param nickname sender nickname
	 * @param rawMessage raw message
	 */
	void sendBukkitMessage(String nickname, String rawMessage);

}
