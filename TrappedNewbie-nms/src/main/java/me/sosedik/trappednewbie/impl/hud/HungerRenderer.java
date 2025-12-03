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
		if (PossessingPlayer.isPossessingSoft(this.player)) return null;

		this.foodDisplay.clear();

		var sugarEater = SugarEater.of(this.player);
		HungerIconSet iconSet = getSet(sugarEater);
		int maxFood = 20;
		int foodLevel = sugarEater.getHungerBarPoints();
		float saturation = sugarEater.getSaturationPoints();
		boolean poisoned = this.player.hasPotionEffect(PotionEffectType.HUNGER);

		// Empty food
		for (int i = 0; i < (maxFood - foodLevel) / 2; i++) this.foodDisplay.add(poisoned ? iconSet.emptyPoison() : iconSet.empty());

		// Half food
		if (foodLevel % 2 == 1) this.foodDisplay.add(poisoned ? iconSet.halfPoison() : iconSet.half());

		// Full food
		for (int i = 0; i < foodLevel / 2; i++) this.foodDisplay.add(poisoned ? iconSet.fullPoison() : iconSet.full());

		Component foods = combine(SpacingUtil.getSpacing(-2), this.foodDisplay);

		if (saturation > 0) {
			this.foodDisplay.clear();
			int fulls = ((int) saturation) / 2;
			float leftover = saturation - ((int) saturation);
			if (leftover > 0) {
				if (leftover < 0.6F) this.foodDisplay.add(iconSet.saturated1());
				else if (leftover < 1.2F) this.foodDisplay.add(iconSet.saturated2());
				else this.foodDisplay.add(iconSet.saturated3());
			}
			for (int i = 0; i < fulls; i++) this.foodDisplay.add(iconSet.saturated4());
			int satIcons = leftover > 0 ? fulls + 1 : fulls;
			int saturationWidth = ((HUNGER_WIDTH - 2) * satIcons) + 1;
			Component saturationDisplay = combine(SpacingUtil.getSpacing(-2), this.foodDisplay);
			foods = Component.textOfChildren(foods, SpacingUtil.getOffset(-saturationWidth - 1, saturationWidth, saturationDisplay));
		}

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
		Component halfPoison,
		Component saturated1,
		Component saturated2,
		Component saturated3,
		Component saturated4
	) {

		HungerIconSet(String suffix) {
			this(
				mapping("hunger/food_empty" + suffix),
				mapping("hunger/food_full" + suffix),
				mapping("hunger/food_half" + suffix),
				mapping("hunger/food_empty_hunger" + suffix),
				mapping("hunger/food_full_hunger" + suffix),
				mapping("hunger/food_half_hunger" + suffix),
				mapping("hunger/sat" + suffix + "_1"),
				mapping("hunger/sat" + suffix + "_2"),
				mapping("hunger/sat" + suffix + "_3"),
				mapping("hunger/sat" + suffix + "_4")
			);
		}

	}

}
