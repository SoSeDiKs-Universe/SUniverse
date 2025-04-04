package me.sosedik.trappednewbie.entity.craft;

import me.sosedik.trappednewbie.entity.api.PaperPlane;
import me.sosedik.trappednewbie.entity.nms.PaperPlaneImpl;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftProjectile;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CraftPaperPlane extends CraftProjectile implements PaperPlane {

	public CraftPaperPlane(CraftServer server, PaperPlaneImpl entity) {
		super(server, entity);
	}

	@Override
	public ItemStack getItemStack() {
		return getHandle().pickupItemStack.asBukkitCopy();
	}

	@Override
	public void setItemStack(ItemStack stack) {
		getHandle().pickupItemStack = net.minecraft.world.item.ItemStack.fromBukkitCopy(stack);
		getHandle().updateDisplayItem();
	}

	@Override
	public PaperPlaneImpl getHandle() {
		return (PaperPlaneImpl) this.entity;
	}

}
