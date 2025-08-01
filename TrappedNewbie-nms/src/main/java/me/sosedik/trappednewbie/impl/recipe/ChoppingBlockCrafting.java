package me.sosedik.trappednewbie.impl.recipe;

import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.utilizer.api.recipe.CustomRecipe;
import me.sosedik.utilizer.util.RecipeManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Random;

@NullMarked
public class ChoppingBlockCrafting implements CustomRecipe {

	private static final Random RANDOM = new Random();

	private final NamespacedKey key;
	private final Material base;
	private final ItemStack result;
	private final int min;
	private final int max;

	public ChoppingBlockCrafting(Material base, Material result, int min, int max) {
		this(base, ItemStack.of(result), min, max);
	}

	public ChoppingBlockCrafting(Material base, ItemStack result, int min, int max) {
		this.key = TrappedNewbie.trappedNewbieKey(base.getKey().getKey() + "_to_" + result.getType().getKey().getKey() + "_from_chopping");
		this.base = base;
		this.result = result;
		this.min = min;
		this.max = max;
	}

	@Override
	public ItemStack getResult() {
		return this.result.asQuantity(this.min + RANDOM.nextInt(this.max - this.min + 1));
	}

	@Override
	public boolean checkMatrix(@Nullable ItemStack[] items) {
		if (items.length != 1) return false;

		ItemStack item = items[0];
		if (ItemStack.isEmpty(item)) return false;

		return item.getType() == this.base;
	}

	@Override
	public NamespacedKey getKey() {
		return this.key;
	}

	public void register() {
		RecipeManager.addRecipe(this);
	}

