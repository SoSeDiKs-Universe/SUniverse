package me.sosedik.requiem;

import me.sosedik.requiem.command.ReviveCommand;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.requiem.listener.entity.CreepersDropCreeperHearts;
import me.sosedik.requiem.listener.entity.FakeHorseSaddles;
import me.sosedik.requiem.listener.entity.OverwriteControlledPandasGenes;
import me.sosedik.requiem.listener.entity.PrepareGhostMobs;
import me.sosedik.requiem.listener.item.ExplodingCreeperHeart;
import me.sosedik.requiem.listener.player.LoadSavePlayers;
import me.sosedik.requiem.listener.player.damage.DamageFeetOnFall;
import me.sosedik.requiem.listener.player.damage.DamageModelLoadSave;
import me.sosedik.requiem.listener.player.ghost.DeathMakesGhosts;
import me.sosedik.requiem.listener.player.ghost.GhostsDontSprint;
import me.sosedik.requiem.listener.player.ghost.GhostsDontStarveOrChoke;
import me.sosedik.requiem.listener.player.ghost.GhostsKeepNightVision;
import me.sosedik.requiem.listener.player.ghost.GhostsPhaseThroughWalls;
import me.sosedik.requiem.listener.player.ghost.MobsDontTargetGhosts;
import me.sosedik.requiem.listener.player.ghost.NoDamageToOrFromGhosts;
import me.sosedik.requiem.listener.player.ghost.NoGhostInteractions;
import me.sosedik.requiem.listener.player.possessed.DeathMakesPossessed;
import me.sosedik.requiem.listener.player.possessed.MilkHelpsSkeletons;
import me.sosedik.requiem.listener.player.possessed.NoAirForWaterPossessing;
import me.sosedik.requiem.listener.player.possessed.NoExpFromPossessedKill;
import me.sosedik.requiem.listener.player.possessed.PossessedDismount;
import me.sosedik.requiem.listener.player.possessed.PossessedInfiniteProjectiles;
import me.sosedik.requiem.listener.player.possessed.PossessedLimitedControl;
import me.sosedik.requiem.listener.player.possessed.PossessedMimikPossessor;
import me.sosedik.requiem.listener.player.possessed.PossessingOverMobs;
import me.sosedik.requiem.listener.player.possessed.PossessorMimiksPossessed;
import me.sosedik.requiem.listener.player.possessed.TransformationsKeepPossessor;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.utilizer.CommandManager;
import me.sosedik.utilizer.api.language.TranslationHolder;
import me.sosedik.utilizer.util.EventUtil;
import me.sosedik.utilizer.util.Scheduler;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class Requiem extends JavaPlugin {

	private static Requiem instance;

	private Scheduler scheduler;

	@Override
	public void onLoad() {
		Requiem.instance = this;
		this.scheduler = new Scheduler(this);

		TranslationHolder.extractLocales(this);
		ResourceLib.loadDefaultResources(this);
	}

	@Override
	public void onEnable() {
		registerCommands();
		EventUtil.registerListeners(this,
			// entity
			CreepersDropCreeperHearts.class,
			FakeHorseSaddles.class,
			OverwriteControlledPandasGenes.class,
			PrepareGhostMobs.class,
			// item
			ExplodingCreeperHeart.class,
			// player
			LoadSavePlayers.class,
			/// damage
			DamageFeetOnFall.class,
			DamageModelLoadSave.class,
			/// ghost
			DeathMakesGhosts.class,
			GhostsDontSprint.class,
			GhostsDontStarveOrChoke.class,
			GhostsKeepNightVision.class,
			GhostsPhaseThroughWalls.class,
			MobsDontTargetGhosts.class,
			NoDamageToOrFromGhosts.class,
			NoGhostInteractions.class,
			/// possessed
			MilkHelpsSkeletons.class,
			DeathMakesPossessed.class,
			NoAirForWaterPossessing.class,
			NoExpFromPossessedKill.class,
			PossessedDismount.class,
			PossessedInfiniteProjectiles.class,
			PossessedLimitedControl.class,
			PossessedMimikPossessor.class,
			PossessingOverMobs.class,
			PossessorMimiksPossessed.class,
			TransformationsKeepPossessor.class
		);
	}

	private void registerCommands() {
		CommandManager.commandManager().registerCommands(this,
			ReviveCommand.class
		);
	}

	@Override
	public void onDisable() {
		GhostyPlayer.saveAllData();
		PossessingPlayer.saveAllData();
		PrepareGhostMobs.unregisterGhosts();
		DamageModelLoadSave.saveAll();
	}

	/**
	 * Gets the plugin instance
	 *
	 * @return the plugin instance
	 */
	public static @NotNull Requiem instance() {
		return Requiem.instance;
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
	public static @NotNull NamespacedKey requiemKey(@NotNull String value) {
		return new NamespacedKey("requiem", value);
	}

}
