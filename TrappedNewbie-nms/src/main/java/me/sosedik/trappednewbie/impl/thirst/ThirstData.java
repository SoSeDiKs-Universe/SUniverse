package me.sosedik.trappednewbie.impl.thirst;

import de.tr7zw.nbtapi.NBT;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PotionContents;
import me.sosedik.delightfulfarming.dataset.DelightfulFarmingItems;
import me.sosedik.delightfulfarming.dataset.DelightfulFarmingTags;
import me.sosedik.trappednewbie.dataset.TrappedNewbieEffects;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.dataset.TrappedNewbieTags;
import me.sosedik.trappednewbie.listener.item.FillingBowlWithWater;
import me.sosedik.trappednewbie.listener.world.RainRefillsWaterAndMakesPuddles;
import me.sosedik.utilizer.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public record ThirstData(
	int thirst,
	float saturation,
	double thirstChance,
	@Nullable ThirstSource thirstSource,
	@Nullable DrinkType drinkType,
	boolean cooled
) {

	public static final double HIGH_THIRST_CHANCE = 0.85;
	public static final double LOW_THIRST_CHANCE = 0.3;

	private static final Map<Material, Integer> THIRST_VALUES = new HashMap<>();
	private static final Map<Material, Float> SATURATION_VALUES = new HashMap<>();
	private static final String THIRST_CHANCE_TAG = "thirst_chance";
	private static final String COOLED_TAG = "cooled";
	private static final String THIRST_SOURCE_TAG = "thirst_source";
	private static final String DRINK_TYPE_TAG = "drink_type";

	public boolean isDummy() {
		return this.thirst == 0;
	}

	public ThirstData withCooled() {
		return new ThirstData(this.thirst, this.saturation, this.thirstChance, this.thirstSource, this.drinkType, true);
	}

	public ThirstData withThirstChance(double thirstChance) {
		return new ThirstData(this.thirst, this.saturation, thirstChance, this.thirstSource, this.drinkType, this.cooled);
	}

	public ItemStack saveInto(ItemStack item) {
		ItemStack result = item.clone();
		Material type = result.getType();
		NBT.modify(result, nbt -> {
			if (type != TrappedNewbieItems.DRAGON_FLASK && isWateryItem(result, type)) nbt.setDouble(THIRST_CHANCE_TAG, this.thirstChance);
			if (this.cooled) nbt.setBoolean(COOLED_TAG, true);
			else nbt.removeKey(COOLED_TAG);
			if (this.thirstSource != null) nbt.setEnum(THIRST_SOURCE_TAG, this.thirstSource);
			else nbt.removeKey(THIRST_SOURCE_TAG);
			if (this.drinkType != null) nbt.setEnum(DRINK_TYPE_TAG, this.drinkType);
			else nbt.removeKey(DRINK_TYPE_TAG);
		});
		return result;
	}

	public static ThirstData of(ItemStack item) {
		return of (item, item.getType());
	}

	public static ThirstData of(ItemStack item, Material type) {
		int thirst;
		var drinkType = DrinkType.fromItem(item, type);
		boolean dehydrating = TrappedNewbieTags.DEHYDRATING_FOOD.isTagged(type);
		double thirstChanceBackup = dehydrating ? 1D : 0D;
		float saturation = SATURATION_VALUES.getOrDefault(type, 0F);
		if (THIRST_VALUES.containsKey(type)) {
			thirst = THIRST_VALUES.get(type);
		} else {
			if (type == Material.POTION || drinkType == DrinkType.WATER) {
				if (isWateryItem(item, type)) {
					thirst = 3;
					thirstChanceBackup = HIGH_THIRST_CHANCE;
					saturation = 0F;
				} else {
					thirst = 6;
					saturation = 1.5F;
				}
			} else if (drinkType == DrinkType.MILK) {
				thirst = 3;
			} else if (drinkType == DrinkType.CACTUS_JUICE) {
				thirst = ThirstyPlayer.MAX_THIRST;
			} else if (TrappedNewbieTags.CANTEENS.isTagged(type)) { // todo
				thirst = 4;
			} else {
				thirst = 0;
			}
		}
		float finalSaturation = saturation;
		double finalThirstChanceBackup = thirstChanceBackup;
		return NBT.get(item, nbt -> {
			double thirstChance = nbt.getOrDefault(THIRST_CHANCE_TAG, finalThirstChanceBackup);
			boolean cooled = nbt.getOrDefault(COOLED_TAG, false);
			return new ThirstData(thirst, finalSaturation, thirstChance, dehydrating ? ThirstSource.DEHYDRATING_FOOD : null, drinkType, cooled);
		});
	}

	private static boolean isWateryItem(ItemStack item, Material type) { // todo canteens
		PotionContents potionContents = item.getData(DataComponentTypes.POTION_CONTENTS);
		if (potionContents == null) return FillingBowlWithWater.REVERSED_BOWLS.get(type) != null;

		PotionType potion = potionContents.potion();
		return potion == PotionType.WATER
			|| potion == PotionType.MUNDANE
			|| potion == PotionType.THICK
			|| potion == PotionType.AWKWARD;
	}

	public static boolean isCooled(ItemStack item) {
		return NBT.get(item, nbt -> (boolean) nbt.getOrDefault(COOLED_TAG, false));
	}

	public static boolean isPure(ItemStack item) {
		if (item.getType() == TrappedNewbieItems.DRAGON_FLASK) return true;
		if (!isWateryItem(item, item.getType())) return true;
		return NBT.get(item, nbt -> (double) nbt.getOrDefault(THIRST_CHANCE_TAG, HIGH_THIRST_CHANCE)) == 0D;
	}

	public static ThirstData of(Block block) {
		boolean cooled = block.getTemperature() < 0.15;
		if (RainRefillsWaterAndMakesPuddles.isWaterPuddle(block))
			return new ThirstData(4, 0F, 0, null, DrinkType.WATER, cooled);
		return new ThirstData(getThirstValue(block), 0F, getThirstChance(block), ThirstSource.DIRTY_WATER, DrinkType.WATER, cooled);
	}

	private static double getThirstChance(Block block) {
		if (block.getType() != Material.WATER) return 0.9;
		// ToDo: custom cauldron
		return switch (block.getBiome()) {
			case Biome b when b == Biome.OCEAN
					|| b == Biome.DEEP_OCEAN
					|| b == Biome.WARM_OCEAN
					|| b == Biome.LUKEWARM_OCEAN
					|| b == Biome.DEEP_LUKEWARM_OCEAN
					|| b == Biome.SWAMP -> 1;
			case Biome b when b == Biome.DESERT -> 0.95;
			case Biome b when b == Biome.BEACH
					|| b == Biome.WINDSWEPT_SAVANNA -> 0.9;
			case Biome b when b == Biome.COLD_OCEAN
					|| b == Biome.DEEP_COLD_OCEAN
					|| b == Biome.SNOWY_BEACH -> 0.85;
			case Biome b when b == Biome.FROZEN_OCEAN
					|| b == Biome.DEEP_FROZEN_OCEAN
					|| b == Biome.SNOWY_TAIGA -> 0.8;
			case Biome b when b == Biome.SNOWY_PLAINS
					|| b == Biome.SNOWY_SLOPES -> 0.7;
			case Biome b when b == Biome.ICE_SPIKES
					|| b == Biome.FROZEN_PEAKS -> 0.65;
			case Biome b when b == Biome.RIVER
					|| b == Biome.FROZEN_RIVER -> 0.2;
			default -> 0.75;
		};
	}

	private static int getThirstValue(Block block) {
		if (block.getType() != Material.WATER) return 2;
		// ToDo: custom cauldron
		return switch (block.getBiome()) {
			case Biome b when b == Biome.OCEAN
					|| b == Biome.DEEP_OCEAN
					|| b == Biome.WARM_OCEAN
					|| b == Biome.LUKEWARM_OCEAN
					|| b == Biome.DEEP_LUKEWARM_OCEAN
					|| b == Biome.SWAMP -> 1;
			case Biome b when b == Biome.FROZEN_OCEAN
					|| b == Biome.DEEP_FROZEN_OCEAN
					|| b == Biome.SNOWY_TAIGA -> 4;
			case Biome b when b == Biome.COLD_OCEAN
					|| b == Biome.DEEP_COLD_OCEAN
					|| b == Biome.SNOWY_BEACH -> 5;
			case Biome b when b == Biome.DESERT
					|| b == Biome.SAVANNA
					|| b == Biome.SAVANNA_PLATEAU
					|| b == Biome.WINDSWEPT_SAVANNA -> 2;
			default -> 3;
		};
	}

	static {
		addThirst(Material.MILK_BUCKET, 5, 1F);
		addThirst(Material.SUSPICIOUS_STEW, 3, 0.5F);
		addThirst(Material.BEETROOT_SOUP, 2, 0.5F);
		addThirst(Material.MUSHROOM_STEW, 2, 0.5F);
		addThirst(Material.RABBIT_STEW, 2, 0.5F);
		addThirst(Material.MELON_SLICE, 2, 1F);
		addThirst(Material.SPIDER_EYE, -4, 0F);
		addThirst(Material.ROTTEN_FLESH, -4, 0F);
		addThirst(Material.SWEET_BERRIES, 1, 0.1F);
		addThirst(Material.GLOW_BERRIES, 1, 0.1F);
		addThirst(Material.APPLE, 1, 0.1F);
		addThirst(DelightfulFarmingItems.BROKEN_EGG, 1, 0.1F);
		addThirst(DelightfulFarmingItems.BROKEN_BROWN_EGG, 1, 0.1F);
		addThirst(DelightfulFarmingItems.BROKEN_BLUE_EGG, 1, 0.1F);
		addThirst(DelightfulFarmingItems.BROKEN_TURTLE_EGG, 1, 0.1F);
		addThirst(DelightfulFarmingItems.CACTUS_FLESH, 3, 1F);
	}

	private static void addThirst(Material type, int thirst, float saturation) {
		THIRST_VALUES.put(type, thirst);
		SATURATION_VALUES.put(type, saturation);
	}

	public enum DrinkType {

		DRINK, WATER, LAVA, MILK, CACTUS_JUICE;

		public static @Nullable DrinkType fromItem(ItemStack item) {
			return fromItem(item, item.getType());
		}

		public static @Nullable DrinkType fromItem(ItemStack item, Material type) {
			if (type == Material.MILK_BUCKET) return MILK;

			DrinkType drinkType = NBT.get(item, nbt -> {
				return nbt.getOrNull(DRINK_TYPE_TAG, DrinkType.class);
			});
			if (drinkType != null) return drinkType;
			if (type == Material.POTION) return ItemUtil.isWaterPotion(item) ? WATER : DRINK;
			if (FillingBowlWithWater.REVERSED_BOWLS.get(type) != null) return WATER; // todo canteens

			return DelightfulFarmingTags.DRINKS.isTagged(type) ? DRINK : null;
		}

	}

	public enum ThirstSource {

		DIRTY_WATER(new PotionEffect(TrappedNewbieEffects.THIRST, 0, 300)),
		DEHYDRATING_FOOD(new PotionEffect(TrappedNewbieEffects.THIRST, 0, 600));

		private final PotionEffect effect;

		ThirstSource(PotionEffect effect) {
			this.effect = effect;
		}

		public PotionEffect getEffect() {
			return this.effect;
		}

	}

}
