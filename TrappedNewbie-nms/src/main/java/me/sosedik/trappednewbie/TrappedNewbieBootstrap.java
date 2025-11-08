package me.sosedik.trappednewbie;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import me.sosedik.kiterino.registry.wrapper.KiterinoMobEffectBehaviourWrapper;
import me.sosedik.kiterino.util.KiterinoBootstrapEntityTypeInjectorImpl;
import me.sosedik.resourcelib.ResourceLibBootstrap;
import me.sosedik.resourcelib.util.BlockCreator;
import me.sosedik.resourcelib.util.ItemCreator;
import me.sosedik.trappednewbie.dataset.TrappedNewbieEntities;
import me.sosedik.trappednewbie.dataset.TrappedNewbieEntityTypes;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.entity.api.Glider;
import me.sosedik.trappednewbie.entity.api.PaperPlane;
import me.sosedik.trappednewbie.entity.craft.CraftGlider;
import me.sosedik.trappednewbie.entity.craft.CraftPaperPlane;
import me.sosedik.trappednewbie.impl.block.nms.ClayKilnBlock;
import me.sosedik.trappednewbie.impl.block.nms.SleepingBagBlock;
import me.sosedik.trappednewbie.impl.block.nms.TotemBaseBlock;
import me.sosedik.trappednewbie.impl.effect.BoneBreakingEffect;
import me.sosedik.trappednewbie.impl.effect.BouncyEffect;
import me.sosedik.trappednewbie.impl.effect.ClimbingEffect;
import me.sosedik.trappednewbie.impl.effect.ComfortEffect;
import me.sosedik.trappednewbie.impl.effect.FirefingersEffect;
import me.sosedik.trappednewbie.impl.effect.HotPotatoEffect;
import me.sosedik.trappednewbie.impl.effect.LifeLeechEffect;
import me.sosedik.trappednewbie.impl.effect.ParalyzedEffect;
import me.sosedik.trappednewbie.impl.effect.QuenchedEffect;
import me.sosedik.trappednewbie.impl.effect.RottenBiteEffect;
import me.sosedik.trappednewbie.impl.effect.ScaryEffect;
import me.sosedik.trappednewbie.impl.effect.ThirstEffect;
import me.sosedik.trappednewbie.impl.effect.WaterboltEffect;
import me.sosedik.trappednewbie.impl.item.nms.HangGliderItem;
import me.sosedik.trappednewbie.impl.item.nms.KnifeItem;
import me.sosedik.trappednewbie.impl.item.nms.PaperPlaneItem;
import me.sosedik.trappednewbie.impl.item.nms.ThrowableRockItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftEntityTypes;
import org.jspecify.annotations.NullMarked;

import java.util.function.Function;

import static org.bukkit.craftbukkit.entity.CraftEntityTypes.createAndMoveEmptyRot;

@NullMarked
public class TrappedNewbieBootstrap implements PluginBootstrap {

