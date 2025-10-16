package me.sosedik.trappednewbie.impl.advancement;

import me.sosedik.packetadvancements.api.advancement.base.BaseAdvancementBuilder;
import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.PlayerKilledEntityTriggerData;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData;
import me.sosedik.utilizer.dataset.UtilizerTags;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.playerKilledEntity;

@NullMarked
public class KillAllPumpkinMobsAdvancement extends BaseAdvancement {

	public KillAllPumpkinMobsAdvancement(BaseAdvancementBuilder<?, ?> advancementBuilder) {
		super(advancementBuilder.requiredProgress(getProgress()));
	}

	private static RequiredAdvancementProgress getProgress() {
		List<List<String>> requirements = new ArrayList<>(UtilizerTags.HALLOWEEN_PUMPKIN_WEARERS.getValues().size());
		List<VanillaTriggerData<?>> triggerDatas = new ArrayList<>();
		for (EntityType type : UtilizerTags.HALLOWEEN_PUMPKIN_WEARERS.getValues()) {
			requirements.add(List.of(type.key().value()));
			triggerDatas.add(triggerData(type));
		}
		return RequiredAdvancementProgress.vanilla(requirements, triggerDatas);
	}

	private static PlayerKilledEntityTriggerData triggerData(EntityType type) {
		return playerKilledEntity(type.key().value())
				.withEntity(entity -> entity
					.withEntityType(type)
					.withEquipment(equipment -> equipment
						.withHelmet(Material.JACK_O_LANTERN)
					)
				);
	}

}
