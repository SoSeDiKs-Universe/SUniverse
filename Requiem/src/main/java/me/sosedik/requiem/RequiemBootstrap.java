package me.sosedik.requiem;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import me.sosedik.kiterino.registry.wrapper.KiterinoMobEffectBehaviourWrapper;
import me.sosedik.requiem.dataset.RequiemItems;
import me.sosedik.requiem.effect.AttritionEffect;
import me.sosedik.requiem.effect.ParasitesEffect;
import me.sosedik.resourcelib.ResourceLibBootstrap;
import me.sosedik.resourcelib.util.BlockCreator;
import me.sosedik.resourcelib.util.ItemCreator;
import org.jspecify.annotations.NullMarked;

import java.util.function.Function;

@NullMarked
public class RequiemBootstrap implements PluginBootstrap {

	@Override
	public void bootstrap(BootstrapContext context) {
		Function<String, KiterinoMobEffectBehaviourWrapper> effectsProvider = key -> switch (key.substring("requiem:".length())) {
			case "parasites" -> new ParasitesEffect();
			case "attrition" -> new AttritionEffect();
			default -> throw new RuntimeException("Unknown effect: %s".formatted(key));
		};
		ResourceLibBootstrap.parseResources(context, effectsProvider);

		ResourceLibBootstrap.setupBlocks(context, null, (key, properties) -> switch (key.substring("requiem:".length())) {
			case String k when k.endsWith("_tombstone") -> BlockCreator.barrier(properties);
			default -> throw new IllegalArgumentException("Unknown blockstate: %s".formatted(key));
		});
		ResourceLibBootstrap.setupItems(context, RequiemItems.class, null, (key, properties) -> switch (key.substring("requiem:".length())) {
			case "ghost_motivator",
			     "ghost_relocator",
			     "host_revocator" -> ItemCreator.bowItem(properties);
			default -> null;
		});
	}

}
