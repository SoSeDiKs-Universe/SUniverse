package me.sosedik.trappednewbie.api.hud;

import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.util.SpacingUtil;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.utilizer.api.message.Mini;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class SimpleHudRenderer implements HudRenderer {

	protected final Player player;
	private final String id;

	protected SimpleHudRenderer(String id, Player player) {
		this.id = id;
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	@Override
	public String getId() {
		return id;
	}

	protected static Component mapping(String key) {
		return ResourceLib.requireFontData(TrappedNewbie.trappedNewbieKey(key)).mapping();
	}

	protected static Component overlayIcon(Component icon, Component overlay, int width) {
		return Mini.combine(SpacingUtil.getNegativePixel(), icon, SpacingUtil.getOffset(-width, width, overlay));
	}

}
