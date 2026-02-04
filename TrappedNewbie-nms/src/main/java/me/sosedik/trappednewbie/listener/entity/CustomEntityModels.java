package me.sosedik.trappednewbie.listener.entity;

import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.bukkit.platform.BukkitAdapter;
import kr.toxicity.model.api.data.renderer.ModelRenderer;
import me.sosedik.trappednewbie.dataset.TrappedNewbieEntityTypes;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * Applies BetterModel's models to entities
 */
@NullMarked
public class CustomEntityModels implements Listener {

	private static final List<EntityType> MODELS = List.of(
		TrappedNewbieEntityTypes.MUTANT_ZOMBIE
	);

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onLoad(EntitiesLoadEvent event) {
		event.getEntities().forEach(entity -> {
			EntityType entityType = entity.getType();
			if (!MODELS.contains(entityType)) return;

			ModelRenderer modelRenderer = BetterModel.modelOrNull(entityType.key().value());
			if (modelRenderer == null) return;

			modelRenderer.getOrCreate(BukkitAdapter.adapt(entity));
		});
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSpawn(CreatureSpawnEvent event) {
		EntityType entityType = event.getEntityType();
		if (!MODELS.contains(entityType)) return;

		ModelRenderer modelRenderer = BetterModel.modelOrNull(entityType.key().value());
		if (modelRenderer == null) return;

		modelRenderer.getOrCreate(BukkitAdapter.adapt(event.getEntity()));
	}

}
