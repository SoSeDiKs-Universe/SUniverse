package me.sosedik.miscme.listener.item;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import me.sosedik.miscme.MiscMe;
import me.sosedik.utilizer.impl.recipe.ShapedCraft;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Shulker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;

/**
 * Colored shulkers drop colored shells
 */
public class ColoredShulkerShells implements Listener {

	private static final NamespacedKey SHULKER_RECIPE_KEY = NamespacedKey.minecraft("shulker_box");

	public ColoredShulkerShells() {
		addDyeRule();
		replaceVanillaRecipe();
	}

	@EventHandler
	public void onDeath(EntityDeathEvent event) {
		if (!(event.getEntity() instanceof Shulker shulker)) return;

		DyeColor dyeColor = shulker.getColor();
		if (dyeColor == null) return;

		event.getDrops().replaceAll(item -> {
			if (item.getType() != Material.SHULKER_SHELL) return item;
			if (item.hasData(DataComponentTypes.DYED_COLOR)) return item;

			item.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(dyeColor.getColor(), false));
			return item;
		});
	}

	private void addDyeRule() {
		ImmersiveDyes.addExtraDyeRule(Material.SHULKER_SHELL, (item, dye) -> {
			if (dye.getType() == ImmersiveDyes.CLEARING_MATERIAL) {
				if (!item.hasData(DataComponentTypes.DYED_COLOR)) return null;

				item = item.clone();
				item.resetData(DataComponentTypes.DYED_COLOR);
				return item;
			}

			DyeColor dyeColor = ImmersiveDyes.getDyeColor(dye);
			if (dyeColor == null) return null;

			if (!item.hasData(DataComponentTypes.DYED_COLOR)) {
				item = item.clone();
				item.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(dyeColor.getColor(), false));
				return item;
			}

			DyedItemColor dyedItemColor = item.getData(DataComponentTypes.DYED_COLOR);
			assert dyedItemColor != null;
			if (dyeColor == DyeColor.getByColor(dyedItemColor.color())) return null;

			item = item.clone();
			dyedItemColor = DyedItemColor.dyedItemColor(dyeColor.getColor(), dyedItemColor.showInTooltip());
			item.setData(DataComponentTypes.DYED_COLOR, dyedItemColor);

			return item;
		});
	}

	private void replaceVanillaRecipe() {
		if (!Bukkit.removeRecipe(SHULKER_RECIPE_KEY))
			MiscMe.logger().warn("Couldn't find shulker box recipe");

		addRecipe(null);
		for (DyeColor dyeColor : DyeColor.values())
			addRecipe(dyeColor);
	}

	private void addRecipe(@Nullable DyeColor dyeColor) {
		Material shulkerBox = dyeColor == null ? Material.SHULKER_BOX : Material.getMaterial(dyeColor.name() + "_SHULKER_BOX");
		if (shulkerBox == null) return;

		String prefix = dyeColor == null ? "" : dyeColor.name().toLowerCase(Locale.US) + "_";
		var craft = new ShapedCraft(new ItemStack(shulkerBox), MiscMe.miscmeKey(prefix + "shulker_box"), "S", "C", "S")
			.withGroup("shulker_box")
			.addIngredients('C', Material.CHEST, Material.TRAPPED_CHEST);

		if (dyeColor == null) {
			craft.addIngredients('S', Material.SHULKER_SHELL);
			// Recipe is cached and will pass, so check for colored input manually as well
			craft.withPreCheck(event -> {
				ItemStack shell1 = null;
				ItemStack shell2 = null;
				for (ItemStack item : event.getMatrix()) {
					if (!ItemStack.isType(item, Material.SHULKER_SHELL)) continue;

					if (shell1 == null) {
						shell1 = item;
					} else {
						shell2 = item;
						break;
					}
				}
				if (shell1 == null) return;
				if (shell2 == null) return;

				Color color1 = getColor(shell1);
				if (color1 == null) return;

				Color color2 = getColor(shell2);
				if (color2 == null) return;
				if (!color1.equals(color2)) return;

				ItemStack result = event.getResult();
				if (!ItemStack.isType(result, Material.SHULKER_BOX)) return;

				DyeColor dye = DyeColor.getByColor(color1);
				if (dye == null) return;

				Material coloredBox = Material.getMaterial(dye.name() + "_SHULKER_BOX");
				if (coloredBox == null) return;

				event.setResult(result.withType(coloredBox));
			});
		} else {
			var shell = new ItemStack(Material.SHULKER_SHELL);
			shell.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(dyeColor.getColor(), false));
			craft.addIngredientItems('S', shell,
			item -> item.hasData(DataComponentTypes.DYED_COLOR)
					&& DyeColor.getByColor(Objects.requireNonNull(item.getData(DataComponentTypes.DYED_COLOR)).color()) == dyeColor
			);
		}

		craft.register();
	}

	private @Nullable Color getColor(ItemStack item) {
		return item.hasData(DataComponentTypes.DYED_COLOR)
				? Objects.requireNonNull(item.getData(DataComponentTypes.DYED_COLOR)).color()
				: null;
	}

}
