package ambit2.rest.property.annotations;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.data.PropertyAnnotation;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.update.propertyannotations.ReadPropertyAnnotations;
import ambit2.rest.OutputWriterConvertor;
import ambit2.rest.RDFJenaConvertor;
import ambit2.rest.ResourceDoc;
import ambit2.rest.StringConvertor;
import ambit2.rest.error.InvalidResourceIDException;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.query.QueryResource;

public class PropertyAnnotationResource  extends QueryResource<IQueryRetrieval<PropertyAnnotation>, PropertyAnnotation> {
	public final static String annotation = "/annotation";
	public PropertyAnnotationResource() {
		super();
		setDocumentation(new ResourceDoc("Feature","Feature"));
	}
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] {
				MediaType.TEXT_HTML,
				MediaType.APPLICATION_RDF_XML,
				MediaType.APPLICATION_RDF_TURTLE,
				MediaType.TEXT_RDF_N3,
				MediaType.TEXT_RDF_NTRIPLES,
//				MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JAVA_OBJECT
				});	
	}
	
	@Override
	public IProcessor<IQueryRetrieval<PropertyAnnotation>, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {

		if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
				PropertyAnnotationURIReporter r = new PropertyAnnotationURIReporter(getRequest(),getDocumentation());
				r.setDelimiter("\n");
				return new StringConvertor(r,MediaType.TEXT_URI_LIST);
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML) ||
				variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE) ||
				variant.getMediaType().equals(MediaType.TEXT_RDF_N3) ||
				variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES) || 
				variant.getMediaType().equals(MediaType.APPLICATION_JSON)
				) {
			return new RDFJenaConvertor<PropertyAnnotation, IQueryRetrieval<PropertyAnnotation>>(
					new PropertyAnnotationRDFReporter(getRequest(),variant.getMediaType(),getDocumentation())
					,variant.getMediaType());		
		} else 
			return new OutputWriterConvertor(
					new PropertyAnnotationHTMLReporter(getRequest(),false,getDocumentation())
					,MediaType.TEXT_HTML);
	}	

	@Override
	protected IQueryRetrieval<PropertyAnnotation> createQuery(Context context,
			Request request, Response response) throws ResourceException {
		Object o = request.getAttributes().get(PropertyResource.idfeaturedef);
		if (o==null) throw new InvalidResourceIDException(request.getResourceRef());
		try {
			int id = Integer.parseInt(o.toString());
			ReadPropertyAnnotations q = new ReadPropertyAnnotations();
			Property p = Property.getInstance("", "");
			p.setId(id);
			q.setFieldname(p);
			return q;
		} catch (NumberFormatException x) {
			throw new InvalidResourceIDException(o);
		} catch (Exception x) {
			throw new InvalidResourceIDException(x);
		} finally {
		}		
		
	}

}
