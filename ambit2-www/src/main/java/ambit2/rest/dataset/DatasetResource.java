package ambit2.rest.dataset;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.routing.Template;

import ambit2.base.data.SourceDataset;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.QueryExecutor;
import ambit2.db.search.StoredQuery;
import ambit2.db.search.structure.QueryCombinedStructure;
import ambit2.db.search.structure.QueryComplement;
import ambit2.db.update.AbstractUpdate;
import ambit2.db.update.dataset.DatasetDeleteStructure;
import ambit2.db.update.dataset.DeleteDataset;
import ambit2.db.update.dataset.ReadDataset;
import ambit2.db.update.storedquery.DeleteStoredQuery;
import ambit2.db.update.structure.ChemicalByDataset;
import ambit2.rest.DBConnection;
import ambit2.rest.OpenTox;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.task.CallableQueryResultsCreator;
import ambit2.rest.task.CallableUpdateDataset;


/**
 * /dataset POST create new dataset
 * /dataset/x POST delete the current content and write new one 
 * /dataset/x PUT - replace/add
 * /DELETE/x  
 * Dataset resource - A set of chemical compounds and assigned features
 * 
 * @author nina
 *
 */
public class DatasetResource<Q extends IQueryRetrieval<IStructureRecord>> extends DatasetStructuresResource<Q> {
	public final static String dataset_complement_uri = "complement";
	public final static String dataset_intersection_uri = "intersection";
	protected FileUpload upload;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		upload = new FileUpload();
		upload.setRequest(getRequest());
		upload.setResponse(getResponse());
		upload.setContext(getContext());
		upload.setApplication(getApplication());
		upload.setDataset(null);
	}
