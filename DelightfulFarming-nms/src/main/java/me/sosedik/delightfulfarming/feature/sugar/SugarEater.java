package me.sosedik.delightfulfarming.feature.sugar;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.papermc.paper.datacomponent.item.FoodProperties;
import me.sosedik.delightfulfarming.DelightfulFarming;
import me.sosedik.delightfulfarming.listener.sugar.CaloriesOnJoinLeave;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.feature.TabRenderer;
import me.sosedik.resourcelib.util.SpacingUtil;
import me.sosedik.utilizer.util.MathUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.ShadowColor;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Random;

import static me.sosedik.delightfulfarming.DelightfulFarming.delightfulFarmingKey;
import static me.sosedik.utilizer.api.message.Mini.combined;

@NullMarked
public class SugarEater {

	private static final Random RANDOM = new Random();

	private static final Component SUGAR_BACKGROUND = ResourceLib.requireFontData(delightfulFarmingKey("sugar_bar")).icon();

	private static final String DYNAMIC_CALORIES_TAG = "dynamic_calories";
	private static final String STATIC_CALORIES_TAG = "static_calories";
	private static final String CUMULATIVE_CALORIES_TAG = "cumulative_calories";
	private static final String MUSCLES_TAG = "muscles";
	private static final String BODY_FAT_TAG = "body_fat";
	private static final String TIMER_TAG = "timer";

	private static final int MIN_BODY_FAT = 0;
	private static final int MAX_BODY_FAT = 150;
	private static final int MIN_MUSCLES = -50;
	private static final int MAX_MUSCLES = 50;
	private static final double EXHAUSTION_MULTIPLIER = -2;

	private final Player player;
	private boolean active = false;
	private int timer;
	private MealTime mealTime;
	private double dynamicMealCalories;
	private double dynamicDailyCumulativeCalories;
	private double dailyTotalStaticCalories;
	private int bodyFat;
	private int muscles;

