package me.sosedik.requiem.listener.entity;

import de.tr7zw.nbtapi.NBT;
import me.sosedik.requiem.api.event.player.PlayerStartPossessingEntityEvent;
import me.sosedik.requiem.api.event.player.PlayerStopPossessingEntityEvent;
import org.bukkit.entity.Panda;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * Overwrite pandas' genes when controlling them
 */
public class OverwriteControlledPandasGenes implements Listener {

	private static final String MAIN_GENE_KEY = "main_gene";
	private static final String HIDDEN_GENE_KEY = "hidden_gene";

	@EventHandler
	public void onPossess(@NotNull PlayerStartPossessingEntityEvent event) {
		if (!(event.getEntity() instanceof Panda entity)) return;

		NBT.modifyPersistentData(entity, nbt -> {
			Panda.Gene gene = entity.getMainGene();
			if (!shouldBeKept(gene)) {
				nbt.setEnum(MAIN_GENE_KEY, gene);
				entity.setMainGene(Panda.Gene.NORMAL);
			}
			gene = entity.getHiddenGene();
			if (!shouldBeKept(gene)) {
				nbt.setEnum(HIDDEN_GENE_KEY, gene);
				entity.setHiddenGene(Panda.Gene.NORMAL);
			}
		});
	}

	private boolean shouldBeKept(@NotNull Panda.Gene gene) {
		return gene == Panda.Gene.BROWN;
	}

	@EventHandler
	public void onUnPossess(@NotNull PlayerStopPossessingEntityEvent event) {
		if (!(event.getEntity() instanceof Panda entity)) return;

		NBT.modifyPersistentData(entity, nbt -> {
			if (nbt.hasTag(MAIN_GENE_KEY)) {
				entity.setMainGene(nbt.getOrDefault(MAIN_GENE_KEY, Panda.Gene.NORMAL));
				nbt.removeKey(MAIN_GENE_KEY);
			}
			if (nbt.hasTag(HIDDEN_GENE_KEY)) {
				entity.setHiddenGene(nbt.getOrDefault(HIDDEN_GENE_KEY, Panda.Gene.NORMAL));
				nbt.removeKey(HIDDEN_GENE_KEY);
			}
		});
	}

}
