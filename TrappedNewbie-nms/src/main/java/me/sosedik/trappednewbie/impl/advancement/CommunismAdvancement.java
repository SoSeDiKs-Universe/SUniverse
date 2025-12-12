package me.sosedik.trappednewbie.impl.advancement;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.sosedik.packetadvancements.api.advancement.base.BaseAdvancementBuilder;
import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.packetadvancements.imlp.progress.vanilla.conditions.ItemTriggerCondition;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.PlayerUseItemOnEntityTriggerData;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData;
import me.sosedik.utilizer.dataset.UtilizerTags;
import me.sosedik.utilizer.util.RomanNumerals;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Salmon;
import org.bukkit.entity.TropicalFish;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.thrownItemPickedUpByEntity;

// MCCheck: 1.21.11, new unobtainable items, new suspicious stew effects, new special items
@NullMarked
public class CommunismAdvancement extends BaseAdvancement {

	public CommunismAdvancement(BaseAdvancementBuilder<?, ?> advancementBuilder) {
		super(advancementBuilder.requiredProgress(getProgress()));
	}

	private static RequiredAdvancementProgress getProgress() {
		List<List<String>> requirements = new ArrayList<>();
		List<VanillaTriggerData<?>> triggerDatas = new ArrayList<>();
		for (Material type : Material.values()) {
			if (type.isLegacy()) continue;
			if (!type.isItem()) continue;
			if (isUnobtainable(type)) continue;
			if (!NamespacedKey.MINECRAFT.equals(type.key().namespace())) continue;

			if (handleSpecialItem(requirements, triggerDatas, type))
				continue;

			String key = type.key().value();
			requirements.add(List.of(key));
			triggerDatas.add(triggerData(key, type, null));
		}
		return RequiredAdvancementProgress.vanilla(requirements, triggerDatas);
	}

