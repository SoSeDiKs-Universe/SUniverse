package me.sosedik.trappednewbie.listener.item;

import de.tr7zw.nbtapi.NBT;
import me.sosedik.kiterino.event.entity.EntityItemConsumeEvent;
import me.sosedik.kiterino.event.entity.ItemConsumeEvent;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.listener.misc.CandiesDropOnHalloween;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Goodie bags give candies upon consumption
 */
// MCCheck: 1.21.10, new candies
@NullMarked
public class GoodieBagsGiveCandies implements Listener {

	private static final Map<Material, Material> CANDIES = new HashMap<>();
	private static final NamespacedKey OPEN_SOUND = ResourceLib.getSound(TrappedNewbie.trappedNewbieKey("item/goodie_bag_open"));

	static {
		addGoodieCandie(TrappedNewbieItems.BLAZE_GOODIE_BAG, TrappedNewbieItems.FIREFINGERS_CANDY);
		addGoodieCandie(TrappedNewbieItems.CREEPER_GOODIE_BAG, TrappedNewbieItems.FIZZLERS_CANDY);
		addGoodieCandie(TrappedNewbieItems.DROWNED_GOODIE_BAG, TrappedNewbieItems.DEADISH_FISH_CANDY);
		addGoodieCandie(TrappedNewbieItems.ENDERMAN_GOODIE_BAG, TrappedNewbieItems.PEARL_POP_CANDY);
		addGoodieCandie(TrappedNewbieItems.GHAST_GOODIE_BAG, TrappedNewbieItems.SCREAMBURSTS_CANDY);
		addGoodieCandie(TrappedNewbieItems.GUARDIAN_GOODIE_BAG, TrappedNewbieItems.EYECE_CREAM_CANDY);
		addGoodieCandie(TrappedNewbieItems.PHANTOM_GOODIE_BAG, TrappedNewbieItems.MEMBRANE_BUTTER_CUPS_CANDY);
		addGoodieCandie(TrappedNewbieItems.SKELETON_GOODIE_BAG, TrappedNewbieItems.BONEBREAKER_CANDY);
		addGoodieCandie(TrappedNewbieItems.SLIME_GOODIE_BAG, TrappedNewbieItems.SLIME_GUM_CANDY);
		addGoodieCandie(TrappedNewbieItems.SPIDER_GOODIE_BAG, TrappedNewbieItems.CHOCOLATE_SPIDER_EYE_CANDY);
		addGoodieCandie(TrappedNewbieItems.ZOMBIE_GOODIE_BAG, TrappedNewbieItems.SOUR_PATCH_ZOMBIES_CANDY);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onUse(PlayerItemConsumeEvent event) {
		tryToUse(event);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onUse(EntityItemConsumeEvent event) {
		tryToUse(event);
	}

	public void tryToUse(ItemConsumeEvent event) {
		ItemStack item = event.getItem();
		Material candyType = CANDIES.get(item.getType());
		if (candyType == null) return;

		event.setReplacement(event.getItem().subtract());

		LivingEntity entity = event.getEntity();

		int amount = NBT.get(item, nbt -> {
			if (!nbt.hasTag(CandiesDropOnHalloween.LOOTER_TAG)) return 1;

			UUID bagOwner = nbt.getUUID(CandiesDropOnHalloween.LOOTER_TAG);
			if (bagOwner == null) return 1;
			if (entity.getUniqueId().equals(bagOwner)) return 1;

			return Math.random() > 0.7 ? 3 : 2;
		});

		entity.emitSound(OPEN_SOUND, 1F, 0.9F + (float) Math.random() * 0.2F);
		if (entity instanceof Player player)
			player.getInventory().addItem(ItemStack.of(candyType, amount));
		// TODO else? consume candy item?
	}

	public static void addGoodieCandie(Material goodieBag, Material candy) {
		CANDIES.put(goodieBag, candy);
	}

}
