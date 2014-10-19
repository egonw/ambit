package ambit2.rest.dataset;

import org.restlet.Request;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.ARFF3ColReporter;
import ambit2.rest.ResourceDoc;
import ambit2.rest.property.PropertyURIReporter;

public class ARFF3ColResourceReporter<Q extends IQueryRetrieval<IStructureRecord>> extends ARFF3ColReporter<Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7827748100581322937L;

	protected PropertyURIReporter reporter;

	public ARFF3ColResourceReporter(Template template, Request request,ResourceDoc doc, String urlPrefix) {
		this(template,null,request,doc,urlPrefix);
	}
	public ARFF3ColResourceReporter(Template template,Profile groupedProperties, Request request,ResourceDoc doc, String urlPrefix) {
		super(template,groupedProperties);
		setUrlPrefix(urlPrefix);
		reporter = new PropertyURIReporter(request,doc);
	}
	@Override
	protected String getRelationName() {
		return reporter.getBaseReference().toString();
	}
	@Override
	protected String getItemIdentifier(Property p) {
		return String.format("%d", p.getId());
	}

}