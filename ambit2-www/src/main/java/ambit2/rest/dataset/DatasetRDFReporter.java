package ambit2.rest.dataset;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.restlet.data.MediaType;
import org.restlet.data.Request;

import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.RetrieveProfileValues;
import ambit2.db.readers.RetrieveTemplateStructure;
import ambit2.db.readers.RetrieveProfileValues.SearchMode;
import ambit2.rest.OT;
import ambit2.rest.QueryRDFReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.property.PropertyURIReporter;
import ambit2.rest.reference.ReferenceURIReporter;
import ambit2.rest.structure.CompoundURIReporter;
import ambit2.rest.structure.ConformerURIReporter;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;

/**
 * RDF/XML
 * @author nina
 *
 * @param <Q>
 */
public class DatasetRDFReporter<Q extends IQueryRetrieval<IStructureRecord>> extends QueryRDFReporter<IStructureRecord,Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6410553622662161903L;
	protected PropertyURIReporter propertyReporter;
	protected ReferenceURIReporter referenceReporter;
	
	protected Template template;
	protected List<Property> header = null;
	public Template getTemplate() {
		return template;
	}
	public void setTemplate(Template template) {
		this.template = template;
	}
	protected Resource dataset;
	
	public DatasetRDFReporter(Request request,MediaType mediaType, Template template) {
		super(request,mediaType);
		setTemplate(template==null?new Template(null):template);
		initProcessors();
		propertyReporter = new PropertyURIReporter(request);
		referenceReporter = new ReferenceURIReporter(request);
	}
	@Override
	protected QueryURIReporter<IStructureRecord, IQueryRetrieval<IStructureRecord>> createURIReporter(
			Request req) {
		return new ConformerURIReporter<IQueryRetrieval<IStructureRecord>>(req);
	}

	protected void initProcessors() {
		
		getProcessors().clear();
		if (getTemplate().size()>0) 
			getProcessors().add(new ProcessorStructureRetrieval(new RetrieveProfileValues(SearchMode.idproperty,getTemplate(),true)) {
				@Override
				public IStructureRecord process(IStructureRecord target)
						throws AmbitException {
					((RetrieveProfileValues)getQuery()).setRecord(target);
					return super.process(target);
				}
			});
		else
		getProcessors().add(new ProcessorStructureRetrieval(new RetrieveTemplateStructure(getTemplate())) {
				@Override
				public IStructureRecord process(IStructureRecord target)
						throws AmbitException {
					((RetrieveTemplateStructure)getQuery()).setRecord(target);
					return super.process(target);
				}
			});
		getProcessors().add(new DefaultAmbitProcessor<IStructureRecord,IStructureRecord>() {
			public IStructureRecord process(IStructureRecord target) throws AmbitException {
				processItem(target);
				return target;
			};
		});			
	}
	@Override
	public void header(OntModel output, Q query) {
		super.header(output,query);
		OT.OTClass.Dataset.createOntClass(output);
		OT.OTClass.DataEntry.createOntClass(output);
		OT.OTClass.Feature.createOntClass(output);
		OT.OTClass.FeatureValue.createOntClass(output);
		OT.OTClass.Compound.createOntClass(output);
		
		
		dataset = output.createIndividual(
				String.format("%s:%s",uriReporter.getRequest().getOriginalRef().getScheme(),uriReporter.getRequest().getOriginalRef().getHierarchicalPart()),
				OT.OTClass.Dataset.getOntClass(output));
		
		dataset.addProperty(DC.identifier, uriReporter.getRequest().getOriginalRef().toString());
		dataset.addProperty(DC.title,query.toString());
		dataset.addProperty(DC.description,uriReporter.getRequest().getOriginalRef().toString());
	}

	@Override

	public void processItem(IStructureRecord item) throws AmbitException {
		try {
			
			if (header == null) 
				header = template2Header(template,true);
				
			Individual dataEntry = getJenaModel().createIndividual(
						OT.OTClass.DataEntry.getOntClass(getJenaModel()));
			dataset.addProperty(OT.OTProperty.dataEntry.createProperty(getJenaModel()),dataEntry);
			int i = 0;
			
			Individual compound = getJenaModel().createIndividual(
					uriReporter.getURI(item),OT.OTClass.Compound.getOntClass(getJenaModel()));
			
			dataEntry.addProperty(OT.OTProperty.compound.createProperty(getJenaModel()), compound);
			
//			if (item.getIdstructure()>0)
//				writer.write(String.format("/conformer/%d",item.getIdstructure()));
			
			for (ambit2.base.data.Property p : header) {
				Object value = item.getProperty(p);
				if (value == null) continue;
				
				Individual feature = getJenaModel().createIndividual(propertyReporter.getURI(p),
						OT.OTClass.Feature.getOntClass(getJenaModel()));
				/*
				feature.addProperty(RDFJenaModel.DCProperty.title.getProperty(getJenaModel()), p.getName());
				feature.addProperty(RDFJenaModel.DCProperty.identifier.getProperty(getJenaModel()),propertyReporter.getURI(p));
				feature.addProperty(RDFJenaModel.OTProperty.hasSource.getProperty(getJenaModel()), referenceReporter.getURI(p.getReference()));
				*/
				Individual featureValue = getJenaModel().createIndividual(OT.OTClass.FeatureValue.getOntClass(getJenaModel()));
				featureValue.addProperty(OT.OTProperty.feature.createProperty(getJenaModel()),feature);

				featureValue.addLiteral(OT.value,getJenaModel().createTypedLiteral(value, 
							(p.getClazz()==Number.class)?XSDDatatype.XSDdouble: XSDDatatype.XSDstring));
				
				i++;
				dataEntry.addProperty(OT.OTProperty.values.createProperty(getJenaModel()),featureValue);
			}
			
		} catch (Exception x) {
			logger.error(x);
		}
		
	}
	public void open() throws DbAmbitException {
		
	}
	protected List<Property> template2Header(Template template, boolean propertiesOnly) {
		List<Property> h = new ArrayList<Property>();
		Iterator<Property> it = template.getProperties(true);
		while (it.hasNext()) {
			Property t = it.next();
			if (!propertiesOnly || (propertiesOnly && (t.getId()>0)))
				h.add(t);
		}
		/*
		Collections.sort(h,new Comparator<Property>() {
			public int compare(Property o1, Property o2) {
				return o1.getOrder()-o2.getOrder();
			}
		});	
		*/
		return h;
	}
}
