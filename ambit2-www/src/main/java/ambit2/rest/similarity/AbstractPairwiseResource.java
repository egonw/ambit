package ambit2.rest.similarity;

import java.awt.Dimension;
import java.io.Serializable;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.SourceDataset;
import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.exceptions.NotFoundException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.CSVReporter;
import ambit2.db.reporters.QueryAbstractReporter;
import ambit2.db.search.StoredQuery;
import ambit2.db.search.property.QueryPairwiseTanimoto;
import ambit2.db.search.structure.QueryStructureByID;
import ambit2.rest.OpenTox;
import ambit2.rest.OutputWriterConvertor;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.dataset.DatasetStructuresResource;
import ambit2.rest.error.InvalidResourceIDException;
import ambit2.rest.query.QueryResource;

public abstract class AbstractPairwiseResource <T extends Serializable,Q extends IQueryRetrieval<T>> extends QueryResource<Q,T> { 
	protected Integer datasetID;
	protected Integer queryResultsID;
	protected Template template;

	public String getCompoundInDatasetPrefix() {
		if (dataset_prefixed_compound_uri)
		return
			datasetID!=null?String.format("%s/%d", OpenTox.URI.dataset.getURI(),datasetID):
			queryResultsID!=null?String.format("%s/R%d", OpenTox.URI.dataset.getURI(),queryResultsID):"";
		else return "";
	}

	protected Q getQueryById(Integer key) throws ResourceException {
		Q query = null;
		datasetID = key;
		QueryPairwiseTanimoto q = new QueryPairwiseTanimoto();
		SourceDataset dataset = new SourceDataset();
		dataset.setID(key);
		q.setFieldname(dataset);
		q.setValue(dataset);
		Form form = getResourceRef(getRequest()).getQueryAsForm();
		setPaging(form, q);
		return (Q)q;
	}
	

	protected Q getQueryById(String key) throws ResourceException {
		if (key.startsWith(DatasetStructuresResource.QR_PREFIX)) {
			key = key.substring(DatasetStructuresResource.QR_PREFIX.length());
			try {
				queryResultsID = Integer.parseInt(key.toString());
			} catch (NumberFormatException x) {
				throw new InvalidResourceIDException(key);
			}
			QueryPairwiseTanimoto q = new QueryPairwiseTanimoto();
			StoredQuery dataset = new StoredQuery();
			dataset.setID(queryResultsID);
			q.setFieldname(dataset);
			q.setValue(dataset);
			Form form = getResourceRef(getRequest()).getQueryAsForm();
			setPaging(form, q);
			return (Q)q;
		} //else return getDatasetByName(key);
		throw new InvalidResourceIDException(key);
	}

	public Template getTemplate() {
		return template;
	}
	public void setTemplate(Template template) {
		this.template = (template==null)?new Template(null):template;

	}
	@Override
	protected Q createQuery(Context context, Request request, Response response)
			throws ResourceException {
		setTemplate(createTemplate(request.getResourceRef().getQueryAsForm()));
		Object id = request.getAttributes().get(DatasetStructuresResource.datasetKey);
		if (id != null)  try {
			
			id = Reference.decode(id.toString());
			return getQueryById(new Integer(id.toString()));

		} catch (NumberFormatException x) {
			return getQueryById(id.toString());
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x.getMessage(),x);
		}
		return null;
	}
	

	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		/* workaround for clients not being able to set accept headers */
		if ((queryObject == null) && !(variant.getMediaType().equals(MediaType.TEXT_HTML))) 
			throw new NotFoundException();
		
		//setTemplate(template);
		Form acceptform = getResourceRef(getRequest()).getQueryAsForm();
		String media = acceptform.getFirstValue("accept-header");
		if (media != null) {
			variant.setMediaType(new MediaType(media));
		}
		String filenamePrefix = getRequest().getResourceRef().getPath();
		
		if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
			Dimension d = new Dimension(150,150);
			Form form = getResourceRef(getRequest()).getQueryAsForm();
			try {
				d.width = Integer.parseInt(form.getFirstValue("w").toString());
			} catch (Exception x) {}
			try {
				d.height = Integer.parseInt(form.getFirstValue("h").toString());
			} catch (Exception x) {}			
			return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
					createHTMLReporter(d),MediaType.TEXT_HTML);
	
		} 
	 /*
		else if (variant.getMediaType().equals(ChemicalMediaType.WEKA_ARFF)) {
			return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
					new ARFFResourceReporter(getTemplate(),getGroupProperties(),getRequest(),getDocumentation(),
								String.format("%s%s",getRequest().getRootRef(),getCompoundInDatasetPrefix())
							),
					ChemicalMediaType.WEKA_ARFF,filenamePrefix);			
		} else if (variant.getMediaType().equals(MediaType.TEXT_CSV)) {
			return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
					createCSVReporter()
					,MediaType.TEXT_CSV,filenamePrefix);
		} 
		//else if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
		//otherwise return json
		 * */
			return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
					createJSONReporter(),
							//getTemplate(),getGroupProperties(),getRequest(),
					MediaType.APPLICATION_JSON,filenamePrefix);	
	}

	
	
	protected abstract QueryAbstractReporter createHTMLReporter(Dimension d);

	protected abstract QueryAbstractReporter createJSONReporter();
	
	protected abstract CSVReporter createCSVReporter();

}