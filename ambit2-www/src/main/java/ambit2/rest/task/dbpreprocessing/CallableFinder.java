package ambit2.rest.task.dbpreprocessing;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

import org.opentox.dsl.OTDataset;
import org.opentox.dsl.OTFeature;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.ProcessorsChain;
import ambit2.base.processors.search.AbstractFinder;
import ambit2.base.processors.search.AbstractFinder.MODE;
import ambit2.base.processors.search.AbstractFinder.SITE;
import ambit2.chebi.ChebiFinder;
import ambit2.core.data.model.Algorithm;
import ambit2.db.DbReaderStructure;
import ambit2.db.processors.AbstractBatchProcessor;
import ambit2.db.processors.AbstractRepositoryWriter.OP;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.processors.RepositoryWriter;
import ambit2.db.readers.RetrieveStructure;
import ambit2.db.search.QueryExecutor;
import ambit2.db.search.property.RetrieveProfile;
import ambit2.db.search.property.RetrieveProfile.QueryMode;
import ambit2.db.search.property.ValuesReader;
import ambit2.db.search.structure.AbstractStructureQuery;
import ambit2.db.search.structure.QueryStructureByID;
import ambit2.db.update.dataset.ReadDataset;
import ambit2.namestructure.Name2StructureFinder;
import ambit2.pubchem.EntrezSearchProcessor;
import ambit2.pubchem.PubchemFinder;
import ambit2.rest.DBConnection;
import ambit2.rest.OpenTox;
import ambit2.rest.dataset.RDFStructuresReader;
import ambit2.rest.rdf.RDFPropertyIterator;
import ambit2.rest.task.CallableQueryProcessor;
import ambit2.rest.task.TaskResult;
import ambit2.search.AllSourcesFinder;
import ambit2.search.chemidplus.ChemIdPlusRequest;
import ambit2.search.csls.CSLSStringRequest;
import ambit2.search.opentox.OpenToxRequest;

/**
 * http://chem.sis.nlm.nih.gov/molfiles/0000050000.mol chemidplus
 *  cas bez tireta i dopqlnen do 10 znaka s prefix ot nuli
 * @author nina
 *
 * @param <USERID>
 */
public class CallableFinder<USERID> extends	CallableQueryProcessor<Object, IStructureRecord,USERID>  {

	protected Reference applicationRootReference;
	protected Template profile;
	protected AbstractFinder.SITE searchSite ;
	protected AbstractFinder.MODE mode;
	
	public CallableFinder(Form form,
			Reference applicationRootReference,Context context,
			Algorithm algorithm,USERID token) {
		super(applicationRootReference,form,context,token);
		this.applicationRootReference = applicationRootReference;
	}
	@Override
	protected void processForm(Reference applicationRootReference,Form form) {
		this.applicationRootReference = applicationRootReference;
		//mode
		Object modeParam = form.getFirstValue("mode");
		try {
			if (modeParam != null)
				this.mode = MODE.valueOf(modeParam.toString().toLowerCase());
			else this.mode = MODE.emptyonly;
		} catch (Exception x) {
			this.mode = MODE.emptyonly;
		}
		//search
		Object search = form.getFirstValue("search");
		try {
			if (search != null)
				searchSite = SITE.valueOf(search.toString().toUpperCase());
			else
				searchSite = SITE.CIR;
		} catch (Exception x) {
			searchSite = SITE.CIR;
		}
		//dataset
		Object dataset = OpenTox.params.dataset_uri.getFirstValue(form);
		//
		String[] xvars = OpenTox.params.feature_uris.getValuesArray(form);
		if (xvars != null) try {
			profile = new Template();
			
			OTDataset ds = OTDataset.dataset(dataset.toString());
			for (String xvar :xvars) {
				String[] xx = xvar.split("\n");
				for (String x : xx )
					if (!x.trim().equals("")) {
						ds = ds.addColumns(OTFeature.feature(x));
						Property key = createPropertyFromReference(x,null);
						key.setEnabled(true);
						profile.add(key);
					}
			}
			dataset =  ds.getUri().toString();

		} catch (Exception x) {
			
		}

		this.sourceReference = dataset==null?null:new Reference(dataset.toString().trim());
	}
	
