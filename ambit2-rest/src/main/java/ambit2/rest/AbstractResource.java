package ambit2.rest;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.wadl.FaultInfo;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.ext.wadl.WadlRepresentation;
import org.restlet.ext.wadl.WadlServerResource;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

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
public abstract class AbstractResource<Q,T extends Serializable,P extends IProcessor<Q, Representation>> extends WadlServerResource {
	protected Q queryObject;
	protected Exception error = null;	
	protected Status response_status = Status.SUCCESS_OK;
	public final static String search_param = "search";
	public final static String property = "property";
	public final static String condition = "condition";
	public final static String caseSensitive = "casesens";
	public final static String returnProperties = "returnProperties";
	
	public final static String max_hits = "max";
	protected static String[] filter = {
		"googlebot","msnbot",
		"yahoo-slurp",
		"teoma",
		"twiceler",
		"gigabot",
		"scrubby",
		"robozilla",
		"nutch",
		"ia_archiver",
		"baiduspider",
		"naverbot",
		"yeti",
		"googlebot-image",
		"googlebot-mobile",
		"yahoo-mmcrawler",
		"psbot",
		"asterias",
		"yahoo-blogs/v3.9",
		"YandexBot/3.0",
		"MJ12bot/v1.3.3"

	};
	
	public AbstractResource() {
		super();
		setAutoDescribed(true);
	}
	public String[] URI_to_handle() {
		return null;
	}

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		checkForBots();
		response_status = Status.SUCCESS_OK;
		queryObject = createQuery(getContext(), getRequest(), getResponse());
		error = null;

	}
	protected void checkForBots() throws ResourceException {
		if (getRequest().getClientInfo()==null) return;
		if (getRequest().getClientInfo().getAgent()==null) return;
		for (int i=0;i < filter.length;i++)
			if (getRequest().getClientInfo().getAgent().toLowerCase().indexOf(filter[i])>=0)
					throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED,getRequest().getClientInfo().getAgentName());
		
	}
	protected void customizeVariants(MediaType[] mimeTypes) {
       // List<Variant> variants = new ArrayList<Variant>();
        for (MediaType m:mimeTypes) getVariants().add(new Variant(m));
        //getVariants().put(Method.GET, variants);
        //getVariants().put(Method.POST, variants);
	}
	public abstract P createConvertor(Variant variant) throws AmbitException, ResourceException;
	
	protected   abstract  Q createQuery(Context context, Request request, Response response) throws ResourceException;
	
	@Override
	public List<Variant> getVariants() {
		List<Variant> vars = super.getVariants();
		return vars;
	}
	
	@Override
	protected Representation get(Variant variant) throws ResourceException {
	try {
			if (variant.getMediaType().equals(MediaType.APPLICATION_WADL)) {
				WadlRepresentation wadl =  new WadlRepresentation(describe());
				//wadl.setApplication(((WadlApplication)getApplication()).getApplicationInfo(getRequest(), getResponse()));

				return wadl;
			} else	
	    	if (MediaType.APPLICATION_JAVA_OBJECT.equals(variant.getMediaType())) {
	    		if ((queryObject!=null) && (queryObject instanceof Serializable))
	    		return new ObjectRepresentation((Serializable)queryObject,MediaType.APPLICATION_JAVA_OBJECT);
	    		else throw new ResourceException(Status.CLIENT_ERROR_NOT_ACCEPTABLE);        		
	    	}
	        if (queryObject != null) {
	        	IProcessor<Q, Representation> convertor = null;

		        	try {
		        		getResponse().setStatus(response_status);
		        		convertor = createConvertor(variant);
			        	Representation r = convertor.process(queryObject);
			        	return r;
		        	} catch (NotFoundException x) {

		    			getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND, new NotFoundException(x.getMessage()));
		    			return null;
		        	} catch (ResourceException x) {
		    			getResponse().setStatus(x.getStatus());
		    			return null;
		        	} catch (Exception x) {

		    			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL,x);
		    			return null;
		        	} finally {
		        		
		        	}

	        	
	        } else {
	        	getResponse().setStatus(response_status==null?Status.CLIENT_ERROR_BAD_REQUEST:response_status,error);
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
	protected String getParameter(Form requestHeaders,String paramName,String description, boolean mandatory) throws ResourceException {
		Object o = requestHeaders.getFirstValue(paramName);
		if (o == null)
			if (mandatory)	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,String.format("Parameter %s [%s] is mandatory!", paramName,description));
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
	protected String getParameter(Form requestHeaders,String paramName,String description) throws ResourceException {
		return getParameter(requestHeaders, paramName,description, false);
	}
	
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		return post(entity);
	}
	
	@Override
	protected void describeGet(MethodInfo info) {
        info.setIdentifier("item");
        info.setDocumentation("To retrieve details of a specific item");

        Iterator<Variant> vars = getVariants().iterator();
        while (vars.hasNext()) {
        	Variant var = vars.next();
            RepresentationInfo repInfo = new RepresentationInfo(var.getMediaType());
            //repInfo.setXmlElement("item");
            repInfo.setDocumentation(String.format("%s representation",var.getMediaType()));
            info.getResponse().getRepresentations().add(repInfo);        	
        }


        FaultInfo faultInfo = new FaultInfo(Status.CLIENT_ERROR_NOT_FOUND,"Not found");
        faultInfo.setIdentifier("itemError");
        faultInfo.setMediaType(MediaType.TEXT_HTML);
        info.getResponse().getFaults().add(faultInfo);

	}

}
