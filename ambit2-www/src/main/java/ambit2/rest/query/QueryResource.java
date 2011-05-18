package ambit2.rest.query;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.CharacterSet;
import org.restlet.data.CookieSetting;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.wadl.WadlRepresentation;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.exceptions.NotFoundException;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.processors.ProcessorException;
import ambit2.base.processors.Reporter;
import ambit2.db.IDBProcessor;
import ambit2.db.UpdateExecutor;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.IQueryObject;
import ambit2.db.update.AbstractUpdate;
import ambit2.rest.AbstractResource;
import ambit2.rest.DBConnection;
import ambit2.rest.OpenTox;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.TaskApplication;
import ambit2.rest.exception.RResourceException;
import ambit2.rest.property.ProfileReader;
import ambit2.rest.rdf.RDFObjectIterator;
import ambit2.rest.task.AmbitFactoryTaskConvertor;
import ambit2.rest.task.CallableQueryProcessor;
import ambit2.rest.task.FactoryTaskConvertor;
import ambit2.rest.task.ICallableTask;
import ambit2.rest.task.ITaskStorage;
import ambit2.rest.task.Task;
import ambit2.rest.task.TaskCreator;

/**
 * Abstract parent class for all resources , which retrieves something from the database
 * @author nina
 *
 * @param <Q>
 * @param <T>
 */
public abstract class QueryResource<Q extends IQueryRetrieval<T>,T extends Serializable>  extends AbstractResource<Q,T,IProcessor<Q,Representation>> {
	protected enum RDF_WRITER  {
		jena,
		stax
	}
	protected RDF_WRITER rdfwriter = RDF_WRITER.jena;
	protected boolean dataset_prefixed_compound_uri = false;
	public final static String query_resource = "/query";
	
