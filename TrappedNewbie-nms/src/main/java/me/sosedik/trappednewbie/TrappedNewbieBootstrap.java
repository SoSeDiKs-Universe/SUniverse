package me.sosedik.trappednewbie;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import me.sosedik.kiterino.util.KiterinoBootstrapEntityTypeInjectorImpl;
import me.sosedik.resourcelib.ResourceLibBootstrap;
import me.sosedik.resourcelib.util.BlockCreator;
import me.sosedik.resourcelib.util.ItemCreator;
import me.sosedik.trappednewbie.dataset.TrappedNewbieEntities;
import me.sosedik.trappednewbie.dataset.TrappedNewbieEntityTypes;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.entity.api.PaperPlane;
import me.sosedik.trappednewbie.entity.craft.CraftPaperPlane;
import me.sosedik.trappednewbie.nms.item.PaperPlaneItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.DispenserBlock;
import org.bukkit.craftbukkit.entity.CraftEntityTypes;
import org.jspecify.annotations.NullMarked;

import static org.bukkit.craftbukkit.entity.CraftEntityTypes.createAndMoveEmptyRot;

@NullMarked
public class TrappedNewbieBootstrap implements PluginBootstrap {

	@Override
	public void bootstrap(BootstrapContext context) {
		ResourceLibBootstrap.parseResources(context, null);
		ResourceLibBootstrap.setupBlocks(context, null, (key, properties) -> switch (key.substring("trapped_newbie:".length())) {
			case String k when k.endsWith("_twig") -> BlockCreator.vegetation(properties, key);
			case String k when k.startsWith("destroy_stage_") -> BlockCreator.barrier(properties, key);
			default -> throw new IllegalArgumentException("Unknown blockstate: %s".formatted(key));
		});
		ResourceLibBootstrap.setupItems(context, TrappedNewbieItems.class, null, (key, properties) -> switch (key.substring("trapped_newbie:".length())) {
			case "paper_plane" -> {
				var item = new PaperPlaneItem(properties);
				DispenserBlock.registerProjectileBehavior(item);
				yield item;
			}
			case "trumpet" -> ItemCreator.crossbowItem(properties, (item, entity, timeLeft) -> true);
			case "flint_shears" -> ItemCreator.shearsItem(properties);
			default -> null;
		});

		context.injectEntityTypes(TrappedNewbieEntityTypes.class, TrappedNewbieEntities.class,
			key -> switch (key.value()) {
				case "paper_plane" -> new CraftEntityTypes.EntityTypeData<>(TrappedNewbieEntityTypes.PAPER_PLANE, PaperPlane.class, CraftPaperPlane::new, createAndMoveEmptyRot(TrappedNewbieEntities.PAPER_PLANE));
				default -> throw new IllegalArgumentException();
			},
			key -> {
				switch (key.value()) {
					case "paper_plane" -> KiterinoBootstrapEntityTypeInjectorImpl.ENTITY_TYPE_REPLACEMENTS.put(TrappedNewbieEntities.PAPER_PLANE, EntityType.SNOWBALL);
					default -> throw new IllegalArgumentException();
				}
			}
		);
	}

}
