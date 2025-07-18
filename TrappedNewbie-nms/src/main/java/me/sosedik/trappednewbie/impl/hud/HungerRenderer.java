package me.sosedik.trappednewbie.impl.hud;

import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.util.SpacingUtil;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.api.hud.SimpleHudRenderer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.ShadowColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static me.sosedik.utilizer.api.message.Mini.combine;

@NullMarked
public class HungerRenderer extends SimpleHudRenderer {

	private static final int HUNGER_WIDTH = ResourceLib.requireFontData(TrappedNewbie.trappedNewbieKey("hunger/empty_food")).width() + 1;
	private static final int HUD_OFFSET = 10;
	private static final int HUD_LENGTH = (HUNGER_WIDTH - 2) * 10 + 2;
	private static final Component EMPTY_HUNGER = mapping("hunger/empty_food");
	private static final Component FULL_HUNGER = mapping("hunger/food");
	private static final Component HALF_HUNGER = mapping("hunger/half_food");
	private static final Component POISONED_FULL_HUNGER = mapping("hunger/poisoned_food");
	private static final Component POISONED_HALF_HUNGER = mapping("hunger/poisoned_half_food");

	private final List<Component> foodDisplay = new ArrayList<>();

	public HungerRenderer(Player player) {
		super("hunger_hud", player);
	}

	@Override
	public @Nullable Component render() {
		if (this.player.getGameMode().isInvulnerable()) return null;
		if (GhostyPlayer.isGhost(this.player)) return null;
		if (PossessingPlayer.isPossessing(this.player)) return null;

		this.foodDisplay.clear();

		int maxFood = 20;
		int foodLevel = this.player.getFoodLevel();
		boolean poisoned = this.player.hasPotionEffect(PotionEffectType.HUNGER);

		// Empty food
		for (int i = 0; i < (maxFood - foodLevel) / 2; i++) this.foodDisplay.add(EMPTY_HUNGER);

		// Half food
		if (foodLevel % 2 == 1) this.foodDisplay.add(poisoned ? POISONED_HALF_HUNGER : HALF_HUNGER);

		// Full food
		for (int i = 0; i < foodLevel / 2; i++) this.foodDisplay.add(poisoned ? POISONED_FULL_HUNGER : FULL_HUNGER);

		Component foods = combine(SpacingUtil.getSpacing(-2), this.foodDisplay);
		return SpacingUtil.getOffset(HUD_OFFSET, HUD_LENGTH, foods.shadowColor(ShadowColor.none()));
	}

}
