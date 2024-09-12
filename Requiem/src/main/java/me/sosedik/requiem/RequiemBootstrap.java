package me.sosedik.requiem;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.event.RegistryEvents;
import me.sosedik.requiem.dataset.RequiemItems;
import me.sosedik.requiem.effect.AttritionEffect;
import me.sosedik.requiem.effect.ParasitesEffect;
import me.sosedik.resourcelib.ResourceLibBootstrap;
import me.sosedik.resourcelib.util.ItemCreator;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.potion.PotionEffectType;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

public class RequiemBootstrap implements PluginBootstrap {

	@Override
	public void bootstrap(@NotNull BootstrapContext context) {
		context.getLifecycleManager().registerEventHandler(RegistryEvents.MOB_EFFECT.freeze(), event -> {
			event.registry().register(potionEffectKey("parasites"), b -> b
					.category(PotionEffectType.Category.HARMFUL)
					.color(NamedTextColor.GOLD.value())
					.wrapper(new ParasitesEffect())
			);
			event.registry().register(potionEffectKey("attrition"), b -> b
					.category(PotionEffectType.Category.NEUTRAL)
					.color(NamedTextColor.WHITE.value())
					.wrapper(new AttritionEffect())
			);
		});

		ResourceLibBootstrap.setupItems(context, RequiemItems.class, (key, b) -> {
			switch (key) {
				case "ghost_motivator",
				     "ghost_relocator" -> b.nmsItemFunction(ItemCreator::bowItem);
			}
		});
	}

	private static @NotNull TypedKey<PotionEffectType> potionEffectKey(@Subst("key") @NotNull String key) {
		return TypedKey.create(RegistryKey.MOB_EFFECT, Requiem.requiemKey(key));
	}

}
