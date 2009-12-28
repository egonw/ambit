package ambit2.rest.rdf;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.routing.Template;

import ambit2.rest.OpenTox;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public abstract class RDFDataEntryIterator<Item,Feature> extends RDFObjectIterator<Item> {
	protected Template conformerTemplate;
	protected Template featureTemplate;
	public RDFDataEntryIterator(Representation representation,MediaType mediaType) throws ResourceException {
		super(representation,mediaType,OT.OTClass.DataEntry.toString());
	}
		
	public RDFDataEntryIterator(Reference reference) throws ResourceException {
		super(reference,OT.OTClass.DataEntry.toString());
	}	
	public RDFDataEntryIterator(Reference reference,MediaType mediaType) throws ResourceException {
		super(reference,mediaType,OT.OTClass.DataEntry.toString());
	}
	
	public RDFDataEntryIterator(InputStream in,MediaType mediaType) throws ResourceException {
		super(in,mediaType,OT.OTClass.DataEntry.toString());
	}	
	public RDFDataEntryIterator(OntModel model, StmtIterator recordIterator) {
		super(model,OT.OTClass.DataEntry.toString(),recordIterator);
	}
	
	public RDFDataEntryIterator(OntModel model) {
		super(model,OT.OTClass.DataEntry.toString());
	}	


	@Override
	protected Template createTemplate() {
		featureTemplate = new Template(String.format("%s%s",baseReference==null?"":baseReference,OpenTox.URI.feature.getResourceID()));
		conformerTemplate = new Template(String.format("%s%s",baseReference==null?"":baseReference,OpenTox.URI.conformer.getResourceID()));
		return new Template(String.format("%s%s",baseReference==null?"":baseReference,OpenTox.URI.compound.getResourceID()));
	}

	protected abstract void setIDChemical(int idchemical);
	protected abstract void setIDConformer(int idchemical);
	@Override
	protected void parseObjectURI(RDFNode uri, Item record) {
		int idchemical = -1;
		int idstructure = -1;
		
		Map<String, Object> vars = new HashMap<String, Object>();
		
		try { 
			conformerTemplate.parse(getIdentifier(uri), vars);
			try {
			idchemical = Integer.parseInt(vars.get(OpenTox.URI.compound.getKey()).toString()); } 
			catch (Exception x) {};
		
			try { idstructure = Integer.parseInt(vars.get(OpenTox.URI.conformer.getKey()).toString()); } 
			catch (Exception x) {};
		} catch (Exception x) {};
		
		if (idchemical<=0) {
			try {
			getTemplate().parse(getIdentifier(uri), vars);
			idchemical = Integer.parseInt(vars.get(OpenTox.URI.compound.getKey()).toString()); } 
			catch (Exception x) {};
		}
		setIDChemical(idchemical);
		setIDConformer(idstructure);
	}

	@Override
	protected Item parseRecord(Resource newEntry, Item record) {
		//get the compound
		StmtIterator compound =  jenaModel.listStatements(new SimpleSelector(newEntry,OT.OTProperty.compound.createProperty(jenaModel),(RDFNode)null));
		while (compound.hasNext()) {
			Statement st = compound.next();
			parseObjectURI(st.getObject(),record);
			if (readStructure(st.getObject(), record))
				break;

		}	
		//get feature values
		parseFeatureValues( newEntry,record);
		return record;
	}
	protected void parseFeatureValues(Resource dataEntry,Item record)  {
		StmtIterator values =  jenaModel.listStatements(new SimpleSelector(dataEntry,OT.OTProperty.values.createProperty(jenaModel),(RDFNode)null));
		
		while (values.hasNext()) {
			Statement st = values.next();
			if (st.getObject().isResource()) {
				Resource fv = (Resource)st.getObject();
				RDFNode value = fv.getProperty(OT.DataProperty.value.createProperty(jenaModel)).getObject();
				
				String feature = fv.getProperty(OT.OTProperty.feature.createProperty(jenaModel)).getObject().toString();
				Feature key = createFeature(fv.getProperty(OT.OTProperty.feature.createProperty(jenaModel)).getObject());
				parseFeatureURI(feature, key);
				setFeatureValue(record, key,  value);
	
			}
		}
	}		
	public abstract  boolean readStructure(RDFNode target,Item record) ;
	protected abstract Feature createFeature(RDFNode feature);
	protected abstract void setFeatureValue(Item record, Feature key, RDFNode value) ;
	protected abstract void parseFeatureURI(String uri,Feature property) ;
}