	// MCCheck: 1.21.8, new blocks
	public static void registerRecipes() {
		// Logs > Planks
		new ChoppingBlockCrafting(Material.ACACIA_LOG, Material.ACACIA_PLANKS, 2, 4).register();
		new ChoppingBlockCrafting(Material.BIRCH_LOG, Material.BIRCH_PLANKS, 2, 4).register();
		new ChoppingBlockCrafting(Material.DARK_OAK_LOG, Material.DARK_OAK_PLANKS, 2, 4).register();
		new ChoppingBlockCrafting(Material.JUNGLE_LOG, Material.JUNGLE_PLANKS, 2, 4).register();
		new ChoppingBlockCrafting(Material.OAK_LOG, Material.OAK_PLANKS, 2, 4).register();
		new ChoppingBlockCrafting(Material.SPRUCE_LOG, Material.SPRUCE_PLANKS, 2, 4).register();
		new ChoppingBlockCrafting(Material.CHERRY_LOG, Material.CHERRY_PLANKS, 2, 4).register();
		new ChoppingBlockCrafting(Material.MANGROVE_LOG, Material.MANGROVE_PLANKS, 2, 4).register();
		new ChoppingBlockCrafting(Material.CRIMSON_STEM, Material.CRIMSON_PLANKS, 2, 4).register();
		new ChoppingBlockCrafting(Material.WARPED_STEM, Material.WARPED_PLANKS, 2, 4).register();
		new ChoppingBlockCrafting(Material.BAMBOO_BLOCK, Material.BAMBOO_PLANKS, 2, 4).register();
		// Stripped Logs > Planks
		new ChoppingBlockCrafting(Material.STRIPPED_ACACIA_LOG, Material.ACACIA_PLANKS, 2, 4).register();
		new ChoppingBlockCrafting(Material.STRIPPED_BIRCH_LOG, Material.BIRCH_PLANKS, 2, 4).register();
		new ChoppingBlockCrafting(Material.STRIPPED_DARK_OAK_LOG, Material.DARK_OAK_PLANKS, 2, 4).register();
		new ChoppingBlockCrafting(Material.STRIPPED_JUNGLE_LOG, Material.JUNGLE_PLANKS, 2, 4).register();
		new ChoppingBlockCrafting(Material.STRIPPED_OAK_LOG, Material.OAK_PLANKS, 2, 4).register();
		new ChoppingBlockCrafting(Material.STRIPPED_SPRUCE_LOG, Material.SPRUCE_PLANKS, 2, 4).register();
		new ChoppingBlockCrafting(Material.STRIPPED_CHERRY_LOG, Material.CHERRY_PLANKS, 2, 4).register();
		new ChoppingBlockCrafting(Material.STRIPPED_MANGROVE_LOG, Material.MANGROVE_PLANKS, 2, 4).register();
		new ChoppingBlockCrafting(Material.STRIPPED_CRIMSON_STEM, Material.CRIMSON_PLANKS, 2, 4).register();
		new ChoppingBlockCrafting(Material.STRIPPED_WARPED_STEM, Material.WARPED_PLANKS, 2, 4).register();
		new ChoppingBlockCrafting(Material.STRIPPED_BAMBOO_BLOCK, Material.BAMBOO_PLANKS, 2, 4).register();
		// Full Logs > Planks
		new ChoppingBlockCrafting(Material.ACACIA_WOOD, Material.ACACIA_PLANKS, 2, 4).register();
		new ChoppingBlockCrafting(Material.BIRCH_WOOD, Material.BIRCH_PLANKS, 2, 4).register();
		new ChoppingBlockCrafting(Material.DARK_OAK_WOOD, Material.DARK_OAK_PLANKS, 2, 4).register();
		new ChoppingBlockCrafting(Material.JUNGLE_WOOD, Material.JUNGLE_PLANKS, 2, 4).register();
		new ChoppingBlockCrafting(Material.OAK_WOOD, Material.OAK_PLANKS, 2, 4).register();
		new ChoppingBlockCrafting(Material.PALE_OAK_WOOD, Material.PALE_OAK_PLANKS, 2, 4).register();
		new ChoppingBlockCrafting(Material.SPRUCE_WOOD, Material.SPRUCE_PLANKS, 2, 4).register();
		new ChoppingBlockCrafting(Material.CHERRY_WOOD, Material.CHERRY_PLANKS, 2, 4).register();
		new ChoppingBlockCrafting(Material.MANGROVE_WOOD, Material.MANGROVE_PLANKS, 2, 4).register();
		new ChoppingBlockCrafting(Material.CRIMSON_HYPHAE, Material.CRIMSON_PLANKS, 2, 4).register();
		new ChoppingBlockCrafting(Material.WARPED_HYPHAE, Material.WARPED_PLANKS, 2, 4).register();
		// Planks > Sticks
		new ChoppingBlockCrafting(Material.ACACIA_PLANKS, TrappedNewbieItems.ACACIA_STICK, 3, 5).register();
		new ChoppingBlockCrafting(Material.BIRCH_PLANKS, TrappedNewbieItems.BIRCH_STICK, 3, 5).register();
		new ChoppingBlockCrafting(Material.CHERRY_PLANKS, TrappedNewbieItems.CHERRY_STICK, 3, 5).register();
		new ChoppingBlockCrafting(Material.DARK_OAK_PLANKS, TrappedNewbieItems.DARK_OAK_STICK, 3, 5).register();
		new ChoppingBlockCrafting(Material.JUNGLE_PLANKS, TrappedNewbieItems.JUNGLE_STICK, 3, 5).register();
		new ChoppingBlockCrafting(Material.MANGROVE_PLANKS, TrappedNewbieItems.MANGROVE_STICK, 3, 5).register();
		new ChoppingBlockCrafting(Material.OAK_PLANKS, TrappedNewbieItems.OAK_STICK, 3, 5).register();
		new ChoppingBlockCrafting(Material.PALE_OAK_PLANKS, TrappedNewbieItems.PALE_OAK_STICK, 3, 5).register();
		new ChoppingBlockCrafting(Material.SPRUCE_PLANKS, TrappedNewbieItems.SPRUCE_STICK, 3, 5).register();
		new ChoppingBlockCrafting(Material.CRIMSON_PLANKS, TrappedNewbieItems.CRIMSON_STICK, 3, 5).register();
		new ChoppingBlockCrafting(Material.WARPED_PLANKS, TrappedNewbieItems.WARPED_STICK, 3, 5).register();
		new ChoppingBlockCrafting(Material.BAMBOO_PLANKS, TrappedNewbieItems.BAMBOOS_STICK, 3, 5).register();
		// Stripped Planks > Sticks
		new ChoppingBlockCrafting(Material.STRIPPED_ACACIA_WOOD, TrappedNewbieItems.ACACIA_STICK, 3, 5).register();
		new ChoppingBlockCrafting(Material.STRIPPED_BIRCH_WOOD, TrappedNewbieItems.BIRCH_STICK, 3, 5).register();
		new ChoppingBlockCrafting(Material.STRIPPED_DARK_OAK_WOOD, TrappedNewbieItems.DARK_OAK_STICK, 3, 5).register();
		new ChoppingBlockCrafting(Material.STRIPPED_JUNGLE_WOOD, TrappedNewbieItems.JUNGLE_STICK, 3, 5).register();
		new ChoppingBlockCrafting(Material.STRIPPED_OAK_WOOD, TrappedNewbieItems.OAK_STICK, 3, 5).register();
		new ChoppingBlockCrafting(Material.STRIPPED_PALE_OAK_WOOD, TrappedNewbieItems.PALE_OAK_STICK, 3, 5).register();
		new ChoppingBlockCrafting(Material.STRIPPED_SPRUCE_WOOD, TrappedNewbieItems.SPRUCE_STICK, 3, 5).register();
		new ChoppingBlockCrafting(Material.STRIPPED_CRIMSON_HYPHAE, TrappedNewbieItems.CRIMSON_STICK, 3, 5).register();
		new ChoppingBlockCrafting(Material.STRIPPED_WARPED_HYPHAE, TrappedNewbieItems.WARPED_STICK, 3, 5).register();
		// Slabs > Sticks
		new ChoppingBlockCrafting(Material.ACACIA_SLAB, TrappedNewbieItems.ACACIA_STICK, 1, 3).register();
		new ChoppingBlockCrafting(Material.BIRCH_SLAB, TrappedNewbieItems.BIRCH_STICK, 1, 3).register();
		new ChoppingBlockCrafting(Material.DARK_OAK_SLAB, TrappedNewbieItems.DARK_OAK_STICK, 1, 3).register();
		new ChoppingBlockCrafting(Material.JUNGLE_SLAB, TrappedNewbieItems.JUNGLE_STICK, 1, 3).register();
		new ChoppingBlockCrafting(Material.OAK_SLAB, TrappedNewbieItems.OAK_STICK, 1, 3).register();
		new ChoppingBlockCrafting(Material.PALE_OAK_SLAB, TrappedNewbieItems.PALE_OAK_STICK, 1, 3).register();
		new ChoppingBlockCrafting(Material.SPRUCE_SLAB, TrappedNewbieItems.SPRUCE_STICK, 1, 3).register();
		new ChoppingBlockCrafting(Material.CRIMSON_SLAB, TrappedNewbieItems.CRIMSON_STICK, 1, 3).register();
		new ChoppingBlockCrafting(Material.WARPED_SLAB, TrappedNewbieItems.WARPED_STICK, 1, 3).register();
		new ChoppingBlockCrafting(Material.BAMBOO_SLAB, TrappedNewbieItems.BAMBOOS_STICK, 1, 3).register();
	}

}
