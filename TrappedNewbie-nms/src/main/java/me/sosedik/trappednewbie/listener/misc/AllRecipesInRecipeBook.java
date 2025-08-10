package me.sosedik.trappednewbie.listener.misc;

import com.destroystokyo.paper.event.player.PlayerRecipeBookClickEvent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PotionContents;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.kiterino.modifier.item.context.SlottedItemModifierContext;
import me.sosedik.miscme.listener.misc.WaterAwarePotionReset;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.impl.item.modifier.CustomLoreModifier;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.dataset.TrappedNewbieTags;
import me.sosedik.trappednewbie.impl.thirst.ThirstData;
import me.sosedik.trappednewbie.listener.advancement.AdvancementRecipes;
import me.sosedik.utilizer.api.recipe.CustomRecipe;
import me.sosedik.utilizer.impl.recipe.BrewingCraft;
import me.sosedik.utilizer.util.InventoryUtil;
import me.sosedik.utilizer.util.RecipeManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.SmokingRecipe;
import org.bukkit.inventory.StonecuttingRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static me.sosedik.utilizer.api.recipe.CustomRecipe.getFromChoice;

/**
 * Recipe book shows all recipes
 */
@NullMarked
public class AllRecipesInRecipeBook implements Listener {

	private static final String FAKE_RECIPE_NAMESPACE = "fake_recipe";
	private static final int[] GRID_SLOTS = {13, 14, 15, 22, 23, 24, 31, 32, 33}; // https://wiki.vg/Inventory#Player_Inventory
	private static final List<ItemStack> AIR = List.of(ItemStack.of(Material.AIR));
	private static final ItemStack FILLED_ITEM = ItemStack.of(TrappedNewbieItems.MATERIAL_AIR);
	private static final List<ItemStack> FILLED = List.of(FILLED_ITEM);
	private static final List<ItemStack> CAMPFIRES = Tag.CAMPFIRES.getValues().stream().map(type -> {
		var item = ItemStack.of(type);
		item.setBlockData(Material.CAMPFIRE.createBlockData(data -> ((Campfire) data).setLit(true)));
		return item;
	}).toList();
	private static final RecipeChoice.ExactChoice CRAFTING_TABLES_CHOICE;
	private static final RecipeChoice.ExactChoice FURNACE_CHOICE;

	static {
		FILLED_ITEM.setData(DataComponentTypes.ITEM_MODEL, ResourceLib.storage().getItemModelMapping(TrappedNewbie.trappedNewbieKey("gui/filled")));

		List<ItemStack> items = new ArrayList<>();
		items.add(ItemStack.of(Material.CRAFTING_TABLE));
		TrappedNewbieTags.WORK_STATIONS.getValues().forEach(type -> items.add(ItemStack.of(type)));
		CRAFTING_TABLES_CHOICE = new RecipeChoice.ExactChoice(items);
		CRAFTING_TABLES_CHOICE.setPredicate(item -> false);
		FURNACE_CHOICE = new RecipeChoice.ExactChoice(List.of(ItemStack.of(TrappedNewbieItems.CLAY_KILN), ItemStack.of(Material.FURNACE)));
		FURNACE_CHOICE.setPredicate(item -> false);
	}

	private static final Map<UUID, RecipeDisplay> VIEWERS = new HashMap<>();
	private static final Map<NamespacedKey, VanillaPotionMix> VANILLA_POTION_RECIPES = new HashMap<>();

	public AllRecipesInRecipeBook() {
		TrappedNewbie.scheduler().sync(this::addRecipes, 2L); // Just in case other plugins/listeners add recipes later
	}