	private static boolean handleSpecialItem(List<List<String>> requirements, List<VanillaTriggerData<?>> triggerDatas, Material type) {
		if (type == Material.SALMON_BUCKET) {
			for (Salmon.Variant v : Salmon.Variant.values()) {
				String variant = v.name().toLowerCase(Locale.US);
				String key = variant + "_" + type.key().value();
				requirements.add(List.of(key));
				triggerDatas.add(triggerData(key, type, """
					{
						"components": {
							"%s": "%s"
						}
					}
					""".formatted(DataComponentTypes.SALMON_SIZE.key(), variant)
				));
			}
			return true;
		}
		if (type == Material.TROPICAL_FISH_BUCKET) {
			for (TropicalFishBucketData data : new TropicalFishBucketData[]{
				new TropicalFishBucketData("anemone", TropicalFish.Pattern.STRIPEY, DyeColor.ORANGE, DyeColor.GRAY),
				new TropicalFishBucketData("black_tang", TropicalFish.Pattern.FLOPPER, DyeColor.GRAY, DyeColor.GRAY),
				new TropicalFishBucketData("blue_tang", TropicalFish.Pattern.FLOPPER, DyeColor.GRAY, DyeColor.BLUE),
				new TropicalFishBucketData("butterflyfish", TropicalFish.Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.GRAY),
				new TropicalFishBucketData("cichlid", TropicalFish.Pattern.SUNSTREAK, DyeColor.BLUE, DyeColor.GRAY),
				new TropicalFishBucketData("clownfish", TropicalFish.Pattern.KOB, DyeColor.ORANGE, DyeColor.WHITE),
				new TropicalFishBucketData("cotton_candy_betta", TropicalFish.Pattern.SPOTTY, DyeColor.PINK, DyeColor.LIGHT_BLUE),
				new TropicalFishBucketData("dottyback", TropicalFish.Pattern.BLOCKFISH, DyeColor.PURPLE, DyeColor.YELLOW),
				new TropicalFishBucketData("emperor_red_snapper", TropicalFish.Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.RED),
				new TropicalFishBucketData("goatfish", TropicalFish.Pattern.SPOTTY, DyeColor.WHITE, DyeColor.YELLOW),
				new TropicalFishBucketData("moorish_idol", TropicalFish.Pattern.GLITTER, DyeColor.WHITE, DyeColor.GRAY),
				new TropicalFishBucketData("ornate_butterflyfish", TropicalFish.Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.ORANGE),
				new TropicalFishBucketData("parrotfish", TropicalFish.Pattern.DASHER, DyeColor.CYAN, DyeColor.PINK),
				new TropicalFishBucketData("queen_angelfish", TropicalFish.Pattern.BRINELY, DyeColor.LIME, DyeColor.LIGHT_BLUE),
				new TropicalFishBucketData("red_cichlid", TropicalFish.Pattern.BETTY, DyeColor.RED, DyeColor.WHITE),
				new TropicalFishBucketData("red_lipped_blenny", TropicalFish.Pattern.SNOOPER, DyeColor.GRAY, DyeColor.RED),
				new TropicalFishBucketData("red_snapper", TropicalFish.Pattern.BLOCKFISH, DyeColor.RED, DyeColor.WHITE),
				new TropicalFishBucketData("threadfin", TropicalFish.Pattern.FLOPPER, DyeColor.WHITE, DyeColor.YELLOW),
				new TropicalFishBucketData("tomato_clownfish", TropicalFish.Pattern.KOB, DyeColor.RED, DyeColor.WHITE),
				new TropicalFishBucketData("triggerfish", TropicalFish.Pattern.SUNSTREAK, DyeColor.GRAY, DyeColor.WHITE),
				new TropicalFishBucketData("yellow_parrotfish", TropicalFish.Pattern.DASHER, DyeColor.CYAN, DyeColor.YELLOW),
				new TropicalFishBucketData("yellow_tang", TropicalFish.Pattern.FLOPPER, DyeColor.YELLOW, DyeColor.YELLOW),
			}) {
				String key = data.name() + "_" + type.key().value();
				requirements.add(List.of(key));
				triggerDatas.add(triggerData(key, type, """
					{
						"components": {
							"%s": "%s",
							"%s": "%s",
							"%s": "%s"
						}
					}
					""".formatted(
						DataComponentTypes.TROPICAL_FISH_PATTERN.key(),
						data.pattern().name().toLowerCase(Locale.US),
						DataComponentTypes.TROPICAL_FISH_BASE_COLOR.key(),
						data.baseColor().name().toLowerCase(Locale.US),
						DataComponentTypes.TROPICAL_FISH_PATTERN_COLOR.key(),
						data.patternColor().name().toLowerCase(Locale.US)
					)
				));
			}
			return true;
		}
		if (type == Material.SUSPICIOUS_STEW) {
			List<List<SuspiciousStewData>> stews = List.of(
				List.of(new SuspiciousStewData("", PotionEffectType.FIRE_RESISTANCE, 60)),
				List.of(new SuspiciousStewData("crafted_", PotionEffectType.BLINDNESS, 220)),
				List.of(new SuspiciousStewData("villager_", PotionEffectType.BLINDNESS, 120)),
				new ArrayList<>(),
				List.of(new SuspiciousStewData("crafted_villager_", PotionEffectType.SATURATION, 7)),
				new ArrayList<>(),
				List.of(new SuspiciousStewData("", PotionEffectType.NAUSEA, 140)),
				List.of(new SuspiciousStewData("crafted_", PotionEffectType.JUMP_BOOST, 100)),
				List.of(new SuspiciousStewData("villager_", PotionEffectType.JUMP_BOOST, 160)),
				new ArrayList<>(),
				List.of(new SuspiciousStewData("crafted_", PotionEffectType.POISON, 220)),
				List.of(new SuspiciousStewData("villager_", PotionEffectType.POISON, 280)),
				new ArrayList<>(),
				List.of(new SuspiciousStewData("", PotionEffectType.REGENERATION, 140)),
				List.of(new SuspiciousStewData("crafted_villager_", PotionEffectType.NIGHT_VISION, 100)),
				new ArrayList<>(),
				List.of(new SuspiciousStewData("crafted_villager_", PotionEffectType.WEAKNESS, 140)),
				new ArrayList<>(),
				List.of(new SuspiciousStewData("", PotionEffectType.WITHER, 140))
			);
			for (int i = 100; i < 120; i++) stews.get(3).add(new SuspiciousStewData("loot_" + i + "ticks_", PotionEffectType.BLINDNESS, i));
			// 120 clashes with a crafted/traded one
			for (int i = 121; i < 141; i++) stews.get(3).add(new SuspiciousStewData("loot_" + i + "ticks_", PotionEffectType.BLINDNESS, i));
			for (int i = 8; i < 11; i++) stews.get(5).add(new SuspiciousStewData("loot_" + i + "ticks_", PotionEffectType.SATURATION, i)); // 7 clashes
			for (int i = 140; i < 160; i++) stews.get(9).add(new SuspiciousStewData("loot_" + i + "ticks_", PotionEffectType.JUMP_BOOST, i));
			// 160 clashes
			for (int i = 161; i < 201; i++) stews.get(9).add(new SuspiciousStewData("loot_" + i + "ticks_", PotionEffectType.JUMP_BOOST, i));
			for (int i = 200; i < 220; i++) stews.get(12).add(new SuspiciousStewData("loot_" + i + "ticks_", PotionEffectType.POISON, i));
			// 220 clashes
			for (int i = 221; i < 280; i++) stews.get(12).add(new SuspiciousStewData("loot_" + i + "ticks_", PotionEffectType.POISON, i));
			// 280 clashes
			for (int i = 281; i < 401; i++) stews.get(12).add(new SuspiciousStewData("loot_" + i + "ticks_", PotionEffectType.POISON, i));
			for (int i = 140; i < 201; i++) stews.get(15).add(new SuspiciousStewData("loot_" + i + "ticks_", PotionEffectType.NIGHT_VISION, i));
			for (int i = 120; i < 140; i++) stews.get(17).add(new SuspiciousStewData("loot_" + i + "ticks_", PotionEffectType.WEAKNESS, i));
			// 140 clashes
			for (int i = 141; i < 161; i++) stews.get(17).add(new SuspiciousStewData("loot_" + i + "ticks_", PotionEffectType.WEAKNESS, i));
			for (List<SuspiciousStewData> datas : stews) {
				List<String> keys = new ArrayList<>();
				for (SuspiciousStewData data : datas) {
					String key = data.prefix() + type.key().value() + "_" + data.potionEffectType().key().value();
					keys.add(key);
					triggerDatas.add(triggerData(key, type, """
						{
							"components": {
								"%s": [
									{
										"id": "%s",
										"duration": %s
									}
								]
							}
						}
						""".formatted(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS.key(), data.potionEffectType().key(), data.duration())
					));
				}
				requirements.add(keys);
			}
			return true;
		}
		if (type == Material.POTION || type == Material.SPLASH_POTION || type == Material.LINGERING_POTION || type == Material.TIPPED_ARROW) {
			for (PotionType potionType : PotionType.values()) {
				if (potionType == PotionType.LUCK) continue;

				String variant = potionType.key().value();
				String key = variant + "_" + type.key().value();
				requirements.add(List.of(key));
				triggerDatas.add(triggerData(key, type, """
					{
						"components": {
							"%s": {
								"potion": "%s"
							}
						}
					}
					""".formatted(DataComponentTypes.POTION_CONTENTS.key(), potionType.key())
				));
			}
			return true;
		}
		if (type == Material.AXOLOTL_BUCKET) {
			for (Axolotl.Variant v : Axolotl.Variant.values()) {
				String variant = v.name().toLowerCase(Locale.US);
				String key = variant + "_" + type.key().value();
				requirements.add(List.of(key));
				triggerDatas.add(triggerData(key, type, """
					{
						"components": {
							"%s": "%s"
						}
					}
					""".formatted(DataComponentTypes.AXOLOTL_VARIANT.key(), variant)
				));
			}
			return true;
		}
		if (type == Material.GOAT_HORN) {
			Registry<MusicInstrument> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.INSTRUMENT);
			registry.forEach(instrument -> {
				NamespacedKey key1 = registry.getKey(instrument);
				if (key1 == null) return;
				if (!NamespacedKey.MINECRAFT.equals(key1.namespace())) return;

				String key = key1.value().replace("_goat_horn", "") + "_" + type.key().value();
				requirements.add(List.of(key));
				triggerDatas.add(triggerData(key, type, """
					{
						"components": {
							"%s": %s
						}
					}
					""".formatted(DataComponentTypes.INSTRUMENT.key(), key1.value())
				));
			});
			return true;
		}
		if (type == Material.OMINOUS_BOTTLE) {
			for (int i = 0; i < 5; i++) {
				String key = type.key().value() + "_" + RomanNumerals.toRoman(i + 1).toLowerCase(Locale.US);
				requirements.add(List.of(key));
				triggerDatas.add(triggerData(key, type, """
					{
						"components": {
							"%s": %s
						}
					}
					""".formatted(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER.key(), i)
				));
			}
			return true;
		}
		return false;
	}

	private static boolean isUnobtainable(Material type) {
		if (UtilizerTags.SPAWN_EGGS.isTagged(type)) return true;
		return type == Material.AIR
			|| type == Material.GLOW_INK_SAC // Zombies are prohibited from picking them up specifically ¯\_(ツ)_/¯
			|| type == Material.PLAYER_HEAD
			|| type == Material.REINFORCED_DEEPSLATE
			|| type == Material.DIRT_PATH
			|| type == Material.KNOWLEDGE_BOOK
			|| type == Material.DEBUG_STICK
			|| type == Material.BEDROCK
			|| type == Material.BUDDING_AMETHYST
			|| type == Material.CHORUS_PLANT
			|| type == Material.END_PORTAL_FRAME
			|| type == Material.FARMLAND
			|| type == Material.FROGSPAWN
			|| type == Material.INFESTED_CHISELED_STONE_BRICKS
			|| type == Material.INFESTED_COBBLESTONE
			|| type == Material.INFESTED_CRACKED_STONE_BRICKS
			|| type == Material.INFESTED_DEEPSLATE
			|| type == Material.INFESTED_STONE
			|| type == Material.INFESTED_MOSSY_STONE_BRICKS
			|| type == Material.INFESTED_STONE_BRICKS
			|| type == Material.SPAWNER
			|| type == Material.TRIAL_SPAWNER
			|| type == Material.VAULT
			|| type == Material.BARRIER
			|| type == Material.COMMAND_BLOCK
			|| type == Material.CHAIN_COMMAND_BLOCK
			|| type == Material.REPEATING_COMMAND_BLOCK
			|| type == Material.COMMAND_BLOCK_MINECART
			|| type == Material.JIGSAW
			|| type == Material.LIGHT
			|| type == Material.PETRIFIED_OAK_SLAB
			|| type == Material.STRUCTURE_BLOCK
			|| type == Material.STRUCTURE_VOID
			|| type == Material.TEST_BLOCK
			|| type == Material.TEST_INSTANCE_BLOCK;
	}

	private static PlayerUseItemOnEntityTriggerData triggerData(String key, Material type, @Nullable String nbt) {
		return thrownItemPickedUpByEntity(key)
				.withEntity(entity -> entity.withEntityType(UtilizerTags.HUMAN_LIKE_ZOMBIES))
				.withItem(ItemTriggerCondition.of(type).withRawComponents(nbt));
	}

	private record TropicalFishBucketData(String name, TropicalFish.Pattern pattern, DyeColor baseColor, DyeColor patternColor) {}

	private record SuspiciousStewData(String prefix, PotionEffectType potionEffectType, int duration) {}

}
