/* DatasetCRUDTest.java
 * Author: nina
 * Date: Mar 28, 2009
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

package ambit2.db.update.test;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;

import ambit2.base.data.SourceDataset;
import ambit2.db.update.IQueryUpdate;
import ambit2.db.update.dataset.CreateDataset;
import ambit2.db.update.dataset.DeleteDataset;
import ambit2.db.update.dataset.UpdateDataset;

public class Dataset_crud_test extends CRUDTest<Object,SourceDataset> {
	@Override
	public void setUp() throws Exception {
		super.setUp();
		dbFile = "src/test/resources/ambit2/db/processors/test/dataset-properties.xml";			
	}
	@Override
	protected IQueryUpdate<Object,SourceDataset> createQuery() throws Exception {
		SourceDataset adataset = new SourceDataset();
		adataset.setName("ambit");
		adataset.setTitle("new_title");
		adataset.setURL("new_url");
		CreateDataset user = new CreateDataset();
		user.setObject(adataset);
		return user;
	}

	@Override
	protected void createVerify(IQueryUpdate<Object,SourceDataset> query) throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED_USER","SELECT * FROM src_dataset where name='ambit'");
		Assert.assertEquals(1,table.getRowCount());
		table = c.createQueryTable("EXPECTED_USERROLE","SELECT * FROM catalog_references where title='new_title'");
		Assert.assertEquals(1,table.getRowCount());		
		c.close();
	}

	@Override
	protected IQueryUpdate<Object,SourceDataset> deleteQuery() throws Exception {
		SourceDataset adataset = new SourceDataset();
		adataset.setName("Dataset 1");		
		return new DeleteDataset(adataset);
	}

	@Override
	protected void deleteVerify(IQueryUpdate<Object,SourceDataset> query) throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM src_dataset where user_name='Dataset 1'");
		Assert.assertEquals(0,table.getRowCount());
		/* shouldn't structures be deleted as well?
		table = c.createQueryTable("EXPECTED","SELECT * FROM structure");
		Assert.assertEquals(2,table.getRowCount());
		*/		
		c.close();
	}

	@Override
	protected IQueryUpdate<Object,SourceDataset> updateQuery() throws Exception {
		SourceDataset adataset = new SourceDataset();
		adataset.setId(1);		
		adataset.setName("nina");
		adataset.setTitle("EURAS.BE");
		return new UpdateDataset(adataset);
	}

	@Override
	protected void updateVerify(IQueryUpdate<Object,SourceDataset> query) throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM src_dataset where name='nina'");
		Assert.assertEquals(1,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM src_dataset where name='Dataset 1'");
		Assert.assertEquals(0,table.getRowCount());		
		c.close();
	}
	@Override
	protected IQueryUpdate<Object, SourceDataset> createQueryNew()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	protected void createVerifyNew(IQueryUpdate<Object, SourceDataset> query)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void testCreateNew() throws Exception {

	}
}
