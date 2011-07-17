package ambit2.rest.property.annotations;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.data.Dictionary;
import ambit2.base.data.Property;
import ambit2.base.data.PropertyAnnotation;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.ResourceDoc;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.template.OntologyResource;

/**
 * Generates uri of {@link PropertyResource}
 * @author nina
 *
 */
public class PropertyAnnotationURIReporter extends QueryURIReporter<PropertyAnnotation, IQueryRetrieval<PropertyAnnotation>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 711954935147788056L;
	protected boolean propertyOnly = false;
	public boolean isPropertyOnly() {
		return propertyOnly;
	}
	public void setPropertyOnly(boolean propertyOnly) {
		this.propertyOnly = propertyOnly;
	}
	public PropertyAnnotationURIReporter(Reference baseRef,ResourceDoc doc) {
		super(baseRef,doc);
	}
	public PropertyAnnotationURIReporter(Request ref,ResourceDoc doc) {
		super(ref,doc);
	}
	public PropertyAnnotationURIReporter() {
		this((Request)null,null);
	}
	@Override
	public String getURI(String ref, PropertyAnnotation record) {

		if (record.getIdproperty()>0)
			return String.format("%s%s/%d%s%s",ref,PropertyResource.featuredef,record.getIdproperty(),
					isPropertyOnly()?"":PropertyAnnotationResource.annotation,
					getDelimiter());
		else
			return null;

	}

}
