package me.sosedik.utilizer.impl.message.tag;

import me.sosedik.utilizer.util.ChatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

@NullMarked
public class PluralTag {

	private PluralTag() {}

	public static final TagResolver PLURALS = TagResolver.resolver("plural", PluralTag::create);

	// Example: <plural:english:apple|apples:10>
	static Tag create(final ArgumentQueue args, final Context ctx) {
		String pluralTypeRaw = args.popOr("Missing plural type").value();
		String words = args.popOr("Missing plural words").value();
		String numRaw = args.popOr("Missing tag to parse count").value();

		try {
			PluralType pluralType = PluralType.getByName(pluralTypeRaw);
			String[] plurals = words.split("\\|");
			numRaw = ChatUtil.getPlainText(ctx.deserialize(numRaw));
			long num = Long.parseLong(numRaw);
			return Tag.selfClosingInserting(getPlural(pluralType, num, plurals));
		} catch (Exception e) {
			throw ctx.newException("Something went wrong while parsing plurals!", e, args);
		}
	}

	private static Component getPlural(PluralType pluralType, long num, String[] plurals) {
		return Component.text(plurals[pluralType.getPluralWordFormIdx(num)]);
	}

	/**
	 * Formulas source:
	 * <a href="http://docs.translatehouse.org/projects/localization-guide/en/latest/l10n/pluralforms.html">Plural forms</a>
	 */
	public enum PluralType {

		NON_PLURAL(n -> 0),
		ARABIC(n -> {
			if (n == 0) return 0;
			if (n == 1) return 1;
			if (n == 2) return 2;
			long hundreds = n % 100;
			if (hundreds >= 3 && hundreds <= 10) return 3;
			if (hundreds >= 11) return 4;
			return 5;
		}),
		ENGLISH(n -> n != 1 ? 1 : 0),
		FRENCH(n -> n > 1 ? 1 : 0),
		RU_UK(n -> {
			long tens = n % 10;
			long hundreds = n % 100;
			if (tens == 1 && hundreds != 11) return 0;
			if (tens >= 2 && tens <= 4 && (hundreds < 10 || hundreds >= 20)) return 1;
			return 2;
		});

		private static final Map<String, PluralType> BY_NAME = new HashMap<>();

		static {
			for (PluralType pluralType : PluralType.values())
				BY_NAME.put(pluralType.name(), pluralType);
			BY_NAME.put("en_us".toUpperCase(Locale.US), ENGLISH);
			BY_NAME.put("ru_ru".toUpperCase(Locale.US), RU_UK);
			BY_NAME.put("uk_ua".toUpperCase(Locale.US), RU_UK);
		}

		public static PluralType getByName(String pluralName) {
			return BY_NAME.getOrDefault(pluralName.toUpperCase(Locale.US), PluralType.NON_PLURAL);
		}

		private final Function<Long, Integer> formula;

		PluralType(Function<Long, Integer> formula) {
			this.formula = formula;
		}

		public int getPluralWordFormIdx(long n) {
			return formula.apply(Math.abs(n));
		}

	}

}
