package me.sosedik.resourcelib.impl.message.tag;

import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.util.SpacingUtil;
import me.sosedik.utilizer.api.message.Mini;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ItemTag {

	private ItemTag() {}

	public static final TagResolver ITEM = TagResolver.resolver("item", ItemTag::create);

	static Tag create(ArgumentQueue args, Context ctx) {
		if (!args.hasNext())
			throw ctx.newException("Can't turn " + args + " into icon", args);

		String[] keys = args.pop().lowerValue().split(";");

		Component icon;
		if (keys.length == 1) {
			icon = ResourceLib.getItemIcon(Key.key(keys[0]));
		} else {
			Component[] icons = new Component[keys.length];
			for (int i = 0; i < keys.length; i++)
				icons[i] = ResourceLib.getItemIcon(Key.key(keys[i]));
			icon = Mini.combine(SpacingUtil.getSpacing(1), icons);
		}

		if (!args.hasNext())
			return Tag.selfClosingInserting(icon);

		Component text = ctx.deserialize(args.pop().value());
		return Tag.selfClosingInserting(Component.textOfChildren(icon, SpacingUtil.getSpacing(4), text));
	}

}