/*
 *
select count(idchemical) from
structure s1 join struc_dataset d1 using(idstructure)
join structure s2 using(idchemical)
join struc_dataset d2 on s2.idstructure=d2.idstructure
where d1.id_srcdataset=8 and d2.id_srcdataset=6
 */
	@Override
	protected String getDefaultTemplateURI(Context context, Request request,Response response) {
		Object id = request.getAttributes().get(datasetKey);
		if (id != null)
			//return String.format("riap://application/dataset/%s%s",id,PropertyResource.featuredef);
		return String.format("%s%s/%s%s",
				getRequest().getRootRef(),OpenTox.URI.dataset.getURI(),id,PropertyResource.featuredef);		
		else 
			return super.getDefaultTemplateURI(context,request,response);
			
	}
	/**
	 * Finds compounds which are in the dataset {dataset_id} , but not in the datasets , 
	 * given in "complement" query 
	 * Two sets can also be "subtracted". The relative complement of A in B 
	 * (also called the set theoretic difference of B and A), denoted by \A, or (B-A)
	 * is the set of all elements which are members of B, but not members of A
	 * @param context
	 * @param request
	 * @param response
	 * @return
	 * @throws ResourceException
	 */
	protected Q createQueryComplement(Context context, Request request,
			Response response) throws ResourceException {
		
		
		Form form = request.getResourceRef().getQueryAsForm();
		String[] datasetsURI =  form.getValuesArray(dataset_complement_uri);
		if ((datasetsURI != null) && (datasetsURI.length>0)) {
			QueryComplement qc = new QueryComplement();
			qc.setChemicalsOnly(true);
			try {
				ChemicalByDataset  cd = new ChemicalByDataset(new Integer(getRequest().getAttributes().get(datasetKey).toString()));
				qc.setScope(cd);
			} catch (Exception x) {}
			Template t = new Template(String.format("%s%s/{%s}",getRequest().getRootRef(),DatasetStructuresResource.dataset,DatasetStructuresResource.datasetKey));
			for (String datasetURI: datasetsURI ) {
				Map<String, Object> vars = new HashMap<String, Object>();
				t.parse(datasetURI, vars);
				try {
					qc.add(new ChemicalByDataset(new Integer(vars.get(DatasetStructuresResource.datasetKey).toString())));
				} catch (Exception x) {
					
				}
			}
			return (Q)qc;
		} 
		return null;
		
	}	
	protected Q createQueryIntersection(Context context, Request request,
			Response response) throws ResourceException {
		
		
		Form form = request.getResourceRef().getQueryAsForm();
		String[] datasetsURI =  form.getValuesArray(dataset_intersection_uri);
		if ((datasetsURI != null) && (datasetsURI.length>0)) {
			QueryCombinedStructure qc = new QueryCombinedStructure() {
				@Override
				protected String getMainSQL() {
					return "select idchemical from chemicals\n";
				}
				@Override
				protected String groupBy() {
					return "";
				}
			};
			qc.setChemicalsOnly(true);
			qc.setCombine_as_and(true);
			try {
			ChemicalByDataset  cd = new ChemicalByDataset(new Integer(getRequest().getAttributes().get(datasetKey).toString()));
			qc.add(cd);
			} catch (Exception x) {}
			Template t = new Template(String.format("%s%s/{%s}",getRequest().getRootRef(),DatasetStructuresResource.dataset,DatasetStructuresResource.datasetKey));
			for (String datasetURI: datasetsURI ) {
				Map<String, Object> vars = new HashMap<String, Object>();
				t.parse(datasetURI, vars);
				try {
					qc.add(new ChemicalByDataset(new Integer(vars.get(DatasetStructuresResource.datasetKey).toString())));
				} catch (Exception x) {
					
				}
			}
			return (Q)qc;
		} 
		return null;
		
	}
	@Override
	protected Q createQuery(Context context, Request request,
			Response response) throws ResourceException {
		
		try {
			
			Q q = createQueryIntersection(context, request, response);
			if (q != null)  {
				setTemplate(createTemplate(context, request, response));
				return q;
			} else {
				q = createQueryComplement(context, request, response);
				if (q != null) {
					setTemplate(createTemplate(context, request, response));
					return q;
				}
				else
					return  super.createQuery(context, request, response);
			}

		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
		}
		
	}
	
	/**
	 * Creates new entry in query table and adds structures into query_results
	 */
	protected Representation copyDatasetToQueryResultsTable(Form form, boolean clearPreviousContent)
			throws ResourceException {
		Callable<Reference> callable = null;
		if ((queryResultsID!=null) && (queryResultsID>0)) {
			
			callable = new CallableQueryResultsCreator(
					form,
					getRequest().getRootRef(),
					getContext(),
					new StoredQuery(queryResultsID));
			((CallableQueryResultsCreator)callable).setClearPreviousContent(clearPreviousContent);
		} else if ((datasetID!=null) && (datasetID>0)) {
			//PUT only for compound_uris[]=... & feature_uris[]=....
			//same for SourceDataset
			SourceDataset dataset = readDataset();
			//reading from dataset uri)
			callable = new CallableUpdateDataset(
					form,
					getRequest().getRootRef(),
					getContext(),
					dataset,
					new DatasetURIReporter<IQueryRetrieval<SourceDataset>>(getRequest())
					);
			((CallableUpdateDataset)callable).setClearPreviousContent(clearPreviousContent);
		} else {
			//POST only - creating a new dataset is via DatasetsResource , should not come here
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		try {
			getResponse().setLocationRef(callable.call());
			getResponse().setStatus(Status.SUCCESS_OK);
			return new StringRepresentation(getResponse().getLocationRef().toString(),MediaType.TEXT_URI_LIST);
		} catch  (Exception x) {
			throw new ResourceException(x);
		}

	}		
	
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		
		if ((entity == null) || !entity.isAvailable()) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Empty content");
		
		if (MediaType.APPLICATION_WWW_FORM.equals(entity.getMediaType())) {
			return copyDatasetToQueryResultsTable(new Form(entity),true);
		} else {
			upload.setDataset(null);
			return  upload.upload(entity,variant,true);
		}
	}
	protected SourceDataset readDataset() throws ResourceException {
		SourceDataset dataset = new SourceDataset();
		dataset.setId(datasetID);
		Connection c = null;
		ResultSet rs = null;
		try {
			DBConnection dbc = new DBConnection(getContext());
			c = dbc.getConnection();
			ReadDataset read = new ReadDataset();
			read.setValue(dataset);
			QueryExecutor x = new QueryExecutor();
			x.setConnection(c);
			rs = x.process(read);
			while (rs.next()) {
				dataset = read.getObject(rs);
			}
			return dataset;
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x.getMessage(),x);
		} finally {
			try { rs.close(); } catch (Exception x) {}
			try { c.close(); } catch (Exception x) {}
		}		
	}
	@Override
	protected Representation put(Representation entity, Variant variant)
			throws ResourceException {
		
		if ((entity == null) || !entity.isAvailable()) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Empty content");
		
	if (MediaType.APPLICATION_WWW_FORM.equals(entity.getMediaType())) {
			return copyDatasetToQueryResultsTable(new Form(entity),false);
		} else if ((datasetID!=null) && (datasetID>0)) {
			SourceDataset dataset = readDataset();
 			upload.setDataset(dataset);
			return  upload.upload(entity,variant,true);
		} else throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED);
	}

	@Override
	protected AbstractUpdate createDeleteObject(IStructureRecord entry)
			throws ResourceException {
		if ((queryResultsID!=null) && (queryResultsID>0)) {
			DeleteStoredQuery c =  new DeleteStoredQuery();
			c.setObject(new StoredQuery(queryResultsID));
			return c;
		} else if ((datasetID!=null) && (datasetID>0)) {
			DeleteDataset c =  new DeleteDataset();
			SourceDataset d = new SourceDataset();
			d.setId(datasetID);
			c.setObject(d);
			return c;
		} else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	}
	
	
	@Override
	protected Representation delete(Variant variant) throws ResourceException {
		
		try {
			Form form = getRequest().getResourceRef().getQueryAsForm();
			String[] compounds = OpenTox.params.compound_uris.getValuesArray(form);
			if ((compounds != null) && (compounds.length>0)) { //partial delete
				try {
					DatasetDeleteStructure deleteObject;
					if ((datasetID!=null) && (datasetID>0)) {
						deleteObject = new DatasetDeleteStructure();
						SourceDataset d = new SourceDataset();
						d.setId(datasetID);						
						deleteObject.setGroup(d);
					} else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
					
					
					IStructureRecord record = new StructureRecord();
					for (String compound: compounds ) {
						if (compound.indexOf("conformer")>0) {
							Object[] ids = OpenTox.URI.conformer.getIds(compound, getRequest().getRootRef());
							record.setIdchemical((Integer)ids[0]);
							record.setIdstructure((Integer)ids[1]);
						} else {
							Object ids = OpenTox.URI.compound.getId(compound, getRequest().getRootRef());
							record.setIdchemical((Integer)ids);
							record.setIdstructure(-1);							
						}
						
						deleteObject.setObject(record);
						executeUpdate(form.getWebRepresentation(), 	null,	deleteObject);						
					}
				} catch (ResourceException x) {
					throw x;
				} catch (Exception x) {
					throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
				}
				//String[] features = OpenTox.params.feature_uris.getValuesArray(form);
			} else  //delete entire dataset
				executeUpdate(getRequestEntity(), 
						null,
						createDeleteObject(null));
				return getResponseEntity();
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
		}
	}
	@Override
	protected QueryURIReporter<IStructureRecord, Q> getURUReporter(
			Request baseReference) throws ResourceException {
		return null;
	}
}
