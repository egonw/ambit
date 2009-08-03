package ambit2.rest.dataset;

import java.io.Writer;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Variant;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.SourceDataset;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.RetrieveDatasets;
import ambit2.db.search.StringCondition;
import ambit2.db.update.dataset.ReadDataset;
import ambit2.rest.DocumentConvertor;
import ambit2.rest.OutputStreamConvertor;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.error.InvalidResourceIDException;
import ambit2.rest.query.QueryResource;

/**
 * http://opentox.org/wiki/1/Dataset
 * 

 * @author nina
 *
 */
public class DatasetsResource extends QueryResource<IQueryRetrieval<SourceDataset>, SourceDataset> {
	
	
	public final static String datasets = "/dataset";	
	public final static String datasetID =  String.format("%s%s",DatasetsResource.datasets,"/{dataset_id}");
	protected boolean collapsed;
	public DatasetsResource(Context context, Request request, Response response) {
		super(context,request,response);
		this.getVariants().add(new Variant(MediaType.TEXT_HTML));		
	}

	@Override
	protected IQueryRetrieval<SourceDataset> createQuery(Context context,
			Request request, Response response) throws AmbitException {
		ReadDataset query = new ReadDataset();
		
		Object id = request.getAttributes().get("dataset_id");
		collapsed = true;
		if (id != null) try {
			SourceDataset dataset = new SourceDataset();
			dataset.setId(new Integer(Reference.decode(id.toString())));
			query.setValue(dataset);
			collapsed = false;
		} catch (NumberFormatException x) {
			error = new InvalidResourceIDException(id);
			query=null;
		} catch (Exception x) {
			query.setValue(null);
		}
		else {
			Form form = request.getResourceRef().getQueryAsForm();
			Object key = form.getFirstValue("search");
			if (key != null) {
				RetrieveDatasets query_by_name = new RetrieveDatasets(null,new SourceDataset(Reference.decode(key.toString())));
				query_by_name.setCondition(StringCondition.getInstance(StringCondition.C_REGEXP));
				return query_by_name;
			} 
		}
		return query;
	}
	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException {

	if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
		return new DocumentConvertor(new DatasetsXMLReporter(getRequest().getRootRef()));	
	} else if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
		return new OutputStreamConvertor(
				new DatasetsHTMLReporter(getRequest().getRootRef(),collapsed),MediaType.TEXT_HTML);
	} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
		return new StringConvertor(	new DatasetURIReporter<IQueryRetrieval<SourceDataset>>(getRequest().getRootRef()) {
			@Override
			public void processItem(SourceDataset dataset, Writer output) {
				super.processItem(dataset, output);
				try {
				output.write('\n');
				} catch (Exception x) {}
			}
		},MediaType.TEXT_URI_LIST);
	} else //html 	
		return new OutputStreamConvertor(
				new DatasetHTMLReporter(getRequest().getRootRef(),collapsed),MediaType.TEXT_HTML);
	}
}
