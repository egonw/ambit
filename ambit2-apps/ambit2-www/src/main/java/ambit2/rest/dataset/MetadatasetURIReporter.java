package ambit2.rest.dataset;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.restnet.c.ResourceDoc;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.data.ISourceDataset;

public class MetadatasetURIReporter<Q extends IQueryRetrieval<M>,M extends ISourceDataset> extends DatasetURIReporter<Q,M> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6706277580631778672L;

	public MetadatasetURIReporter(Request baseRef,ResourceDoc doc) {
		super(baseRef,doc);
	}
	public MetadatasetURIReporter(Reference baseRef,ResourceDoc doc) {
		super(baseRef,doc);
	}	
	public MetadatasetURIReporter() {
		super();
	}	
	@Override
	public String getURI(String ref, M dataset) {
		return String.format("%s/metadata",super.getURI(ref, dataset));
	}
}
