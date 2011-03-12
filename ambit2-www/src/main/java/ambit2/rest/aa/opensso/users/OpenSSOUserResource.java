package ambit2.rest.aa.opensso.users;

import java.io.Writer;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.poi.hssf.record.formula.functions.T;
import org.opentox.aa.opensso.OpenSSOToken;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.CookieSetting;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.security.User;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.rest.StringConvertor;
import ambit2.rest.aa.opensso.OpenSSOServicesConfig;
import ambit2.rest.aa.opensso.OpenSSOUser;
import ambit2.rest.algorithm.CatalogHTMLReporter;
import ambit2.rest.algorithm.CatalogResource;

public class OpenSSOUserResource extends CatalogResource<OpenSSOUser>{
	public static final String resource = "opentoxuser";
	
	@Override
	protected Iterator<OpenSSOUser> createQuery(Context context,
			Request request, Response response) throws ResourceException {
		
		User user = request.getClientInfo().getUser();
		if (user == null) user = new OpenSSOUser();
		if (user instanceof OpenSSOUser) 
			return new SingleItemIterator<OpenSSOUser>(((OpenSSOUser)user));
		else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	}
	
	protected void setTokenCookies(Variant variant) {
		User user = getRequest().getClientInfo().getUser();
		if (user instanceof OpenSSOUser) {
			if ((user==null) || (((OpenSSOUser)user).getToken()==null))
				super.setTokenCookies(variant);
			else {
				CookieSetting cS = new CookieSetting(0, "subjectid", ((OpenSSOUser)user).getToken());
				cS.setSecure(true);
				cS.setComment("OpenSSO token");
				cS.setPath("/");
			    this.getResponse().getCookieSettings().add(cS);
			} 
		}
	}
	
	
	@Override
	public IProcessor<Iterator<OpenSSOUser>, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {

		if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
			return new StringConvertor(
					new OpenSSOUserHTMLReporter(getRequest(),getDocumentation()),MediaType.TEXT_HTML);
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			return new StringConvertor(	new OpenSSOUsersURIReporter(getRequest(),getDocumentation()) {
				@Override
				public void processItem(OpenSSOUser src, Writer output) {
					super.processItem(src, output);
					try {
					output.write('\n');
					} catch (Exception x) {}
				}
			},MediaType.TEXT_URI_LIST);
			
		} else //html 	
			return new StringConvertor(
					new CatalogHTMLReporter<T>(getRequest(),getDocumentation()),MediaType.TEXT_HTML);
		
	}
	
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		if ((entity != null) && MediaType.APPLICATION_WWW_FORM.equals(entity.getMediaType())) {
			Form form = new Form(entity);	
			OpenSSOToken ssoToken = new OpenSSOToken(OpenSSOServicesConfig.getInstance().getOpenSSOService());
			try {
				String username = form.getFirstValue("user");
				String pass = form.getFirstValue("password");
				if (ssoToken.login(username,pass)) {
					OpenSSOUser user = new OpenSSOUser();
					user.setToken(ssoToken.getToken());
					getRequest().getClientInfo().setUser(user);
					user.setIdentifier(username);
				} else {
					getRequest().getClientInfo().setUser(null);
					this.getResponse().getCookieSettings().removeAll("subjectid");
				}
				queryObject = createQuery(getContext(), getRequest(), getResponse());
		
				return get(variant);					
			} catch (Exception x) {
				throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,x);
			}
		} else throw new ResourceException(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
		
	}
	@Override
	protected Representation delete() throws ResourceException {
		String token = getToken();
		if (token != null) try {
			OpenSSOToken ssoToken = new OpenSSOToken(OpenSSOServicesConfig.getInstance().getOpenSSOService());
			if (ssoToken.logout()) {
				getRequest().getClientInfo().setUser(null);
				this.getResponse().getCookieSettings().removeAll("subjectid");
			}
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,x);
		}
		return get();
	}
	/**
	 * Logout
	 */
	@Override
	protected Representation delete(Variant variant) throws ResourceException {
		String token = getToken();
		if (token != null) try {
			OpenSSOToken ssoToken = new OpenSSOToken(OpenSSOServicesConfig.getInstance().getOpenSSOService());
			ssoToken.setToken(token);
			if (ssoToken.logout()) {
				getRequest().getClientInfo().setUser(null);
				this.getResponse().getCookieSettings().removeAll("subjectid");
			}
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,x);
		}
		queryObject = createQuery(getContext(), getRequest(), getResponse());
		getRequest().getOriginalRef().setQuery(null);
		getRequest().getResourceRef().setQuery(null);
		return get(variant);
	}
}
