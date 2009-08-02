package ambit2.rest.reference;

import java.io.Writer;

import org.restlet.data.Reference;

import ambit2.base.data.LiteratureEntry;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.QueryURIReporter;

public class ReferenceHTMLReporter extends QueryHTMLReporter<LiteratureEntry, IQueryRetrieval<LiteratureEntry>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7959033048710547839L;

	public ReferenceHTMLReporter() {
		this(null,true);
	}
	public ReferenceHTMLReporter(Reference baseRef, boolean collapsed) {
		super(baseRef,collapsed);
	}
	@Override
	protected QueryURIReporter createURIReporter(Reference reference) {
		return new ReferenceURIReporter<IQueryRetrieval<LiteratureEntry>>(reference);
	}
	@Override
	public void header(Writer w, IQueryRetrieval<LiteratureEntry> query) {
		super.header(w, query);
		try {w.write(String.format("<h3>Reference%s</h3>",collapsed?"s":""));} catch (Exception x) {}
	}
	@Override
	public void processItem(LiteratureEntry item, Writer output) {
		try {
			output.write("<div>");
			output.write("Name: ");
			output.write(String.format(
						"<a href=\"%s\">%s</a>",
						uriReporter.getURI(item),
						item.getName()));
			if (!collapsed) {
				output.write("&nbsp;");
				output.write("URL: ");
				output.write(String.format(
						"<a href=\"%s\" target='_blank'>%s</a><br>",
						item.getURL(),
						item.getURL()));	
			}
			output.write("</div>");	
		} catch (Exception x) {
			
		}
	}

}
