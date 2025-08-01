package me.sosedik.trappednewbie.impl.hud;

import me.sosedik.delightfulfarming.feature.sugar.SugarEater;
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

	private static final int HUNGER_WIDTH = ResourceLib.requireFontData(TrappedNewbie.trappedNewbieKey("hunger/food_empty")).width() + 1;
	private static final int HUD_OFFSET = 10;
	private static final int HUD_LENGTH = (HUNGER_WIDTH - 2) * 10 + 2;
	private static final HungerIconSet BREAKFAST_ICON_SET = new HungerIconSet("_breakfast");
	private static final HungerIconSet DINNER_ICON_SET = new HungerIconSet("");
	private static final HungerIconSet SUPPER_ICON_SET = new HungerIconSet("_supper");
	private static final HungerIconSet NIGHT_SNACK_ICON_SET = new HungerIconSet("_snack");

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

		var sugarEater = SugarEater.of(this.player);
		HungerIconSet iconSet = getSet(sugarEater);
		int maxFood = 20;
		int foodLevel = sugarEater.getHungerBarPoints();
		boolean poisoned = this.player.hasPotionEffect(PotionEffectType.HUNGER);

		// Empty food
		for (int i = 0; i < (maxFood - foodLevel) / 2; i++) this.foodDisplay.add(poisoned ? iconSet.emptyPoison() : iconSet.empty());

		// Half food
		if (foodLevel % 2 == 1) this.foodDisplay.add(poisoned ? iconSet.halfPoison() : iconSet.half());

		// Full food
		for (int i = 0; i < foodLevel / 2; i++) this.foodDisplay.add(poisoned ? iconSet.fullPoison() : iconSet.full());

		Component foods = combine(SpacingUtil.getSpacing(-2), this.foodDisplay);
		return SpacingUtil.getOffset(HUD_OFFSET, HUD_LENGTH, foods.shadowColor(ShadowColor.none()));
	}

	private HungerIconSet getSet(SugarEater sugarEater) {
		return switch (sugarEater.getMealTime()) {
			case BREAKFAST -> BREAKFAST_ICON_SET;
			case DINNER -> DINNER_ICON_SET;
			case SUPPER -> SUPPER_ICON_SET;
			case NIGHT_SNACK -> NIGHT_SNACK_ICON_SET;
		};
	}

	private record HungerIconSet(
		Component empty,
		Component full,
		Component half,
		Component emptyPoison,
		Component fullPoison,
		Component halfPoison
	) {

		HungerIconSet(String suffix) {
			this(
				mapping("hunger/food_empty" + suffix),
				mapping("hunger/food_full" + suffix),
				mapping("hunger/food_half" + suffix),
				mapping("hunger/food_empty_hunger" + suffix),
				mapping("hunger/food_empty_hunger" + suffix),
				mapping("hunger/food_empty_hunger" + suffix)
			);
		}

	}

}
