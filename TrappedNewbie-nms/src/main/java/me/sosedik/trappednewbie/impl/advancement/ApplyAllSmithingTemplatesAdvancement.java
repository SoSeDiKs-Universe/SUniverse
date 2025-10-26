package me.sosedik.trappednewbie.impl.advancement;

import me.sosedik.packetadvancements.api.advancement.base.BaseAdvancementBuilder;
import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData;
import me.sosedik.utilizer.dataset.UtilizerTags;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.recipeCrafted;

@NullMarked
public class ApplyAllSmithingTemplatesAdvancement extends BaseAdvancement {

	public ApplyAllSmithingTemplatesAdvancement(BaseAdvancementBuilder<?, ?> advancementBuilder) {
		super(advancementBuilder.requiredProgress(getProgress()));
	}

	private static RequiredAdvancementProgress getProgress() {
		List<List<String>> requirements = new ArrayList<>(UtilizerTags.SMITHING_TEMPLATES.getValues().size() - 1);
		List<VanillaTriggerData<?>> triggerDatas = new ArrayList<>();
		for (Material trimType : UtilizerTags.SMITHING_TEMPLATES.getValues()) {
			if (trimType == Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE) continue;

			requirements.add(List.of(trimType.key().value()));
			triggerDatas.add(triggerData(trimType));
		}
		return RequiredAdvancementProgress.vanilla(requirements, triggerDatas);
	}

	private static VanillaTriggerData<?> triggerData(Material trimType) {
		NamespacedKey recipeKey = new NamespacedKey(trimType.key().namespace(), trimType.key().value() + "_smithing_trim");
		if (Bukkit.getRecipe(recipeKey) == null)
			throw new RuntimeException("Couldn't find trim recipe: " + recipeKey + ", required for " + trimType.key());
		return recipeCrafted(trimType.key().value(), recipeKey);
	}

}
