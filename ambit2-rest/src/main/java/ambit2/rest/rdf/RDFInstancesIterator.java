package ambit2.rest.rdf;

import java.io.InputStream;
import java.util.Hashtable;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import ambit2.rest.rdf.OT.OTClass;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;

public class RDFInstancesIterator extends RDFDataEntryIterator<Instance, Attribute> {
	public static final String CompoundURI="CompoundURI";
	public RDFInstancesIterator(Representation representation, MediaType mediaType) throws ResourceException {
		this(OT.createModel(representation,mediaType));
	}
	
	public RDFInstancesIterator(Reference reference) throws ResourceException {
		this(OT.createModel(reference, MediaType.APPLICATION_RDF_XML));
	}
	
	public RDFInstancesIterator(Reference reference,MediaType mediaType) throws ResourceException {
		this(OT.createModel(reference, mediaType));
	}
	
	public RDFInstancesIterator(InputStream in,MediaType mediaType) throws ResourceException {
		this(OT.createModel(in, mediaType));
	}	

	public RDFInstancesIterator(OntModel model, StmtIterator recordIterator) {
		this(model);
		this.recordIterator = recordIterator;
	}
	public RDFInstancesIterator(OntModel model) {
		super(model);
		urilookup = new Hashtable<String, Attribute>();
		attributes = parseFeatures();
		StmtIterator dataset =  jenaModel.listStatements(new SimpleSelector(null,
				RDF.type,
				OT.OTClass.Dataset.getOntClass(jenaModel)));		
		while (dataset.hasNext()) {
			instances = new Instances(
					dataset.next().getSubject().toString()
					, attributes, 0);
			break;
		}
		
	}

	protected FastVector attributes;
	protected Hashtable<String, Attribute> urilookup;
	protected Instances instances;
	protected int maxNominalValues = 20;
	
	
	@Override
	protected Attribute createFeature(RDFNode feature) {
		Attribute a = null;
		try {
			if (
				((Resource)feature).getProperty(RDF.type).getObject().equals(
						OTClass.NumericFeature.getOntClass(jenaModel)))
			
			//if("http://www.w3.org/2001/XMLSchema#double".equals(
				//	((Resource)feature).getProperty(DC.type).getObject().toString()))
				a = new Attribute(feature.toString());
		} catch(Exception x) {
			a = null;
		}
		if (a == null)	a = new Attribute(feature.toString(),(FastVector)null);
		urilookup.put(feature.toString(),a);
		return a;
	}

	@Override
	protected void parseFeatureURI(String uri, Attribute property) {
	}

	@Override
	public boolean readStructure(RDFNode target, Instance record) {
		return true;
	}

	@Override
	protected void setFeatureValue(Instance record, Attribute key, RDFNode value) {
		if (value.isLiteral())  {
	 		//
			if (key.isNumeric()) try {
				record.setValue(key, ((Literal)value).getDouble());
			} catch (Exception x) {  }
			else try {
				//key.addStringValue(((Literal)value).getString());
				record.setValue(key, ((Literal)value).getString() );
			} catch (Exception x) { }
		}
	}

	@Override
	protected void setIDChemical(int idchemical) {
	}

	@Override
	protected void setIDConformer(int idchemical) {
	}

	@Override
	protected Instance createRecord() {
		if (instances != null) {
			return new Instance(attributes.size());
		} else return null;
	}
	@Override
	protected Instance parseRecord(Resource newEntry, Instance record) {
		
		String id = null;
		//get the compound
		StmtIterator compound =  jenaModel.listStatements(new SimpleSelector(newEntry,OT.OTProperty.compound.createProperty(jenaModel),(RDFNode)null));
		while (compound.hasNext()) {
			Statement st = compound.next();
			id = st.getObject().toString();
			
			break;
		}
		record.setValue(urilookup.get(CompoundURI),id);
		//get feature values
		
		parseFeatureValues( newEntry,record);
		record.setDataset(instances);
		instances.add(record);
		return record;
	}
	@Override
	protected void parseFeatureValues(Resource dataEntry, Instance record) {
		StmtIterator values =  jenaModel.listStatements(new SimpleSelector(dataEntry,OT.OTProperty.values.createProperty(jenaModel),(RDFNode)null));
		
		while (values.hasNext()) {
			Statement st = values.next();
			if (st.getObject().isResource()) {
				Resource fv = (Resource)st.getObject();
				RDFNode value = fv.getProperty(OT.DataProperty.value.createProperty(jenaModel)).getObject();
				RDFNode feature = fv.getProperty(OT.OTProperty.feature.createProperty(jenaModel)).getObject();

				Attribute key = urilookup.get( feature.toString());
				if (key != null) 
					setFeatureValue(record, key,  value);
	
			}
		}
	}
	protected FastVector parseFeatures() {
		FastVector attributes = new FastVector();
		Attribute id = new Attribute(CompoundURI,(FastVector)null);
		urilookup.put(CompoundURI,id);
		attributes.addElement(id);
		
		Resource s = OT.OTClass.Feature.getOntClass(jenaModel);
		if (s==null) return null;
		
		Property valueProperty = OT.DataProperty.value.createProperty(jenaModel);
		StmtIterator features =  jenaModel.listStatements(new SimpleSelector(null,null,s));
		while (features.hasNext()) {
			
			Statement feature = features.next();
			
			int ndouble=0;
			int nstring = 0;
			
			FastVector nominal = new FastVector();
			StmtIterator entries =  jenaModel.listStatements(
					new SimpleSelector(null,OT.OTProperty.feature.createProperty(jenaModel),feature.getSubject()));
			while (entries.hasNext()) {
				
				Resource entry = entries.next().getSubject();
				try {
					
					Statement values = entry.getProperty(valueProperty);
					if (values.getObject().isLiteral()) {
						Class clazz = ((Literal)values.getObject()).getDatatype().getJavaClass();
						if (clazz == Double.class) ndouble++;
						else if (clazz == Float.class) ndouble++;
						else if (clazz == Integer.class) ndouble++;
						else if (clazz == Long.class) ndouble++;
						else if (clazz == Short.class) ndouble++;
						else {
							String value = ((Literal)values.getObject()).getString();
							if ((nominal.size()<(maxNominalValues+1)) && !nominal.contains(value))
								nominal.addElement(value);
							
							nstring++;
						}
					}

				} catch (Exception x) {
					x.printStackTrace();
				}
				
			}
			
			if ((ndouble+nstring)==0) continue;
			Attribute a = null;
			
			if (ndouble > nstring) //numeric feature
				a = new Attribute(feature.getSubject().toString());
			else if (nominal.size()>maxNominalValues) //string attribute
				a = new Attribute(feature.getSubject().toString(),(FastVector)null);
			else
				a = new Attribute(feature.getSubject().toString(),nominal);
			urilookup.put(feature.getSubject().toString(),a);
			attributes.addElement(a);
		}
		return attributes;
	}	
	public Instances getInstances() {
		return instances;
	}

	
}
