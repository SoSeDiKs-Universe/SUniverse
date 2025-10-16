package me.sosedik.requiem.listener.item;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.sosedik.requiem.Requiem;
import me.sosedik.requiem.dataset.RequiemItems;
import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.listener.misc.DelayedActions;
import me.sosedik.utilizer.util.DelayedAction;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Players explode when eating creeper's heart
 */
@NullMarked
public class ExplodingCreeperHeart implements Listener {

	/**
	 * Damage caused by the player consuming a creeper heart
	 */
	public static final DamageType CREEPER_HEART = RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE).getOrThrow(Requiem.requiemKey("creeper_heart"));

	private static final String CREEPER_HEART_DELAYED_ACTION_ID = "creeper_heart";

	public ExplodingCreeperHeart() {
		DelayedActions.registerDelayedAction(CREEPER_HEART_DELAYED_ACTION_ID, CreeperHeartExplodeDelayedAction::new);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onConsume(PlayerItemConsumeEvent event) {
		if (event.getItem().getType() != RequiemItems.CREEPER_HEART) return;

		Player player = event.getPlayer();
		if (DelayedActions.isActive(player, CREEPER_HEART_DELAYED_ACTION_ID)) return;

		var action = new CreeperHeartExplodeDelayedAction(player, null);
		DelayedActions.scheduleAction(player, action, 3 * 20 + 1);
	}

	private static class CreeperHeartExplodeDelayedAction extends DelayedAction {

		private final Player player;

		public CreeperHeartExplodeDelayedAction(Player player, @Nullable ReadableNBT data) {
			super(CREEPER_HEART_DELAYED_ACTION_ID, data);
			this.player = player;
		}

		@Override
		public void tick() {
			int messageId = switch (this.ticksLeft) {
				case 3 * 20 -> 3;
				case 2 * 20 -> 2;
				case 20 -> 1;
				case 0 -> 0;
				default -> -1;
			};
			if (messageId == -1) return;

			var messenger = Messenger.messenger(this.player);
			var title = messenger.getMessage("creeper_heart.title." + messageId);
			var subtitle = messenger.getMessage("creeper_heart.subtitle." + messageId);
			this.player.showTitle(Title.title(title, subtitle, Title.Times.times(Ticks.duration(10L), Ticks.duration(messageId == 0 ? 60L : 20L), Ticks.duration(10L))));
		}

		@Override
		public void execute() {
			LivingEntity locHolder = PossessingPlayer.getPossessed(this.player);
			if (locHolder == null)
				locHolder = this.player;
			locHolder.getWorld().createExplosion(locHolder, locHolder.getLocation(), 6F, true, true);
			// Yes, exploding from inside is deadly
			if (!locHolder.isInvulnerable()) {
				locHolder.setHealth(0.1);
				var damageSource = DamageSource.builder(CREEPER_HEART)
					.withDamageLocation(locHolder.getLocation())
					.withCausingEntity(locHolder)
					.withDirectEntity(locHolder)
					.build();
				locHolder.damage(Double.MAX_VALUE, damageSource);
			}
		}

		@Override
		public ReadWriteNBT save() {
			return NBT.createNBTObject();
		}

	}

}
