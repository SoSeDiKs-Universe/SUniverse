package me.sosedik.trappednewbie.impl.effect;

import me.sosedik.kiterino.registry.wrapper.KiterinoMobEffectBehaviourWrapper;
import me.sosedik.trappednewbie.api.item.VisualArmor;
import me.sosedik.trappednewbie.dataset.TrappedNewbieDamageTypes;
import me.sosedik.trappednewbie.listener.player.VisualArmorLayer;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import net.kyori.adventure.util.TriState;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.time.Duration;

@NullMarked
public class HotPotatoEffect implements KiterinoMobEffectBehaviourWrapper {

	@Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
		entity.setVisualFire(TriState.TRUE);
		return true;
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}

	@Override
	public void onEffectAdded(LivingEntity entity, int amplifier) {
		if (!(entity instanceof Player player)) return;

		var titleText = Component.text("🔥");
		Component subtitleText = Messenger.messenger(player).getMessage("effect.trapped_newbie.hot_potato.name");
		var title = Title.title(titleText, subtitleText, Title.Times.times(Ticks.duration(10), Duration.ofSeconds(2), Ticks.duration(10)));
		player.showTitle(title);
	}

	@Override
	public void onEffectRemoved(LivingEntity entity, int amplifier) {
		entity.setVisualFire(TriState.NOT_SET);
	}

	@Override
	public void onEffectExpired(LivingEntity entity, int amplifier) {
		if (!(entity instanceof Player player)) {
			EntityEquipment equipment = entity.getEquipment();
			if (equipment == null) return;

			ItemStack item = equipment.getItemInMainHand();
			if (item.getType() != Material.BAKED_POTATO)
				item = equipment.getItemInOffHand();

			if (item.getType() == Material.BAKED_POTATO)
				item.subtract();

			return;
		}

		if (!hasBakedPotato(player)) {
			player.playSound(player, Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 1F, 1F);
			return;
		}
		player.damage(Integer.MAX_VALUE, DamageSource.builder(TrappedNewbieDamageTypes.HOT_POTATO).build());
	}

	private boolean hasBakedPotato(Player player) {
		if (player.getInventory().contains(Material.BAKED_POTATO)) return true;
		if (player.getItemOnCursor().getType() == Material.BAKED_POTATO) return true;
		if (player.getInventory().getItemInOffHand().getType() == Material.BAKED_POTATO) return true;

		VisualArmor visualArmor = VisualArmorLayer.getVisualArmor(player);
		return visualArmor.hasHelmet() && visualArmor.getHelmet().getType() == Material.BAKED_POTATO;
	}

}
