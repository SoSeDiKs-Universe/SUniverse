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
import org.jspecify.annotations.NullMarked;

/**
 * Builds on top of markdown renderer to output minimessage
 */
@NullMarked
public class MiniNodeRenderer extends CoreMarkdownNodeRenderer {

	private final MarkdownWriter writer;

	public MiniNodeRenderer(MarkdownNodeRendererContext context) {
		super(context);
		this.writer = context.getWriter();
	}

	@Override
	public void visit(Document document) {
		visitChildren(document);
		// Omit newline in the end
	}

	@Override
	public void visit(Text text) {
		writer.raw(text.getLiteral());
	}

	@Override
	public void visit(HardLineBreak hardLineBreak) {
		writer.raw("  <br>");
	}

	@Override
	public void visit(SoftLineBreak softLineBreak) {
		writer.raw("<br>");
	}

	@Override
	public void visit(Emphasis emphasis) {
		writer.raw("<i>");
		visitChildren(emphasis);
		writer.raw("</i>");
	}

	@Override
	public void visit(StrongEmphasis strongEmphasis) {
		writer.raw("<b>");
		visitChildren(strongEmphasis);
		writer.raw("</b>");
	}

	@Override
	public void visit(Link link) {
		String destination = link.getDestination();
		if (destination.startsWith("click:open_url") || destination.startsWith("lang:")) {
			writer.raw("<" + destination + ">");
			return;
		}
		writer.raw("<link:'");
		visitChildren(link);
		writer.raw("':'");
		writer.raw(destination);
		writer.raw("'>");
	}

}
