package me.sosedik.miscme;

import me.sosedik.miscme.dataset.MiscMeRecipes;
import me.sosedik.miscme.impl.item.modifier.ArmorTooltipModifier;
import me.sosedik.miscme.impl.item.modifier.BarometerModifier;
import me.sosedik.miscme.impl.item.modifier.BookAuthorOnlineModifier;
import me.sosedik.miscme.impl.item.modifier.ClockModifier;
import me.sosedik.miscme.impl.item.modifier.ColoredShulkerShellModifier;
import me.sosedik.miscme.impl.item.modifier.CompassModifier;
import me.sosedik.miscme.impl.item.modifier.DepthMeterModifier;
import me.sosedik.miscme.impl.item.modifier.DurabilityTooltipModifier;
import me.sosedik.miscme.impl.item.modifier.EnchantmentTooltipModifier;
import me.sosedik.miscme.impl.item.modifier.FancierDyedLoreModifier;
import me.sosedik.miscme.impl.item.modifier.FancierTrimLoreModifier;
import me.sosedik.miscme.impl.item.modifier.FancyTooltipModifier;
import me.sosedik.miscme.impl.item.modifier.LunarClockModifier;
import me.sosedik.miscme.impl.item.modifier.LuxmeterModifier;
import me.sosedik.miscme.impl.item.modifier.RepairableTooltipModifier;
import me.sosedik.miscme.impl.item.modifier.SignsShowTextInLoreModifier;
import me.sosedik.miscme.impl.item.modifier.SpeedometerModifier;
import me.sosedik.miscme.impl.item.modifier.ToolTooltipModifier;
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
import me.sosedik.miscme.listener.block.ExtractFurnaceExperience;
import me.sosedik.miscme.listener.block.FallenCampfireSpreadsFire;
import me.sosedik.miscme.listener.block.FallingBeeNests;
import me.sosedik.miscme.listener.block.MelonPumpkinBlowing;
import me.sosedik.miscme.listener.block.NoIceInNether;
import me.sosedik.miscme.listener.block.NoteBlockShowsNotes;
import me.sosedik.miscme.listener.block.PickableSnow;
import me.sosedik.miscme.listener.block.SignsRetain;
import me.sosedik.miscme.listener.entity.AnimalAgeLocking;
import me.sosedik.miscme.listener.entity.ArmorStandBedrockPoses;
import me.sosedik.miscme.listener.entity.ArmorStandSpawnsWithArms;
import me.sosedik.miscme.listener.entity.BetterEntitySuffocation;
import me.sosedik.miscme.listener.entity.BurningSpreadsWhenAttacking;
import me.sosedik.miscme.listener.entity.ChickenEggsHatch;
import me.sosedik.miscme.listener.entity.ChickensBreedEggs;
import me.sosedik.miscme.listener.entity.ColoredShulkerUponSpawn;
import me.sosedik.miscme.listener.entity.DeadLivingsIgnoreCactiAndSweetBushes;
import me.sosedik.miscme.listener.entity.DyeableShulkers;
import me.sosedik.miscme.listener.entity.DynamicCreeperExplosion;
import me.sosedik.miscme.listener.entity.DynamicMovingSpeed;
import me.sosedik.miscme.listener.entity.DynamicVisibilityRange;
import me.sosedik.miscme.listener.entity.EndermanTeleportsPlayers;
import me.sosedik.miscme.listener.entity.ExtraPigsOnBreeding;
import me.sosedik.miscme.listener.entity.FriendlyFireTameable;
import me.sosedik.miscme.listener.entity.ItemFrameReverseRotate;
import me.sosedik.miscme.listener.entity.ItemFrameSpillables;
import me.sosedik.miscme.listener.entity.MinecartSlimeBoost;
import me.sosedik.miscme.listener.entity.MobPatting;
import me.sosedik.miscme.listener.entity.MoreBabyMobs;
import me.sosedik.miscme.listener.entity.MovingMinecartsHurtEntities;
import me.sosedik.miscme.listener.entity.PrimingExplosiveMinecart;
import me.sosedik.miscme.listener.entity.RainbowSheepDropRandomWool;
import me.sosedik.miscme.listener.entity.ReleaseEntityFromVehicle;
import me.sosedik.miscme.listener.entity.ShearableEntities;
import me.sosedik.miscme.listener.entity.SheepBurnableWool;
import me.sosedik.miscme.listener.entity.SheepRegrowNaturalWool;
import me.sosedik.miscme.listener.entity.SnowmanPumpkin;
import me.sosedik.miscme.listener.entity.TNTDefuse;
import me.sosedik.miscme.listener.entity.WololoChangesNaturalSheepWool;
import me.sosedik.miscme.listener.fixup.IgnitableCobweb;
import me.sosedik.miscme.listener.item.BottledAir;
import me.sosedik.miscme.listener.item.ColoredShulkerShells;
import me.sosedik.miscme.listener.item.DyeableItemsInItemFrames;
import me.sosedik.miscme.listener.item.EasierBlockFlattening;
import me.sosedik.miscme.listener.item.FireAspectIsFlintAndSteel;
import me.sosedik.miscme.listener.item.FireHitLitsEntities;
import me.sosedik.miscme.listener.item.FlintAndSteelIgnitesEntities;
import me.sosedik.miscme.listener.item.ImmersiveDyes;
import me.sosedik.miscme.listener.item.InventoryRefresher;
import me.sosedik.miscme.listener.item.ItemRightClickMessages;
import me.sosedik.miscme.listener.item.NoSwordInstaBreak;
import me.sosedik.miscme.listener.item.ReadableBooksInFrames;
import me.sosedik.miscme.listener.item.ShovelsConvertAdditionalBlocks;
import me.sosedik.miscme.listener.item.ShovelsRemoveSnow;
import me.sosedik.miscme.listener.item.SwordsSwingThroughGrass;
import me.sosedik.miscme.listener.item.TorchesBurnCobwebs;
import me.sosedik.miscme.listener.misc.BetterTimeSetCommand;
import me.sosedik.miscme.listener.misc.WaterAwareBottleReset;
import me.sosedik.miscme.listener.player.BurningForcesToRun;
import me.sosedik.miscme.listener.player.FireExtinguishByHand;
import me.sosedik.miscme.listener.player.HidePlayerNameTags;
import me.sosedik.miscme.listener.player.JumpingOverFences;
import me.sosedik.miscme.listener.player.PlayerSpeedTracker;
import me.sosedik.miscme.listener.player.SwapHalfStack;
import me.sosedik.miscme.listener.projectile.ArrowsBreakFragileBlocks;
import me.sosedik.miscme.listener.projectile.BurningLitsProjectiles;
import me.sosedik.miscme.listener.projectile.BurningProjectileCreatesFire;
import me.sosedik.miscme.listener.projectile.SnowballCreatesSnow;
import me.sosedik.miscme.listener.projectile.SnowballFreezesEntities;
import me.sosedik.miscme.listener.projectile.WaterPotionSplashesTorches;
import me.sosedik.miscme.listener.vehicle.JumpyBoats;
import me.sosedik.miscme.listener.world.CustomDayCycleCleanup;
import me.sosedik.miscme.listener.world.TrailPaths;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.utilizer.api.language.TranslationHolder;
import me.sosedik.utilizer.util.EventUtil;
import me.sosedik.utilizer.util.Scheduler;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
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
		ResourceLib.loadDefaultResources(this);
	}

	@Override
	public void onEnable() {
		new ArmorTooltipModifier(miscMeKey("armor_tooltip")).register();
		new BarometerModifier(miscMeKey("barometer")).register();
		new BookAuthorOnlineModifier(miscMeKey("book_author_online")).register();
		new ClockModifier(miscMeKey("clock")).register();
		new ColoredShulkerShellModifier(miscMeKey("colored_shulker_shell")).register();
		new CompassModifier(miscMeKey("compass")).register();
		new DepthMeterModifier(miscMeKey("depth_meter")).register();
		new DurabilityTooltipModifier(miscMeKey("durability_tooltip")).register();
		new EnchantmentTooltipModifier(miscMeKey("enchantment_tooltip")).register();
		new FancierDyedLoreModifier(miscMeKey("fancier_dyed_lore")).register();
		new FancierTrimLoreModifier(miscMeKey("fancier_trim_lore")).register();
		new FancyTooltipModifier(miscMeKey("fancy_tooltip")).register();
		new LunarClockModifier(miscMeKey("lunar_clock")).register();
		new LuxmeterModifier(miscMeKey("luxmeter")).register();
		new RepairableTooltipModifier(miscMeKey("repairable_tooltip")).register();
		new SignsShowTextInLoreModifier(miscMeKey("signs_show_text_in_lore")).register();
		new SpeedometerModifier(miscMeKey("speedometer")).register();
		new ToolTooltipModifier(miscMeKey("tool_tooltip")).register();

		MiscMeRecipes.addRecipes();

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
			ExtractFurnaceExperience.class,
			FallenCampfireSpreadsFire.class,
			FallingBeeNests.class,
			MelonPumpkinBlowing.class,
			NoIceInNether.class,
			NoteBlockShowsNotes.class,
			PickableSnow.class,
			SignsRetain.class,
			// entity
			AnimalAgeLocking.class,
			ArmorStandBedrockPoses.class,
			ArmorStandSpawnsWithArms.class,
			BetterEntitySuffocation.class,
			BurningSpreadsWhenAttacking.class,
			ChickenEggsHatch.class,
			ChickensBreedEggs.class,
			ColoredShulkerUponSpawn.class,
			DeadLivingsIgnoreCactiAndSweetBushes.class,
			DyeableShulkers.class,
			DynamicCreeperExplosion.class,
			DynamicMovingSpeed.class,
			DynamicVisibilityRange.class,
			EndermanTeleportsPlayers.class,
			ExtraPigsOnBreeding.class,
			FriendlyFireTameable.class,
			ItemFrameSpillables.class,
			ItemFrameReverseRotate.class,
			MinecartSlimeBoost.class,
			MobPatting.class,
			MoreBabyMobs.class,
			MovingMinecartsHurtEntities.class,
			PrimingExplosiveMinecart.class,
			RainbowSheepDropRandomWool.class,
			ReleaseEntityFromVehicle.class,
			ShearableEntities.class,
			SheepBurnableWool.class,
			SheepRegrowNaturalWool.class,
			SnowmanPumpkin.class,
			TNTDefuse.class,
			WololoChangesNaturalSheepWool.class,
			// fixup
			IgnitableCobweb.class,
			// item
			BottledAir.class,
			ColoredShulkerShells.class,
			DyeableItemsInItemFrames.class,
			EasierBlockFlattening.class,
			FireAspectIsFlintAndSteel.class,
			FireHitLitsEntities.class,
			FlintAndSteelIgnitesEntities.class,
			ImmersiveDyes.class,
			InventoryRefresher.class,
			ItemRightClickMessages.class,
			NoSwordInstaBreak.class,
			ReadableBooksInFrames.class,
			ShovelsConvertAdditionalBlocks.class,
			ShovelsRemoveSnow.class,
			SwordsSwingThroughGrass.class,
			TorchesBurnCobwebs.class,
			// misc
			BetterTimeSetCommand.class,
			WaterAwareBottleReset.class,
			// player
			BurningForcesToRun.class,
			FireExtinguishByHand.class,
			HidePlayerNameTags.class,
			JumpingOverFences.class,
			PlayerSpeedTracker.class,
			SwapHalfStack.class,
			// projectile
			ArrowsBreakFragileBlocks.class,
			BurningLitsProjectiles.class,
			BurningProjectileCreatesFire.class,
			SnowballCreatesSnow.class,
			SnowballFreezesEntities.class,
			WaterPotionSplashesTorches.class,
			// vehicle
			JumpyBoats.class,
			// world
			CustomDayCycleCleanup.class,
			TrailPaths.class
		);

		new ShearableEntities.ShearableBehavior()
			.withDrop(Material.FEATHER, 1, 2)
			.registerFor(EntityType.CHICKEN);
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
	public static NamespacedKey miscMeKey(String value) {
		return new NamespacedKey(NAMESPACE, value);
	}

}
