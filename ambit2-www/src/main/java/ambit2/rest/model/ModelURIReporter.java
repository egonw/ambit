package ambit2.rest.model;

import java.io.Writer;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.ResourceDoc;

public class ModelURIReporter<Q extends IQueryRetrieval<ModelQueryResults>> extends QueryURIReporter<ModelQueryResults, Q> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3648376868814044783L;

	public ModelURIReporter(Request baseRef,ResourceDoc doc) {
		super(baseRef,doc);
	}
	public ModelURIReporter(Request baseRef) {
		super(baseRef,null);
	}
	public ModelURIReporter() {
		super();
	}	
	@Override
	public String getURI(String ref, ModelQueryResults model) {
		return
		String.format("%s%s/%s", 
				ref,
				ModelResource.resource,model==null?"":Reference.encode(model.getQueryID().toString()));
	}
	public void footer(Writer output, Q query) {};
	public void header(Writer output, Q query) {};
	
	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
}	