	public SugarEater(Player player, ReadWriteNBT nbt) {
		this.player = player;
		this.timer = nbt.hasTag(TIMER_TAG) ? nbt.getInteger(TIMER_TAG) : (int) player.getWorld().getTime();
		this.mealTime = calcMealTime();
		this.dynamicMealCalories = nbt.getOrDefault(DYNAMIC_CALORIES_TAG, 0D);
		this.dailyTotalStaticCalories = nbt.getOrDefault(STATIC_CALORIES_TAG, 1500D);
		this.dynamicDailyCumulativeCalories = nbt.getOrDefault(CUMULATIVE_CALORIES_TAG, 1500D);
		this.bodyFat = nbt.getOrDefault(BODY_FAT_TAG, 5);
		this.muscles = nbt.getOrDefault(MUSCLES_TAG, 0);

		TabRenderer.of(player).addHeaderElement(delightfulFarmingKey("sugar_eater"), () -> {
			if (!isActive()) return null;
			if (true) return List.of(
				combined(SpacingUtil.getNegativePixel(),
					SpacingUtil.getOffset(-(186 / 2), 186 + 1, SUGAR_BACKGROUND),
					SpacingUtil.getOffset(1, 24 + 1, Component.text("a").shadowColor(ShadowColor.none()).font(delightfulFarmingKey("fonts"))),
					SpacingUtil.getOffset(24 + 1, 24 + 1, Component.text("a").shadowColor(ShadowColor.none()).font(delightfulFarmingKey("fonts"))),
//					SpacingUtil.getOffset(24 * 2 + 1, 24 + 1, Component.text("a").shadowColor(ShadowColor.none()).font(delightfulFarmingKey("fonts"))),
						SpacingUtil.getOffset(-(24), 24 + 1, Component.text("b").shadowColor(ShadowColor.none()).font(delightfulFarmingKey("fonts"))),
						SpacingUtil.getOffset(-(24 * 2), 24 + 1, Component.text("b").shadowColor(ShadowColor.none()).font(delightfulFarmingKey("fonts"))),
//						SpacingUtil.getOffset(-(24 * 3), 24 + 1, Component.text("b").shadowColor(ShadowColor.none()).font(delightfulFarmingKey("fonts"))),
					SpacingUtil.getOffset(1, 24 + 1, Component.text("c").shadowColor(ShadowColor.none()).font(delightfulFarmingKey("fonts"))),
					SpacingUtil.getOffset(24 + 1, 24 + 1, Component.text("c").shadowColor(ShadowColor.none()).font(delightfulFarmingKey("fonts"))),
//					SpacingUtil.getOffset(24 * 2 + 1, 24 + 1, Component.text("c").shadowColor(ShadowColor.none()).font(delightfulFarmingKey("fonts"))),
						SpacingUtil.getOffset(-(24), 24 + 1, Component.text("d").shadowColor(ShadowColor.none()).font(delightfulFarmingKey("fonts"))),
						SpacingUtil.getOffset(-(24 * 2), 24 + 1, Component.text("d").shadowColor(ShadowColor.none()).font(delightfulFarmingKey("fonts")))
//						SpacingUtil.getOffset(-(24 * 3), 24 + 1, Component.text("d").shadowColor(ShadowColor.none()).font(delightfulFarmingKey("fonts")))
//					Component.text("a")
				),
				Component.empty(),
				Component.empty(),
				Component.empty(),
				Component.empty(),
				Component.empty(),
				Component.empty() // remove
//				MUSCLES_BACKGROUND,
//				Component.empty(),
//				BODY_FAT_BACKGROUND
			);
			return List.of(
				Component.text("Muscles: " + getMuscles()),
				Component.text("Body fat: " + getBodyFat()),
				Component.text("Calories: " + MathUtil.round(getDynamicMealCalories(), 1) + " | " + MathUtil.round(getDynamicDailyCumulativeCalories(), 1) + " | " + MathUtil.round(getDailyTotalStaticCalories(), 1))
			);
		});

		DelightfulFarming.scheduler().sync(task -> {
			if (!this.player.isOnline()) return true;

			this.timer = (int) this.player.getWorld().getTime();
			if (!this.mealTime.is(this.timer)) {
				addCumulativeCalories(getDynamicMealCalories());
				MealTime newMealTime = calcMealTime();
				if (newMealTime == MealTime.BREAKFAST && this.mealTime == MealTime.NIGHT_SNACK) {
					onDailyReset();
				} else {
					this.dynamicMealCalories = 0;
				}
				this.mealTime = newMealTime;
			}

			return false;
		}, 1L, 1L);
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
		this.player.sendHealthUpdate();
	}

	public double getDynamicMealCalories() {
		return this.dynamicMealCalories;
	}

	public double getDailyTotalStaticCalories() {
		return this.dailyTotalStaticCalories;
	}

	public double getDynamicDailyCumulativeCalories() {
		return this.dynamicDailyCumulativeCalories;
	}

	public void addCumulativeCalories(double calories) {
		this.dynamicDailyCumulativeCalories += calories;
	}

	public void addCalories(FoodProperties data) {
		double calories = getDynamicMealCalories();
		if (calories >= 2000) return;

		if (calories >= 1000) {
			addCalories(data.saturation() * 50);
			return;
		}

		int adding = data.nutrition() * 50;
		double total = calories + adding;
		if (total > 1000) {
			addCalories(total - 1000);
			addCalories(data.saturation() * 50);
			return;
		}

		addCalories(adding);
	}

