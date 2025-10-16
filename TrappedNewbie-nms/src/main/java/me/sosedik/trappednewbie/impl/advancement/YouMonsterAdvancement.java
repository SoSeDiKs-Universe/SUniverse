package me.sosedik.trappednewbie.impl.advancement;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.sosedik.packetadvancements.api.advancement.base.BaseAdvancementBuilder;
import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.packetadvancements.imlp.progress.vanilla.conditions.EntityStateTriggerCondition;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData;
import net.kyori.adventure.key.Key;
import org.bukkit.DyeColor;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Llama;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.TraderLlama;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.playerKilledEntity;

// MCCheck: 1.21.8, new baby animals, entity nbt tags
@NullMarked
public class YouMonsterAdvancement extends BaseAdvancement {

	public YouMonsterAdvancement(BaseAdvancementBuilder<?, ?> advancementBuilder) {
		super(advancementBuilder.requiredProgress(getProgress()));
	}

	private static RequiredAdvancementProgress getProgress() {
		List<List<String>> requirements = new ArrayList<>();
		List<VanillaTriggerData<?>> triggerDatas = new ArrayList<>();

		for (EntityType type : List.of(
			EntityType.DONKEY, EntityType.MULE, EntityType.OCELOT, EntityType.HOGLIN,
			EntityType.TURTLE, EntityType.TADPOLE, EntityType.CAMEL, EntityType.SNIFFER,
			EntityType.POLAR_BEAR, EntityType.ARMADILLO, EntityType.SQUID, EntityType.GLOW_SQUID,
			EntityType.DOLPHIN, EntityType.HAPPY_GHAST
		)) {
			String criterion = type.key().value();
			requirements.add(List.of(criterion));
			triggerDatas.add(triggerData(type, criterion, null));
		}

		RegistryAccess.registryAccess().getRegistry(RegistryKey.CHICKEN_VARIANT).forEach(tag -> {
			Key key = tag.key();
			String criterion = Key.MINECRAFT_NAMESPACE.equals(key.namespace()) ? key.value() : key.namespace() + "_" + key.value();
			criterion = "chicken_" + criterion;
			requirements.add(List.of(criterion));
			triggerDatas.add(triggerData(EntityType.CHICKEN, criterion, "{variant:\"%s\"}".formatted(key)));
		});
		RegistryAccess.registryAccess().getRegistry(RegistryKey.PIG_VARIANT).forEach(tag -> {
			Key key = tag.key();
			String criterion = Key.MINECRAFT_NAMESPACE.equals(key.namespace()) ? key.value() : key.namespace() + "_" + key.value();
			criterion = "pig_" + criterion;
			requirements.add(List.of(criterion));
			triggerDatas.add(triggerData(EntityType.PIG, criterion, "{variant:\"%s\"}".formatted(key)));
		});
		RegistryAccess.registryAccess().getRegistry(RegistryKey.COW_VARIANT).forEach(tag -> {
			Key key = tag.key();
			String criterion = Key.MINECRAFT_NAMESPACE.equals(key.namespace()) ? key.value() : key.namespace() + "_" + key.value();
			criterion = "cow_" + criterion;
			requirements.add(List.of(criterion));
			triggerDatas.add(triggerData(EntityType.COW, criterion, "{variant:\"%s\"}".formatted(key)));
		});
		for (MushroomCow.Variant value : MushroomCow.Variant.values()) {
			String criterion = "mooshroom_" + value.name().toLowerCase(Locale.US);
			requirements.add(List.of(criterion));
			triggerDatas.add(triggerData(EntityType.MOOSHROOM, criterion, "{Type:\"%s\"}".formatted(value.name().toLowerCase(Locale.US))));
		}
		for (DyeColor dyeColor : DyeColor.values()) {
			String criterion = "sheep_" + dyeColor.name().toLowerCase(Locale.US);
			requirements.add(List.of(criterion));
			triggerDatas.add(triggerData(EntityType.SHEEP, criterion, "{Color:%sb}".formatted(dyeColor.getWoolData())));
		}
		for (int i = 0; i < Rabbit.Type.values().length; i++) {
			Rabbit.Type type = Rabbit.Type.values()[i];
			if (type == Rabbit.Type.THE_KILLER_BUNNY) continue;

			String criterion = "rabbit_" + type.name().toLowerCase(Locale.US);
			requirements.add(List.of(criterion));
			triggerDatas.add(triggerData(EntityType.RABBIT, criterion, "{RabbitType:%s}".formatted(i)));
		}
		for (int i = 0; i < Llama.Color.values().length; i++) {
			Llama.Color type = Llama.Color.values()[i];

			String criterion = "llama_" + type.name().toLowerCase(Locale.US);
			requirements.add(List.of(criterion));
			triggerDatas.add(triggerData(EntityType.LLAMA, criterion, "{Variant:%s}".formatted(i)));
		}
		for (int i = 0; i < TraderLlama.Color.values().length; i++) {
			TraderLlama.Color type = TraderLlama.Color.values()[i];

			String criterion = "trader_llama_" + type.name().toLowerCase(Locale.US);
			requirements.add(List.of(criterion));
			triggerDatas.add(triggerData(EntityType.TRADER_LLAMA, criterion, "{Variant:%s}".formatted(i)));
		}
		for (Panda.Gene type : Panda.Gene.values()) {
			String criterion = "panda_" + type.name().toLowerCase(Locale.US);
			requirements.add(List.of(criterion));
			triggerDatas.add(triggerData(EntityType.PANDA, criterion, "{MainGene:\"%s\"}".formatted(type.name().toLowerCase(Locale.US))));
		}
		for (Fox.Type type : Fox.Type.values()) {
			String criterion = "fox_" + type.name().toLowerCase(Locale.US);
			requirements.add(List.of(criterion));
			triggerDatas.add(triggerData(EntityType.FOX, criterion, "{Type:\"%s\"}".formatted(type.name().toLowerCase(Locale.US))));
		}
		for (int i = 0; i < Axolotl.Variant.values().length; i++) {
			Axolotl.Variant type = Axolotl.Variant.values()[i];

			String criterion = "axolotl_" + type.name().toLowerCase(Locale.US);
			requirements.add(List.of(criterion));
			triggerDatas.add(triggerData(EntityType.AXOLOTL, criterion, "{Variant:%s}".formatted(i)));
		}

		requirements.add(List.of("warm_strider"));
		triggerDatas.add(triggerData(EntityType.STRIDER, "warm_strider", "{OnGround:0b}"));
		requirements.add(List.of("cold_strider"));
		triggerDatas.add(triggerData(EntityType.STRIDER, "cold_strider", "{OnGround:1b}"));

		requirements.add(List.of("goat_screaming"));
		triggerDatas.add(triggerData(EntityType.GOAT, "goat_screaming", "{IsScreamingGoat:1b}"));
		requirements.add(List.of("goat_not_screaming"));
		triggerDatas.add(triggerData(EntityType.GOAT, "goat_not_screaming", "{IsScreamingGoat:0b}"));

		requirements.add(List.of("bee_sting_no_pollen_calm"));
		triggerDatas.add(triggerData(EntityType.BEE, "bee_sting_no_pollen", "{HasStung:0b,HasNectar:0b,BukkitValues:{angry:0b}}"));
		requirements.add(List.of("bee_sting_no_pollen_angry"));
		triggerDatas.add(triggerData(EntityType.BEE, "bee_sting_no_pollen", "{HasStung:0b,HasNectar:0b,BukkitValues:{angry:1b}}"));
		requirements.add(List.of("bee_sting_pollen_calm"));
		triggerDatas.add(triggerData(EntityType.BEE, "bee_sting_pollen", "{HasStung:0b,HasNectar:1b,BukkitValues:{angry:0b}}"));
		requirements.add(List.of("bee_sting_pollen_angry"));
		triggerDatas.add(triggerData(EntityType.BEE, "bee_sting_pollen", "{HasStung:0b,HasNectar:1b,BukkitValues:{angry:1b}}"));
		requirements.add(List.of("bee_no_sting_no_pollen_calm"));
		triggerDatas.add(triggerData(EntityType.BEE, "bee_no_sting_no_pollen", "{HasStung:1b,HasNectar:0b,BukkitValues:{angry:0b}}"));
		requirements.add(List.of("bee_no_sting_no_pollen_angry"));
		triggerDatas.add(triggerData(EntityType.BEE, "bee_no_sting_no_pollen", "{HasStung:1b,HasNectar:0b,BukkitValues:{angry:1b}}"));
		requirements.add(List.of("bee_no_sting_pollen_calm"));
		triggerDatas.add(triggerData(EntityType.BEE, "bee_no_sting_pollen", "{HasStung:1b,HasNectar:1b,BukkitValues:{angry:0b}}"));
		requirements.add(List.of("bee_no_sting_pollen_angry"));
		triggerDatas.add(triggerData(EntityType.BEE, "bee_no_sting_pollen", "{HasStung:1b,HasNectar:1b,BukkitValues:{angry:1b}}"));

		for (int i = 0; i < Horse.Style.values().length; i++) {
			Horse.Style style = Horse.Style.values()[i];
			for (int j = 0; j < Horse.Color.values().length; j++) {
				Horse.Color color = Horse.Color.values()[j];
				String criterion = "horse_" + style.name().toLowerCase(Locale.US) + "_" + color.name().toLowerCase(Locale.US);
				requirements.add(List.of(criterion));
				int variant = j & 0xFF | i << 8 & 0xFF00;
				triggerDatas.add(triggerData(EntityType.HORSE, criterion, "{Variant:%s}".formatted(variant)));
			}
		}

		RegistryAccess.registryAccess().getRegistry(RegistryKey.WOLF_VARIANT).forEach(tag -> {
			Key key = tag.key();

			String criterion = Key.MINECRAFT_NAMESPACE.equals(key.namespace()) ? key.value() : key.namespace() + "_" + key.value();
			criterion = "wolf_" + criterion + "_wild_calm";
			requirements.add(List.of(criterion));
			triggerDatas.add(triggerData(EntityType.WOLF, criterion, "{variant:\"%s\",BukkitValues:{angry:0b}}".formatted(key.toString())));
			criterion = Key.MINECRAFT_NAMESPACE.equals(key.namespace()) ? key.value() : key.namespace() + "_" + key.value();
			criterion = "wolf_" + criterion + "_wild_angry";
			requirements.add(List.of(criterion));
			triggerDatas.add(triggerData(EntityType.WOLF, criterion, "{variant:\"%s\",BukkitValues:{angry:1b}}".formatted(key.toString())));

			for (int i = 0; i < DyeColor.values().length; i++) {
				DyeColor dyeColor = DyeColor.values()[i];
				criterion = Key.MINECRAFT_NAMESPACE.equals(key.namespace()) ? key.value() : key.namespace() + "_" + key.value();
				criterion = "wolf_" + criterion + "_" + dyeColor.name().toLowerCase(Locale.US);
				requirements.add(List.of(criterion));
				triggerDatas.add(triggerData(EntityType.WOLF, criterion, "{CollarColor:%sb,variant:\"%s\"}".formatted(i, key.toString())));
			}
		});

		RegistryAccess.registryAccess().getRegistry(RegistryKey.CAT_VARIANT).forEach(tag -> {
			Key key = tag.key();
			for (int i = 0; i < DyeColor.values().length; i++) {
				DyeColor dyeColor = DyeColor.values()[i];
				String criterion = Key.MINECRAFT_NAMESPACE.equals(key.namespace()) ? key.value() : key.namespace() + "_" + key.value();
				criterion = "cat_" + criterion + "_" + dyeColor.name().toLowerCase(Locale.US);
				requirements.add(List.of(criterion));
				triggerDatas.add(triggerData(EntityType.CAT, criterion, "{CollarColor:%sb,variant:\"%s\"}".formatted(i, key.toString())));
			}
		});

		return RequiredAdvancementProgress.vanilla(requirements, triggerDatas);
	}

	private static VanillaTriggerData<?> triggerData(EntityType type, String criterion, @Nullable String nbt) {
		return playerKilledEntity(criterion)
				.withEntity(entity -> entity
					.withEntityType(type)
					.withState(EntityStateTriggerCondition::baby)
					.withNbt(nbt)
				);
	}

}
