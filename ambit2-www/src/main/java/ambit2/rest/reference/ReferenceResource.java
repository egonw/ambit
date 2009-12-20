package ambit2.rest.reference;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.update.AbstractUpdate;
import ambit2.db.update.reference.CreateReference;
import ambit2.db.update.reference.ReadReference;
import ambit2.rest.DocumentConvertor;
import ambit2.rest.OutputWriterConvertor;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.RDFJenaConvertor;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.propertyvalue.PropertyValueReporter;
import ambit2.rest.query.QueryResource;
import ambit2.rest.rdf.RDFObjectIterator;
import ambit2.rest.rdf.RDFReferenceIterator;

/**
 * Reference resource, inOpenTox API coressponds to feature.hasSource
 * <br>
 * Supported operations:
 * <ul>
 * <li>GET 	 /reference/{id} 	 returns returns text/uri-list or text/xml or text/html
 * <li>POST 	 /reference?source_uri=URI-to-denote-the-reference 
 * <li>PUT not yet supported
 * </ul>
 * @author nina
 *
 * @param <Q>
 */
public class ReferenceResource	extends QueryResource<ReadReference,ILiteratureEntry> {

	public final static String reference = "/reference";
	public final static String idreference = "idreference";
	

	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		if (variant.getMediaType().equals(MediaType.TEXT_PLAIN)) {
			
			return new StringConvertor(new PropertyValueReporter());
			} else if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
				return new DocumentConvertor(new ReferenceDOMReporter(getRequest()));
			} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
				return new StringConvertor(	new ReferenceURIReporter<IQueryRetrieval<ILiteratureEntry>>(getRequest()) {
					@Override
					public void processItem(ILiteratureEntry dataset) throws AmbitException  {
						super.processItem(dataset);
						try {
							output.write('\n');
						} catch (Exception x) {}
					}
				},MediaType.TEXT_URI_LIST);
			} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML) ||
					variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE) ||
					variant.getMediaType().equals(MediaType.TEXT_RDF_N3) ||
					variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES)
					) {
				return new RDFJenaConvertor<ILiteratureEntry, IQueryRetrieval<ILiteratureEntry>>(
						new ReferenceRDFReporter<IQueryRetrieval<ILiteratureEntry>>(getRequest(),variant.getMediaType())
						,variant.getMediaType());					
			} else 
				return new OutputWriterConvertor(
						new ReferenceHTMLReporter(getRequest(),queryObject.getValue()==null),
						MediaType.TEXT_HTML);
	}

	@Override
	protected ReadReference createQuery(Context context, Request request, Response response)
			throws ResourceException {
		Object idref = request.getAttributes().get(idreference);
		try {
			if (idref==null) {
				/*
				Form form = request.getResourceRef().getQueryAsForm();
				Object key = form.getFirstValue(QueryResource.search_param);
				if (key != null) {
					RetrieveFieldNamesByAlias q = new RetrieveFieldNamesByAlias(Reference.decode(key.toString()));
					q.setCondition(StringCondition.getInstance(StringCondition.C_SOUNDSLIKE));
					return q;
				} else 
				*/
					return new ReadReference();
			}			
			else return new ReadReference(new Integer(Reference.decode(idref.toString())));
		} catch (Exception x) {
			throw new ResourceException(
					Status.CLIENT_ERROR_BAD_REQUEST,
					String.format("Invalid resource id %d",idref),
					x
					);
		}
	} 
	@Override
	protected Representation post(Representation entity)
			throws ResourceException {
		if (getRequest().getAttributes().get(idreference)==null)
			createNewObject(entity);
		else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		return getResponse().getEntity();
	}
	@Override
	protected QueryURIReporter<ILiteratureEntry, ReadReference> getURUReporter(
			Request baseReference) throws ResourceException {
		return new ReferenceURIReporter<ReadReference>(baseReference);
	}
	@Override
	protected RDFObjectIterator<ILiteratureEntry> createObjectIterator(
			Reference reference, MediaType mediaType) throws ResourceException {
		return new RDFReferenceIterator(reference,mediaType);
	}
	@Override
	protected RDFObjectIterator<ILiteratureEntry> createObjectIterator(
			Representation entity) throws ResourceException {
		return new RDFReferenceIterator(entity,entity.getMediaType());
	}
	@Override
	protected LiteratureEntry onError(String sourceURI) {
		return LiteratureEntry.getInstance(sourceURI,sourceURI);	
	}

	@Override
	protected AbstractUpdate createUpdateObject(
			ILiteratureEntry entry) throws ResourceException {
		return new CreateReference(entry);
	}
}
