package me.sosedik.trappednewbie.impl.advancement;

import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancementBuilderImpl;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.PlayerKilledEntityTriggerData;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData;
import me.sosedik.trappednewbie.dataset.TrappedNewbieTags;
import me.sosedik.utilizer.dataset.UtilizerTags;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

// MCCheck: 1.21.7, new zombie types
@NullMarked
public class RockPaperShearsAdvancement extends BaseAdvancement {

	private static final String PAPER_BEATS_ROCK = "paper_beats_rock";
	private static final String ROCK_BEATS_SHEARS = "rock_beats_shears";
	private static final String SHEARS_BEATS_PAPER = "shears_beats_paper";

	public RockPaperShearsAdvancement(BaseAdvancementBuilderImpl advancementBuilder) {
		super(advancementBuilder.requiredProgress(getProgress()));
	}

	private static RequiredAdvancementProgress getProgress() {
		String zombie = EntityType.ZOMBIE.key().value();
		String zombieVillager = EntityType.ZOMBIE_VILLAGER.key().value();
		String drowned = EntityType.DROWNED.key().value();
		String husk = EntityType.HUSK.key().value();
		List<List<String>> requirements = List.of(
			List.of(PAPER_BEATS_ROCK + "_" + zombie, PAPER_BEATS_ROCK + "_" + zombieVillager, PAPER_BEATS_ROCK + "_" + drowned, PAPER_BEATS_ROCK + "_" + husk),
			List.of(ROCK_BEATS_SHEARS + "_" + zombie, ROCK_BEATS_SHEARS + "_" + zombieVillager, ROCK_BEATS_SHEARS + "_" + drowned, ROCK_BEATS_SHEARS + "_" + husk),
			List.of(SHEARS_BEATS_PAPER + "_" + zombie, SHEARS_BEATS_PAPER + "_" + zombieVillager, SHEARS_BEATS_PAPER + "_" + drowned, SHEARS_BEATS_PAPER + "_" + husk)
		);
		Material[] shearsArray = UtilizerTags.SHEARS.getValues().toArray(new Material[0]);
		List<Material> rocks = new ArrayList<>();
		rocks.addAll(TrappedNewbieTags.ROCKS.getValues());
		rocks.addAll(List.of(Material.COBBLESTONE, Material.STONE, Material.ANDESITE, Material.DIORITE, Material.GRANITE));
		Material[] rocksArray = rocks.toArray(new Material[0]);
		Material[] papersArray = new Material[]{Material.PAPER, Material.MAP, Material.FILLED_MAP};
		return RequiredAdvancementProgress.vanilla(requirements,
			playerKilledEntity(PAPER_BEATS_ROCK, EntityType.ZOMBIE, papersArray, rocksArray),
			playerKilledEntity(PAPER_BEATS_ROCK, EntityType.ZOMBIE_VILLAGER, papersArray, rocksArray),
			playerKilledEntity(PAPER_BEATS_ROCK, EntityType.DROWNED, papersArray, rocksArray),
			playerKilledEntity(PAPER_BEATS_ROCK, EntityType.HUSK, papersArray, rocksArray),
			playerKilledEntity(ROCK_BEATS_SHEARS, EntityType.ZOMBIE, rocksArray, shearsArray),
			playerKilledEntity(ROCK_BEATS_SHEARS, EntityType.ZOMBIE_VILLAGER, rocksArray, shearsArray),
			playerKilledEntity(ROCK_BEATS_SHEARS, EntityType.DROWNED, rocksArray, shearsArray),
			playerKilledEntity(ROCK_BEATS_SHEARS, EntityType.HUSK, rocksArray, shearsArray),
			playerKilledEntity(SHEARS_BEATS_PAPER, EntityType.ZOMBIE, shearsArray, papersArray),
			playerKilledEntity(SHEARS_BEATS_PAPER, EntityType.ZOMBIE_VILLAGER, shearsArray, papersArray),
			playerKilledEntity(SHEARS_BEATS_PAPER, EntityType.DROWNED, shearsArray, papersArray),
			playerKilledEntity(SHEARS_BEATS_PAPER, EntityType.HUSK, shearsArray, papersArray)
		);
	}

	private static PlayerKilledEntityTriggerData playerKilledEntity(String criterion, EntityType entityType, Material[] playerItems, Material[] entityItems) {
		return VanillaTriggerData.playerKilledEntity(criterion + "_" + entityType.key().value())
			.withPlayer(player -> player.withEquipment(equipment -> equipment.withMainHand(playerItems)))
			.withEntity(entity ->
				entity.withEntityType(entityType)
					.withEquipment(equipment -> equipment.withMainHand(entityItems))
					.withDistanceToPlayer(distance -> distance.maxAbsolute(5D))
			);
	}

}
