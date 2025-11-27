package me.sosedik.trappednewbie.entity.craft;

import me.sosedik.trappednewbie.entity.api.Glider;
import me.sosedik.trappednewbie.entity.nms.GliderEntityImpl;
import net.minecraft.world.entity.Display;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftItemDisplay;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CraftGlider extends CraftItemDisplay implements Glider {

	public CraftGlider(CraftServer server, Display.ItemDisplay entity) {
		super(server, entity);
	}

	@Override
	public GliderEntityImpl getHandle() {
		return (GliderEntityImpl) this.entity;
	}

}
