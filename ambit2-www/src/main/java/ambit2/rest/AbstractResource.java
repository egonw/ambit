package ambit2.rest;

import java.util.ArrayList;
import java.util.List;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.exceptions.NotFoundException;
import ambit2.base.interfaces.IProcessor;

/**
 * Abstract class for resources
 * @author nina
 *
 * @param <Q>
 * @param <T>
 * @param <P>
 */
public abstract class AbstractResource<Q,T,P extends IProcessor<Q, Representation>> extends ServerResource {
	protected Q query;
	protected Exception error = null;	
	protected Status status = Status.SUCCESS_OK;
	
	public String[] URI_to_handle() {
		return null;
	}

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		try {
			status = Status.SUCCESS_OK;
			query = createQuery(getContext(), getRequest(), getResponse());
			error = null;
		} catch (StatusException x) {
			query = null;
			error = x;
			status = x.getStatus();
		}			
	}
	protected void customizeVariants(MediaType[] mimeTypes) {
        List<Variant> variants = new ArrayList<Variant>();
        for (MediaType mileType:mimeTypes) variants.add(new Variant(mileType));
        getVariants().put(Method.GET, variants);
	}
	public abstract P createConvertor(Variant variant) throws AmbitException, ResourceException;
	
	protected   abstract  Q createQuery(Context context, Request request, Response response) throws StatusException;
	
	public synchronized Representation get(Variant variant) {

		try {
	        if (query != null) {
	        	IProcessor<Q, Representation> convertor = null;

		        	try {
		        		getResponse().setStatus(status);
		        		convertor = createConvertor(variant);
			        	Representation r = convertor.process(query);
			        	return r;
		        	} catch (NotFoundException x) {

		    			getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND, new NotFoundException(x.getMessage()));
		    			return null;
		        	} catch (StatusException x) {
		    			getResponse().setStatus(x.getStatus());
		    			return null;
		        	} catch (Exception x) {

		    			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL,x);
		    			return null;
		        	} finally {
		        		
		        	}

	        	
	        } else {
	        	getResponse().setStatus(status==null?Status.CLIENT_ERROR_BAD_REQUEST:status,error);
	        	return null;	        	
	        }
		} catch (Exception x) {
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL,x);
			return null;
		}
	}				
	/**
	 * Returns parameter value and throwsan exception if value is missing of mandatory parameter
	 * @param requestHeaders
	 * @param paramName
	 * @param mandatory
	 * @return
	 * @throws ResourceException
	 */
	protected String getParameter(Form requestHeaders,String paramName,boolean mandatory) throws ResourceException {
		Object o = requestHeaders.getFirstValue(paramName);
		if (o == null)
			if (mandatory)	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,String.format("Parameter %s is mandatory!", paramName));
			else return null;
		else return o.toString();
	}
	/**
	 * Calls {@link #getParameter(Form, String, boolean)} with false for the last argument
	 * @param requestHeaders
	 * @param paramName
	 * @return
	 * @throws ResourceException
	 */
	protected String getParameter(Form requestHeaders,String paramName) throws ResourceException {
		return getParameter(requestHeaders, paramName,false);
	}
}
