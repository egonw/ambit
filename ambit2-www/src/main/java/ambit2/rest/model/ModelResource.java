package ambit2.rest.model;

import java.sql.Connection;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.core.data.model.Algorithm.AlgorithmFormat;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.update.model.ReadModel;
import ambit2.rest.OpenTox;
import ambit2.rest.OutputWriterConvertor;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.RDFJenaConvertor;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.query.ProcessingResource;
import ambit2.rest.query.QueryResource;
import ambit2.rest.task.CallableDescriptorCalculator;
import ambit2.rest.task.CallableQueryProcessor;
import ambit2.rest.task.CallableWekaPredictor;

/**
 * Model as in http://opentox.org/development/wiki/Model
 * Supported REST operation:
 * GET 	 /model<br>
 * GET 	 /model/{id}
 * @author nina
 *
 */
public class ModelResource extends ProcessingResource<IQueryRetrieval<ModelQueryResults>, ModelQueryResults> {

	
	public final static String resource = OpenTox.URI.model.getURI();
	public final static String resourceKey =  OpenTox.URI.model.getKey();
	public final static String resourceID = OpenTox.URI.model.getResourceID();
	protected boolean collapsed = true;
	
	public enum modeltypes  {
		pka,toxtree
	};
	
	protected String category = "";

	protected Object getModelID(Object id) throws ResourceException {
		
		if (id != null) try {
			id = Reference.decode(id.toString());
			collapsed = false;
			return new Integer(id.toString());
			
		} catch (NumberFormatException x) {
			return id;
		} catch (Exception x) {
			return null;
		} else return null;

	}
	@Override
	protected IQueryRetrieval<ModelQueryResults> createQuery(Context context,
			Request request, Response response) throws ResourceException {
		
		ReadModel query = getModelQuery(getModelID(getRequest().getAttributes().get(resourceKey)));
		Form form = getRequest().getResourceRef().getQueryAsForm();
		String name = form.getFirstValue(QueryResource.search_param);
		if (name!=null) query.setFieldname(name);
		collapsed = query.getValue()!=null;
		return query;
	}
	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException, ResourceException {
	if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
		return new OutputWriterConvertor(
				new ModelHTMLReporter(getRequest(),collapsed),MediaType.TEXT_HTML);
	} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
		return new StringConvertor(	new ModelURIReporter<IQueryRetrieval<ModelQueryResults>>(getRequest()) {
			@Override
			public Object processItem(ModelQueryResults dataset) throws AmbitException  {
				super.processItem(dataset);
				try {
				output.write('\n');
				} catch (Exception x) {}
				return null;
			}
		},MediaType.TEXT_URI_LIST);
	} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML) ||
			variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE) ||
			variant.getMediaType().equals(MediaType.TEXT_RDF_N3) ||
			variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES)
			) {
		return new RDFJenaConvertor<ModelQueryResults,IQueryRetrieval<ModelQueryResults>>(
				new ModelRDFReporter<IQueryRetrieval<ModelQueryResults>>(getRequest(),variant.getMediaType())
				,variant.getMediaType());			
	} else //html 	
		return new OutputWriterConvertor(
				new ModelHTMLReporter(getRequest(),collapsed),MediaType.TEXT_HTML);
	}
	
	@Override
	protected QueryURIReporter<ModelQueryResults, IQueryRetrieval<ModelQueryResults>> getURUReporter(
			Request baseReference) throws ResourceException {
		return new ModelURIReporter<IQueryRetrieval<ModelQueryResults>>(getRequest());
	}
	
	@Override
	protected CallableQueryProcessor createCallable(Form form,	ModelQueryResults model) throws ResourceException {

		try {
			if (model.getContentMediaType().equals(AlgorithmFormat.WEKA.getMediaType()))
				return //reads Instances, instead of IStructureRecord
				new CallableWekaPredictor(
						form,
						getRequest().getRootRef(),
						getContext(),
						model,
						new ModelURIReporter<IQueryRetrieval<ModelQueryResults>>(getRequest()));
			else if (model.getContentMediaType().equals(AlgorithmFormat.JAVA_CLASS.getMediaType())) {
				return
				new CallableDescriptorCalculator(
						form,
						getRequest().getRootRef(),
						getContext(),
						model,
						new ModelURIReporter<IQueryRetrieval<ModelQueryResults>>(getRequest()));
		} else throw new ResourceException(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE,model.getContentMediaType());
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x.getMessage(),x);
		} finally {

		}
	}
	/*
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		synchronized (this) {
			Form form = getRequest().getResourceRef().getQueryAsForm();
			Object datasetURI = form.getFirstValue(dataset_uri);
			if (datasetURI==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,String.format("Empty %s", dataset_uri));
			final Reference reference = new Reference(Reference.decode(datasetURI.toString()));
			
			//models
			IQueryRetrieval<ModelQueryResults> query = createQuery(getContext(),getRequest(),getResponse());
			if (query==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			
			
			Connection conn = null;
			
			QueryReporter<ModelQueryResults,IQueryRetrieval<ModelQueryResults>,Object> readModels = new QueryReporter<ModelQueryResults,IQueryRetrieval<ModelQueryResults>,Object>() {
				@Override
				public Object processItem(ModelQueryResults model) throws AmbitException {
					try {
						Reference ref =  ((AmbitApplication)getApplication()).addTask(
								String.format("Apply model %s to %s",model.toString(),reference),
								new CallableModelPredictor(
										reference,
										getRequest().getRootRef(),
										(AmbitApplication)getApplication(),
										model,
										new ModelURIReporter<IQueryRetrieval<ModelQueryResults>>(getRequest())),	
								getRequest().getRootRef());		
						getResponse().setLocationRef(ref);
						//getResponse().setStatus(Status.SUCCESS_CREATED);
						getResponse().setStatus(Status.REDIRECTION_SEE_OTHER);
						getResponse().setEntity(null);
					} catch (Exception x) {
						if (x.getCause() instanceof ResourceException)
							getResponse().setStatus( ((ResourceException)x.getCause()).getStatus());
						else
							getResponse().setStatus(new Status(Status.SERVER_ERROR_INTERNAL,x.getMessage()));
					}
					return null;
					
				}
				public void open() throws DbAmbitException {};
				@Override
				public void header(Object output, IQueryRetrieval<ModelQueryResults> query) {};
				@Override
				public void footer(Object output, IQueryRetrieval<ModelQueryResults> query) {};
					
			};
			try {
	    		conn = ((AmbitApplication)getApplication()).getConnection(getRequest());
	    		if (conn.isClosed()) conn = ((AmbitApplication)getApplication()).getConnection(getRequest());
	    		readModels.setConnection(conn);
				readModels.process(query);		
				return getResponse().getEntity();
			} catch (AmbitException x) {
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
			} catch (SQLException x) {
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
			} finally {
				try { conn.close();} catch  (Exception x) {}
			}
		}
	}
	*/
	protected ReadModel getModelQuery(Object idmodel) throws ResourceException {
		if (idmodel == null) return new ReadModel();
		else if (idmodel instanceof Integer)
			return new ReadModel((Integer)idmodel);
		else {
			ReadModel query = new ReadModel(null);
			query.setFieldname(idmodel.toString());
			return query;
		}
	}
	
}

