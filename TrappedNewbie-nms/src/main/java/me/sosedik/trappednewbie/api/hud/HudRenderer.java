package me.sosedik.trappednewbie.api.hud;

import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface HudRenderer {

	String getId();

	@Nullable Component render();

	default void onQuit() {
		// Override if needed
	}

}
