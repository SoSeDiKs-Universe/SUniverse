package me.sosedik.utilizer.api.recipe;

import me.sosedik.utilizer.api.event.recipe.ItemCraftEvent;
import me.sosedik.utilizer.api.event.recipe.ItemCraftPrepareEvent;
import me.sosedik.utilizer.api.event.recipe.RemainingItemEvent;
import me.sosedik.utilizer.listener.misc.CustomRecipeLeftovers;
import me.sosedik.utilizer.listener.misc.ExtraRecipeHandlers;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.BiPredicate;
import java.util.function.Consumer;

/**
 * Allows adding extra recipe checks
 *
 * @param <T> recipe type
 */
@SuppressWarnings("unchecked")
@NullMarked
public interface ExtraRecipeChecks<T> extends CustomRecipe {

	/**
	 * Adds a prepare item craft event check
	 *
	 * @param check check
	 */
	default T withPreCheck(Consumer<ItemCraftPrepareEvent> check) {
		ExtraRecipeHandlers.addPreCraftCheck(getKey(), check);
		return (T) this;
	}

	/**
	 * Adds a leftover item check
	 *
	 * @param check check
	 */
	default T withLeftoverCheck(Consumer<RemainingItemEvent> check) {
		ExtraRecipeHandlers.addLeftoverCheck(getKey(), check);
		return (T) this;
	}

	/**
	 * Adds a post-craft check
	 *
	 * @param check check
	 */
	default T withCraftCheck(Consumer<ItemCraftEvent> check) {
		ExtraRecipeHandlers.addExtraCheck(getKey(), check);
		return (T) this;
	}

	/**
	 * Adds an exemption leftover rule for all ingredients
	 */
	default T withExemptLeftovers() {
		return withExemptLeftovers(null);
	}

	/**
	 * Adds an exemption leftover rule
	 *
	 * @param check checks whether the item should be exempt from leaving a leftover
	 */
	default T withExemptLeftovers(@Nullable BiPredicate<RemainingItemEvent, ItemStack> check) {
		CustomRecipeLeftovers.addExemptRule(getKey(), check);
		return (T) this;
	}

}
