package me.sosedik.trappednewbie.dataset;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.sosedik.trappednewbie.TrappedNewbie;
import org.bukkit.damage.DamageType;

public class TrappedNewbieDamageTypes {

	public static final DamageType GLASS_SHARD = RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE).getOrThrow(TrappedNewbie.trappedNewbieKey("glass_shard"));
	public static final DamageType PRICKY_BLOCK = RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE).getOrThrow(TrappedNewbie.trappedNewbieKey("pricky_block"));
	public static final DamageType SUICIDE = RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE).getOrThrow(TrappedNewbie.trappedNewbieKey("suicide"));
	public static final DamageType THIRST_DAMAGE = RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE).getOrThrow(TrappedNewbie.trappedNewbieKey("thirst"));
	public static final DamageType HOT_POTATO = RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE).getOrThrow(TrappedNewbie.trappedNewbieKey("hot_potato"));

}
