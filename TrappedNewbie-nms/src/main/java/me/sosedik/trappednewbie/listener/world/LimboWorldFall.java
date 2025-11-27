package me.sosedik.trappednewbie.listener.world;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import me.sosedik.miscme.dataset.MoreMobHeads;
import me.sosedik.miscme.impl.item.modifier.BookAuthorOnlineModifier;
import me.sosedik.moves.listener.movement.FreeFall;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.requiem.listener.player.ghost.GhostsPhaseThroughWalls;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.impl.item.modifier.CustomLoreModifier;
import me.sosedik.resourcelib.impl.item.modifier.CustomNameModifier;
import me.sosedik.socializer.listener.FriendlyPlayers;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import me.sosedik.trappednewbie.dataset.TrappedNewbieFonts;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.impl.item.modifier.LetterModifier;
import me.sosedik.utilizer.Utilizer;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.api.message.Mini;
import me.sosedik.utilizer.util.InventoryUtil;
import me.sosedik.utilizer.util.ItemUtil;
import me.sosedik.utilizer.util.LocationUtil;
import me.sosedik.utilizer.util.MathUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.Markers;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.item.BoundItem;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemWrapper;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static me.sosedik.utilizer.api.message.Mini.raw;

/**
 * Teleport to resource world when falling in limbo world
 */
@NullMarked
public class LimboWorldFall implements Listener {

	private static final Set<UUID> PENDING = new HashSet<>();
	private static final Vector ZERO_VELOCITY = new Vector();

	@EventHandler(priority = EventPriority.LOWEST)
	public void onFall(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (player.getWorld() != Utilizer.limboWorld()) return;
		if (event.getDamageSource().getDamageType() != DamageType.OUT_OF_WORLD) return;

		event.setCancelled(true);
		player.setVelocity(ZERO_VELOCITY);
		if (TrappedNewbieAdvancements.REQUIEM_ROOT.isDone(player)) {
			if (player.getOpenInventory().getTopInventory().getType() != InventoryType.CRAFTING) return;
			if (PENDING.contains(player.getUniqueId())) return;

			if (TrappedNewbieAdvancements.BRAVE_NEW_WORLD.awardAllCriteria(player))
				removeFreeFriendshipLetters(player);

			openTPMenu(player);
		} else {
			LocationUtil.smartTeleport(player, Utilizer.limboWorld().getSpawnLocation().center(1), false);
			player.sendMessage(Mini.combine(Component.space(), TrappedNewbieFonts.WANDERING_TRADER_HEAD.mapping(), Messenger.messenger(player).getMessage("limbo.welcome.ignored")));
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		PENDING.remove(event.getPlayer().getUniqueId());
	}

	private void openTPMenu(Player player) {
		List<Item> menuItems = new ArrayList<>();
		menuItems.add(getItem(player, null));
		menuItems.add(getItem(player, player.getUniqueId()));
		FriendlyPlayers.getFriendshipData(player).getFriends()
			.forEach(uuid -> menuItems.add(getItem(player, uuid)));
		openTPMenu(player, menuItems);
	}

	public static void openTPMenu(Player player, List<Item> menuItems) {
		ItemStack background = ItemStack.of(TrappedNewbieItems.MATERIAL_AIR);
		background.setData(DataComponentTypes.ITEM_MODEL, ResourceLib.storage().getItemModelMapping(TrappedNewbie.trappedNewbieKey("gui/filled")));

		BoundItem back = BoundItem.pagedBuilder()
			.setItemProvider(viewer -> {
				var messenger = Messenger.messenger(viewer);
				var item = ItemStack.of(Material.ARROW);
				item.setData(DataComponentTypes.ITEM_MODEL, ResourceLib.storage().getItemModelMapping(TrappedNewbie.trappedNewbieKey("gui/backward")));
				item = CustomNameModifier.named(item, messenger.getMessage("gui.previous_page"));
				return new ItemWrapper(item);
			})
			.addClickHandler((item, gui, click) -> gui.setPage(gui.getPage() - 1))
			.build();

		BoundItem forward = BoundItem.pagedBuilder()
			.setItemProvider(viewer -> {
				var messenger = Messenger.messenger(viewer);
				var item = ItemStack.of(Material.ARROW);
				item.setData(DataComponentTypes.ITEM_MODEL, ResourceLib.storage().getItemModelMapping(TrappedNewbie.trappedNewbieKey("gui/forward")));
				item = CustomNameModifier.named(item, messenger.getMessage("gui.next_page"));
				return new ItemWrapper(item);
			})
			.addClickHandler((item, gui, click) -> gui.setPage(gui.getPage() + 1))
			.build();

		Window.builder()
			.setTitle(Messenger.messenger(player).getMessage("gui.soul_travel"))
			.setUpperGui(PagedGui.itemsBuilder()
				.setStructure(
					"x x x x x x x x x",
					"x x x x x x x x x",
					"x x x x x x x x x",
					"x x x x x x x x x",
					"# # # < # > # # #"
				)
				.addIngredient('#', background)
				.addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
				.addIngredient('<', back)
				.addIngredient('>', forward)
				.setContent(menuItems)
				.build()
			)
			.setViewer(player)
			.build()
			.open();
	}

	private static Item getItem(Player player, @Nullable UUID worldOwnerUuid) {
		return Item.builder()
			.setItemProvider(viewer -> {
				var messenger = Messenger.messenger(viewer);

				ItemStack item;
				if (worldOwnerUuid == null) {
					item = ItemUtil.texturedHead(MoreMobHeads.WANDERING_TRADER);
				} else {
					item = ItemStack.of(Material.PLAYER_HEAD);
					item.setData(DataComponentTypes.PROFILE, ResolvableProfile.resolvableProfile().uuid(worldOwnerUuid).build());
				}
				if (worldOwnerUuid == null) {
					item = CustomNameModifier.named(item, messenger.getMessage("gui.soul_travel.destination.overworld"));
					item = CustomLoreModifier.lored(item, "gui.soul_travel.destination.overworld.description");
				} else {
					Component status = BookAuthorOnlineModifier.getStatus(worldOwnerUuid);
					if (status == null) status = Component.text(worldOwnerUuid.toString());
					item = CustomNameModifier.named(item, messenger.getMessage("gui.soul_travel.destination.poverworld", raw("owner", status)));
					item = CustomLoreModifier.lored(item, "gui.soul_travel.destination.poverworld.description");
				}
				item.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().addHiddenComponents(DataComponentTypes.PROFILE).build());
				return new ItemWrapper(item);
			})
			.addClickHandler(click -> {
				PENDING.add(player.getUniqueId());
				player.closeInventory();
				World world = worldOwnerUuid == null
					? Bukkit.getWorlds().getFirst()
					: PerPlayerWorlds.getResourceWorld(worldOwnerUuid, World.Environment.NORMAL);
				runTeleport(player, world, GhostyPlayer.isGhost(player))
					.thenRun(() -> PENDING.remove(player.getUniqueId()));
			})
			.build();
	}

