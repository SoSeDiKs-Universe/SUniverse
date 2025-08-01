package me.sosedik.delightfulfarming.feature.sugar;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.papermc.paper.datacomponent.item.FoodProperties;
import me.sosedik.delightfulfarming.DelightfulFarming;
import me.sosedik.delightfulfarming.listener.sugar.CaloriesOnJoinLeave;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.Random;

@NullMarked
public class SugarEater {

	private static final Random RANDOM = new Random();

	private static final String MEAL_CALORIES_TAG = "meal_calories";
	private static final String DAILY_CALORIES_TAG = "daily_calories";
	private static final String DYNAMIC_CALORIES_TAG = "dynamic_calories";
	private static final String TIMER_TAG = "timer";

	private final Player player;
	private boolean active = false;
	private int timer;
	private MealTime mealTime;
	private int mealCalories;
	private int dailyTotalCalories;
	private double dynamicDailyCalories;

	public SugarEater(Player player, ReadWriteNBT nbt) {
		this.player = player;
		this.timer = nbt.hasTag(TIMER_TAG) ? nbt.getInteger(TIMER_TAG) : (int) player.getWorld().getTime();
		this.mealTime = calcMealTime();
		this.mealCalories = nbt.getOrDefault(MEAL_CALORIES_TAG, 0);
		this.dailyTotalCalories = nbt.getOrDefault(DAILY_CALORIES_TAG, 1000);
		this.dynamicDailyCalories = nbt.getOrDefault(DYNAMIC_CALORIES_TAG, 1000D);

		DelightfulFarming.scheduler().sync(task -> {
			if (!this.player.isOnline()) return true;

			this.timer = (int) this.player.getWorld().getTime();
			if (!this.mealTime.is(this.timer)) {
				this.mealTime = calcMealTime();
				this.mealCalories = 0;
			}

			return false;
		}, 1L, 1L);
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getMealCalories() {
		return this.mealCalories;
	}

	public int getDailyTotalCalories() {
		return this.dailyTotalCalories;
	}

	public double getDynamicDailyCalories() {
		return this.dynamicDailyCalories;
	}

	public void addCalories(FoodProperties data) {
		int calories = getMealCalories();
		if (calories >= 2000) return;

		if (calories >= 1000) {
			addCalories((int) (data.saturation() * 50));
			return;
		}

		int adding = data.nutrition() * 50;
		int total = calories + adding;
		if (total > 1000) {
			addCalories(total - 1000);
			addCalories((int) (data.saturation() * 50));
			return;
		}

		addCalories(adding);
	}

	public void addCalories(int calories) {
		if (getMealCalories() >= 2000) return;

		int newMealCalories = getMealCalories() + calories;
		if (newMealCalories > 2000) {
			int diff = getMealCalories() - 2000;
			this.mealCalories = 2000;
			this.dailyTotalCalories = Math.min(getDailyTotalCalories() + diff, 8000);
		} else {
			this.mealCalories = newMealCalories;
			this.dailyTotalCalories = Math.min(getDailyTotalCalories() + calories, 8000);
		}
	}

	public void onExhaustion(float exhaustion) {
		this.dynamicDailyCalories -= exhaustion * 50;
	}

	public int getHungerPoints() {
		if (getMealCalories() >= 2000) return 20;
		return 10;
	}

	public int getHungerBarPoints() {
		if (!isActive()) return this.player.getFoodLevel();
		return Math.clamp(getMealCalories() / 50, 0, 20);
	}

	public MealTime getMealTime() {
		return this.mealTime;
	}

	private MealTime calcMealTime() {
		return MealTime.getMealTime(this.timer);
	}

	public ReadWriteNBT save() {
		ReadWriteNBT nbt = NBT.createNBTObject();
		nbt.setInteger(MEAL_CALORIES_TAG, getMealCalories());
		nbt.setInteger(DAILY_CALORIES_TAG, getDailyTotalCalories());
		nbt.setDouble(DYNAMIC_CALORIES_TAG, getDynamicDailyCalories());
//		nbt.setInteger(STORED_CALORIES_TAG, sugarEater.getStoredCalories());
//		nbt.setInteger(BODY_FAT_TAG, sugarEater.getBodyFat());
//		nbt.setInteger(MUSCLES_TAG, sugarEater.getMuscles());
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
