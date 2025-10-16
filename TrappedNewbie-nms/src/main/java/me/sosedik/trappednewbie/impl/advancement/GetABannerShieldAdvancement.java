package me.sosedik.trappednewbie.impl.advancement;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancementBuilderImpl;
import me.sosedik.packetadvancements.imlp.progress.vanilla.conditions.ItemTriggerCondition;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@NullMarked
public class GetABannerShieldAdvancement extends BaseAdvancement {

	public GetABannerShieldAdvancement(BaseAdvancementBuilderImpl advancementBuilder) {
		super(advancementBuilder.requiredProgress(getProgress()));
	}

	private static RequiredAdvancementProgress getProgress() {
		List<List<String>> requirements = new ArrayList<>();
		List<VanillaTriggerData<?>> triggerDatas = new ArrayList<>();
		for (DyeColor dyeColor : DyeColor.values()) {
			requirements.add(List.of(dyeColor.name().toLowerCase(Locale.US)));
			triggerDatas.add(triggerData(dyeColor));
		}
		return RequiredAdvancementProgress.vanilla(requirements, triggerDatas);
	}

	private static VanillaTriggerData<?> triggerData(DyeColor dyeColor) {
		String name = dyeColor.name().toLowerCase(Locale.US);
		String components = """
			{
				"components": {
					"%s": "%s"
				}
			}
			""".formatted(DataComponentTypes.BASE_COLOR.key(), name);
		return VanillaTriggerData.inventoryChanged(name)
			.withItems(
				ItemTriggerCondition.of(Material.SHIELD)
					.withRawComponents(components)
			);
	}

}
