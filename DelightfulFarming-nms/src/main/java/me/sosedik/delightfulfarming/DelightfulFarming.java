package me.sosedik.delightfulfarming;

import me.sosedik.delightfulfarming.dataset.DelightfulFarmingItems;
import me.sosedik.delightfulfarming.dataset.DelightfulFarmingRecipes;
import me.sosedik.delightfulfarming.impl.item.modifier.BerriesModifier;
import me.sosedik.delightfulfarming.impl.item.modifier.ClockMealModifier;
import me.sosedik.delightfulfarming.listener.block.NoBerriesPlacement;
import me.sosedik.delightfulfarming.listener.sugar.AlwaysAllowEating;
import me.sosedik.delightfulfarming.listener.sugar.CaloriesExhaustion;
import me.sosedik.delightfulfarming.listener.sugar.CaloriesOnFoodConsume;
import me.sosedik.delightfulfarming.listener.sugar.CaloriesOnJoinLeave;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.utilizer.api.language.TranslationHolder;
import me.sosedik.utilizer.impl.storage.SimpleRotatableBlockStorage;
import me.sosedik.utilizer.listener.BlockStorage;
import me.sosedik.utilizer.listener.item.PlaceableBlockItems;
import me.sosedik.utilizer.util.EventUtil;
import me.sosedik.utilizer.util.Scheduler;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class DelightfulFarming extends JavaPlugin {

	private static @UnknownNullability DelightfulFarming instance;

	private @UnknownNullability Scheduler scheduler;

	@Override
	public void onLoad() {
		DelightfulFarming.instance = this;
		this.scheduler = new Scheduler(this);

		TranslationHolder.extractLocales(this);
		ResourceLib.loadDefaultResources(this);

		BlockStorage.addMapping(DelightfulFarmingItems.SWEET_BERRY_BASKET, SimpleRotatableBlockStorage.class);
		BlockStorage.addMapping(DelightfulFarmingItems.GLOW_BERRY_BASKET, SimpleRotatableBlockStorage.class);
	}

	@Override
	public void onEnable() {
		DelightfulFarmingRecipes.addRecipes();

		EventUtil.registerListeners(this,
			// block
			NoBerriesPlacement.class,
			// sugar
			AlwaysAllowEating.class,
			CaloriesExhaustion.class,
			CaloriesOnFoodConsume.class,
			CaloriesOnJoinLeave.class
		);

		PlaceableBlockItems.addMapping(DelightfulFarmingItems.SWEET_BERRY_PIPS, DelightfulFarmingItems.SWEET_BERRY_PIPS_BUSH);
		PlaceableBlockItems.addMapping(DelightfulFarmingItems.GLOW_BERRY_PIPS, DelightfulFarmingItems.GLOW_BERRY_PIPS_BUSH);

		new BerriesModifier(delightfulFarmingKey("berries_modifier")).register();
		new ClockMealModifier(delightfulFarmingKey("clock_meal")).register();
	}

	/**
	 * Gets the plugin instance
	 *
	 * @return the plugin instance
	 */
	public static DelightfulFarming instance() {
		return DelightfulFarming.instance;
	}

	/**
	 * Gets the plugin's task scheduler
	 *
	 * @return the plugin's task scheduler
	 */
	public static Scheduler scheduler() {
		return instance().scheduler;
	}

	/**
	 * Gets the plugin's component logger
	 *
	 * @return the plugin's component logger
	 */
	public static ComponentLogger logger() {
		return instance().getComponentLogger();
	}

	/**
	 * Makes a namespaced key with this plugin's namespace
	 *
	 * @param value value
	 * @return namespaced key
	 */
	public static NamespacedKey delightfulFarmingKey(String value) {
		return new NamespacedKey("delightful_farming", value);
	}

}
