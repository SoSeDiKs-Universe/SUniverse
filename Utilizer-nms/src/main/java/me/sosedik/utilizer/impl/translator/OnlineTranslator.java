package me.sosedik.utilizer.impl.translator;

import me.sosedik.utilizer.Utilizer;
import me.sosedik.utilizer.api.language.translator.TranslationLanguage;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jspecify.annotations.NullMarked;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Uses Google translate
 */
@NullMarked
public class OnlineTranslator {

	private static final Pattern TRANSLATION_RESULT = Pattern.compile("class=\"result-container\">([^<]*)</div>", Pattern.MULTILINE);

	private static boolean failedToTranslate = false;

	/**
	 * Translates the text from one language to another
	 *
	 * @param textToTranslate text to translate
	 * @param translateFrom language to translate from
	 * @param translateTo language to translate to
	 * @return translated text
	 */
	@SuppressWarnings("deprecation")
	public static String translate(String textToTranslate, TranslationLanguage translateFrom, TranslationLanguage translateTo) {
		if (failedToTranslate) return textToTranslate;
		if (textToTranslate.isBlank()) return textToTranslate;

		try {
			String pageSource = "";
			try {
				pageSource = getPageSource(textToTranslate, translateFrom.id(), translateTo.id());
				Matcher matcher = TRANSLATION_RESULT.matcher(pageSource);
				if (matcher.find()) {
					String match = matcher.group(1);
					if (match != null && !match.isEmpty()) {
						return StringEscapeUtils.unescapeHtml4(match);
					}
				}
				throw new TranslatorException("Could not translate \"" + textToTranslate + "\": result page couldn't be parsed");
			} catch (SocketTimeoutException | UnknownHostException e) {
				failedToTranslate = true;
				Utilizer.scheduler().async(() -> failedToTranslate = false, 60 * 20L);
				Utilizer.logger().warn("[Translator] Connection timed out, disabling for a minute");
				return textToTranslate;
			} catch (Exception e) {
				try {
					Path p = Files.createTempFile("translator-pagedump", ".html").toAbsolutePath();
					Files.writeString(p, pageSource);
					throw new TranslatorException("Could not translate string, see dumped page at " + p, e);
				} catch (IOException ioe) {
					throw new TranslatorException("Could not translate string, and the page could not be dumped", ioe);
				}
			}
		} catch (TranslatorException e) {
			e.printStackTrace();
			failedToTranslate = true;
			Utilizer.scheduler().async(() -> failedToTranslate = false, 60 * 60 * 20L);
			return textToTranslate;
		}
	}

	private static String getPageSource(String textToTranslate, String translateFrom, String translateTo) throws IOException, TranslatorException {
		String pageUrl = String.format("https://translate.google.com/m?hl=en&sl=%s&tl=%s&ie=UTF-8&prev=_m&q=%s", translateFrom, translateTo, URLEncoder.encode(textToTranslate.trim(), StandardCharsets.UTF_8));
		URL url;
		try {
			url = new URI(pageUrl).toURL();
		} catch (URISyntaxException e) {
			throw new TranslatorException("Couldn't parse page url", e);
		}
		HttpURLConnection connection = null;
		StringBuilder pageSource = new StringBuilder();
		try {
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(2000);
			connection.setReadTimeout(2000);
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			try (var bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					pageSource.append(line).append('\n');
				}
			}
			return pageSource.toString();
		} finally {
			if (connection != null)
				connection.disconnect();
		}
	}

}
