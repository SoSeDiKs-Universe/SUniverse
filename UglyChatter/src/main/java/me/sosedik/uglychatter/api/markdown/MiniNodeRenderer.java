package me.sosedik.uglychatter.api.markdown;

import org.commonmark.node.Document;
import org.commonmark.node.Emphasis;
import org.commonmark.node.HardLineBreak;
import org.commonmark.node.Link;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.Text;
import org.commonmark.renderer.markdown.CoreMarkdownNodeRenderer;
import org.commonmark.renderer.markdown.MarkdownNodeRendererContext;
import org.commonmark.renderer.markdown.MarkdownWriter;
import org.jetbrains.annotations.NotNull;

/**
 * Builds on top of markdown renderer to output minimessage
 */
public class MiniNodeRenderer extends CoreMarkdownNodeRenderer {

	private final MarkdownWriter writer;

	public MiniNodeRenderer(@NotNull MarkdownNodeRendererContext context) {
		super(context);
		this.writer = context.getWriter();
	}

	@Override
	public void visit(@NotNull Document document) {
		visitChildren(document);
		// Omit newline in the end
	}

	@Override
	public void visit(@NotNull Text text) {
		writer.raw(text.getLiteral());
	}

	@Override
	public void visit(@NotNull HardLineBreak hardLineBreak) {
		writer.raw("  <br>");
	}

	@Override
	public void visit(@NotNull SoftLineBreak softLineBreak) {
		writer.raw("<br>");
	}

	@Override
	public void visit(@NotNull Emphasis emphasis) {
		writer.raw("<i>");
		visitChildren(emphasis);
		writer.raw("</i>");
	}

	@Override
	public void visit(@NotNull StrongEmphasis strongEmphasis) {
		writer.raw("<b>");
		visitChildren(strongEmphasis);
		writer.raw("</b>");
	}

	@Override
	public void visit(@NotNull Link link) {
		writer.raw("<link:'");
		visitChildren(link);
		writer.raw("':'");
		writer.raw(link.getDestination());
		writer.raw("'>");
	}

}
