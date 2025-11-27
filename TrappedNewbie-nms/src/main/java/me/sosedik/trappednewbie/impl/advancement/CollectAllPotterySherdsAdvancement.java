package me.sosedik.trappednewbie.impl.advancement;

import me.sosedik.packetadvancements.api.advancement.base.BaseAdvancementBuilder;
import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.packetadvancements.imlp.progress.vanilla.conditions.ItemTriggerCondition;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.inventoryChanged;

@NullMarked
public class CollectAllPotterySherdsAdvancement extends BaseAdvancement {

	public CollectAllPotterySherdsAdvancement(BaseAdvancementBuilder<?, ?> advancementBuilder) {
		super(advancementBuilder.requiredProgress(getProgress()));
	}

	private static RequiredAdvancementProgress getProgress() {
		List<List<String>> requirements = new ArrayList<>(Tag.ITEMS_DECORATED_POT_SHERDS.getValues().size());
		List<VanillaTriggerData<?>> triggerDatas = new ArrayList<>();
		for (Material sherdType : Tag.ITEMS_DECORATED_POT_SHERDS.getValues()) {
			requirements.add(List.of(sherdType.key().value()));
			triggerDatas.add(triggerData(sherdType));
		}
		return RequiredAdvancementProgress.vanilla(requirements, triggerDatas);
	}

	private static VanillaTriggerData<?> triggerData(Material sherdType) {
		return inventoryChanged(sherdType.key().value())
				.withItems(ItemTriggerCondition.of(sherdType));
	}

}
