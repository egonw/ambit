package ambit2.rest.bookmark;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Bookmark;
import ambit2.db.facets.bookmarks.BookmarksByTopicFacetQuery;
import ambit2.rest.facet.FacetResource;
import ambit2.rest.rdf.Annotea;

public class BookmarkTopicsResource extends FacetResource<BookmarksByTopicFacetQuery>  {
	public final static String resource = "hasTopics";
	@Override
	protected BookmarksByTopicFacetQuery createQuery(Context context,
			Request request, Response response) throws ResourceException {
		
		Object user = request.getAttributes().get(BookmarkResource.creator);
		if (user==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"No user name");
		Bookmark bookmark = user==null?null:new Bookmark(user.toString());
		
		Form form = getResourceRef(request).getQueryAsForm();
		Object key = form.getFirstValue(Annotea.BookmarkProperty.hasTopic.toString());
		if (key != null) {
			if (bookmark==null) bookmark = new Bookmark();
			bookmark.setHasTopic(Reference.decode(key.toString()));
		} 
		
		BookmarksByTopicFacetQuery q = new BookmarksByTopicFacetQuery(bookmark.getHasTopic());
		q.setFieldname(bookmark);
		return q;
	}

}
