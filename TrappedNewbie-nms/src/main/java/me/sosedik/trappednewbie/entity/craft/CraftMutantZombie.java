package me.sosedik.trappednewbie.entity.craft;

import me.sosedik.trappednewbie.entity.api.MutantZombie;
import me.sosedik.trappednewbie.entity.nms.MutantZombieImpl;
import net.minecraft.world.entity.monster.zombie.Zombie;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftZombie;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CraftMutantZombie extends CraftZombie implements MutantZombie {

	public CraftMutantZombie(CraftServer server, Zombie entity) {
		super(server, entity);
	}

	@Override
	public MutantZombieImpl getHandle() {
		return (MutantZombieImpl) this.entity;
	}

}