	/**TODO
	 * http://markmail.org/search/?q=restlet+statusservice+variant#query:restlet%20statusservice%20variant+page:1+mid:2qrzgzbendopxg5t+state:results
an alternate design where you would leverage the new RepresentationInfo class added to Restlet 2.0 
by overriding the "ServerResource#getInfo(Variant)" method. 
This would allow you to support content negotiation and conditional processing
 without having to connect to your database.
Then, when the "get(Variant)" method calls you back,
 you would connect to your database, throw any exception that occurs and return a verified representation. 
	 */


	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] {
				MediaType.TEXT_HTML,
				MediaType.TEXT_PLAIN,
				MediaType.TEXT_URI_LIST,
				MediaType.TEXT_CSV,
				MediaType.APPLICATION_RDF_XML,
				MediaType.APPLICATION_RDF_TURTLE,
				MediaType.TEXT_RDF_N3,
				MediaType.TEXT_RDF_NTRIPLES,
				MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JAVA_OBJECT,
				MediaType.APPLICATION_WADL
				
		});		
		if (queryObject!=null) {
			Form form = getParams();
			setPaging(form, queryObject);
		}

	}	
	protected Form getParams() {
		return getRequest().getResourceRef().getQueryAsForm();
	}
	/*
	protected Connection getConnection() throws SQLException , AmbitException {
		Connection connection = ((AmbitApplication)getApplication()).getConnection(getRequest());
		if (connection.isClosed()) connection = ((AmbitApplication)getApplication()).getConnection(getRequest());
		return connection;
	}
	*/
	protected  Q returnQueryObject() {
		return queryObject;
	}
	
	protected void configureDatasetMembersPrefixOption(boolean prefix) {
		dataset_prefixed_compound_uri = prefix;
	}
	protected void configureRDFWriterOption(String defaultWriter) {
		try { 
			Object jenaOption = getRequest().getResourceRef().getQueryAsForm().getFirstValue("rdfwriter");
			//if no option ?rdfwriter=jena|stax , then take from properties rdf.writer
			//if not defined there, use jena
			rdfwriter = RDF_WRITER.valueOf(jenaOption==null?defaultWriter:jenaOption.toString().toLowerCase());
		} catch (Exception x) { 
			rdfwriter = RDF_WRITER.jena;
		}
	}
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		try {
			CookieSetting cS = new CookieSetting(0, "subjectid", getToken());
			cS.setPath("/");
	        this.getResponse().getCookieSettings().add(cS);
	        
			int maxRetry=3;
			if (variant.getMediaType().equals(MediaType.APPLICATION_WADL)) {
				return new WadlRepresentation();
			} else	
        	if (MediaType.APPLICATION_JAVA_OBJECT.equals(variant.getMediaType())) {
        		if ((queryObject!=null) && (queryObject instanceof Serializable))
        		return new ObjectRepresentation((Serializable)returnQueryObject(),MediaType.APPLICATION_JAVA_OBJECT);
        		else throw new ResourceException(Status.CLIENT_ERROR_NOT_ACCEPTABLE);        		
        	}				
	        if (queryObject != null) {
        	
	        	IProcessor<Q, Representation>  convertor = null;
	        	Connection connection = null;
	        	int retry=0;
	        	while (retry <maxRetry) {
		        	try {
		        		DBConnection dbc = new DBConnection(getContext());
		        		configureRDFWriterOption(dbc.rdfWriter());
		        		configureDatasetMembersPrefixOption(dbc.dataset_prefixed_compound_uri());
		        		convertor = createConvertor(variant);

		        		connection = dbc.getConnection(getRequest());
		        		Reporter reporter = ((RepresentationConvertor)convertor).getReporter();
			        	if (reporter instanceof IDBProcessor)
			        		((IDBProcessor)reporter).setConnection(connection);
			        	Representation r = convertor.process(queryObject);
			        	r.setCharacterSet(CharacterSet.UTF_8);
			        	return r;
			        	
		        	} catch (ResourceException x) {
		    			throw x;			        	
		        	} catch (NotFoundException x) {
		        		processNotFound(x,retry);
		    			
		        	} catch (SQLException x) {
		        		Context.getCurrentLogger().severe(x.getMessage());
		        		if (retry <maxRetry) {
		        			retry++;
		        			getResponse().setStatus(Status.SERVER_ERROR_SERVICE_UNAVAILABLE,x,String.format("Retry %d ",retry));
		        			continue;
		        		}
		        		else {
		        			throw new RResourceException(Status.SERVER_ERROR_SERVICE_UNAVAILABLE,x,variant);
		        		}
		        	} catch (Exception x) {
		        		Context.getCurrentLogger().severe(x.getMessage());
		    			throw new RResourceException(Status.SERVER_ERROR_INTERNAL,x,variant);
	
		        	} finally {

		        		//try { if (connection !=null) connection.close(); } catch (Exception x) {};
		        		//try { if ((convertor !=null) && (convertor.getReporter() !=null)) convertor.getReporter().close(); } catch (Exception x) {}
		        	}
	        	}
    			return null;	        	
	        	
	        } else {
	        	if (variant.getMediaType().equals(MediaType.TEXT_HTML)) try {
	    			IProcessor<Q, Representation>  convertor = createConvertor(variant);
	    			Representation r = convertor.process(null);
	            	return r;			
	    		} catch (Exception x) { 
	    			throw new RResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x,variant); 
	    		}  else {
	    			throw new RResourceException(Status.CLIENT_ERROR_BAD_REQUEST,error,variant);
	    		}
    	
	        }
		} catch (RResourceException x) {
			throw x;	        
		} catch (ResourceException x) {
			throw new RResourceException(x.getStatus(),x,variant);
		} catch (Exception x) {
			throw new RResourceException(Status.SERVER_ERROR_INTERNAL,x,variant);
		}
	}		
	
	protected void processNotFound(NotFoundException x, int retry) throws Exception {
		throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND,String.format("Query returns no results! %s",x.getMessage()));
	}
	protected void customizeEntry(T entry, Connection conection) throws ResourceException {
		
	}
	/**
	 * POST - create entity based on parameters in http header, creates a new entry in the databaseand returns an url to it
	 */
	public void executeUpdate(Representation entity, T entry, AbstractUpdate updateObject) throws ResourceException {

		Connection c = null;
		//TODO it is inefficient to instantiate executor in all classes
		UpdateExecutor executor = new UpdateExecutor();
		try {
    		DBConnection dbc = new DBConnection(getContext());
    		c = dbc.getConnection(getRequest());			

			executor.setConnection(c);
			executor.open();
			executor.process(updateObject);
			
			customizeEntry(entry, c);
			
			QueryURIReporter<T,Q> uriReporter = getURUReporter(getRequest());
			if (uriReporter!=null) {
				getResponse().setLocationRef(uriReporter.getURI(entry));
				getResponse().setEntity(uriReporter.getURI(entry),MediaType.TEXT_HTML);
			}
			getResponse().setStatus(Status.SUCCESS_OK);
			
		} catch (SQLException x) {
			Context.getCurrentLogger().severe(x.getMessage());
			getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN,x,x.getMessage());			
			getResponse().setEntity(null);			
		} catch (ProcessorException x) {
			Context.getCurrentLogger().severe(x.getMessage());
			getResponse().setStatus((x.getCause() instanceof SQLException)?Status.CLIENT_ERROR_FORBIDDEN:Status.SERVER_ERROR_INTERNAL,
					x,x.getMessage());			
			getResponse().setEntity(null);			
		} catch (Exception x) {
			Context.getCurrentLogger().severe(x.getMessage());
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL,x,x.getMessage());			
			getResponse().setEntity(null);
		} finally {
			try {executor.close();} catch (Exception x) {}
			try {if(c != null) c.close();} catch (Exception x) {}
		}
	}	
	/**
	 * POST - create entity based on parameters in the query, creates a new entry in the databaseand returns an url to it
	 * TODO Refactor to allow multiple objects 
	 */
	public void createNewObject(Representation entity) throws ResourceException {
		T entry = createObjectFromHeaders(null, entity);
		executeUpdate(entity, 
				entry,
				createUpdateObject(entry));
	
	}
	
	
	/**
	 * DELETE - create entity based on parameters in the query, creates a new entry in the database and returns an url to it
	 
	public void deleteObject(Representation entity) throws ResourceException {
		Form queryForm = getRequest().getResourceRef().getQueryAsForm();
		T entry = createObjectFromHeaders(queryForm, entity);
		executeUpdate(entity, 
				entry,
				createDeleteObject(entry));
	
	}	
	*/
	
	protected Representation delete(Variant variant) throws ResourceException {
		Representation entity = getRequestEntity();
		Form queryForm = null;
		if (MediaType.APPLICATION_WWW_FORM.equals(entity.getMediaType()))
			queryForm = new Form(entity);
		T entry = createObjectFromHeaders(queryForm, entity);
		executeUpdate(entity, 
				entry,
				createDeleteObject(entry));
		getResponse().setStatus(Status.SUCCESS_OK);
		return new EmptyRepresentation();
	};
	protected QueryURIReporter<T, Q>  getURUReporter(Request baseReference) throws ResourceException {
		throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED,String.format("%s getURUReporter()", getClass().getName()) );
	}
	protected  AbstractUpdate createUpdateObject(T entry) throws ResourceException {
		throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED,String.format("%s createUpdateObject()", getClass().getName()));
	}
	protected  AbstractUpdate createDeleteObject(T entry) throws ResourceException {
		throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED,String.format("%s createDeleteObject()", getClass().getName()));
	}	
	protected RDFObjectIterator<T> createObjectIterator(Reference reference, MediaType mediaType) throws ResourceException {
		throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED,String.format("%s createObjectIterator()", getClass().getName()));
	}
	
	protected RDFObjectIterator<T> createObjectIterator(Representation entity) throws ResourceException {
		throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED,String.format("%s createObjectIterator", getClass().getName()));
	}
	/**
	 * Return this object if can't parse source_uri
	 * @param uri
	 * @return
	 */
	protected T onError(String uri) {
		return null;
	}
	/**
	 * either entity in RDF/XML or ?source_uri=URI
	 */
	protected T createObjectFromHeaders(Form queryForm, Representation entity) throws ResourceException {
		RDFObjectIterator<T> iterator = null;
		if (!entity.isAvailable()) { //using URI
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Empty content");
		} else 
			if (MediaType.TEXT_URI_LIST.equals(entity.getMediaType())) {
				return createObjectFromURIlist(entity);
			} else if (MediaType.APPLICATION_WWW_FORM.equals(entity.getMediaType())) {
				return createObjectFromWWWForm(entity);
			} else if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType())) {
				return createObjectFromMultiPartForm(entity);				
			
			} else // assume RDF
			try {
				iterator = createObjectIterator(entity);
				iterator.setCloseModel(true);
				iterator.setBaseReference(getRequest().getRootRef());
				
				while (iterator.hasNext()) {
					T nextObject = iterator.next();
					if (accept(nextObject)) return nextObject;
				}
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Nothing to write! "+getRequest().getRootRef() );	
			} catch (ResourceException x)  {
				throw x;
			} catch (Exception x) {
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);	
			} finally {
				try { iterator.close(); } catch (Exception x) {}
			}
		
	}
	
	protected boolean accept(T object) throws ResourceException  {
		return true;
	}
	protected String getObjectURI(Form queryForm) throws ResourceException {
		return getParameter(queryForm,
				OpenTox.params.source_uri.toString(),
				OpenTox.params.source_uri.getDescription(),
				true);		
	}
	protected T createObjectFromWWWForm(Representation entity) throws ResourceException {
		Form queryForm = new Form(entity);
		String sourceURI = getObjectURI(queryForm);
		RDFObjectIterator<T> iterator = null;
		try {
			iterator = createObjectIterator(new Reference(sourceURI),entity.getMediaType()==null?MediaType.APPLICATION_RDF_XML:entity.getMediaType());
			iterator.setCloseModel(true);
			iterator.setBaseReference(getRequest().getRootRef());
			while (iterator.hasNext()) {
				return iterator.next();
			}		
			//if none
			return onError(sourceURI);
		} catch (Exception x) {
			return onError(sourceURI);
		} finally {
			try { iterator.close(); } catch (Exception x) {}
		}		
	}
	protected T createObjectFromMultiPartForm(Representation entity) throws ResourceException {
		throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED);
	}	
	protected T createObjectFromURIlist(Representation entity) throws ResourceException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(entity.getStream()));
			String line = null;
			while ((line = reader.readLine())!= null)
				return onError(line);
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
		} finally {
			try { reader.close();} catch (Exception x) {}
		}
		
	}
	
	protected Representation process(Representation entity, final Variant variant, final boolean async)
			throws ResourceException {
		synchronized (this) {
			
			Connection conn = null;
			try {
				
				final Form form = new Form(entity);
				final Reference reference = new Reference(getObjectURI(form));
				//models
				IQueryRetrieval<T> query = createQuery(getContext(),getRequest(),getResponse());
				if (query==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
				
				TaskCreator<Object,T> taskCreator = new TaskCreator<Object,T>(form,async) {
					@Override
					protected ICallableTask getCallable(Form form,
							T item) throws ResourceException {
						return createCallable(form,item);
					}
					@Override
					protected Task<Reference, Object> createTask(
							ICallableTask callable,
							T item) throws ResourceException {
	
							return ((TaskApplication)getApplication()).addTask(
								String.format("Apply %s %s %s",item.toString(),reference==null?"":"to",reference==null?"":reference),									
								callable,
								getRequest().getRootRef(),
								getToken());		
						}
				};
			
				DBConnection dbc = new DBConnection(getApplication().getContext());
				conn = dbc.getConnection(getRequest());	
				
				List<UUID> r = null;
				try {
					taskCreator.setConnection(conn);
					r =  taskCreator.process(query);
				} finally {
					try {
			    		taskCreator.setConnection(null);
			    		taskCreator.close();
					} catch (Exception x) {}
		    		try { conn.close(); conn=null;} catch  (Exception x) {}
				}
				if ((r==null) || (r.size()==0)) throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
				else {
					ITaskStorage storage = ((TaskApplication)getApplication()).getTaskStorage();
					FactoryTaskConvertor<Object> tc = new AmbitFactoryTaskConvertor<Object>(storage);
					if (r.size()==1) {
						Task<Reference,Object> task = storage.findTask(r.get(0));
						task.update();
						setStatus(task.isDone()?Status.SUCCESS_OK:Status.SUCCESS_ACCEPTED);
						return tc.createTaskRepresentation(r.get(0), variant,getRequest(), getResponse(),getDocumentation());
					} else 
						return tc.createTaskRepresentation(r.iterator(), variant,getRequest(), getResponse(),getDocumentation());
				}
			} catch (RResourceException x) {				
				throw x;
			} catch (ResourceException x) {				
				throw new RResourceException(x.getStatus(),x, variant);
			} catch (AmbitException x) {
				throw new RResourceException(new Status(Status.SERVER_ERROR_INTERNAL,x),variant);
			} catch (SQLException x) {
				throw new RResourceException(new Status(Status.SERVER_ERROR_INTERNAL,x),variant);		
			} catch (Exception x) {
				throw new RResourceException(new Status(Status.SERVER_ERROR_INTERNAL,x),variant);				
			} finally {
				try { if (conn != null) conn.close(); } catch  (Exception x) {}
			}
		}
	}
	
	
	protected CallableQueryProcessor createCallable(Form form,T item) throws ResourceException  {
		throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED);
	}
	
	protected void setPaging(Form form, IQueryObject queryObject) {
		String max = form.getFirstValue(max_hits);
		String page = form.getFirstValue(OpenTox.params.page.toString());
		String pageSize = form.getFirstValue(OpenTox.params.pagesize.toString());
		if (max != null)
		try {
			queryObject.setPage(0);
			queryObject.setPageSize(Long.parseLong(form.getFirstValue(max_hits).toString()));
			return;
		} catch (Exception x) {
			
		}
		try {
			queryObject.setPage(Integer.parseInt(page));
		} catch (Exception x) {
		}
		try {
			queryObject.setPageSize(Long.parseLong(pageSize));
		} catch (Exception x) {

		}			
	}
	
	protected Template createTemplate(Form form) throws ResourceException {
		String[] featuresURI =  OpenTox.params.feature_uris.getValuesArray(form);
		return createTemplate(getContext(),getRequest(),getResponse(), featuresURI);
	}
	protected Template createTemplate(Context context, Request request,
			Response response,String[] featuresURI) throws ResourceException {
		
		try {
			Template profile = new Template(null);
			profile.setId(-1);				
			
			ProfileReader reader = new ProfileReader(getRequest().getRootRef(),profile);
			reader.setCloseConnection(false);
			
			

			DBConnection dbc = new DBConnection(getContext());
			Connection conn = dbc.getConnection(getRequest());
			try {
				for (String featureURI:featuresURI) {
					if (featureURI == null) continue;
					reader.setConnection(conn);
					profile = reader.process(new Reference(featureURI));
					reader.setProfile(profile);
					
				}
				//	readFeatures(featureURI, profile);
				if (profile.size() == 0) {
					reader.setConnection(conn);
					String templateuri = getDefaultTemplateURI(context,request,response);
					if (templateuri!= null) profile = reader.process(new Reference(templateuri));
					reader.setProfile(profile);
				}
			} catch (Exception x) {
				System.out.println(getRequest().getResourceRef());
				//x.printStackTrace();
			} finally {
				//the reader closes the connection
				reader.setCloseConnection(true);
				try { reader.close();} catch (Exception x) {}
				//try { conn.close();} catch (Exception x) {}
			}
			return profile;
		} catch (Exception x) {
			getLogger().info(x.getMessage());
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
		}
		
	}	
	protected String getDefaultTemplateURI(Context context, Request request,Response response) {
		return null;
	}
}
