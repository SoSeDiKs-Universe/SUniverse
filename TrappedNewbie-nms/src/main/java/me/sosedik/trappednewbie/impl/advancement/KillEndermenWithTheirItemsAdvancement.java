package me.sosedik.trappednewbie.impl.advancement;

import me.sosedik.packetadvancements.api.advancement.base.BaseAdvancementBuilder;
import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.PlayerKilledEntityTriggerData;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.playerKilledEntity;

// MCCheck: 1.21.10, nbt tag
@NullMarked
public class KillEndermenWithTheirItemsAdvancement extends BaseAdvancement {

	public KillEndermenWithTheirItemsAdvancement(BaseAdvancementBuilder<?, ?> advancementBuilder) {
		super(advancementBuilder.requiredProgress(getProgress()));
	}

	private static RequiredAdvancementProgress getProgress() {
		List<List<String>> requirements = new ArrayList<>();
		List<VanillaTriggerData<?>> triggerDatas = new ArrayList<>();

		for (Material item : Tag.ENDERMAN_HOLDABLE.getValues()) {
			requirements.add(List.of(item.key().value()));
			triggerDatas.add(triggerData(item));
		}

		return RequiredAdvancementProgress.vanilla(requirements, triggerDatas);
	}

	private static PlayerKilledEntityTriggerData triggerData(Material item) {
		return playerKilledEntity(item.key().value())
				.withEntity(entity -> entity
					.withEntityType(EntityType.ENDERMAN)
					.withNbt("{carriedBlockState:{Name:\"%s\"}}".formatted(item.key()))
				);
	}

}
