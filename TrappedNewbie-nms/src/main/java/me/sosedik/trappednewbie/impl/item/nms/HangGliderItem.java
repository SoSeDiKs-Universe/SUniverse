package me.sosedik.trappednewbie.impl.item.nms;

import com.destroystokyo.paper.ParticleBuilder;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.entity.nms.GliderEntityImpl;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.jspecify.annotations.NullMarked;

import java.util.Map;

@NullMarked
public class HangGliderItem extends Item {

	private static final Map<Material, Particle> PARTICLES = Map.ofEntries(
		Map.entry(TrappedNewbieItems.CHERRY_HANG_GLIDER, Particle.CHERRY_LEAVES),
		Map.entry(TrappedNewbieItems.SCULK_HANG_GLIDER, Particle.SCULK_CHARGE_POP),
		Map.entry(TrappedNewbieItems.AZALEA_HANG_GLIDER, Particle.SPORE_BLOSSOM_AIR),
		Map.entry(TrappedNewbieItems.PHANTOM_HANG_GLIDER, Particle.MYCELIUM)
	);

	public HangGliderItem(Object properties) {
		super(((Properties) properties));

		DispenserBlock.registerBehavior(this, GliderEntityImpl::createDispenser);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack item = player.getItemInHand(hand);
		if (level instanceof ServerLevel serverWorld && GliderEntityImpl.create(serverWorld, player, item, hand)) {
			player.setItemInHand(hand, ItemStack.EMPTY);
			return InteractionResult.SUCCESS_SERVER;
		}

		return super.use(level, player, hand);
	}

	public void tickGlider(ServerLevel level, GliderEntityImpl entity, ItemStack itemStack) {
		if (entity.tickCount % 2 != 0) return;

		Particle particle = PARTICLES.get(itemStack.asBukkitMirror().getType());
		if (particle == null) return;

		new ParticleBuilder(particle)
			.location(level.getWorld(), entity.getX(), entity.getY() + 1.5, entity.getZ())
			.offset(0.8, 0, 0.8)
			.count(Math.max((int) (entity.getDeltaMovement().lengthSqr() * 3F), 2))
			.extra(0)
			.spawn();
	}

}