	@Override
	public void bootstrap(BootstrapContext context) {
		Function<String, KiterinoMobEffectBehaviourWrapper> effectsProvider = key -> switch (key.substring("trapped_newbie:".length())) {
			case "bone_breaking" -> new BoneBreakingEffect();
			case "bouncy" -> new BouncyEffect();
			case "climbing" -> new ClimbingEffect();
			case "comfort" -> new ComfortEffect();
			case "firefingers" -> new FirefingersEffect();
			case "hot_potato" -> new HotPotatoEffect();
			case "life_leech" -> new LifeLeechEffect();
			case "paralyzed" -> new ParalyzedEffect();
			case "quenched" -> new QuenchedEffect();
			case "rotten_bite" -> new RottenBiteEffect();
			case "scary" -> new ScaryEffect();
			case "thirst" -> new ThirstEffect();
			case "waterbolt" -> new WaterboltEffect();
			default -> throw new RuntimeException("Unknown effect: %s".formatted(key));
		};
		ResourceLibBootstrap.parseResources(context, effectsProvider);
		ResourceLibBootstrap.setupBlocks(context, null, (key, properties) -> switch (key.substring("trapped_newbie:".length())) {
			case String k when k.endsWith("_branch") -> BlockCreator.vegetation(properties, key, Material::isSolid, Block.cube(14, 0.0625, 14));
			case String k when k.equals("pebble") || k.endsWith("_pebble") -> BlockCreator.vegetation(properties, key, Material::isSolid, Block.cube(14, 0.0625, 14));
			case String k when k.startsWith("destroy_stage_") -> BlockCreator.waterloggedBarrier(properties, key);
			case String k when k.endsWith("_work_station") -> BlockCreator.directionalBarrier(properties, key);
			case String k when k.endsWith("_drum") -> BlockCreator.barrier(properties, key);
			case String k when k.endsWith("_totem_base") -> new TotemBaseBlock(properties, NamespacedKey.fromString(key));
			case String k when k.endsWith("_chopping_block") -> BlockCreator.fakeSculk(properties, key);
			case "clay_kiln" -> new ClayKilnBlock(properties, key);
			case "sleeping_bag" -> new SleepingBagBlock(properties);
			default -> throw new IllegalArgumentException("Unknown blockstate: %s".formatted(key));
		});
		// Should be ignored internally due to ANVIL tag
		var dummyMaterial = new ToolMaterial(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 1, 2F, 0F, 0, ItemTags.ANVIL);
		ResourceLibBootstrap.setupItems(context, TrappedNewbieItems.class, null, (key, properties) -> switch (key.substring("trapped_newbie:".length())) {
			case "paper_plane" -> {
				var item = new PaperPlaneItem(properties);
				DispenserBlock.registerProjectileBehavior(item);
				yield item;
			}
			case String k when k.equals("rock") || k.equals("ball_of_mud") || k.endsWith("_rock") -> {
				var item = new ThrowableRockItem(properties);
				DispenserBlock.registerProjectileBehavior(item);
				yield item;
			}
			case String k when k.endsWith("hang_glider") -> new HangGliderItem(properties);
			case "firestriker", "trumpet", "canteen", "reinforced_canteen", "dragon_flask" -> ItemCreator.crossbowItem(properties, (item, entity, timeLeft) -> true);
			case String k when k.endsWith("glass_shard") || k.endsWith("goodie_bag") -> ItemCreator.crossbowItem(properties, (item, entity, timeLeft) -> true);
			case "flint_axe" -> new AxeItem(dummyMaterial, 6F, -3.2F, (Item.Properties) properties);
			case "flint_pickaxe" -> new Item(((Item.Properties) properties).pickaxe(dummyMaterial, 1F, -3F));
			case "flint_shovel" -> new ShovelItem(dummyMaterial, 1.5F, -3F, (Item.Properties) properties);
			case "flint_shears" -> ItemCreator.shearsItem(properties);
			case "flint_knife" -> new KnifeItem(((Item.Properties) properties)
				.component(DataComponents.TOOL, KnifeItem.createToolProperties())
				.component(DataComponents.WEAPON, new Weapon(1)));
			default -> null;
		});

		context.injectEntityTypes(TrappedNewbieEntityTypes.class, TrappedNewbieEntities.class,
			key -> switch (key.value()) {
				case "paper_plane" -> new CraftEntityTypes.EntityTypeData<>(TrappedNewbieEntityTypes.PAPER_PLANE, PaperPlane.class, CraftPaperPlane::new, createAndMoveEmptyRot(TrappedNewbieEntities.PAPER_PLANE));
				case "glider" -> new CraftEntityTypes.EntityTypeData<>(TrappedNewbieEntityTypes.GLIDER, Glider.class, CraftGlider::new, createAndMoveEmptyRot(TrappedNewbieEntities.GLIDER));
				default -> throw new IllegalArgumentException();
			},
			key -> {
				switch (key.value()) {
					case "paper_plane" -> KiterinoBootstrapEntityTypeInjectorImpl.ENTITY_TYPE_REPLACEMENTS.put(TrappedNewbieEntities.PAPER_PLANE, EntityType.SNOWBALL);
					case "glider" -> KiterinoBootstrapEntityTypeInjectorImpl.ENTITY_TYPE_REPLACEMENTS.put(TrappedNewbieEntities.GLIDER, EntityType.ITEM_DISPLAY);
					default -> throw new IllegalArgumentException();
				}
			}
		);
	}

}
