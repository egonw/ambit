package ambit2.rest.template;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.base.data.Dictionary;
import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.QueryRDFReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.ResourceDoc;
import ambit2.rest.property.PropertyRDFReporter;
import ambit2.rest.property.PropertyURIReporter;
import ambit2.rest.rdf.RDFPropertyIterator;

import com.hp.hpl.jena.ontology.OntModel;

public class TemplateRDFReporter<Q extends IQueryRetrieval<Property>> extends QueryRDFReporter<Property, Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8857789530109166243L;
	protected PropertyRDFReporter<IQueryRetrieval<Property>> reporterProperty;
	protected boolean recursive = false;
	protected int count = 0;
	
	public TemplateRDFReporter(Request request,ResourceDoc doc,MediaType mediaType,Boolean isRecursive) {
		super(request,mediaType,doc);
		reporterProperty = new PropertyRDFReporter<IQueryRetrieval<Property>>(request,mediaType,doc);
		recursive = isRecursive;
		
	}
	@Override
	protected QueryURIReporter createURIReporter(Request reference,ResourceDoc doc) {
		return new PropertyURIReporter(reference,doc);
	}

	@Override
	public void setOutput(OntModel output) throws AmbitException {
		super.setOutput(output);
		reporterProperty.setOutput(output);
	}
	public void open() throws DbAmbitException {
		
	}
	@Override
	public Object processItem(Property item) throws AmbitException {
		if (item == null) return null;
		count++;
		if (item.getClazz() != Dictionary.class) {
			((Property) item).setOrder(count);
			reporterProperty.processItem((Property) item);
		} else {
			Property p  = new Property(((Dictionary)item).getTemplate());
			p.setLabel(((Dictionary) item).getParentTemplate());
			p.setClazz(Dictionary.class);
			p.setId(-1);
			p.setEnabled(true);
			p.setOrder(count);
			reporterProperty.processItem(p);
			

			if (recursive) {
			
				Reference newuri = (uriReporter.getBaseReference()!=null)?
					new Reference(uriReporter.getURI(p)+"/view/tree"):
					new Reference("riap://application"+uriReporter.getURI(p)+"/view/tree");
					
				if (!newuri.equals(uriReporter.getResourceRef())) {
					RDFPropertyIterator parser=null;
					OntModel jenaModel = null;
					try {
						parser = new RDFPropertyIterator(newuri);
						jenaModel = parser.getJenaModel();
						parser.setBaseReference(uriReporter.getBaseReference());
						while (parser.hasNext()) {
							Property property = parser.next();
							if (property != null)
							processItem(property);
						}
						parser.close();
					} catch (Exception x) {
						
					} finally {
						try {parser.close(); } catch (Exception x) {}
						try {jenaModel.close(); } catch (Exception x) {}
					}
				}					
			}

		}
		return null;
	}
	
	

}