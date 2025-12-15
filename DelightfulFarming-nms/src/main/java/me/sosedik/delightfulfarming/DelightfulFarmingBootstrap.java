package me.sosedik.delightfulfarming;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import me.sosedik.delightfulfarming.dataset.DelightfulFarmingItems;
import me.sosedik.delightfulfarming.impl.block.nms.GlowBerryPipsBushBlock;
import me.sosedik.delightfulfarming.impl.block.nms.SweetBerryPipsBushBlock;
import me.sosedik.resourcelib.ResourceLibBootstrap;
import me.sosedik.resourcelib.util.BlockCreator;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jspecify.annotations.NullMarked;

import java.util.Set;

import static java.util.Objects.requireNonNull;

@NullMarked
public class DelightfulFarmingBootstrap implements PluginBootstrap {

	public static final TypedKey<Enchantment> SWEEPING_ENCHANT = EnchantmentKeys.create(Key.key("delightful_farming", "sweeping"));

	@Override
	public void bootstrap(BootstrapContext context) {
		ResourceLibBootstrap.parseResources(context, null);
		ResourceLibBootstrap.setupBlocks(context, null, (key, properties) -> switch (key.substring("delightful_farming:".length())) {
			case "glow_berry_pips_bush" -> new GlowBerryPipsBushBlock((BlockBehaviour.Properties) properties, requireNonNull(NamespacedKey.fromString(key)));
			case "sweet_berry_pips_bush" -> new SweetBerryPipsBushBlock((BlockBehaviour.Properties) properties, requireNonNull(NamespacedKey.fromString(key)));
			case String k when k.endsWith("_basket") -> BlockCreator.barrier(properties, key);
			case "charcoal_block" -> BlockCreator.simpleBlock(properties, key);
			default -> throw new IllegalArgumentException("Unknown blockstate: %s".formatted(key));
		});
		ResourceLibBootstrap.setupItems(context, DelightfulFarmingItems.class, null, null);

		context.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.compose().newHandler(event -> {
			event.registry().register(
				SWEEPING_ENCHANT,
				b -> b.description(Component.translatable("enchantment.delightful_farming.sweeping"))
					.supportedItems(event.getOrCreateTag(ItemTypeTagKeys.HOES))
					.anvilCost(1)
					.maxLevel(3)
					.weight(3)
					.minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
					.maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(50, 0))
					.activeSlots(EquipmentSlotGroup.MAINHAND)
			);
		}));
		context.getLifecycleManager().registerEventHandler(LifecycleEvents.TAGS.postFlatten(RegistryKey.ENCHANTMENT), event -> {
			event.registrar().addToTag(
				EnchantmentTagKeys.TOOLTIP_ORDER,
				Set.of(SWEEPING_ENCHANT)
			);
		});
	}

}
