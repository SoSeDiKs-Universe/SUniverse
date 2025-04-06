package me.sosedik.utilizer.impl.message.tag;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Parses {@code <random_color>} into a random default color, excluding black
 */
@NullMarked
public class RandomColorTag {

	private RandomColorTag() {}

	public static final TagResolver RANDOM_COLOR = TagResolver.resolver(Set.of("random_color", "random_colour"), RandomColorTag::create);

	private static final Random RANDOM = new Random();
	private static final List<NamedTextColor> COLORS = new ArrayList<>(NamedTextColor.NAMES.values());

	static {
		COLORS.remove(NamedTextColor.BLACK); // Ignoring black color due to its bad visibility
	}

	static Tag create(ArgumentQueue args, Context ctx) {
		return Tag.styling(getColor());
	}

	private static NamedTextColor getColor() {
		return COLORS.get(RANDOM.nextInt(COLORS.size()));
	}

}
