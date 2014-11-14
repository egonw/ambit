package ambit2.rest.substance.study;

import java.io.Writer;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.r.QueryReporter;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.facet.IFacet;
import ambit2.base.json.JSONUtils;
import ambit2.db.substance.study.facet.SubstanceStudyFacet;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.facet.FacetURIReporter;


public class StudySummaryJSONReporter<Q extends IQueryRetrieval<IFacet>> extends QueryReporter<IFacet, Q, Writer> {
	protected QueryURIReporter<IFacet, IQueryRetrieval<IFacet>> uriReporter;
	protected String jsonp = null;
	protected String comma = null;
	
	public StudySummaryJSONReporter(Request baseRef) {
		this(baseRef,null);
	}
	public StudySummaryJSONReporter(Request baseRef, String jsonp) {
		super();
		uriReporter = new FacetURIReporter<IQueryRetrieval<IFacet>>(baseRef);
		this.jsonp = jsonp;
	}
	@Override
	public void open() throws DbAmbitException {
		
	}

	@Override
	public void header(Writer output, Q query) {
		try {
			if (jsonp!=null) {
				output.write(jsonp);
				output.write("(");
			}
			output.write("{\"facet\": [");
				//"Name,Count,URI,Subcategory\n");
		} catch (Exception x) {}
		
	}

	@Override
	public void footer(Writer output, Q query) {
		try {
			output.write("\n]\n}");
			
			if (jsonp!=null) {
				output.write(");");
			}
			output.flush();
			} catch (Exception x) {}
	}

	private String printOrder(IFacet item) {
		try {
			if (item instanceof SubstanceStudyFacet) {
				SubstanceStudyFacet ssf = (SubstanceStudyFacet)item;
				return Integer.toString(ssf.getSortingOrder());
			} 
		} catch (Exception x) { }
		return "null";
	}

	@Override
	public Object processItem(IFacet item) throws AmbitException {
		try {
			if (comma!=null) getOutput().write(comma);

			if ((uriReporter!=null) && (uriReporter.getBaseReference()!=null))
				output.write(String.format("\n\t{\n\t\"topcategory\": {\n\t\t\t\"title\": "));
				output.write(item.getValue()==null?"null":JSONUtils.jsonQuote(JSONUtils.jsonEscape(item.getValue().toString())));
				output.write(",\n\t\t\t\"uri\":");
				output.write(item==null?"null":JSONUtils.jsonQuote(JSONUtils.jsonEscape(
						uriReporter.getURI(item)+
						"?top="+
						(item.getValue()==null?"":Reference.encode(item.getValue().toString()))
						)));
				
				output.write("\n\t},\n\t\"category\":  {\n\t\t\t\"title\":");
				output.write(item.getSubcategoryTitle()==null?"null":JSONUtils.jsonQuote(JSONUtils.jsonEscape(item.getSubcategoryTitle())));

				output.write(",\n\t\t\t\"description\":");
				output.write(printOrder(item));
				//change description to order, but leave the former for compatibility
				output.write(",\n\t\t\t\"order\":");
				output.write(printOrder(item));
				
				output.write(",\n\t\t\t\"uri\":");
				String suri = item.getSubCategoryURL(
						uriReporter.getURI(item))+
						"?top="+
						(item.getValue()==null?"":Reference.encode(item.getValue().toString()))+
						"&category="+(item.getSubcategoryTitle()==null?"":Reference.encode(item.getSubcategoryTitle()));
				output.write(suri==null?"null":JSONUtils.jsonQuote(JSONUtils.jsonEscape(suri)));
				output.write("\n\t},\n\t\"count\":");
				output.write(Integer.toString(item.getCount()));
				output.write("\n\t}");
				comma = ",";
		} catch (Exception x) {
			x.printStackTrace();
			logger.log(Level.WARNING,x.getMessage(),x);
		}
		return item;
	}

}