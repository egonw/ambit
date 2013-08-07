package ambit2.rest.substance;

import org.restlet.Request;

import ambit2.base.data.SubstanceRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.OpenTox;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.ResourceDoc;

public class SubstanceURIReporter<Q extends IQueryRetrieval<SubstanceRecord>> extends QueryURIReporter<SubstanceRecord, Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3811264823419677254L;

	public SubstanceURIReporter(Request request,ResourceDoc doc) {
		super(request,doc);
	}
	@Override
	public String getURI(String ref, SubstanceRecord item) {
		if (item.getIdsubstance()>0)
			return String.format("%s%s/%d", ref,OpenTox.URI.substance.getURI(),item.getIdsubstance());
		else
			return String.format("%s%s/%s", ref,OpenTox.URI.substance.getURI(),item.getI5UUID());
	}

}