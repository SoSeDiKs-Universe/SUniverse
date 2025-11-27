package me.sosedik.utilizer.listener.misc;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBTList;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import me.sosedik.utilizer.Utilizer;
import org.bukkit.Location;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.RayTraceResult;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Workaround to catch air right clicks with an empty hand while sneaking
 */
@NullMarked
public class SneakEmptyHandRightClickCatcher implements Listener {

	private static final Map<UUID, Interaction> INTERACTIONS = new HashMap<>();
	private static final String MARKERS_TAG = "clicky_markers";
	private static final int INTERACTION_SIZE = 1;
	private static final int NON_INTERACTION_SIZE = 0;

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSneak(PlayerToggleSneakEvent event) {
		Player player = event.getPlayer();
		if (!event.isSneaking()) {
			Interaction entity = INTERACTIONS.remove(player.getUniqueId());
			if (entity != null)
				entity.remove();
			return;
		}

		Interaction interaction = makeInteraction(player);
		Utilizer.scheduler().sync(task -> {
			if (!player.isOnline()) return true;

			Location loc;
			RayTraceResult rayTraceResult = player.rayTraceBlocks(4);
			if (rayTraceResult == null) {
				loc = player.getLocation().add(player.getLocation().getDirection().normalize().multiply(3)).addY(1);
			} else {
				loc = rayTraceResult.getHitPosition().toLocation(player.getWorld()).addY(-1);
			}

			interaction.teleport(loc);
			boolean marker = NBT.getPersistentData(interaction, nbt -> !nbt.hasTag(MARKERS_TAG) || nbt.getStringList(MARKERS_TAG).isEmpty());
			interaction.setResponsive(!marker);
			interaction.setInteractionHeight(marker ? NON_INTERACTION_SIZE : INTERACTION_SIZE);
			interaction.setInteractionWidth(marker ? NON_INTERACTION_SIZE : INTERACTION_SIZE);

			return false;
		}, 0L, 1L);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onAttack(PrePlayerAttackEntityEvent event) {
		if (!(event.getAttacked() instanceof Interaction interaction)) return;
		if (getInteraction(event.getPlayer()) != interaction) return;

		event.setCancelled(true);
	}

	private Interaction makeInteraction(Player player) {
		return INTERACTIONS.computeIfAbsent(player.getUniqueId(), k -> {
			Interaction interaction = player.getWorld().spawn(player.getLocation(), Interaction.class, catcher -> {
				catcher.setPersistent(false);
				catcher.setInvulnerable(true);
				catcher.setVisibleByDefault(false);
				catcher.setInteractionHeight(NON_INTERACTION_SIZE);
				catcher.setInteractionWidth(NON_INTERACTION_SIZE);
				// Features needing this workaround have to manually mark it
				catcher.setResponsive(false);
			});
			Utilizer.scheduler().sync(() -> player.showEntity(Utilizer.instance(), interaction), 10L);
			return interaction;
		});
	}

	public static @Nullable Interaction getInteraction(Player player) {
		return INTERACTIONS.get(player.getUniqueId());
	}

	public static void markInteractor(Interaction interaction, String id, boolean remove) {
		NBT.modifyPersistentData(interaction, nbt -> {
			if (remove) {
				if (nbt.hasTag(MARKERS_TAG))
					nbt.getStringList(MARKERS_TAG).remove(id);
				return;
			}
			ReadWriteNBTList<String> stringList = nbt.getStringList(MARKERS_TAG);
			if (!stringList.contains(id))
				stringList.add(id);
		});
	}

}
