package me.sosedik.trappednewbie.dataset;

import com.destroystokyo.paper.MaterialTags;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.common.base.Preconditions;
import de.tr7zw.nbtapi.NBT;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.BannerPatternLayers;
import io.papermc.paper.datacomponent.item.BundleContents;
import io.papermc.paper.datacomponent.item.ChargedProjectiles;
import io.papermc.paper.datacomponent.item.DamageResistant;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import io.papermc.paper.datacomponent.item.Equippable;
import io.papermc.paper.datacomponent.item.Fireworks;
import io.papermc.paper.datacomponent.item.ItemArmorTrim;
import io.papermc.paper.datacomponent.item.ItemContainerContents;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import io.papermc.paper.datacomponent.item.PotDecorations;
import io.papermc.paper.datacomponent.item.PotionContents;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import io.papermc.paper.datacomponent.item.UseCooldown;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.keys.VillagerTypeKeys;
import io.papermc.paper.registry.keys.tags.DamageTypeTagKeys;
import io.papermc.paper.registry.keys.tags.StructureTagKeys;
import me.sosedik.delightfulfarming.dataset.DelightfulFarmingItems;
import me.sosedik.miscme.dataset.MoreMobHeads;
import me.sosedik.packetadvancements.api.advancement.IAdvancement;
import me.sosedik.packetadvancements.api.tab.AdvancementManager;
import me.sosedik.packetadvancements.api.tab.AdvancementTab;
import me.sosedik.packetadvancements.imlp.progress.vanilla.conditions.BlockTriggerCondition;
import me.sosedik.packetadvancements.imlp.progress.vanilla.conditions.DamageTriggerCondition;
import me.sosedik.packetadvancements.imlp.progress.vanilla.conditions.EntityStateTriggerCondition;
import me.sosedik.packetadvancements.imlp.progress.vanilla.conditions.EntityTriggerCondition;
import me.sosedik.packetadvancements.imlp.progress.vanilla.conditions.ItemTriggerCondition;
import me.sosedik.packetadvancements.imlp.progress.vanilla.conditions.MinMaxBoundsTriggerCondition;
import me.sosedik.packetadvancements.imlp.progress.vanilla.conditions.context.TimeTriggerCondition;
import me.sosedik.packetadvancements.util.storage.JsonStorage;
import me.sosedik.requiem.dataset.RequiemItems;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.api.advancement.display.AdvancementFrame;
import me.sosedik.trappednewbie.api.advancement.display.AnnouncementMessage;
import me.sosedik.trappednewbie.api.advancement.display.FancierAdvancementDisplay;
import me.sosedik.trappednewbie.api.advancement.display.OpeningHolderAdvancementDisplay;
import me.sosedik.trappednewbie.api.advancement.reward.FancyAdvancementReward;
import me.sosedik.trappednewbie.impl.advancement.ApplyAllSmithingTemplatesAdvancement;
import me.sosedik.trappednewbie.impl.advancement.AttackSquidInTheAirWithASnowballAdvancement;
import me.sosedik.trappednewbie.impl.advancement.AttackWithAllAxesAdvancement;
import me.sosedik.trappednewbie.impl.advancement.AttackWithAllShovelsAdvancement;
import me.sosedik.trappednewbie.impl.advancement.AttackWithAllWeaponsAdvancement;
import me.sosedik.trappednewbie.impl.advancement.AttackWithAnEggAdvancement;
import me.sosedik.trappednewbie.impl.advancement.AttackZombieWithAnEggAdvancement;
import me.sosedik.trappednewbie.impl.advancement.BlowUpAllMonstersWithTNTAdvancement;
import me.sosedik.trappednewbie.impl.advancement.CollectAStackOfPotterySherdsAdvancement;
import me.sosedik.trappednewbie.impl.advancement.CollectAllPotterySherdsAdvancement;
import me.sosedik.trappednewbie.impl.advancement.CommunismAdvancement;
import me.sosedik.trappednewbie.impl.advancement.FrozenHeartAdvancement;
import me.sosedik.trappednewbie.impl.advancement.GetABannerShieldAdvancement;
import me.sosedik.trappednewbie.impl.advancement.GetALeatherCopperArmorAdvancement;
import me.sosedik.trappednewbie.impl.advancement.GetALeatherEmeraldArmorAdvancement;
import me.sosedik.trappednewbie.impl.advancement.GetALeatherNetheriteArmorAdvancement;
import me.sosedik.trappednewbie.impl.advancement.GetAStackOfAllSmithingTemplatesAdvancement;
import me.sosedik.trappednewbie.impl.advancement.GiveWindMaceToAFoxAdvancement;
import me.sosedik.trappednewbie.impl.advancement.HuntLandAnimalsAdvancement;
import me.sosedik.trappednewbie.impl.advancement.InspectorGadgetAdvancement;
import me.sosedik.trappednewbie.impl.advancement.KillAHostileMobAdvancement;
import me.sosedik.trappednewbie.impl.advancement.KillAMobThatIsWearingItsHeadAdvancement;
import me.sosedik.trappednewbie.impl.advancement.KillAllAllJockeysAdvancement;
import me.sosedik.trappednewbie.impl.advancement.KillAllAquaticWithATridentAdvancement;
import me.sosedik.trappednewbie.impl.advancement.KillAllBabyZombiesAdvancement;
import me.sosedik.trappednewbie.impl.advancement.KillAllHostileMobsAdvancement;
import me.sosedik.trappednewbie.impl.advancement.KillAllJockeysAdvancement;
import me.sosedik.trappednewbie.impl.advancement.KillAllNetherWithATridentAdvancement;
import me.sosedik.trappednewbie.impl.advancement.KillAllPumpkinMobsAdvancement;
import me.sosedik.trappednewbie.impl.advancement.KillAllUsingTheirItemsAdvancement;
import me.sosedik.trappednewbie.impl.advancement.KillAllZombieVillagersAdvancement;
import me.sosedik.trappednewbie.impl.advancement.KillEndermenWithTheirItemsAdvancement;
import me.sosedik.trappednewbie.impl.advancement.KillHostileDungeonMobsAdvancement;
import me.sosedik.trappednewbie.impl.advancement.KillHostileEndMobsAdvancement;
import me.sosedik.trappednewbie.impl.advancement.KillHostileNetherMobsAdvancement;
import me.sosedik.trappednewbie.impl.advancement.KillHostileOverworldNightMobsAdvancement;
import me.sosedik.trappednewbie.impl.advancement.MasterShieldsmanAdvancement;
import me.sosedik.trappednewbie.impl.advancement.MereMortalAdvancement;
import me.sosedik.trappednewbie.impl.advancement.ObtainEveryArmorTrimAdvancement;
import me.sosedik.trappednewbie.impl.advancement.PyrotechnicAdvancement;
import me.sosedik.trappednewbie.impl.advancement.RockPaperShearsAdvancement;
import me.sosedik.trappednewbie.impl.advancement.Walk10KKMAdvancement;
import me.sosedik.trappednewbie.impl.advancement.WearARealNetheriteArmorAdvancement;
import me.sosedik.trappednewbie.impl.advancement.WearArmorWithSameTrimsAdvancement;
import me.sosedik.trappednewbie.impl.advancement.WearSilentArmorAdvancement;
import me.sosedik.trappednewbie.impl.advancement.YouMonsterAdvancement;
import me.sosedik.trappednewbie.impl.item.modifier.BucketModifier;
import me.sosedik.trappednewbie.impl.item.modifier.LetterModifier;
import me.sosedik.trappednewbie.impl.item.modifier.ScrapModifier;
import me.sosedik.trappednewbie.impl.thirst.ThirstData;
import me.sosedik.trappednewbie.listener.advancement.AdvancementsAdvancement;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.dataset.UtilizerTags;
import me.sosedik.utilizer.util.BiomeTags;
import me.sosedik.utilizer.util.ItemUtil;
import me.sosedik.utilizer.util.MiscUtil;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.generator.structure.Structure;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.loot.LootTables;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress.alwaysDone;
import static me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress.neverDone;
import static me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress.requirements;
import static me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress.simple;
import static me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress.vanilla;
import static me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress.vanillaAny;
import static me.sosedik.packetadvancements.api.tab.AdvancementTab.buildTab;
import static me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement.buildBase;
import static me.sosedik.packetadvancements.imlp.advancement.fake.FakeAdvancement.buildFake;
import static me.sosedik.packetadvancements.imlp.advancement.linking.LinkingAdvancement.buildLinking;
import static me.sosedik.packetadvancements.imlp.advancement.multi.MultiParentAdvancement.buildMulti;
import static me.sosedik.packetadvancements.imlp.display.AdvancementVisibilities.grandParentGranted;
import static me.sosedik.packetadvancements.imlp.display.AdvancementVisibilities.hidden;
import static me.sosedik.packetadvancements.imlp.display.AdvancementVisibilities.ifDone;
import static me.sosedik.packetadvancements.imlp.display.AdvancementVisibilities.ifVisible;
import static me.sosedik.packetadvancements.imlp.display.AdvancementVisibilities.parentGranted;
import static me.sosedik.packetadvancements.imlp.display.SimpleAdvancementTabDisplay.simpleTabDisplay;
import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.anyBlockUse;
import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.bredAnimals;
import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.consumeItem;
import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.entityHurtPlayer;
import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.entityKilledPlayer;
import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.fallFromHeight;
import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.fishingRodHooked;
import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.inventoryChanged;
import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.itemUsedOnBlock;
import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.killedByArrow;
import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.location;
import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.placedBlock;
import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.playerGeneratesContainerLoot;
import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.playerHurtEntity;
import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.playerInteractedWithEntity;
import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.playerKilledEntity;
import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.recipeCrafted;
import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.shotCrossbow;
import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.startedRiding;
import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.thrownItemPickedUpByEntity;
import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.usingItem;
import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.villagerTrade;
import static me.sosedik.trappednewbie.TrappedNewbie.trappedNewbieKey;

@NullMarked
@SuppressWarnings("unused")
public class TrappedNewbieAdvancements {

	private static final ItemStack WANDERING_TRADER_HEAD = ItemUtil.texturedHead(MoreMobHeads.WANDERING_TRADER);
	private static final ItemStack VILLAGER_HEAD = ItemUtil.texturedHead(MoreMobHeads.PLAINS_VILLAGER);
	private static final ItemStack SHIFTING_HEAD = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWQ4NmQwOTZiOGMwOGFkNWRhNmVhY2E5YTEwMTc1MWZiYzY4NzcwZDY3ZDUzMWU4Y2ZhNTYzYmE3M2Y5N2EzYyJ9fX0=");
	private static final ItemStack PIG_HEAD = ItemUtil.texturedHead(MoreMobHeads.PIG_REGULAR);
	private static final ItemStack MR_SHEEP_HEAD = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ0M2M0YjAwNDNhZWQyYThjMDAzNTEzZjI3OTgwMDM1NzY0MjUzMzg3N2UzZjJkNjhmZDkyYjY5NGJlM2E4NiJ9fX0=");
	private static final ItemStack PIG_HOG_HEAD = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTNmZTNlYzU3YjYwMDkxMzg1MWVjMDMyODBjOTc5MTZmYWU5ZTA4NmFlYWFhNDcwMzM5NDE2ZmRmNGEyYTMwZiJ9fX0=");
	private static final ItemStack HAPPY_1000_DAYS_HEAD = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA1YTZiZTIyZmJjNGRhZWU2YzAyZDJlYzUyNjI2NTE1Mjg3N2Y3ZDc1YWQzN2FjZjI5YTMzNWU5ZGFiODEzMCJ9fX0=");
	private static final ItemStack LADDER_ACE_HEAD = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2MwOTQwNjRlMGNlOGI2ODAxOTlkMTA5MmQ0Mjg5NDUxMzM4N2NlYzA1MGY2NGQ3YTkyNzk3YmIyYTY0NGJjNyJ9fX0=");
	private static final ItemStack CHEST_HEAD = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2UyZWI0NzUxZTNjNTBkNTBmZjE2MzUyNTc2NjYzZDhmZWRmZTNlMDRiMmYwYjhhMmFhODAzYjQxOTM2M2NhMSJ9fX0=");
	private static final ItemStack CHRISTMAS_CHEST_HEAD = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzZkZjQ3YzMwYjFlM2RiNTJlNDFmNWVlYjgwNmM2OWZlZjgwNTk1NTBlOGY1N2IwYTgzYjIyNjBhNjZkOTI3ZSJ9fX0=");
	private static final ItemStack MINECART_HEAD = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2MzNTNmMjg5NmE0OTE1M2QyMzM4OGM3YjcxNzgxZDAyM2I5ZWJiNzU0NjdhZWY5Njg5MmYxYzIzZmQ0MiJ9fX0=");
	private static final ItemStack CRAFTING_TABLE_HEAD = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWYzMGZmYmMwMTEwZWZhMzRlMDMwODYwZGExOGM4YTFkNmIyMjNkZTBmMDBkOWU0YzVkMGNmYTdlY2ZhZmE0OCJ9fX0=");
	private static final ItemStack TRADER_HEAD = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmY3MWFmZDg3ZjdkMmVkZGQyNmY0MzUxYjkxNTYzNTY3NDM0MThjMzRkMTM0OGM5YjNjNWFmYjk5YzZiODMzOSJ9fX0=");
	private static final ItemStack SURVIVE_HEAD_1 = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWQ0NTJiZmNkYzNlYTE0ZjllNjEyYjFjOTZhYmVmOTdjMTBlOTZjNzExNmVhMmE0YjFhNWRmOGQ0YWExNzJmOSJ9fX0=");
	private static final ItemStack SURVIVE_HEAD_2 = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjYwZmE2MzIyNzRlODliMDY0YzMwOTBjMWFiMTEwMTQ3YzgzZWM3M2VjNGMzMWNhNzUwODE0NTdlYmI4YjI2OSJ9fX0=");
	private static final ItemStack SURVIVE_HEAD_3 = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDRhYjlkNmQ5ZjZkMjMxNGEyYmZmZjk4ZmRiODBmY2I0Y2UwYzhlZjc4ODEzMWE5YzZjMjJlY2M3MGU4M2Q3OSJ9fX0=");
	private static final ItemStack STONKS_HEAD = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWUxNzBmMzZmNjY3MThmOGE2OWMyNWI4YTk5ZTI5ZDMwNjYxODU1M2MyM2IxZmUwNzBjZmViMzU5Y2NhMGU3YSJ9fX0=");
	private static final ItemStack DESERT_HEAD = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTAzMDdmNDkzZmRkYTc2ODU4ZTVkYWE1MTFlM2ZkYjRiN2VkMjQxZjdkM2E3YTIyNjU2ZWQ1YzZiYWNiZjUyIn19fQ==");
	private static final ItemStack JACK_O_LANTERN_HEAD = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDkyMTlmMjI5YTg2NjU1NDdhMWQ0MTZjMTUyN2MyZjk4YzNkYjE2NzE2ODU5NGI1MDgzZGIzZDgxNjA1NjQ2NCJ9fX0=");
	private static final ItemStack FROZEN_HEART_HEAD = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTFmNzcyZjBlMGE5NGM4NGIyNjgyNmQxNWVjNGNlMzljMDEzZDVmM2ViMGZjOGMxODY4MDA4YzdiNzRjZDAwNSJ9fX0=");
	private static final ItemStack ASSASSIN_HEAD = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTJhNWU2NDJiZjAwMjZhODJhN2E0MGIxNWRmMzA3Nzg1NzAwYWU0OTc0YmIzN2MxZWQ1MDVlMTJmM2EzNmJkNiJ9fX0=");
	private static final ItemStack THE_WALKING_HEAD = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDI4ZDlmZjU0MTg4YTFhZmVlNjViOTRmM2JmY2NlMzIxYzY0M2EzNDU5MGMxNGIxOTJiMmUzZWMyZjUyNWQzIn19fQ==");
	private static final ItemStack ARACHNOPHOBIA_HEAD = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTAxMjhlOWRiNGI1NDRlMTQ3ZWM2OGY5NGQ4NWY5ZGI4MTA5OTRhZWI5NDNiMDM4ZGQ0OTFlYTJlYTlhNDY5NiJ9fX0=");
	private static final ItemStack SANS_HEAD = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ5MDcwODM4ZjZkNzU0M2U3NGQ0MjE3ZjI4YzcxNDdkODAxNDI3MWJlNDhhMzkzMGE5YjY2OWI0YTY1NWZmNSJ9fX0=");
	private static final ItemStack KOMARU_HEAD = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWUwMDNjMzVhZGEzMGM2MGFhNzgyNjQzNzE0YjU0N2U0NzRhNjcwYmE0MjcwNzEyMjFlNzE0NTA3NWQwODQ3MCJ9fX0=");
	private static final ItemStack MR_WORLDWIDE_HEAD = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDRjNTY0OTYwNzc0ZDRhNzc1ZmMzZjFhNzY5NmU3NGY4Y2FmMWEyMDRkNDk1ZDNmYjFhYmIwNGZhOTFkMmJmIn19fQ==");
	private static final ItemStack DINNERBONE_HEAD = ItemStack.of(Material.PLAYER_HEAD);
	private static final ItemStack JEB_HEAD = ItemStack.of(Material.PLAYER_HEAD);
	private static final ItemStack TECHNOBLADE_HEAD = ItemStack.of(Material.PLAYER_HEAD);
	private static final TextColor PURPLE = AnnouncementMessage.SUPER_TORTURE.getColor();
	private static final TextColor GRAY = requireNonNull(TextColor.fromHexString("#cccccc"));

	static {
		DINNERBONE_HEAD.setData(DataComponentTypes.PROFILE, ResolvableProfile.resolvableProfile()
			.uuid(UUID.fromString("61699b2e-d327-4a01-9f1e-0ea8c3f06bc6"))
			.name("Dinnerbone")
			.addProperty(new ProfileProperty("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTBjNDEwZmFkOGQ5ZDg4MjVhZDU2YjBlNDQzZTI3NzdhNmI0NmJmYTIwZGFjZDFkMmY1NWVkYzcxZmJlYjA2ZCJ9fX0="))
			.build());
		JEB_HEAD.setData(DataComponentTypes.PROFILE, ResolvableProfile.resolvableProfile()
			.uuid(UUID.fromString("853c80ef-3c37-49fd-aa49-938b674adae6"))
			.name("jeb_")
			.addProperty(new ProfileProperty("textures", "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDJlN2Y3OTdlOTJhOTk1NmU5MTUxYjM1YmJhZWMwMTIzNjVhOTAyY2U4OTc5MGRhYjVhNDc3ODliZWQ5NzE5MCJ9fX0="))
			.build());
		TECHNOBLADE_HEAD.setData(DataComponentTypes.PROFILE, ResolvableProfile.resolvableProfile()
			.uuid(UUID.fromString("b876ec32-e396-476b-a115-8438d83c67d4"))
			.name("Technoblade")
			.addProperty(new ProfileProperty("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGM5MDdhN2Y0ZjBjM2E2YzljOWI3OTM0OGY3YmYxOTkzNjk1YjZkMmVhZTJmYWI3MDRhMWE0ZDliODI4OGNiZSJ9fX0="))
			.build());
	}

	public static final AdvancementManager MANAGER = new AdvancementManager(new JsonStorage(TrappedNewbie.instance()));

	public static final AdvancementTab REQUIEM_TAB = buildTab("requiem", MANAGER)
		.display(simpleTabDisplay().inverseY().display(display().backgroundPathBlock(Material.SOUL_SAND).icon(Material.SKELETON_SKULL)))
		.build();
	public static final IAdvancement REQUIEM_ROOT = buildBase(REQUIEM_TAB, "visual_root")
		.display(display().noAnnounceChat().withAdvancementFrame(AdvancementFrame.SQUIRCLE).icon(Material.SUNFLOWER)).requiredProgress(requirements("interact", "open", "letter", "friendship"))
		.visibilityRule(hidden())
		.buildAndRegister();
	public static final IAdvancement OPENING_HOLDER = buildFake(REQUIEM_ROOT, "holder")
		.display(new OpeningHolderAdvancementDisplay().x(-1.25F).noAnnounceChat().withAdvancementFrame(AdvancementFrame.SPEECH_BUBBLE).icon(WANDERING_TRADER_HEAD))
		.requiredProgress(alwaysDone())
		.buildAndRegister();
	public static final IAdvancement BRAVE_NEW_WORLD = buildBase(REQUIEM_ROOT, "brave_new_world")
		.display(display().x(1F).noAnnounceChat().icon(braveNewWorldItem())).buildAndRegister();
	public static final IAdvancement FIRST_POSSESSION = buildBase(BRAVE_NEW_WORLD, "first_possession")
		.display(display().x(1.25F).withAdvancementFrame(AdvancementFrame.SHARP).icon(RequiemItems.HOST_REVOCATOR))
		.visibilityRule(parentGranted())
		.buildAndRegister();
	public static final IAdvancement EAT_A_ROASTED_SPIDER_EYE = buildBase(FIRST_POSSESSION, "eat_a_roasted_spider_eye")
		.display(display().xy(0.5F, -1F).withAdvancementFrame(AdvancementFrame.CIRCLE).icon(DelightfulFarmingItems.ROASTED_SPIDER_EYE))
		.visibilityRule(parentGranted())
		.requiredProgress(vanilla(consumeItem().withItem(ItemTriggerCondition.of(DelightfulFarmingItems.ROASTED_SPIDER_EYE))))
		.buildAndRegister();
	public static final IAdvancement GOOD_AS_NEW = buildBase(FIRST_POSSESSION, "good_as_new")
		.display(display().x(1.2F).withAdvancementFrame(AdvancementFrame.SHARP).icon(ItemUtil.texturedHead(MoreMobHeads.ZOMBIE_VILLAGER_PLAINS_ARMORER)))
		.buildAndRegister();
	public static final IAdvancement GET_A_NECRONOMICON = buildBase(GOOD_AS_NEW, "get_a_necronomicon")
		.display(display().x(1.25F).withAdvancementFrame(AdvancementFrame.ARROW_RIGHT).icon(RequiemItems.NECRONOMICON))
		.buildAndRegister();
	public static final IAdvancement GET_INTO_A_PERSONAL_VOID = buildBase(GET_A_NECRONOMICON, "get_into_a_personal_void")
		.display(display().x(1F).withAdvancementFrame(AdvancementFrame.SQUIRCLE).icon(Material.BEDROCK))
		.buildAndRegister();
	public static final IAdvancement MERE_MORTAL = buildBase(GOOD_AS_NEW, "mere_mortal")
		.display(display().x(1F).icon(Material.PLAYER_HEAD))
		.visibilityRule(hidden())
		.buildAndRegister(MereMortalAdvancement::new); // ToDo
	public static final IAdvancement I_HATE_SAND = buildBase(GOOD_AS_NEW, "i_hate_sand")
		.display(display().xy(1F, 1F).withAdvancementFrame(AdvancementFrame.CIRCLE).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.SAND))
		.buildAndRegister();
	public static final IAdvancement KUNG_FU_PANDA = buildBase(I_HATE_SAND, "kung_fu_panda")
		.display(display().x(1F).withAdvancementFrame(AdvancementFrame.CIRCLE).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.BAMBOO))
		.buildAndRegister();
	public static final IAdvancement FIRST_DRINK = buildBase(GOOD_AS_NEW, "first_drink")
		.display(display().xy(1F, -1.6F).fancyDescriptionParent(NamedTextColor.GRAY).icon(Material.DRAGON_BREATH))
		.visibilityRule(grandParentGranted())
		.buildAndRegister();

	public static final AdvancementTab BASICS_TAB = buildTab("basics", MANAGER)
		.display(simpleTabDisplay().inverseY().display(display().backgroundPathBlock(Material.GRAVEL).icon(Material.ROOTED_DIRT)))
		.build();
	public static final IAdvancement BASICS_ROOT = buildBase(BASICS_TAB, "visual_root").display(display().withAdvancementFrame(AdvancementFrame.SQUIRCLE).icon(Material.ROOTED_DIRT))
			.visibilityRule(ifDone(false, BRAVE_NEW_WORLD))
			.requiredProgress(alwaysDone())
			.buildAndRegister();
	public static final IAdvancement GET_A_FIBER = buildBase(BASICS_ROOT, "get_a_fiber").display(display().x(1.5F).withAdvancementFrame(AdvancementFrame.SHARP).icon(TrappedNewbieItems.FIBER))
			.withReward(rewards().addItems(ItemStack.of(TrappedNewbieItems.FIBER, 3)))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.FIBER))))
			.buildAndRegister();
	public static final IAdvancement MAKE_A_TWINE = buildBase(GET_A_FIBER, "make_a_twine").display(display().x(1F).withAdvancementFrame(AdvancementFrame.SHARP).icon(TrappedNewbieItems.TWINE))
			.withReward(rewards().addItems(ItemStack.of(TrappedNewbieItems.TWINE, 2)))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.TWINE))))
			.buildAndRegister();
	public static final IAdvancement GET_A_ROCK = buildBase(BASICS_ROOT, "get_a_rock").display(display().xy(-0.2F, -2F).icon(TrappedNewbieItems.ROCK))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.ROCK))))
			.buildAndRegister();
	public static final IAdvancement MAKE_A_COBBLESTONE = buildBase(GET_A_ROCK, "make_a_cobblestone").display(display().x(-1F).icon(Material.COBBLESTONE))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.COBBLESTONE))))
			.buildAndRegister();
	public static final IAdvancement FIND_GRAVEL = buildBase(BASICS_ROOT, "find_gravel").display(display().xy(1F, -1F).withAdvancementFrame(AdvancementFrame.SHARP).icon(Material.GRAVEL))
			.withReward(rewards().withExp(10))
			.buildAndRegister();
	public static final IAdvancement GET_A_FLINT = buildBase(FIND_GRAVEL, "get_a_flint").display(display().x(1F).withAdvancementFrame(AdvancementFrame.SHARP).icon(Material.FLINT))
			.withReward(rewards().addItems(ItemStack.of(Material.FLINT, 2)))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.FLINT))))
			.buildAndRegister();
	public static final IAdvancement GET_A_FLAKED_FLINT = buildBase(GET_A_FLINT, "get_a_flaked_flint").display(display().x(1F).withAdvancementFrame(AdvancementFrame.SHARP).icon(TrappedNewbieItems.FLAKED_FLINT))
			.withReward(rewards().withExp(10).addItems(ItemStack.of(TrappedNewbieItems.FLAKED_FLINT, 3)))
			.buildAndRegister();
	public static final IAdvancement MAKE_FLINT_SHEARS = buildMulti(GET_A_FLAKED_FLINT, "make_flint_shears", MAKE_A_TWINE).linkingToAll(false).display(display().xy(1F, 0.5F).goalFrame().icon(TrappedNewbieItems.FLINT_SHEARS))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.FLINT_SHEARS))))
			.buildAndRegister();
	public static final IAdvancement GET_A_BRANCH = buildBase(BASICS_ROOT, "get_a_branch").display(display().xy(2F, 1F).withAdvancementFrame(AdvancementFrame.SHARP).icon(TrappedNewbieItems.OAK_BRANCH))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieTags.BRANCHES))))
			.buildAndRegister();
	public static final IAdvancement MAKE_ROUGH_STICKS = buildBase(MAKE_FLINT_SHEARS, "make_rough_sticks").display(display().xy(1.2F, 0.75F).withAdvancementFrame(AdvancementFrame.SHARP).icon(TrappedNewbieItems.ROUGH_STICK))
			.withReward(rewards().addItems(ItemStack.of(TrappedNewbieItems.ROUGH_STICK, 3)))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.ROUGH_STICK))))
			.buildAndRegister();
	public static final IAdvancement MAKE_A_COBBLESTONE_HAMMER = buildBase(MAKE_A_COBBLESTONE, "make_a_cobblestone_hammer").display(display().x(-1F).goalFrame().icon(TrappedNewbieItems.COBBLESTONE_HAMMER))
			.visibilityRule(ifDone(MAKE_ROUGH_STICKS))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.COBBLESTONE_HAMMER))))
			.buildAndRegister();
	public static final IAdvancement MAKE_A_FLINT_AXE = buildBase(MAKE_ROUGH_STICKS, "make_a_flint_axe").display(display().y(2F).withAdvancementFrame(AdvancementFrame.ARROW_LEFT).icon(TrappedNewbieItems.FLINT_AXE))
			.visibilityRule(parentGranted())
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.FLINT_AXE))))
			.buildAndRegister();
	public static final IAdvancement GET_A_LOG = buildBase(MAKE_A_FLINT_AXE, "get_a_log").display(display().x(-1F).withAdvancementFrame(AdvancementFrame.SHARP).icon(Material.OAK_LOG))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Tag.LOGS))))
			.buildAndRegister();
	public static final IAdvancement MAKE_A_CHOPPING_BLOCK = buildBase(GET_A_LOG, "make_a_chopping_block").display(display().x(-1F).icon(TrappedNewbieItems.OAK_CHOPPING_BLOCK))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieTags.ITEM_CHOPPING_BLOCKS))))
			.buildAndRegister();
	public static final IAdvancement MAKE_PLANKS = buildBase(MAKE_A_CHOPPING_BLOCK, "make_planks").display(display().x(-1F).icon(Material.OAK_PLANKS))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Tag.PLANKS))))
			.buildAndRegister();
	public static final IAdvancement MAKE_STICKS = buildBase(MAKE_PLANKS, "make_sticks").display(display().x(-1.05F).icon(TrappedNewbieItems.OAK_STICK))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieTags.STICKS))))
			.buildAndRegister();
	public static final IAdvancement MAKE_A_GRASS_MESH = buildBase(BASICS_ROOT, "make_a_grass_mesh").display(display().xy(1F, -2.5F).goalFrame().icon(TrappedNewbieItems.GRASS_MESH))
			.visibilityRule(ifDone(MAKE_ROUGH_STICKS))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.GRASS_MESH))))
			.buildAndRegister();
	public static final IAdvancement TREASURE_HUNT = buildBase(MAKE_A_GRASS_MESH, "treasure_hunt").display(display().x(1F).withAdvancementFrame(AdvancementFrame.CIRCLE).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.BOWL)).buildAndRegister();
	public static final IAdvancement MEET_THE_FLINTSTONES = buildBase(TREASURE_HUNT, "meet_the_flintstones").display(display().x(1F).withAdvancementFrame(AdvancementFrame.STAR).fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.FLINT))
			.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.FLINT, 8)))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.FLINT).withMinAmount(64))))
			.buildAndRegister();
	public static final IAdvancement CAMPING_OUT = buildBase(MAKE_ROUGH_STICKS, "camping_out").display(display().xy(1.2F, -1F).withAdvancementFrame(AdvancementFrame.BLOCK).icon(Material.CAMPFIRE))
			.withReward(rewards().withExp(10).addItems(ItemStack.of(TrappedNewbieItems.ROUGH_STICK, 4))) // TODO ghastshmallow
			.visibilityRule(parentGranted())
			.buildAndRegister();
	public static final IAdvancement SPAWN_CAMPING = buildBase(CAMPING_OUT, "spawn_camping").display(display().xy(0.5F, 1F).withAdvancementFrame(AdvancementFrame.STAR).fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.ENDER_EYE))
			.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.PORKCHOP, 16)))
			.buildAndRegister();
	public static final IAdvancement NOT_SPAWN_CAMPING = buildBase(SPAWN_CAMPING, "not_spawn_camping").display(display().xy(1F, 0.3F).challengeFrame().torture().fancyDescriptionParent(NamedTextColor.DARK_RED).icon(() -> {
				var item = ItemStack.of(Material.CAMPFIRE);
				item.setBlockData(Material.CAMPFIRE.createBlockData(data -> ((Campfire) data).setLit(true)));
				return item;
			}))
			.withReward(rewards()
				.withTrophy(ItemStack.of(Material.COMPASS))
			)
			.buildAndRegister();
	public static final IAdvancement CORNER_CAMPING = buildBase(NOT_SPAWN_CAMPING, "corner_camping").display(display().xy(1F, 0.3F).withAdvancementFrame(AdvancementFrame.BUTTERFLY).torture().fancyDescriptionParent(NamedTextColor.DARK_RED).icon(() -> {
				var item = ItemStack.of(Material.SOUL_CAMPFIRE);
				item.setBlockData(Material.SOUL_CAMPFIRE.createBlockData(data -> ((Campfire) data).setLit(true)));
				return item;
			}))
			.withReward(rewards()
				.withTrophy(ItemStack.of(Material.BOW))
			)
			.buildAndRegister();
	public static final IAdvancement MAKE_A_FIRE = buildBase(CAMPING_OUT, "make_a_fire").display(display().x(1F).goalFrame().icon(TrappedNewbieItems.INVENTORY_FIRE))
			.buildAndRegister();
	public static final IAdvancement MAKE_A_FIRE_FILLER = buildFake(MAKE_A_FIRE).display(display().x(0.5F).isHidden(true))
			.requiredProgress(neverDone())
			.buildAndRegister();
	public static final IAdvancement GET_A_CHARCOAL = buildBase(MAKE_A_FIRE_FILLER, "get_a_charcoal").display(display().xy(1F, -0.55F).icon(Material.CHARCOAL))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.CHARCOAL))))
			.buildAndRegister();
	public static final IAdvancement MAKE_A_FIRESTRIKER = buildBase(GET_A_CHARCOAL, "make_a_firestriker").display(display().x(1F).goalFrame().icon(TrappedNewbieItems.FIRESTRIKER))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.FIRESTRIKER))))
			.buildAndRegister();
	public static final IAdvancement MAKE_A_FLINT_PICKAXE = buildBase(MAKE_STICKS, "make_a_flint_pickaxe").display(display().xy(0.5F, 1.5F).goalFrame().icon(TrappedNewbieItems.FLINT_PICKAXE))
			.visibilityRule(ifDone(MAKE_PLANKS))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.FLINT_PICKAXE))))
			.buildAndRegister();
	public static final IAdvancement MAKE_A_WORK_STATION = buildBase(GET_A_LOG, "make_a_work_station").display(display().xy(0.5F, 1.5F).withAdvancementFrame(AdvancementFrame.BLOCK).icon(TrappedNewbieItems.OAK_WORK_STATION))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieTags.ITEM_WORK_STATIONS))))
			.buildAndRegister();
	public static final IAdvancement MAKE_A_FLINT_KNIFE = buildBase(MAKE_ROUGH_STICKS, "make_a_flint_knife")
		.display(display().xy(0.5F, -3.9F).goalFrame().icon(TrappedNewbieItems.FLINT_KNIFE))
		.visibilityRule(parentGranted())
		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.FLINT_KNIFE))))
		.buildAndRegister();
	public static final IAdvancement MAKE_A_TOTEM_BASE = buildBase(MAKE_A_FLINT_KNIFE, "make_a_totem_base").display(display().x(1F).icon(TrappedNewbieItems.OAK_TOTEM_BASE))
			.visibilityRule(ifDone(MAKE_A_WORK_STATION))
			.buildAndRegister();
	public static final IAdvancement PERFORM_A_RITUAL = buildBase(MAKE_A_TOTEM_BASE, "perform_a_ritual").display(display().x(1F).icon(TrappedNewbieItems.TOTEMIC_STAFF))
			.visibilityRule(ifDone(MAKE_A_WORK_STATION))
			.buildAndRegister();

	public static final IAdvancement GET_A_STRING = buildBase(PERFORM_A_RITUAL, "get_a_string").display(display().xy(1.3F, -0.5F).icon(Material.STRING))
			.withReward(rewards().addItems(ItemStack.of(Material.STRING, 3)))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.STRING))))
			.buildAndRegister();
	public static final IAdvancement GET_A_WOOL = buildBase(PERFORM_A_RITUAL, "get_a_wool").display(display().xy(1.3F, 0.5F).icon(Material.WHITE_WOOL))
			.withReward(rewards().addItems(ItemStack.of(Material.MUTTON, 2)).addItems(ItemStack.of(Material.WHITE_WOOL)))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Tag.WOOL))))
			.buildAndRegister();
	public static final IAdvancement MAKE_A_CARPET = buildMulti(GET_A_STRING, "make_a_carpet", GET_A_WOOL).display(display().xy(1.2F, 0.5F).withAdvancementFrame(AdvancementFrame.HEART).icon(Material.WHITE_CARPET))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Tag.WOOL_CARPETS))))
			.buildAndRegister();
	public static final IAdvancement MAKE_A_SLEEPING_BAG = buildBase(MAKE_A_CARPET, "make_a_sleeping_bag").display(display().x(1F).icon(ItemStack.of(TrappedNewbieItems.SLEEPING_BAG).withColor(DyeColor.WHITE)))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.SLEEPING_BAG))))
			.buildAndRegister();
