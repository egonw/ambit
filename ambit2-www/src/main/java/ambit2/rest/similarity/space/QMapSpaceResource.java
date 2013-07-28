package ambit2.rest.similarity.space;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.simiparity.space.QMap;
import ambit2.db.simiparity.space.QMapSpace;
import ambit2.db.simiparity.space.QueryQMapSpace;
import ambit2.rest.OpenTox;
import ambit2.rest.OutputWriterConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.query.QueryResource;

public class QMapSpaceResource  extends QueryResource<IQueryRetrieval<QMapSpace>, QMapSpace> {
	public final static String resource = "/toxmatch";
	
	public QMapSpaceResource() {
		super();
		setHtmlbyTemplate(true);
	}
	@Override
	public String getTemplateName() {
		return "toxmatch.ftl";
	}
	@Override
	public IProcessor<IQueryRetrieval<QMapSpace>, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {
		String filenamePrefix = getRequest().getResourceRef().getPath();
		if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
				QMapURIReporter r = new QMapURIReporter(getRequest(),getDocumentation());
				r.setDelimiter("\n");
				return new StringConvertor(r,MediaType.TEXT_URI_LIST,filenamePrefix);
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
					return new OutputWriterConvertor(new QMapSpaceJSONReporter(getRequest()),MediaType.APPLICATION_JSON);
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_JAVASCRIPT)) {
			return new OutputWriterConvertor(new QMapSpaceJSONReporter(getRequest()),MediaType.APPLICATION_JSON);
		}			
		throw new ResourceException(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);		
	}	

	@Override
	protected IQueryRetrieval<QMapSpace> createQuery(Context context,
			Request request, Response response) throws ResourceException {
		QueryQMapSpace query = new QueryQMapSpace();
		Form form = request.getResourceRef().getQueryAsForm();
		try {
			query.setThreshold_a(Integer.parseInt(form.getFirstValue("thresholda")));
		} catch (Exception x) {query.setThreshold_a(0);}
		Object qmapURI = OpenTox.params.qmap_uri.getFirstValue(form);
		if (qmapURI!=null) try {
			int id = (Integer)OpenTox.URI.qmap.getId(Reference.decode(qmapURI.toString().trim()),request.getRootRef());
			query.setValue(new QMap(id));
			return query;
		} catch (Exception x) { /* ignore the filter	*/}

		
		//filter by dataset
		Object datasetURI = OpenTox.params.dataset_uri.getFirstValue(form);
		if (datasetURI!=null) try {
			int id = (Integer)OpenTox.URI.dataset.getId(Reference.decode(datasetURI.toString().trim()),request.getRootRef());
			QMap map = new QMap();
			map.setDataset(new SourceDataset());
			map.getDataset().setID(id);
			query.setValue(map);
		} catch (Exception x) { /* ignore the filter	*/}

		Object propertyURI = OpenTox.params.feature_uris.getFirstValue(form);
		if (propertyURI!=null) try {
			int id = (Integer)OpenTox.URI.feature.getId(Reference.decode(propertyURI.toString().trim()),request.getRootRef());
			if (query.getValue()==null) query.setValue(new QMap());
			Property p = new Property(null);
			p.setId(id);
			query.getValue().setProperty(p);
		} catch (Exception x) { /* ignore the filter	*/}

		
		return query;
	}



}