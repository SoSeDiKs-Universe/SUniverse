package me.sosedik.trappednewbie.impl.advancement;

import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancementBuilderImpl;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.PlayerKilledEntityTriggerData;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@NullMarked
public class InspectorGadgetAdvancement extends BaseAdvancement {

	public InspectorGadgetAdvancement(BaseAdvancementBuilderImpl advancementBuilder) {
		super(advancementBuilder.requiredProgress(getProgress()));
	}

	private static RequiredAdvancementProgress getProgress() {
		Set<EntityType> zombieTypes = Tag.ENTITY_TYPES_ZOMBIES.getValues();
		List<VanillaTriggerData<?>> triggerDatas = new ArrayList<>(zombieTypes.size());
		zombieTypes.forEach(zombieType -> triggerDatas.add(playerKilledEntity(zombieType)));
		return RequiredAdvancementProgress.vanillaAny(triggerDatas);
	}

	private static PlayerKilledEntityTriggerData playerKilledEntity(EntityType entityType) {
		return VanillaTriggerData.playerKilledEntity(entityType.key().value())
				.withPlayer(player -> player.withEquipment(equipment -> equipment.withMainHand(Material.SPYGLASS)))
				.withEntity(entity ->
					entity.withEntityType(entityType)
						.withDistanceToPlayer(distance -> distance.maxAbsolute(5D))
				);
	}

}
