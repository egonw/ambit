package ambit2.rest.facet;

import java.util.Iterator;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.Template;
import ambit2.db.facets.propertyvalue.PropertyDatasetFacetQuery;
import ambit2.rest.OpenTox;

/**
 * Number of compounds given property value and a dataset
 * @author nina
 *
 */
public class CompoundsByPropertyValueInDatasetResource extends FacetResource<PropertyDatasetFacetQuery> {
	public static final String resource = "/ncompound_value";
	@Override
	protected PropertyDatasetFacetQuery createQuery(Context context,
			Request request, Response response) throws ResourceException {
		
		String uri = getParams().getFirstValue(OpenTox.params.dataset_uri.toString());
		int datasetid = (Integer)OpenTox.URI.dataset.getId(uri,getRequest().getRootRef());
		if (datasetid<1) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"No dataset_uri!");
		
		SourceDataset dataset = new SourceDataset(uri);
		dataset.setId(datasetid);
		
		
		Template profile = getProperty(getParams().getValuesArray(OpenTox.params.feature_uris.toString()),1);
		if ((profile==null) || (profile.size()==0))
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"No feature_uris[] !");
		
		PropertyDatasetFacetQuery q = new PropertyDatasetFacetQuery();
		Iterator<Property> i = profile.getProperties(true);
		while (i.hasNext()) {
			q.setFieldname(i.next());
			break;
		}
		q.setValue(dataset);
		return q;
	}
}