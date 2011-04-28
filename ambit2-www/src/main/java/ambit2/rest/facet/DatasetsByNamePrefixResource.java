package ambit2.rest.facet;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.resource.ResourceException;

import ambit2.db.facets.datasets.DatasetByPrefixNameFacetQuery;
import ambit2.rest.query.QueryResource;

/**
 * 

 * @author nina
 *
 */
public class DatasetsByNamePrefixResource  extends FacetResource<DatasetByPrefixNameFacetQuery>  {
	public static final String resource = "/ndatasets_nameprefix";
	
	@Override
	protected DatasetByPrefixNameFacetQuery createQuery(Context context,
			Request request, Response response) throws ResourceException {
		DatasetByPrefixNameFacetQuery q = new DatasetByPrefixNameFacetQuery(getRequest().getResourceRef().toString());
		try {
			q.setValue(request.getResourceRef().getQueryAsForm().getFirstValue(QueryResource.search_param));
		} catch (Exception x) {
			q.setValue(null);
		}
		try {
			q.setFieldname(getStructure());
		} catch (Exception x) {
			q.setFieldname(null);
		}		
		return q;
		
	}

}