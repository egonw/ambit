package ambit2.rest.test.query;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.rest.structure.CompoundLookup;
import ambit2.rest.test.ResourceTest;

public class CompoundLookupTest extends ResourceTest {
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/query%s/search/all?search=%s&search=%s", port,
				CompoundLookup.resource,
				//"/smarts",
				Reference.encode("1530-32-1"),
				Reference.encode(String.format("50-00-0", port)));
	}
	@Test
	public void testURI() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_URI_LIST);
	}
	@Override
	public boolean verifyResponseURI(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while ((line = r.readLine())!= null) {
			Assert.assertEquals(String.format("http://localhost:%d/compound/11/conformer/100215",port),line);
		}
		return true;
	}
}
