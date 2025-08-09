package me.sosedik.trappednewbie.impl.hud;

import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.resourcelib.util.SpacingUtil;
import me.sosedik.trappednewbie.api.hud.SimpleHudRenderer;
import me.sosedik.trappednewbie.dataset.TrappedNewbieEffects;
import me.sosedik.trappednewbie.impl.thirst.ThirstyPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.ShadowColor;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static me.sosedik.utilizer.api.message.Mini.combined;

@NullMarked
public class ThirstRenderer extends SimpleHudRenderer {

	private static final ThirstIconSet NORMAL = new ThirstIconSet("");
	private static final ThirstIconSet POISONED = new ThirstIconSet("_poisoned");
	private static final ThirstIconSet HOT = new ThirstIconSet("_hot");
	private static final ThirstIconSet HOT_POISONED = new ThirstIconSet("_hot_poisoned");
	private static final int HUD_OFFSET = 11;
	private static final int HUD_LENGTH = 80;

	private final List<Component> thirstDisplay = new ArrayList<>();

	public ThirstRenderer(Player player) {
		super("thirst_hud", player);
	}

	@Override
	public @Nullable Component render() {
		if (this.player.getGameMode().isInvulnerable()) return null;
		if (GhostyPlayer.isGhost(this.player)) return null;
		if (PossessingPlayer.isPossessing(this.player)) return null;

		this.thirstDisplay.clear();

		ThirstIconSet iconSet = getIconSet();

		var thirstyPlayer = ThirstyPlayer.of(this.player);
		int maxThirst = ThirstyPlayer.MAX_THIRST;
		int thirstLevel = thirstyPlayer.getThirst();
		float saturation = thirstyPlayer.getSaturation();

		// Empty thirst
		for (int i = 0; i < (maxThirst - thirstLevel) / 2; i++) this.thirstDisplay.add(iconSet.empty());

		// Half thirst
		if (thirstLevel % 2 == 1) this.thirstDisplay.add(iconSet.half());

		// Full thirst
		for (int i = 0; i < thirstLevel / 2; i++) this.thirstDisplay.add(iconSet.full());

		Component thirsts = combined(this.thirstDisplay);
		return SpacingUtil.getOffset(HUD_OFFSET, HUD_LENGTH, thirsts.shadowColor(ShadowColor.none()));
	}

	private ThirstIconSet getIconSet() {
		if (this.player.hasPotionEffect(TrappedNewbieEffects.THIRST)) {
			return POISONED;
		}
		return NORMAL;
	}

	private record ThirstIconSet(
		Component empty,
		Component full,
		Component half,
		Component saturation,
		Component saturationHalf
	) {

		ThirstIconSet(String type) {
			this(
				mapping("thirst/thirst" + type + "_empty"),
				mapping("thirst/thirst" + type + "_full"),
				mapping("thirst/thirst" + type + "_half"),
				mapping("thirst/thirst" + type + "_sat"),
				mapping("thirst/thirst" + type + "_sat_half")
			);
		}

	}

}
