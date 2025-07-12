package me.sosedik.trappednewbie.impl.advancement;

import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancementBuilderImpl;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.PlayerKilledEntityTriggerData;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData;
import me.sosedik.trappednewbie.dataset.TrappedNewbieTags;
import me.sosedik.utilizer.dataset.UtilizerTags;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.jspecify.annotations.NullMarked;

import java.util.List;

// MCCheck: 1.21.7, new stone/rock types, new paper types
@NullMarked
public class RockPaperShearsAdvancement extends BaseAdvancement {

	private static final String PAPER_BEATS_ROCK = "paper_beats_rock";
	private static final String ROCK_BEATS_SHEARS = "rock_beats_shears";
	private static final String SHEARS_BEATS_PAPER = "shears_beats_paper";

	public RockPaperShearsAdvancement(BaseAdvancementBuilderImpl advancementBuilder) {
		super(advancementBuilder.requiredProgress(getProgress()));
	}

	private static RequiredAdvancementProgress getProgress() {
		// There are also [stone] slabs and other variants, but really, the player should've known better smh
		Material[] rocksArray = TrappedNewbieTags.ROCKY.getValues().toArray(new Material[0]);
		Material[] papersArray = TrappedNewbieTags.PAPERS.getValues().toArray(new Material[0]);
		Material[] shearsArray = UtilizerTags.SHEARS.getValues().toArray(new Material[0]);

		return RequiredAdvancementProgress.vanilla(
			List.of(
				List.of(PAPER_BEATS_ROCK),
				List.of(ROCK_BEATS_SHEARS),
				List.of(SHEARS_BEATS_PAPER)
			),
			List.of(
				playerKilledEntity(PAPER_BEATS_ROCK, papersArray, rocksArray),
				playerKilledEntity(ROCK_BEATS_SHEARS, rocksArray, shearsArray),
				playerKilledEntity(SHEARS_BEATS_PAPER, shearsArray, papersArray)
			)
		);
	}

	private static PlayerKilledEntityTriggerData playerKilledEntity(String criterion, Material[] playerItems, Material[] entityItems) {
		return VanillaTriggerData.playerKilledEntity(criterion)
			.withPlayer(player -> player.withEquipment(equipment -> equipment.withMainHand(playerItems)))
			.withEntity(entity ->
				entity.withEntityType(Tag.ENTITY_TYPES_ZOMBIES)
					.withEquipment(equipment -> equipment.withMainHand(entityItems))
					.withDistanceToPlayer(distance -> distance.maxAbsolute(5D))
			);
	}

}
