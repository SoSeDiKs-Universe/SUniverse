package me.sosedik.trappednewbie.dataset;

import me.sosedik.trappednewbie.TrappedNewbie;
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

	private static <T extends Entity> EntityType<T> fromKey(String value, EntityType.Builder<T> builder) {
		return EntityType.register(TrappedNewbie.trappedNewbieKey(value), builder);
	}

}
