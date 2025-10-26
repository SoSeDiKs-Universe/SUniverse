package me.sosedik.trappednewbie.impl.advancement;

import me.sosedik.packetadvancements.api.advancement.base.BaseAdvancementBuilder;
import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.packetadvancements.imlp.progress.vanilla.conditions.ItemTriggerCondition;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData;
import me.sosedik.utilizer.dataset.UtilizerTags;
import org.bukkit.Material;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.inventoryChanged;

@NullMarked
public class GetAStackOfAllSmithingTemplatesAdvancement extends BaseAdvancement {

	public GetAStackOfAllSmithingTemplatesAdvancement(BaseAdvancementBuilder<?, ?> advancementBuilder) {
		super(advancementBuilder.requiredProgress(getProgress()));
	}

	private static RequiredAdvancementProgress getProgress() {
		List<List<String>> requirements = new ArrayList<>(UtilizerTags.SMITHING_TEMPLATES.getValues().size());
		List<VanillaTriggerData<?>> triggerDatas = new ArrayList<>();
		for (Material smithingType : UtilizerTags.SMITHING_TEMPLATES.getValues()) {
			requirements.add(List.of(smithingType.key().value()));
			triggerDatas.add(triggerData(smithingType));
		}
		return RequiredAdvancementProgress.vanilla(requirements, triggerDatas);
	}

	private static VanillaTriggerData<?> triggerData(Material smithingType) {
		return inventoryChanged(smithingType.key().value())
			.withItems(ItemTriggerCondition.of(smithingType).withMinAmount(smithingType.getMaxStackSize()));
	}

}
