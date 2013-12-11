package ambit2.rest.substance.study;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.resource.ResourceException;

import ambit2.db.substance.study.facet.SubstanceStudyFacetQuery;
import ambit2.rest.facet.FacetResource;
import ambit2.rest.substance.SubstanceResource;

public class SubstanceStudyFacetResource extends FacetResource<SubstanceStudyFacetQuery>  {
	public final static String resource = "studysummary";
	@Override
	protected SubstanceStudyFacetQuery createQuery(Context context,
			Request request, Response response) throws ResourceException {
		
		Object substanceuuid = request.getAttributes().get(SubstanceResource.idsubstance);
		/*
		Form form = getResourceRef(request).getQueryAsForm();
		Object key = form.getFirstValue(Annotea.BookmarkProperty.hasTopic.toString());
		if (key != null) {
			if (bookmark==null) bookmark = new Bookmark();
			bookmark.setHasTopic(Reference.decode(key.toString()));
		} 
		*/
		SubstanceStudyFacetQuery q = new SubstanceStudyFacetQuery(
				String.format("%s%s/%s/study",getRootRef(),SubstanceResource.substance,substanceuuid)
				);
		q.setFieldname(substanceuuid==null?null:substanceuuid.toString());
		return q;
	}
	protected ambit2.db.reporters.QueryReporter createJSONReporter(Request request) {
		return new StudySummaryJSONReporter(request);
	};
	
}