	private void removeFreeFriendshipLetters(Player player) {
		ItemStack item;
		// TODO separate remove method, otherwise it can technically get stuck on folding
		while ((item = InventoryUtil.findItem(player, i -> i.getType() == TrappedNewbieItems.LETTER && LetterModifier.isFriendshipLetter(i))) != null)
			item.setAmount(0);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onWorldChange(PlayerTeleportEvent event) {
		if (event.getTo().getWorld() != Utilizer.limboWorld()) return;
		if (event.getFrom().getWorld() == Utilizer.limboWorld()) return;

		event.setTo(Utilizer.limboWorld().getSpawnLocation().center(1));
	}

	/**
	 * Teleports the player to a spawn location in the world
	 *
	 * @param player player
	 * @param world world
	 */
	public static CompletableFuture<Void> runTeleport(Player player, World world, boolean leap) {
		player.setVelocity(ZERO_VELOCITY);
		var teleported = new CompletableFuture<@Nullable Void>();
		if (world.key().value().startsWith("worlds-resources/")) {
			List<Player> players = world.getPlayers();
			if (!players.isEmpty()) {
				Player random = MathUtil.getRandom(players);
				LocationUtil.smartTeleport(player, random.getLocation().toHighestLocation(HeightMap.MOTION_BLOCKING).addY(120), false)
					.thenRun(() -> {
						player.closeInventory();
						player.setVelocity(ZERO_VELOCITY);
						player.setFallDistance(0F);
						if (leap)
							FreeFall.startLeaping(player);
						teleported.complete(null);
					});
				return teleported;
			}
		}
		LocationUtil.runRtp(player, world, GhostsPhaseThroughWalls.RTP_RANGE, false)
			.thenRun(() -> {
				player.closeInventory();
				Entity vehicle = player.getVehicle();
				player.setFallDistance(0F);
				if (vehicle == null) {
					if (leap)
						FreeFall.startLeaping(player);
				} else {
					vehicle.setFallDistance(0F);
					Location loc = player.getLocation().toHighestLocation().center(1);
					LocationUtil.smartTeleport(player, loc, false);
				}
				teleported.complete(null);
			});
		return teleported;
	}

}
