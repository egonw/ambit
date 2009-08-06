package ambit2.rest.reference;

import java.io.Writer;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Variant;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.StringCondition;
import ambit2.db.search.property.RetrieveFieldNamesByAlias;
import ambit2.db.update.property.ReadProperty;
import ambit2.db.update.reference.ReadReference;
import ambit2.rest.DocumentConvertor;
import ambit2.rest.OutputStreamConvertor;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.StatusException;
import ambit2.rest.StringConvertor;
import ambit2.rest.error.InvalidResourceIDException;
import ambit2.rest.propertyvalue.PropertyValueReporter;
import ambit2.rest.query.QueryResource;
import ambit2.rest.structure.CompoundResource;

/**
 * Retrieves {@link LiteratureEntry}
 * @author nina
 *
 * @param <Q>
 */
public class ReferenceResource	extends QueryResource<ReadReference,LiteratureEntry> {
	public final static String reference = "/reference";
	public final static String idreference = "idreference";
	public final static String referenceID = String.format("%s/{%s}",reference,idreference);
	
	public ReferenceResource(Context context, Request request, Response response) {
		super(context,request,response);
		this.getVariants().add(new Variant(MediaType.TEXT_HTML));
		this.getVariants().add(new Variant(MediaType.TEXT_XML));
		this.getVariants().add(new Variant(MediaType.TEXT_URI_LIST));
		this.getVariants().add(new Variant(MediaType.TEXT_PLAIN));		
	
	}
	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException {
		if (variant.getMediaType().equals(MediaType.TEXT_PLAIN)) {
			
			return new StringConvertor(new PropertyValueReporter());
			} else if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
				return new DocumentConvertor(new ReferenceDOMReporter(getRequest().getRootRef()));
			} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
				return new StringConvertor(	new ReferenceURIReporter<IQueryRetrieval<LiteratureEntry>>(getRequest().getRootRef()) {
					@Override
					public void processItem(LiteratureEntry dataset, Writer output) {
						super.processItem(dataset, output);
						try {
						output.write('\n');
						} catch (Exception x) {}
					}
				},MediaType.TEXT_URI_LIST);
			} else 
				return new OutputStreamConvertor(
						new ReferenceHTMLReporter(getRequest().getRootRef(),query.getValue()==null),
						MediaType.TEXT_HTML);
	}

	@Override
	protected ReadReference createQuery(Context context, Request request, Response response)
			throws StatusException {
		Object idref = request.getAttributes().get(idreference);
		try {
			if (idref==null) {
				/*
				Form form = request.getResourceRef().getQueryAsForm();
				Object key = form.getFirstValue("search");
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
			throw new StatusException(
					new Status(Status.CLIENT_ERROR_BAD_REQUEST,x,String.format("Invalid resource id %d",idref))
					);
		}
	} 

}
