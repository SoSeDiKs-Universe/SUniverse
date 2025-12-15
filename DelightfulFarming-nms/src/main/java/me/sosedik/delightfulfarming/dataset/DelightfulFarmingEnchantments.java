package me.sosedik.delightfulfarming.dataset;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.sosedik.delightfulfarming.DelightfulFarming;
import org.bukkit.enchantments.Enchantment;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class DelightfulFarmingEnchantments {

	public static final Enchantment SWEEPING = getEnchantment("sweeping");

	private static Enchantment getEnchantment(String key) {
		return RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).getOrThrow(DelightfulFarming.delightfulFarmingKey(key));
	}

}