	private void addRecipes() {
		List<Recipe> fakedRecipes = new ArrayList<>();
		for (Iterator<Recipe> it = Bukkit.recipeIterator(); it.hasNext(); ) {
			Recipe recipe = it.next();
			switch (recipe) {
				case ShapedRecipe shapedRecipe when isCraftingTableRecipe(shapedRecipe) ->
						fakedRecipes.add(getDummyRecipe(shapedRecipe.getKey(), shapedRecipe.getGroup(), shapedRecipe.getResult(), CRAFTING_TABLES_CHOICE));
				case ShapelessRecipe shapelessRecipe when shapelessRecipe.getChoiceList().size() > 4 ->
						fakedRecipes.add(getDummyRecipe(shapelessRecipe.getKey(), shapelessRecipe.getGroup(), shapelessRecipe.getResult(), CRAFTING_TABLES_CHOICE));
				case StonecuttingRecipe stonecuttingRecipe ->
						fakedRecipes.add(getDummyRecipe(stonecuttingRecipe.getKey(), stonecuttingRecipe.getGroup(), stonecuttingRecipe.getResult(), new RecipeChoice.ExactChoice(ItemStack.of(Material.STONECUTTER))));
				case FurnaceRecipe furnaceRecipe ->
						fakedRecipes.add(getDummyRecipe(furnaceRecipe.getKey(), furnaceRecipe.getGroup(), furnaceRecipe.getResult(), FURNACE_CHOICE));
				case BlastingRecipe blastingRecipe ->
						fakedRecipes.add(getDummyRecipe(blastingRecipe.getKey(), blastingRecipe.getGroup(), blastingRecipe.getResult(), FURNACE_CHOICE));
				case SmokingRecipe smokingRecipe ->
						fakedRecipes.add(getDummyRecipe(smokingRecipe.getKey(), smokingRecipe.getGroup(), smokingRecipe.getResult(), FURNACE_CHOICE));
				case CampfireRecipe campfireRecipe ->
						fakedRecipes.add(getDummyRecipe(campfireRecipe.getKey(), campfireRecipe.getGroup(), campfireRecipe.getResult(), FURNACE_CHOICE));
				default -> {}
			}
		}
		RecipeManager.getRecipesFor(BrewingCraft.class).forEach(recipe -> fakedRecipes.add(getDummyRecipe(recipe.getKey(), recipe.getGroup(), recipe.getResult(), new RecipeChoice.ExactChoice(ItemStack.of(Material.BREWING_STAND)))));
		fakedRecipes.forEach(Bukkit::addRecipe);
		addVanillaPotionMixes();
	}

	private static boolean isCraftingTableRecipe(ShapedRecipe recipe) {
		if (recipe.getChoiceMap().size() > 4) return true;

		String[] shape = recipe.getShape();
		if (shape.length >= 3) return true;

		for (String line : shape) {
			if (line.length() >= 3)
				return true;
		}

		return false;
	}

	public static NamespacedKey constructFakedKey(NamespacedKey key) {
		return new NamespacedKey(FAKE_RECIPE_NAMESPACE,key.getNamespace() + "/" + key.getKey());
	}

	private static Recipe getDummyRecipe(NamespacedKey key, String group, ItemStack result, RecipeChoice.ExactChoice blocks) {
		var recipe = new ShapelessRecipe(constructFakedKey(key), result);
		recipe.setGroup(group);
		recipe.addIngredient(dummyStack(result));
		recipe.addIngredient(namelessChoice(blocks));
		recipe.addIngredient(2, TrappedNewbieItems.MATERIAL_AIR);
		recipe.setCategory(CraftingBookCategory.REDSTONE);
		return recipe;
	}

	private static RecipeChoice.ExactChoice namelessChoice(RecipeChoice.ExactChoice choice) {
		List<ItemStack> items = new ArrayList<>();
		for (ItemStack item : choice.getChoices())
			items.add(dummyStack(item));
		return new RecipeChoice.ExactChoice(items);
	}

