package me.sosedik.miscme;

import me.sosedik.miscme.impl.item.modifier.BookAuthorOnlineModifier;
import me.sosedik.miscme.impl.item.modifier.ColoredShulkerShellModifier;
import me.sosedik.miscme.impl.item.modifier.FancierDyedLoreModifier;
import me.sosedik.miscme.impl.item.modifier.FancierTrimLoreModifier;
import me.sosedik.miscme.impl.item.modifier.SignsShowTextInLoreModifier;
import me.sosedik.miscme.listener.block.BlockKnocking;
import me.sosedik.miscme.listener.block.CampfireSetsOnFire;
import me.sosedik.miscme.listener.block.ChestThrowsEntities;
import me.sosedik.miscme.listener.block.ClickThroughHanging;
import me.sosedik.miscme.listener.block.ClickThroughSigns;
import me.sosedik.miscme.listener.block.ContainersInLiquidsReleaseContents;
import me.sosedik.miscme.listener.block.Couplings;
import me.sosedik.miscme.listener.block.DontEditSignsOnPlace;
import me.sosedik.miscme.listener.block.DoorBells;
import me.sosedik.miscme.listener.block.ExplosiveCoal;
import me.sosedik.miscme.listener.block.MelonPumpkinBlowing;
import me.sosedik.miscme.listener.block.NoIceInNether;
import me.sosedik.miscme.listener.block.NoteBlockShowsNotes;
import me.sosedik.miscme.listener.block.SignsRetain;
import me.sosedik.miscme.listener.entity.ArmorStandBedrockPoses;
import me.sosedik.miscme.listener.entity.ArmorStandSpawnsWithArms;
import me.sosedik.miscme.listener.entity.BurningSpreadsWhenAttacking;
import me.sosedik.miscme.listener.entity.ColoredShulkerUponSpawn;
import me.sosedik.miscme.listener.entity.DyeableShulkers;
import me.sosedik.miscme.listener.entity.DynamicCreeperExplosion;
import me.sosedik.miscme.listener.entity.EndermanTeleportsPlayers;
import me.sosedik.miscme.listener.entity.ItemFrameReverseRotate;
import me.sosedik.miscme.listener.entity.MinecartSlimeBoost;
import me.sosedik.miscme.listener.entity.MoreBabyMobs;
import me.sosedik.miscme.listener.entity.MovingMinecartsHurtEntities;
import me.sosedik.miscme.listener.entity.PrimingExplosiveMinecart;
import me.sosedik.miscme.listener.entity.RainbowSheepDropRandomWool;
import me.sosedik.miscme.listener.entity.SheepBurnableWool;
import me.sosedik.miscme.listener.entity.SheepRegrowNaturalWool;
import me.sosedik.miscme.listener.entity.TNTDefuse;
import me.sosedik.miscme.listener.entity.WololoChangesNaturalSheepWool;
import me.sosedik.miscme.listener.item.BottledAir;
import me.sosedik.miscme.listener.item.ColoredShulkerShells;
import me.sosedik.miscme.listener.item.DyeableItemsInItemFrames;
import me.sosedik.miscme.listener.item.EasierShovelPathCreation;
import me.sosedik.miscme.listener.item.FireAspectIsFlintAndSteel;
import me.sosedik.miscme.listener.item.FlintAndSteelIgnitesEntities;
import me.sosedik.miscme.listener.item.ImmersiveDyes;
import me.sosedik.miscme.listener.item.ReadableBooksInFrames;
import me.sosedik.miscme.listener.item.ShovelsConvertAdditionalBlocks;
import me.sosedik.miscme.listener.misc.BetterTimeSetCommand;
import me.sosedik.miscme.listener.misc.WaterAwarePotionReset;
import me.sosedik.miscme.listener.player.BurningForcesToRun;
import me.sosedik.miscme.listener.player.HidePlayerNameTags;
import me.sosedik.miscme.listener.player.JumpingOverFences;
import me.sosedik.miscme.listener.player.SwapHalfStack;
import me.sosedik.miscme.listener.projectile.BurningLitsProjectiles;
import me.sosedik.miscme.listener.projectile.BurningProjectileCreatesFire;
import me.sosedik.miscme.listener.vehicle.JumpyBoats;
import me.sosedik.miscme.listener.world.CustomDayCycleCleanup;
import me.sosedik.utilizer.api.language.TranslationHolder;
import me.sosedik.utilizer.util.EventUtil;
import me.sosedik.utilizer.util.Scheduler;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class MiscMe extends JavaPlugin {

	public static final String NAMESPACE = "miscme";

	private static @UnknownNullability MiscMe instance;

	private @UnknownNullability Scheduler scheduler;

	@Override
	public void onLoad() {
		MiscMe.instance = this;
		this.scheduler = new Scheduler(this);

		TranslationHolder.extractLocales(this);
	}

	@Override
	public void onEnable() {
		new BookAuthorOnlineModifier(miscmeKey("book_author_online")).register();
		new ColoredShulkerShellModifier(miscmeKey("colored_shulker_shell")).register();
		new FancierDyedLoreModifier(miscmeKey("fancier_dyed_lore")).register();
		new FancierTrimLoreModifier(miscmeKey("fancier_trim_lore")).register();
		new SignsShowTextInLoreModifier(miscmeKey("signs_show_text_in_lore")).register();

		EventUtil.registerListeners(this,
			// block
			BlockKnocking.class,
			CampfireSetsOnFire.class,
			ChestThrowsEntities.class,
			ClickThroughHanging.class,
			ClickThroughSigns.class,
			ContainersInLiquidsReleaseContents.class,
			Couplings.class,
			DontEditSignsOnPlace.class,
			DoorBells.class,
			ExplosiveCoal.class,
			MelonPumpkinBlowing.class,
			NoIceInNether.class,
			NoteBlockShowsNotes.class,
			SignsRetain.class,
			// entity
			ArmorStandBedrockPoses.class,
			ArmorStandSpawnsWithArms.class,
			BurningSpreadsWhenAttacking.class,
			ColoredShulkerUponSpawn.class,
			DyeableShulkers.class,
			DynamicCreeperExplosion.class,
			EndermanTeleportsPlayers.class,
			ItemFrameReverseRotate.class,
			MinecartSlimeBoost.class,
			MoreBabyMobs.class,
			MovingMinecartsHurtEntities.class,
			PrimingExplosiveMinecart.class,
			RainbowSheepDropRandomWool.class,
			SheepBurnableWool.class,
			SheepRegrowNaturalWool.class,
			TNTDefuse.class,
			WololoChangesNaturalSheepWool.class,
			// item
			BottledAir.class,
			ColoredShulkerShells.class,
			DyeableItemsInItemFrames.class,
			EasierShovelPathCreation.class,
			FireAspectIsFlintAndSteel.class,
			FlintAndSteelIgnitesEntities.class,
			ImmersiveDyes.class,
			ReadableBooksInFrames.class,
			ShovelsConvertAdditionalBlocks.class,
			// misc
			BetterTimeSetCommand.class,
			WaterAwarePotionReset.class,
			// player
			BurningForcesToRun.class,
			HidePlayerNameTags.class,
			JumpingOverFences.class,
			SwapHalfStack.class,
			// projectile
			BurningLitsProjectiles.class,
			BurningProjectileCreatesFire.class,
			// vehicle
			JumpyBoats.class,
			// world
			CustomDayCycleCleanup.class
		);
	}

	/**
	 * Gets the plugin instance
	 *
	 * @return the plugin instance
	 */
	public static MiscMe instance() {
		return MiscMe.instance;
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
	public static NamespacedKey miscmeKey(String value) {
		return new NamespacedKey(NAMESPACE, value);
	}

}
