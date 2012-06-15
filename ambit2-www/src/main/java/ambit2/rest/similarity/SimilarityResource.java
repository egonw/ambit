package ambit2.rest.similarity;

import java.awt.Dimension;
import java.util.BitSet;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.CSVReporter;
import ambit2.db.reporters.QueryAbstractReporter;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.structure.QueryCombinedStructure;
import ambit2.db.search.structure.QuerySimilarityBitset;
import ambit2.db.update.structure.ChemicalByDataset;
import ambit2.rest.DisplayMode;
import ambit2.rest.OpenTox;
import ambit2.rest.ResourceDoc;
import ambit2.rest.dataset.DatasetResource;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.query.StructureQueryResource;
import ambit2.rest.structure.CompoundHTMLReporter;

/**
 *  Retrieve similar compounds, given a smiles
 * /similarity?search="smiles"&threshold=0.9
 * @author nina
 *
 */
public class SimilarityResource<Q extends IQueryRetrieval<IStructureRecord>> extends StructureQueryResource<Q> {
	
	public final static String resource =  "/similarity";
	public final static String smiles =  "smiles";
	public final static String resourceID =  String.format("/{%s}",smiles);
	protected double threshold = 0.9;
	protected String dataset_id;

	protected IAtomContainer mol ;

	public SimilarityResource() {
		super();
		setDocumentation(new ResourceDoc("dataset","Dataset"));
	}
	
	@Override
	public String getCompoundInDatasetPrefix() {
		if (dataset_prefixed_compound_uri)
		return
				dataset_id!=null?String.format("%s/%s", OpenTox.URI.dataset.getURI(),dataset_id):"";

		else return "";
	}	
	protected String getDefaultTemplateURI(Context context, Request request,Response response) {
		return (dataset_id == null)?null:
			String.format("%s%s/%s%s",
					getRequest().getRootRef(),OpenTox.URI.dataset.getURI(),dataset_id,PropertyResource.featuredef);				
			
//			String.format("riap://application/dataset/%s%s",dataset_id,PropertyResource.featuredef);
	}
	
	@Override
	protected CSVReporter createCSVReporter() {
		CSVReporter csvReporter = super.createCSVReporter();
		csvReporter.setSimilarityColumn(Property.getInstance("metric",queryObject==null?"":queryObject.toString(),"http://ambit.sourceforge.net"));
		return csvReporter;
	}
	/*
	@Override
	protected QueryAbstractReporter createHTMLReporter(Dimension d) {
		return new CompoundHTMLReporter(getCompoundInDatasetPrefix(),getRequest(),getDocumentation(),
						DisplayMode.singleitem,null,getTemplate(),getGroupProperties(),d,headless);
	}
	*/
	@Override
	protected Q createQuery(Context context,
			Request request, Response response) throws ResourceException {
		Form form = getResourceRef(getRequest()).getQueryAsForm();
		mol = getMolecule(form);
		if ((mol==null)||(mol.getAtomCount()==0)) 
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND,"Empty molecule");
		threshold = 0.0;
        try {
        	threshold = new Double(Reference.decode(form.getFirstValue("threshold")));
        } catch (Exception x) {
        	threshold = 0.9;
        }			
        
		QuerySimilarityBitset q = new QuerySimilarityBitset();
		q.setChemicalsOnly(true);
		q.setThreshold(threshold);
		q.setCondition(NumberCondition.getInstance(">"));		
		q.setName("Similarity");
		try {
			q.setValue(getBitset(mol));
			
			QueryCombinedStructure qc= null;
			try {
				qc = new QueryCombinedStructure();
				qc.add(q);
				qc.setChemicalsOnly(true);
				this.dataset_id = Reference.decode(getRequest().getAttributes().get(DatasetResource.datasetKey).toString());
				ChemicalByDataset  cd = new ChemicalByDataset(new Integer(dataset_id));
				qc.setScope(cd);
				setTemplate(createTemplate(context, request, response));
				return (Q)qc;
			} catch (Exception x) {
				setTemplate(createTemplate(context, request, response));
				return (Q)q;
			}	
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
		}

	}

	public BitSet getBitset(IAtomContainer molecule) throws AmbitException {
		FingerprintGenerator gen = new FingerprintGenerator();
		return gen.process(molecule);
	}

		
}