	@Override
	protected ProcessorsChain<IStructureRecord, IBatchStatistics, IProcessor> createProcessors()
			throws Exception {
		ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor> p = 
			new ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor>();

		RetrieveStructure r = new RetrieveStructure(false); //exactly our structure, nothing else
		r.setPageSize(1);
		r.setPage(0);
		p.add(new ProcessorStructureRetrieval(r));

		IProcessor<IStructureRecord,IStructureRecord> valuesReader = getValuesReader();
		if (valuesReader!=null) p.add(valuesReader);
		
		IProcessor<String,String> request;
		LiteratureEntry le;

		switch (searchSite) {
		case PUBCHEM: {
			p.add(new PubchemFinder(profile,mode));
			le =  new LiteratureEntry("PubChem",EntrezSearchProcessor.entrezURL);
			break;
		}
		case OPENTOX: {
			request = new OpenToxRequest(searchSite.getURI());
			p.add(new AllSourcesFinder(profile,request,mode));
			le =  new LiteratureEntry(request.toString(),request.toString());
			break;
		}		
		case NAME2STRUCTURE: {
			p.add(new Name2StructureFinder(profile,mode));
			le =  new LiteratureEntry(searchSite.getTitle(),searchSite.getURI());
			break;
		}
		case CHEBI: {
			p.add(new ChebiFinder(profile,mode));
			le =  new LiteratureEntry("ChEBI","http://www.ebi.ac.uk/chebi");
			break;
		}
		/*
		case CHEMBL: {
			p.add(new ChemBLFinder(profile,mode));
			le =  new LiteratureEntry(searchSite.name(),"https://www.ebi.ac.uk/chemblws");
			break;
		}	
		*/	
		case CHEMIDPLUS: {
			request = new ChemIdPlusRequest();
			p.add(new AllSourcesFinder(profile,request,mode));
			le =  new LiteratureEntry(request.toString(),request.toString());
			break;
		}
		default : {
			request = new CSLSStringRequest();
			p.add(new AllSourcesFinder(profile,request,mode));
			le =  new LiteratureEntry(request.toString(),request.toString());
			break;
		}
		}
		//don't duplicate if there is already a hit
		SourceDataset dataset = new SourceDataset(searchSite.toString(),le);
		dataset = getDataset(dataset);
		
		QueryStructureByID q = new QueryStructureByID();
		q.setChemicalsOnly(true);
		q.setFieldname(dataset);
		q.setPageSize(1);
		q.setPage(0);
		p.add(new ProcessorStructureRetrieval(q) {
			@Override
			public IStructureRecord process(IStructureRecord target)
					throws AmbitException {
				if (target==null) return null;
				if (((QueryStructureByID)getQuery()).getFieldname().getID()>=0) {
					IStructureRecord record = super.process(target);
					target.setIdchemical(record.getIdchemical());
					target.setIdstructure(record.getIdstructure());
					return target;
				} else return target;
			}
		});		
		
		RepositoryWriter writer = new RepositoryWriter() {
			@Override
			public List<IStructureRecord> process(IStructureRecord target)
					throws AmbitException {
				if (target == null) return null;
				return super.process(target);
			}
		};
		writer.setOperation(OP.UPDATE);
		writer.setDataset(dataset);
		p.add(writer);
		return p;
	}
	
	protected SourceDataset getDataset(SourceDataset dataset) {
		
		QueryExecutor exec = new QueryExecutor();
		Connection connection = null;
		try {
			ReadDataset q = new ReadDataset();
			q.setPage(0);
			q.setPageSize(1);
			q.setValue(dataset);
			DBConnection dbc = new DBConnection(context);
			connection = dbc.getConnection();
			exec.setConnection(connection);
			ResultSet rs = exec.process(q);
			while (rs.next()) {
				dataset = q.getObject(rs);
			}
			try {exec.closeResults(rs);} catch (Exception x) {};
		} catch (Exception x) {
			//if failed will return the original dataset
		} finally {
			try {exec.close();} catch (Exception x) {};
			try {connection.close();} catch (Exception x) {};
		}
		return dataset;
	}

	@Override
	protected TaskResult createReference(Connection connection)
			throws Exception {
		return new TaskResult(sourceReference.toString(),false);
	}
	@Override
	protected AbstractBatchProcessor createBatch(Object target)
			throws Exception {
		if (target == null) throw new Exception("");
		if (target instanceof AbstractStructureQuery) {
			DbReaderStructure reader = new DbReaderStructure();
			reader.setHandlePrescreen(true);
			return reader;
		} else
			return new RDFStructuresReader(target.toString());
	}
	@Override
	protected Object createTarget(Reference reference) throws Exception {
		if (profile.size()==0) throw new Exception("No columns to search!");
		try {
			Object q = getQueryObject(reference, applicationRootReference);
			return q==null?reference:q;
		} catch (Exception x) {
			return reference;
		}
	}
	protected Property createPropertyFromReference(String attribute, LiteratureEntry le) {
		RDFPropertyIterator reader = null;
		try {
			reader = new RDFPropertyIterator(new Reference(attribute));
			reader.setBaseReference(applicationRootReference);
			while (reader.hasNext()) {
				return reader.next();
			}
			return null;	
		} catch (Exception x) {
			return new Property(attribute.toString(),le);
		} finally {
			try {reader.close(); } catch (Exception x) {}
		}
	}	
	
	protected IProcessor<IStructureRecord,IStructureRecord> getValuesReader() {
		if  (profile.size()>0) {
			ValuesReader readProfile = new ValuesReader(null);  //no reader necessary
			RetrieveProfile propertyQuery = new RetrieveProfile(QueryMode.idproperty);
			propertyQuery.setValue(profile);
			readProfile.setProfile(profile);
			readProfile.setPropertyQuery(propertyQuery);
			return readProfile;
		} else return null;
	}
}

