package ambit2.db.processors.test;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.io.FileInputStream;

import junit.framework.Assert;
import net.idea.i5._5.ambit2.I5AmbitProcessor;
import net.idea.i5.io.I5ZReader;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.openscience.cdk.io.IChemObjectReaderErrorHandler;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.io.DownloadTool;
import ambit2.core.io.IRawReader;
import ambit2.core.processors.structure.key.PropertyKey;
import ambit2.core.processors.structure.key.ReferenceSubstanceUUID;
import ambit2.db.processors.DBSubstanceWriter;

public class SubstanceWriterTest extends DbUnitTest {

	
	@Test
	public void testWriteMultipleFiles_i5d() throws Exception {
		
		setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
        IDatabaseConnection c = getConnection();
        
		ITable chemicals = 	c.createQueryTable("EXPECTED","SELECT * FROM chemicals");
		Assert.assertEquals(0,chemicals.getRowCount());
		ITable strucs = 	c.createQueryTable("EXPECTED","SELECT * FROM structure");
		Assert.assertEquals(0,strucs.getRowCount());
		ITable srcdataset = 	c.createQueryTable("EXPECTED","SELECT * FROM src_dataset");
		Assert.assertEquals(0,srcdataset.getRowCount());
		ITable struc_src = 	c.createQueryTable("EXPECTED","SELECT * FROM struc_dataset");
		Assert.assertEquals(0,struc_src.getRowCount());
		ITable property = 	c.createQueryTable("EXPECTED","SELECT * FROM properties");
		Assert.assertEquals(0,property.getRowCount());
		ITable property_values = 	c.createQueryTable("EXPECTED","SELECT * FROM property_values");
		Assert.assertEquals(0,property_values.getRowCount());

		/**
		 * Now reading only substances and reference substances
    Document types:
    EndpointStudyRecord: 877
    AttachmentDocument: 5
    LegalEntity: 1
    ReferenceSubstance: 6
    Substance: 1
    EndpointRecord: 14
		 */
//		InputStream in = I5AmbitProcessor.class.getClassLoader().getResourceAsStream("net/idea/i5/_5/substance/i5z/IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734.i5z");
//		InputStream in = I5AmbitProcessor.class.getClassLoader().getResourceAsStream("net/idea/i5/_5/substance/i5z/IUC4-e2b69497-1c50-3d0b-a2b2-41d0a4d74c54.i5z");
//		InputStream in = I5AmbitProcessor.class.getClassLoader().getResourceAsStream("net/idea/i5/_5/substance/i5z/IUC4-f5dd46ce-6fc9-316f-a468-c4f9acfcfc3c.i5z");
		
		InputStream in = new FileInputStream(new File("F:/nina/Ideaconsult/Projects/2013 - LRI AMBIT/AMBIT-LRI/IUCLID example for 4 endpoints.i5z"));
		Assert.assertNotNull(in);
		File i5z = File.createTempFile("test_", ".i5z");
		try {
			DownloadTool.download(in, i5z);
		} finally {
			in.close();
		}
		Assert.assertTrue(i5z.exists());

	    I5ZReader reader = getReader(i5z);
		int records = write(reader,c.getConnection(),new ReferenceSubstanceUUID());
		reader.close();
        c.close();
        /*
        c = getConnection();
		url = I5ZReader.class.getClassLoader().getResource("net/idea/i5/_5/substance/i5z/Benzoicacid.i5z");
	    reader = getReader(new File(url.getFile()));
		records += write(reader,c.getConnection(),new ReferenceSubstanceUUID());
		reader.close();
        c.close();
        
        c = getConnection();
		URL url = I5ZReader.class.getClassLoader().getResource("net/idea/i5/_5/substance/i5z/formaldehyde.i5z");
	    reader = getReader(new File(url.getFile()));
		records += write(reader,c.getConnection(),new ReferenceSubstanceUUID());
		reader.close();
        c.close();
        */
        
        /*
         * NPE at junit.framework.AssertionFailedError: null
	at junit.framework.Assert.fail(Assert.java:47)
	at junit.framework.Assert.assertTrue(Assert.java:20)
	at junit.framework.Assert.assertTrue(Assert.java:27)
	at ambit2.db.processors.test.SubstanceWriterTest.write(SubstanceWriterTest.java:184)
	at ambit2.db.processors.test.SubstanceWriterTest.testWriteMultipleFiles_i5d(SubstanceWriterTest.java:94)

        */
        
/*

  Document types:
    EndpointStudyRecord: 877
    AttachmentDocument: 5
    LegalEntity: 1
    ReferenceSubstance: 6
    Substance: 1
    EndpointRecord: 14
 */
		Assert.assertEquals(103,records);

        
        c = getConnection();
        ITable substance = 	c.createQueryTable("EXPECTED","SELECT * FROM substance");
        Assert.assertEquals(1,substance.getRowCount());
        Assert.assertNotNull(substance.getValue(0,"uuid"));

		chemicals = 	c.createQueryTable("EXPECTED","SELECT * FROM chemicals");
		Assert.assertEquals(6,chemicals.getRowCount());
		//there are two empty file without $$$$ sign, which are skipped
		strucs = 	c.createQueryTable("EXPECTED","SELECT * FROM structure");
		Assert.assertEquals(6,strucs.getRowCount());
		srcdataset = 	c.createQueryTable("EXPECTED","SELECT * FROM src_dataset where name='IUCLID5 .i5z file'");
		Assert.assertEquals(1,srcdataset.getRowCount());
		struc_src = 	c.createQueryTable("EXPECTED","SELECT * FROM struc_dataset");
		Assert.assertEquals(6,struc_src.getRowCount());
		property = 	c.createQueryTable("EXPECTED","SELECT * FROM properties join catalog_references using(idreference) where comments='http://www.opentox.org/api/1.1#IUCLID5_UUID'");
		Assert.assertEquals(1,property.getRowCount());
		property = 	c.createQueryTable("EXPECTED","SELECT * FROM properties join catalog_references using(idreference) order by name");
		//Assert.assertEquals(34,property.getRowCount());
		Assert.assertEquals(22,property.getRowCount());
		property_values = 	c.createQueryTable("EXPECTED","SELECT * FROM property_values");
		Assert.assertEquals(48,property_values.getRowCount());		
		ITable tuples = 	c.createQueryTable("EXPECTED","SELECT * FROM tuples");
		Assert.assertEquals(13,tuples.getRowCount());			
		ITable p_tuples = 	c.createQueryTable("EXPECTED","SELECT * FROM property_tuples");
		Assert.assertEquals(71,p_tuples.getRowCount());				
		ITable p_cas = 	c.createQueryTable("EXPECTED","SELECT idchemical,idstructure,value FROM property_values join property_string using(idvalue_string) join properties using(idproperty) where name='CasRN'");
		Assert.assertEquals(6,p_cas.getRowCount());
		ITable p_ec = 	c.createQueryTable("EXPECTED","SELECT idchemical,idstructure,value FROM property_values join property_string using(idvalue_string) join properties using(idproperty) where name='EC'");
		Assert.assertEquals(6,p_ec.getRowCount());		
		ITable p_uuid = 	c.createQueryTable("EXPECTED","SELECT idchemical,idstructure,value FROM property_values join property_string using(idvalue_string) join properties using(idproperty) where name='I5UUID'");
		Assert.assertEquals(6,p_uuid.getRowCount());
		c.close();
	}	
	private I5ZReader getReader(File i5z) throws Exception {
		 I5ZReader reader = new I5ZReader(i5z);
		    reader.setErrorHandler(new IChemObjectReaderErrorHandler() {
				
				@Override
				public void handleError(String message, int row, int colStart, int colEnd,
						Exception exception) {
				}
				
				@Override
				public void handleError(String message, int row, int colStart, int colEnd) {
				}
				
				@Override
				public void handleError(String message, Exception exception) {
				}
				
				@Override
				public void handleError(String message) {
				}
			});
		    return reader;
	}
	public int write(IRawReader<IStructureRecord> reader,Connection connection) throws Exception  {
		return write(reader, connection,new ReferenceSubstanceUUID());
	}

	public int write(IRawReader<IStructureRecord> reader,Connection connection,PropertyKey key) throws Exception  {
		
		DBSubstanceWriter writer = new DBSubstanceWriter(DBSubstanceWriter.datasetMeta(),new SubstanceRecord());
		writer.setConnection(connection);
        writer.open();
		int records = 0;
		while (reader.hasNext()) {
            Object record = reader.next();
            if (record==null) continue;
            Assert.assertTrue(record instanceof IStructureRecord);
            writer.process((IStructureRecord)record);
            records++;
		}
		writer.close();
		return records;
	}	
	
}
