/* RepositoryWriterTest.java
 * Author: nina
 * Date: Jan 9, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit2.db.processors.test;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.base.config.Preferences;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.StructureRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.FileInputState;
import ambit2.core.io.IRawReader;
import ambit2.core.io.RawIteratingFolderReader;
import ambit2.core.io.RawIteratingSDFReader;
import ambit2.core.processors.structure.MoleculeWriter;
import ambit2.core.processors.structure.key.IStructureKey;
import ambit2.core.processors.structure.key.PropertyKey;
import ambit2.core.processors.structure.key.PubchemCID;
import ambit2.core.processors.structure.key.SmilesKey;
import ambit2.db.SourceDataset;
import ambit2.db.processors.PropertyImporter;
import ambit2.db.processors.RepositoryWriter;

public class RepositoryWriterTest extends DbUnitTest {

	@Test
	public void testWrite() throws Exception {
		
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
		
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("ambit2/db/processors/test/input.sdf");
		Assert.assertNotNull(in);
		RawIteratingSDFReader reader = new RawIteratingSDFReader(new InputStreamReader(in));
		reader.setReference(LiteratureEntry.getInstance("input.sdf"));
		write(reader,c.getConnection());
        c.close();
        
        c = getConnection();
		chemicals = 	c.createQueryTable("EXPECTED","SELECT * FROM chemicals");
		Assert.assertEquals(5,chemicals.getRowCount());
		chemicals = 	c.createQueryTable("EXPECTED","SELECT * FROM chemicals where smiles is not null and inchi is not null and formula is not null");
		Assert.assertEquals(3,chemicals.getRowCount());		
		strucs = 	c.createQueryTable("EXPECTED","SELECT * FROM structure");
		Assert.assertEquals(7,strucs.getRowCount());
		srcdataset = 	c.createQueryTable("EXPECTED","SELECT * FROM src_dataset where name='TEST INPUT'");
		Assert.assertEquals(1,srcdataset.getRowCount());
		struc_src = 	c.createQueryTable("EXPECTED","SELECT * FROM struc_dataset");
		Assert.assertEquals(7,struc_src.getRowCount());
		
		property = 	c.createQueryTable("EXPECTED","SELECT * FROM properties");
		Assert.assertEquals(73,property.getRowCount());
		property_values = 	c.createQueryTable("EXPECTED","SELECT * FROM property_values");
		Assert.assertEquals(228,property_values.getRowCount());		
		ITable tuples = 	c.createQueryTable("EXPECTED","SELECT * FROM tuples");
		Assert.assertEquals(7,tuples.getRowCount());			
		ITable p_tuples = 	c.createQueryTable("EXPECTED","SELECT * FROM property_tuples");
		Assert.assertEquals(228,p_tuples.getRowCount());				
		c.close();
		/**
		 * Removing redundant properties
insert ignore into property_values
select id,idproperty,idstructure,idvalue,idtype,user_name,status from property_values where idstructure>3
on duplicate key update idstructure=3
delete from property_values where idstructure>3

insert ignore into struc_dataset
select idstructure,id_srcdataset from struc_dataset where idstructure>3
on duplicate key update idstructure=3
delete from struc_dataset where idstructure>3
		 */

	}
	
	@Test
	public void testWriteEmptySmiles() throws Exception {
		
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
		
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("ambit2/db/processors/test/problem.sdf");
		Assert.assertNotNull(in);
		RawIteratingSDFReader reader = new RawIteratingSDFReader(new InputStreamReader(in));
		reader.setReference(LiteratureEntry.getInstance("Empty smiles"));
		write(reader,c.getConnection());
        c.close();
        
        c = getConnection();
		chemicals = 	c.createQueryTable("EXPECTED","SELECT * FROM chemicals");
		Assert.assertEquals(3,chemicals.getRowCount());
		strucs = 	c.createQueryTable("EXPECTED","SELECT * FROM structure");
		Assert.assertEquals(3,strucs.getRowCount());
		srcdataset = 	c.createQueryTable("EXPECTED","SELECT * FROM src_dataset where name='TEST INPUT'");
		Assert.assertEquals(1,srcdataset.getRowCount());
		struc_src = 	c.createQueryTable("EXPECTED","SELECT * FROM struc_dataset");
		Assert.assertEquals(3,struc_src.getRowCount());
		
		property = 	c.createQueryTable("EXPECTED","SELECT * FROM properties");
		Assert.assertEquals(13,property.getRowCount());
		property_values = 	c.createQueryTable("EXPECTED","SELECT * FROM property_values");
		Assert.assertEquals(39,property_values.getRowCount());		
		ITable tuples = 	c.createQueryTable("EXPECTED","SELECT * FROM tuples");
		Assert.assertEquals(3,tuples.getRowCount());			
		ITable p_tuples = 	c.createQueryTable("EXPECTED","SELECT * FROM property_tuples");
		Assert.assertEquals(39,p_tuples.getRowCount());				
		c.close();
		/**
		 * Removing redundant properties
insert ignore into property_values
select id,idproperty,idstructure,idvalue,idtype,user_name,status from property_values where idstructure>3
on duplicate key update idstructure=3
delete from property_values where idstructure>3

insert ignore into struc_dataset
select idstructure,id_srcdataset from struc_dataset where idstructure>3
on duplicate key update idstructure=3
delete from struc_dataset where idstructure>3
		 */

	}	
	
	@Test
	public void testMultiStrucSameSmiles() throws Exception {
		
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
		
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("ambit2/db/processors/test/struc_cas.sdf");
		Assert.assertNotNull(in);
		RawIteratingSDFReader reader = new RawIteratingSDFReader(new InputStreamReader(in));
		reader.setReference(LiteratureEntry.getInstance("Multi strucsame smiles"));
		write(reader,c.getConnection());
        c.close();
        
        c = getConnection();
		chemicals = 	c.createQueryTable("EXPECTED","SELECT * FROM chemicals");
		Assert.assertEquals(3,chemicals.getRowCount());
		strucs = 	c.createQueryTable("EXPECTED","SELECT * FROM structure");
		Assert.assertEquals(3,strucs.getRowCount());
		srcdataset = 	c.createQueryTable("EXPECTED","SELECT * FROM src_dataset where name='TEST INPUT'");
		Assert.assertEquals(1,srcdataset.getRowCount());
		struc_src = 	c.createQueryTable("EXPECTED","SELECT * FROM struc_dataset");
		Assert.assertEquals(3,struc_src.getRowCount());
		
		property = 	c.createQueryTable("EXPECTED","SELECT * FROM properties");
		Assert.assertEquals(13,property.getRowCount());
		property_values = 	c.createQueryTable("EXPECTED","SELECT * FROM property_values");
		Assert.assertEquals(39,property_values.getRowCount());		
		ITable tuples = 	c.createQueryTable("EXPECTED","SELECT * FROM tuples");
		Assert.assertEquals(3,tuples.getRowCount());			
		ITable p_tuples = 	c.createQueryTable("EXPECTED","SELECT * FROM property_tuples");
		Assert.assertEquals(39,p_tuples.getRowCount());				
		c.close();
		/**
		 * Removing redundant properties
insert ignore into property_values
select id,idproperty,idstructure,idvalue,idtype,user_name,status from property_values where idstructure>3
on duplicate key update idstructure=3
delete from property_values where idstructure>3

insert ignore into struc_dataset
select idstructure,id_srcdataset from struc_dataset where idstructure>3
on duplicate key update idstructure=3
delete from struc_dataset where idstructure>3
		 */

	}		
	public int write(IRawReader<IStructureRecord> reader,Connection connection) throws Exception  {
		return write(reader, connection,null);
	}
	public int write(IRawReader<IStructureRecord> reader,Connection connection,PropertyKey key) throws Exception  {
		

		RepositoryWriter writer = new RepositoryWriter();
		if (key != null)
			writer.setPropertyKey(key);
		writer.setDataset(new SourceDataset("TEST INPUT",LiteratureEntry.getInstance("File","file:input.sdf")));
		writer.setConnection(connection);
        writer.open();
		int records = 0;
		while (reader.hasNext()) {
            IStructureRecord record = reader.nextRecord();
			writer.write(record);
			records ++;
			System.out.println(record);
		}
		reader.close();
		writer.close();
		return records;
	}	
	
	protected int importProperties(IIteratingChemObjectReader reader,Connection connection,IStructureKey key) throws Exception  {
		
		IStructureRecord record = new StructureRecord();
		MoleculeWriter molwriter = new MoleculeWriter();
		PropertyImporter writer = new PropertyImporter();
		if (key != null)
			writer.setPropertyKey(key);
		writer.setDataset(new SourceDataset("Imported properties",LiteratureEntry.getInstance("File","file:property.csv")));
		writer.setConnection(connection);
        writer.open();
		int records = 0;
		while (reader.hasNext()) {
			Object o = reader.next();
			if (o instanceof IAtomContainer) {
				/*
				record.setReference(LiteratureEntry.getInstance("File","file:property.csv"));
				record.setIdchemical(-1);
				record.setIdstructure(-1);
				record.setFormat(IStructureRecord.MOL_TYPE.SDF.toString());
				record.clearProperties();
				record.addProperties(((IAtomContainer)o).getProperties());
				record.setContent(molwriter.process((IAtomContainer)o));
				writer.write(record);
				*/
				writer.write((IAtomContainer)o);
				records ++;
			} else throw new AmbitException("Unsupported class "+o.getClass().getName());
		}
		reader.close();
		writer.close();
		return records;
	}		
	@Test
	public void testWriteMultipleFiles() throws Exception {
		
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
		
		File dir = new File("src/test/resources/ambit2/db/processors/sdf");

	   FilenameFilter filter = new FilenameFilter() {
	        public boolean accept(File dir, String name) {
	            return !name.startsWith(".");
	        }
	    };
		
		File[] files = dir.listFiles(filter);
		Assert.assertEquals(11, files.length);
		RawIteratingFolderReader reader = new RawIteratingFolderReader(files);
		write(reader,c.getConnection());
		reader.close();
        c.close();
        
        c = getConnection();
		chemicals = 	c.createQueryTable("EXPECTED","SELECT * FROM chemicals");
		Assert.assertEquals(8,chemicals.getRowCount());
		//there are two empty file without $$$$ sign, which are skipped
		strucs = 	c.createQueryTable("EXPECTED","SELECT * FROM structure");
		Assert.assertEquals(15,strucs.getRowCount());
		srcdataset = 	c.createQueryTable("EXPECTED","SELECT * FROM src_dataset where name='TEST INPUT'");
		Assert.assertEquals(1,srcdataset.getRowCount());
		struc_src = 	c.createQueryTable("EXPECTED","SELECT * FROM struc_dataset");
		Assert.assertEquals(15,struc_src.getRowCount());
		
		property = 	c.createQueryTable("EXPECTED","SELECT * FROM properties");
		//Assert.assertEquals(34,property.getRowCount());
		Assert.assertEquals(220,property.getRowCount());
		property_values = 	c.createQueryTable("EXPECTED","SELECT * FROM property_values");
		Assert.assertEquals(378,property_values.getRowCount());		
		ITable tuples = 	c.createQueryTable("EXPECTED","SELECT * FROM tuples");
		Assert.assertEquals(15,tuples.getRowCount());			
		ITable p_tuples = 	c.createQueryTable("EXPECTED","SELECT * FROM property_tuples");
		Assert.assertEquals(378,p_tuples.getRowCount());				
		ITable p_cas = 	c.createQueryTable("EXPECTED","SELECT idchemical,idstructure,name,value FROM structure join values_string using(idstructure) where name=\"CasRN\"");
		Assert.assertEquals(13,p_cas.getRowCount());
	
		c.close();
		/**
		 * Removing redundant properties
insert ignore into property_values
select id,idproperty,idstructure,idvalue,idtype,user_name,status from property_values where idstructure>3
on duplicate key update idstructure=3
delete from property_values where idstructure>3

insert ignore into struc_dataset
select idstructure,id_srcdataset from struc_dataset where idstructure>3
on duplicate key update idstructure=3
delete from struc_dataset where idstructure>3
		 */

	}	
	
	@Test
	public void testSeekByCID() throws Exception {
		
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
		
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("ambit2/db/processors/cid/712.sdf");
		Assert.assertNotNull(in);
		RawIteratingSDFReader reader = new RawIteratingSDFReader(new InputStreamReader(in));
		reader.setReference(LiteratureEntry.getInstance("712.sdf"));
		write(reader,c.getConnection(),new PubchemCID());
        c.close();
        
        c = getConnection();
		chemicals = 	c.createQueryTable("EXPECTED","SELECT * FROM chemicals");
		Assert.assertEquals(1,chemicals.getRowCount());
		strucs = 	c.createQueryTable("EXPECTED","SELECT * FROM structure");
		Assert.assertEquals(3,strucs.getRowCount());
		srcdataset = 	c.createQueryTable("EXPECTED","SELECT * FROM src_dataset where name='TEST INPUT'");
		Assert.assertEquals(1,srcdataset.getRowCount());
		struc_src = 	c.createQueryTable("EXPECTED","SELECT * FROM struc_dataset");
		Assert.assertEquals(3,struc_src.getRowCount());
		
		property = 	c.createQueryTable("EXPECTED","SELECT * FROM properties");
		Assert.assertEquals(39,property.getRowCount());
		property_values = 	c.createQueryTable("EXPECTED","SELECT * FROM property_values");
		Assert.assertEquals(66,property_values.getRowCount());		
		ITable tuples = 	c.createQueryTable("EXPECTED","SELECT * FROM tuples");
		Assert.assertEquals(3,tuples.getRowCount());			
		ITable p_tuples = 	c.createQueryTable("EXPECTED","SELECT * FROM property_tuples");
		Assert.assertEquals(66,p_tuples.getRowCount());				
		c.close();
		/**
		 * Removing redundant properties
insert ignore into property_values
select id,idproperty,idstructure,idvalue,idtype,user_name,status from property_values where idstructure>3
on duplicate key update idstructure=3
delete from property_values where idstructure>3

insert ignore into struc_dataset
select idstructure,id_srcdataset from struc_dataset where idstructure>3
on duplicate key update idstructure=3
delete from struc_dataset where idstructure>3
		 */

	}		
	
	@Test
	public void testImportPropertiesBySMILES() throws Exception {
		
		Preferences.setProperty(Preferences.STOP_AT_UNKNOWNATOMTYPES, "false");
		setUpDatabase("src/test/resources/ambit2/db/processors/test/dataset-properties.xml");
        IDatabaseConnection c = getConnection();
        
		ITable chemicals = 	c.createQueryTable("EXPECTED","SELECT * FROM chemicals");
		Assert.assertEquals(5,chemicals.getRowCount());
		ITable strucs = 	c.createQueryTable("EXPECTED","SELECT * FROM structure");
		Assert.assertEquals(5,strucs.getRowCount());
		ITable srcdataset = 	c.createQueryTable("EXPECTED","SELECT * FROM src_dataset");
		Assert.assertEquals(1,srcdataset.getRowCount());
		ITable struc_src = 	c.createQueryTable("EXPECTED","SELECT * FROM struc_dataset");
		Assert.assertEquals(2,struc_src.getRowCount());
		ITable property = 	c.createQueryTable("EXPECTED","SELECT * FROM properties");
		Assert.assertEquals(4,property.getRowCount());
		ITable property_values = 	c.createQueryTable("EXPECTED","SELECT * FROM property_values");
		Assert.assertEquals(4,property_values.getRowCount());
		
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("ambit2/db/processors/cid/property.csv");
		Assert.assertNotNull(in);
		IIteratingChemObjectReader reader = FileInputState.getReader(in,".csv");
		
		importProperties(reader,c.getConnection(),new SmilesKey());
        c.close();
        
        c = getConnection();
		chemicals = 	c.createQueryTable("EXPECTED","SELECT * FROM chemicals");
		Assert.assertEquals(5,chemicals.getRowCount());
		strucs = 	c.createQueryTable("EXPECTED","SELECT * FROM structure");
		Assert.assertEquals(5,strucs.getRowCount());
		srcdataset = 	c.createQueryTable("EXPECTED","SELECT * FROM src_dataset where name='Imported properties'");
		Assert.assertEquals(1,srcdataset.getRowCount());
		struc_src = 	c.createQueryTable("EXPECTED","SELECT * FROM struc_dataset");
		Assert.assertEquals(2,struc_src.getRowCount());
		
		property = 	c.createQueryTable("EXPECTED","SELECT * FROM properties");
		Assert.assertEquals(7,property.getRowCount());
		property_values = 	c.createQueryTable("EXPECTED","SELECT * FROM property_values");
		Assert.assertEquals(10,property_values.getRowCount());		
		property_values = 	c.createQueryTable("EXPECTED","SELECT * FROM property_values where idstructure=100215");
		Assert.assertEquals(5,property_values.getRowCount());			
		ITable tuples = 	c.createQueryTable("EXPECTED","SELECT * FROM tuples");
		Assert.assertEquals(2,tuples.getRowCount());			
		ITable p_tuples = 	c.createQueryTable("EXPECTED","SELECT * FROM property_tuples join tuples using(idtuple) join src_dataset using(id_srcdataset) where name='Imported properties'");
		Assert.assertEquals(6,p_tuples.getRowCount());				
		c.close();
		/**
		 * Removing redundant properties
insert ignore into property_values
select id,idproperty,idstructure,idvalue,idtype,user_name,status from property_values where idstructure>3
on duplicate key update idstructure=3
delete from property_values where idstructure>3

insert ignore into struc_dataset
select idstructure,id_srcdataset from struc_dataset where idstructure>3
on duplicate key update idstructure=3
delete from struc_dataset where idstructure>3
		 */

	}			
	
	@Test
	public void testImportPropertiesByKey() throws Exception {
		
		setUpDatabase("src/test/resources/ambit2/db/processors/test/dataset-properties.xml");
        IDatabaseConnection c = getConnection();
        
		ITable chemicals = 	c.createQueryTable("EXPECTED","SELECT * FROM chemicals");
		Assert.assertEquals(5,chemicals.getRowCount());
		ITable strucs = 	c.createQueryTable("EXPECTED","SELECT * FROM structure");
		Assert.assertEquals(5,strucs.getRowCount());
		ITable srcdataset = 	c.createQueryTable("EXPECTED","SELECT * FROM src_dataset");
		Assert.assertEquals(1,srcdataset.getRowCount());
		ITable struc_src = 	c.createQueryTable("EXPECTED","SELECT * FROM struc_dataset");
		Assert.assertEquals(2,struc_src.getRowCount());
		ITable property = 	c.createQueryTable("EXPECTED","SELECT * FROM properties");
		Assert.assertEquals(4,property.getRowCount());
		ITable property_values = 	c.createQueryTable("EXPECTED","SELECT * FROM property_values");
		Assert.assertEquals(4,property_values.getRowCount());
		
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("ambit2/db/processors/cid/712.sdf");
		Assert.assertNotNull(in);
		IIteratingChemObjectReader reader = FileInputState.getReader(in,".sdf");
		ITable tuples = 	c.createQueryTable("EXPECTED","SELECT * FROM tuples");
		Assert.assertEquals(0,tuples.getRowCount());	
		
		importProperties(reader,c.getConnection(),new PubchemCID());
        c.close();
        
        c = getConnection();
		chemicals = 	c.createQueryTable("EXPECTED","SELECT * FROM chemicals");
		Assert.assertEquals(5,chemicals.getRowCount());
		strucs = 	c.createQueryTable("EXPECTED","SELECT * FROM structure");
		Assert.assertEquals(5,strucs.getRowCount());
		srcdataset = 	c.createQueryTable("EXPECTED","SELECT * FROM src_dataset where name='Imported properties'");
		Assert.assertEquals(1,srcdataset.getRowCount());
		struc_src = 	c.createQueryTable("EXPECTED","SELECT * FROM struc_dataset");
		Assert.assertEquals(2,struc_src.getRowCount());
		
		property = 	c.createQueryTable("EXPECTED","SELECT * FROM properties");
		Assert.assertEquals(43,property.getRowCount());
		property_values = 	c.createQueryTable("EXPECTED","SELECT * FROM property_values");
		Assert.assertEquals(43,property_values.getRowCount());		
		property_values = 	c.createQueryTable("EXPECTED","SELECT * FROM property_values where idstructure=100215");
		Assert.assertEquals(2,property_values.getRowCount());			
		tuples = 	c.createQueryTable("EXPECTED","SELECT * FROM tuples");
		Assert.assertEquals(5,tuples.getRowCount());			
		ITable p_tuples = 	c.createQueryTable("EXPECTED","SELECT * FROM property_tuples join tuples using(idtuple) join src_dataset using(id_srcdataset) where name='Imported properties'");
		Assert.assertEquals(115,p_tuples.getRowCount());				
		c.close();
		/**
		 * Removing redundant properties
insert ignore into property_values
select id,idproperty,idstructure,idvalue,idtype,user_name,status from property_values where idstructure>3
on duplicate key update idstructure=3
delete from property_values where idstructure>3

insert ignore into struc_dataset
select idstructure,id_srcdataset from struc_dataset where idstructure>3
on duplicate key update idstructure=3
delete from struc_dataset where idstructure>3
		 */

	}				
}
