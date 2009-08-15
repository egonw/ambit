package ambit2.rest.test.dataset;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.restlet.Client;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.FileRepresentation;
import org.restlet.resource.Representation;

import ambit2.rest.ChemicalMediaType;
import ambit2.rest.test.ResourceTest;


public class DatasetsResourceTest extends ResourceTest {
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/dataset", port);
	}
	@Test
	public void testURI() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_URI_LIST);
	}
	@Override
	public boolean verifyResponseURI(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count=0;
		while ((line = reader.readLine())!=null) {
			System.out.println(line);
			count++;
		}
		return count ==3;
	}			
	@Test
	public void testXML() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_XML);
	}
	@Override
	public boolean verifyResponseXML(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count=0;
		while ((line = reader.readLine())!=null) {
			System.out.println(line);
			count++;
		}
		return count ==1;
	}		
	@Test
	public void testHTML() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_HTML);
	}
	@Override
	public boolean verifyResponseHTML(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count=0;
		while ((line = reader.readLine())!=null) {
			System.out.println(line);
			count++;
		}
		return count>0;
	}		
	public Response testPost(String uri, MediaType media, Form headers) throws Exception {
		Request request = new Request();
		Client client = new Client(Protocol.HTTP);
		request.setResourceRef(uri);
		request.setMethod(Method.POST);
		request.getAttributes().put("org.restlet.http.headers", headers);		
		request.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(media));
		return client.handle(request);
	}
	
	public Response testPostFile(String uri, MediaType media, Representation file) throws Exception {
		Request request = new Request();
		Client client = new Client(Protocol.HTTP);
		ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;  
		ChallengeResponse authentication = new ChallengeResponse(scheme,  
		         "opentox", "opentox");  
		request.setChallengeResponse(authentication);  
		request.setResourceRef(uri);
		request.setMethod(Method.POST);
		request.setEntity(file);
		request.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(media));
		return client.handle(request);
	}	
		
	@Test
	public void testCreateEntry() throws Exception {
		
		FileRepresentation rep = new FileRepresentation(
				"E:/src/ambit2-all/ambit2-www/src/test/resources/input.sdf", 
				ChemicalMediaType.CHEMICAL_MDLSDF, 0);
				//EncodeRepresentation encodedRep = new EncodeRepresentation(Encoding.GZIP,rep);
				
		Response r = testPostFile(getTestURI(), ChemicalMediaType.CHEMICAL_MDLSDF, rep);
		Reference uri = r.getLocationRef();
		while (!r.getStatus().equals(Status.SUCCESS_OK)) {
			//System.out.println(r.getStatus() + " " +r.getLocationRef());
			
			Request request = new Request();
			Client client = new Client(Protocol.HTTP);
			request.setResourceRef(uri);
			request.setMethod(Method.GET);
			r = client.handle(request);
			uri = r.getLocationRef();
		}
		System.out.println(r.getStatus());
		//System.out.println(r.getLocationRef());
	//	Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM structure");
		Assert.assertEquals(11,table.getRowCount());
		c.close();
		
	}	
	
	
}