package ambit2.rest.algorithm;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.rest.AbstractResource;
import ambit2.rest.StatusException;
import ambit2.rest.StringConvertor;

/**
 * Algorithms as per http://opentox.org/development/wiki/Algorithms
 * @author nina
 *
 */
public class AlgorithmCatalogResource<T> extends AbstractResource<Iterator<T>,T,IProcessor<Iterator<T>, Representation>> {
	public final static String algorithm = "/algorithm";	
	public final static String algorithmKey =  "algorithm_id";

	public enum algorithmtypes  {
		util,preprocessing,clustering,descriptorcalculation,learning,rules
	};
	
	protected String category = "";
	public AlgorithmCatalogResource(Context context, Request request, Response response) {
		super(context,request,response);
		this.getVariants().add(new Variant(MediaType.TEXT_HTML));
		this.getVariants().add(new Variant(MediaType.TEXT_XML));
		this.getVariants().add(new Variant(MediaType.TEXT_URI_LIST));		

	}
	@Override
	protected Iterator<T> createQuery(Context context, Request request,
			Response response) throws StatusException {
		ArrayList<T> q = new ArrayList<T>();
		for (algorithmtypes d : algorithmtypes.values())
			q.add((T)String.format("%s/%s","algorithm",d.toString()));	
		return q.iterator();
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public static String getAlgorithmURI(String category) {
		return String.format("%s%s/{%s}",algorithm,category,algorithmKey);
	}
	@Override
	public IProcessor<Iterator<T>, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {
		/*
		if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
			return new DocumentConvertor(new DatasetsXMLReporter(getRequest().getRootRef()));	
			*/
		if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
			return new StringConvertor(
					new CatalogHTMLReporter<T>(getRequest().getRootRef()),MediaType.TEXT_HTML);
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
		
			return new StringConvertor(	new CatalogURIReporter<T>(getRequest().getRootRef()) {
				@Override
				public void processItem(T src, Writer output) {
					super.processItem(src, output);
					try {
					output.write('\n');
					} catch (Exception x) {}
				}
			},MediaType.TEXT_URI_LIST);
			
		} else //html 	
			return new StringConvertor(
					new CatalogHTMLReporter<T>(getRequest().getRootRef()),MediaType.TEXT_HTML);
		
	}
	
}
