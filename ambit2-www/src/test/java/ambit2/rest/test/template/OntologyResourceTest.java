package ambit2.rest.test.template;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;
import org.restlet.data.MediaType;
import org.w3c.dom.Document;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.rest.property.PropertyDOMParser;
import ambit2.rest.template.OntologyResource;
import ambit2.rest.test.ResourceTest;

public class OntologyResourceTest extends ResourceTest {
	
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d%s/All/All/view/tree", port,OntologyResource.resource);
	}
	/*
	@Test
	public void testURI() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_URI_LIST);
	}
	*/
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
		return count >0;
	}	
	@Test
	public void testXML() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_XML);
	}
	/*
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
		//<?xml version="1.0" encoding="UTF-8"?><features xmlns="http://opentox.org/Feature/1.0"><feature CompoundID="11" ConformerID="100215" value="1530-32-1"/></features>
		throw new Exception("TODO: Parse XML and verify values");
	}		
	*/
	@Override
	public boolean verifyResponseXML(String uri, MediaType media, InputStream in)
			throws Exception {
		
		Document doc = createDOM(in);
		PropertyDOMParser parser = new PropertyDOMParser() {
			@Override
			public void handleItem(Property item) throws AmbitException {
				System.out.print(item);
				System.out.print("\t");
				System.out.print(item.getId());
				System.out.print("\t");
				System.out.print(item.getClazz());
				System.out.print("\t");				
				System.out.println(item.getClass().getName());
				
				
			}
        };
        parser.parse(doc);
        return true;
        
	}	
}
