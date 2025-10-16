package me.sosedik.trappednewbie.listener.advancement;

import io.papermc.paper.event.player.PlayerClientLoadedWorldEvent;
import me.sosedik.packetadvancements.api.advancement.IAdvancement;
import me.sosedik.packetadvancements.api.event.TeamMadeAdvancementEvent;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import me.sosedik.trappednewbie.dataset.TrappedNewbieTags;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static me.sosedik.trappednewbie.TrappedNewbie.trappedNewbieKey;

/**
 * Granting recipes for advancements
 */
// MCCheck: 1.21.10, new recipes
@NullMarked
public class AdvancementRecipes implements Listener {

	private static final Map<IAdvancement, List<NamespacedKey>> RECIPES = new HashMap<>();

	static {
		addRecipe(TrappedNewbieAdvancements.GET_A_FIBER, trappedNewbieKey("twine"));
		addRecipe(TrappedNewbieAdvancements.GET_A_BRANCH,
			trappedNewbieKey("acacia_sapling_to_acacia_branch"),
			trappedNewbieKey("birch_sapling_to_birch_branch"),
			trappedNewbieKey("cherry_sapling_to_cherry_branch"),
			trappedNewbieKey("dark_oak_sapling_to_dark_oak_branch"),
			trappedNewbieKey("jungle_sapling_to_jungle_branch"),
			trappedNewbieKey("mangrove_propagule_to_mangrove_branch"),
			trappedNewbieKey("oak_sapling_to_oak_branch"),
			trappedNewbieKey("pale_oak_sapling_to_pale_oak_branch"),
			trappedNewbieKey("spruce_sapling_to_spruce_branch"),
			trappedNewbieKey("dead_bush_to_dead_branch")
		);
		addRecipe(TrappedNewbieAdvancements.MAKE_ROUGH_STICKS,
			trappedNewbieKey("stick"),
			trappedNewbieKey("rough_stick"),
			trappedNewbieKey("firestriker")
		);
		addRecipe(TrappedNewbieAdvancements.MAKE_A_TWINE, trappedNewbieKey("grass_mesh"));
		addRecipe(TrappedNewbieAdvancements.GET_A_ROCK,
			trappedNewbieKey("rock_to_cobblestone"),
			trappedNewbieKey("cobblestone_hammer")
		);
		addRecipe(TrappedNewbieAdvancements.GET_A_CLAY_BALL, trappedNewbieKey("clay_kiln"));
		addRecipe(TrappedNewbieAdvancements.MAKE_A_COBBLESTONE, trappedNewbieKey("cobblestone_hammer"));
		addRecipe(TrappedNewbieAdvancements.GET_A_FLINT,
			trappedNewbieKey("flint_knife"),
			trappedNewbieKey("flint_axe"),
			trappedNewbieKey("flint_pickaxe"),
			trappedNewbieKey("flint_shears"),
			trappedNewbieKey("flint_shovel_1"),
			trappedNewbieKey("flint_shovel_2")
		);
		addRecipe(TrappedNewbieAdvancements.MAKE_A_FLINT_KNIFE, trappedNewbieKey("sleeping_bag"));
		addRecipe(TrappedNewbieAdvancements.GET_A_LOG, trappedNewbieKey("campfire"));
		addRecipe(TrappedNewbieAdvancements.GET_A_LOG, TrappedNewbieTags.CHOPPING_BLOCKS.getValues().stream().map(Material::getKey).toArray(NamespacedKey[]::new));
		addRecipe(TrappedNewbieAdvancements.GET_A_LOG, TrappedNewbieTags.WORK_STATIONS.getValues().stream().map(Material::getKey).toArray(NamespacedKey[]::new));
	}

	@EventHandler
	public void onJoin(PlayerClientLoadedWorldEvent event) {
		Player player = event.getPlayer();
		if (TrappedNewbieAdvancements.MAKE_A_WORK_STATION.isDone(player))
			discoverRecipes(player);
		else
			grantAdvancementRecipes(player);
	}

	@EventHandler
	public void onAdvancement(TeamMadeAdvancementEvent event) {
		IAdvancement advancement = event.getAdvancement();
		if (advancement == TrappedNewbieAdvancements.MAKE_A_WORK_STATION) {
			event.getTeam().getOnlinePlayers().forEach(AdvancementRecipes::discoverRecipes);
			return;
		}

		List<NamespacedKey> namespacedKeys = RECIPES.get(advancement);
		if (namespacedKeys == null) return;

		event.getTeam().getOnlinePlayers().forEach(player -> player.discoverRecipes(namespacedKeys));
	}

	private void grantAdvancementRecipes(Player player) {
		RECIPES.forEach((advancement, recipeKeys) -> {
			if (advancement.isDone(player))
				player.discoverRecipes(recipeKeys);
		});
	}

	public static void discoverRecipes(Player player) {
		List<NamespacedKey> recipeKeys = new ArrayList<>();
		Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
		while (recipeIterator.hasNext()) {
			if (recipeIterator.next() instanceof Keyed keyed)
				recipeKeys.add(keyed.getKey());
		}
		player.discoverRecipes(recipeKeys);
	}

	private static void addRecipe(IAdvancement advancement, NamespacedKey... recipeKeys) {
		for (NamespacedKey recipeKey : recipeKeys) {
			if (Bukkit.getRecipe(recipeKey) == null)
				TrappedNewbie.logger().warn("Unknown recipe key: {}", recipeKey);
		}
		RECIPES.computeIfAbsent(advancement, k -> new ArrayList<>()).addAll(List.of(recipeKeys));
	}

}
