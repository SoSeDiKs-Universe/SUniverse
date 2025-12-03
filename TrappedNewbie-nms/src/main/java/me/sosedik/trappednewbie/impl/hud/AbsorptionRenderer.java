package me.sosedik.trappednewbie.impl.hud;

import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.util.SpacingUtil;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.api.hud.SimpleHudRenderer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.ShadowColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static me.sosedik.utilizer.api.message.Mini.combine;

@NullMarked
public class AbsorptionRenderer extends SimpleHudRenderer {

	private static final int HEART_WIDTH = ResourceLib.requireFontData(TrappedNewbie.trappedNewbieKey("heart/extra_heart")).width() + 1;
	private static final int HUD_OFFSET = -91;
	private static final Component EXTRA_HEART = mapping("heart/extra_heart");
	private static final Component EXTRA_HALF_HEART = overlayIcon(EXTRA_HEART, mapping("heart/extra_half_heart"), HEART_WIDTH);

	private final List<Component> absorptionDisplay = new ArrayList<>();

	public AbsorptionRenderer(Player player) {
		super("absorption_hud", player);
	}

	@Override
	public @Nullable Component render() {
		if (this.player.getGameMode().isInvulnerable()) return null;

		int currentAbsorption = getAbsorption(this.player);
		if (currentAbsorption <= 0) return null;

		this.absorptionDisplay.clear();

		// Full hearts
		for (int i = 0; i < currentAbsorption / 2; i++) this.absorptionDisplay.add(EXTRA_HEART);

		// Half heart
		if (currentAbsorption % 2 == 1) {
			currentAbsorption++;
			this.absorptionDisplay.add(EXTRA_HALF_HEART);
		}

		Component combined = combine(SpacingUtil.getSpacing(-2), absorptionDisplay);
		return SpacingUtil.getOffset(HUD_OFFSET, (HEART_WIDTH - 2) * (currentAbsorption / 2) + 2, combined.shadowColor(ShadowColor.none()));
	}

	/**
	 * Gets the rendered absorption amount
	 *
	 * @param player player
	 * @return rendered absorption amount
	 */
	public static int getAbsorption(Player player) {
		double absorption;
		if (player.getVehicle() instanceof LivingEntity vehicle && vehicle.getRider() == player && PossessingPlayer.isPossessingSoft(player)) {
			absorption = vehicle.getAbsorptionAmount();
		} else {
			absorption = player.getAbsorptionAmount();
		}
		return (int) Math.min(Math.ceil(absorption), 20);
	}

}
