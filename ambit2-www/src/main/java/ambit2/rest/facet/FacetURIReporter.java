package ambit2.rest.facet;

import java.net.URLEncoder;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.facet.IFacet;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.facets.bookmarks.BookmarksByTopicFacet;
import ambit2.db.facets.datasets.EndpointCompoundFacet;
import ambit2.db.facets.propertyvalue.PropertyDatasetFacet;
import ambit2.db.facets.qlabel.DatasetConsensusLabelFacet;
import ambit2.db.facets.qlabel.DatasetStructureQLabelFacet;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.OpenTox;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.bookmark.BookmarkResource;
import ambit2.rest.structure.ConformerURIReporter;

/**
 * Generates URI for {@link IFacet}
 * @author nina
 *
 * @param <Q>
 */
public class FacetURIReporter <Q extends IQueryRetrieval<IFacet>> extends QueryURIReporter<IFacet, Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8868430033131766579L;
	protected ConformerURIReporter<IQueryRetrieval<IStructureRecord>> cmpReporter;
	public ConformerURIReporter<IQueryRetrieval<IStructureRecord>> getCmpReporter() {
		return cmpReporter;
	}
	public void setCmpReporter(
			ConformerURIReporter<IQueryRetrieval<IStructureRecord>> cmpReporter) {
		this.cmpReporter = cmpReporter;
	}
	public FacetURIReporter(Request baseRef) {
		super(baseRef,null);
		cmpReporter = new ConformerURIReporter<IQueryRetrieval<IStructureRecord>>(baseRef,null);
	}
	public FacetURIReporter() {
		this(null);
	}	

	@Override
	public String getURI(String ref, IFacet item) {
		Reference root = getBaseReference();

		if (item instanceof EndpointCompoundFacet) {
			
			EndpointCompoundFacet q = (EndpointCompoundFacet) item;
			
			String cmpURI = "";
			if ((q.getDataset()!=null) && (q.getDataset().getIdchemical()>0 || q.getDataset().getIdstructure()>0)) 
				cmpURI = String.format("&%s=%s",OpenTox.params.compound_uri,URLEncoder.encode(cmpReporter.getURI(q.getDataset())));

			return q.getResultsURL(root.toString(),cmpURI);
		} else if (item instanceof DatasetStructureQLabelFacet) {
			DatasetStructureQLabelFacet q = (DatasetStructureQLabelFacet) item;
			return q.getResultsURL(root.toString());		
		} else if (item instanceof DatasetConsensusLabelFacet) {
			DatasetConsensusLabelFacet q = (DatasetConsensusLabelFacet) item;
			return q.getResultsURL(root.toString());			
				
		} else if (item instanceof PropertyDatasetFacet)  {
			PropertyDatasetFacet<Property,SourceDataset> q = (PropertyDatasetFacet<Property,SourceDataset>) item;
			return String.format("%s/dataset/%d?feature_uris[]=%s/dataset/%s/feature&feature_uris[]=%s/feature/%s&property=%s/feature/%s&search=%s",
							root,q.getDataset().getId(),
						    root,q.getDataset().getId(),
						    root,q.getProperty().getId(),
						    root,q.getProperty().getId(),
							item.getValue());
		} else if (item instanceof BookmarksByTopicFacet)  {
			BookmarksByTopicFacet q = (BookmarksByTopicFacet) item;
			return String.format("%s%s/%s?hasTopic=%s",
							root,
						    BookmarkResource.resource,
						    q.getCreator(),
						    Reference.encode(item.getValue().toString()));
		} else 
			return item.getResultsURL(root.toString());
	}

}