	private static ItemStack dummyStack(ItemStack item) {
		item = item.asOne();
		item.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().hideTooltip(true).build());
		item.setData(DataComponentTypes.CUSTOM_NAME, Component.empty());
		return item;
	}

	private void handleShapedRecipe(Player player, ShapedRecipe shapedRecipe) {
		Map<Integer, List<ItemStack>> items = new HashMap<>();
		items.put(0, List.of(shapedRecipe.getResult()));
		for (int i = 1; i < 5; i++) items.put(i, FILLED);
		for (int i = 0; i < 27; i++) items.put(i + 9, FILLED);
		for (int i : GRID_SLOTS) items.put(i, AIR);
		items.put(20, CRAFTING_TABLES_CHOICE.getChoices());
		String[] shape = shapedRecipe.getShape();
		boolean addXOffset = true;
		for (String row : shape) {
			if (row.length() == 1) continue;
			addXOffset = false;
			break;
		}
		int shapeLength = shape.length;
		int offset = 3 * (3 - shapeLength);
		for (int i = 0; i < GRID_SLOTS.length; i++) {
			int j = i / 3;
			int k = i % 3;
			if (shapeLength <= j) continue;
			if (shape[j].length() <= k) continue;

			char ch = shape[j].charAt(k);
			@Nullable RecipeChoice recipeChoice = shapedRecipe.getChoiceMap().get(ch);
			int xOffset = addXOffset ? 1 : 0;
			items.put(GRID_SLOTS[i + offset + xOffset], recipeChoice == null ? AIR : getFromChoice(recipeChoice));
		}
		VIEWERS.put(player.getUniqueId(), new RecipeDisplay(player, items));
	}

	private void handleShapelessRecipe(Player player, ShapelessRecipe shapelessRecipe) {
		Map<Integer, List<ItemStack>> items = new HashMap<>();
		items.put(0, List.of(shapelessRecipe.getResult()));
		for (int i = 1; i < 5; i++) items.put(i, FILLED);
		for (int i = 0; i < 27; i++) items.put(i + 9, FILLED);
		items.put(20, CRAFTING_TABLES_CHOICE.getChoices());
		for (int i = 0; i < GRID_SLOTS.length; i++) {
			List<RecipeChoice> choices = shapelessRecipe.getChoiceList();
			if (choices.size() <= i) {
				items.put(GRID_SLOTS[i], AIR);
				continue;
			}
			@Nullable RecipeChoice recipeChoice = choices.get(i);
			items.put(GRID_SLOTS[i], recipeChoice == null ? AIR : getFromChoice(recipeChoice));
		}
		VIEWERS.put(player.getUniqueId(), new RecipeDisplay(player, items));
	}

	private void handleStonecuttingRecipe(Player player, StonecuttingRecipe stonecuttingRecipe) {
		Map<Integer, List<ItemStack>> items = new HashMap<>();
		items.put(0, List.of(stonecuttingRecipe.getResult()));
		for (int i = 1; i < 5; i++) items.put(i, FILLED);
		for (int i = 0; i < 27; i++) items.put(i + 9, FILLED);

		if (stonecuttingRecipe.getGroup().isEmpty()) {
			items.put(1, getFromChoice(stonecuttingRecipe.getInputChoice()));
			items.put(3, List.of(ItemStack.of(Material.STONECUTTER)));
		} else {
			List<ItemStack> inputs = new ArrayList<>();
			List<ItemStack> results = new ArrayList<>();
			ItemStack result = stonecuttingRecipe.getResult();
			for (Recipe r : Bukkit.getRecipesFor(result)) {
				if (!(r instanceof StonecuttingRecipe recipe)) continue;
				inputs.add(getFromChoice(recipe.getInputChoice()).get(0));
				results.add(recipe.getResult());
			}
			items.put(1, getFromChoice(stonecuttingRecipe.getInputChoice()));
			items.put(24, inputs);
			items.put(26, results);
			items.put(18, List.of(ItemStack.of(Material.STONECUTTER)));
			int inputsSize = inputs.size();
			for (int i = 0; i < GRID_SLOTS.length; i++) {
				if (i >= inputsSize) {
					items.put(GRID_SLOTS[i] - 2, AIR);
					continue;
				}
				items.put(GRID_SLOTS[i] - 2, List.of(inputs.get(i)));
			}
		}
		VIEWERS.put(player.getUniqueId(), new RecipeDisplay(player, items));
	}

	private void handleFurnaceRecipe(Player player, FurnaceRecipe furnaceRecipe) {
		Map<Integer, List<ItemStack>> items = new HashMap<>();
		items.put(0, List.of(furnaceRecipe.getResult()));
		for (int i = 1; i < 5; i++) items.put(i, FILLED);
		for (int i = 0; i < 27; i++) items.put(i + 9, FILLED);
		items.put(21, FURNACE_CHOICE.getChoices());
		items.put(23, getFromChoice(furnaceRecipe.getInputChoice()));
		items.put(30, List.of(ItemStack.of(TrappedNewbieItems.INVENTORY_FIRE), FILLED.getFirst()));
		VIEWERS.put(player.getUniqueId(), new RecipeDisplay(player, items));
	}

	private void handleFurnaceRecipe(Player player, CookingRecipe<?> cookingRecipe, List<ItemStack> furnaces) {
		Map<Integer, List<ItemStack>> items = new HashMap<>();
		items.put(0, List.of(cookingRecipe.getResult()));
		for (int i = 1; i < 5; i++) items.put(i, FILLED);
		for (int i = 0; i < 27; i++) items.put(i + 9, FILLED);
		items.put(21, furnaces);
		items.put(23, getFromChoice(cookingRecipe.getInputChoice()));
		VIEWERS.put(player.getUniqueId(), new RecipeDisplay(player, items));
	}

	private void handleBrewingRecipe(Player player, BrewingCraft brewingCraft) {
		Map<Integer, List<ItemStack>> items = new HashMap<>();
		items.put(0, List.of(brewingCraft.getResult()));
		for (int i = 1; i < 5; i++) items.put(i, FILLED);
		for (int i = 0; i < 27; i++) items.put(i + 9, FILLED);
		items.put(9, List.of(ItemStack.of(Material.BLAZE_POWDER)));
		items.put(19, List.of(ItemStack.of(Material.BREWING_STAND)));
		items.put(13, getFromChoice(brewingCraft.getIngredient()));

		var item = ItemStack.of(Material.POTION);
		item.setData(DataComponentTypes.POTION_CONTENTS, PotionContents.potionContents().potion(PotionType.AWKWARD).build());

		var awkwardPotion = List.of(item);
		items.put(30, awkwardPotion);
		items.put(31, awkwardPotion);
		items.put(32, awkwardPotion);
//		if (brewingCraft.canSplash()) { // TODO is this needed?
//			items.put(16, List.of(CustomLoreModifier.lored(ItemStack.of(Material.GUNPOWDER), "recipes.potion.splash")));
//			items.put(25, List.of(Objects.requireNonNull(brewingCraft.getSplash())));
//		}
//		if (brewingCraft.canLinger()) {
//			items.put(17, List.of(CustomLoreModifier.lored(ItemStack.of(Material.DRAGON_BREATH), "recipes.potion.lingering")));
//			items.put(26, List.of(Objects.requireNonNull(brewingCraft.getLingering())));
//		}
		VIEWERS.put(player.getUniqueId(), new RecipeDisplay(player, items));
	}

	@EventHandler
	public void onRecipe(PlayerRecipeBookClickEvent event) {
		Player player = event.getPlayer();
		if (player.getOpenInventory().getTopInventory().getType() != InventoryType.CRAFTING) return;

		RecipeDisplay currentDisplay = VIEWERS.remove(player.getUniqueId());
		if (currentDisplay != null) currentDisplay.cancel();

		NamespacedKey recipeKey = event.getRecipe();
		if (!FAKE_RECIPE_NAMESPACE.equals(recipeKey.getNamespace())) return;

		String[] keys = recipeKey.getKey().split("/", 2);
		if (keys.length != 2) return;

		event.setCancelled(true);

		var originalKey = new NamespacedKey(keys[0], keys[1]);
		TrappedNewbie.scheduler().sync(() -> displayRecipe(player, originalKey));
	}

	private void displayRecipe(Player player, NamespacedKey recipeKey) {
		clearCraftingGreed(player);
		Recipe recipe = Bukkit.getRecipe(recipeKey);
		switch (recipe) {
			case null -> {
				CustomRecipe customRecipe = RecipeManager.getRecipe(recipeKey);
				if (customRecipe instanceof BrewingCraft brewingCraft) {
					handleBrewingRecipe(player, brewingCraft);
				} else {
					VanillaPotionMix vanillaPotionMix = VANILLA_POTION_RECIPES.get(recipeKey);
					if (vanillaPotionMix != null)
						vanillaPotionMix.handle(player);
				}
			}
			case ShapedRecipe shapedRecipe -> handleShapedRecipe(player, shapedRecipe);
			case ShapelessRecipe shapelessRecipe -> handleShapelessRecipe(player, shapelessRecipe);
			case StonecuttingRecipe stonecuttingRecipe -> handleStonecuttingRecipe(player, stonecuttingRecipe);
			case FurnaceRecipe furnaceRecipe -> handleFurnaceRecipe(player, furnaceRecipe);
			case BlastingRecipe blastingRecipe -> handleFurnaceRecipe(player, blastingRecipe, List.of(ItemStack.of(Material.BLAST_FURNACE)));
			case SmokingRecipe smokingRecipe -> handleFurnaceRecipe(player, smokingRecipe, List.of(ItemStack.of(Material.SMOKER)));
			case CampfireRecipe campfireRecipe -> handleFurnaceRecipe(player, campfireRecipe, CAMPFIRES);
			default -> {}
		}
	}

	private void clearCraftingGreed(Player player) {
		Inventory inventory = player.getOpenInventory().getTopInventory();
		for (int i = 1; i <= 4; i++) {
			ItemStack adding = inventory.getItem(i);
			if (!ItemStack.isEmpty(adding)) {
				inventory.setItem(i, null);
				InventoryUtil.addOrDrop(player, adding, true);
			}
		}
	}

	@EventHandler
	public void onCraft(PrepareItemCraftEvent event) {
		if (!(event.getRecipe() instanceof ShapelessRecipe recipe)) return;

		ItemStack result = event.getInventory().getResult();
		if (ItemStack.isEmpty(result)) return;

		NamespacedKey key = recipe.getKey();
		if (!FAKE_RECIPE_NAMESPACE.equals(key.getNamespace())) return;

		event.getInventory().setResult(null);
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		if (!(event.getPlayer() instanceof Player player)) return;
		if (player.getOpenInventory().getTopInventory().getType() != InventoryType.CRAFTING) return;

		RecipeDisplay recipeDisplay = VIEWERS.remove(player.getUniqueId());
		if (recipeDisplay != null)
			recipeDisplay.cancel();
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		RecipeDisplay recipeDisplay = VIEWERS.remove(event.getPlayer().getUniqueId());
		if (recipeDisplay != null)
			recipeDisplay.cancel();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onOpenForget(InventoryOpenEvent event) {
		if (!hasRecipeBook(event.getInventory().getType())) return;
		if (!(event.getPlayer() instanceof Player player)) return;

		player.undiscoverRecipes(player.getDiscoveredRecipes().stream().filter(key -> FAKE_RECIPE_NAMESPACE.equals(key.getNamespace())).toList());
	}

	@EventHandler
	public void onCloseDiscover(InventoryCloseEvent event) {
		if (!hasRecipeBook(event.getInventory().getType())) return;
		if (!(event.getPlayer() instanceof Player player)) return;

		if (TrappedNewbieAdvancements.MAKE_A_WORK_STATION.isDone(player))
			AdvancementRecipes.discoverRecipes(player);
	}

	// MCCheck: 1.21.8, new inventories
	public static boolean hasRecipeBook(InventoryType type) {
		return switch (type) {
			case WORKBENCH, FURNACE, BLAST_FURNACE, SMOKER -> true;
			default -> false;
		};
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onQuit(InventoryClickEvent event) {
		if (event.getClickedInventory() == null) return;
		if (!(event.getWhoClicked() instanceof Player player)) return;
		if (player.getOpenInventory().getTopInventory().getType() != InventoryType.CRAFTING) return;

		if (event.getSlot() > 4) {
			RecipeDisplay recipeDisplay = VIEWERS.get(player.getUniqueId());
			if (recipeDisplay == null) return;

			event.setCancelled(true);
			recipeDisplay.sendDisplay();
		} else {
			RecipeDisplay recipeDisplay = VIEWERS.remove(player.getUniqueId());
			if (recipeDisplay != null)
				recipeDisplay.cancel();
		}
	}

	private static class RecipeDisplay extends BukkitRunnable {

		private final Player player;
		private final Map<Integer, List<ItemStack>> items;
		private int currentDisplay = 0;

		public RecipeDisplay(Player player, Map<Integer, List<ItemStack>> items) {
			this.player = player;
			this.items = items;
			TrappedNewbie.scheduler().async(this, 0L, 30L);
		}

		@Override
		public void run() {
			this.currentDisplay++;
			sendDisplay();
		}

		public void sendDisplay() {
			this.items.forEach((slot, items) -> {
				int size = items.size();
				ItemStack item = size == 1 ? items.getFirst() : items.get(this.currentDisplay % size);
				this.player.sendItem(slot, item);
			});
		}

		public @Nullable ItemStack getItem(int slot) {
			List<ItemStack> fakeItems = this.items.get(slot);
			if (fakeItems == null) return null;

			int size = fakeItems.size();
			if (size == 1) return fakeItems.getFirst();
			return fakeItems.get(this.currentDisplay % size);
		}

		@Override
		public synchronized void cancel() {
			super.cancel();
			if (this.player.isValid())
				this.player.updateInventory();
		}

	}

	// MCCheck: 1.21.8, new potion mixes
	private static void addVanillaPotionMixes() { // From PotionBrewing class
		addVanillaPotionMix("splash_potion", ItemStack.of(Material.SPLASH_POTION), new RecipeChoice.MaterialChoice(Material.GUNPOWDER), new RecipeChoice.MaterialChoice(Material.POTION), null, null);
		addVanillaPotionMix("lingering_potion", ItemStack.of(Material.LINGERING_POTION), new RecipeChoice.MaterialChoice(Material.DRAGON_BREATH), new RecipeChoice.MaterialChoice(Material.SPLASH_POTION), null, null);
		ItemStack waterPotion = ThirstData.of(WaterAwarePotionReset.getWaterBottle(1)).withThirstChance(0.5F).saveInto(WaterAwarePotionReset.getWaterBottle(1));
		addVanillaPotionMix(PotionType.MUNDANE, potion(Material.POTION, PotionType.MUNDANE), new RecipeChoice.ExactChoice(BrewingCraft.MUNDANE_INGREDIENTS.stream().map(ItemStack::of).toList()), new RecipeChoice.ExactChoice(waterPotion), null, null);
		addVanillaPotionMix(PotionType.THICK, potion(Material.POTION, PotionType.THICK), new RecipeChoice.MaterialChoice(Material.GLOWSTONE_DUST), new RecipeChoice.ExactChoice(waterPotion), null, null);
		addVanillaPotionMix(PotionType.AWKWARD, potion(Material.POTION, PotionType.AWKWARD), new RecipeChoice.ExactChoice(ItemStack.of(Material.NETHER_WART)), new RecipeChoice.ExactChoice(waterPotion), null, null);
		addVanillaPotionMix(PotionType.NIGHT_VISION, Material.GOLDEN_CARROT, null, PotionType.LONG_NIGHT_VISION);
		addVanillaPotionMix(PotionType.INVISIBILITY, Material.FERMENTED_SPIDER_EYE, PotionType.NIGHT_VISION, null, PotionType.LONG_INVISIBILITY);
		addVanillaPotionMix(PotionType.FIRE_RESISTANCE, Material.MAGMA_CREAM, null, PotionType.LONG_FIRE_RESISTANCE);
		addVanillaPotionMix(PotionType.LEAPING, Material.RABBIT_FOOT, PotionType.STRONG_LEAPING, PotionType.LONG_LEAPING);
		addVanillaPotionMix(PotionType.SLOWNESS, Material.FERMENTED_SPIDER_EYE,
			List.of(potion(Material.POTION, PotionType.LEAPING), potion(Material.POTION, PotionType.LEAPING), potion(Material.POTION, PotionType.STRONG_LEAPING), potion(Material.POTION, PotionType.STRONG_LEAPING)),
			PotionType.STRONG_SLOWNESS, PotionType.LONG_SLOWNESS
		);
		addVanillaPotionMix(PotionType.TURTLE_MASTER, Material.TURTLE_HELMET, PotionType.STRONG_TURTLE_MASTER, PotionType.LONG_TURTLE_MASTER);
		addVanillaPotionMix(PotionType.SWIFTNESS, Material.SUGAR, PotionType.STRONG_SWIFTNESS, PotionType.LONG_SWIFTNESS);
		addVanillaPotionMix(PotionType.WATER_BREATHING, Material.PUFFERFISH, null, PotionType.LONG_WATER_BREATHING);
		addVanillaPotionMix(PotionType.HEALING, Material.GLISTERING_MELON_SLICE, PotionType.STRONG_HEALING, null);
		addVanillaPotionMix(PotionType.HARMING, Material.FERMENTED_SPIDER_EYE,
			List.of(potion(Material.POTION, PotionType.HEALING), potion(Material.POTION, PotionType.POISON), potion(Material.POTION, PotionType.STRONG_HEALING), potion(Material.POTION, PotionType.LONG_POISON)),
			PotionType.STRONG_HARMING, null
		);
		addVanillaPotionMix(PotionType.POISON, Material.SPIDER_EYE, PotionType.STRONG_POISON, PotionType.LONG_POISON);
		addVanillaPotionMix(PotionType.REGENERATION, Material.GHAST_TEAR, PotionType.STRONG_REGENERATION, PotionType.LONG_REGENERATION);
		addVanillaPotionMix(PotionType.STRENGTH, Material.BLAZE_POWDER, PotionType.STRONG_STRENGTH, PotionType.LONG_STRENGTH);
		addVanillaPotionMix(PotionType.WEAKNESS, Material.FERMENTED_SPIDER_EYE, null, PotionType.LONG_WEAKNESS);
		addVanillaPotionMix(PotionType.SLOW_FALLING, Material.PHANTOM_MEMBRANE, null, PotionType.LONG_SLOW_FALLING);
	}

	private record VanillaPotionMix(Map<Integer, List<ItemStack>> items) {

		void handle(Player player) {
			VIEWERS.put(player.getUniqueId(), new RecipeDisplay(player, this.items));
		}

	}

	private static void addVanillaPotionMix(PotionType type, Material ingredient, List<ItemStack> choices, @Nullable PotionType upgraded, @Nullable PotionType extended) {
		addVanillaPotionMix(
			type.name().toLowerCase(),
			potion(Material.POTION, type),
			new RecipeChoice.MaterialChoice(ingredient),
			new RecipeChoice.ExactChoice(choices),
			upgraded == null ? null : potion(Material.POTION, upgraded),
			extended == null ? null : potion(Material.POTION, extended)
		);
	}

	private static void addVanillaPotionMix(PotionType type, Material ingredient, @Nullable PotionType upgraded, @Nullable PotionType extended) {
		var item = ItemStack.of(Material.POTION);
		item.setData(DataComponentTypes.POTION_CONTENTS, PotionContents.potionContents().potion(PotionType.AWKWARD).build());
		addVanillaPotionMix(type, ingredient, item, upgraded, extended);
	}

	private static void addVanillaPotionMix(PotionType type, Material ingredient, ItemStack base, @Nullable PotionType upgraded, @Nullable PotionType extended) {
		addVanillaPotionMix(
			type.name().toLowerCase() + "_potion",
			potion(Material.POTION, type),
			new RecipeChoice.ExactChoice(ItemStack.of(ingredient)),
			new RecipeChoice.ExactChoice(base),
			upgraded == null ? null : potion(Material.POTION, upgraded),
			extended == null ? null : potion(Material.POTION, extended)
		);
	}

	private static void addVanillaPotionMix(PotionType type, Material ingredient, PotionType base, @Nullable PotionType upgraded, @Nullable PotionType extended) {
		addVanillaPotionMix(
			type.name().toLowerCase() + "_potion",
			potion(Material.POTION, type),
			new RecipeChoice.MaterialChoice(ingredient),
			new RecipeChoice.ExactChoice(potion(Material.POTION, base), upgraded == null ? potion(Material.POTION, extended) : potion(Material.POTION, upgraded)),
			upgraded == null ? null : potion(Material.POTION, upgraded),
			extended == null ? null : potion(Material.POTION, extended)
		);
	}

	private static void addVanillaPotionMix(PotionType type, ItemStack result, RecipeChoice ingredient, RecipeChoice base, @Nullable ItemStack upgraded, @Nullable ItemStack extended) {
		addVanillaPotionMix(type.name().toLowerCase() + "_potion", result, ingredient, base, upgraded, extended);
	}

	private static void addVanillaPotionMix(String key, ItemStack result, RecipeChoice ingredient, RecipeChoice base, @Nullable ItemStack upgraded, @Nullable ItemStack extended) {
		Map<Integer, List<ItemStack>> items = new HashMap<>();
		items.put(0, List.of(result));
		for (int i = 1; i < 5; i++) items.put(i, FILLED);
		for (int i = 0; i < 27; i++) items.put(i + 9, FILLED);
		items.put(9, List.of(ItemStack.of(Material.BLAZE_POWDER)));
		items.put(19, List.of(ItemStack.of(Material.BREWING_STAND)));
		items.put(13, getFromChoice(ingredient));
		var basePotion = getFromChoice(base);
		items.put(30, basePotion);
		items.put(31, basePotion);
		items.put(32, basePotion);
		if (upgraded != null) {
			items.put(16, List.of(CustomLoreModifier.lored(ItemStack.of(Material.GLOWSTONE_DUST), "recipes.potion.upgraded")));
			items.put(25, List.of(upgraded));
		}
		if (extended != null) {
			items.put(17, List.of(CustomLoreModifier.lored(ItemStack.of(Material.REDSTONE), "recipes.potion.extended")));
			items.put(26, List.of(extended));
		}
		NamespacedKey recipeKey = new NamespacedKey(FAKE_RECIPE_NAMESPACE, key);
		VANILLA_POTION_RECIPES.put(recipeKey, new VanillaPotionMix(items));
		Bukkit.addRecipe(getDummyRecipe(recipeKey, "", result, new RecipeChoice.ExactChoice(ItemStack.of(Material.BREWING_STAND))));
	}

	private static ItemStack potion(Material type, PotionType base) {
		var item = ItemStack.of(type);
		item.setData(DataComponentTypes.POTION_CONTENTS, PotionContents.potionContents().potion(base).build());
		return item;
	}

	public static class RecipeItemParser extends ItemModifier {

		public RecipeItemParser(NamespacedKey modifierId) {
			super(modifierId);
		}

		@Override
		public ModificationResult modify(ItemContextBox contextBox) {
			Player player = contextBox.getViewer();
			if (player == null) return ModificationResult.PASS;
			if (!(contextBox.getContext() instanceof SlottedItemModifierContext context)) return ModificationResult.PASS;

			int slot = context.slot();
			if (slot < 0) return ModificationResult.PASS;
			if (slot >= 5 && slot <= 8) return ModificationResult.PASS;
			if (slot >= 36) return ModificationResult.PASS;
			if (player.getOpenInventory().getTopInventory().getType() != InventoryType.CRAFTING) return ModificationResult.PASS;

			RecipeDisplay recipeDisplay = VIEWERS.get(player.getUniqueId());
			if (recipeDisplay == null) return ModificationResult.PASS;

			ItemStack recipeItem = recipeDisplay.getItem(slot);
			if (recipeItem == null) return ModificationResult.PASS;

			ItemStack item = modifyItem(player, contextBox.getLocale(), recipeItem);

			contextBox.setItem(item == null ? recipeItem.clone() : item);

			return ModificationResult.RETURN;
		}

		@Override
		public boolean skipAir() {
			return false;
		}

	}

}
