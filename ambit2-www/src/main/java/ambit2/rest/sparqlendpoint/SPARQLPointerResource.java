package ambit2.rest.sparqlendpoint;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.rest.StringConvertor;
import ambit2.rest.algorithm.CatalogHTMLReporter;
import ambit2.rest.algorithm.CatalogResource;
import ambit2.rest.reporters.CatalogURIReporter;

public class SPARQLPointerResource extends CatalogResource<String> {
	public static final String resource = "/sparqlendpoint";
	protected List<String> pointers = new ArrayList<String>();
	
	public SPARQLPointerResource() {
		super();
		
	}
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		getVariants().clear();
		customizeVariants(new MediaType[] {
				MediaType.TEXT_PLAIN,
				MediaType.TEXT_URI_LIST,
				});
		
		
	}
	
	
	@Override
	protected synchronized Iterator<String> createQuery(Context context, Request request,
			Response response) throws ResourceException {
		if (pointers.size()==0)
			pointers.add(String.format("%s/ontology",getRequest().getRootRef()));
		return pointers.iterator();
	}

	@Override
	public IProcessor<Iterator<String>, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {
		MediaType mime = variant.getMediaType().equals(MediaType.TEXT_URI_LIST)?MediaType.TEXT_URI_LIST:MediaType.TEXT_PLAIN;
			return new StringConvertor(	new CatalogURIReporter<String>(getRequest()) {
				@Override
				public void processItem(String src, Writer output) {
					super.processItem(src, output);
					try {
					output.write('\n');
					} catch (Exception x) {}
				}
				@Override
				public String getURI(String item) {

					return item;
				}
				@Override
				public String getURI(String ref, String item) {

					return item;
				}
			},mime);
			

		
	}
}
