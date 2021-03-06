package ambit2.core.test.io;

import java.io.FileInputStream;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.core.io.bcf.EurasBCFReader;


public class EurasBCFReaderTest  {
	@Test
	public void test() throws Exception {
		FileInputStream in = new FileInputStream("../src/test/resources/endpoints/bioconcentration_factor/EURAS_CEFIC_LRI-BCF_Fields&References_2008-01-08.xls");
		EurasBCFReader reader = new EurasBCFReader(in,0);
		int c = 0;
		while (reader.hasNext()) {
			IAtomContainer m = (IAtomContainer)reader.next();
	
			c++;
		}
		reader.close();
		Assert.assertEquals(1130,c);
	}
}
