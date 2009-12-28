package ambit2.rest.reference;

import org.restlet.Request;

import ambit2.base.data.ILiteratureEntry;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.QueryURIReporter;

/**
 * Generates URI for {@link ReferenceResource}
 * @author nina
 *
 * @param <Q>
 */
public class ReferenceURIReporter <Q extends IQueryRetrieval<ILiteratureEntry>> extends QueryURIReporter<ILiteratureEntry, Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8868430033131766579L;
	public ReferenceURIReporter(Request baseRef) {
		super(baseRef);
	}
	public ReferenceURIReporter() {
		this(null);
	}	

	@Override
	public String getURI(String ref, ILiteratureEntry item) {
		return String.format("%s%s/%d",ref,ReferenceResource.reference,item.getId());
	}

}
