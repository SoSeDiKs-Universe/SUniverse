package me.sosedik.trappednewbie.listener.block;

import me.sosedik.resourcelib.feature.HudMessenger;
import me.sosedik.trappednewbie.api.item.VisualArmor;
import me.sosedik.trappednewbie.listener.player.VisualArmorLayer;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.util.BiomeTags;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NullMarked;

import java.util.Set;

/**
 * Some fluids require gloves to pickup
 */
@NullMarked
public class FluidPickupRequiresGloves implements Listener {

	public static final Set<Biome> REQUIRE_GLOVES = BiomeTags.builder()
		.addTag(BiomeTags.COLD).addTag(BiomeTags.SNOWY)
		.add(Biome.COLD_OCEAN, Biome.DEEP_COLD_OCEAN, Biome.FROZEN_OCEAN, Biome.FROZEN_RIVER, Biome.DEEP_FROZEN_OCEAN)
		.build();

	@EventHandler(ignoreCancelled = true)
	public void onBucketFill(PlayerBucketFillEvent event) {
		ItemStack item = event.getItemStack();
		if (ItemStack.isEmpty(item)) return;

		Player player = event.getPlayer();
		if (player.getGameMode().isInvulnerable()) return;

		if (item.getType() == Material.LAVA_BUCKET) {
			VisualArmor visualArmor = VisualArmorLayer.getVisualArmor(player);
			if (!visualArmor.canUseVisualArmor()) return;

			if (visualArmor.hasNonBrokenGloves()) {
				ItemStack damaged = visualArmor.getGloves().damage(1, player);
				visualArmor.setGloves(damaged);
				return;
			}

			event.setCancelled(true);
			HudMessenger.of(player).displayMessage(Messenger.messenger(player).getMessage("equipment.gloves.lava_bucket_fill"), 60);
			player.setFireTicks(Math.max(6 * 20, player.getFireTicks()));
			Item droppedItem = player.dropItem(event.getHand(), 1);
			if (droppedItem != null)
				droppedItem.setFireTicks(5 * 20);
			return;
		}

		if (item.getType() == Material.WATER_BUCKET && REQUIRE_GLOVES.contains(player.getLocation().getBlock().getBiome())) {
			VisualArmor visualArmor = VisualArmorLayer.getVisualArmor(player);
			if (!visualArmor.canUseVisualArmor()) return;

			if (visualArmor.hasNonBrokenGloves()) {
				ItemStack damaged = visualArmor.getGloves().damage(1, player);
				visualArmor.setGloves(damaged);
				return;
			}

			HudMessenger.of(player).displayMessage(Messenger.messenger(player).getMessage("equipment.gloves.water_bucket_fill"), 60);
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 12 * 20, 0));
			player.setFreezeTicks(player.getFreezeTicks() + 12 * 20);
		}
	}

}
