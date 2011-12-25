package ambit2.rest.facet;

import java.io.Writer;

import org.restlet.Request;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.facet.IFacet;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.AmbitResource;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.ResourceDoc;

public class FacetHTMLReporter<Facet extends IFacet> extends QueryHTMLReporter<Facet, IQueryRetrieval<Facet>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7959033048710547839L;

	public FacetHTMLReporter() {
		this(null);
	}
	public FacetHTMLReporter(Request baseRef) {
		super(baseRef,false,null);
	}
	@Override
	protected QueryURIReporter createURIReporter(Request request, ResourceDoc doc) {
		return new FacetURIReporter(request);
	}
	@Override
	public void header(Writer w, IQueryRetrieval<Facet> query) {
		super.header(w, query);
		
		try {
			headerBeforeTable(w,query);
			w.write(AmbitResource.jsTableSorter("facet","pager"));
			w.write(String.format("<table class='tablesorter' id='facet' border=\"0\" cellpadding=\"0\" cellspacing=\"1\""));
			w.write(String.format("<caption CLASS=\"zebra\">Summary</caption>",query.toString()));
			w.write(String.format("<thead><th>%s</th><th>Count</th><th></th></thead>",query.toString()));
			w.write("<tbody>");
			
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
	public void headerBeforeTable(Writer w, IQueryRetrieval<Facet> query) {
		
	}
	@Override
	public void footer(Writer w, IQueryRetrieval<Facet> query) {
		try {
			w.write("</tbody>");
			w.write(String.format("</table>"));
		} catch (Exception x) {}
		super.footer(w, query);
	}	
	@Override
	public Object processItem(Facet item) throws AmbitException  {
		try {
			output.write("<tr>");
			output.write("<td>");
			output.write(item.getValue().toString());
			output.write("</td>");
			
			output.write("<td>");
			String uri = uriReporter.getURI(item);
			String d = uri.indexOf("?")>0?"&":"?";
			
			output.write(String.format(
						"<a href=\"%s%spage=0&pagesize=100\">(%d)</a>",
						uri,d,
						item.getCount()));
			output.write("</td>");
			output.write("<td>");
			String subcategory = item.getSubCategoryURL(uriReporter.getBaseReference().toString());
			if (subcategory!=null)
				output.write(String.format(
							"<a href=\"%s\">%s</a>",
							subcategory,
							item.getSubcategoryTitle()==null?"Subcategory":item.getSubcategoryTitle()));
			output.write("</td>");				
			output.write("</tr>");
			
		} catch (Exception x) {
			x.printStackTrace();
		}
		return item;
	}

}
