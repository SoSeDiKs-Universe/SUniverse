package me.sosedik.trappednewbie.dataset;

import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.entity.nms.GliderEntityImpl;
import me.sosedik.trappednewbie.entity.nms.MutantZombieImpl;
import me.sosedik.trappednewbie.entity.nms.PaperPlaneImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class TrappedNewbieEntities {

	public static final EntityType<PaperPlaneImpl> PAPER_PLANE = fromKey("paper_plane",
		EntityType.Builder.<PaperPlaneImpl>of(PaperPlaneImpl::new, MobCategory.MISC) // Same as Arrow
			.noLootTable()
			.sized(0.5F, 0.5F)
			.eyeHeight(0.25F)
			.clientTrackingRange(4)
			.updateInterval(20)
	);
	public static final EntityType<GliderEntityImpl> GLIDER = fromKey("glider",
		EntityType.Builder.of(GliderEntityImpl::new, MobCategory.MISC)
			.noLootTable()
			.sized(0.8F, 1.3F)
			.clientTrackingRange(5)
			.updateInterval(1)
	);
	public static final EntityType<MutantZombieImpl> MUTANT_ZOMBIE = fromKey("mutant_zombie",
		EntityType.Builder.of(MutantZombieImpl::new, MobCategory.MONSTER)
			.noLootTable()
			.sized(1.8F, 3.2F)
			.eyeHeight(2.8F)
			.passengerAttachments(2.0125F) // ZOMBIE
			.notInPeaceful()
	);

	private static <T extends Entity> EntityType<T> fromKey(String value, EntityType.Builder<T> builder) {
		return EntityType.register(TrappedNewbie.trappedNewbieKey(value), builder);
	}

}
