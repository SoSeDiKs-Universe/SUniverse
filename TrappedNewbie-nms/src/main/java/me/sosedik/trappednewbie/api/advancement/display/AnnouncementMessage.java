package me.sosedik.trappednewbie.api.advancement.display;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.util.Objects;

public enum AnnouncementMessage {

	SUPER_CHALLENGE(Objects.requireNonNull(TextColor.fromHexString("#ff2a2a"))),
	TORTURE(NamedTextColor.DARK_RED),
	SUPER_TORTURE(Objects.requireNonNull(TextColor.fromHexString("#8b00e8"))),
	CHEAT(NamedTextColor.BLACK);

	private final TextColor color;

	AnnouncementMessage(TextColor color) {
		this.color = color;
	}

	public TextColor getColor() {
		return this.color;
	}

}