//	public static final IAdvancement SLEEP_IN_BED = buildBase(MAKE_A_SLEEPING_BAG, "sleep_in_bed").display(display().x(1F).challengeFrame().icon(Material.WHITE_BED)) // TODO
//			.visibilityRule(grandParentGranted())
//			.buildAndRegister();
//	public static final IAdvancement LUCID_DREAMING = buildBase(SLEEP_IN_BED, "lucid_dreaming").display(display().xy(1.5F, 0.6F).challengeFrame().icon(Material.PHANTOM_SPAWN_EGG)).visibilityRule(parentGranted()).buildAndRegister(); // TODO
	public static final IAdvancement GLIDE_IN_A_HANG_GLIDER = buildBase(GET_A_WOOL, "glide_in_a_hang_glider")
		.display(display().xy(1.5F, 0.75F).withAdvancementFrame(AdvancementFrame.ARROW_UP).icon(TrappedNewbieItems.HANG_GLIDER))
		.withReward(rewards().addItems(ItemStack.of(Material.PHANTOM_MEMBRANE, 3)))
		.buildAndRegister();
	public static final IAdvancement MAKE_A_FLINT_SHOVEL = buildBase(MAKE_ROUGH_STICKS, "make_a_flint_shovel").display(display().xy(1.25F, 1.75F).goalFrame().icon(TrappedNewbieItems.FLINT_SHOVEL))
			.visibilityRule(parentGranted())
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.FLINT_SHOVEL))))
			.buildAndRegister();
	public static final IAdvancement PATHWAYS = buildBase(MAKE_A_FLINT_SHOVEL, "pathways").display(display().xy(0.5F, 1F).withAdvancementFrame(AdvancementFrame.CIRCLE).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.DIRT_PATH))
			.buildAndRegister();
	public static final IAdvancement GET_A_CLAY_BALL = buildBase(MAKE_A_FLINT_SHOVEL, "get_a_clay_ball").display(display().x(1.25F).withAdvancementFrame(AdvancementFrame.SHARP).icon(Material.CLAY_BALL))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.CLAY_BALL))))
			.buildAndRegister();
	public static final IAdvancement MAKE_A_CLAY_KILN = buildBase(GET_A_CLAY_BALL, "make_a_clay_kiln").display(display().x(1.15F).withAdvancementFrame(AdvancementFrame.BLOCK).icon(TrappedNewbieItems.CLAY_KILN))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.CLAY_KILN))))
			.buildAndRegister();
	public static final IAdvancement MAKE_A_STONE = buildBase(MAKE_A_CLAY_KILN, "make_a_stone").display(display().x(1F).withAdvancementFrame(AdvancementFrame.SQUIRCLE).icon(Material.STONE))
			.visibilityRule(parentGranted())
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.STONE))))
			.buildAndRegister();
	public static final IAdvancement GET_A_CLAY_BUCKET = buildBase(GET_A_CLAY_BALL, "get_a_clay_bucket")
		.display(display().xy(0.5F, 1F).withAdvancementFrame(AdvancementFrame.CIRCLE).icon(BucketModifier.BucketType.CLAY.save(ItemStack.of(Material.BUCKET))))
		.requiredProgress(vanilla(
			inventoryChanged()
				.withItems(ItemTriggerCondition.of(Material.BUCKET)
					.withRawComponents(
						"""
						{
							"components": {
								"minecraft:custom_data": {
									"bucket_type": "CLAY"
								}
							}
						}
						"""
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement BURN_A_CLAY_BUCKET = buildBase(GET_A_CLAY_BUCKET, "burn_a_clay_bucket")
		.display(display().x(1F).withAdvancementFrame(AdvancementFrame.CIRCLE).icon(ScrapModifier.makeScrap(BucketModifier.BucketType.CERAMIC.save(ItemStack.of(Material.BUCKET)))))
		.buildAndRegister();
//	public static final IAdvancement MAKE_A_STONE_TOOL = buildBase(MAKE_A_STONE, "make_a_stone_tool").display(display().xy(1F, 0F).goalFrame().icon(Material.STONE_PICKAXE))
//			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.STONE_AXE, Material.STONE_PICKAXE, Material.STONE_HOE, Material.STONE_SHOVEL, Material.STONE_SWORD))))
//			.buildAndRegister();
//	public static final IAdvancement MOAR_TOOLS = buildBase(MAKE_A_STONE_TOOL, "moar_tools").display(display().xy(0.5F, 1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.STONE_SHOVEL))
//			.requiredProgress(vanilla(
//				inventoryChanged().withItems(ItemTriggerCondition.of(Material.STONE_AXE)),
//				inventoryChanged().withItems(ItemTriggerCondition.of(Material.STONE_PICKAXE)),
//				inventoryChanged().withItems(ItemTriggerCondition.of(Material.STONE_HOE)),
//				inventoryChanged().withItems(ItemTriggerCondition.of(Material.STONE_SHOVEL)),
//				inventoryChanged().withItems(ItemTriggerCondition.of(Material.STONE_SWORD)))
//			)
//			.buildAndRegister();
	public static final IAdvancement GET_A_BRICK = buildBase(MAKE_A_FIRE_FILLER, "get_a_brick").display(display().xy(1F, 0.55F).icon(Material.BRICK))
		.withReward(rewards().addItems(ItemStack.of(Material.BRICK, 8)))
		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.BRICK))))
		.buildAndRegister();
	public static final IAdvancement MAKE_A_POT = buildBase(GET_A_BRICK, "make_a_pot").display(display().x(1F).withAdvancementFrame(AdvancementFrame.BLOCK).icon(Material.FLOWER_POT))
		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.FLOWER_POT))))
		.buildAndRegister();
