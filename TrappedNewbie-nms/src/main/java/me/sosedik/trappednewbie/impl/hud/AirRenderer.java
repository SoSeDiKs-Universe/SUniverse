package me.sosedik.trappednewbie.impl.hud;

import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.util.SpacingUtil;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.api.hud.SimpleHudRenderer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.ShadowColor;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static me.sosedik.utilizer.api.message.Mini.combine;

@NullMarked
public class AirRenderer extends SimpleHudRenderer {

	private static final int AIR_WIDTH = ResourceLib.requireFontData(TrappedNewbie.trappedNewbieKey("air/air")).width() + 1;
	private static final int HUD_OFFSET = 10;
	private static final int HUD_LENGTH = (AIR_WIDTH - 2) * 10 + 2;
	private static final Component EMPTY_AIR = mapping("air/air_empty");
	private static final Component AIR_BUBBLE = mapping("air/air");
	private static final Component AIR_BUBBLE_POP = mapping("air/air_bursting");

	private final List<Component> airDisplay = new ArrayList<>();
	private int lastAir = Integer.MAX_VALUE;

	public AirRenderer(Player player) {
		super("air_hud", player);
	}

	@Override
	public @Nullable Component render() {
		if (this.player.getGameMode().isInvulnerable()) return null;
		if (GhostyPlayer.isGhost(player)) return null;

		int[] airs = getAir();
		int scaledAir = airs[0];
		int scaledMaxAir = airs[1];
		int originalAir = airs[2];
		int originalMaxAir = airs[3];
		if (originalAir >= originalMaxAir) return null;

		this.airDisplay.clear();

		int pop = originalMaxAir / 10;

		// Empty air
		for (int i = 0; i < scaledMaxAir - scaledAir; i++) this.airDisplay.add(EMPTY_AIR);

		// Bubble pop
		if (originalAir < this.lastAir && originalAir % pop == 0) {
			this.airDisplay.removeLast();
			this.airDisplay.add(AIR_BUBBLE_POP);
		}

		// Leftover air
		for (int i = 0; i < scaledAir; i++) this.airDisplay.add(AIR_BUBBLE);

		this.lastAir = originalAir;

		Component thirsts = combine(SpacingUtil.getSpacing(-2), this.airDisplay);
		return SpacingUtil.getOffset(HUD_OFFSET, HUD_LENGTH, thirsts.shadowColor(ShadowColor.none()));
	}

	private int[] getAir() {
		int air = this.player.getRemainingAir();
		int maxAir = this.player.getMaximumAir();
		if (maxAir <= 10) return new int[]{air, 10, air, maxAir};
		return new int[]{(int) Math.ceil(air * 10D / maxAir), 10, air, maxAir};
	}

}
