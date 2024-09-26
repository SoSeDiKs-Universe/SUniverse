package me.sosedik.miscme;

import me.sosedik.miscme.impl.item.modifier.SignsShowTextInLore;
import me.sosedik.miscme.listener.block.BlockKnocking;
import me.sosedik.miscme.listener.block.CampfireSetsOnFire;
import me.sosedik.miscme.listener.block.DontEditSignsOnPlace;
import me.sosedik.miscme.listener.block.DoorBells;
import me.sosedik.miscme.listener.block.SignsRetain;
import me.sosedik.miscme.listener.item.BottledAir;
import me.sosedik.miscme.listener.misc.BetterTimeSetCommand;
import me.sosedik.miscme.listener.misc.WaterAwarePotionReset;
import me.sosedik.miscme.listener.player.HidePlayerNameTags;
import me.sosedik.miscme.listener.world.CustomDayCycleCleanup;
import me.sosedik.utilizer.util.EventUtil;
import me.sosedik.utilizer.util.Scheduler;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class MiscMe extends JavaPlugin {

	private static MiscMe instance;

	private Scheduler scheduler;

	@Override
	public void onLoad() {
		MiscMe.instance = this;
		this.scheduler = new Scheduler(this);
	}

	@Override
	public void onEnable() {
		new SignsShowTextInLore(miscmeKey("signs_show_text_in_lore")).register();

		EventUtil.registerListeners(this,
			// block
			BlockKnocking.class,
			CampfireSetsOnFire.class,
			DontEditSignsOnPlace.class,
			DoorBells.class,
			SignsRetain.class,
			// item
			BottledAir.class,
			// misc
			BetterTimeSetCommand.class,
			WaterAwarePotionReset.class,
			// player
			HidePlayerNameTags.class,
			// world
			CustomDayCycleCleanup.class
		);
	}

	/**
	 * Gets the plugin instance
	 *
	 * @return the plugin instance
	 */
	public static @NotNull MiscMe instance() {
		return MiscMe.instance;
	}

	/**
	 * Gets the plugin's task scheduler
	 *
	 * @return the plugin's task scheduler
	 */
	public static @NotNull Scheduler scheduler() {
		return instance().scheduler;
	}

	/**
	 * Gets the plugin's component logger
	 *
	 * @return the plugin's component logger
	 */
	public static @NotNull ComponentLogger logger() {
		return instance().getComponentLogger();
	}

	/**
	 * Makes a namespaced key with this plugin's namespace
	 *
	 * @param value value
	 * @return namespaced key
	 */
	public static @NotNull NamespacedKey miscmeKey(@NotNull String value) {
		return new NamespacedKey("miscme", value);
	}

}
