package me.sosedik.trappednewbie.impl.advancement;

import io.papermc.paper.registry.keys.tags.DamageTypeTagKeys;
import me.sosedik.packetadvancements.api.advancement.IAdvancement;
import me.sosedik.packetadvancements.api.advancement.base.BaseAdvancementBuilder;
import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.api.reward.AdvancementReward;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData;
import me.sosedik.trappednewbie.api.advancement.reward.FancyAdvancementReward;
import me.sosedik.utilizer.util.InventoryUtil;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.playerHurtEntity;

@NullMarked
public class AttackWithAnEggAdvancement extends BaseAdvancement {

	public AttackWithAnEggAdvancement(BaseAdvancementBuilder<?, ?> advancementBuilder) {
		super(advancementBuilder.withReward(getReward()).requiredProgress(getProgress()));
	}

	private static AdvancementReward getReward() {
		return new FancyAdvancementReward().withExtraAction(action -> {
			Player completer = action.completer();
			if (completer == null) return;

			IAdvancement advancement = action.advancement();
			for (Material type : Tag.ITEMS_EGGS.getValues()) {
				if (!advancement.hasCriteria(completer, type.key().value())) continue;

				InventoryUtil.addOrDrop(completer, ItemStack.of(type, 4), false);
				return;
			}
		});
	}

	private static RequiredAdvancementProgress getProgress() {
		List<VanillaTriggerData<?>> triggerDatas = new ArrayList<>();
		Tag.ITEMS_EGGS.getValues().forEach(eggType -> triggerDatas.add(triggerData(eggType)));
		return RequiredAdvancementProgress.vanillaAny(triggerDatas);
	}

	private static VanillaTriggerData<?> triggerData(Material eggType) {
		return playerHurtEntity(eggType.key().value())
			.withDamage(damage -> damage
				.withDamageSource(source -> source
					.withTag(DamageTypeTagKeys.IS_PROJECTILE, true)
					.withDirectEntity(direct -> direct
						.withEntityType(EntityType.EGG)
						.withNbt("{Item:{id:\"%s\"}}".formatted(eggType.key()))
					)
				)
			);
	}

}