//	public static final IAdvancement GET_LEAVES = buildBase(MAKE_FLINT_SHEARS, "get_leaves").display(display().xy(0.5F, -1.1F).withAdvancementFrame(AdvancementFrame.CIRCLE).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.OAK_LEAVES))
//		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Tag.LEAVES))))
//		.buildAndRegister();
//	public static final IAdvancement GET_A_DEAD_BUSH = buildBase(MAKE_FLINT_SHEARS, "get_a_dead_bush").display(display().xy(0.5F, -2.1F).withAdvancementFrame(AdvancementFrame.CIRCLE).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.DEAD_BUSH))
//		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.DEAD_BUSH))))
//		.buildAndRegister();
	public static final IAdvancement FLOWERS_FOR_YOU = buildBase(BASICS_ROOT, "flowers_for_you").display(display().x(-1F).withAdvancementFrame(AdvancementFrame.CIRCLE).fancyDescriptionParent(NamedTextColor.GREEN).icon(TrappedNewbieItems.FLOWER_BOUQUET))
		.visibilityRule(ifDone(MAKE_A_WORK_STATION))
		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.FLOWER_BOUQUET))))
		.buildAndRegister();

	private static final IAdvancement GET_A_FLAKED_FLINT_TO_MAKE_A_TWINE_LINKER = buildLinking(GET_A_FLAKED_FLINT, MAKE_A_TWINE).visibilityRule(ifVisible(false, MAKE_FLINT_SHEARS)).buildAndRegister();
	private static final IAdvancement MAKE_FLINT_SHEARS_TO_GET_A_BRANCH_LINKER = buildLinking(MAKE_FLINT_SHEARS, GET_A_BRANCH).buildAndRegister();

	public static final AdvancementTab ADVENTURE_TAB = buildTab("adventure", MANAGER)
		.display(simpleTabDisplay().inverseY().display(display().backgroundPathTexture("block/sandstone_top").icon(Material.MAP)))
		.build();
	public static final IAdvancement ADVENTURE_ROOT = buildBase(ADVENTURE_TAB, "visual_root")
		.display(display().withAdvancementFrame(AdvancementFrame.SQUIRCLE).fancyDescriptionParent(GRAY).icon(Material.MAP))
		.visibilityRule(ifDone(false, FIRST_POSSESSION))
		.requiredProgress(alwaysDone())
		.buildAndRegister();
	public static final IAdvancement FIND_A_BROKEN_NETHER_PORTAL = buildBase(ADVENTURE_ROOT, "find_a_broken_nether_portal")
		.display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.CRYING_OBSIDIAN))
		.requiredProgress(vanilla(
			location()
				.withLocation(loc -> loc
					.withDimension(World.Environment.NORMAL)
					.withStructure(
						RegistryAccess.registryAccess().getRegistry(RegistryKey.STRUCTURE).stream()
							.filter(structure -> {
								NamespacedKey key = RegistryAccess.registryAccess().getRegistry(RegistryKey.STRUCTURE).getKeyOrThrow(structure);
								return NamespacedKey.MINECRAFT.equals(key.namespace()) && key.value().startsWith("ruined_portal");
							})
							.toList()
					)
				)
				.withPlayer(player -> player.inverted().withGameModes(GameMode.SPECTATOR))
		))
		.buildAndRegister();
	public static final IAdvancement FIND_A_DESERT_PYRAMID = buildBase(FIND_A_BROKEN_NETHER_PORTAL, "find_a_desert_pyramid")
		.display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.CHISELED_SANDSTONE))
		.requiredProgress(vanilla(
			location()
				.withLocation(loc -> loc
					.withStructure(Structure.DESERT_PYRAMID)
				)
				.withPlayer(player -> player.inverted().withGameModes(GameMode.SPECTATOR))
		))
		.buildAndRegister();
	public static final IAdvancement GET_A_BRUSH = buildBase(FIND_A_DESERT_PYRAMID, "get_a_brush")
		.display(display().xy(0.5F, 1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.BRUSH))
		.requiredProgress(vanilla(
			inventoryChanged().withItems(ItemTriggerCondition.of(Material.BRUSH))
		))
		.buildAndRegister();
	public static final IAdvancement GET_A_SNIFFER_EGG = buildBase(GET_A_BRUSH, "get_a_sniffer_egg")
		.display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.SNIFFER_EGG))
		.requiredProgress(vanilla(
			inventoryChanged().withItems(ItemTriggerCondition.of(Material.SNIFFER_EGG))
		))
		.buildAndRegister();
	public static final IAdvancement GET_A_SNIFFER_SEED = buildBase(GET_A_SNIFFER_EGG, "get_a_sniffer_seed")
		.display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.PITCHER_POD))
		.requiredProgress(vanilla(
			inventoryChanged().withItems(ItemTriggerCondition.of(Material.TORCHFLOWER_SEEDS, Material.PITCHER_POD))
		))
		.buildAndRegister();
	public static final IAdvancement BREED_SNIFFERS = buildBase(GET_A_SNIFFER_SEED, "breed_sniffers")
		.display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.TORCHFLOWER_SEEDS))
		.withReward(rewards().addItems(ItemStack.of(Material.TORCHFLOWER_SEEDS, 4)))
		.requiredProgress(vanilla(
			bredAnimals()
				.withParent(entity -> entity.withEntityType(EntityType.SNIFFER))
				.withPartner(entity -> entity.withEntityType(EntityType.SNIFFER))
		))
		.buildAndRegister();
	public static final IAdvancement FEED_A_SNIFFLET = buildBase(BREED_SNIFFERS, "feed_a_snifflet")
		.display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.SNIFFER_SPAWN_EGG))
		.requiredProgress(vanilla(
			playerInteractedWithEntity()
				.withEntity(entity -> entity
					.withState(EntityStateTriggerCondition::baby)
					.withEntityType(EntityType.SNIFFER)
				)
				.withItem(ItemTriggerCondition.of(Tag.ITEMS_SNIFFER_FOOD))
		))
		.buildAndRegister();
	public static final IAdvancement PLANT_A_SNIFFER_SEED = buildBase(FEED_A_SNIFFLET, "plant_a_sniffer_seed")
		.display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.TORCHFLOWER))
		.requiredProgress(vanillaAny(
			placedBlock().withBlock(Material.PITCHER_CROP),
			placedBlock().withBlock(Material.TORCHFLOWER_CROP)
		))
		.buildAndRegister();
	public static final IAdvancement WATER_A_FLOWER_POT = buildBase(PLANT_A_SNIFFER_SEED, "water_a_flower_pot")
		.display(display().x(1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(ItemUtil.glint(Material.ALLIUM)))
		.withReward(rewards().withExp(25))
		.buildAndRegister();
	public static final IAdvancement GET_A_STACK_OF_SNIFFER_PLANTS = buildBase(WATER_A_FLOWER_POT, "get_a_stack_of_sniffer_plants")
		.display(display().x(1F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(ItemUtil.glint(Material.PITCHER_PLANT)))
		.withReward(rewards().withExp(110).addItems(ItemStack.of(Material.SNIFFER_EGG, 4)))
		.requiredProgress(vanilla(
			inventoryChanged(Material.TORCHFLOWER.key().value()).withItems(ItemTriggerCondition.of(Material.TORCHFLOWER).withMinAmount(Material.TORCHFLOWER.getMaxStackSize())),
			inventoryChanged(Material.PITCHER_PLANT.key().value()).withItems(ItemTriggerCondition.of(Material.PITCHER_PLANT).withMinAmount(Material.PITCHER_PLANT.getMaxStackSize()))
		))
		.buildAndRegister();
	public static final IAdvancement GET_A_STACK_OF_SNIFFER_EGGS = buildBase(GET_A_STACK_OF_SNIFFER_PLANTS, "get_a_stack_of_sniffer_eggs")
		.display(display().x(1F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(ItemUtil.glint(Material.SUGAR)))
		.withReward(rewards()
			.withExp(200)
			.addItems(ItemStack.of(Material.SNIFFER_EGG, 16))
			.withTrophy(ItemStack.of(Material.SUGAR))
		)
		.requiredProgress(vanilla(
			inventoryChanged().withItems(ItemTriggerCondition.of(Material.SNIFFER_EGG).withMinAmount(Material.SNIFFER_EGG.getMaxStackSize()))
		))
		.buildAndRegister();
	public static final IAdvancement GET_A_POTTERY_SHERD = buildBase(GET_A_BRUSH, "get_a_pottery_sherd")
		.display(display().xy(1F, 1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.SUSPICIOUS_SAND))
		.requiredProgress(vanilla(
			inventoryChanged().withItems(ItemTriggerCondition.of(Tag.ITEMS_DECORATED_POT_SHERDS))
		))
		.buildAndRegister();
	public static final IAdvancement CRAFT_A_DECORATED_POT_FROM_SHERDS = buildBase(GET_A_POTTERY_SHERD, "craft_a_decorated_pot_from_sherds")
		.display(display().x(1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(() -> {
			var item = ItemStack.of(Material.DECORATED_POT);
			item.setData(DataComponentTypes.POT_DECORATIONS, PotDecorations.potDecorations(ItemType.BRICK, ItemType.HEART_POTTERY_SHERD, ItemType.BRICK, ItemType.EXPLORER_POTTERY_SHERD));
			return item;
		}))
		.withReward(rewards().withExp(50))
		.requiredProgress(vanilla(
			recipeCrafted(NamespacedKey.minecraft("decorated_pot"))
				.withIngredients(
					ItemTriggerCondition.of(Tag.ITEMS_DECORATED_POT_SHERDS),
					ItemTriggerCondition.of(Tag.ITEMS_DECORATED_POT_SHERDS),
					ItemTriggerCondition.of(Tag.ITEMS_DECORATED_POT_SHERDS),
					ItemTriggerCondition.of(Tag.ITEMS_DECORATED_POT_SHERDS)
				)
		))
		.buildAndRegister();
	public static final IAdvancement COLLECT_ALL_POTTERY_SHERDS = buildBase(CRAFT_A_DECORATED_POT_FROM_SHERDS, "collect_all_pottery_sherds")
		.display(display().x(1F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(Material.PRIZE_POTTERY_SHERD))
		.withReward(rewards()
			.withExp(200)
			.addItems(() -> {
				var item = ItemStack.of(Material.BROWN_BUNDLE);
				item.setData(DataComponentTypes.BUNDLE_CONTENTS, BundleContents.bundleContents(
					Tag.ITEMS_DECORATED_POT_SHERDS.getValues().stream().map(ItemStack::of).toList()
				));
				return item;
			})
			.withTrophy(ItemStack.of(Material.YELLOW_GLAZED_TERRACOTTA))
			.withExtraMessage(p -> Messenger.messenger(p).getMessage("advancement.reward.pottery_sherds"))
		)
		.buildAndRegister(CollectAllPotterySherdsAdvancement::new);
	public static final IAdvancement COLLECT_A_STACK_OF_POTTERY_SHERDS = buildBase(COLLECT_ALL_POTTERY_SHERDS, "collect_a_stack_of_pottery_sherds")
		.display(display().x(1F).challengeFrame().fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).icon(ItemUtil.glint(Material.ARMS_UP_POTTERY_SHERD)))
		.withReward(rewards()
			.withExp(700)
			.withTrophy(ItemStack.of(Material.ANGLER_POTTERY_SHERD))
		)
		.buildAndRegister(CollectAStackOfPotterySherdsAdvancement::new);
	public static final IAdvancement BREAK_A_SUSPICIOUS_BLOCK = buildBase(COLLECT_A_STACK_OF_POTTERY_SHERDS, "break_a_suspicious_block")
		.display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(ItemUtil.glint(Material.WOODEN_SHOVEL)))
		.withReward(rewards().withExp(20))
		.buildAndRegister();
	public static final IAdvancement GET_A_SUSPICIOUS_BLOCK = buildBase(BREAK_A_SUSPICIOUS_BLOCK, "get_a_suspicious_block")
		.display(display().x(1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.SUSPICIOUS_GRAVEL))
		.withReward(rewards().withExp(50))
		.requiredProgress(vanilla(
			inventoryChanged().withItems(ItemTriggerCondition.of(Material.SUSPICIOUS_SAND, Material.SUSPICIOUS_GRAVEL))
		))
		.buildAndRegister();
	public static final IAdvancement BREAK_A_STACK_OF_SUSPICIOUS_BLOCKS = buildBase(GET_A_SUSPICIOUS_BLOCK, "break_a_stack_of_suspicious_blocks")
		.display(display().x(1F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(ItemUtil.glint(Material.SUSPICIOUS_GRAVEL)))
		.withReward(rewards()
			.withExp(110)
			.withTrophy(ItemStack.of(Material.WOODEN_SHOVEL))
		)
		.requiredProgress(simple(Material.SUSPICIOUS_SAND.getMaxStackSize()))
		.buildAndRegister();
	public static final IAdvancement GET_A_STACK_OF_SUSPICIOUS_BLOCKS = buildBase(BREAK_A_STACK_OF_SUSPICIOUS_BLOCKS, "get_a_stack_of_suspicious_blocks")
		.display(display().x(1F).challengeFrame().fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).icon(Material.SUSPICIOUS_SAND))
		.withReward(rewards()
			.withExp(505)
			.withTrophy(ItemStack.of(Material.IRON_TRAPDOOR))
		)
		.requiredProgress(vanilla(
			inventoryChanged(Material.SUSPICIOUS_SAND.key().value()).withItems(ItemTriggerCondition.of(Material.SUSPICIOUS_SAND).withMinAmount(Material.SUSPICIOUS_SAND.getMaxStackSize())),
			inventoryChanged(Material.SUSPICIOUS_GRAVEL.key().value()).withItems(ItemTriggerCondition.of(Material.SUSPICIOUS_GRAVEL).withMinAmount(Material.SUSPICIOUS_GRAVEL.getMaxStackSize()))
		))
		.buildAndRegister();
	public static final IAdvancement FIND_TRAIL_RUINS = buildBase(GET_A_BRUSH, "find_trail_ruins")
		.display(display().xy(1F, 2F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.WHITE_GLAZED_TERRACOTTA))
		.requiredProgress(vanilla(
			location()
				.withLocation(loc -> loc
					.withStructure(Structure.TRAIL_RUINS)
				)
				.withPlayer(player -> player.inverted().withGameModes(GameMode.SPECTATOR))
		))
		.buildAndRegister();
	public static final IAdvancement GET_A_TRIM_SMITHING_TEMPLATE = buildBase(FIND_TRAIL_RUINS, "get_a_trim_smithing_template")
		.display(display().x(1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE))
		.withReward(rewards().withExp(50))
		.requiredProgress(vanilla(
			inventoryChanged().withItems(ItemTriggerCondition.of(Material.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE, Material.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, Material.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, Material.HOST_ARMOR_TRIM_SMITHING_TEMPLATE))
		))
		.buildAndRegister();
	public static final IAdvancement GET_A_MUSIC_DISC_RELIC = buildBase(GET_A_TRIM_SMITHING_TEMPLATE, "get_a_music_disc_relic")
		.display(display().x(1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.MUSIC_DISC_RELIC))
		.withReward(rewards().withExp(50))
		.requiredProgress(vanilla(
			inventoryChanged().withItems(ItemTriggerCondition.of(Material.MUSIC_DISC_RELIC))
		))
		.buildAndRegister();
	public static final IAdvancement GET_A_SMITHING_TEMPLATE = buildBase(FIND_A_DESERT_PYRAMID, "get_a_smithing_template")
		.display(display().x(1.5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE))
		.requiredProgress(vanilla(
			inventoryChanged().withItems(ItemTriggerCondition.of(UtilizerTags.SMITHING_TEMPLATES))
		))
		.buildAndRegister();
	public static final IAdvancement GET_AN_EYE_ARMOR_TRIM_SMITHING_TEMPLATE = buildBase(GET_A_SMITHING_TEMPLATE, "get_an_eye_armor_trim_smithing_template")
		.display(display().x(1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.EYE_ARMOR_TRIM_SMITHING_TEMPLATE))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.ENDER_PEARL, 4)))
		.requiredProgress(vanilla(
			inventoryChanged().withItems(ItemTriggerCondition.of(Material.EYE_ARMOR_TRIM_SMITHING_TEMPLATE))
		))
		.buildAndRegister();
	public static final IAdvancement GET_A_VEX_ARMOR_TRIM_SMITHING_TEMPLATE = buildBase(GET_AN_EYE_ARMOR_TRIM_SMITHING_TEMPLATE, "get_a_vex_armor_trim_smithing_template")
		.display(display().x(1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.VEX_ARMOR_TRIM_SMITHING_TEMPLATE))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.TOTEM_OF_UNDYING)))
		.requiredProgress(vanilla(
			inventoryChanged().withItems(ItemTriggerCondition.of(Material.VEX_ARMOR_TRIM_SMITHING_TEMPLATE))
		))
		.buildAndRegister();
	public static final IAdvancement GET_A_SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE = buildBase(GET_A_VEX_ARMOR_TRIM_SMITHING_TEMPLATE, "get_a_silence_armor_trim_smithing_template")
		.display(display().x(1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.ECHO_SHARD, 6)))
		.requiredProgress(vanilla(
			inventoryChanged().withItems(ItemTriggerCondition.of(Material.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE))
		))
		.buildAndRegister();
	public static final IAdvancement WEAR_A_SILENT_ARMOR = buildBase(GET_A_SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, "wear_a_silent_armor")
		.display(display().x(1F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(ItemUtil.glint(ItemStack.of(Material.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE))))
		.withReward(rewards()
			.withExp(90)
			.withTrophy(ItemStack.of(Material.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE))
		)
		.buildAndRegister(WearSilentArmorAdvancement::new);
	public static final IAdvancement APPLY_ALL_SMITHING_TEMPLATES = buildBase(WEAR_A_SILENT_ARMOR, "apply_all_smithing_templates")
		.display(display().x(1F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(Material.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE))
		.withReward(rewards()
			.withExp(150)
			.addItems(() -> {
				var item = ItemStack.of(Material.GRAY_SHULKER_BOX);
				item.setData(DataComponentTypes.CONTAINER, ItemContainerContents.containerContents(
					UtilizerTags.SMITHING_TEMPLATES.getValues().stream().filter(type -> type != Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE).map(ItemStack::of).toList()
				));
				return item;
			})
			.withTrophy(ItemStack.of(Material.LEATHER_CHESTPLATE).withColor(Color.fromRGB(12200224)))
			.withExtraMessage(p -> Messenger.messenger(p).getMessage("advancement.reward.smithing_templates"))
		)
		.buildAndRegister(ApplyAllSmithingTemplatesAdvancement::new);
	public static final IAdvancement GET_A_STACK_OF_ALL_SMITHING_TEMPLATES = buildBase(APPLY_ALL_SMITHING_TEMPLATES, "get_a_stack_of_all_smithing_templates")
		.display(display().x(1F).withAdvancementFrame(AdvancementFrame.STAR).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).icon(Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE))
		.withReward(rewards().withExp(833).addItems(ItemStack.of(Material.DIAMOND_BLOCK, 16)))
		.buildAndRegister(GetAStackOfAllSmithingTemplatesAdvancement::new);
	public static final IAdvancement TRIM_WITH_ANY_ARMOR_PATTERN = buildBase(FIND_A_DESERT_PYRAMID, "trim_with_any_armor_pattern")
		.display(display().xy(0.5F, -1.5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(() -> {
			var item = ItemStack.of(Material.IRON_HELMET);
			item.setData(DataComponentTypes.TRIM, ItemArmorTrim.itemArmorTrim(
				new ArmorTrim(TrimMaterial.LAPIS, TrimPattern.SENTRY)
			).build());
			return item;
		}))
		.requiredProgress(vanillaAny(
			UtilizerTags.SMITHING_TEMPLATES.getValues().stream()
				.map(type -> recipeCrafted(type.getKey().value(), new NamespacedKey(type.key().namespace(), type.key().value() + "_smithing_trim")))
				.toList()
		))
		.buildAndRegister();
	public static final IAdvancement GET_A_LEATHER_DIAMOND_CHESTPLATE = buildBase(TRIM_WITH_ANY_ARMOR_PATTERN, "get_a_leather_diamond_chestplate")
		.display(display().xy(1F, 0.5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(ItemStack.of(Material.LEATHER_CHESTPLATE).withColor(DyeColor.LIGHT_BLUE)))
		.withReward(rewards().withExp(20))
		.requiredProgress(vanilla(inventoryChanged()
			.withPlayer(player -> player
				.withEquipment(equipment -> equipment
					.withChestplate(
						ItemTriggerCondition.of(Material.LEATHER_CHESTPLATE)
							.withRawComponents(
								"""
								{
									"components": {
										"minecraft:dyed_color": %s,
										"minecraft:custom_name": "%s"
									}
								}
								""".formatted(DyeColor.LIGHT_BLUE.getColor().asRGB(), "<lang:item.minecraft.diamond_chestplate>")
							)
					)
				)
			)
		))
		.buildAndRegister();
	public static final IAdvancement GET_A_LEATHER_COPPER_ARMOR = buildBase(GET_A_LEATHER_DIAMOND_CHESTPLATE, "get_a_leather_copper_armor")
		.display(display().x(1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(() -> {
			var item = ItemStack.of(Material.LEATHER_CHESTPLATE);
			item.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(DyeColor.ORANGE.getColor()));
			item.setData(DataComponentTypes.TRIM, ItemArmorTrim.itemArmorTrim(new ArmorTrim(TrimMaterial.COPPER, TrimPattern.BOLT)).build());
			return item;
		}))
		.withReward(rewards().withExp(60).addItems(ItemStack.of(Material.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE)))
		.buildAndRegister(GetALeatherCopperArmorAdvancement::new);
	public static final IAdvancement GET_A_LEATHER_NETHERITE_ARMOR = buildBase(GET_A_LEATHER_COPPER_ARMOR, "get_a_leather_netherite_armor")
		.display(display().x(1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(() -> {
			var item = ItemStack.of(Material.LEATHER_CHESTPLATE);
			item.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(DyeColor.BLACK.getColor()));
			item.setData(DataComponentTypes.TRIM, ItemArmorTrim.itemArmorTrim(new ArmorTrim(TrimMaterial.NETHERITE, TrimPattern.SILENCE)).build());
			return item;
		}))
		.withReward(rewards().withExp(80).addItems(ItemStack.of(Material.BLACK_DYE, 8)))
		.buildAndRegister(GetALeatherNetheriteArmorAdvancement::new);
	public static final IAdvancement GET_A_LEATHER_EMERALD_ARMOR = buildBase(GET_A_LEATHER_NETHERITE_ARMOR, "get_a_leather_emerald_armor")
		.display(display().x(1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(() -> {
			var item = ItemStack.of(Material.LEATHER_CHESTPLATE);
			item.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(DyeColor.LIME.getColor()));
			item.setData(DataComponentTypes.TRIM, ItemArmorTrim.itemArmorTrim(new ArmorTrim(TrimMaterial.EMERALD, TrimPattern.SILENCE)).build());
			return item;
		}))
		.withReward(rewards().withExp(60).addItems(ItemStack.of(Material.EMERALD, 8)))
		.buildAndRegister(GetALeatherEmeraldArmorAdvancement::new);
	public static final IAdvancement DUPLICATE_A_SMITHING_TEMPLATE = buildBase(TRIM_WITH_ANY_ARMOR_PATTERN, "duplicate_a_smithing_template")
		.display(display().xy(1F, -0.5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(ItemStack.of(Material.DIAMOND)))
		.withReward(rewards().addItems(ItemStack.of(Material.DIAMOND, 8)))
		.requiredProgress(vanillaAny(
			UtilizerTags.SMITHING_TEMPLATES.getValues().stream()
				.map(type -> recipeCrafted(type.getKey().value(), type.getKey()))
				.toList()
		))
		.buildAndRegister();
	public static final IAdvancement WEAR_ARMOR_WITH_SAME_TRIMS = buildBase(DUPLICATE_A_SMITHING_TEMPLATE, "wear_armor_with_same_trims")
		.display(display().x(1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(() -> {
			var item = ItemStack.of(Material.DIAMOND_BOOTS);
			item.setData(DataComponentTypes.TRIM, ItemArmorTrim.itemArmorTrim(new ArmorTrim(TrimMaterial.REDSTONE, TrimPattern.SENTRY)).build());
			return item;
		}))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.DIAMOND, 8)))
		.buildAndRegister(WearArmorWithSameTrimsAdvancement::new);
	public static final IAdvancement WEAR_A_REAL_NETHERITE_ARMOR = buildBase(WEAR_ARMOR_WITH_SAME_TRIMS, "wear_a_real_netherite_armor")
		.display(display().x(1F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(() -> {
			var item = ItemStack.of(Material.NETHERITE_CHESTPLATE);
			item.setData(DataComponentTypes.TRIM, ItemArmorTrim.itemArmorTrim(new ArmorTrim(TrimMaterial.NETHERITE, TrimPattern.SILENCE)).build());
			return item;
		}))
		.withReward(rewards()
			.withExp(125)
			.addItems(ItemStack.of(Material.NETHERITE_SCRAP, 2))
			.withTrophy(ItemStack.of(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE))
		)
		.buildAndRegister(WearARealNetheriteArmorAdvancement::new);
	public static final IAdvancement TRIM_WITH_ALL_MATERIALS = buildBase(WEAR_A_REAL_NETHERITE_ARMOR, "trim_with_all_materials")
		.display(display().x(1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(() -> {
			var item = ItemStack.of(Material.NETHERITE_CHESTPLATE);
			item.setData(DataComponentTypes.TRIM, ItemArmorTrim.itemArmorTrim(new ArmorTrim(TrimMaterial.GOLD, TrimPattern.SENTRY)).build());
			return item;
		}))
		.withReward(rewards()
			.withExp(100)
			.addItems(ItemStack.of(Material.DIAMOND, 18))
			.withTrophy(ItemStack.of(Material.LEATHER_CHESTPLATE))
		)
		.requiredProgress(vanilla(
			RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL).stream()
				.filter(trimMaterial -> NamespacedKey.MINECRAFT.equals(RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL).getKeyOrThrow(trimMaterial).namespace()))
				.map(trimMaterial ->
					inventoryChanged(RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL).getKeyOrThrow(trimMaterial).value())
						.withItems(ItemTriggerCondition.builder()
							.withRawComponents(
								"""
								{
									"predicates": {
										"minecraft:trim": {
											"material": "%s"
										}
									}
								}
								""".formatted(RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL).getKeyOrThrow(trimMaterial))
							)
						)
				)
				.toList()
		))
		.buildAndRegister();
	public static final IAdvancement OBTAIN_EVERY_ARMOR_TRIM_WITH_EVERY_MATERIAL = buildMulti(GET_A_LEATHER_EMERALD_ARMOR, "obtain_every_armor_trim_with_every_material", TRIM_WITH_ALL_MATERIALS)
		.display(display().xy(1F, -0.5F).withAdvancementFrame(AdvancementFrame.BUTTERFLY).fancyDescriptionParent(NamedTextColor.BLACK).superTorture().icon(() -> {
			var item = ItemStack.of(Material.TURTLE_HELMET);
			item.setData(DataComponentTypes.TRIM, ItemArmorTrim.itemArmorTrim(new ArmorTrim(TrimMaterial.NETHERITE, TrimPattern.SILENCE)));
			return item;
		}))
		.requiredProgress(requirements("turtle", "leather", "chainmail", "copper", "iron", "golden", "diamond", "netherite"))
		.buildAndRegister();
	public static final IAdvancement OBTAIN_EVERY_TURTLE_ARMOR_TRIM = buildBase(OBTAIN_EVERY_ARMOR_TRIM_WITH_EVERY_MATERIAL, "obtain_every_turtle_armor_trim")
		.display(display().xy(1F, 0.5F).withAdvancementFrame(AdvancementFrame.STAR).fancyDescriptionParent(NamedTextColor.DARK_RED).torture().icon(Material.TURTLE_SCUTE))
		.buildAndRegister(b -> new ObtainEveryArmorTrimAdvancement(b, List.of(Material.TURTLE_SCUTE)));
	public static final IAdvancement OBTAIN_EVERY_LEATHER_ARMOR_TRIM = buildBase(OBTAIN_EVERY_TURTLE_ARMOR_TRIM, "obtain_every_leather_armor_trim")
		.display(display().x(1F).withAdvancementFrame(AdvancementFrame.STAR).fancyDescriptionParent(NamedTextColor.DARK_RED).torture().icon(Material.LEATHER))
		.buildAndRegister(b -> new ObtainEveryArmorTrimAdvancement(b, List.of(Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS)));
	public static final IAdvancement OBTAIN_EVERY_CHAINMAIL_ARMOR_TRIM = buildBase(OBTAIN_EVERY_LEATHER_ARMOR_TRIM, "obtain_every_chainmail_armor_trim")
		.display(display().x(1F).withAdvancementFrame(AdvancementFrame.STAR).fancyDescriptionParent(NamedTextColor.DARK_RED).torture().icon(Material.IRON_CHAIN))
		.buildAndRegister(b -> new ObtainEveryArmorTrimAdvancement(b, List.of(Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS)));
	public static final IAdvancement OBTAIN_EVERY_COPPER_ARMOR_TRIM = buildBase(OBTAIN_EVERY_CHAINMAIL_ARMOR_TRIM, "obtain_every_copper_armor_trim")
		.display(display().x(1F).withAdvancementFrame(AdvancementFrame.STAR).fancyDescriptionParent(NamedTextColor.DARK_RED).torture().icon(Material.COPPER_INGOT))
		.buildAndRegister(b -> new ObtainEveryArmorTrimAdvancement(b, List.of(Material.COPPER_HELMET, Material.COPPER_CHESTPLATE, Material.COPPER_LEGGINGS, Material.COPPER_BOOTS)));
	public static final IAdvancement OBTAIN_EVERY_IRON_ARMOR_TRIM = buildBase(OBTAIN_EVERY_ARMOR_TRIM_WITH_EVERY_MATERIAL, "obtain_every_iron_armor_trim")
		.display(display().xy(1F, -0.5F).withAdvancementFrame(AdvancementFrame.STAR).fancyDescriptionParent(NamedTextColor.DARK_RED).torture().icon(Material.IRON_INGOT))
		.buildAndRegister(b -> new ObtainEveryArmorTrimAdvancement(b, List.of(Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS)));
	public static final IAdvancement OBTAIN_EVERY_GOLDEN_ARMOR_TRIM = buildBase(OBTAIN_EVERY_IRON_ARMOR_TRIM, "obtain_every_golden_armor_trim")
		.display(display().x(1F).withAdvancementFrame(AdvancementFrame.STAR).fancyDescriptionParent(NamedTextColor.DARK_RED).torture().icon(Material.GOLD_INGOT))
		.buildAndRegister(b -> new ObtainEveryArmorTrimAdvancement(b, List.of(Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS)));
	public static final IAdvancement OBTAIN_EVERY_DIAMOND_ARMOR_TRIM = buildBase(OBTAIN_EVERY_GOLDEN_ARMOR_TRIM, "obtain_every_diamond_armor_trim")
		.display(display().x(1F).withAdvancementFrame(AdvancementFrame.STAR).fancyDescriptionParent(NamedTextColor.DARK_RED).torture().icon(Material.DIAMOND))
		.buildAndRegister(b -> new ObtainEveryArmorTrimAdvancement(b, List.of(Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS)));
	public static final IAdvancement OBTAIN_EVERY_NETHERITE_ARMOR_TRIM = buildBase(OBTAIN_EVERY_DIAMOND_ARMOR_TRIM, "obtain_every_netherite_armor_trim")
		.display(display().x(1F).withAdvancementFrame(AdvancementFrame.STAR).fancyDescriptionParent(NamedTextColor.DARK_RED).torture().icon(Material.NETHERITE_INGOT))
		.buildAndRegister(b -> new ObtainEveryArmorTrimAdvancement(b, List.of(Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS)));
	public static final IAdvancement FIND_A_JUNGLE_PYRAMID = buildBase(FIND_A_DESERT_PYRAMID, "find_a_jungle_pyramid")
		.display(display().xy(0.5F, -3F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.MOSSY_COBBLESTONE))
		.requiredProgress(vanilla(
			location()
				.withLocation(loc -> loc
					.withStructure(Structure.JUNGLE_PYRAMID)
				)
				.withPlayer(player -> player.inverted().withGameModes(GameMode.SPECTATOR))
		))
		.buildAndRegister();
	public static final IAdvancement FIND_A_SWAMP_HUT = buildBase(FIND_A_JUNGLE_PYRAMID, "find_a_swamp_hut")
		.display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.CAULDRON))
		.requiredProgress(vanilla(
			location()
				.withLocation(loc -> loc
					.withStructure(Structure.SWAMP_HUT)
				)
				.withPlayer(player -> player.inverted().withGameModes(GameMode.SPECTATOR))
		))
		.buildAndRegister();
	public static final IAdvancement FIND_AN_IGLOO = buildBase(FIND_A_SWAMP_HUT, "find_an_igloo")
		.display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.SNOW_BLOCK))
		.withReward(rewards().withExp(100))
		.requiredProgress(vanilla(
			location()
				.withLocation(loc -> loc
					.withStructure(Structure.IGLOO)
				)
				.withPlayer(player -> player.inverted().withGameModes(GameMode.SPECTATOR))
		))
		.buildAndRegister();
	public static final IAdvancement FIND_ALL_STRUCTURES = buildBase(FIND_AN_IGLOO, "find_all_structures")
		.display(display().x(1F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(Material.STRUCTURE_BLOCK))
		.withReward(rewards().withExp(100))
		.requiredProgress(vanilla(
			MiscUtil.combineToList(
				RegistryAccess.registryAccess().getRegistry(RegistryKey.STRUCTURE).stream()
					.filter(structure -> {
						NamespacedKey key = RegistryAccess.registryAccess().getRegistry(RegistryKey.STRUCTURE).getKeyOrThrow(structure);
						return NamespacedKey.MINECRAFT.equals(key.namespace())
							&& !key.value().equals("nether_fossil")
							&& !key.value().equals("buried_treasure")
							&& !key.value().startsWith("ruined_portal")
							&& !key.value().startsWith("shipwreck");
					})
					.map(structure ->
						location(RegistryAccess.registryAccess().getRegistry(RegistryKey.STRUCTURE).getKeyOrThrow(structure).value())
							.withLocation(loc -> loc
								.withStructure(structure)
							)
							.withPlayer(player -> player.inverted().withGameModes(GameMode.SPECTATOR)))
					.toList(),
				List.of(
					location("shipwreck")
						.withLocation(loc -> loc
							.withStructure(
								RegistryAccess.registryAccess().getRegistry(RegistryKey.STRUCTURE).stream()
									.filter(structure -> {
										NamespacedKey key = RegistryAccess.registryAccess().getRegistry(RegistryKey.STRUCTURE).getKeyOrThrow(structure);
										return NamespacedKey.MINECRAFT.equals(key.namespace()) && key.value().startsWith("shipwreck");
									})
									.toList()
							)
						)
						.withPlayer(player -> player.inverted().withGameModes(GameMode.SPECTATOR))
				),
				List.of(
					location("ruined_portal")
						.withLocation(loc -> loc
							.withStructure(
								RegistryAccess.registryAccess().getRegistry(RegistryKey.STRUCTURE).stream()
									.filter(structure -> {
										NamespacedKey key = RegistryAccess.registryAccess().getRegistry(RegistryKey.STRUCTURE).getKeyOrThrow(structure);
										return NamespacedKey.MINECRAFT.equals(key.namespace()) && key.value().startsWith("ruined_portal");
									})
									.toList()
							)
						)
						.withPlayer(player -> player.inverted().withGameModes(GameMode.SPECTATOR))
				),
				List.of(
					playerGeneratesContainerLoot("dungeon_loot_chest")
						.withLootTable(LootTables.SIMPLE_DUNGEON),
					playerGeneratesContainerLoot("buried_treasure")
						.withLootTable(LootTables.BURIED_TREASURE)
				)
			)
		))
		.buildAndRegister();
	public static final IAdvancement USE_A_BRUSH_IN_ALL_STRUCTURES = buildBase(FIND_ALL_STRUCTURES, "use_a_brush_in_all_structures")
		.display(display().x(1F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(ItemUtil.glint(Material.BRUSH)))
		.withReward(rewards()
			.withExp(250)
			.withTrophy(ItemStack.of(Material.BRUSH))
		)
		.requiredProgress(vanilla(
			MiscUtil.combineToList(
				RegistryAccess.registryAccess().getRegistry(RegistryKey.STRUCTURE).stream()
					.filter(structure -> {
						NamespacedKey key = RegistryAccess.registryAccess().getRegistry(RegistryKey.STRUCTURE).getKeyOrThrow(structure);
						return NamespacedKey.MINECRAFT.equals(key.namespace())
							&& !key.value().equals("nether_fossil")
							&& !key.value().startsWith("ruined_portal")
							&& !key.value().startsWith("shipwreck");
					})
					.map(structure ->
						itemUsedOnBlock(RegistryAccess.registryAccess().getRegistry(RegistryKey.STRUCTURE).getKeyOrThrow(structure).value())
							.withItem(ItemTriggerCondition.of(Material.BRUSH))
							.withLocation(loc -> loc
								.withStructure(structure)
							)
							.withPlayer(player -> player.inverted().withGameModes(GameMode.SPECTATOR)))
					.toList(),
				List.of(
					itemUsedOnBlock("shipwreck")
						.withItem(ItemTriggerCondition.of(Material.BRUSH))
						.withLocation(loc -> loc
							.withStructure(
								RegistryAccess.registryAccess().getRegistry(RegistryKey.STRUCTURE).stream()
									.filter(structure -> {
										NamespacedKey key = RegistryAccess.registryAccess().getRegistry(RegistryKey.STRUCTURE).getKeyOrThrow(structure);
										return NamespacedKey.MINECRAFT.equals(key.namespace()) && key.value().startsWith("shipwreck");
									})
									.toList()
							)
						)
						.withPlayer(player -> player.inverted().withGameModes(GameMode.SPECTATOR))
				),
				List.of(
					itemUsedOnBlock("ruined_portal")
						.withItem(ItemTriggerCondition.of(Material.BRUSH))
						.withLocation(loc -> loc
							.withStructure(
								RegistryAccess.registryAccess().getRegistry(RegistryKey.STRUCTURE).stream()
									.filter(structure -> {
										NamespacedKey key = RegistryAccess.registryAccess().getRegistry(RegistryKey.STRUCTURE).getKeyOrThrow(structure);
										return NamespacedKey.MINECRAFT.equals(key.namespace()) && key.value().startsWith("ruined_portal");
									})
									.toList()
							)
						)
						.withPlayer(player -> player.inverted().withGameModes(GameMode.SPECTATOR))
				),
				List.of(
					itemUsedOnBlock("dungeon_spawner")
						.withItem(ItemTriggerCondition.of(Material.BRUSH))
						.withLocation(loc -> loc
							.withBlock(block -> block.withBlocks(Material.SPAWNER))
						)
						.withPlayer(player -> player.inverted().withGameModes(GameMode.SPECTATOR))
				)
			)
		))
		.buildAndRegister();
	public static final IAdvancement BE_IN_TWO_STRUCTURES = buildBase(USE_A_BRUSH_IN_ALL_STRUCTURES, "be_in_two_structures")
		.display(display().x(1F).withAdvancementFrame(AdvancementFrame.STAR).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).icon(Material.JIGSAW))
		.withReward(rewards().withExp(150))
		.buildAndRegister();
	public static final IAdvancement GET_A_NAME_TAG = buildBase(ADVENTURE_ROOT, "get_a_name_tag")
		.display(display().xy(1F, -6F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.NAME_TAG))
		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.NAME_TAG))))
		.buildAndRegister();
	public static final IAdvancement NAME_A_RABBIT_TOAST = buildBase(GET_A_NAME_TAG, "name_a_rabbit_toast")
		.display(display().xy(1F, 0.5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.RABBIT_SPAWN_EGG))
		.withReward(rewards().addItems(ItemStack.of(Material.NAME_TAG), ItemStack.of(Material.RABBIT_FOOT)))
		.requiredProgress(vanilla(
			playerInteractedWithEntity()
				.withItem(ItemTriggerCondition.of(Material.NAME_TAG)
					.withRawComponents(
						"""
						{
							"components": {
								"minecraft:custom_name": "Toast"
							}
						}
						"""
					)
				)
				.withEntity(entity -> entity.withEntityType(EntityType.RABBIT))
		))
		.buildAndRegister();
	public static final IAdvancement TURN_A_MOB_UPSIDE_DOWN = buildBase(NAME_A_RABBIT_TOAST, "turn_a_mob_upside_down")
		.display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(DINNERBONE_HEAD))
		.withReward(rewards()
			.addItems(ItemStack.of(Material.NAME_TAG))
			.withTrophy(DINNERBONE_HEAD)
		)
		.requiredProgress(vanillaAny(
			playerInteractedWithEntity("dinnerbone")
				.withItem(ItemTriggerCondition.of(Material.NAME_TAG)
					.withRawComponents(
						"""
						{
							"components": {
								"minecraft:custom_name": "Dinnerbone"
							}
						}
						"""
					)
				)
				.withEntity(entity -> entity.inverted().withEntityType(UtilizerTags.NON_MOB_ENTITIES)),
			playerInteractedWithEntity("grumm")
				.withItem(ItemTriggerCondition.of(Material.NAME_TAG)
					.withRawComponents(
						"""
						{
							"components": {
								"minecraft:custom_name": "Grumm"
							}
						}
						"""
					)
				)
				.withEntity(entity -> entity.inverted().withEntityType(UtilizerTags.NON_MOB_ENTITIES))
		))
		.buildAndRegister();
	public static final IAdvancement NAME_A_SHEEP_JEB = buildBase(TURN_A_MOB_UPSIDE_DOWN, "name_a_sheep_jeb")
		.display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.MAGENTA_WOOL))
		.withReward(rewards()
			.addItems(ItemStack.of(Material.NAME_TAG))
			.withTrophy(JEB_HEAD)
		)
		.requiredProgress(vanilla(
			playerInteractedWithEntity()
				.withItem(ItemTriggerCondition.of(Material.NAME_TAG)
					.withRawComponents(
						"""
						{
							"components": {
								"minecraft:custom_name": "jeb_"
							}
						}
						"""
					)
				)
				.withEntity(entity -> entity.withEntityType(EntityType.SHEEP))
		))
		.buildAndRegister();
	public static final IAdvancement KILL_A_SLENDERMAN = buildBase(NAME_A_SHEEP_JEB, "kill_a_slenderman")
		.display(display().x(1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.ENDERMAN_SPAWN_EGG))
		.withReward(rewards().addItems(ItemStack.of(Material.NAME_TAG), ItemStack.of(Material.ENDER_PEARL, 3)))
		.requiredProgress(vanillaAny(
			LangOptionsStorage.getSupportedLanguages().stream()
				.map(langOptions -> Messenger.messenger(langOptions).getRawMessage("mob.name.slenderman")[0])
				.distinct()
				.map(name -> playerKilledEntity()
					.withEntity(entity -> entity
						.withEntityType(EntityType.ENDERMAN)
						.withNbt("{CustomName:\"%s\"}".formatted(name))
					)
				)
				.toList()
		))
		.buildAndRegister();
	public static final IAdvancement KILL_JOHNNY = buildBase(KILL_A_SLENDERMAN, "kill_johnny")
		.display(display().x(1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.VINDICATOR_SPAWN_EGG))
		.withReward(rewards().addItems(ItemStack.of(Material.NAME_TAG), ItemStack.of(Material.EMERALD, 3)))
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity
					.withEntityType(EntityType.VINDICATOR)
					.withNbt("{CustomName:\"Johnny\"}")
				)
		))
		.buildAndRegister();
	public static final IAdvancement NAME_A_PIGLIN_BRUTE_TECHNOBLADE = buildBase(KILL_JOHNNY, "name_a_piglin_brute_technoblade")
		.display(display().x(1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(TECHNOBLADE_HEAD))
		.withReward(rewards()
			.addItems(ItemStack.of(Material.NAME_TAG), ItemStack.of(Material.GOLDEN_SWORD))
			.withTrophy(TECHNOBLADE_HEAD)
		)
		.requiredProgress(vanilla(
			playerInteractedWithEntity()
				.withItem(ItemTriggerCondition.of(Material.NAME_TAG)
					.withRawComponents(
						"""
						{
							"components": {
								"minecraft:custom_name": "Technoblade"
							}
						}
						"""
					)
				)
				.withEntity(entity -> entity.withEntityType(EntityType.PIGLIN_BRUTE))
		))
		.buildAndRegister();
	public static final IAdvancement NAME_A_WARDEN_PLACEHOLDER = buildBase(NAME_A_PIGLIN_BRUTE_TECHNOBLADE, "name_a_warden_placeholder")
		.display(display().x(1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.SCULK_VEIN))
		.withReward(rewards().withExp(25))
		.requiredProgress(vanillaAny(
			LangOptionsStorage.getSupportedLanguages().stream()
				.map(langOptions -> Messenger.messenger(langOptions).getRawMessage("mob.name.placeholder")[0])
				.distinct()
				.map(name ->
					playerInteractedWithEntity()
						.withItem(ItemTriggerCondition.of(Material.NAME_TAG)
							.withRawComponents(
								"""
								{
									"components": {
										"minecraft:custom_name": "%s"
									}
								}
								""".formatted(name)
							)
						)
						.withEntity(entity -> entity.withEntityType(EntityType.WARDEN))
				)
				.toList()
		))
		.buildAndRegister();
	public static final IAdvancement NAME_A_CAT_KOMARU = buildBase(NAME_A_WARDEN_PLACEHOLDER, "name_a_cat_komaru")
		.display(display().x(1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(KOMARU_HEAD))
		.withReward(rewards()
			.withExp(8)
			.withTrophy(KOMARU_HEAD)
		)
		.requiredProgress(vanillaAny(
			LangOptionsStorage.getSupportedLanguages().stream()
				.map(langOptions -> Messenger.messenger(langOptions).getRawMessage("mob.name.komaru")[0])
				.distinct()
				.map(name ->
					playerInteractedWithEntity()
						.withItem(ItemTriggerCondition.of(Material.NAME_TAG)
							.withRawComponents(
								"""
								{
									"components": {
										"minecraft:custom_name": "%s"
									}
								}
								""".formatted(name)
							)
						)
						.withEntity(entity -> entity.withEntityType(EntityType.CAT))
				)
				.toList()
		))
		.buildAndRegister();
	public static final IAdvancement GET_A_JUKEBOX = buildBase(GET_A_NAME_TAG, "get_a_jukebox")
		.display(display().xy(1F, -0.5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.JUKEBOX))
		.withReward(rewards().addItems(ItemStack.of(Material.DIAMOND)))
		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.JUKEBOX))))
		.buildAndRegister();
	public static final IAdvancement PLAY_A_MUSIC_DISC = buildBase(GET_A_JUKEBOX, "play_a_music_disc")
		.display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.MUSIC_DISC_CAT))
		.requiredProgress(vanilla(
			itemUsedOnBlock()
				.withItem(ItemTriggerCondition.builder()
					.withRawComponents(
						"""
						{
							"predicates": {
								"minecraft:jukebox_playable": {}
							}
						}
						"""
					)
				)
				.withLocation(loc -> loc
					.withBlock(block -> block
						.withBlocks(Material.JUKEBOX)
						.withProperties(properties -> properties
							.withProperty("has_record", true)
						)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement PLAY_A_JUKEBOX_IN_MEADOWS = buildBase(PLAY_A_MUSIC_DISC, "play_a_jukebox_in_meadows")
		.display(display().x(1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.AZURE_BLUET))
		.requiredProgress(vanilla(
			itemUsedOnBlock()
				.withItem(ItemTriggerCondition.builder()
					.withRawComponents(
						"""
						{
							"predicates": {
								"minecraft:jukebox_playable": {}
							}
						}
						"""
					)
				)
				.withLocation(loc -> loc
					.withBiome(Biome.MEADOW)
					.withBlock(block -> block
						.withBlocks(Material.JUKEBOX)
						.withProperties(properties -> properties
							.withProperty("has_record", true)
						)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement PLAY_ALL_MUSIC_DISCS = buildBase(PLAY_A_JUKEBOX_IN_MEADOWS, "play_all_music_discs")
		.display(display().x(1F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(Material.MUSIC_DISC_CHIRP))
		.withReward(rewards().withExp(250))
		.requiredProgress(vanilla(
			MaterialTags.MUSIC_DISCS.getValues().stream()
				.map(type -> itemUsedOnBlock(type.key().value())
					.withItem(ItemTriggerCondition.of(type))
					.withLocation(loc -> loc
						.withBlock(block -> block
							.withBlocks(Material.JUKEBOX)
							.withProperties(properties -> properties
								.withProperty("has_record", true)
							)
						)
					))
				.toList()
		))
		.buildAndRegister();
	public static final IAdvancement GET_AN_ENCHANTED_GOLDEN_APPLE = buildBase(PLAY_ALL_MUSIC_DISCS, "get_an_enchanted_golden_apple")
		.display(display().x(1F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(Material.ENCHANTED_GOLDEN_APPLE))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.GOLD_INGOT, 4)))
		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.ENCHANTED_GOLDEN_APPLE))))
		.buildAndRegister();
	public static final IAdvancement EAT_A_STACK_OF_ENCHANTED_GOLDEN_APPLES = buildBase(GET_AN_ENCHANTED_GOLDEN_APPLE, "eat_a_stack_of_enchanted_golden_apples")
		.display(display().x(1F).withAdvancementFrame(AdvancementFrame.STAR).fancyDescriptionParent(NamedTextColor.DARK_RED).icon(Material.ENCHANTED_GOLDEN_APPLE))
		.withReward(rewards()
			.withTrophy(ItemStack.of(Material.ENCHANTED_GOLDEN_APPLE))
		)
		.buildAndRegister();
	public static final IAdvancement FIND_A_VILLAGE = buildBase(ADVENTURE_ROOT, "find_a_village")
		.display(display().xy(1F, 5.25F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.OAK_DOOR))
		.requiredProgress(vanilla(
			location()
				.withLocation(loc -> loc
					.withStructure(MiscUtil.getTagValues(StructureTagKeys.VILLAGE))
				)
				.withPlayer(player -> player.inverted().withGameModes(GameMode.SPECTATOR))
		))
		.buildAndRegister();
	public static final IAdvancement TRADE_WITH_A_VILLAGER = buildBase(FIND_A_VILLAGE, "trade_with_a_villager")
		.display(display().x(1.5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.EMERALD))
		.withReward(rewards().addItems(ItemStack.of(Material.EMERALD, 2)))
		.requiredProgress(vanilla(
			villagerTrade()
				.withEntity(entity -> entity.withEntityType(EntityType.VILLAGER))
		))
		.buildAndRegister();
	public static final IAdvancement GET_A_STACK_OF_EMERALDS = buildBase(TRADE_WITH_A_VILLAGER, "get_a_stack_of_emeralds")
		.display(display().y(-1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.EMERALD))
		.withReward(rewards().withExp(100).addItems(ItemStack.of(Material.EMERALD, 8)))
		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.EMERALD).withMinAmount(Material.EMERALD.getMaxStackSize()))))
		.buildAndRegister();
	public static final IAdvancement GET_A_STACK_OF_EMERALD_BLOCKS = buildBase(GET_A_STACK_OF_EMERALDS, "get_a_stack_of_emerald_blocks")
		.display(display().x(-1F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(Material.EMERALD_BLOCK))
		.withReward(rewards()
			.withExp(250)
			.addItems(ItemStack.of(Material.EMERALD_BLOCK, 4))
			.withTrophy(ItemStack.of(Material.EGG))
		)
		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.EMERALD_BLOCK).withMinAmount(Material.EMERALD_BLOCK.getMaxStackSize()))))
		.buildAndRegister();
	public static final IAdvancement TRADE_AN_ENCHANTED_BOOK = buildBase(TRADE_WITH_A_VILLAGER, "trade_an_enchanted_book")
		.display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.ENCHANTED_BOOK))
		.requiredProgress(vanilla(
			villagerTrade()
				.withItem(ItemTriggerCondition.of(Material.ENCHANTED_BOOK))
		))
		.buildAndRegister();
	public static final IAdvancement TRADE_AN_EXPERIENCE_BOTTLE = buildBase(TRADE_AN_ENCHANTED_BOOK, "trade_an_experience_bottle")
		.display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.EXPERIENCE_BOTTLE))
		.withReward(rewards().addItems(ItemStack.of(Material.EMERALD, 8)))
		.requiredProgress(vanilla(
			villagerTrade()
				.withItem(ItemTriggerCondition.of(Material.EXPERIENCE_BOTTLE))
		))
		.buildAndRegister();
	public static final IAdvancement TRADE_A_COOKIE_AT_MIDNIGHT = buildBase(TRADE_AN_EXPERIENCE_BOTTLE, "trade_a_cookie_at_midnight")
		.display(display().x(1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.COOKIE))
		.withReward(rewards()
			.withExp(70)
			.addItems(ItemStack.of(Material.COOKIE, 8))
			.withTrophy(ItemStack.of(Material.CLOCK))
		)
		.requiredProgress(vanilla(
			villagerTrade()
				.withItem(ItemTriggerCondition.of(Material.COOKIE))
				.withEntity(entity -> entity.withEntityType(EntityType.VILLAGER))
				.withPlayer(
					new TimeTriggerCondition(24_000L, MinMaxBoundsTriggerCondition.Ints.ofIntegers(21_000, 22_000))
				)
		))
		.buildAndRegister();
	public static final IAdvancement TRADE_BRICKS_IN_RIVER = buildBase(TRADE_A_COOKIE_AT_MIDNIGHT, "trade_bricks_in_river")
		.display(display().x(1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.BRICK))
		.withReward(rewards().withExp(70).addItems(ItemStack.of(Material.BRICK, 32)))
		.requiredProgress(vanilla(
			villagerTrade()
				.withItem(ItemTriggerCondition.of(Material.BRICK))
				.withEntity(entity -> entity
					.withEntityType(EntityType.VILLAGER)
					.withLocation(loc -> loc
						.withBiome(BiomeTags.RIVER)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement TRADE_TERRACOTTA_IN_BADLANDS = buildBase(TRADE_BRICKS_IN_RIVER, "trade_terracotta_in_badlands")
		.display(display().x(1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.WHITE_TERRACOTTA))
		.withReward(rewards().withExp(85).addItems(ItemStack.of(Material.TERRACOTTA, 32)))
		.requiredProgress(vanilla(
			villagerTrade()
				.withItem(ItemTriggerCondition.of(Tag.ITEMS_TERRACOTTA))
				.withEntity(entity -> entity
					.withEntityType(EntityType.VILLAGER)
					.withLocation(loc -> loc
						.withBiome(BiomeTags.BADLANDS)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement TRADE_DRIPSTONE_BLOCK_IN_DRIPSTONE_CAVES = buildBase(TRADE_TERRACOTTA_IN_BADLANDS, "trade_dripstone_block_in_dripstone_caves")
		.display(display().x(1F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(Material.DRIPSTONE_BLOCK))
		.withReward(rewards().withExp(85).addItems(ItemStack.of(Material.DRIPSTONE_BLOCK, 32)))
		.requiredProgress(vanilla(
			villagerTrade()
				.withItem(ItemTriggerCondition.of(Material.DRIPSTONE_BLOCK))
				.withEntity(entity -> entity
					.withEntityType(EntityType.VILLAGER)
					.withLocation(loc -> loc
						.withBiome(Biome.DRIPSTONE_CAVES)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement TRADE_WITH_ALL_VILLAGER_TYPES = buildBase(TRADE_WITH_A_VILLAGER, "trade_with_all_villager_types")
		.display(display().xy(1F, -1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.ACACIA_LOG))
		.withReward(rewards().addItems(ItemStack.of(Material.EMERALD, 5)))
		.requiredProgress(vanilla(
			// MCCheck: 1.21.10, new natural villager types
			Stream.of(VillagerTypeKeys.PLAINS, VillagerTypeKeys.DESERT, VillagerTypeKeys.SAVANNA, VillagerTypeKeys.TAIGA, VillagerTypeKeys.SNOW)
				.map(type -> villagerTrade(type.key().value())
					.withEntity(entity -> entity
						.withNbt("{VillagerData:{type:\"%s\"}}".formatted(type.key().asString()))
					)
				)
				.toList()
		))
		.buildAndRegister();
	public static final IAdvancement TRADE_WITH_A_MASTER_VILLAGER = buildBase(TRADE_WITH_ALL_VILLAGER_TYPES, "trade_with_a_master_villager")
		.display(display().x(1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.LOOM))
		.withReward(rewards().addItems(ItemStack.of(Material.EMERALD, 5)))
		.requiredProgress(vanilla(
			villagerTrade()
				.withEntity(entity -> entity
					.withNbt("{VillagerData:{level:5}}")
				)
		))
		.buildAndRegister();
	public static final IAdvancement TRADE_WITH_EVERY_VILLAGER_PROFESSION = buildBase(TRADE_WITH_A_MASTER_VILLAGER, "trade_with_every_villager_profession")
		.display(display().x(1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.FLETCHING_TABLE))
		.withReward(rewards().addItems(ItemStack.of(Material.EMERALD, 10)))
		.requiredProgress(vanilla(
			Registry.VILLAGER_PROFESSION.stream()
				.filter(type -> type != Villager.Profession.NONE && type != Villager.Profession.NITWIT && NamespacedKey.MINECRAFT.equals(type.key().namespace()))
				.map(type -> villagerTrade(type.key().value())
					.withEntity(entity -> entity
						.withNbt("{VillagerData:{profession:\"%s\"}}".formatted(type.key().asString()))
					)
				)
				.toList()
		))
		.buildAndRegister();
	public static final IAdvancement TRADE_WITH_A_JUNGLE_VILLAGER = buildBase(TRADE_WITH_EVERY_VILLAGER_PROFESSION, "trade_with_a_jungle_villager")
		.display(display().x(1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.JUNGLE_LOG))
		.withReward(rewards().withExp(100).addItems(ItemStack.of(Material.EMERALD, 8)))
		.requiredProgress(vanilla(
			villagerTrade()
				.withEntity(entity -> entity
					.withNbt("{VillagerData:{type:\"%s\"}}".formatted(Villager.Type.JUNGLE.key().asString()))
				)
		))
		.buildAndRegister();
	public static final IAdvancement TRADE_WITH_A_SWAMP_VILLAGER = buildBase(TRADE_WITH_A_JUNGLE_VILLAGER, "trade_with_a_swamp_villager")
		.display(display().x(1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.LILY_PAD))
		.withReward(rewards().withExp(100).addItems(ItemStack.of(Material.EMERALD, 8)))
		.requiredProgress(vanilla(
			villagerTrade()
				.withEntity(entity -> entity
					.withNbt("{VillagerData:{type:\"%s\"}}".formatted(Villager.Type.SWAMP.key().asString()))
				)
		))
		.buildAndRegister();
	public static final IAdvancement TRADE_AT_BUILD_HEIGHT = buildBase(TRADE_WITH_A_SWAMP_VILLAGER, "trade_at_build_height")
		.display(display().x(1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.NETHER_STAR))
		.withReward(rewards().withExp(100).addItems(ItemStack.of(Material.EMERALD, 16)))
		.requiredProgress(vanilla(
			villagerTrade()
				.withEntity(entity -> entity
					.withLocation(loc -> loc
						.withDimension(World.Environment.NORMAL)
						.minY(Bukkit.getWorlds().getFirst().getMaxHeight() - 1D)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement TRADE_WITH_ALL_MASTER_VILLAGERS = buildBase(TRADE_AT_BUILD_HEIGHT, "trade_with_all_master_villagers")
		.display(display().x(1F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(Material.GRINDSTONE))
		.withReward(rewards().withExp(100).addItems(ItemStack.of(Material.EMERALD, 32)))
		.requiredProgress(vanilla(
			Registry.VILLAGER_PROFESSION.stream()
				.filter(type -> type != Villager.Profession.NONE && type != Villager.Profession.NITWIT && NamespacedKey.MINECRAFT.equals(type.key().namespace()))
				.map(type -> villagerTrade(type.key().value())
					.withEntity(entity -> entity
						.withNbt("{VillagerData:{profession:\"%s\",level:5}}".formatted(type.key().asString()))
					)
				)
				.toList()
		))
		.buildAndRegister();
	public static final IAdvancement TRADE_WITH_EVERY_VILLAGER_IN_ALL_BIOMES = buildBase(TRADE_WITH_ALL_MASTER_VILLAGERS, "trade_with_every_villager_in_all_biomes")
		.display(display().x(1F).withAdvancementFrame(AdvancementFrame.BUTTERFLY).torture().fancyDescriptionParent(NamedTextColor.DARK_RED).icon(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjFkZDRmZTRhNDI5YWJkNjY1ZGZkYjNlMjEzMjFkNmVmYTZhNmI1ZTdiOTU2ZGI5YzVkNTljOWVmYWIyNSJ9fX0=")))
		.withReward(rewards()
			.withTrophy(MR_WORLDWIDE_HEAD)
		)
		.requiredProgress(vanilla(
			Registry.VILLAGER_PROFESSION.stream()
				.filter(type -> type != Villager.Profession.NONE && type != Villager.Profession.NITWIT && NamespacedKey.MINECRAFT.equals(type.key().namespace()))
				.flatMap(profession ->
					RegistryAccess.registryAccess().getRegistry(RegistryKey.BIOME).stream()
						.filter(biome -> biome != Biome.THE_VOID && NamespacedKey.MINECRAFT.equals(biome.key().namespace()))
						.map(biome -> villagerTrade(profession.key().value() + "_" + biome.key().value())
							.withEntity(entity -> entity
								.withLocation(loc -> loc.withBiome(biome))
								.withNbt("{VillagerData:{profession:\"%s\"}}".formatted(profession.key().asString()))
							)
						)
				)
				.toList()
		))
		.buildAndRegister();
	public static final IAdvancement RING_A_BELL = buildBase(FIND_A_VILLAGE, "ring_a_bell")
		.display(display().xy(1F, 2F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.BELL))
		.requiredProgress(vanilla(
			anyBlockUse()
				.withLocation(loc -> loc
					.withBlock(BlockTriggerCondition.of(Material.BELL))
					.withStructure(MiscUtil.getTagValues(StructureTagKeys.VILLAGE))
				)
		))
		.buildAndRegister();
	public static final IAdvancement RING_AN_ALARM_BELL = buildBase(RING_A_BELL, "ring_an_alarm_bell")
		.display(display().x(1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzFkNDlhNjBmNWQyOTNhMGViNzRmODA4MjU3MzQ2NDU2MjY0MGU3NDdlY2Y0Y2U4ZDc5YzQwMTg3OTljOTAyYyJ9fX0=")))
		.withReward(rewards().withExp(35))
		.requiredProgress(vanilla(
			anyBlockUse()
				.withPlayer(
					new TimeTriggerCondition(24_000L, MinMaxBoundsTriggerCondition.Ints.ofIntegers(0, 500))
				)
				.withLocation(loc -> loc
					.withBlock(BlockTriggerCondition.of(Material.BELL))
					.withDimension(World.Environment.NORMAL)
					.minY(Bukkit.getWorlds().getFirst().getMaxHeight() - 1D)
				)
		))
		.buildAndRegister();
	public static final IAdvancement VILLAGER_SPY = buildBase(RING_AN_ALARM_BELL, "villager_spy")
		.display(display().x(1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.RED_TULIP))
		.withReward(rewards()
			.withExp(50)
			.withTrophy(ItemStack.of(Material.TUFF))
		)
		.requiredProgress(vanilla(
			usingItem()
				.withItem(ItemTriggerCondition.of(Material.SPYGLASS))
				.withPlayer(player -> player
					.withLookingAt(entity -> entity
						.withEntityType(Tag.ENTITY_TYPES_BOAT)
						.withNbt("{Passengers:[{id:\"%s\"},{id:\"%s\"}]}".replace("%s", EntityType.VILLAGER.key().asString()))
						.withDistanceToPlayer(d -> d.minAbsolute(50D))
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement GOT_YOUR_NOSE = buildBase(VILLAGER_SPY, "got_your_nose")
		.display(display().x(1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.SPYGLASS))
		.withReward(rewards()
			.withTrophy(ItemStack.of(Material.BROWN_WOOL))
		)
		.requiredProgress(vanilla(
			usingItem()
				.withItem(ItemTriggerCondition.of(Material.SPYGLASS))
				.withPlayer(player -> player
					.withLookingAt(entity -> entity
						.withEntityType(EntityType.VILLAGER)
						.withDistanceToPlayer(d -> d.maxAbsolute(1D))
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement TRADE_A_BELL = buildBase(TRADE_WITH_A_VILLAGER, "trade_a_bell")
		.display(display().xy(1F, 1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.BELL))
		.withReward(rewards().addItems(ItemStack.of(Material.EMERALD, 5)))
		.requiredProgress(vanilla(
			villagerTrade()
				.withItem(ItemTriggerCondition.of(Material.BELL))
		))
		.buildAndRegister();
	public static final IAdvancement PLACE_ALL_VILLAGER_WORKSTATIONS = buildBase(TRADE_A_BELL, "place_all_villager_workstations")
		.display(display().x(1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.STONECUTTER))
		.withReward(rewards().addItems(ItemStack.of(Material.EMERALD, 5)))
		.requiredProgress(vanilla(
			UtilizerTags.VILLAGER_WORKSTATIONS.getValues().stream()
				.map(type -> placedBlock(type.key().value()).withBlock(type))
				.toList()
		))
		.buildAndRegister();
	public static final IAdvancement GET_A_STACK_OF_ALL_VILLAGER_WORKSTATIONS = buildBase(PLACE_ALL_VILLAGER_WORKSTATIONS, "get_a_stack_of_all_villager_workstations")
		.display(display().x(1F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(Material.BREWING_STAND))
		.withReward(rewards().withExp(100).addItems(ItemStack.of(Material.EMERALD, 16)))
		.requiredProgress(vanilla(
			UtilizerTags.VILLAGER_WORKSTATIONS.getValues().stream()
				.map(type -> inventoryChanged(type.key().value()).withItems(ItemTriggerCondition.of(type).withMinAmount(type.getMaxStackSize())))
				.toList()
		))
		.buildAndRegister();
	public static final IAdvancement GEAR_UP_A_VILLAGER_IN_NETHERITE = buildBase(GET_A_STACK_OF_ALL_VILLAGER_WORKSTATIONS, "gear_up_a_villager_in_netherite")
		.display(display().x(1F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(Material.NETHERITE_CHESTPLATE))
		.withReward(rewards()
			.withExp(60)
			.addItems(ItemStack.of(Material.EMERALD, 16))
			.withTrophy(ItemStack.of(Material.EMERALD))
		)
		.buildAndRegister();
	public static final IAdvancement GET_A_TALL_GRASS = buildBase(ADVENTURE_ROOT, "get_a_tall_grass")
		.display(display().xy(1F, -4.25F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.LARGE_FERN))
		.requiredProgress(vanillaAny(
			inventoryChanged(Material.TALL_GRASS.key().value()).withItems(ItemTriggerCondition.of(Material.TALL_GRASS)),
			inventoryChanged(Material.LARGE_FERN.key().value()).withItems(ItemTriggerCondition.of(Material.LARGE_FERN))
		))
		.buildAndRegister();
	public static final IAdvancement GET_A_STACK_OF_TALL_GRASS = buildBase(GET_A_TALL_GRASS, "get_a_stack_of_tall_grass")
		.display(display().x(1F).challengeFrame().fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).icon(Material.LARGE_FERN))
		.withReward(rewards()
			.withExp(800)
			.addItems(ItemStack.of(Material.LARGE_FERN))
			.withTrophy(ItemStack.of(Material.GOLD_NUGGET))
		)
		.requiredProgress(vanillaAny(
			inventoryChanged(Material.TALL_GRASS.key().value()).withItems(ItemTriggerCondition.of(Material.TALL_GRASS).withMinAmount(Material.TALL_GRASS.getMaxStackSize())),
			inventoryChanged(Material.LARGE_FERN.key().value()).withItems(ItemTriggerCondition.of(Material.LARGE_FERN).withMinAmount(Material.LARGE_FERN.getMaxStackSize()))
		))
		.buildAndRegister();

	public static final AdvancementTab NATURE_TAB = buildTab("nature", MANAGER)
		.display(simpleTabDisplay().inverseY().display(display().backgroundPathTexture("block/green_concrete_powder").icon(Material.PUMPKIN_PIE)))
		.build();
	public static final IAdvancement NATURE_ROOT = buildBase(NATURE_TAB, "visual_root")
		.display(display().withAdvancementFrame(AdvancementFrame.SQUIRCLE).fancyDescriptionParent(GRAY).icon(Material.PUMPKIN_PIE))
		.visibilityRule(ifDone(false, FIRST_POSSESSION))
		.requiredProgress(vanilla(consumeItem()))
		.buildAndRegister();
	public static final IAdvancement EAT_ROTTEN_FLESH = buildBase(NATURE_ROOT, "eat_rotten_flesh")
		.display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.ROTTEN_FLESH))
		.requiredProgress(vanilla(consumeItem().withItem(ItemTriggerCondition.of(Material.ROTTEN_FLESH))))
		.buildAndRegister();
	public static final IAdvancement EAT_CREEPER_HEART = buildBase(EAT_ROTTEN_FLESH, "eat_creeper_heart")
		.display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(RequiemItems.CREEPER_HEART))
		.requiredProgress(vanilla(consumeItem().withItem(ItemTriggerCondition.of(RequiemItems.CREEPER_HEART))))
		.buildAndRegister();
	public static final IAdvancement EAT_1K_SPIDER_EYES = buildBase(EAT_CREEPER_HEART, "eat_1k_spider_eyes")
		.display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.SPIDER_EYE))
		.withReward(rewards()
			.withExp(500)
			.addItems(ItemStack.of(Material.DIAMOND, 77))
			.withTrophy(ItemStack.of(Material.CHIPPED_ANVIL))
		)
		.requiredProgress(simple(1000))
		.buildAndRegister();
	public static final IAdvancement EAT_BOILED_AXOLOTL = buildBase(NATURE_ROOT, "eat_boiled_axolotl")
		.display(display().xy(1F, 1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(TrappedNewbieItems.BOILED_AXOLOTL_BUCKET))
		.requiredProgress(vanilla(consumeItem().withItem(ItemTriggerCondition.of(TrappedNewbieItems.BOILED_AXOLOTL_BUCKET))))
		.buildAndRegister();
	public static final IAdvancement DRINK_CACTUS_JUICE = buildBase(NATURE_ROOT, "drink_cactus_juice")
		.display(display().x(-1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(TrappedNewbieRecipes.getFilled(ItemStack.of(TrappedNewbieItems.CACTUS_BOWL), ThirstData.DrinkType.CACTUS_JUICE)))
		.buildAndRegister();

	public static final AdvancementTab BUILDING_TAB = buildTab("building", MANAGER)
		.display(simpleTabDisplay().inverseY().display(display().backgroundPathTexture("block/oak_planks").icon(Material.OAK_PLANKS)))
		.build();
	public static final IAdvancement BUILDING_ROOT = buildBase(BUILDING_TAB, "visual_root")
		.display(display().withAdvancementFrame(AdvancementFrame.SQUIRCLE).fancyDescriptionParent(GRAY).icon(Material.OAK_PLANKS))
		.visibilityRule(ifDone(false, FIRST_POSSESSION))
		.requiredProgress(alwaysDone())
		.buildAndRegister();

	public static final AdvancementTab WEAPONRY_TAB = buildTab("weaponry", MANAGER)
		.display(simpleTabDisplay().inverseY().display(display().backgroundPathTexture("block/smithing_table_top").icon(Material.STONE_SWORD)))
		.build();
	public static final IAdvancement WEAPONRY_ROOT = buildBase(WEAPONRY_TAB, "visual_root").
		display(display().withAdvancementFrame(AdvancementFrame.SQUIRCLE).fancyDescriptionParent(GRAY).icon(Material.STONE_SWORD))
		.visibilityRule(ifDone(false, FIRST_POSSESSION))
		.requiredProgress(vanilla(
			inventoryChanged()
				.withItems(ItemTriggerCondition.of(Tag.ITEMS_SWORDS))
		))
		.buildAndRegister();
	public static final IAdvancement EXPELLIARMUS = buildBase(WEAPONRY_ROOT, "expelliarmus").display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(ItemUtil.glint(TrappedNewbieItems.ROUGH_STICK)))
		.requiredProgress(vanilla(playerHurtEntity("expelliarmus").withEntity(entity -> entity.withDistanceToPlayer(distance -> distance.maxAbsolute(5D))).withPlayer(player -> player.withEquipment(equipment -> equipment.withMainHand(ItemTriggerCondition.of(MiscUtil.combineArrays(TrappedNewbieTags.STICKS.getValues().toArray(new Material[0]), new Material[] {TrappedNewbieItems.ROUGH_STICK})))))))
		.buildAndRegister();
	public static final IAdvancement SLAPFISH = buildBase(EXPELLIARMUS, "slapfish").display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.COD))
		.requiredProgress(vanilla(playerHurtEntity("slapfish").withEntity(entity -> entity.withDistanceToPlayer(distance -> distance.maxAbsolute(5D))).withPlayer(player -> player.withEquipment(equipment -> equipment.withMainHand(Tag.ITEMS_FISHES)))))
		.buildAndRegister();
	public static final IAdvancement DIEMONDS = buildBase(SLAPFISH, "diemonds").display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.DIAMOND))
		.requiredProgress(vanilla(playerHurtEntity("diemonds").withEntity(entity -> entity.withDistanceToPlayer(distance -> distance.maxAbsolute(5D))).withPlayer(player -> player.withEquipment(equipment -> equipment.withMainHand(Material.DIAMOND)))))
		.buildAndRegister();
	public static final IAdvancement INSPECTOR_GADGET = buildBase(DIEMONDS, "inspector_gadget").display(display().xy(1F, -0.5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.SPYGLASS))
		.withReward(rewards().addItems(ItemStack.of(Material.COPPER_INGOT, 4), ItemStack.of(Material.AMETHYST_SHARD, 4)))
		.requiredProgress(vanilla(playerKilledEntity().withEntity(entity -> entity.withDistanceToPlayer(distance -> distance.maxAbsolute(5D))).withPlayer(player -> player.withEquipment(equipment -> equipment.withMainHand(Material.SPYGLASS)))))
		.buildAndRegister(InspectorGadgetAdvancement::new);
	public static final IAdvancement ROCK_PAPER_SHEARS = buildBase(INSPECTOR_GADGET, "rock_paper_shears").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.SHEARS)).buildAndRegister(RockPaperShearsAdvancement::new);
	public static final IAdvancement ITS_TIME_CONSUMING = buildBase(DIEMONDS, "its_time_consuming").display(display().xy(1F, 0.5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.CLOCK))
		.withReward(rewards().addItems(ItemStack.of(Material.GOLD_INGOT, 4)))
		.requiredProgress(vanilla(playerKilledEntity().withEntity(entity -> entity.withDistanceToPlayer(distance -> distance.maxAbsolute(5D))).withPlayer(player -> player.withEquipment(equipment -> equipment.withMainHand(Material.CLOCK)))))
		.buildAndRegister();
	public static final IAdvancement DIE_TWICE_WITHIN_10S = buildBase(ITS_TIME_CONSUMING, "die_twice_within_10s").display(display().x(1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(banner(Material.BLACK_BANNER, new Pattern(DyeColor.WHITE, PatternType.STRIPE_LEFT), new Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP), new Pattern(DyeColor.WHITE, PatternType.STRIPE_MIDDLE), new Pattern(DyeColor.BLACK, PatternType.BORDER)))).buildAndRegister();
	public static final IAdvancement DIE_TWICE_WITHIN_5S = buildBase(DIE_TWICE_WITHIN_10S, "die_twice_within_5s").display(display().x(1F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(banner(Material.WHITE_BANNER, new Pattern(DyeColor.BLACK, PatternType.STRIPE_BOTTOM), new Pattern(DyeColor.BLACK, PatternType.STRIPE_LEFT), new Pattern(DyeColor.WHITE, PatternType.BORDER))))
		.withReward(rewards()
			.withDeathExp(35)
			.withDeathTrophy(() -> {
				ItemStack item = shield(DyeColor.WHITE);
				item.setData(DataComponentTypes.BANNER_PATTERNS, BannerPatternLayers.bannerPatternLayers(List.of(
					new Pattern(DyeColor.BLACK, PatternType.HALF_VERTICAL),
					new Pattern(DyeColor.BLACK, PatternType.STRIPE_BOTTOM)
				)));
				return item;
			})
		)
		.buildAndRegister();
	public static final IAdvancement HALF_A_HEART_1M = buildBase(ROCK_PAPER_SHEARS, "half_a_heart_1m").display(display().x(1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.NETHER_WART))
		.withReward(rewards().withExp(50))
		.buildAndRegister();
	public static final IAdvancement HALF_A_HEART_1H = buildBase(HALF_A_HEART_1M, "half_a_heart_1h").display(display().x(1F).challengeFrame().torture().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(Material.NETHER_WART_BLOCK))
		.withReward(rewards()
			.withTrophy(ItemStack.of(Material.BLACK_DYE))
		)
		.buildAndRegister();
	public static final IAdvancement HALF_A_HEART_6H = buildBase(HALF_A_HEART_1H, "half_a_heart_6h").display(display().x(1F).withAdvancementFrame(AdvancementFrame.BUTTERFLY).superTorture().fancyDescriptionParent(NamedTextColor.DARK_RED).icon(potionItem(Material.POTION, PotionType.REGENERATION)))
		.withReward(rewards()
			.withTrophy(ItemStack.of(Material.COMPOSTER))
		)
		.buildAndRegister();
	public static final IAdvancement GET_A_SHIELD = buildBase(WEAPONRY_ROOT, "get_a_shield").display(display().xy(1F, 1.5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.SHIELD))
		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.SHIELD))))
		.buildAndRegister();
	public static final IAdvancement BLOCK_WITH_SHIELD = buildBase(GET_A_SHIELD, "block_with_shield").display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.ROTTEN_FLESH))
		.requiredProgress(vanilla(entityHurtPlayer().withDamage(DamageTriggerCondition::blocked)))
		.buildAndRegister();
	public static final IAdvancement GET_A_BANNER_SHIELD = buildBase(BLOCK_WITH_SHIELD, "get_a_banner_shield").display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(shield(DyeColor.RED)))
		.withReward(rewards().addItems(ItemStack.of(Material.WHITE_WOOL, 6)))
		.buildAndRegister(GetABannerShieldAdvancement::new);
	public static final IAdvancement MORE_SHIELDS = buildBase(GET_A_BANNER_SHIELD, "more_shields").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(() -> {
			ItemStack item = shield(DyeColor.WHITE);
			item.setData(DataComponentTypes.BANNER_PATTERNS, BannerPatternLayers.bannerPatternLayers(List.of(
				new Pattern(DyeColor.LIGHT_GRAY, PatternType.MOJANG),
				new Pattern(DyeColor.LIGHT_BLUE, PatternType.GRADIENT_UP),
				new Pattern(DyeColor.BLACK, PatternType.GRADIENT),
				new Pattern(DyeColor.BLACK, PatternType.TRIANGLE_BOTTOM),
				new Pattern(DyeColor.WHITE, PatternType.CIRCLE),
				new Pattern(DyeColor.BLACK, PatternType.SQUARE_BOTTOM_LEFT)
			)));
			return item;
		}))
		.withReward(rewards()
			.withExp(25)
			.withTrophy(() -> {
				ItemStack item = shield(DyeColor.LIME);
				item.setData(DataComponentTypes.BANNER_PATTERNS, BannerPatternLayers.bannerPatternLayers(List.of(
					new Pattern(DyeColor.BROWN, PatternType.PIGLIN),
					new Pattern(DyeColor.BROWN, PatternType.TRIANGLES_BOTTOM),
					new Pattern(DyeColor.BROWN, PatternType.STRIPE_CENTER),
					new Pattern(DyeColor.GREEN, PatternType.TRIANGLES_TOP),
					new Pattern(DyeColor.GREEN, PatternType.GRADIENT),
					new Pattern(DyeColor.GREEN, PatternType.BRICKS)
				)));
				return item;
			})
		)
		.buildAndRegister();
	public static final IAdvancement MASTER_SHIELDSMAN = buildBase(MORE_SHIELDS, "master_shieldsman").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(shield(DyeColor.BLACK)))
		.withReward(rewards()
			.withExp(100)
			.withTrophy(() -> {
				ItemStack item = shield(DyeColor.BLUE);
				item.setData(DataComponentTypes.BANNER_PATTERNS, BannerPatternLayers.bannerPatternLayers(List.of(
					new Pattern(DyeColor.RED, PatternType.FLOWER),
					new Pattern(DyeColor.BLUE, PatternType.HALF_HORIZONTAL),
					new Pattern(DyeColor.YELLOW, PatternType.TRIANGLE_TOP),
					new Pattern(DyeColor.BLUE, PatternType.STRIPE_TOP),
					new Pattern(DyeColor.YELLOW, PatternType.TRIANGLES_BOTTOM),
					new Pattern(DyeColor.BLACK, PatternType.BORDER),
					new Pattern(DyeColor.LIGHT_GRAY, PatternType.CURLY_BORDER)
				)));
				item.addUnsafeEnchantment(Enchantment.UNBREAKING, 10);
				item.addUnsafeEnchantment(Enchantment.THORNS, 1);
				return item;
			})
		)
		.buildAndRegister(MasterShieldsmanAdvancement::new);
	public static final IAdvancement DEFLECT_40 = buildBase(MASTER_SHIELDSMAN, "deflect_40").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(() -> {
			ItemStack item = shield(DyeColor.BLACK);
			item.setData(DataComponentTypes.BANNER_PATTERNS, BannerPatternLayers.bannerPatternLayers(List.of(
				new Pattern(DyeColor.GRAY, PatternType.CURLY_BORDER),
				new Pattern(DyeColor.GRAY, PatternType.TRIANGLE_BOTTOM),
				new Pattern(DyeColor.GRAY, PatternType.TRIANGLE_TOP),
				new Pattern(DyeColor.GRAY, PatternType.BRICKS)
			)));
			return item;
		}))
		.requiredProgress(vanilla(entityHurtPlayer().withDamage(damage -> damage.blocked().withMinDealtDamage(40D))))
		.buildAndRegister();
	public static final IAdvancement ATTACK_WITH_AN_AXE = buildBase(WEAPONRY_ROOT, "attack_with_an_axe").display(display().xy(1F, 2.5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.STONE_AXE))
		.requiredProgress(vanilla(
			playerHurtEntity()
				.withDamage(damage -> damage
					.withDamageSource(source -> source
						.withTag(DamageTypeTagKeys.IS_PLAYER_ATTACK, true)
						.withSourceEntity(entity -> entity
							.withEquipment(equipment -> equipment
								.withMainHand(Tag.ITEMS_AXES)
							)
						)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement ATTACK_WITH_ALL_AXES = buildBase(ATTACK_WITH_AN_AXE, "attack_with_all_axes").display(display().x(1).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.DIAMOND_AXE))
		.withReward(rewards().withExp(50))
		.buildAndRegister(AttackWithAllAxesAdvancement::new);
	public static final IAdvancement ATTACK_WITH_ALL_SHOVELS = buildBase(ATTACK_WITH_ALL_AXES, "attack_with_all_shovels").display(display().x(1).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.IRON_SHOVEL))
		.withReward(rewards().withExp(100))
		.buildAndRegister(AttackWithAllShovelsAdvancement::new);
	public static final IAdvancement SPLEAF = buildBase(ATTACK_WITH_ALL_SHOVELS, "spleaf").display(display().x(1).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.BIG_DRIPLEAF))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.BIG_DRIPLEAF, 4)))
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity
					.withDistanceToPlayer(distance -> distance.maxAbsolute(5D))
					.withSteppingLocation(loc -> loc
						.withBlock(block -> block.withBlocks(Material.BIG_DRIPLEAF))
					)
				)
				.withDamage(source -> source
					.withTag(DamageTypeTagKeys.IS_PLAYER_ATTACK, true)
					.withSourceEntity(entity -> entity
						.withSteppingLocation(loc -> loc
							.withBlock(block -> block.withBlocks(Material.BIG_DRIPLEAF))
						)
						.withEquipment(equipment -> equipment
							.withMainHand(Tag.ITEMS_SHOVELS)
						)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement ATTACK_WITH_ALL_WEAPONS = buildBase(SPLEAF, "attack_with_all_weapons").display(display().x(1).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.NETHERITE_SWORD))
		.withReward(rewards()
			.withExp(100)
			.withTrophy(ItemStack.of(Material.STICK))
		)
		.buildAndRegister(AttackWithAllWeaponsAdvancement::new);
	public static final IAdvancement SHOOT_A_MOB_WITH_A_BOW = buildBase(WEAPONRY_ROOT, "shoot_a_mob_with_a_bow").display(display().xy(1F, 4F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.BOW))
		.requiredProgress(vanilla(
			playerHurtEntity()
				.withDamage(damage -> damage
					.withDamageSource(source -> source
						.withTag(DamageTypeTagKeys.IS_PROJECTILE, true)
						.withDirectEntity(entity -> entity
							.withEntityType(Tag.ENTITY_TYPES_ARROWS)
							.withNbt("{weapon:{id:\"%s\"}}".formatted(Material.BOW.getKey()))
						)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement POINT_BLANK_SHOT = buildBase(SHOOT_A_MOB_WITH_A_BOW, "point_blank_shot").display(display().xy(1F, 0.5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.STRING))
		.withReward(rewards().addItems(ItemStack.of(Material.ARROW, 8)))
		.requiredProgress(vanilla(
			playerHurtEntity()
				.withDamage(damage -> damage
					.withDamageSource(source -> source
						.withTag(DamageTypeTagKeys.IS_PROJECTILE, true)
						.withDirectEntity(entity -> entity
							.withEntityType(Tag.ENTITY_TYPES_ARROWS)
						)
					)
				)
				.withEntity(entity -> entity.withDistanceToPlayer(distance -> distance.maxAbsolute(2D)))
		))
		.buildAndRegister();
	public static final IAdvancement BOW_SPAMMER = buildBase(POINT_BLANK_SHOT, "bow_spammer").display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.STRING))
		.withReward(rewards().addItems(ItemStack.of(Material.ARROW, 8)))
		.requiredProgress(vanilla(
			playerHurtEntity()
				.withDamage(damage -> damage
					.withMaxTakenDamage(1D)
					.withDamageSource(source -> source
						.withTag(DamageTypeTagKeys.IS_PROJECTILE, true)
						.withDirectEntity(entity -> entity
							.withEntityType(Tag.ENTITY_TYPES_ARROWS)
						)
					)
				)
				.withEntity(entity -> entity.withDistanceToPlayer(distance -> distance.maxAbsolute(2D)))
		))
		.buildAndRegister();
	public static final IAdvancement SNIPER_DUEL = buildBase(BOW_SPAMMER, "sniper_duel").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.SKELETON_SKULL))
		.withReward(rewards()
			.withExp(50)
			.addItems(ItemStack.of(Material.ARROW, 32))
			.addItems(() -> {
				var item = ItemStack.of(Material.ENCHANTED_BOOK);
				item.setData(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantments.itemEnchantments().add(Enchantment.POWER, 3).build());
				return item;
			})
		)
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withDamage(source -> source
					.withTag(DamageTypeTagKeys.IS_PROJECTILE, true)
					.withDirectEntity(entity -> entity
						.withEntityType(Tag.ENTITY_TYPES_ARROWS)
					)
				)
				.withEntity(entity -> entity
					.withEntityType(UtilizerTags.BOW_SKELETONS)
					.withDistanceToPlayer(distance -> distance.minHorizontal(50D))
				)
		))
		.buildAndRegister();
	public static final IAdvancement BAT_DUEL = buildBase(SNIPER_DUEL, "bat_duel").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().torture().icon(ItemUtil.texturedHead(MoreMobHeads.BAT)))
		.withReward(rewards().withTrophy(ItemStack.of(Material.BOW)))
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withDamage(source -> source
					.withTag(DamageTypeTagKeys.IS_PROJECTILE, true)
					.withDirectEntity(entity -> entity
						.withEntityType(Tag.ENTITY_TYPES_ARROWS)
					)
				)
				.withEntity(entity -> entity
					.withEntityType(EntityType.BAT)
					.withDistanceToPlayer(distance -> distance.minHorizontal(100D))
				)
		))
		.buildAndRegister();
	public static final IAdvancement THERE_IT_GOES = buildBase(SNIPER_DUEL, "there_it_goes").display(display().xy(1F, -1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.SPECTRAL_ARROW))
		.withReward(rewards()
			.withExp(100)
			.withTrophy(ItemStack.of(Material.SPECTRAL_ARROW))
		)
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withDamage(source -> source
					.withTag(DamageTypeTagKeys.IS_PROJECTILE, true)
				)
				.withEntity(entity -> entity
					.withLocation(loc -> loc
						.withDimension(World.Environment.NORMAL)
						.maxY(Bukkit.getWorlds().getFirst().getMinHeight() + 5D)
					)
				)
				.withPlayer(player -> player
					.withLocation(loc -> loc
						.withDimension(World.Environment.NORMAL)
						.minY(Bukkit.getWorlds().getFirst().getMaxHeight() - 1D)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement HUNT_LAND_ANIMALS = buildBase(BAT_DUEL, "hunt_land_animals").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.CHICKEN))
		.withReward(rewards()
			.withExp(100)
			.addItems(() -> {
				var item = ItemStack.of(Material.ENCHANTED_BOOK);
				item.setData(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantments.itemEnchantments().add(Enchantment.INFINITY, 1).build());
				return item;
			})
			.withTrophy(() -> {
				var item = ItemStack.of(Material.IRON_SWORD);
				item.addEnchantment(Enchantment.FIRE_ASPECT, 2);
				return item;
			})
		)
		.buildAndRegister(HuntLandAnimalsAdvancement::new);
	public static final IAdvancement YOU_MONSTER = buildBase(THERE_IT_GOES, "you_monster").display(display().x(1F).withAdvancementFrame(AdvancementFrame.BUTTERFLY).fancyDescriptionParent(NamedTextColor.DARK_RED).superTorture().icon(Material.DIAMOND_SWORD))
		.withReward(rewards()
			.withTrophy(ItemStack.of(Material.RED_DYE))
		)
		.buildAndRegister(YouMonsterAdvancement::new);
	public static final IAdvancement GET_A_FLETCHING_TABLE = buildBase(SHOOT_A_MOB_WITH_A_BOW, "get_a_fletching_table").display(display().xy(1F, -0.5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.FLETCHING_TABLE))
		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.FLETCHING_TABLE))))
		.buildAndRegister();
	public static final IAdvancement DIE_FROM_SKELETON_NOT_FROM_AN_ARROW = buildBase(GET_A_FLETCHING_TABLE, "die_from_skeleton_not_from_an_arrow").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().torture().icon(Material.TARGET))
		.withReward(rewards().withDeathTrophy(() -> ItemStack.of(Material.TARGET)))
		.requiredProgress(vanilla(
			entityKilledPlayer()
				.withEntity(entity -> entity
					.withEntityType(UtilizerTags.BOW_SKELETONS)
					.withEquipment(equipment -> equipment
						.withMainHand(Material.BOW)
					)
				)
				.withDamage(source -> source
					.withTag(DamageTypeTagKeys.IS_PROJECTILE, false)
				)
		))
		.buildAndRegister();
	public static final IAdvancement THROW_TRIDENT = buildBase(WEAPONRY_ROOT, "throw_trident").display(display().xy(1F, 5.5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.TRIDENT))
		.requiredProgress(vanilla(
			playerHurtEntity()
				.withDamage(damage -> damage
					.withDamageSource(source -> source
						.withTag(DamageTypeTagKeys.IS_PROJECTILE, true)
						.withDirectEntity(entity -> entity
							.withEntityType(EntityType.TRIDENT)
						)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement KILL_A_DROWNED_WITH_A_TRIDENT_FROM_30M = buildBase(THROW_TRIDENT, "kill_a_drowned_with_a_trident_from_30m").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.WATER_BUCKET))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.TRIDENT)))
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity
					.withEntityType(EntityType.DROWNED)
					.withDistanceToPlayer(distance -> distance.minHorizontal(30D))
				)
				.withDamage(source -> source
					.withTag(DamageTypeTagKeys.IS_PROJECTILE, true)
					.withDirectEntity(entity -> entity
						.withEntityType(EntityType.TRIDENT)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement KILL_A_BABY_TURTLE_WITH_A_TRIDENT_FROM_A_HEIGHT = buildBase(KILL_A_DROWNED_WITH_A_TRIDENT_FROM_30M, "kill_a_baby_turtle_with_a_trident_from_a_height").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.TURTLE_EGG))
		.withReward(rewards()
			.withExp(100)
			.addItems(ItemStack.of(Material.TURTLE_SCUTE, 2))
			.withTrophy(ItemStack.of(Material.SPYGLASS))
		)
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withPlayer(player -> player
					.withLocation(loc -> loc.minY(300D))
				)
				.withEntity(entity -> entity
					.withEntityType(EntityType.TURTLE)
					.withState(EntityStateTriggerCondition::baby)
					.withLocation(loc -> loc.maxY((double) Bukkit.getWorlds().getFirst().getSeaLevel()))
				)
				.withDamage(source -> source
					.withTag(DamageTypeTagKeys.IS_PROJECTILE, true)
					.withDirectEntity(entity -> entity
						.withEntityType(EntityType.TRIDENT)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement KILL_ALL_AQUATIC_WITH_A_TRIDENT = buildBase(KILL_A_BABY_TURTLE_WITH_A_TRIDENT_FROM_A_HEIGHT, "kill_all_aquatic_with_a_trident").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.PRISMARINE_SHARD))
		.withReward(rewards()
			.withExp(100)
			.addItems(() -> {
				var item = ItemStack.of(Material.ENCHANTED_BOOK);
				item.setData(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantments.itemEnchantments().add(Enchantment.RIPTIDE, 3).build());
				return item;
			})
		)
		.buildAndRegister(KillAllAquaticWithATridentAdvancement::new);
	public static final IAdvancement KILL_ALL_NETHER_WITH_A_TRIDENT = buildBase(KILL_ALL_AQUATIC_WITH_A_TRIDENT, "kill_all_nether_with_a_trident").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.FIRE_CHARGE))
		.withReward(rewards()
			.withExp(100)
			.addItems(() -> {
				var item = ItemStack.of(Material.ENCHANTED_BOOK);
				item.setData(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantments.itemEnchantments().add(Enchantment.LOYALTY, 3).build());
				return item;
			})
		)
		.buildAndRegister(KillAllNetherWithATridentAdvancement::new);
	public static final IAdvancement ATTACK_WITH_A_MACE = buildBase(WEAPONRY_ROOT, "attack_with_a_mace").display(display().xy(1F, 6.5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.MACE))
		.requiredProgress(vanilla(
			playerHurtEntity()
				.withDamage(damage -> damage
					.withDamageSource(source -> source
						.withTag(DamageTypeTagKeys.MACE_SMASH, true)
					)
				)
				.withPlayer(player -> player
					.withEquipment(equipment -> equipment.withMainHand(Material.MACE))
				)
		))
		.buildAndRegister();
	public static final IAdvancement MACE_100_DAMAGE = buildBase(ATTACK_WITH_A_MACE, "mace_100_damage").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.DEEPSLATE_REDSTONE_ORE))
		.withReward(rewards().withExp(50))
		.requiredProgress(vanilla(
			playerHurtEntity()
				.withDamage(damage -> damage
					.withDamageSource(source -> source
						.withTag(DamageTypeTagKeys.MACE_SMASH, true)
					)
					.withMinDealtDamage(100D)
				)
				.withPlayer(player -> player
					.withEquipment(equipment -> equipment.withMainHand(Material.MACE))
				)
		))
		.buildAndRegister();
	public static final IAdvancement GIVE_WIND_MACE_TO_A_FOX = buildBase(MACE_100_DAMAGE, "give_wind_mace_to_a_fox").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(ItemUtil.glint(Material.MACE)))
		.withReward(rewards().withExp(60))
		.buildAndRegister(GiveWindMaceToAFoxAdvancement::new);
	public static final IAdvancement MACE_500_DAMAGE = buildBase(GIVE_WIND_MACE_TO_A_FOX, "mace_500_damage").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.SCULK))
		.withReward(rewards()
			.withExp(200)
			.addItems(ItemStack.of(Material.HEAVY_CORE))
			.withTrophy(ItemStack.of(Material.SCULK_CATALYST))
		)
		.requiredProgress(vanilla(
			playerHurtEntity()
				.withDamage(damage -> damage
					.withDamageSource(source -> source
						.withTag(DamageTypeTagKeys.MACE_SMASH, true)
					)
					.withMinDealtDamage(500D)
				)
				.withEntity(entity -> entity.withEntityType(EntityType.WARDEN))
				.withPlayer(player -> player
					.withEquipment(equipment -> equipment.withMainHand(Material.MACE))
				)
		))
		.buildAndRegister();
	public static final IAdvancement ATTACK_WITH_AN_EGG = buildBase(WEAPONRY_ROOT, "attack_with_an_egg").display(display().xy(1F, -3F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.EGG))
		.buildAndRegister(AttackWithAnEggAdvancement::new);
	public static final IAdvancement ATTACK_ZOMBIE_WITH_AN_EGG = buildBase(ATTACK_WITH_AN_EGG, "attack_zombie_with_an_egg").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.ZOMBIE_HEAD))
		.buildAndRegister(AttackZombieWithAnEggAdvancement::new);
	public static final IAdvancement ATTACK_SQUID_IN_THE_AIR_WITH_A_SNOWBALL = buildBase(ATTACK_ZOMBIE_WITH_AN_EGG, "attack_squid_in_the_air_with_a_snowball").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.SNOWBALL))
		.withReward(rewards().withExp(70))
		.buildAndRegister(AttackSquidInTheAirWithASnowballAdvancement::new);
	public static final IAdvancement HOOK_PIG = buildBase(WEAPONRY_ROOT, "hook_pig").display(display().xy(1F, -4F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.PORKCHOP))
		.requiredProgress(vanilla(fishingRodHooked().withEntity(entity -> entity.withEntityType(EntityType.PIG))))
		.buildAndRegister();
	public static final IAdvancement HOOK_MONSTER = buildBase(HOOK_PIG, "hook_monster").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.FISHING_ROD))
		.requiredProgress(vanilla(fishingRodHooked().withEntity(entity -> entity.withEntityType(UtilizerTags.HOSTILE_MONSTERS))))
		.buildAndRegister();
	public static final IAdvancement HOOK_TNT = buildBase(HOOK_MONSTER, "hook_tnt").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(ItemUtil.glint(Material.FISHING_ROD)))
		.withReward(rewards().withExp(25))
		.requiredProgress(vanilla(fishingRodHooked().withEntity(entity -> entity.withEntityType(EntityType.TNT))))
		.buildAndRegister();
	public static final IAdvancement HOOK_WARDEN = buildBase(HOOK_TNT, "hook_warden").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(ItemUtil.glint(Material.WARD_ARMOR_TRIM_SMITHING_TEMPLATE)))
		.withReward(rewards().withExp(40).addItems(ItemStack.of(Material.SCULK_CATALYST, 2)))
		.requiredProgress(vanilla(fishingRodHooked().withEntity(entity -> entity.withEntityType(EntityType.WARDEN))))
		.buildAndRegister();
	public static final IAdvancement WHEN_PIGS_FINALLY_FLY = buildBase(HOOK_WARDEN, "when_pigs_finally_fly").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(PIG_HEAD))
		.withReward(rewards()
			.withExp(100)
			.addItems(ItemStack.of(Material.PORKCHOP, 32))
			.withTrophy(ItemStack.of(Material.PORKCHOP))
		)
		.requiredProgress(vanilla(fishingRodHooked().withEntity(entity -> entity.withEntityType(EntityType.PIG).withDistanceToPlayer(distance -> distance.minAbsolute(25D)))))
		.buildAndRegister();
	public static final IAdvancement ATTACK_WITH_A_SNOWBALL = buildBase(WEAPONRY_ROOT, "attack_with_a_snowball").display(display().xy(1F, -1.5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.SNOWBALL))
		.withReward(rewards().addItems(ItemStack.of(Material.SNOWBALL, 16)))
		.requiredProgress(vanilla(
			playerHurtEntity()
				.withDamage(damage -> damage
					.withDamageSource(source -> source
						.withTag(DamageTypeTagKeys.IS_PROJECTILE, true)
						.withDirectEntity(direct -> direct
							.withEntityType(EntityType.SNOWBALL)
							.withNbt("{Item:{id:\"%s\"}}".formatted(Material.SNOWBALL.key()))
						)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement KILL_SNOW_GOLEM_WITH_A_SNOWBALL = buildBase(ATTACK_WITH_A_SNOWBALL, "kill_snow_golem_with_a_snowball").display(display().xy(1F, 0.5F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.CARVED_PUMPKIN))
		.withReward(rewards().withExp(25).addItems(ItemStack.of(Material.SNOWBALL, 4)))
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withPlayer(player -> player
					.withEquipment(equipment -> equipment
						.withMainHand(Material.SNOWBALL)
					)
				)
				.withEntity(entity -> entity
					.withEntityType(EntityType.SNOW_GOLEM)
				)
		))
		.buildAndRegister();
	public static final IAdvancement GET_KILLED_BY_A_SNOW_GOLEM = buildBase(KILL_SNOW_GOLEM_WITH_A_SNOWBALL, "get_killed_by_a_snow_golem").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(ItemUtil.glint(Material.SNOWBALL)))
		.withReward(rewards().withExp(50))
		.requiredProgress(vanilla(
			entityKilledPlayer()
				.withEntity(entity -> entity
					.withEntityType(EntityType.SNOW_GOLEM)
				)
		))
		.buildAndRegister();
	public static final IAdvancement KILL_A_BLAZE_WITH_A_SNOWBALL = buildBase(ATTACK_WITH_A_SNOWBALL, "kill_a_blaze_with_a_snowball").display(display().xy(1F, -0.5F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.BLAZE_POWDER))
		.withReward(rewards().addItems(ItemStack.of(Material.BLAZE_ROD, 4)))
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity
					.withEntityType(EntityType.BLAZE)
				)
				.withDamage(source -> source
					.withTag(DamageTypeTagKeys.IS_PROJECTILE, true)
					.withDirectEntity(direct -> direct
						.withEntityType(EntityType.SNOWBALL)
						.withNbt("{Item:{id:\"%s\"}}".formatted(Material.SNOWBALL.key()))
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement KILL_A_BRAZE_WITH_A_BRAZE_ROD_WHILE_ON_FIRE = buildBase(KILL_A_BLAZE_WITH_A_SNOWBALL, "kill_a_braze_with_a_braze_rod_while_on_fire").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.BLAZE_ROD))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.BLAZE_ROD, 8)))
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity
					.withEntityType(EntityType.BLAZE)
					.withState(EntityStateTriggerCondition::onFire)
				)
				.withPlayer(player -> player
					.withEquipment(equipment -> equipment
						.withMainHand(Material.BLAZE_ROD)
					)
					.withState(EntityStateTriggerCondition::onFire)
				)
				.withDamage(source -> source
					.withTag(DamageTypeTagKeys.IS_PLAYER_ATTACK, true)
				)
		))
		.buildAndRegister();
	public static final IAdvancement KILL_10_CHICKEN_IN_AIR = buildBase(KILL_A_BRAZE_WITH_A_BRAZE_ROD_WHILE_ON_FIRE, "kill_10_chicken_in_air").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.GUSTER_BANNER_PATTERN))
		.withReward(rewards()
			.withExp(80)
			.withTrophy(ItemStack.of(Material.FEATHER))
		)
		.buildAndRegister();
	public static final IAdvancement GET_TNT = buildBase(WEAPONRY_ROOT, "get_tnt").display(display().xy(1F, -5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.TNT))
		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.TNT))))
		.buildAndRegister();
	public static final IAdvancement BLOW_UP_A_CREEPER_WITH_TNT = buildBase(GET_TNT, "blow_up_a_creeper_with_tnt").display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.CREEPER_HEAD))
		.withReward(rewards().addItems(ItemStack.of(Material.TNT, 5)))
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity
					.withEntityType(EntityType.CREEPER)
				)
				.withDamage(source -> source
					.withTag(DamageTypeTagKeys.IS_EXPLOSION, true)
					.withDirectEntity(entity -> entity
						.withEntityType(EntityType.TNT)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement GET_A_ZOMBIE_HEAD = buildBase(BLOW_UP_A_CREEPER_WITH_TNT, "get_a_zombie_head").display(display().x(1F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(Material.ZOMBIE_HEAD))
		.withReward(rewards().withExp(250))
		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.ZOMBIE_HEAD))))
		.buildAndRegister();
	public static final IAdvancement IGNITE_250_TNT = buildBase(GET_A_ZOMBIE_HEAD, "ignite_250_tnt").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(ItemUtil.glint(Material.TNT)))
		.withReward(rewards().withExp(90).addItems(ItemStack.of(Material.TNT, 4)))
		.requiredProgress(simple(250))
		.buildAndRegister();
	public static final IAdvancement IGNITE_CHARGED_CREEPER_MIDAIR = buildBase(IGNITE_250_TNT, "ignite_charged_creeper_midair").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).withAdvancementFrame(AdvancementFrame.CRESTED).icon(ItemUtil.glint(Material.FIREWORK_ROCKET)))
		.withReward(rewards()
			.withExp(65)
			.withExtraMessage(player -> Messenger.messenger(player).getMessage("advancement.reward.shearable_creepers"))
		)
		.buildAndRegister();
	public static final IAdvancement BLOW_UP_ALL_MONSTERS_WITH_TNT = buildBase(IGNITE_CHARGED_CREEPER_MIDAIR, "blow_up_all_monsters_with_tnt").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.FIRE_CHARGE))
		.withReward(rewards()
			.withExp(100)
			.addItems(ItemStack.of(Material.TNT, 25))
			.withTrophy(() -> {
				var item = ItemStack.of(Material.LEATHER_CHESTPLATE);
				item.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(Color.fromRGB(14364442)));
				item.setData(DataComponentTypes.TRIM, ItemArmorTrim.itemArmorTrim(new ArmorTrim(TrimMaterial.QUARTZ, TrimPattern.HOST)));
				return item;
			})
		)
		.buildAndRegister(BlowUpAllMonstersWithTNTAdvancement::new);
	public static final IAdvancement GET_A_CROSSBOW = buildBase(WEAPONRY_ROOT, "get_a_crossbow").display(display().xy(1F, -6F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.TRIPWIRE_HOOK))
		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.CROSSBOW))))
		.buildAndRegister();
	public static final IAdvancement SHOOT_A_CROSSBOW = buildBase(GET_A_CROSSBOW, "shoot_a_crossbow").display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.CROSSBOW))
		.withReward(rewards().addItems(ItemStack.of(Material.ARROW, 8)))
		.requiredProgress(vanilla(shotCrossbow().withItem(ItemTriggerCondition.of(Material.CROSSBOW))))
		.buildAndRegister();
	public static final IAdvancement HOLD_A_CROSSBOW_AND_A_SPYGLASS = buildBase(SHOOT_A_CROSSBOW, "hold_a_crossbow_and_a_spyglass").display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.SPYGLASS))
		.withReward(rewards().withExp(20))
		.requiredProgress(vanilla(
			usingItem()
				.withPlayer(player -> player
					.withEquipment(equipment -> equipment
						.withOffHand(Material.CROSSBOW)
					)
				)
				.withItem(ItemTriggerCondition.of(Material.SPYGLASS))
		))
		.buildAndRegister();
	public static final IAdvancement KILL_A_PILLAGER_WITH_A_CROSSBOW = buildBase(HOLD_A_CROSSBOW_AND_A_SPYGLASS, "kill_a_pillager_with_a_crossbow").display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(ItemUtil.texturedHead(MoreMobHeads.PILLAGER)))
		.withReward(rewards().addItems(ItemStack.of(Material.EMERALD, 2)))
		.requiredProgress(vanilla(
			killedByArrow()
				.withEntity(entity -> entity.withEntityType(EntityType.PILLAGER))
				.withWeapon(ItemTriggerCondition.of(Material.CROSSBOW))
		))
		.buildAndRegister();
	public static final IAdvancement CROSSBOWS_HOTBAR = buildBase(KILL_A_PILLAGER_WITH_A_CROSSBOW, "crossbows_hotbar").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(() -> {
			var item = ItemStack.of(Material.CROSSBOW);
			item.setData(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectiles.chargedProjectiles().add(ItemStack.of(Material.ARROW)).build());
			return item;
		}))
		.withReward(rewards()
			.withExp(50)
			.addItems(ItemStack.of(Material.CROSSBOW), ItemStack.of(Material.ARROW, 32))
			.addItems(() -> {
				var item = ItemStack.of(Material.ENCHANTED_BOOK);
				item.setData(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantments.itemEnchantments().add(Enchantment.MULTISHOT, 1).build());
				return item;
			})
		)
		.buildAndRegister();
	public static final IAdvancement GET_A_FIREWORK = buildBase(CROSSBOWS_HOTBAR, "get_a_firework").display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.FIREWORK_ROCKET))
		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.FIREWORK_ROCKET))))
		.buildAndRegister();
	public static final IAdvancement PYROTECHNIC = buildBase(GET_A_FIREWORK, "pyrotechnic").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(() -> {
			var item = ItemStack.of(Material.FIREWORK_STAR);
			item.setData(DataComponentTypes.FIREWORK_EXPLOSION, FireworkEffect.builder().with(FireworkEffect.Type.BALL).withColor(Color.fromRGB(4312372)).build());
			return item;
		}))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.GUNPOWDER, 16), ItemStack.of(Material.DIAMOND)))
		.buildAndRegister(PyrotechnicAdvancement::new);
	public static final IAdvancement CROSSBOW_WITH_FIREWORK = buildBase(PYROTECHNIC, "crossbow_with_firework").display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(() -> {
			var item = ItemStack.of(Material.CROSSBOW);
			item.setData(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectiles.chargedProjectiles().add(ItemStack.of(Material.FIREWORK_ROCKET)).build());
			return item;
		}))
		.withReward(rewards().addItems(ItemStack.of(Material.GUNPOWDER, 8)))
		.buildAndRegister();
	public static final IAdvancement KILL_WITH_FIREWORK = buildBase(CROSSBOW_WITH_FIREWORK, "kill_with_firework").display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(() -> {
			var item = ItemStack.of(Material.FIREWORK_STAR);
			item.setData(DataComponentTypes.FIREWORK_EXPLOSION, FireworkEffect.builder().with(FireworkEffect.Type.BALL).withColor(Color.fromRGB(11743532)).build());
			return item;
		}))
		.withReward(rewards().addItems(ItemStack.of(Material.GUNPOWDER, 8)))
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withDamage(source -> source
					.withDirectEntity(entity -> entity.withEntityType(EntityType.FIREWORK_ROCKET))
				)
		))
		.buildAndRegister();
	public static final IAdvancement KILL_GHAST_WITH_FIREWORK = buildBase(KILL_WITH_FIREWORK, "kill_ghast_with_firework").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.GHAST_TEAR))
		.withReward(rewards().addItems(ItemStack.of(Material.GUNPOWDER, 16)))
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity.withEntityType(EntityType.GHAST))
				.withDamage(source -> source
					.withDirectEntity(entity -> entity.withEntityType(EntityType.FIREWORK_ROCKET))
				)
		))
		.buildAndRegister();
	public static final IAdvancement GET_A_MOB_BOTTLE = buildBase(WEAPONRY_ROOT, "get_a_mob_bottle")
		.display(display().xy(1F, -7F).fancyDescriptionParent(NamedTextColor.GREEN).icon(TrappedNewbieItems.BEE_BOTTLE))
		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(UtilizerTags.BOTTLEABLE_MOBS))))
		.buildAndRegister();
	public static final IAdvancement CAPTURE_SLIMES = buildBase(GET_A_MOB_BOTTLE, "capture_slimes")
		.display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(TrappedNewbieItems.SLIME_BOTTLE))
		.requiredProgress(vanilla(
			inventoryChanged()
				.withItems(ItemTriggerCondition.of(TrappedNewbieItems.SLIME_BOTTLE, TrappedNewbieItems.SLIME_BUCKET)),
			inventoryChanged()
				.withItems(ItemTriggerCondition.of(TrappedNewbieItems.MAGMA_CUBE_BOTTLE, TrappedNewbieItems.MAGMA_CUBE_BUCKET))
		))
		.buildAndRegister();
	public static final IAdvancement CAPTURE_ALL_BOTTLEABLE = buildBase(CAPTURE_SLIMES, "capture_all_bottleable")
		.display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(TrappedNewbieItems.ENDERMITE_BOTTLE))
		.requiredProgress(vanilla(
			UtilizerTags.BOTTLEABLE_MOBS.getValues().stream()
				.map(bottle -> inventoryChanged(bottle.key().value()).withItems(ItemTriggerCondition.of(bottle)))
				.toList()
		))
		.buildAndRegister();
	public static final IAdvancement GET_A_MOB_BOOK = buildBase(CAPTURE_ALL_BOTTLEABLE, "get_a_mob_book")
		.display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(TrappedNewbieItems.ALLAY_BOOK))
		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(UtilizerTags.BOOKABLE_MOBS))))
		.buildAndRegister();

	public static final AdvancementTab MAGIC_TAB = buildTab("magic", MANAGER)
		.display(simpleTabDisplay().inverseY().display(display().backgroundPathTexture("block/obsidian").icon(Material.ENCHANTING_TABLE)))
		.build();
	public static final IAdvancement MAGIC_ROOT = buildBase(MAGIC_TAB, "visual_root")
		.display(display().withAdvancementFrame(AdvancementFrame.SQUIRCLE).fancyDescriptionParent(GRAY).icon(Material.ENCHANTING_TABLE))
		.visibilityRule(ifDone(false, FIRST_POSSESSION))
		.requiredProgress(alwaysDone())
		.buildAndRegister();

	public static final AdvancementTab NETHER_TAB = buildTab("nether", MANAGER)
		.display(simpleTabDisplay().inverseY().display(display().backgroundPathTexture("block/netherrack").icon(Material.RED_NETHER_BRICKS)))
		.build();
	public static final IAdvancement NETHER_ROOT = buildBase(NETHER_TAB, "visual_root")
		.display(display().withAdvancementFrame(AdvancementFrame.SQUIRCLE).fancyDescriptionParent(GRAY).icon(Material.RED_NETHER_BRICKS))
		.visibilityRule(ifDone(false, FIRST_POSSESSION))
		.requiredProgress(alwaysDone())
		.buildAndRegister();
	public static final IAdvancement GET_A_STRIDER_BUCKET = buildBase(NETHER_ROOT, "get_a_strider_bucket")
		.display(display().x(1F).withAdvancementFrame(AdvancementFrame.GOAL).fancyDescriptionParent(NamedTextColor.AQUA).icon(TrappedNewbieItems.STRIDER_BUCKET))
		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.STRIDER_BUCKET))))
		.buildAndRegister();

	public static final AdvancementTab THE_END_TAB = buildTab("the_end", MANAGER)
		.display(simpleTabDisplay().inverseY().display(display().backgroundPathTexture("block/end_stone").icon(Material.END_STONE)))
		.build();
	public static final IAdvancement THE_END_ROOT = buildBase(THE_END_TAB, "visual_root")
		.display(display().withAdvancementFrame(AdvancementFrame.SQUIRCLE).fancyDescriptionParent(GRAY).icon(Material.END_STONE))
		.visibilityRule(ifDone(false, FIRST_POSSESSION))
		.requiredProgress(alwaysDone())
		.buildAndRegister();

	public static final AdvancementTab CHALLENGES_TAB = buildTab("challenges", MANAGER)
		.display(simpleTabDisplay().inverseY().display(display().backgroundPathBlock(Material.BEDROCK).icon(Material.ENDER_EYE)))
		.build();
	public static final IAdvancement CHALLENGES_ROOT = buildBase(CHALLENGES_TAB, "visual_root")
		.display(display().withAdvancementFrame(AdvancementFrame.SQUIRCLE).fancyDescriptionParent(GRAY).icon(Material.ENDER_EYE))
		.visibilityRule(ifDone(false, FIRST_POSSESSION))
		.requiredProgress(alwaysDone())
		.buildAndRegister();
	public static final IAdvancement DEFLECT_200 = buildBase(CHALLENGES_ROOT, "deflect_200").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().superChallenge().icon(ItemUtil.glint(Material.SKULL_BANNER_PATTERN)))
		.requiredProgress(vanilla(entityHurtPlayer().withDamage(damage -> damage.blocked().withMinDealtDamage(200D))))
		.withReward(rewards()
			.withExp(250)
			.withTrophy(() -> {
				var item = ItemStack.of(Material.SHIELD);
				item.setData(DataComponentTypes.UNBREAKABLE);
				return item;
			})
		)
		.buildAndRegister();
	public static final IAdvancement DEFLECT_SHIELD = buildBase(DEFLECT_200, "deflect_shield").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().superChallenge().icon(() -> {
			var item = ItemStack.of(Material.SHIELD);
			item.setData(DataComponentTypes.BASE_COLOR, DyeColor.RED);
			item.setData(DataComponentTypes.BANNER_PATTERNS, BannerPatternLayers.bannerPatternLayers(List.of(
				new Pattern(DyeColor.BLACK, PatternType.GRADIENT_UP),
				new Pattern(DyeColor.BLACK, PatternType.MOJANG)
			)));
			return item;
		}))
		.withReward(rewards()
			.withExp(300)
			.withTrophy(() -> {
				var item = ItemStack.of(Material.SHIELD);
				item.setData(DataComponentTypes.UNBREAKABLE);
				item.setData(DataComponentTypes.ITEM_MODEL, Material.OAK_PLANKS.key());
				return item;
			})
		)
		.requiredProgress(vanilla(entityHurtPlayer().withDamage(damage -> damage.blocked().withMinDealtDamage((double) Material.SHIELD.getMaxDurability()))))
		.buildAndRegister();
	public static final IAdvancement KILL_A_HOSTILE_MOB = buildBase(WEAPONRY_ROOT, "kill_a_hostile_mob").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.WOODEN_SWORD))
		.buildAndRegister(KillAHostileMobAdvancement::new);
	public static final IAdvancement KILL_HOSTILE_OVERWORLD_NIGHT_MOBS = buildBase(KILL_A_HOSTILE_MOB, "kill_hostile_overworld_night_mobs").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.IRON_SWORD))
		.withReward(rewards().withExp(50))
		.buildAndRegister(KillHostileOverworldNightMobsAdvancement::new);
	public static final IAdvancement KILL_HOSTILE_NETHER_MOBS = buildBase(KILL_HOSTILE_OVERWORLD_NIGHT_MOBS, "kill_hostile_nether_mobs").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.BLAZE_ROD))
		.withReward(rewards().withExp(50))
		.buildAndRegister(KillHostileNetherMobsAdvancement::new);
	public static final IAdvancement KILL_HOSTILE_END_MOBS = buildBase(KILL_HOSTILE_NETHER_MOBS, "kill_hostile_end_mobs").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.ENDER_PEARL))
		.withReward(rewards().withExp(50))
		.buildAndRegister(KillHostileEndMobsAdvancement::new);
	public static final IAdvancement KILL_HOSTILE_DUNGEON_MOBS = buildBase(KILL_HOSTILE_END_MOBS, "kill_hostile_dungeon_mobs").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.STONE_BRICKS))
		.withReward(rewards().withExp(50))
		.buildAndRegister(KillHostileDungeonMobsAdvancement::new);
	public static final IAdvancement KILL_ALL_HOSTILE_MOBS = buildBase(KILL_HOSTILE_DUNGEON_MOBS, "kill_all_hostile_mobs").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.DIAMOND_SWORD))
		.withReward(rewards()
			.withExp(100)
			.withTrophy(ItemStack.of(Material.BOOK))
		)
		.buildAndRegister(KillAllHostileMobsAdvancement::new);
	public static final IAdvancement KILL_MOBS_USING_THEIR_ITEMS = buildBase(KILL_ALL_HOSTILE_MOBS, "kill_mobs_using_their_items").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).withAdvancementFrame(AdvancementFrame.BUTTERFLY).torture().icon(Material.GRAY_STAINED_GLASS_PANE))
		.withReward(rewards()
			.withTrophy(ItemStack.of(Material.GRAY_STAINED_GLASS_PANE))
		)
		.buildAndRegister(KillAllUsingTheirItemsAdvancement::new);
	public static final IAdvancement GET_ATTACKED_BY_A_PHANTOM = buildBase(KILL_A_HOSTILE_MOB, "get_attacked_by_a_phantom").display(display().xy(0F, 1.5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.PHANTOM_MEMBRANE))
		.requiredProgress(vanilla(
			entityHurtPlayer()
				.withDamage(damage -> damage
					.withSourceEntity(entity -> entity.withEntityType(EntityType.PHANTOM))
				)
		))
		.buildAndRegister();
	public static final IAdvancement BLOCK_A_PHANTOM_WITH_A_SHIELD = buildBase(GET_ATTACKED_BY_A_PHANTOM, "block_a_phantom_with_a_shield").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(() -> {
			ItemStack item = shield(DyeColor.BLUE);
			item.setData(DataComponentTypes.BANNER_PATTERNS, BannerPatternLayers.bannerPatternLayers(List.of(
				new Pattern(DyeColor.LIME, PatternType.STRIPE_MIDDLE),
				new Pattern(DyeColor.BLUE, PatternType.STRIPE_CENTER),
				new Pattern(DyeColor.BLACK, PatternType.STRIPE_TOP),
				new Pattern(DyeColor.BLACK, PatternType.STRIPE_BOTTOM)
			)));
			return item;
		}))
		.withReward(rewards().addItems(ItemStack.of(Material.PHANTOM_MEMBRANE)))
		.requiredProgress(vanilla(
			entityHurtPlayer()
				.withDamage(damage -> damage
					.blocked()
					.withSourceEntity(entity -> entity.withEntityType(EntityType.PHANTOM))
				)
		))
		.buildAndRegister();
	public static final IAdvancement GET_A_STACK_OF_PHANTOM_MEMBRANES = buildBase(BLOCK_A_PHANTOM_WITH_A_SHIELD, "get_a_stack_of_phantom_membranes").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.PHANTOM_SPAWN_EGG))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.PHANTOM_MEMBRANE, 8)))
		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.PHANTOM_MEMBRANE).withAmount(Material.PHANTOM_MEMBRANE.getMaxStackSize()))))
		.buildAndRegister();
	public static final IAdvancement SHOOT_A_PHANTOM = buildBase(GET_A_STACK_OF_PHANTOM_MEMBRANES, "shoot_a_phantom").display(display().xy(-1F, -0.5F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.BOW))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.PHANTOM_MEMBRANE, 3)))
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity
					.withEntityType(EntityType.PHANTOM)
					.withDistanceToPlayer(distance -> distance.minAbsolute(30D))
				)
				.withDamage(damage -> damage
					.withTag(DamageTypeTagKeys.IS_PROJECTILE, true)
					.withDirectEntity(direct -> direct
						.withEntityType(Tag.ENTITY_TYPES_ARROWS)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement SHOOT_TWO_PHANTOMS_AT_ONCE = buildBase(SHOOT_A_PHANTOM, "shoot_two_phantoms_at_once").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.CROSSBOW))
		.withReward(rewards().withExp(65).addItems(ItemStack.of(Material.PHANTOM_MEMBRANE, 8)))
		.requiredProgress(vanilla(
			killedByArrow()
				.withEntity(
					EntityTriggerCondition.builder().withEntityType(EntityType.PHANTOM),
					EntityTriggerCondition.builder().withEntityType(EntityType.PHANTOM)
				)
				.withWeapon(ItemTriggerCondition.of(Material.CROSSBOW))
		))
		.buildAndRegister();
	public static final IAdvancement KILL_A_PHANTOM_IN_THE_NETHER = buildBase(SHOOT_TWO_PHANTOMS_AT_ONCE, "kill_a_phantom_in_the_nether").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.NETHERRACK))
		.withReward(rewards()
			.withExp(100)
			.addItems(ItemStack.of(Material.PHANTOM_MEMBRANE, 8))
			.addItems(() -> {
				var item = ItemStack.of(Material.ENCHANTED_BOOK);
				item.setData(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantments.itemEnchantments().add(Enchantment.FEATHER_FALLING, 3).build());
				return item;
			})
		)
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity
					.withEntityType(EntityType.PHANTOM)
					.withLocation(loc -> loc.withDimension(World.Environment.NETHER))
				)
		))
		.buildAndRegister();
	public static final IAdvancement CATCH_A_PHANTOM_WITH_A_FISHING_ROD = buildBase(GET_A_STACK_OF_PHANTOM_MEMBRANES, "catch_a_phantom_with_a_fishing_rod").display(display().xy(-1F, 0.5F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.FISHING_ROD))
		.withReward(rewards().withExp(40).addItems(ItemStack.of(Material.PHANTOM_MEMBRANE, 4)))
		.requiredProgress(vanilla(
			fishingRodHooked()
				.withEntity(entity -> entity
					.withEntityType(EntityType.PHANTOM)
				)
		))
		.buildAndRegister();
	public static final IAdvancement GET_KILLED_BY_A_PHANTOM_IN_THE_SKY = buildBase(CATCH_A_PHANTOM_WITH_A_FISHING_ROD, "get_killed_by_a_phantom_in_the_sky").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.PHANTOM_MEMBRANE))
		.withReward(rewards().withDeathExp(60))
		.requiredProgress(vanilla(
			entityKilledPlayer()
				.withEntity(entity -> entity
					.withEntityType(EntityType.PHANTOM)
				)
				.withPlayer(player -> player
					.withLocation(loc -> loc
						.minY(319D)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement KILL_A_PHANTOM_UNDERGROUND_WITH_A_SHOVEL = buildBase(GET_KILLED_BY_A_PHANTOM_IN_THE_SKY, "kill_a_phantom_underground_with_a_shovel").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(ItemUtil.glint(Material.IRON_SHOVEL)))
		.withReward(rewards()
			.withExp(80)
			.withTrophy(() -> {
				var item = ItemStack.of(Material.IRON_SHOVEL);
				item.addUnsafeEnchantment(Enchantment.SHARPNESS, 1);
				return item;
			})
		)
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity
					.withEntityType(EntityType.PHANTOM)
					.withLocation(loc -> loc
						.withDimension(World.Environment.NORMAL)
						.maxY(Bukkit.getWorlds().getFirst().getMinHeight() + 5D)
					)
				)
				.withPlayer(player -> player
					.withEquipment(equipment -> equipment
						.withMainHand(Material.IRON_SHOVEL)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement KILL_A_ZOMBIE = buildBase(KILL_A_HOSTILE_MOB, "kill_a_zombie").display(display().xy(0F, 4F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.ROTTEN_FLESH))
		.withReward(rewards().addItems(ItemStack.of(Material.ROTTEN_FLESH, 3)))
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity.withEntityType(EntityType.ZOMBIE))
		))
		.buildAndRegister();
	public static final IAdvancement GET_A_STACK_OF_ROTTEN_FLESH = buildBase(KILL_A_ZOMBIE, "get_a_stack_of_rotten_flesh").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.ZOMBIE_SPAWN_EGG))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.ROTTEN_FLESH, 8)).addItems(() -> {
			var item = ItemStack.of(Material.ENCHANTED_BOOK);
			item.setData(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantments.itemEnchantments().add(Enchantment.SHARPNESS, 2).build());
			return item;
		}))
		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.ROTTEN_FLESH).withAmount(64))))
		.buildAndRegister();
	public static final IAdvancement KILL_A_ZOMBIE_VILLAGER = buildBase(GET_A_STACK_OF_ROTTEN_FLESH, "kill_a_zombie_villager").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.IRON_NUGGET))
		.withReward(rewards().addItems(ItemStack.of(Material.ROTTEN_FLESH, 3)))
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity.withEntityType(EntityType.ZOMBIE_VILLAGER))
		))
		.buildAndRegister();
	public static final IAdvancement KILL_ALL_ZOMBIE_VILLAGERS = buildBase(KILL_A_ZOMBIE_VILLAGER, "kill_all_zombie_villagers").display(display().x(-1F).withAdvancementFrame(AdvancementFrame.BUTTERFLY).fancyDescriptionParent(NamedTextColor.DARK_RED).torture().icon(ItemUtil.glint(Material.DRAGON_BREATH)))
		.withReward(rewards()
			.withTrophy(VILLAGER_HEAD)
		)
		.buildAndRegister(KillAllZombieVillagersAdvancement::new);
	public static final IAdvancement KILL_A_BABY_ZOMBIE = buildBase(KILL_ALL_ZOMBIE_VILLAGERS, "kill_a_baby_zombie").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.PAPER))
			.requiredProgress(vanilla(
				playerKilledEntity()
					.withEntity(entity -> entity
						.withEntityType(Tag.ENTITY_TYPES_ZOMBIES)
						.withState(EntityStateTriggerCondition::baby)
					)
			))
			.buildAndRegister();
	public static final IAdvancement KILL_ALL_BABY_ZOMBIES = buildBase(KILL_A_BABY_ZOMBIE, "kill_all_baby_zombies").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.LEVER))
		.withReward(rewards().withExp(50))
		.buildAndRegister(KillAllBabyZombiesAdvancement::new);
	public static final IAdvancement KILL_A_CHICKEN_JOCKEY = buildBase(KILL_ALL_BABY_ZOMBIES, "kill_a_chicken_jockey").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(ItemUtil.texturedHead(MoreMobHeads.TEMPERATE_CHICKEN)))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.CHICKEN, 8), ItemStack.of(Material.FEATHER, 8)))
		.requiredProgress(vanillaAny(
			playerKilledEntity("chicken")
				.withEntity(entity -> entity
					.withEntityType(EntityType.CHICKEN)
					.withPassenger(passenger -> passenger
						.withEntityType(Tag.ENTITY_TYPES_ZOMBIES)
						.withState(EntityStateTriggerCondition::baby)
					)
				),
			playerKilledEntity("zombie")
				.withEntity(entity -> entity
					.withEntityType(Tag.ENTITY_TYPES_ZOMBIES)
					.withState(EntityStateTriggerCondition::baby)
					.withVehicle(vehicle -> vehicle
						.withEntityType(EntityType.CHICKEN)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement KILL_A_CHICKEN_JOCKEY_IN_A_MANSION = buildBase(KILL_A_CHICKEN_JOCKEY, "kill_a_chicken_jockey_in_a_mansion").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.PLAYER_HEAD))
		.withReward(rewards()
			.withExp(100)
			.withTrophy(ItemStack.of(Material.COOKED_CHICKEN))
		)
		.requiredProgress(vanillaAny(
			playerKilledEntity("chicken")
				.withEntity(entity -> entity
					.withEntityType(EntityType.CHICKEN)
					.withPassenger(passenger -> passenger
						.withEntityType(Tag.ENTITY_TYPES_ZOMBIES)
						.withState(EntityStateTriggerCondition::baby)
					)
					.withLocation(loc -> loc.withStructure(Structure.MANSION))
				),
			playerKilledEntity("zombie")
				.withEntity(entity -> entity
					.withEntityType(Tag.ENTITY_TYPES_ZOMBIES)
					.withState(EntityStateTriggerCondition::baby)
					.withVehicle(vehicle -> vehicle
						.withEntityType(EntityType.CHICKEN)
					)
					.withLocation(loc -> loc.withStructure(Structure.MANSION))
				)
		))
		.buildAndRegister();
	public static final IAdvancement KILL_ALL_JOCKEYS = buildBase(KILL_A_CHICKEN_JOCKEY_IN_A_MANSION, "kill_all_jockeys")
		.display(display().x(-1F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_RED).torture().icon(Material.ZOMBIE_HEAD))
		.withReward(rewards()
			.withExp(100)
			.withTrophy(() -> {
				var item = ItemStack.of(Material.HORN_CORAL);
				item.setData(DataComponentTypes.EQUIPPABLE, Equippable.equippable(EquipmentSlot.HEAD).build());
				return item;
			})
		)
		.buildAndRegister(KillAllJockeysAdvancement::new);
	public static final IAdvancement KILL_ALL_ALL_JOCKEYS = buildBase(KILL_ALL_JOCKEYS, "kill_all_all_jockeys")
		.display(display().x(-1F).withAdvancementFrame(AdvancementFrame.BUTTERFLY).fancyDescriptionParent(NamedTextColor.BLACK).superTorture().icon(Material.SKELETON_SKULL))
		.withReward(rewards()
			.withExp(100)
			.withTrophy(ItemStack.of(Material.BOW))
		)
		.buildAndRegister(KillAllAllJockeysAdvancement::new);
	public static final IAdvancement KILL_A_HUSK = buildBase(GET_A_STACK_OF_ROTTEN_FLESH, "kill_a_husk").display(display().xy(1F, 1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.DEAD_BUSH))
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity.withEntityType(EntityType.HUSK))
		))
		.buildAndRegister();
	public static final IAdvancement GET_KILLED_BY_A_HUSK_WHILE_RIDING_A_CAMEL = buildBase(GET_A_STACK_OF_ROTTEN_FLESH, "get_killed_by_a_husk_while_riding_a_camel").display(display().xy(0F, 1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(DESERT_HEAD))
		.withReward(rewards().withExp(40).addItems(ItemStack.of(Material.ROTTEN_FLESH, 4)))
		.requiredProgress(vanilla(
			entityKilledPlayer()
				.withEntity(entity -> entity.withEntityType(EntityType.HUSK))
				.withPlayer(player -> player
					.withVehicle(vehicle -> vehicle.withEntityType(EntityType.CAMEL))
				)
		))
		.buildAndRegister();
	public static final IAdvancement KILL_A_HUSK_IN_TUNDRA = buildBase(GET_KILLED_BY_A_HUSK_WHILE_RIDING_A_CAMEL, "kill_a_husk_in_tundra").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.SNOW_BLOCK))
		.withReward(rewards()
			.withExp(100)
			.addItems(ItemStack.of(Material.SAND, 32))
			.addItems(() -> {
				var item = ItemStack.of(Material.ENCHANTED_BOOK);
				item.setData(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantments.itemEnchantments().add(Enchantment.SMITE, 2).build());
				return item;
			})
			.withTrophy(ItemStack.of(Material.PACKED_ICE))
		)
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity
					.withEntityType(EntityType.HUSK)
					.withLocation(loc -> loc
						.withBiome(List.of(Biome.SNOWY_PLAINS, Biome.ICE_SPIKES))
						.minY(64D)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement FROZEN_HEART = buildBase(KILL_A_HUSK_IN_TUNDRA, "frozen_heart").display(display().x(-1F).withAdvancementFrame(AdvancementFrame.STAR).fancyDescriptionParent(NamedTextColor.DARK_RED).torture().icon(FROZEN_HEART_HEAD))
		.withReward(rewards().withExp(200).addItems(ItemStack.of(Material.SNOWBALL, 64)))
		.buildAndRegister(FrozenHeartAdvancement::new);
	public static final IAdvancement GIVE_A_ZOMBIE_A_TOTEM_OF_UNDYING = buildBase(FROZEN_HEART, "give_a_zombie_a_totem_of_undying").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.TOTEM_OF_UNDYING))
		.requiredProgress(vanilla(
			thrownItemPickedUpByEntity()
				.withItem(ItemTriggerCondition.of(Material.TOTEM_OF_UNDYING))
				.withEntity(entity -> entity.withEntityType(Tag.ENTITY_TYPES_ZOMBIES))
		))
		.buildAndRegister();
	public static final IAdvancement KILL_A_MOB_WEARING_A_PUMPKIN = buildBase(GIVE_A_ZOMBIE_A_TOTEM_OF_UNDYING, "kill_a_mob_wearing_a_pumpkin").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.CARVED_PUMPKIN))
		.withReward(rewards().addItems(ItemStack.of(Material.CARVED_PUMPKIN, 8)))
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity
					.withEquipment(equipment -> equipment
						.withHelmet(Material.CARVED_PUMPKIN)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement KILL_A_MOB_WEARING_A_JACK_O_LANTERN = buildBase(KILL_A_MOB_WEARING_A_PUMPKIN, "kill_a_mob_wearing_a_jack_o_lantern").display(display().x(-1F).withAdvancementFrame(AdvancementFrame.STAR).fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.JACK_O_LANTERN))
		.withReward(rewards()
			.withExp(100)
			.addItems(ItemStack.of(Material.JACK_O_LANTERN, 64))
			.withTrophy(ItemStack.of(Material.CARVED_PUMPKIN))
		)
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity
					.withEquipment(equipment -> equipment
						.withHelmet(Material.JACK_O_LANTERN)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement KILL_ALL_MOBS_WEARING_A_JACK_O_LANTERN = buildBase(KILL_A_MOB_WEARING_A_JACK_O_LANTERN, "kill_all_mobs_wearing_a_jack_o_lantern").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().icon(Material.JACK_O_LANTERN))
		.withReward(rewards()
			.withTrophy(JACK_O_LANTERN_HEAD)
		)
		.buildAndRegister(KillAllPumpkinMobsAdvancement::new);
	public static final IAdvancement KILL_1K_MOBS_WEARING_A_JACK_O_LANTERN = buildBase(KILL_ALL_MOBS_WEARING_A_JACK_O_LANTERN, "kill_1k_mobs_wearing_a_jack_o_lantern").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().icon(ItemUtil.glint(Material.JACK_O_LANTERN)))
		.withReward(rewards()
			.withTrophy(ItemStack.of(Material.SKELETON_SKULL))
		)
		.requiredProgress(simple(1000))
		.buildAndRegister();
	public static final IAdvancement KILL_A_DROWNED = buildBase(GET_A_STACK_OF_ROTTEN_FLESH, "kill_a_drowned").display(display().xy(1F, -1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.SEAGRASS))
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity.withEntityType(EntityType.DROWNED))
		))
		.buildAndRegister();
	public static final IAdvancement BLOCK_A_DROWNED_TRIDENT = buildBase(GET_A_STACK_OF_ROTTEN_FLESH, "block_a_drowned_trident").display(display().xy(0F, -1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(() -> {
			ItemStack item = shield(DyeColor.CYAN);
			item.setData(DataComponentTypes.BANNER_PATTERNS, BannerPatternLayers.bannerPatternLayers(List.of(
				new Pattern(DyeColor.LIGHT_BLUE, PatternType.STRIPE_MIDDLE),
				new Pattern(DyeColor.CYAN, PatternType.STRIPE_CENTER),
				new Pattern(DyeColor.GREEN, PatternType.TRIANGLES_TOP),
				new Pattern(DyeColor.LIGHT_BLUE, PatternType.STRIPE_BOTTOM),
				new Pattern(DyeColor.BROWN, PatternType.TRIANGLES_BOTTOM)
			)));
			return item;
		}))
		.requiredProgress(vanilla(
			entityHurtPlayer()
				.withDamage(damage -> damage
					.blocked()
					.withDamageSource(source -> source
						.withTag(DamageTypeTagKeys.IS_PROJECTILE, true)
						.withDirectEntity(entity -> entity
							.withEntityType(EntityType.TRIDENT)
						)
					)
					.withSourceEntity(entity -> entity.withEntityType(EntityType.DROWNED))
				)
		))
		.buildAndRegister();
	public static final IAdvancement OBTAIN_A_TRIDENT = buildBase(BLOCK_A_DROWNED_TRIDENT, "obtain_a_trident").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.TRIDENT))
		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.TRIDENT))))
		.buildAndRegister();
	public static final IAdvancement KILL_A_DROWNED_HOLDING_A_NAUTILUS_SHELL = buildBase(OBTAIN_A_TRIDENT, "kill_a_drowned_holding_a_nautilus_shell").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.NAUTILUS_SHELL))
		.withReward(rewards().addItems(ItemStack.of(Material.NAUTILUS_SHELL)))
		.requiredProgress(vanillaAny(
			playerKilledEntity()
				.withEntity(entity -> entity
					.withEntityType(EntityType.DROWNED)
					.withEquipment(equipment -> equipment
						.withMainHand(Material.NAUTILUS_SHELL)
					)
				),
			playerKilledEntity()
				.withEntity(entity -> entity
					.withEntityType(EntityType.DROWNED)
					.withEquipment(equipment -> equipment
						.withOffHand(Material.NAUTILUS_SHELL)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement KILL_A_DROWNED_HOLDING_A_NAUTILUS_SHELL_AND_A_TRIDENT = buildBase(KILL_A_DROWNED_HOLDING_A_NAUTILUS_SHELL, "kill_a_drowned_holding_a_nautilus_shell_and_a_trident").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(ItemUtil.glint(Material.TRIDENT)))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.TRIDENT)))
		.requiredProgress(vanillaAny(
			playerKilledEntity()
				.withEntity(entity -> entity
					.withEntityType(EntityType.DROWNED)
					.withEquipment(equipment -> equipment
						.withMainHand(Material.TRIDENT)
						.withOffHand(Material.NAUTILUS_SHELL)
					)
				),
			playerKilledEntity()
				.withEntity(entity -> entity
					.withEntityType(EntityType.DROWNED)
					.withEquipment(equipment -> equipment
						.withMainHand(Material.NAUTILUS_SHELL)
						.withOffHand(Material.TRIDENT)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement KILL_ALL_DIAMOND_ZOMBIE = buildBase(KILL_A_DROWNED_HOLDING_A_NAUTILUS_SHELL_AND_A_TRIDENT, "kill_all_diamond_zombie").display(display().x(-1F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(Material.DIAMOND_HELMET))
		.withReward(rewards()
			.withExp(100)
			.addItems(ItemStack.of(Material.DIAMOND, 9))
			.withTrophy(ItemStack.of(Material.LIME_STAINED_GLASS_PANE))
		)
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity
					.withEntityType(UtilizerTags.HUMAN_LIKE_ZOMBIES)
					.withEquipment(equipment -> equipment
						.withHelmet(Material.DIAMOND_HELMET)
						.withChestplate(Material.DIAMOND_CHESTPLATE)
						.withLeggings(Material.DIAMOND_LEGGINGS)
						.withBoots(Material.DIAMOND_BOOTS)
						.withMainHand(Material.DIAMOND_SWORD)
					)
				)
		))
		.buildAndRegister();
	// MCCheck: 1.21.10, nbt
	public static final IAdvancement KILL_ALL_DIAMOND_ZOMBIE_BUT_HARD = buildBase(KILL_ALL_DIAMOND_ZOMBIE, "kill_all_diamond_zombie_but_hard").display(display().x(-1F).withAdvancementFrame(AdvancementFrame.BUTTERFLY).fancyDescriptionParent(NamedTextColor.BLACK).cheat().icon(ASSASSIN_HEAD))
		.withReward(rewards()
			.withTrophy(ItemStack.of(Material.PLAYER_HEAD))
		)
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity
					.withEntityType(UtilizerTags.HUMAN_LIKE_ZOMBIES)
					.withEquipment(equipment -> equipment
						.withHelmet(Material.DIAMOND_HELMET)
						.withChestplate(Material.DIAMOND_CHESTPLATE)
						.withLeggings(Material.DIAMOND_LEGGINGS)
						.withBoots(Material.DIAMOND_BOOTS)
						.withMainHand(Material.IRON_SWORD)
					)
					.withNbt("{CanPickUpLoot:0b}")
				)
		))
		.buildAndRegister();
	public static final IAdvancement ZOMBIE_FAMILY_REUNION = buildBase(KILL_ALL_DIAMOND_ZOMBIE_BUT_HARD, "zombie_family_reunion").display(display().x(-1F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).torture().icon(ItemUtil.texturedHead(MoreMobHeads.ZOMBIFIED_PIGLIN)))
		.withReward(rewards()
			.withExp(100)
			.addItems(ItemStack.of(Material.ROTTEN_FLESH, 64))
			.withTrophy(THE_WALKING_HEAD)
		)
		.buildAndRegister();
	public static final IAdvancement COMMUNISM = buildBase(ZOMBIE_FAMILY_REUNION, "communism").display(display().x(-1F).withAdvancementFrame(AdvancementFrame.BUTTERFLY).fancyDescriptionParent(NamedTextColor.DARK_RED).torture().icon(Material.GOLDEN_HOE))
		.withReward(rewards()
			.withTrophy(ItemStack.of(Material.BOOK))
		)
		.buildAndRegister(CommunismAdvancement::new);
	public static final IAdvancement KILL_A_SPIDER = buildBase(KILL_A_HOSTILE_MOB, "kill_a_spider").display(display().xy(0F, -1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.STRING))
		.withReward(rewards().addItems(ItemStack.of(Material.STRING, 3)))
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity.withEntityType(EntityType.SPIDER))
		))
		.buildAndRegister();
	public static final IAdvancement KILL_A_CAVE_SPIDER = buildBase(KILL_A_SPIDER, "kill_a_cave_spider").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.SPIDER_EYE))
		.withReward(rewards().addItems(ItemStack.of(Material.STRING, 3), ItemStack.of(Material.SPIDER_EYE)))
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity.withEntityType(EntityType.CAVE_SPIDER))
		))
		.buildAndRegister();
	public static final IAdvancement GET_A_STACK_OF_STRINGS = buildBase(KILL_A_CAVE_SPIDER, "get_a_stack_of_strings").display(display().x(-1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.SPIDER_SPAWN_EGG))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.STRING, 8), ItemStack.of(Material.SPIDER_EYE, 3)).addItems(() -> {
			var item = ItemStack.of(Material.ENCHANTED_BOOK);
			item.setData(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantments.itemEnchantments().add(Enchantment.PROTECTION, 2).build());
			return item;
		}))
		.requiredProgress(vanilla(
			inventoryChanged()
				.withItems(ItemTriggerCondition.of(Material.STRING).withAmount(64))
		))
		.buildAndRegister();
	public static final IAdvancement KILL_A_SPIDER_SKELETON_JOCKEY = buildBase(GET_A_STACK_OF_STRINGS, "kill_a_spider_skeleton_jockey").display(display().x(-1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.COBWEB))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.STRING, 8), ItemStack.of(Material.BONE, 8)))
		.requiredProgress(vanillaAny(
			playerKilledEntity("spider")
				.withEntity(entity -> entity
					.withEntityType(EntityType.SPIDER)
					.withPassenger(passenger -> passenger
						.withEntityType(Tag.ENTITY_TYPES_SKELETONS)
					)
				),
			playerKilledEntity("skeleton")
				.withEntity(entity -> entity
					.withEntityType(Tag.ENTITY_TYPES_SKELETONS)
					.withVehicle(vehicle -> vehicle
						.withEntityType(EntityType.SPIDER)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement KILL_500_JOCKEYS = buildBase(KILL_A_SPIDER_SKELETON_JOCKEY, "kill_500_jockeys").display(display().x(-1F).withAdvancementFrame(AdvancementFrame.STAR).fancyDescriptionParent(NamedTextColor.DARK_RED).torture().icon(Material.SKELETON_SKULL))
		.withReward(rewards().withTrophy(ItemStack.of(Material.EMERALD)))
		.requiredProgress(simple(500))
		.buildAndRegister();
	public static final IAdvancement KILL_INVISIBLE_SPIDER = buildBase(KILL_500_JOCKEYS, "kill_invisible_spider").display(display().x(-1F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(Material.FERMENTED_SPIDER_EYE))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.FERMENTED_SPIDER_EYE), ItemStack.of(Material.GOLDEN_CARROT)))
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity
					.withEntityType(UtilizerTags.SPIDERS)
					.withEffects(PotionEffectType.INVISIBILITY)
				)
		))
		.buildAndRegister();
	public static final IAdvancement KILL_ALL_SPIDERS_WITH_EFFECTS = buildBase(KILL_INVISIBLE_SPIDER, "kill_all_spiders_with_effects").display(display().x(-1F).withAdvancementFrame(AdvancementFrame.STAR).fancyDescriptionParent(NamedTextColor.DARK_RED).torture().icon(ARACHNOPHOBIA_HEAD))
		.withReward(rewards().withExp(140).addItems(ItemStack.of(Material.STRING, 16)))
		.requiredProgress(vanilla(
			killedByArrow()
				.withWeapon(ItemTriggerCondition.of(Material.CROSSBOW))
				.withEntity(
					EntityTriggerCondition.builder().withEntityType(EntityType.SPIDER).withEffects(PotionEffectType.INVISIBILITY),
					EntityTriggerCondition.builder().withEntityType(EntityType.SPIDER).withEffects(PotionEffectType.REGENERATION),
					EntityTriggerCondition.builder().withEntityType(EntityType.SPIDER).withEffects(PotionEffectType.STRENGTH),
					EntityTriggerCondition.builder().withEntityType(EntityType.SPIDER).withEffects(PotionEffectType.SPEED)
				)
		))
		.buildAndRegister();
	public static final IAdvancement KILL_A_SKELETON = buildBase(KILL_A_HOSTILE_MOB, "kill_a_skeleton").display(display().xy(0F, -3F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.BONE))
		.withReward(rewards().addItems(ItemStack.of(Material.BONE, 3), ItemStack.of(Material.ARROW, 8)))
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity.withEntityType(EntityType.SKELETON))
		))
		.buildAndRegister();
	public static final IAdvancement GET_A_BONE_MEAL = buildBase(KILL_A_SKELETON, "get_a_bone_meal").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.BONE_MEAL))
		.withReward(rewards().addItems(ItemStack.of(Material.BONE_MEAL, 8)))
		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.BONE_MEAL))))
		.buildAndRegister();
	public static final IAdvancement DEFLECT_A_SKELETON_ARROW = buildBase(GET_A_BONE_MEAL, "deflect_a_skeleton_arrow").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(() -> {
			var item = ItemStack.of(Material.SHIELD);
			item.setData(DataComponentTypes.BANNER_PATTERNS, BannerPatternLayers.bannerPatternLayers(List.of(
				new Pattern(DyeColor.LIGHT_GRAY, PatternType.TRIANGLE_BOTTOM),
				new Pattern(DyeColor.LIGHT_GRAY, PatternType.TRIANGLE_TOP),
				new Pattern(DyeColor.LIGHT_GRAY, PatternType.RHOMBUS),
				new Pattern(DyeColor.LIGHT_GRAY, PatternType.CURLY_BORDER),
				new Pattern(DyeColor.WHITE, PatternType.SKULL)
			)));
			return item;
		}))
		.requiredProgress(vanilla(
			entityHurtPlayer()
				.withDamage(damage -> damage
					.blocked()
					.withDamageSource(source -> source
						.withTag(DamageTypeTagKeys.IS_PROJECTILE, true)
					)
					.withSourceEntity(source -> source
						.withEntityType(Tag.ENTITY_TYPES_SKELETONS)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement GET_A_STACK_OF_BONES = buildBase(DEFLECT_A_SKELETON_ARROW, "get_a_stack_of_bones").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.SKELETON_SPAWN_EGG))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.BONE, 8), ItemStack.of(Material.ARROW, 16)).addItems(() -> {
				var item = ItemStack.of(Material.ENCHANTED_BOOK);
				item.setData(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantments.itemEnchantments().add(Enchantment.POWER, 2).build());
				return item;
			})
		)
		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.BONE).withAmount(64))))
		.buildAndRegister();
	public static final IAdvancement GET_A_STACK_OF_BONE_BLOCKS = buildBase(GET_A_STACK_OF_BONES, "get_a_stack_of_bone_blocks").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.BONE_BLOCK))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.BONE_BLOCK, 8)))
		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.BONE_BLOCK).withAmount(64))))
		.buildAndRegister();
	public static final IAdvancement BONE_TO_PARTY = buildBase(GET_A_STACK_OF_BONE_BLOCKS, "bone_to_party").display(display().x(-1F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWExNWQwNWQwYTg2ODk2OGM2NjhiNWQ4NmM0MDgzMDUzZWJlOWZkZWE5Y2U0YWExMzE1MDM1ZDNiYTg4NDNkIn19fQ==")))
		.withReward(rewards()
			.withExp(50)
			.addItems(ItemStack.of(Material.BONE, 64))
			.withTrophy(SANS_HEAD)
		)
		.buildAndRegister();
	public static final IAdvancement KILL_A_STRAY = buildBase(GET_A_BONE_MEAL, "kill_a_stray").display(display().xy(1F, 1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.SNOWBALL))
		.withReward(rewards().addItems(ItemStack.of(Material.BONE, 3)).addItems(potionItem(Material.TIPPED_ARROW, PotionType.SLOWNESS).asQuantity(8)))
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity.withEntityType(EntityType.STRAY))
		))
		.buildAndRegister();
	public static final IAdvancement KILL_A_STRAY_IN_A_DESERT = buildBase(GET_A_BONE_MEAL, "kill_a_stray_in_a_desert").display(display().y(1F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(Material.SAND))
		.withReward(rewards()
			.withExp(100)
			.addItems(potionItem(Material.TIPPED_ARROW, PotionType.SLOWNESS).asQuantity(32))
			.addItems(() -> {
				var item = ItemStack.of(Material.ENCHANTED_BOOK);
				item.setData(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantments.itemEnchantments().add(Enchantment.PROJECTILE_PROTECTION, 2).build());
				return item;
			})
			.withTrophy(ItemStack.of(Material.MAGMA_BLOCK))
		)
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity
					.withEntityType(EntityType.STRAY)
					.withLocation(loc -> loc
						.withBiome(Biome.DESERT)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement RIDE_A_SKELETON_HORSE = buildBase(KILL_A_STRAY_IN_A_DESERT, "ride_a_skeleton_horse").display(display().x(-1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.BONE))
		.withReward(rewards()
			.addItems(ItemStack.of(Material.ARROW, 32))
			.addItems(() -> {
				var item = ItemStack.of(Material.ENCHANTED_BOOK);
				item.setData(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantments.itemEnchantments().add(Enchantment.POWER, 3).build());
				return item;
			})
		)
		.requiredProgress(vanilla(
			startedRiding()
				.withPlayer(player -> player
					.withVehicle(vehicle -> vehicle
						.withEntityType(EntityType.SKELETON_HORSE)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement KILL_400_SKELETON_HORSES = buildBase(RIDE_A_SKELETON_HORSE, "kill_400_skeleton_horses").display(display().x(-1F).withAdvancementFrame(AdvancementFrame.BUTTERFLY).fancyDescriptionParent(NamedTextColor.BLACK).cheat().icon(ItemUtil.glint(Material.IRON_HELMET)))
		.withReward(rewards()
			.withTrophy(ItemStack.of(Material.BONE))
		)
		.requiredProgress(simple(400))
		.buildAndRegister();
	public static final IAdvancement KILL_AN_ELDER_GUARDIAN_IN_A_DESERT = buildBase(KILL_400_SKELETON_HORSES, "kill_an_elder_guardian_in_a_desert").display(display().x(-1F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(ItemUtil.glint(Material.CACTUS)))
		.withReward(rewards().withExp(200))
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity
					.withEntityType(EntityType.ELDER_GUARDIAN)
					.withLocation(loc -> loc
						.withBiome(Biome.DESERT)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement KILL_A_BOGGED = buildBase(GET_A_BONE_MEAL, "kill_a_bogged").display(display().xy(1F, -1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.SLIME_BALL))
		.withReward(rewards().addItems(ItemStack.of(Material.BONE, 3)))
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity.withEntityType(EntityType.BOGGED))
		))
		.buildAndRegister();
	public static final IAdvancement SHOOT_A_MOB_WITH_A_POISON_ARROW = buildBase(GET_A_BONE_MEAL, "shoot_a_mob_with_a_poison_arrow").display(display().y(-1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(potionItem(Material.TIPPED_ARROW, PotionType.POISON)))
		.withReward(rewards().addItems(potionItem(Material.TIPPED_ARROW, PotionType.POISON).asQuantity(8)))
		.requiredProgress(vanillaAny(
			playerHurtEntity(PotionType.POISON.key().value())
				.withDamage(damage -> damage
					.withDamageSource(source -> source
						.withTag(DamageTypeTagKeys.IS_PROJECTILE, true)
						.withDirectEntity(entity -> entity
							.withEntityType(EntityType.ARROW)
							.withNbt("{item:{components:{\"%s\":{potion:\"%s\"}}}}".formatted(DataComponentTypes.POTION_CONTENTS.key(), PotionType.POISON.key()))
						)
					)
				),
			playerHurtEntity(PotionType.LONG_POISON.key().value())
				.withDamage(damage -> damage
					.withDamageSource(source -> source
						.withTag(DamageTypeTagKeys.IS_PROJECTILE, true)
						.withDirectEntity(entity -> entity
							.withEntityType(EntityType.ARROW)
							.withNbt("{item:{components:{\"%s\":{potion:\"%s\"}}}}".formatted(DataComponentTypes.POTION_CONTENTS.key(), PotionType.LONG_POISON.key()))
						)
					)
				),
			playerHurtEntity(PotionType.STRONG_POISON.key().value())
				.withDamage(damage -> damage
					.withDamageSource(source -> source
						.withTag(DamageTypeTagKeys.IS_PROJECTILE, true)
						.withDirectEntity(entity -> entity
							.withEntityType(EntityType.ARROW)
							.withNbt("{item:{components:{\"%s\":{potion:\"%s\"}}}}".formatted(DataComponentTypes.POTION_CONTENTS.key(), PotionType.STRONG_POISON.key()))
						)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement SUSPICIOUS_DUEL = buildBase(SHOOT_A_MOB_WITH_A_POISON_ARROW, "suspicious_duel").display(display().x(-1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(ItemUtil.glint(Material.BOW)))
		.withReward(rewards().withExp(50))
		.requiredProgress(vanilla(
			usingItem()
				.withItem(ItemTriggerCondition.of(Material.SPYGLASS))
				.withPlayer(player -> player
					.withLookingAt(entity -> entity
						.withEntityType(UtilizerTags.BOW_SKELETONS)
						.withTargetedEntity(targetEntity -> targetEntity
							.withEntityType(UtilizerTags.BOW_SKELETONS)
							.withTargetedEntity(targetEntity2 -> targetEntity2
								.withEntityType(UtilizerTags.BOW_SKELETONS)
							)
						)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement GET_A_SKELETON_SKULL = buildBase(SUSPICIOUS_DUEL, "get_a_skeleton_skull").display(display().x(-1F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(Material.SKELETON_SKULL))
		.withReward(rewards().withExp(250))
		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.SKELETON_SKULL))))
		.buildAndRegister();
	public static final IAdvancement CHESTFUL_OF_SKELETON_SKULLS = buildBase(GET_A_SKELETON_SKULL, "chestful_of_skeleton_skulls").display(display().x(-1F).withAdvancementFrame(AdvancementFrame.STAR).fancyDescriptionParent(NamedTextColor.DARK_RED).torture().icon(Material.SKELETON_SKULL))
		.withReward(rewards()
			.withTrophy(ItemStack.of(Material.DIRT))
		)
		.buildAndRegister();
	public static final IAdvancement KILL_A_CREEPER = buildBase(KILL_A_HOSTILE_MOB, "kill_a_creeper").display(display().xy(0F, -6F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.GUNPOWDER))
		.withReward(rewards().addItems(ItemStack.of(Material.GUNPOWDER, 3)))
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity.withEntityType(EntityType.CREEPER))
		))
		.buildAndRegister();
	public static final IAdvancement SHOOT_A_CREEPER = buildBase(KILL_A_CREEPER, "shoot_a_creeper").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.BOW))
		.withReward(rewards().addItems(ItemStack.of(Material.GUNPOWDER, 4)))
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity.withEntityType(EntityType.CREEPER))
				.withDamage(damage -> damage
					.withTag(DamageTypeTagKeys.IS_PROJECTILE, true)
					.withDirectEntity(entity -> entity
						.withEntityType(Tag.ENTITY_TYPES_ARROWS)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement BLOCK_A_CREEPER = buildBase(SHOOT_A_CREEPER, "block_a_creeper").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(() -> {
			ItemStack item = shield(DyeColor.LIME);
			item.setData(DataComponentTypes.BANNER_PATTERNS, BannerPatternLayers.bannerPatternLayers(List.of(
				new Pattern(DyeColor.LIME, PatternType.HALF_HORIZONTAL),
				new Pattern(DyeColor.LIME, PatternType.HALF_HORIZONTAL_BOTTOM),
				new Pattern(DyeColor.BLACK, PatternType.CREEPER),
				new Pattern(DyeColor.BLACK, PatternType.CREEPER),
				new Pattern(DyeColor.BLACK, PatternType.CREEPER)
			)));
			return item;
		}))
		.withReward(rewards().addItems(ItemStack.of(Material.GUNPOWDER, 8)))
		.requiredProgress(vanilla(
			entityHurtPlayer()
				.withDamage(damage -> damage
					.blocked()
					.withSourceEntity(entity -> entity
						.withEntityType(EntityType.CREEPER)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement IGNITE_A_CREEPER = buildBase(BLOCK_A_CREEPER, "ignite_a_creeper").display(display().xy(-1F, 0.5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.FLINT_AND_STEEL))
		.withReward(rewards().addItems(ItemStack.of(Material.GUNPOWDER, 4)))
		.requiredProgress(vanilla(
			playerInteractedWithEntity()
				.withItem(ItemTriggerCondition.of(Tag.ITEMS_CREEPER_IGNITERS))
				.withEntity(entity -> entity.withEntityType(EntityType.CREEPER))
		))
		.buildAndRegister();
	public static final IAdvancement DIE_FROM_A_CREEPER = buildBase(BLOCK_A_CREEPER, "die_from_a_creeper").display(display().xy(-1F, -0.5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTY4MzI1MzQ4YmI4MzEwZjFkNzQ4ZmRjYWFiZjQzZGNmOWJkMjQ0ZDE4OWNjYjE4ZDZhYzU0N2ZiMDA0NzhhNCJ9fX0=")))
		.requiredProgress(vanilla(
			entityKilledPlayer()
				.withEntity(entity -> entity.withEntityType(EntityType.CREEPER))
		))
		.buildAndRegister();
	public static final IAdvancement GET_A_STACK_OF_GUNPOWDER = buildBase(IGNITE_A_CREEPER, "get_a_stack_of_gunpowder").display(display().xy(-1F, -0.5F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.CREEPER_SPAWN_EGG))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.GUNPOWDER, 8)).addItems(() -> {
			var item = ItemStack.of(Material.ENCHANTED_BOOK);
			item.setData(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantments.itemEnchantments().add(Enchantment.LOOTING, 1).build());
			return item;
		}))
		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.GUNPOWDER).withAmount(Material.GUNPOWDER.getMaxStackSize()))))
		.buildAndRegister();
	public static final IAdvancement LOOK_AT_A_CREEPER_CLOSELY = buildBase(IGNITE_A_CREEPER, "look_at_a_creeper_closely").display(display().xy(1F, 0.5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.SPYGLASS))
		.requiredProgress(vanilla(
			usingItem()
				.withItem(ItemTriggerCondition.of(Material.SPYGLASS))
				.withPlayer(player -> player
					.withLookingAt(entity -> entity
						.withEntityType(EntityType.CREEPER)
						.withDistanceToPlayer(distance -> distance
							.maxAbsolute(1D)
						)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement BARRELFUL_OF_GUNPOWDER = buildBase(GET_A_STACK_OF_GUNPOWDER, "barrelful_of_gunpowder").display(display().xy(-1F, 0.5F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODIyYTQ4YTU3NTllZGRlZjllMjkxOGZjODU5OTZmODQ5MWNjOTI1NzhkNTRkY2Q2MmUyYjZkOTEzYmZiNDIxZSJ9fX0=")))
		.withReward(rewards().withExp(150))
		.buildAndRegister();
	public static final IAdvancement GET_A_CREEPER_HEAD = buildBase(GET_A_STACK_OF_GUNPOWDER, "get_a_creeper_head").display(display().xy(-1F, -0.5F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(Material.CREEPER_HEAD))
		.withReward(rewards().withExp(250))
		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.CREEPER_HEAD))))
		.buildAndRegister();
	public static final IAdvancement KILL_A_MOB_WEARING_ITS_HEAD = buildBase(GET_A_CREEPER_HEAD, "kill_a_mob_wearing_its_head").display(display().x(-1F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(Material.WITHER_SKELETON_SKULL))
		.withReward(rewards()
			.withExp(100)
			.addItems(() -> {
				var item = ItemStack.of(Material.ENCHANTED_BOOK);
				item.setData(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantments.itemEnchantments().add(Enchantment.CHANNELING, 1).build());
				return item;
			})
			.withTrophy(ItemStack.of(Material.LEATHER_LEGGINGS).withColor(TextColor.color(13665433)))
		)
		.buildAndRegister();
	public static final IAdvancement KILL_A_MOB_THAT_IS_WEARING_ITS_HEAD = buildBase(KILL_A_MOB_WEARING_ITS_HEAD, "kill_a_mob_that_is_wearing_its_head").display(display().x(-1F).withAdvancementFrame(AdvancementFrame.STAR).fancyDescriptionParent(NamedTextColor.DARK_RED).torture().icon(Material.PLAYER_HEAD))
		.withReward(rewards()
			.withTrophy(ItemStack.of(Material.PLAYER_HEAD))
		)
		.buildAndRegister(KillAMobThatIsWearingItsHeadAdvancement::new);
	public static final IAdvancement KILL_A_SLIME = buildBase(KILL_A_HOSTILE_MOB, "kill_a_slime").display(display().xy(0F, -7.5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.SLIME_BALL))
		.withReward(rewards().addItems(ItemStack.of(Material.SLIME_BALL, 3)))
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity.withEntityType(EntityType.SLIME))
		))
		.buildAndRegister();
	public static final IAdvancement BOUNCE_FROM_A_SLIME_BLOCK = buildBase(KILL_A_SLIME, "bounce_from_a_slime_block").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.SLIME_BLOCK))
		.requiredProgress(vanilla(
			fallFromHeight()
				.withDistance(distance -> distance.minY(29.9))
				.withPlayer(player -> player
					.withSteppingLocation(loc -> loc
						.withBlock(block -> block.withBlocks(Material.SLIME_BLOCK))
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement GET_A_STACK_OF_SLIME_BALLS = buildBase(BOUNCE_FROM_A_SLIME_BLOCK, "get_a_stack_of_slime_balls").display(display().x(-1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.SLIME_SPAWN_EGG))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.SLIME_BALL, 8)))
		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.SLIME_BALL).withAmount(Material.SLIME_BALL.getMaxStackSize()))))
		.buildAndRegister();
	public static final IAdvancement GET_A_STACK_OF_SLIME_BLOCKS = buildBase(GET_A_STACK_OF_SLIME_BALLS, "get_a_stack_of_slime_blocks").display(display().x(-1F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(ItemUtil.texturedHead(MoreMobHeads.SLIME)))
		.withReward(rewards().withExp(200).addItems(ItemStack.of(Material.SLIME_BLOCK, 8)))
		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.SLIME_BLOCK).withAmount(Material.SLIME_BLOCK.getMaxStackSize()))))
		.buildAndRegister();
	public static final IAdvancement KILL_AN_ENDERMAN = buildBase(KILL_A_HOSTILE_MOB, "kill_an_enderman").display(display().xy(0F, -9F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.ENDER_PEARL))
		.withReward(rewards().addItems(ItemStack.of(Material.ENDER_PEARL)))
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity.withEntityType(EntityType.ENDERMAN))
		))
		.buildAndRegister();
	public static final IAdvancement KILL_AN_ENDERMITE = buildBase(KILL_AN_ENDERMAN, "kill_an_endermite").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.PURPLE_DYE))
		.withReward(rewards().addItems(ItemStack.of(Material.ENDER_PEARL)))
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity.withEntityType(EntityType.ENDERMITE))
		))
		.buildAndRegister();
	public static final IAdvancement KILL_10K_ENDERMITES = buildBase(KILL_AN_ENDERMITE, "kill_10k_endermites").display(display().xy(-1F, 0.5F).withAdvancementFrame(AdvancementFrame.STAR).fancyDescriptionParent(NamedTextColor.DARK_RED).torture().icon(Material.ENDER_PEARL))
		.withReward(rewards()
			.withTrophy(ItemStack.of(Material.ENDERMITE_SPAWN_EGG))
		)
		.requiredProgress(simple(10_000, 100))
		.buildAndRegister();
	public static final IAdvancement ENDER_PEARL_DAMAGE = buildBase(KILL_AN_ENDERMITE, "ender_pearl_damage").display(display().xy(-1F, -0.5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.ENDER_PEARL))
		.withReward(rewards().addItems(ItemStack.of(Material.ENDER_PEARL, 2)))
		.requiredProgress(vanilla(
			playerHurtEntity()
				.withDamage(damage -> damage
					.withDamageSource(source -> source
						.withTag(DamageTypeTagKeys.IS_PROJECTILE, true)
						.withDirectEntity(entity -> entity
							.withEntityType(EntityType.ENDER_PEARL)
						)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement GET_A_STACK_OF_ENDER_PEARLS = buildBase(ENDER_PEARL_DAMAGE, "get_a_stack_of_ender_pearls").display(display().xy(-1F, 0.5F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.ENDERMAN_SPAWN_EGG))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.ENDER_PEARL, 4))
			.addItems(() -> {
				var item = ItemStack.of(Material.ENCHANTED_BOOK);
				item.setData(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantments.itemEnchantments().add(Enchantment.KNOCKBACK, 1).build());
				return item;
			}))
		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.ENDER_PEARL).withAmount(Material.ENDER_PEARL.getMaxStackSize()))))
		.buildAndRegister();
	public static final IAdvancement USE_100_STACKS_OF_ENDER_PEARLS = buildBase(GET_A_STACK_OF_ENDER_PEARLS, "use_100_stacks_of_ender_pearls").display(display().xy(-1F, 0.5F).withAdvancementFrame(AdvancementFrame.STAR).fancyDescriptionParent(NamedTextColor.DARK_RED).torture().icon(Material.ENDER_EYE))
		.withReward(rewards()
			.withTrophy(ItemStack.of(Material.ENDER_PEARL))
		)
		.requiredProgress(simple(100 * Material.ENDER_PEARL.getMaxStackSize()))
		.buildAndRegister();
	public static final IAdvancement KILL_AN_ENDERMAN_IN_EACH_DIMENSION = buildBase(GET_A_STACK_OF_ENDER_PEARLS, "kill_an_enderman_in_each_dimension").display(display().xy(-1F, -0.5F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.ENDER_EYE))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.ENDER_PEARL, 4)))
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity
					.withEntityType(EntityType.ENDERMAN)
					.withLocation(loc -> loc.withDimension(World.Environment.NORMAL))
				),
			playerKilledEntity()
				.withEntity(entity -> entity
					.withEntityType(EntityType.ENDERMAN)
					.withLocation(loc -> loc.withDimension(World.Environment.NETHER))
				),
			playerKilledEntity()
				.withEntity(entity -> entity
					.withEntityType(EntityType.ENDERMAN)
					.withLocation(loc -> loc.withDimension(World.Environment.THE_END))
				)
		))
		.buildAndRegister();
	public static final IAdvancement KILL_AN_ENDERMAN_WITH_AN_ARROW = buildMulti(KILL_AN_ENDERMAN_IN_EACH_DIMENSION, "kill_an_enderman_with_an_arrow", USE_100_STACKS_OF_ENDER_PEARLS).display(display().xy(-1F, 0.5F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(Material.ARROW))
		.withReward(rewards()
			.withExp(100)
			.addItems(ItemStack.of(Material.ENDER_PEARL, 6))
			.withTrophy(ItemStack.of(Material.ARROW))
		)
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity
					.withEntityType(EntityType.ENDERMAN)
				)
				.withDamage(damage -> damage
					.withTag(DamageTypeTagKeys.IS_PLAYER_ATTACK, true)
					.withSourceEntity(entity -> entity
						.withEquipment(equipment -> equipment
							.withMainHand(Tag.ITEMS_ARROWS)
						)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement KILL_A_WITHER_WITH_AN_ARROW = buildBase(KILL_AN_ENDERMAN_WITH_AN_ARROW, "kill_a_wither_with_an_arrow").display(display().xy(-1F, 0.5F).withAdvancementFrame(AdvancementFrame.STAR).fancyDescriptionParent(NamedTextColor.DARK_RED).torture().icon(potionItem(Material.TIPPED_ARROW, PotionType.WEAKNESS)))
		.withReward(rewards()
			.withExp(70)
			.withTrophy(ItemStack.of(Material.ARROW))
		)
		.requiredProgress(vanillaAny(
			playerKilledEntity()
				.withEntity(entity -> entity
					.withEntityType(EntityType.WITHER)
				)
				.withDamage(damage -> damage
					.withTag(DamageTypeTagKeys.IS_PLAYER_ATTACK, true)
					.withSourceEntity(entity -> entity
						.withEquipment(equipment -> equipment
							.withMainHand(Tag.ITEMS_ARROWS)
						)
					)
				),
			playerKilledEntity("supersonic_arrow")
				.withEntity(entity -> entity
					.withEntityType(EntityType.WITHER)
				)
				.withDamage(damage -> damage
					.withDirectEntity(entity -> entity
						.withEntityType(Tag.ENTITY_TYPES_ARROWS)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement KILL_ENDERMEN_WITH_THEIR_ITEMS = buildBase(KILL_AN_ENDERMAN_WITH_AN_ARROW, "kill_endermen_with_their_items").display(display().xy(-1F, -0.5F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(Material.RED_SAND))
		.withReward(rewards()
			.withExp(200)
			.withTrophy(ItemStack.of(Material.ORANGE_WOOL))
		)
		.buildAndRegister(KillEndermenWithTheirItemsAdvancement::new);
	public static final IAdvancement KILL_A_WITCH = buildBase(KILL_A_HOSTILE_MOB, "kill_a_witch").display(display().xy(0F, 6.5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.SUGAR))
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity.withEntityType(EntityType.WITCH))
		))
		.buildAndRegister();
	public static final IAdvancement MIRACLE_DRINK = buildBase(KILL_A_WITCH, "miracle_drink").display(display().x(-1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.HONEY_BOTTLE))
		.withReward(rewards().withExp(50))
		.buildAndRegister();
	public static final IAdvancement KILL_A_WITCH_WITH_A_SPLASH_POTION = buildBase(MIRACLE_DRINK, "kill_a_witch_with_a_splash_potion").display(display().x(-1F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(potionItem(Material.SPLASH_POTION, PotionType.POISON)))
		.withReward(rewards().withExp(50))
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity.withEntityType(EntityType.WITCH))
				.withDamage(damage -> damage
					.withDirectEntity(entity -> entity
						.withEntityType(EntityType.SPLASH_POTION)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement THE_INQUISITION = buildBase(KILL_A_WITCH_WITH_A_SPLASH_POTION, "the_inquisition").display(display().xy(-1F, 0.5F).withAdvancementFrame(AdvancementFrame.STAR).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).torture().icon(() -> {
			var item = ItemStack.of(Material.CAMPFIRE);
			item.setBlockData(Material.CAMPFIRE.createBlockData(data -> ((Campfire) data).setLit(true)));
			return item;
		}))
		.withReward(rewards().withExp(60).addItems(() -> {
			var item = ItemStack.of(Material.CAMPFIRE);
			item.setBlockData(Material.CAMPFIRE.createBlockData(data -> ((Campfire) data).setLit(true)));
			return item;
		}))
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity.withEntityType(EntityType.WITCH))
				.withDamage(damage -> damage
					.withTag(UtilizerTags.IS_CAMPFIRE, true)
				)
		))
		.buildAndRegister();
	public static final IAdvancement KILL_500_WITCHES_WITH_LINGERING_POTIONS = buildBase(KILL_A_WITCH_WITH_A_SPLASH_POTION, "kill_500_witches_with_lingering_potions").display(display().xy(-1F, -0.5F).withAdvancementFrame(AdvancementFrame.STAR).fancyDescriptionParent(NamedTextColor.DARK_RED).torture().icon(Material.LINGERING_POTION))
		.withReward(rewards()
			.withTrophy(ItemStack.of(Material.TRIPWIRE_HOOK))
		)
		.requiredProgress(simple(500))
		.buildAndRegister();
	public static final IAdvancement KILL_A_SILVERFISH = buildBase(KILL_A_HOSTILE_MOB, "kill_a_silverfish").display(display().xy(0F, 8F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.CRACKED_STONE_BRICKS))
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity.withEntityType(EntityType.SILVERFISH))
		))
		.buildAndRegister();
	public static final IAdvancement KILL_10K_SILVERFISHES = buildBase(KILL_A_SILVERFISH, "kill_10k_silverfishes").display(display().x(-1F).withAdvancementFrame(AdvancementFrame.BUTTERFLY).fancyDescriptionParent(NamedTextColor.DARK_RED).torture().icon(Material.COBBLESTONE))
		.requiredProgress(simple(10_000, 100))
		.buildAndRegister();
	public static final IAdvancement STAND_ON_ALL_NATURAL_SPAWNERS = buildBase(KILL_10K_SILVERFISHES, "stand_on_all_natural_spawners").display(display().x(-1F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(Material.SPAWNER))
		.withReward(rewards().withExp(110))
		.requiredProgress(requirements(UtilizerTags.NATURAL_SPAWNERS.getValues().stream().map(entityType -> List.of(entityType.key().value())).toList()))
		.buildAndRegister();
	public static final IAdvancement STAND_ON_A_MANSION_SPIDER_SPAWNER = buildBase(STAND_ON_ALL_NATURAL_SPAWNERS, "stand_on_a_mansion_spider_spawner").display(display().x(-1F).withAdvancementFrame(AdvancementFrame.STAR).fancyDescriptionParent(PURPLE).superChallenge().icon(ItemUtil.glint(Material.SPAWNER)))
		.withReward(rewards().withExp(600))
		.buildAndRegister();
	public static final IAdvancement GET_KILLED_BY_A_WARDEN = buildBase(KILL_A_HOSTILE_MOB, "get_killed_by_a_warden").display(display().xy(0F, 9.5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.SCULK_CATALYST))
		.requiredProgress(vanilla(
			entityKilledPlayer()
				.withEntity(entity -> entity.withEntityType(EntityType.WARDEN))
		))
		.buildAndRegister();
	public static final IAdvancement LOOK_AT_A_WARDEN_WITH_A_SPYGLASS = buildBase(GET_KILLED_BY_A_WARDEN, "look_at_a_warden_with_a_spyglass").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.SPYGLASS))
		.requiredProgress(vanilla(
			usingItem()
				.withItem(ItemTriggerCondition.of(Material.SPYGLASS))
				.withPlayer(player -> player
					.withLookingAt(entity -> entity.withEntityType(EntityType.WARDEN))
				)
		))
		.buildAndRegister();
	public static final IAdvancement KILL_A_WARDEN = buildBase(LOOK_AT_A_WARDEN_WITH_A_SPYGLASS, "kill_a_warden").display(display().x(-1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.SCULK_SENSOR))
		.withReward(rewards()
			.withExp(50)
			.withTrophy(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmNmMzY3NGIyZGRjMGVmN2MzOWUzYjljNmI1ODY3N2RlNWNmMzc3ZDJlYjA3M2YyZjNmZTUwOTE5YjFjYTRjOSJ9fX0="))
		)
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity.withEntityType(EntityType.WARDEN))
		))
		.buildAndRegister();
	public static final IAdvancement WARDENS_THRUST = buildBase(KILL_A_WARDEN, "wardens_thrust")
		.display(display().x(-1F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTAzODE3MzdkNGRhNTI4ODIzNTQ3MjUxYjE0NTU2OGQxNTI1M2E4N2IxMTE5M2MzZGFmZjZhZTM1NTc3NSJ9fX0=")))
		.withReward(rewards().withExp(80))
		.requiredProgress(vanilla(
			entityHurtPlayer()
				.withPlayer(player -> player.withState(state -> state.flying()))
				.withDamage(damage -> damage
					.withDamageSource(source -> source
						.withTag(UtilizerTags.IS_SONIC_BOOM, true)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement GET_A_WARDEN_TO_THE_HEIGHT_LIMIT = buildBase(WARDENS_THRUST, "get_a_warden_to_the_height_limit").display(display().xy(-1F, 0.5F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(Material.BLACK_CONCRETE_POWDER))
		.withReward(rewards()
			.withExp(100)
			.addItems(ItemStack.of(Material.SCULK_CATALYST, 4))
			.withTrophy(ItemStack.of(Material.WARPED_SIGN))
		)
		.buildAndRegister();
	public static final IAdvancement BE_NEAR_FIVE_WARDENS = buildBase(GET_A_WARDEN_TO_THE_HEIGHT_LIMIT, "be_near_five_wardens").display(display().x(-1F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(Material.SCULK_SHRIEKER))
		.withReward(rewards().withExp(200).addItems(ItemStack.of(Material.SCULK_SHRIEKER, 5)))
		.buildAndRegister();
	public static final IAdvancement BE_NEAR_20_RAVAGERS = buildBase(BE_NEAR_FIVE_WARDENS, "be_near_20_ravagers").display(display().x(-1F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(ItemUtil.glint(Material.COMPOSTER)))
		.withReward(rewards()
			.withExp(180)
			.withTrophy(() -> {
				ItemStack item = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODQ0YWU5MmJiNDM0N2RlOWVhNWI2MzAwOGM0NDZiMzQwOWEwMjVkODU0N2M4OWFlOThiZTVjYWU4ZDAxMjFjNyJ9fX0=");
				item.setData(DataComponentTypes.DAMAGE_RESISTANT, DamageResistant.damageResistant(DamageTypeTagKeys.IS_FIRE));
				return item;
			})
		)
		.buildAndRegister();
	public static final IAdvancement KILL_A_WARDEN_IN_A_SNOW_BIOME = buildBase(WARDENS_THRUST, "kill_a_warden_in_a_snow_biome").display(display().xy(-1F, -0.5F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(Material.CYAN_STAINED_GLASS))
		.withReward(rewards().withExp(100))
		.requiredProgress(vanilla(
			playerKilledEntity()
				.withEntity(entity -> entity
					.withEntityType(EntityType.WARDEN)
					.withLocation(loc -> loc
						.withBiome(List.of(Biome.SNOWY_PLAINS, Biome.ICE_SPIKES))
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement TAKE_A_HIT_FROM_A_WARDEN_WITH_PROTECTION = buildBase(KILL_A_WARDEN_IN_A_SNOW_BIOME, "take_a_hit_from_a_warden_with_protection").display(display().x(-1F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(ItemUtil.glint(Material.NETHERITE_CHESTPLATE)))
		.withReward(rewards()
			.withExp(200)
			.addItems(ItemStack.of(Material.NETHERITE_INGOT))
			.withTrophy(ItemStack.of(Material.POLISHED_DEEPSLATE_SLAB))
		)
		.requiredProgress(vanilla(
			entityHurtPlayer()
				.withDamage(damage -> damage
					.notBlocked()
					.withSourceEntity(entity -> entity.withEntityType(EntityType.WARDEN))
				)
				.withPlayer(player -> player
					.withEffects(effects -> effects.with(PotionEffectType.RESISTANCE, e -> e.withAmplifier(3, null)))
					.withEquipment(equipment -> equipment
						.withHelmet(ItemTriggerCondition.of(Material.NETHERITE_HELMET)
							.withRawComponents("""
								{
									"predicates": {
										"enchantments": [
											{
												"enchantments": "%s",
												"levels": {
													"min": 4
												}
											}
										]
									}
								}
								""".formatted(Enchantment.PROTECTION.key()))
						)
						.withChestplate(ItemTriggerCondition.of(Material.NETHERITE_CHESTPLATE)
							.withRawComponents("""
								{
									"predicates": {
										"enchantments": [
											{
												"enchantments": "%s",
												"levels": {
													"min": 4
												}
											}
										]
									}
								}
								""".formatted(Enchantment.PROTECTION.key()))
						)
						.withLeggings(ItemTriggerCondition.of(Material.NETHERITE_LEGGINGS)
							.withRawComponents("""
								{
									"predicates": {
										"enchantments": [
											{
												"enchantments": "%s",
												"levels": {
													"min": 4
												}
											}
										]
									}
								}
								""".formatted(Enchantment.PROTECTION.key()))
						)
						.withBoots(ItemTriggerCondition.of(Material.NETHERITE_BOOTS)
							.withRawComponents("""
								{
									"predicates": {
										"enchantments": [
											{
												"enchantments": "%s",
												"levels": {
													"min": 4
												}
											}
										]
									}
								}
								""".formatted(Enchantment.PROTECTION.key()))
						)
					)
				)
		))
		.buildAndRegister();
	public static final IAdvancement HUG_A_WARDEN_FOR_A_MINUTE = buildBase(TAKE_A_HIT_FROM_A_WARDEN_WITH_PROTECTION, "hug_a_warden_for_a_minute").display(display().x(-1F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTVmZWJhMzc1MGNhZjNkODFiNjQ1ZjdhMzhlYWRkYTk2NjUzZDdiM2NhMjg0MzliNDUxNTE0ZTRmYWFmNDg2NCJ9fX0=")))
		.withReward(rewards()
			.withExp(120)
			.withTrophy(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGEwMzZjZTkzN2ZhYWNiMTcyMGQ4MTgxMGQwMGNhMmI4ZjMzMTc5MDk2N2YzMzQxMTRmMDFiZDQ1YTlkOTQzYiJ9fX0="))
		)
		.buildAndRegister();

	public static final AdvancementTab STATISTICS_TAB = buildTab("statistics", MANAGER)
		.display(simpleTabDisplay().inverseY().display(display().backgroundPathTexture("block/loom_side").icon(Material.WRITABLE_BOOK)))
		.build();
	public static final IAdvancement STATISTICS_ROOT = buildBase(STATISTICS_TAB, "visual_root")
		.display(display().withAdvancementFrame(AdvancementFrame.SQUIRCLE).fancyDescriptionParent(GRAY).icon(Material.WRITABLE_BOOK))
		.visibilityRule(ifDone(false, BRAVE_NEW_WORLD))
		.requiredProgress(alwaysDone())
		.buildAndRegister();
	public static final IAdvancement STATISTICS_RIGHT_LINKER = buildFake(STATISTICS_ROOT).display(display().x(0.75F).isHidden(true)).buildAndRegister();
	public static final IAdvancement STATISTICS_UP_LINKER = buildFake(STATISTICS_ROOT).display(display().x(-0.5F).isHidden(true)).buildAndRegister();
	public static final IAdvancement STATISTICS_DOWN_LINKER = buildFake(STATISTICS_ROOT).display(display().x(-0.5F).isHidden(true)).buildAndRegister();
	// Up
	public static final IAdvancement PLAY_1D = buildBase(STATISTICS_UP_LINKER, "play_1d").display(display().xy(0.5F, 1.25F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.SUNFLOWER))
		.withReward(rewards().withExp(50))
		.buildAndRegister();
	public static final IAdvancement PLAY_100D = buildBase(STATISTICS_UP_LINKER, "play_100d").display(display().xy(0.5F, 2.25F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.CLOCK))
		.withReward(rewards().withExp(200))
		.buildAndRegister();
	public static final IAdvancement PLAY_365D = buildBase(STATISTICS_UP_LINKER, "play_365d").display(display().xy(0.5F, 3.25F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(Material.FIREWORK_ROCKET))
		.withReward(rewards()
			.withExp(500)
			.withTrophy(() -> {
				var item = ItemStack.of(Material.FIREWORK_ROCKET, 16);
				item.setData(DataComponentTypes.FIREWORKS, Fireworks.fireworks()
						.flightDuration(3)
						.addEffect(FireworkEffect.builder()
							.with(FireworkEffect.Type.STAR)
							.withFlicker()
							.withTrail()
							.withColor(Color.fromRGB(12134697), Color.fromRGB(16777215))
							.withFade(Color.fromRGB(16777215))
							.build()
						)
						.build()
					);
				return item;
			})
		)
		.buildAndRegister();
	public static final IAdvancement PLAY_1000D = buildBase(STATISTICS_UP_LINKER, "play_1000d").display(display().xy(0.5F, 4.25F).challengeFrame().fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).icon(HAPPY_1000_DAYS_HEAD))
		.withReward(rewards()
			.withExp(300)
			.withTrophy(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTk4OTQ2YmNkM2IwYTE2ZmJmZjM1ZDE2MTRmYmJiZmYzYzE5NzI2NjNjNTUzMjE1NGZlYTFjYTJhNGMxOWIxZSJ9fX0="))
		)
		.buildAndRegister();
	// Down
	public static final IAdvancement GAME_LEAVES_1 = buildBase(STATISTICS_DOWN_LINKER, "game_leaves_1").display(display().xy(0.5F, -1.25F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.OAK_LEAVES)).buildAndRegister();
	public static final IAdvancement GAME_LEAVES_10 = buildBase(STATISTICS_DOWN_LINKER, "game_leaves_10").display(display().xy(0.5F, -2.25F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.SPRUCE_LEAVES))
		.requiredProgress(simple(10))
		.buildAndRegister();
	public static final IAdvancement GAME_LEAVES_100 = buildBase(STATISTICS_DOWN_LINKER, "game_leaves_100").display(display().xy(0.5F, -3.25F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.DARK_OAK_LEAVES))
		.withReward(rewards()
			.withTrophy(ItemStack.of(Material.OAK_LEAVES))
		)
		.requiredProgress(simple(100))
		.buildAndRegister();
	public static final IAdvancement GAME_LEAVES_1K = buildBase(STATISTICS_DOWN_LINKER, "game_leaves_1k").display(display().xy(0.5F, -4.25F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().torture().icon(Material.JUNGLE_LEAVES))
		.requiredProgress(simple(1000, 100))
		.buildAndRegister();
	public static final IAdvancement GAME_LEAVES_5K = buildBase(STATISTICS_DOWN_LINKER, "game_leaves_5k").display(display().xy(0.5F, -5.25F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(Material.ACACIA_LEAVES))
		.requiredProgress(simple(5000, 100))
		.buildAndRegister();
	public static final IAdvancement GAME_LEAVES_10K = buildBase(STATISTICS_DOWN_LINKER, "game_leaves_10k").display(display().xy(0.5F, -6.25F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(Material.BIRCH_LEAVES))
		.requiredProgress(simple(10_000, 100))
		.buildAndRegister();
	// Right top
	public static final IAdvancement BELL_100 = buildBase(STATISTICS_RIGHT_LINKER, "bell_100").display(display().x(1).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.STICK)).buildAndRegister();
	public static final IAdvancement BELL_1K = buildBase(BELL_100, "bell_1k").display(display().x(1).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.MUSIC_DISC_13)).buildAndRegister();
	public static final IAdvancement BELL_10K = buildBase(BELL_1K, "bell_10k").display(display().x(1).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.BELL)).buildAndRegister();
	public static final IAdvancement BELL_100K = buildBase(BELL_10K, "bell_100k").display(display().x(1).fancyDescriptionParent(NamedTextColor.DARK_RED).torture().challengeFrame().icon(Material.REDSTONE)).buildAndRegister();
	public static final IAdvancement BELL_1000K = buildBase(BELL_100K, "bell_1000k").display(display().x(1).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(Material.SKELETON_SKULL)).buildAndRegister();
	public static final IAdvancement BELL_10000K = buildBase(BELL_1000K, "bell_10000k").display(display().x(1).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(ItemUtil.glint(Material.BELL)))
		.withReward(rewards()
			.withTrophy(ItemStack.of(Material.BELL))
		)
		.buildAndRegister();
	public static final IAdvancement LEVEL_30 = buildBase(STATISTICS_RIGHT_LINKER, "level_30").display(display().xy(1F, 1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(ItemUtil.glint(Material.EXPERIENCE_BOTTLE)))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.LAPIS_LAZULI, 12)))
		.buildAndRegister();
	public static final IAdvancement LEVEL_100 = buildBase(LEVEL_30, "level_100").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(ItemUtil.glint(Material.ENCHANTED_BOOK)))
		.withReward(rewards().withExp(200).addItems(ItemStack.of(Material.LAPIS_LAZULI, 64)))
		.buildAndRegister();
	public static final IAdvancement LEVEL_300 = buildBase(LEVEL_100, "level_300").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(ItemUtil.glint(Material.LAPIS_BLOCK)))
		.withReward(rewards()
			.withExp(500)
			.addItems(ItemStack.of(Material.LAPIS_BLOCK, 20))
			.withTrophy(ItemStack.of(Material.SMOKER))
		)
		.buildAndRegister();
	public static final IAdvancement LEVEL_1000 = buildBase(LEVEL_300, "level_1000").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().icon(ItemUtil.glint(Material.SCULK)))
		.withReward(rewards()
			.withExp(1000)
			.withTrophy(ItemStack.of(Material.MAGMA_CREAM))
		)
		.buildAndRegister();
	public static final IAdvancement LEVEL_2500 = buildBase(LEVEL_1000, "level_2500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().superTorture().icon(ItemUtil.glint(Material.ENCHANTING_TABLE))).buildAndRegister();
	public static final IAdvancement LEVEL_5000 = buildBase(LEVEL_2500, "level_5000").display(display().x(1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(ItemUtil.glint(Material.DAMAGED_ANVIL))).buildAndRegister();
	public static final IAdvancement ENCHANT_10 = buildBase(STATISTICS_RIGHT_LINKER, "enchant_10").display(display().xy(1F, 2F).fancyDescriptionParent(NamedTextColor.GREEN).icon(ItemUtil.glint(Material.IRON_SWORD)))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.LAPIS_LAZULI, 12)))
		.buildAndRegister();
	public static final IAdvancement ENCHANT_50 = buildBase(ENCHANT_10, "enchant_50").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(ItemUtil.glint(Material.DIAMOND_AXE)))
		.withReward(rewards().withExp(200).addItems(ItemStack.of(Material.LAPIS_LAZULI, 64)))
		.buildAndRegister();
	public static final IAdvancement ENCHANT_250 = buildBase(ENCHANT_50, "enchant_250").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(ItemUtil.glint(Material.TRIDENT)))
		.withReward(rewards()
			.withExp(500)
			.addItems(ItemStack.of(Material.LAPIS_BLOCK, 20))
			.withTrophy(ItemStack.of(Material.ENCHANTED_BOOK))
		)
		.buildAndRegister();
	public static final IAdvancement ENCHANT_1000 = buildBase(ENCHANT_250, "enchant_1000").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().superChallenge().icon(ItemUtil.glint(Material.NETHERITE_PICKAXE))).buildAndRegister();
	public static final IAdvancement ENCHANT_2500 = buildBase(ENCHANT_1000, "enchant_2500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().torture().icon(ItemUtil.glint(Material.BOW))).buildAndRegister();
	public static final IAdvancement ENCHANT_5K = buildBase(ENCHANT_2500, "enchant_5k").display(display().x(1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(ItemUtil.glint(Material.BRUSH)))
		.withReward(rewards()
			.withExp(500)
			.withTrophy(ItemStack.of(Material.ENCHANTING_TABLE))
		)
		.buildAndRegister();
	public static final IAdvancement ENCHANT_10K = buildBase(ENCHANT_5K, "enchant_10k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(ItemUtil.glint(Material.ENCHANTED_BOOK))).buildAndRegister();
	public static final IAdvancement WASH_10 = buildBase(STATISTICS_RIGHT_LINKER, "wash_10").display(display().xy(1F, 3F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.WHITE_BANNER)).requiredProgress(simple(10, 1)).buildAndRegister();
	public static final IAdvancement WASH_50 = buildBase(WASH_10, "wash_50").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(ItemStack.of(Material.LEATHER_BOOTS).withColor(NamedTextColor.LIGHT_PURPLE))).requiredProgress(simple(50, 1)).buildAndRegister();
	public static final IAdvancement WASH_250 = buildBase(WASH_50, "wash_250").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.CAULDRON)).requiredProgress(simple(250, 1))
		.withReward(rewards()
			.withTrophy(() -> {
				var item = ItemStack.of(Material.LEATHER_BOOTS);
				item.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(Color.fromRGB(16777215)));
				return item;
			})
		)
		.buildAndRegister();
	public static final IAdvancement WASH_1K = buildBase(WASH_250, "wash_1k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().torture().icon(ItemUtil.glint(Material.CAULDRON))).requiredProgress(simple(1000, 1)).buildAndRegister();
	public static final IAdvancement WASH_5K = buildBase(WASH_1K, "wash_5k").display(display().x(1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(Material.SHULKER_BOX)).requiredProgress(simple(5000, 1)).buildAndRegister();
	public static final IAdvancement WASH_10K = buildBase(WASH_5K, "wash_10k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(Material.WATER_BUCKET)).requiredProgress(simple(10000, 1)).buildAndRegister();
	public static final IAdvancement FISH_5 = buildBase(STATISTICS_RIGHT_LINKER, "fish_5").display(display().xy(1F, 4F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.FISHING_ROD))
		.withReward(rewards().withExp(5))
		.buildAndRegister();
	public static final IAdvancement FISH_25 = buildBase(FISH_5, "fish_25").display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.INK_SAC))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.SALMON, 5)))
		.buildAndRegister();
	public static final IAdvancement FISH_100 = buildBase(FISH_25, "fish_100").display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.COD)).buildAndRegister();
	public static final IAdvancement FISH_250 = buildBase(FISH_100, "fish_250").display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).goalFrame().icon(Material.SALMON))
		.withReward(rewards()
			.withExp(300)
			.withTrophy(ItemStack.of(Material.SALMON_BUCKET))
		)
		.buildAndRegister();
	public static final IAdvancement FISH_500 = buildBase(FISH_250, "fish_500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(ItemUtil.glint(Material.COD))).buildAndRegister();
	public static final IAdvancement FISH_1K = buildBase(FISH_500, "fish_1k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).challengeFrame().icon(ItemUtil.glint(Material.SALMON)))
		.withReward(rewards()
			.withExp(100)
			.withTrophy(() -> {
				var item = ItemStack.of(Material.FISHING_ROD);
				item.addEnchantment(Enchantment.MENDING, 1);
				item.addEnchantment(Enchantment.LUCK_OF_THE_SEA, 3);
				item.addEnchantment(Enchantment.LURE, 3);
				item.addEnchantment(Enchantment.UNBREAKING, 3);
				return item;
			})
		)
		.buildAndRegister();
	public static final IAdvancement FISH_2500 = buildBase(FISH_1K, "fish_2500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().superChallenge().icon(Material.TROPICAL_FISH))
		.withReward(rewards()
			.withTrophy(() -> {
				var item = ItemStack.of(Material.FISHING_ROD);
				item.setData(DataComponentTypes.UNBREAKABLE);
				return item;
			})
		)
		.buildAndRegister();
	public static final IAdvancement FISH_5K = buildBase(FISH_2500, "fish_5k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().torture().icon(Material.PUFFERFISH)).buildAndRegister();
	public static final IAdvancement FISH_10K = buildBase(FISH_5K, "fish_10k").display(display().x(1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(Material.NAUTILUS_SHELL)).buildAndRegister();
	public static final IAdvancement FISH_50K = buildBase(FISH_10K, "fish_50k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(ItemUtil.glint(Material.FISHING_ROD))).buildAndRegister();
	public static final IAdvancement EAT_200 = buildBase(STATISTICS_RIGHT_LINKER, "eat_200").display(display().xy(1F, 5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.BREAD)).requiredProgress(simple(200, 1))
		.withReward(rewards().withExp(50))
		.buildAndRegister();
	public static final IAdvancement EAT_1K = buildBase(EAT_200, "eat_1k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.COOKED_PORKCHOP)).requiredProgress(simple(1000, 1))
		.withReward(rewards().withExp(200))
		.buildAndRegister();
	public static final IAdvancement EAT_2500 = buildBase(EAT_1K, "eat_2500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.COOKED_BEEF)).requiredProgress(simple(2500, 1))
		.withReward(rewards()
			.withExp(500)
			.withTrophy(ItemStack.of(Material.COOKED_BEEF))
		)
		.buildAndRegister();
	public static final IAdvancement EAT_5K = buildBase(EAT_2500, "eat_5k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().superChallenge().icon(Material.GOLDEN_CARROT)).requiredProgress(simple(5000, 1)).buildAndRegister();
	public static final IAdvancement EAT_10K = buildBase(EAT_5K, "eat_10k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().torture().icon(Material.GOLDEN_APPLE)).requiredProgress(simple(10000, 1))
		.withReward(rewards()
			.withExp(550)
			.withTrophy(ItemStack.of(Material.DRIED_KELP))
		)
		.buildAndRegister();
	public static final IAdvancement EAT_25K = buildBase(EAT_10K, "eat_25k").display(display().x(1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(Material.COOKIE)).requiredProgress(simple(25000, 1)).buildAndRegister();
	public static final IAdvancement EAT_50K = buildBase(EAT_25K, "eat_50k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(Material.CAKE)).requiredProgress(simple(50000, 1)).buildAndRegister();
	public static final IAdvancement TOTEM_5 = buildBase(STATISTICS_RIGHT_LINKER, "totem_5").display(display().xy(1F, 6F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.GOLD_NUGGET))
		.withReward(rewards().withExp(15).addItems(ItemStack.of(Material.EMERALD, 4)))
		.requiredProgress(simple(5))
		.buildAndRegister();
	public static final IAdvancement TOTEM_10 = buildBase(TOTEM_5, "totem_10").display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.TOTEM_OF_UNDYING))
		.withReward(rewards().withExp(25).addItems(ItemStack.of(Material.TOTEM_OF_UNDYING)))
		.requiredProgress(simple(10))
		.buildAndRegister();
	public static final IAdvancement TOTEM_25 = buildBase(TOTEM_10, "totem_25").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.RAW_GOLD))
		.withReward(rewards().withExp(50))
		.requiredProgress(simple(25))
		.buildAndRegister();
	public static final IAdvancement TOTEM_50 = buildBase(TOTEM_25, "totem_50").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.PLAYER_HEAD))
		.withReward(rewards().withExp(75))
		.requiredProgress(simple(50))
		.buildAndRegister();
	public static final IAdvancement TOTEM_100 = buildBase(TOTEM_50, "totem_100").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.ZOMBIE_HEAD))
		.withReward(rewards()
			.withExp(100)
			.withTrophy(ItemStack.of(Material.TOTEM_OF_UNDYING))
		)
		.requiredProgress(simple(100))
		.buildAndRegister();
	public static final IAdvancement TOTEM_250 = buildBase(TOTEM_100, "totem_250").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.SKELETON_SKULL))
		.withReward(rewards().withExp(150))
		.requiredProgress(simple(250, 25))
		.buildAndRegister();
	public static final IAdvancement TOTEM_500 = buildBase(TOTEM_250, "totem_500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().superChallenge().icon(Material.WITHER_SKELETON_SKULL))
		.withReward(rewards().withExp(250))
		.requiredProgress(simple(500, 50))
		.buildAndRegister();
	public static final IAdvancement TOTEM_1000 = buildBase(TOTEM_500, "totem_1000").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().torture().icon(Material.EVOKER_SPAWN_EGG))
		.withReward(rewards()
			.withExp(350)
			.withTrophy(ItemStack.of(Material.TOTEM_OF_UNDYING))
		)
		.requiredProgress(simple(1000, 10))
		.buildAndRegister();
	public static final IAdvancement TOTEM_2500 = buildBase(TOTEM_1000, "totem_2500").display(display().x(1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(Material.END_CRYSTAL))
		.withReward(rewards()
			.withExp(500)
			.withTrophy(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDI2ODg1N2JmYzJmMWI5YjA2MzZlMTVlNGYwN2Q3MWM5YmIyNjhjYjc5YzNkNDdmNTU3OTk2MzBkNmFiMDgzMCJ9fX0=")) // todo textured heads
		)
		.requiredProgress(simple(2500, 5))
		.buildAndRegister();
	public static final IAdvancement TOTEM_5000 = buildBase(TOTEM_2500, "totem_5000").display(display().x(1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(ItemUtil.glint(Material.TOTEM_OF_UNDYING)))
		.withReward(rewards().withExp(1000))
		.requiredProgress(simple(5000, 5))
		.buildAndRegister();
	public static final IAdvancement RAID_5 = buildBase(STATISTICS_RIGHT_LINKER, "raid_5").display(display().xy(1F, 7F).fancyDescriptionParent(NamedTextColor.GREEN).challengeFrame().icon(Material.CROSSBOW))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.EMERALD, 4)))
		.buildAndRegister();
	public static final IAdvancement RAID_20 = buildBase(RAID_5, "raid_20").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.IRON_AXE))
		.withReward(rewards().withExp(70).addItems(ItemStack.of(Material.EMERALD, 16)))
		.buildAndRegister();
	public static final IAdvancement RAID_100 = buildBase(RAID_20, "raid_100").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(ItemUtil.glint(Material.SADDLE)))
		.withReward(rewards()
			.withExp(120)
			.addItems(ItemStack.of(Material.TOTEM_OF_UNDYING))
			.withTrophy(() -> {
				var item = ItemStack.of(Material.IRON_AXE);
				item.addEnchantment(Enchantment.SHARPNESS, 5);
				return item;
			})
		)
		.buildAndRegister();
	public static final IAdvancement RAID_2000 = buildBase(RAID_100, "raid_2000").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).withAdvancementFrame(AdvancementFrame.BUTTERFLY).icon(ItemUtil.glint(Material.TOTEM_OF_UNDYING)))
		.withReward(rewards()
			.withExp(250)
			.addItems(ItemStack.of(Material.EMERALD_BLOCK, 6))
			.withTrophy(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWM3M2UxNmZhMjkyNjg5OWNmMTg0MzQzNjBlMjE0NGY4NGVmMWViOTgxZjk5NjE0ODkxMjE0OGRkODdlMGIyYSJ9fX0=")) // toto textured heads
		)
		.buildAndRegister();
	// Right bottom
	public static final IAdvancement BREED_100 = buildBase(STATISTICS_RIGHT_LINKER, "breed_100").display(display().xy(1F, -1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.PORKCHOP))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.WHEAT, 32)))
		.buildAndRegister();
	public static final IAdvancement BREED_500 = buildBase(BREED_100, "breed_500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.BEEF))
		.withReward(rewards().withExp(200).addItems(ItemStack.of(Material.HAY_BLOCK, 16), ItemStack.of(Material.GOLDEN_APPLE, 8)))
		.buildAndRegister();
	public static final IAdvancement BREED_2500 = buildBase(BREED_500, "breed_2500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.CHICKEN))
		.withReward(rewards()
			.withExp(500)
			.addItems(ItemStack.of(Material.HAY_BLOCK, 64), ItemStack.of(Material.ENCHANTED_GOLDEN_APPLE, 8))
			.withTrophy(ItemStack.of(Material.OAK_BOAT))
		)
		.buildAndRegister();
	public static final IAdvancement BREED_5K = buildBase(BREED_2500, "breed_5k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().superChallenge().icon(Material.MUTTON)).buildAndRegister();
	public static final IAdvancement BREED_10K = buildBase(BREED_5K, "breed_10k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().torture().icon(Material.RABBIT))
		.withReward(rewards()
			.withExp(550)
			.withTrophy(MR_SHEEP_HEAD) // todo textured heads
		).buildAndRegister();
	public static final IAdvancement BREED_15K = buildBase(BREED_10K, "breed_15k").display(display().x(1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(Material.HONEY_BOTTLE)).buildAndRegister();
	public static final IAdvancement BREED_25K = buildBase(BREED_15K, "breed_25k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(ItemUtil.glint(Material.EGG))).buildAndRegister();
	public static final IAdvancement KILL_250 = buildBase(STATISTICS_RIGHT_LINKER, "kill_250").display(display().xy(1F, -2F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.STONE_SWORD))
		.withReward(rewards()
			.withExp(50)
			.addItems(() -> {
				var item = ItemStack.of(Material.ENCHANTED_BOOK);
				item.setData(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantments.itemEnchantments().add(Enchantment.SHARPNESS, 2).build());
				return item;
			})
		)
		.buildAndRegister();
	public static final IAdvancement KILL_2500 = buildBase(KILL_250, "kill_2500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.IRON_SWORD ))
		.withReward(rewards()
			.withExp(200)
			.addItems(() -> {
				var item = ItemStack.of(Material.ENCHANTED_BOOK);
				item.setData(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantments.itemEnchantments().add(Enchantment.SHARPNESS, 4).build());
				return item;
			})
		)
		.buildAndRegister();
	public static final IAdvancement KILL_25K = buildBase(KILL_2500, "kill_25k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.DIAMOND_SWORD))
		.withReward(rewards()
			.withExp(500)
			.addItems(() -> {
				var item = ItemStack.of(Material.ENCHANTED_BOOK);
				item.setData(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantments.itemEnchantments().add(Enchantment.SHARPNESS, 5).build());
				return item;
			})
			.addItems(() -> {
				var item = ItemStack.of(Material.ENCHANTED_BOOK);
				item.setData(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantments.itemEnchantments().add(Enchantment.MENDING, 1).build());
				return item;
			})
			.withTrophy(ItemStack.of(Material.DANDELION))
		)
		.buildAndRegister();
	public static final IAdvancement KILL_50K = buildBase(KILL_25K, "kill_50k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().superChallenge().icon(Material.GOLDEN_SWORD)).buildAndRegister();
	public static final IAdvancement KILL_100K = buildBase(KILL_50K, "kill_100k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().torture().icon(Material.NETHERITE_SWORD)).buildAndRegister();
	public static final IAdvancement KILL_250K = buildBase(KILL_100K, "kill_250k").display(display().x(1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(Material.WOODEN_SWORD)).buildAndRegister();
	public static final IAdvancement KILL_500K = buildBase(KILL_250K, "kill_500k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(ItemUtil.glint(Material.NETHERITE_SWORD)))
		.withReward(rewards()
			.withExp(400)
			.withTrophy(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA3MTU0OTFiZjJiODVjMjc2ZmZiNzAxN2JhY2I0NDNjNzg5YjBlNWQ0NjVmYzMzYjVmOTkzYmQzYjQyMGZiZCJ9fX0=")) // todo textured heads
		)
		.buildAndRegister();
	public static final IAdvancement TRADE_100 = buildBase(STATISTICS_RIGHT_LINKER, "trade_100").display(display().xy(1F, -3F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.EMERALD))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.EMERALD, 8)))
		.buildAndRegister();
	public static final IAdvancement TRADE_500 = buildBase(TRADE_100, "trade_500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.EMERALD_BLOCK))
		.withReward(rewards().withExp(200).addItems(ItemStack.of(Material.EMERALD, 24)))
		.buildAndRegister();
	public static final IAdvancement TRADE_2500 = buildBase(TRADE_500, "trade_2500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.EGG))
		.withReward(rewards()
			.withExp(500)
			.addItems(ItemStack.of(Material.EMERALD, 64))
			.withTrophy(ItemStack.of(Material.MAGENTA_GLAZED_TERRACOTTA))
		)
		.buildAndRegister();
	public static final IAdvancement TRADE_10K = buildBase(TRADE_2500, "trade_10k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().superChallenge().icon(Material.PUMPKIN))
		.withReward(rewards()
			.withExp(400)
			.withTrophy(STONKS_HEAD)
		)
		.buildAndRegister();
	public static final IAdvancement TRADE_25K = buildBase(TRADE_10K, "trade_25k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().torture().icon(Material.MELON)).buildAndRegister();
	public static final IAdvancement TRADE_50K = buildBase(TRADE_25K, "trade_50k").display(display().x(1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(Material.GLASS)).buildAndRegister();
	public static final IAdvancement TRADE_250K = buildBase(TRADE_50K, "trade_250k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(TRADER_HEAD)).buildAndRegister();
	public static final IAdvancement LOOT_10 = buildBase(STATISTICS_RIGHT_LINKER, "loot_10").display(display().xy(1F, -4F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.CHEST)).requiredProgress(simple(10, 1))
		.withReward(rewards().withExp(50))
		.buildAndRegister();
	public static final IAdvancement LOOT_100 = buildBase(LOOT_10, "loot_100").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.BARREL)).requiredProgress(simple(100, 1))
		.withReward(rewards().withExp(200))
		.buildAndRegister();
	public static final IAdvancement LOOT_500 = buildBase(LOOT_100, "loot_500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).goalFrame().icon(Material.ENDER_CHEST)).requiredProgress(simple(500, 1))
		.withReward(rewards()
			.withExp(500)
			.withTrophy(ItemStack.of(Material.TRAPPED_CHEST))
		)
		.buildAndRegister();
	public static final IAdvancement LOOT_1000 = buildBase(LOOT_500, "loot_1000").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().superChallenge().icon(Material.ENCHANTED_GOLDEN_APPLE)).requiredProgress(simple(1000, 1)).buildAndRegister();
	public static final IAdvancement LOOT_2500 = buildBase(LOOT_1000, "loot_2500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().torture().icon(ItemUtil.glint(Material.BARREL))).requiredProgress(simple(2500, 1))
		.withReward(rewards()
			.withExp(550)
			.withTrophy(ItemStack.of(Material.CHEST))
		)
		.buildAndRegister();
	public static final IAdvancement LOOT_5K = buildBase(LOOT_2500, "loot_5k").display(display().x(1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(ItemUtil.glint(Material.ENDER_CHEST))).requiredProgress(simple(5000, 1)).buildAndRegister();
	public static final IAdvancement LOOT_10K = buildBase(LOOT_5K, "loot_10k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(Material.FLINT_AND_STEEL)).requiredProgress(simple(10000, 1)).buildAndRegister();
	public static final IAdvancement OPEN_CHEST_100 = buildBase(STATISTICS_RIGHT_LINKER, "open_chest_100").display(display().xy(1F, -5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.CHEST))
		.withReward(rewards().withExp(15))
		.requiredProgress(simple(100))
		.buildAndRegister();
	public static final IAdvancement OPEN_CHEST_1K = buildBase(OPEN_CHEST_100, "open_chest_1k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.CHEST_MINECART))
		.withReward(rewards().withExp(30))
		.requiredProgress(simple(1000, 100))
		.buildAndRegister();
	public static final IAdvancement OPEN_CHEST_10K = buildBase(OPEN_CHEST_1K, "open_chest_10k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(CHEST_HEAD))
		.withReward(rewards()
			.withExp(80)
			.withTrophy(ItemStack.of(Material.CHEST))
		)
		.requiredProgress(simple(10_000, 100))
		.buildAndRegister();
	public static final IAdvancement OPEN_CHEST_25K = buildBase(OPEN_CHEST_10K, "open_chest_25k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).withAdvancementFrame(AdvancementFrame.BUTTERFLY).icon(CHRISTMAS_CHEST_HEAD))
		.withReward(rewards()
			.withExp(150)
			.withTrophy(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzZkZjQ3YzMwYjFlM2RiNTJlNDFmNWVlYjgwNmM2OWZlZjgwNTk1NTBlOGY1N2IwYTgzYjIyNjBhNjZkOTI3ZSJ9fX0=")) // todo textured heads
		)
		.requiredProgress(simple(25_000, 25))
		.buildAndRegister();
	public static final IAdvancement OPEN_SHULKER_100 = buildBase(OPEN_CHEST_25K, "open_shulker_100").display(display().x(1.5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.WHITE_SHULKER_BOX))
		.withReward(rewards().withExp(25))
		.requiredProgress(simple(100))
		.buildAndRegister();
	public static final IAdvancement OPEN_SHULKER_1K = buildBase(OPEN_SHULKER_100, "open_shulker_1k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.LIGHT_GRAY_SHULKER_BOX))
		.withReward(rewards().withExp(30))
		.requiredProgress(simple(1000, 100))
		.buildAndRegister();
	public static final IAdvancement OPEN_SHULKER_10K = buildBase(OPEN_SHULKER_1K, "open_shulker_10k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.GRAY_SHULKER_BOX))
		.withReward(rewards()
			.withExp(100)
			.withTrophy(ItemStack.of(Material.PINK_SHULKER_BOX))
		)
		.requiredProgress(simple(10_000, 100))
		.buildAndRegister();
	public static final IAdvancement OPEN_SHULKER_100K = buildBase(OPEN_SHULKER_10K, "open_shulker_100k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).withAdvancementFrame(AdvancementFrame.BUTTERFLY).icon(Material.BLACK_SHULKER_BOX))
		.withReward(rewards()
			.withExp(300)
			.withTrophy(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjA4OGNjODJhNTk3NGYxMWYxYjg4ZGRjMTE0YjI2MjE2MWE0ZmJjMDkwZDIxMzQ0OTAzNTlhMjFlNDEyYSJ9fX0=")) // todo textured heads
		)
		.requiredProgress(simple(100_000, 100))
		.buildAndRegister();
	public static final IAdvancement OPEN_CRAFTING_TABLE_15 = buildBase(STATISTICS_RIGHT_LINKER, "open_crafting_table_15").display(display().xy(1F, -6F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.OAK_LOG))
		.withReward(rewards().withExp(5))
		.requiredProgress(simple(15))
		.buildAndRegister();
	public static final IAdvancement OPEN_CRAFTING_TABLE_100 = buildBase(OPEN_CRAFTING_TABLE_15, "open_crafting_table_100").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.OAK_PLANKS))
		.withReward(rewards().withExp(15))
		.requiredProgress(simple(100))
		.buildAndRegister();
	public static final IAdvancement OPEN_CRAFTING_TABLE_500 = buildBase(OPEN_CRAFTING_TABLE_100, "open_crafting_table_500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(ItemUtil.glint(Material.CRAFTING_TABLE)))
		.withReward(rewards()
			.withExp(50)
			.withTrophy(ItemStack.of(Material.CRAFTING_TABLE))
		)
		.requiredProgress(simple(500, 100))
		.buildAndRegister();
	public static final IAdvancement OPEN_CRAFTING_TABLE_2500 = buildBase(OPEN_CRAFTING_TABLE_500, "open_crafting_table_2500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).withAdvancementFrame(AdvancementFrame.BUTTERFLY).icon(CRAFTING_TABLE_HEAD))
		.withReward(rewards()
			.withExp(130)
			.withTrophy(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmYyNTc3MjY5MzdiYTE4MTQwOTYyMDllYjdiNTE2OGM2NjU1MmQyNWU0MWIxZTUxNGFhNmQzMWM0ZDNhYTZkYyJ9fX0="))
		)
		.requiredProgress(simple(2500, 25))
		.buildAndRegister();
	public static final IAdvancement BREAK_100_IRON = buildBase(STATISTICS_RIGHT_LINKER, "break_100_iron").display(display().xy(1F, -7F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.IRON_PICKAXE))
		.withReward(rewards().withExp(20).addItems(ItemStack.of(Material.COBBLESTONE, 16)))
		.requiredProgress(simple(100))
		.buildAndRegister();
	public static final IAdvancement BREAK_2500_DIAMOND = buildBase(BREAK_100_IRON, "break_2500_diamond").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.DIAMOND_PICKAXE))
		.withReward(rewards().withExp(35).addItems(ItemStack.of(Material.IRON_INGOT, 16)))
		.requiredProgress(simple(2500, 25))
		.buildAndRegister();
	public static final IAdvancement BREAK_10K_NETHERITE = buildBase(BREAK_2500_DIAMOND, "break_10k_netherite").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.NETHERITE_PICKAXE))
		.withReward(rewards()
			.withExp(150)
			.addItems(ItemStack.of(Material.DIAMOND, 5))
			.withTrophy(ItemStack.of(Material.DIAMOND_ORE))
		)
		.requiredProgress(simple(10_000, 100))
		.buildAndRegister();
	public static final IAdvancement BREAK_100K_NETHERITE = buildBase(BREAK_10K_NETHERITE, "break_100k_netherite").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).withAdvancementFrame(AdvancementFrame.BUTTERFLY).icon(ItemUtil.glint(Material.NETHERITE_PICKAXE)))
		.withReward(rewards()
			.withExp(400)
			.addItems(ItemStack.of(Material.DIAMOND_ORE, 5))
			.withTrophy(() -> {
				var item = ItemStack.of(Material.GOLDEN_PICKAXE);
				item.addUnsafeEnchantment(Enchantment.EFFICIENCY, 6);
				return item;
			})
		)
		.requiredProgress(simple(100_000, 100))
		.buildAndRegister();
	public static final IAdvancement SURVIVE_1H = buildBase(STATISTICS_RIGHT_LINKER, "survive_1h").display(display().xy(1F, -8F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.CLOCK))
		.withReward(rewards().withExp(30))
		.buildAndRegister();
	public static final IAdvancement SURVIVE_10H = buildBase(SURVIVE_1H, "survive_10h").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(SURVIVE_HEAD_1))
		.withReward(rewards().withExp(70))
		.buildAndRegister();
	public static final IAdvancement SURVIVE_50H = buildBase(SURVIVE_10H, "survive_50h").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(SURVIVE_HEAD_2))
		.withReward(rewards()
			.withExp(300)
			.withTrophy(ItemStack.of(Material.CLOCK))
		)
		.buildAndRegister();
	public static final IAdvancement SURVIVE_200H = buildBase(SURVIVE_50H, "survive_200h").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).withAdvancementFrame(AdvancementFrame.BUTTERFLY).superChallenge().icon(SURVIVE_HEAD_3))
		.withReward(rewards()
			.withExp(1000)
			.withTrophy(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmUwOTk4NTgxMDg3ZWYyY2M3ZTA1OGQ3ZGZjZTIxOTZiY2FjZDk2ZmY0NjhlNjQ4MjM3YWE4OWQ4ZWI2NmUzMCJ9fX0=")) // TODO textured heads
		)
		.buildAndRegister();
	public static final IAdvancement DEATHS_1 = buildBase(SURVIVE_200H, "deaths_1").display(display().x(1.5F).fancyDescriptionParent(NamedTextColor.DARK_RED).icon(RequiemItems.HEART)).buildAndRegister();
	public static final IAdvancement DEATHS_50 = buildBase(DEATHS_1, "deaths_50").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.BONE))
		.requiredProgress(simple(50))
		.buildAndRegister();
	public static final IAdvancement DEATHS_250 = buildBase(DEATHS_50, "deaths_250").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(RequiemItems.SPINE))
		.requiredProgress(simple(250, 25))
		.buildAndRegister();
	public static final IAdvancement DEATHS_1000 = buildBase(DEATHS_250, "deaths_1000").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).withAdvancementFrame(AdvancementFrame.BUTTERFLY).icon(Material.SKELETON_SKULL))
		.requiredProgress(simple(1000, 100))
		.buildAndRegister();
//	public static final IAdvancement HORSE_SPEED_1 = buildBase(STATISTICS_RIGHT_LINKER, "horse_speed_1").display(display().xy(1F, -9F).fancyDescriptionParent(NamedTextColor.GREEN).icon(ItemStack.of(Material.LEATHER_HORSE_ARMOR).withColor(TextColor.color(16707753))))
//		.withReward(rewards().withExp(10))
//		.buildAndRegister();
//	public static final IAdvancement HORSE_SPEED_2 = buildBase(HORSE_SPEED_1, "horse_speed_2").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(ItemStack.of(Material.LEATHER_HORSE_ARMOR).withColor(TextColor.color(16495988))))
//		.withReward(rewards().withExp(50))
//		.buildAndRegister();
//	public static final IAdvancement HORSE_SPEED_3 = buildBase(HORSE_SPEED_2, "horse_speed_3").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(ItemStack.of(Material.LEATHER_HORSE_ARMOR).withColor(TextColor.color(16481652))))
//		.withReward(rewards()
//			.withExp(75)
//			.withTrophy(ItemStack.of(Material.SADDLE))
//		)
//		.buildAndRegister();
//	public static final IAdvancement HORSE_SPEED_4 = buildBase(HORSE_SPEED_3, "horse_speed_4").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).withAdvancementFrame(AdvancementFrame.BUTTERFLY).icon(ItemStack.of(Material.LEATHER_HORSE_ARMOR).withColor(TextColor.color(16352992))))
//		.withReward(rewards()
//			.withExp(300)
//			.withTrophy(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzBjNDBjOWQ0NGQ2NjA5YmNkOGFmMjgyOTcyMThhY2U3ZWVhMmU0NTFjNDNkNzZiYWFmOTJjYmVmMGEwZGNhZSJ9fX0="))
//		)
//		.buildAndRegister();

	// Left top
	public static final IAdvancement WALK_10KM = buildBase(STATISTICS_ROOT, "walk_10km").display(display().x(-1.75F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.LEATHER_BOOTS))
		.withReward(rewards().withExp(50))
		.buildAndRegister();
	public static final IAdvancement WALK_50KM = buildBase(WALK_10KM, "walk_50km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.CHAINMAIL_BOOTS))
		.withReward(rewards().withExp(200))
		.buildAndRegister();
	public static final IAdvancement WALK_250KM = buildBase(WALK_50KM, "walk_250km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.IRON_BOOTS))
		.withReward(rewards()
			.withExp(500)
			.withTrophy(() -> {
				var item = ItemStack.of(Material.LEATHER_BOOTS);
				item.setData(DataComponentTypes.UNBREAKABLE);
				return item;
			})
		)
		.buildAndRegister();
	public static final IAdvancement WALK_1000KM = buildBase(WALK_250KM, "walk_1000km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().torture().icon(Material.GOLDEN_BOOTS))
		.withReward(rewards()
			.withExp(1000)
			.withTrophy(() -> {
				var item = ItemStack.of(Material.CLOCK);
				item.setData(DataComponentTypes.USE_COOLDOWN, UseCooldown.useCooldown(60 * 60).cooldownGroup(trappedNewbieKey("time_machine")).build());
				return item;
			}).withTrophyModel(trappedNewbieKey("time_machine"))
		)
		.buildAndRegister(Walk10KKMAdvancement::new);
	public static final IAdvancement WALK_5000KM = buildBase(WALK_1000KM, "walk_5000km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().superTorture().icon(Material.DIAMOND_BOOTS))
		.withReward(rewards().withExp(5000))
		.buildAndRegister();
	public static final IAdvancement WALK_10000KM = buildBase(WALK_5000KM, "walk_10000km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(Material.NETHERITE_BOOTS))
		.withReward(rewards().withExp(10000))
		.buildAndRegister();
	public static final IAdvancement SPRINT_10KM = buildBase(WALK_10KM, "sprint_10km").display(display().y(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.RED_TERRACOTTA))
		.withReward(rewards().withExp(50))
		.buildAndRegister();
	public static final IAdvancement SPRINT_MARATHON = buildBase(SPRINT_10KM, "sprint_marathon").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.GRASS_BLOCK))
		.withReward(rewards().withExp(200))
		.buildAndRegister();
	public static final IAdvancement SPRINT_250KM = buildBase(SPRINT_MARATHON, "sprint_250km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.CHAINMAIL_LEGGINGS))
		.withReward(rewards()
			.withExp(500)
			.withTrophy(ItemStack.of(TrappedNewbieItems.DRAGON_FLASK))
		)
		.buildAndRegister();
	public static final IAdvancement SPRINT_1000KM = buildBase(SPRINT_250KM, "sprint_1000km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().superChallenge().icon(ItemUtil.glint(Material.NETHERITE_LEGGINGS)))
		.withReward(rewards()
			.withExp(400)
			.withTrophy(ItemStack.of(Material.CHAINMAIL_BOOTS))
		)
		.buildAndRegister();
	public static final IAdvancement SPRINT_2500KM = buildBase(SPRINT_1000KM, "sprint_2500km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().torture().icon(potionItem(Material.SPLASH_POTION, PotionType.INVISIBILITY))).buildAndRegister();
	public static final IAdvancement SPRINT_5000KM = buildBase(SPRINT_2500KM, "sprint_5000km").display(display().x(-1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(Material.FEATHER)).buildAndRegister();
	public static final IAdvancement SPRINT_10000KM = buildBase(SPRINT_5000KM, "sprint_10000km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(Material.BARRIER)).buildAndRegister();
	public static final IAdvancement JUMP_1K = buildBase(WALK_10KM, "jump_1k").display(display().y(2F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.OAK_FENCE))
		.withReward(rewards().withExp(50))
		.buildAndRegister();
	public static final IAdvancement JUMP_10K = buildBase(JUMP_1K, "jump_10k").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.ACACIA_LEAVES))
		.withReward(rewards().withExp(200))
		.buildAndRegister();
	public static final IAdvancement JUMP_50K = buildBase(JUMP_10K, "jump_50k").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).goalFrame().icon(Material.CYAN_CARPET))
		.withReward(rewards()
			.withExp(500)
			.withTrophy(ItemStack.of(Material.SLIME_BLOCK))
		)
		.buildAndRegister();
	public static final IAdvancement JUMP_100K = buildBase(JUMP_50K, "jump_100k").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().superChallenge().icon(Material.SLIME_BLOCK)).buildAndRegister();
	public static final IAdvancement JUMP_250K = buildBase(JUMP_100K, "jump_250k").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().torture().icon(Material.RABBIT_FOOT))
		.withReward(rewards()
			.withExp(400)
			.withTrophy(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTE0NDkxOGUzNjc5ZjE5OGRmYWE3MzNiZjAwMzViY2NlMWVkMjkxOGQ1OTczMWVjNjU0MzgzNzJjM2U1NDhmIn19fQ==")) // todo textured heads
		)
		.buildAndRegister();
	public static final IAdvancement JUMP_500K = buildBase(JUMP_250K, "jump_500k").display(display().x(-1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(potionItem(Material.POTION, PotionType.LEAPING))).buildAndRegister();
	public static final IAdvancement JUMP_1000K = buildBase(JUMP_500K, "jump_1000k").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(Material.PINK_BED)).buildAndRegister();
	public static final IAdvancement BOAT_1KM = buildBase(WALK_10KM, "boat_1km").display(display().y(3F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.OAK_BOAT))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.NAUTILUS_SHELL)))
		.buildAndRegister();
	public static final IAdvancement BOAT_10KM = buildBase(BOAT_1KM, "boat_10km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.SPRUCE_BOAT))
		.withReward(rewards().withExp(200).addItems(ItemStack.of(Material.NAUTILUS_SHELL, 3)))
		.buildAndRegister();
	public static final IAdvancement BOAT_25KM = buildBase(BOAT_10KM, "boat_25km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.DARK_OAK_BOAT))
		.withReward(rewards()
			.withExp(500)
			.addItems(ItemStack.of(Material.NAUTILUS_SHELL, 8))
			.withTrophy(ItemStack.of(Material.DARK_OAK_BOAT))
		)
		.buildAndRegister();
	public static final IAdvancement BOAT_50KM = buildBase(BOAT_25KM, "boat_50km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().superChallenge().icon(Material.MANGROVE_BOAT)).buildAndRegister();
	public static final IAdvancement BOAT_100KM = buildBase(BOAT_50KM, "boat_100km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().torture().icon(Material.ACACIA_BOAT)).buildAndRegister();
	public static final IAdvancement BOAT_250KM = buildBase(BOAT_100KM, "boat_250km").display(display().x(-1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(Material.BIRCH_BOAT)).buildAndRegister();
	public static final IAdvancement BOAT_500KM = buildBase(BOAT_250KM, "boat_500km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(ItemUtil.glint(Material.CHERRY_BOAT)))
		.withReward(rewards()
			.withExp(500)
			.withTrophy(ItemStack.of(Material.CHERRY_BOAT))
		)
		.buildAndRegister();
	public static final IAdvancement HORSE_1KM = buildBase(WALK_10KM, "horse_1km").display(display().y(4F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.IRON_HORSE_ARMOR))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.LEATHER, 3), ItemStack.of(Material.IRON_HORSE_ARMOR)))
		.buildAndRegister();
	public static final IAdvancement HORSE_10KM = buildBase(HORSE_1KM, "horse_10km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.GOLDEN_HORSE_ARMOR))
		.withReward(rewards().withExp(200).addItems(ItemStack.of(Material.LEATHER, 8), ItemStack.of(Material.GOLDEN_HORSE_ARMOR)))
		.buildAndRegister();
	public static final IAdvancement HORSE_25KM = buildBase(HORSE_10KM, "horse_25km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.DIAMOND_HORSE_ARMOR))
		.withReward(rewards()
			.withExp(500)
			.addItems(ItemStack.of(Material.LEATHER, 25), ItemStack.of(Material.DIAMOND_HORSE_ARMOR), ItemStack.of(Material.SADDLE))
			.withTrophy(() -> {
				var item = ItemStack.of(Material.BOW);
				item.setData(DataComponentTypes.UNBREAKABLE);
				return item;
			})
		)
		.buildAndRegister();
	public static final IAdvancement HORSE_50KM = buildBase(HORSE_25KM, "horse_50km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().superChallenge().icon(Material.BONE)).buildAndRegister();
	public static final IAdvancement HORSE_100KM = buildBase(HORSE_50KM, "horse_100km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().torture().icon(ItemUtil.texturedHead(MoreMobHeads.SKELETON_HORSE))).buildAndRegister();
	public static final IAdvancement HORSE_250KM = buildBase(HORSE_100KM, "horse_250km").display(display().x(-1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(Material.SADDLE))
		.withReward(rewards()
			.withExp(250)
			.withTrophy(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Q4ODY0M2IwNDg2Nzk2MjRhNDJlZTA0NjY2YTQ4NjVlYzE2ODcxYzczMjc5NzM0NjVhZjZiZDVhYmRkOGNhNyJ9fX0=")) // todo textured heads
		)
		.buildAndRegister();
	public static final IAdvancement HORSE_500KM = buildBase(HORSE_250KM, "horse_500km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(wrathHorseItem())).buildAndRegister();
	public static final IAdvancement PIG_100M = buildBase(WALK_10KM, "pig_100m").display(display().y(5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.SADDLE))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.PORKCHOP, 3)))
		.buildAndRegister();
	public static final IAdvancement PIG_1KM = buildBase(PIG_100M, "pig_1km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.CARROT_ON_A_STICK))
		.withReward(rewards().withExp(200).addItems(ItemStack.of(Material.PORKCHOP, 8), ItemStack.of(Material.CARROT, 16)))
		.buildAndRegister();
	public static final IAdvancement PIG_10KM = buildBase(PIG_1KM, "pig_10km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(PIG_HEAD))
		.withReward(rewards()
			.withExp(500)
			.addItems(ItemStack.of(Material.PORKCHOP, 24), ItemStack.of(Material.SADDLE))
			.withTrophy(ItemStack.of(Material.PISTON))
		)
		.buildAndRegister();
	public static final IAdvancement PIG_25KM = buildBase(PIG_10KM, "pig_25km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().superChallenge().icon(Material.PORKCHOP)).buildAndRegister();
	public static final IAdvancement PIG_50KM = buildBase(PIG_25KM, "pig_50km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().torture().icon(Material.COOKED_PORKCHOP)).buildAndRegister();
	public static final IAdvancement PIG_100KM = buildBase(PIG_50KM, "pig_100km").display(display().x(-1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(TrappedNewbieItems.ASH))
		.withReward(rewards()
			.withExp(200)
			.withTrophy(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2FiZjM0OWUwOTU5NGE2ZjYzMGEzMWU2MDdlY2U5YTExODc0N2UxN2U5NmUyN2ExOGExMWFlMDljZjFiMTJkMSJ9fX0=")) // todo textured heads
		)
		.buildAndRegister();
	public static final IAdvancement PIG_250KM = buildBase(PIG_100KM, "pig_250km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(PIG_HOG_HEAD)).buildAndRegister();
	public static final IAdvancement ELYTRA_10KM = buildBase(WALK_10KM, "elytra_10km").display(display().y(6F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.PHANTOM_MEMBRANE))
		.withReward(rewards()
			.withExp(50)
			.addItems(() -> {
				var item = ItemStack.of(Material.FIREWORK_ROCKET, 64);
				item.setData(DataComponentTypes.FIREWORKS, Fireworks.fireworks().flightDuration(1).build());
				return item;
			})
		)
		.buildAndRegister();
	public static final IAdvancement ELYTRA_100KM = buildBase(ELYTRA_10KM, "elytra_100km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.ELYTRA))
		.withReward(rewards()
			.withExp(200)
			.addItems(() -> {
				var item = ItemStack.of(Material.FIREWORK_ROCKET, 192);
				item.setData(DataComponentTypes.FIREWORKS, Fireworks.fireworks().flightDuration(2).build());
				return item;
			})
		)
		.buildAndRegister();
	public static final IAdvancement ELYTRA_1000KM = buildBase(ELYTRA_100KM, "elytra_1000km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.FIREWORK_ROCKET))
		.withReward(rewards()
			.withExp(500)
			.addItems(() -> {
				var fireworks = ItemStack.of(Material.FIREWORK_ROCKET, 64);
				fireworks.setData(DataComponentTypes.FIREWORKS, Fireworks.fireworks().flightDuration(3).build());
				var item = ItemStack.of(Material.RED_SHULKER_BOX);
				List<ItemStack> contents = new ArrayList<>(27);
				for (int i = 0; i < 27; i++) {
					if (i % 9 >= 3 && i % 9 <= 5) {
						contents.add(item);
					} else {
						contents.add(ItemStack.empty());
					}
				}
				item.setData(DataComponentTypes.CONTAINER, ItemContainerContents.containerContents(contents));
				return item;
			})
			.withTrophy(() -> {
				var item = ItemStack.of(Material.ELYTRA);
				item.addEnchantment(Enchantment.UNBREAKING, 3);
				item.addEnchantment(Enchantment.MENDING, 1);
				return item;
			})
		)
		.buildAndRegister();
	public static final IAdvancement ELYTRA_2500KM = buildBase(ELYTRA_1000KM, "elytra_2500km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().superChallenge().icon(Material.PURPUR_BLOCK)).buildAndRegister();
	public static final IAdvancement ELYTRA_5000KM = buildBase(ELYTRA_2500KM, "elytra_5000km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().torture().icon(Material.FIREWORK_STAR)).buildAndRegister();
	public static final IAdvancement ELYTRA_10000KM = buildBase(ELYTRA_5000KM, "elytra_10000km").display(display().x(-1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(brokenElytra())).buildAndRegister();
	public static final IAdvancement ELYTRA_15000KM = buildBase(ELYTRA_10000KM, "elytra_15000km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(ItemUtil.glint(Material.ELYTRA)))
		.withReward(rewards()
			.withTrophy(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTNjMjQ4NzBhNjVlNGYzZmE0M2I5MmYxOTRhM2M2ZjdlYzllNmNkNmM1MmYxZTY2MjRlNWMyNmI4ZGQ1ZDkifX19")) // todo textured heads
		)
		.buildAndRegister();
	// Left bottom
	public static final IAdvancement SWIM_1KM = buildBase(WALK_10KM, "swim_1km").display(display().y(-1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(ItemStack.of(Material.LEATHER_LEGGINGS).withColor(Color.BLUE)))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.PUFFERFISH, 3)))
		.buildAndRegister();
	public static final IAdvancement SWIM_10KM = buildBase(SWIM_1KM, "swim_10km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.GOLDEN_LEGGINGS))
		.withReward(rewards().withExp(200).addItems(ItemStack.of(Material.PUFFERFISH, 8)))
		.buildAndRegister();
	public static final IAdvancement SWIM_50KM = buildBase(SWIM_10KM, "swim_50km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.GOLD_BLOCK))
		.withReward(rewards()
			.withExp(500)
			.addItems(ItemStack.of(Material.PUFFERFISH, 24))
			.withTrophy(ItemStack.of(Material.SUNFLOWER))
		)
		.buildAndRegister();
	public static final IAdvancement SWIM_100KM = buildBase(SWIM_50KM, "swim_100km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().superChallenge().icon(Material.WATER_BUCKET)).buildAndRegister();
	public static final IAdvancement SWIM_250KM = buildBase(SWIM_100KM, "swim_250km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().torture().icon(ItemUtil.glint(Material.RAW_GOLD_BLOCK)))
		.withReward(rewards()
			.withExp(400)
			.withTrophy(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzI2NjY2ODFkMzk0MzI1YzJkMWMwN2E0ZDJkZTRmZjEwZWMxODkwZmI1MGNhYTMzZmEyOWNjODc1ZWMxZDhlIn19fQ=="))
		)
		.buildAndRegister();
	public static final IAdvancement SWIM_500KM = buildBase(SWIM_250KM, "swim_500km").display(display().x(-1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(Material.CONDUIT)).buildAndRegister();
	public static final IAdvancement SWIM_1000KM = buildBase(SWIM_500KM, "swim_1000km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(Material.COD)).buildAndRegister();
	public static final IAdvancement SNEAK_100M = buildBase(WALK_10KM, "sneak_100m").display(display().y(-2F).fancyDescriptionParent(NamedTextColor.GREEN).icon(ItemStack.of(Material.LEATHER_BOOTS).withColor(Color.BLACK)))
		.withReward(rewards().withExp(50))
		.buildAndRegister();
	public static final IAdvancement SNEAK_1KM = buildBase(SNEAK_100M, "sneak_1km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(ItemStack.of(Material.LEATHER_CHESTPLATE).withColor(Color.BLACK)))
		.withReward(rewards().withExp(200))
		.buildAndRegister();
	public static final IAdvancement SNEAK_10KM = buildBase(SNEAK_1KM, "sneak_10km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.PANDA_SPAWN_EGG))
		.withReward(rewards()
			.withExp(500)
			.withTrophy(() -> {
				var item = ItemStack.of(Material.LEATHER_LEGGINGS);
				item.setData(DataComponentTypes.UNBREAKABLE);
				item.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(Color.BLACK));
				return item;
			})
		)
		.buildAndRegister();
	public static final IAdvancement SNEAK_25KM = buildBase(SNEAK_10KM, "sneak_25km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().superChallenge().icon(potionItem(Material.POTION, PotionType.INVISIBILITY))).buildAndRegister();
	public static final IAdvancement SNEAK_50KM = buildBase(SNEAK_25KM, "sneak_50km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().torture().icon(Material.BARRIER)).buildAndRegister();
	public static final IAdvancement SNEAK_100KM = buildBase(SNEAK_50KM, "sneak_100km").display(display().x(-1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(Material.STRUCTURE_VOID))
		.withReward(rewards()
			.withExp(400)
			.withTrophy(ItemStack.of(Material.IRON_SWORD))
		)
		.buildAndRegister();
	public static final IAdvancement SNEAK_250KM = buildBase(SNEAK_100KM, "sneak_250km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(SHIFTING_HEAD)).buildAndRegister();
	public static final IAdvancement CLIMB_100M = buildBase(WALK_10KM, "climb_100m").display(display().y(-3F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.VINE))
		.withReward(rewards().withExp(5))
		.buildAndRegister();
	public static final IAdvancement CLIMB_500M = buildBase(CLIMB_100M, "climb_500m").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.WEEPING_VINES))
		.withReward(rewards().withExp(45))
		.buildAndRegister();
	public static final IAdvancement CLIMB_3KM = buildBase(CLIMB_500M, "climb_3km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.LADDER))
		.withReward(rewards()
			.withExp(160)
			.withTrophy(ItemStack.of(Material.LADDER))
		)
		.buildAndRegister();
	public static final IAdvancement CLIMB_10KM = buildBase(CLIMB_3KM, "climb_10km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).withAdvancementFrame(AdvancementFrame.BUTTERFLY).icon(LADDER_ACE_HEAD))
		.withReward(rewards()
			.withExp(300)
			.withTrophy(ItemStack.of(Material.LADDER))
		)
		.buildAndRegister();
	public static final IAdvancement MINECART_1KM = buildBase(WALK_10KM, "minecart_1km").display(display().y(-4F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.MINECART))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.IRON_INGOT, 8)))
		.buildAndRegister();
	public static final IAdvancement MINECART_10KM = buildBase(MINECART_1KM, "minecart_10km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.FURNACE_MINECART))
		.withReward(rewards().withExp(200).addItems(ItemStack.of(Material.IRON_INGOT, 24)))
		.buildAndRegister();
	public static final IAdvancement MINECART_50KM = buildBase(MINECART_10KM, "minecart_50km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.COMMAND_BLOCK_MINECART))
		.withReward(rewards()
			.withExp(500)
			.addItems(ItemStack.of(Material.IRON_BLOCK, 8))
			.withTrophy(ItemStack.of(Material.FURNACE_MINECART))
		)
		.buildAndRegister();
	public static final IAdvancement MINECART_250KM = buildBase(MINECART_50KM, "minecart_250km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().superChallenge().icon(MINECART_HEAD))
		.withReward(rewards()
			.withExp(250)
			.withTrophy(ItemStack.of(Material.POWERED_RAIL))
		)
		.buildAndRegister();
	public static final IAdvancement MINECART_500KM = buildBase(MINECART_250KM, "minecart_500km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().torture().icon(Material.HOPPER_MINECART)).buildAndRegister();
	public static final IAdvancement MINECART_5000KM = buildBase(MINECART_500KM, "minecart_5000km").display(display().x(-1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(Material.CHEST_MINECART)).buildAndRegister();
	public static final IAdvancement MINECART_15000KM = buildBase(MINECART_5000KM, "minecart_15000km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(Material.TNT_MINECART)).buildAndRegister();
	public static final IAdvancement STRIDER_100M = buildBase(WALK_10KM, "strider_100m").display(display().y(-5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.LAVA_BUCKET))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.WARPED_FUNGUS, 3)))
		.buildAndRegister();
	public static final IAdvancement STRIDER_1KM = buildBase(STRIDER_100M, "strider_1km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.WARPED_FUNGUS_ON_A_STICK))
		.withReward(rewards().withExp(200).addItems(ItemStack.of(Material.WARPED_FUNGUS, 8)))
		.buildAndRegister();
	public static final IAdvancement STRIDER_10KM = buildBase(STRIDER_1KM, "strider_10km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.STRIDER_SPAWN_EGG))
		.withReward(rewards()
			.withExp(500)
			.addItems(ItemStack.of(Material.WARPED_FUNGUS, 24))
			.withTrophy(ItemStack.of(Material.WARPED_FUNGUS))
		)
		.buildAndRegister();
	public static final IAdvancement STRIDER_25KM = buildBase(STRIDER_10KM, "strider_25km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().superChallenge().icon(Material.WARPED_FUNGUS)).buildAndRegister();
	public static final IAdvancement STRIDER_50KM = buildBase(STRIDER_25KM, "strider_50km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().torture().icon(Material.LEAD)).buildAndRegister();
	public static final IAdvancement STRIDER_100KM = buildBase(STRIDER_50KM, "strider_100km").display(display().x(-1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(Material.LEAD)).buildAndRegister();
	public static final IAdvancement STRIDER_250KM = buildBase(STRIDER_100KM, "strider_250km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(ItemUtil.texturedHead(MoreMobHeads.STRIDER)))
		.withReward(rewards()
			.withExp(180)
			.withTrophy(ItemUtil.texturedHead(MoreMobHeads.STRIDER)) // TODO actual heads should render texture in modifier
		)
		.buildAndRegister();
	public static final IAdvancement HAPPY_GHAST_100M = buildBase(WALK_10KM, "happy_ghast_100m").display(display().y(-6F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.LIGHT_GRAY_HARNESS))
		.withReward(rewards().withExp(50))
		.buildAndRegister();
	public static final IAdvancement HAPPY_GHAST_1KM = buildBase(HAPPY_GHAST_100M, "happy_ghast_1km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.BROWN_HARNESS))
		.withReward(rewards().withExp(200).addItems(ItemStack.of(Material.DRIED_GHAST)))
		.buildAndRegister();
	public static final IAdvancement HAPPY_GHAST_10KM = buildBase(HAPPY_GHAST_1KM, "happy_ghast_10km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.RED_HARNESS))
		.withReward(rewards()
			.withExp(500)
			.addItems(ItemStack.of(Material.DRIED_GHAST, 3))
			.withTrophy(ItemStack.of(Material.LIGHT_BLUE_WOOL))
		)
		.buildAndRegister();
	public static final IAdvancement HAPPY_GHAST_25KM = buildBase(HAPPY_GHAST_10KM, "happy_ghast_25km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().superChallenge().icon(Material.YELLOW_HARNESS)).buildAndRegister();
	public static final IAdvancement HAPPY_GHAST_50KM = buildBase(HAPPY_GHAST_25KM, "happy_ghast_50km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().torture().icon(Material.MAGENTA_HARNESS))
		.withReward(rewards()
			.withExp(1000)
			.withTrophy(ItemStack.of(Material.LIGHT_GRAY_HARNESS))
		)
		.buildAndRegister();
	public static final IAdvancement HAPPY_GHAST_100KM = buildBase(HAPPY_GHAST_50KM, "happy_ghast_100km").display(display().x(-1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(Material.PURPLE_HARNESS)).buildAndRegister();
	public static final IAdvancement HAPPY_GHAST_250KM = buildBase(HAPPY_GHAST_100KM, "happy_ghast_250km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(Material.BLACK_HARNESS)).buildAndRegister();

	private static ItemStack braveNewWorldItem() {
		var item = ItemStack.of(TrappedNewbieItems.LETTER);
		NBT.modify(item, nbt -> {
			nbt.setItemStackArray(LetterModifier.CONTENTS_TAG, new ItemStack[]{WANDERING_TRADER_HEAD});
			nbt.setEnum(LetterModifier.TYPE_TAG, LetterModifier.LetterType.STAR);
		});
		return item;
	}

	private static ItemStack brokenElytra() {
		var item = ItemStack.of(Material.ELYTRA);
		item.setData(DataComponentTypes.DAMAGE, 1);
		item.setData(DataComponentTypes.MAX_DAMAGE, 1);
		return item;
	}

	private static ItemStack wrathHorseItem() {
		var item = ItemStack.of(Material.LEATHER_HORSE_ARMOR);
		item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
		item.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(Color.fromRGB(10409302)));
		return item;
	}

	private static ItemStack potionItem(Material itemType, PotionType potionType) {
		var item = ItemStack.of(itemType);
		item.setData(DataComponentTypes.POTION_CONTENTS, PotionContents.potionContents().potion(potionType).build());
		return item;
	}

	private static ItemStack banner(Material type, Pattern... patterns) {
		var item = ItemStack.of(type);
		item.setData(DataComponentTypes.BANNER_PATTERNS, BannerPatternLayers.bannerPatternLayers(List.of(patterns)));
		return item;
	}

	private static ItemStack shield(DyeColor dyeColor) {
		var item = ItemStack.of(Material.SHIELD);
		item.setData(DataComponentTypes.BASE_COLOR, dyeColor);
		return item;
	}

	private static FancierAdvancementDisplay.FancierAdvancementDisplayImpl display() {
		return FancierAdvancementDisplay.fancierDisplay().fancyDescriptionParent(NamedTextColor.GRAY);
	}

	private static FancyAdvancementReward rewards() {
		return new FancyAdvancementReward();
	}

	public static void setupAdvancements() {
		Preconditions.checkArgument(KILL_ALL_ALL_JOCKEYS.getRequiredProgress().requirements().size() == 757, "KILL_ALL_ALL_JOCKEYS: Jockeys count changed to %s".formatted(KILL_ALL_ALL_JOCKEYS.getRequiredProgress().requirements().size()));
		Preconditions.checkArgument(FIND_ALL_STRUCTURES.getRequiredProgress().requirements().size() == 27, "FIND_ALL_STRUCTURES & USE_A_BRUSH_IN_ALL_STRUCTURES: Structure count changed to %s".formatted(FIND_ALL_STRUCTURES.getRequiredProgress().requirements().size()));
		Preconditions.checkArgument(TRADE_WITH_EVERY_VILLAGER_IN_ALL_BIOMES.getRequiredProgress().requirements().size() == 832, "TRADE_WITH_EVERY_VILLAGER: Villager types count changed to %s".formatted(TRADE_WITH_EVERY_VILLAGER_IN_ALL_BIOMES.getRequiredProgress().requirements().size()));

		AdvancementsAdvancement.addAdvancement(OBTAIN_EVERY_ARMOR_TRIM_WITH_EVERY_MATERIAL, Map.of(
			OBTAIN_EVERY_TURTLE_ARMOR_TRIM, "turtle",
			OBTAIN_EVERY_LEATHER_ARMOR_TRIM, "leather",
			OBTAIN_EVERY_CHAINMAIL_ARMOR_TRIM, "chainmail",
			OBTAIN_EVERY_COPPER_ARMOR_TRIM, "copper",
			OBTAIN_EVERY_IRON_ARMOR_TRIM, "iron",
			OBTAIN_EVERY_GOLDEN_ARMOR_TRIM, "golden",
			OBTAIN_EVERY_DIAMOND_ARMOR_TRIM, "diamond",
			OBTAIN_EVERY_NETHERITE_ARMOR_TRIM, "netherite"
		));
	}

}
