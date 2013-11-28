package ambit2.rest.substance.composition;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.relation.STRUCTURE_RELATION;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.substance.ReadSubstance;
import ambit2.db.substance.relation.ReadSubstanceComposition;
import ambit2.rest.OpenTox;
import ambit2.rest.OutputWriterConvertor;
import ambit2.rest.query.QueryResource;
import ambit2.rest.substance.SubstanceResource;

public class SubstanceCompositionResource<Q extends IQueryRetrieval<CompositionRelation>> extends QueryResource<Q,CompositionRelation> { 
	protected STRUCTURE_RELATION relation = STRUCTURE_RELATION.HAS_CONSTITUENT;
	public final static String composition = OpenTox.URI.composition.getURI();
	public final static String idcomposition = OpenTox.URI.composition.getKey();
	public final static String compositionID = OpenTox.URI.composition.getResourceID();
	
	public SubstanceCompositionResource() {
		super();
		setHtmlbyTemplate(true);
	}

	@Override
	public String getTemplateName() {
		return "composition.ftl";
	}
	@Override
	public IProcessor<Q, Representation> createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		/* workaround for clients not being able to set accept headers */
		Form acceptform = getResourceRef(getRequest()).getQueryAsForm();
		String media = acceptform.getFirstValue("accept-header");
		if (media != null) variant.setMediaType(new MediaType(media));

		String filenamePrefix = getRequest().getResourceRef().getPath();
		/*
		if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			QueryURIReporter r = (QueryURIReporter)getURIReporter();
			r.setDelimiter("\n");
			return new StringConvertor(
					r,MediaType.TEXT_URI_LIST,filenamePrefix);
		} else 
		*/
		if (variant.getMediaType().equals(MediaType.APPLICATION_JAVASCRIPT)) {
			String jsonpcallback = getParams().getFirstValue("jsonp");
			if (jsonpcallback==null) jsonpcallback = getParams().getFirstValue("callback");
			SubstanceCompositionJSONReporter cmpreporter = new SubstanceCompositionJSONReporter(getRequest(),getDocumentation(),jsonpcallback);
			return new OutputWriterConvertor<CompositionRelation, Q>(
					cmpreporter,
					MediaType.APPLICATION_JAVASCRIPT,filenamePrefix);
		} else { //json by default
			SubstanceCompositionJSONReporter cmpreporter = new SubstanceCompositionJSONReporter(getRequest(),getDocumentation(),null);
			return new OutputWriterConvertor<CompositionRelation, Q>(
					cmpreporter,
					MediaType.APPLICATION_JSON,filenamePrefix);
		}
	}	
	@Override
	protected Q createQuery(Context context, Request request, Response response)
			throws ResourceException {
		Object key = request.getAttributes().get(SubstanceResource.idsubstance);
		Object cmp = request.getAttributes().get(idcomposition);
		if (key==null) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		} else {
			STRUCTURE_RELATION relation = null;
			try {
				if (cmp!=null)
					relation = STRUCTURE_RELATION.valueOf(cmp.toString());
			} catch (Exception x) { relation = null;}
			ReadSubstanceComposition q = null;
			try {
				q = new ReadSubstanceComposition();
				q.setValue(new SubstanceRecord(Integer.parseInt(key.toString())));
				q.setFieldname(relation);
				return (Q)q;
			} catch (Exception x) {
				int len = key.toString().trim().length(); 
				if ((len > 40) && (len <=45)) {
					SubstanceRecord record = new SubstanceRecord();
					record.setCompanyUUID(key.toString());
					q = new ReadSubstanceComposition();
					q.setValue(record);
					q.setFieldname(relation);
					return (Q)q;
				}	
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			}
			
		}

	}
	
}
