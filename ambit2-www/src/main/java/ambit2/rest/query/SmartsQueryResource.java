package ambit2.rest.query;

import java.util.ArrayList;

import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.inchi.InChIToStructure;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import uk.ac.cam.ch.wwmm.opsin.NameToStructure;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.config.AmbitCONSTANTS;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.structure.ChemicalByAssessment;
import ambit2.db.search.structure.FreeTextQuery;
import ambit2.db.search.structure.QueryCombinedStructure;
import ambit2.db.search.structure.QueryDatasetByID;
import ambit2.db.search.structure.QuerySMARTS;
import ambit2.db.search.structure.QueryStructureByID;
import ambit2.descriptors.FunctionalGroup;
import ambit2.rest.OpenTox;
import ambit2.rest.dataset.DatasetResource;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.structure.CompoundResource;

/**
 * SMARTS search in database
 * @author nina
 *
 */
public class SmartsQueryResource  extends StructureQueryResource<IQueryRetrieval<IStructureRecord>> {
	public final static String smartsKey =  "smarts";
	public final static String resourceID =  String.format("/{%s}",smartsKey);
	public final static String resource =  String.format("/%s",smartsKey);
	protected String dataset_id;
	protected String textSearch = "CasRN";
	
	@Override
	public String getCompoundInDatasetPrefix() {
		if (dataset_prefixed_compound_uri)
		return
				dataset_id!=null?String.format("%s/%s", OpenTox.URI.dataset.getURI(),dataset_id):"";

		else return "";
	}	
	protected String getDefaultTemplateURI(Context context, Request request,Response response) {
		return (dataset_id == null)?
				null
			//String.format("riap://application%s?text=%s",PropertyResource.featuredef,Reference.encode(textSearch))
			//	String.format("%s%s?text=%s",
				//		getRequest().getRootRef(),PropertyResource.featuredef,Reference.encode(textSearch))				
			:
				
				
			String.format("%s%s/%s%s",
						getRequest().getRootRef(),OpenTox.URI.dataset.getURI(),dataset_id,PropertyResource.featuredef);				
			//String.format("riap://application/dataset/%s%s",dataset_id,PropertyResource.featuredef);
	}
	protected IQueryRetrieval<IStructureRecord> getScopeQuery(Context context, Request request,
								Response response) throws ResourceException {
		Form form = request.getResourceRef().getQueryAsForm();
		String[] keys = form.getValuesArray("text");
		
		ArrayList<String> s = new ArrayList<String>();
		for (String key : keys) {
			if (key==null)continue;
			String[] k = key.replace("\""," ").split(" ");
			for (String kk:k) 
				if (!"".equals(kk.trim())) 
					s.add(kk);
		}
		if (s.size()==0) return null;
		keys = s.toArray(keys);
		if ((keys!=null) && (keys.length>0)) {
			FreeTextQuery query = new FreeTextQuery();
			query.setFieldname(keys);
			query.setValue(keys);
			textSearch=query.toString().trim();
			return query;
		} else return null;
	}
	@Override
	protected IQueryRetrieval<IStructureRecord> createQuery(Context context, Request request,
			Response response) throws ResourceException {
		try {
			rdfwriter = RDF_WRITER.jena;
			IQueryRetrieval<IStructureRecord> freetextQuery = getScopeQuery(context, request, response);
			
			Form form = request.getResourceRef().getQueryAsForm();
			try { includeMol = "true".equals(form.getFirstValue("mol")); } catch (Exception x) { includeMol=false;}
			Object key = form.getFirstValue(QueryResource.search_param);
			Object b64key = form.getFirstValue(QueryResource.b64search_param);
			if ((key ==null) && (b64key ==null)) {
				key = request.getAttributes().get(smartsKey);
				if (key==null) {
					if (freetextQuery == null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Empty smarts");
					else {
						setTemplate(createTemplate(context, request, response));
						return freetextQuery;
					}
				}
			}
			
			boolean fp1024_screenining = true;
			if (form.getFirstValue("fp1024")!=null)
			try {
				fp1024_screenining = Boolean.parseBoolean(form.getFirstValue("fp1024"));
			} catch (Exception x) {
				fp1024_screenining = true;
			}
			boolean sk1024_screenining = true;
			if (form.getFirstValue("sk1024")!=null)
				try {
					sk1024_screenining = Boolean.parseBoolean(form.getFirstValue("sk1024"));
				} catch (Exception x) {
					sk1024_screenining = true;
				}			
			
			boolean read_atomprops = true;
			if (form.getFirstValue("atomproperties")!=null)
				try {
					read_atomprops = Boolean.parseBoolean(form.getFirstValue("atomproperties"));
				} catch (Exception x) {
					read_atomprops = true;
				}	
				
			String smarts = getSMILES(form,true);
			
			QuerySMARTS query = new QuerySMARTS();
			query.setFp1024_screening(fp1024_screenining);
			query.setSk1024_screening(sk1024_screenining);
			query.setUsePrecalculatedAtomProperties(read_atomprops);
			query.setChemicalsOnly(true);
			if (smarts.startsWith(AmbitCONSTANTS.INCHI)) 
				smarts = inchi2smiles(smarts);
			else {
				String smiles = name2smiles(smarts);
				if (smiles!=null) smarts = smiles;
			}
			query.setValue(new FunctionalGroup(smarts,smarts,smarts));
			try {
				if (smarts != null) query.prepareScreening();
			} catch (Exception x) {
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
			}
			
			try {
				
				Object cmpid = request.getAttributes().get(CompoundResource.idcompound);
				if (cmpid != null) {
					IStructureRecord record = new StructureRecord();
					record.setIdchemical(Integer.parseInt(Reference.decode(cmpid.toString())));
					QueryStructureByID scope = new QueryStructureByID();
					scope.setPageSize(1);
					scope.setChemicalsOnly(true);
					scope.setValue(record);
					
					QueryCombinedStructure combined = new QueryCombinedStructure();
					combined.setChemicalsOnly(true);
					combined.setCombine_as_and(true);
					combined.add(query);
					combined.setScope(scope);
					setTemplate(createTemplate(context, request, response));
					return combined;
				}
				
				Object datasetid = request.getAttributes().get(DatasetResource.datasetKey);
				if (datasetid != null) {
					QueryDatasetByID scope = new QueryDatasetByID();
					this.dataset_id=Reference.decode(datasetid.toString());
					setTemplate(createTemplate(context, request, response));
					scope.setValue(new Integer(dataset_id));
					
					QueryCombinedStructure combined = new DatasetQueryCombined();
					combined.setCombine_as_and(true);
					combined.add(query);
					if (freetextQuery!= null) combined.add(freetextQuery);
					combined.setScope(scope);
					return combined;
				} 
				String[] folder = form.getValuesArray("folder");
				if ((folder!=null) && (folder.length>0)) {
					ChemicalByAssessment qa = new ChemicalByAssessment(folder);
					QueryCombinedStructure qc = new QueryCombinedStructure();
					qc.add(query);
					qc.setChemicalsOnly(true);
					qc.setScope(qa);
					setTemplate(createTemplate(context, request, response));
					return qc;
				}	
				if (freetextQuery != null) {
					QueryCombinedStructure combined = new MyQueryCombined();
					combined.setChemicalsOnly(true);
					combined.setCombine_as_and(true);
					combined.add(query);
					combined.setScope(freetextQuery);
					setTemplate(createTemplate(context, request, response));			
					return combined;
				}
				
			} catch (NumberFormatException x) {
				throw new ResourceException(
						Status.CLIENT_ERROR_BAD_REQUEST,String.format("Invalid resource id %s",
								getResourceRef(getRequest())),x
						);				
			} catch (Exception x) {
				throw new ResourceException(
						Status.SERVER_ERROR_INTERNAL,x.getMessage(),x
						);
			}
			setTemplate(createTemplate(context, request, response));
			return query;
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(
					Status.SERVER_ERROR_INTERNAL,x.getMessage(),x
					);
		}
	}		

	
	protected String inchi2smiles(String inchi) {
		try {
			InChIGeneratorFactory f = InChIGeneratorFactory.getInstance();
			InChIToStructure c = f.getInChIToStructure(inchi, SilentChemObjectBuilder.getInstance());
			if ((c==null) || (c.getAtomContainer()==null) || (c.getAtomContainer().getAtomCount()==0)) return null;
			SmilesGenerator g = new SmilesGenerator();
			g.setUseAromaticityFlag(true);
			return g.createSMILES(c.getAtomContainer());
		} catch (Exception x) {
			return null;
		}
	}
	
	
	protected String name2smiles(String name) {
		try {
			NameToStructure name2structure = NameToStructure.getInstance();
			return name2structure.parseToSmiles(name);
		} catch (Exception x) {
			return null;
		}
	}
}

class MyQueryCombined extends QueryCombinedStructure { 
	/**
	 * 
	 */
	private static final long serialVersionUID = -214359046685832984L;
	@Override
	protected String groupBy() {
		return "";
	}
	protected String getScopeSQL() {
		if (isCombine_as_and())
			return "select Q1.idquery,s.idchemical,-1,Q1.selected as selected,Q1.metric as metric from chemicals as s";
		else
			return "select QSCOPE.idquery,s.idchemical,-1,QSCOPE.selected as selected,QSCOPE.metric as metric from chemicals as s";
	
	}			
	@Override
	protected String getMainSQL() {
		return "select Q1.idquery,s.idchemical,-1,Q1.selected as selected,Q1.metric as metric from chemicals as s\n";
	}						
};

class DatasetQueryCombined extends  QueryCombinedStructure {
	/**
	 * 
	 */
	private static final long serialVersionUID = 64174880636638320L;
	@Override
	protected String getMainSQL() {
		return "select Q1.idquery,s.idchemical,-1,Q1.selected as selected,Q1.metric as metric from chemicals as s\n";
	}
	
	@Override
	protected String groupBy() {
		return " group by idchemical";
	}
	@Override
	protected String joinOn() {
		return "idchemical";
	}
};