	public void addCalories(double calories) {
		if (calories > 0 && getDynamicMealCalories() >= 2000) return;

		double newMealCalories = getDynamicMealCalories() + calories;
		if (newMealCalories > 2000) {
			double dyn = getDynamicMealCalories();
			double diff = dyn - 2000;
			this.dynamicMealCalories = 2000;
			if (diff > 0)
				this.dailyTotalStaticCalories = Math.min(getDailyTotalStaticCalories() + diff, 8000);
		} else {
			this.dynamicMealCalories = newMealCalories;
			if (calories > 0)
				this.dailyTotalStaticCalories = Math.min(getDailyTotalStaticCalories() + calories, 8000);
		}

		while (getDynamicMealCalories() < 0) {
			double cumulative = getDynamicDailyCumulativeCalories();
			if (cumulative > 0) {
				double adding = Math.min(cumulative, 10);
				this.dynamicDailyCumulativeCalories -= adding;
				this.dynamicMealCalories += adding;
				continue;
			}

			int bodyFat = getBodyFat();
			if (bodyFat > MIN_BODY_FAT) {
				addBodyFat(-1);
				addCumulativeCalories(700);
				continue;
			}

			int muscles = getMuscles();
			if (muscles > MIN_MUSCLES) {
				addMuscles(-1);
				addCumulativeCalories(1000);
				continue;
			}

			// ToDo: Death message
			DelightfulFarming.scheduler().sync(() -> {
				player.sendMessage("No calories, no fat, no muscles 💀");
//				player.setHealth(0); // ToDo: not yet done :D
			});
			return;
		}
	}

	public int getBodyFat() {
		return bodyFat;
	}

	public void addBodyFat(int bodyFat) {
		this.bodyFat = Math.clamp(getBodyFat() + bodyFat, MIN_BODY_FAT, MAX_BODY_FAT);
	}

	public int getMuscles() {
		return muscles;
	}

	public void addMuscles(int muscles) {
		this.muscles = Math.clamp(getMuscles() + muscles, MIN_MUSCLES, MAX_MUSCLES);
	}

	public void onExhaustion(float exhaustion) {
		addCalories(exhaustion * EXHAUSTION_MULTIPLIER);
	}

	public void onDailyReset() {
		int fatBonus = (int) (getDynamicDailyCumulativeCalories() * MathUtil.getRandomDoubleInRange(0.0002, 0.0004));
		int musclesBonus = (int) (Math.max(0D, getDailyTotalStaticCalories() - getDynamicDailyCumulativeCalories()) * 0.001);
		addBodyFat(fatBonus); // 10-20% calories / 500
		addMuscles(musclesBonus); // 10% calories / 100
		this.dynamicMealCalories = 0;
		this.dynamicDailyCumulativeCalories = 0;
		this.dailyTotalStaticCalories = 0;
		player.sendMessage("Daily calories reset (fat: +%s ; muscles: +%s)".formatted(fatBonus, musclesBonus)); // todo debug remove
	}

	public int getHungerPoints() {
		if (getDynamicMealCalories() >= 2000) return 20;
		return 10;
	}

	public int getHungerBarPoints() {
		if (!isActive()) return this.player.getFoodLevel();
		return (int) Math.clamp(getDynamicMealCalories() / 50, 0D, 20D);
	}

	public float getSaturationPoints() {
		if (!isActive()) return this.player.getSaturation();
		double dynamicMealCalories = getDynamicMealCalories();
		if (dynamicMealCalories <= 1000) return 0F;
		dynamicMealCalories -= 1000;
		return Math.clamp((float) dynamicMealCalories / 50, 0F, 20F);
	}

	public MealTime getMealTime() {
		return this.mealTime;
	}

	private MealTime calcMealTime() {
		return MealTime.getMealTime(this.timer);
	}

	public ReadWriteNBT save() {
		ReadWriteNBT nbt = NBT.createNBTObject();
		nbt.setDouble(DYNAMIC_CALORIES_TAG, getDynamicMealCalories());
		nbt.setDouble(STATIC_CALORIES_TAG, getDailyTotalStaticCalories());
		nbt.setDouble(CUMULATIVE_CALORIES_TAG, getDynamicDailyCumulativeCalories());
		nbt.setInteger(MUSCLES_TAG, getMuscles());
		nbt.setInteger(BODY_FAT_TAG, getBodyFat());
		return nbt;
	}

	public static SugarEater of(Player player) {
		return CaloriesOnJoinLeave.getOrLoad(player);
	}

	public void onLongAbsence() {
		if (!isActive()) {
		}
	}

}
