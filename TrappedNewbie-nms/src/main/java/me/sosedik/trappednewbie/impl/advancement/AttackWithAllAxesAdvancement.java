package me.sosedik.trappednewbie.impl.advancement;

import io.papermc.paper.registry.keys.tags.DamageTypeTagKeys;
import me.sosedik.packetadvancements.api.advancement.base.BaseAdvancementBuilder;
import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.playerHurtEntity;

@NullMarked
public class AttackWithAllAxesAdvancement extends BaseAdvancement {

	public AttackWithAllAxesAdvancement(BaseAdvancementBuilder<?, ?> advancementBuilder) {
		super(advancementBuilder.requiredProgress(getProgress()));
	}

	private static RequiredAdvancementProgress getProgress() {
		List<List<String>> requirements = new ArrayList<>(Tag.ITEMS_AXES.getValues().size());
		List<VanillaTriggerData<?>> triggerDatas = new ArrayList<>();
		for (Material axeType : Tag.ITEMS_AXES.getValues()) {
			requirements.add(List.of(axeType.key().value()));
			triggerDatas.add(triggerData(axeType));
		}
		return RequiredAdvancementProgress.vanilla(requirements, triggerDatas);
	}

	private static VanillaTriggerData<?> triggerData(Material axeType) {
		return playerHurtEntity(axeType.key().value())
				.withDamage(damage -> damage
					.withDamageSource(source -> source
						.withTag(DamageTypeTagKeys.IS_PLAYER_ATTACK, true)
						.withSourceEntity(entity -> entity
							.withEquipment(equipment -> equipment
								.withMainHand(axeType)
							)
						)
					)
				);
	}

}
