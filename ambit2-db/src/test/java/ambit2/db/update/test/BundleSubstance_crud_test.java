package ambit2.db.update.test;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;

import net.idea.modbcum.i.query.IQueryUpdate;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.update.bundle.substance.AddSubstanceToBundle;
import ambit2.db.update.bundle.substance.DeleteSubstanceFromBundle;

public class BundleSubstance_crud_test extends CRUDTest<SubstanceEndpointsBundle,SubstanceRecord> {
	@Override
	public void setUp() throws Exception {
		super.setUp();
		dbFile = "src/test/resources/ambit2/db/processors/test/descriptors-datasets.xml";			
	}
	@Override
	protected IQueryUpdate<SubstanceEndpointsBundle, SubstanceRecord> createQuery()
			throws Exception {
		AddSubstanceToBundle query = new AddSubstanceToBundle();
		query.setGroup(new SubstanceEndpointsBundle(1));
		query.setObject(new SubstanceRecord(1));
		return query;
	}

	@Override
	protected IQueryUpdate<SubstanceEndpointsBundle, SubstanceRecord> createQueryNew()
			throws Exception {
		AddSubstanceToBundle query = new AddSubstanceToBundle();
		query.setGroup(new SubstanceEndpointsBundle(1));
		query.setObject(new SubstanceRecord("IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734"));
		return query;		
	}

	@Override
	protected IQueryUpdate<SubstanceEndpointsBundle, SubstanceRecord> updateQuery()
			throws Exception {
		return null;
	}
	@Override
	public void testUpdate() throws Exception {

	}
	@Override
	public void testDelete() throws Exception {
		//super.testDelete();
	}
	@Override
	protected IQueryUpdate<SubstanceEndpointsBundle, SubstanceRecord> deleteQuery()
			throws Exception {
		DeleteSubstanceFromBundle query = new DeleteSubstanceFromBundle();
		query.setGroup(new SubstanceEndpointsBundle(1));
		query.setObject(new SubstanceRecord(1));
		return query;
	}

	@Override
	protected void createVerify(
			IQueryUpdate<SubstanceEndpointsBundle, SubstanceRecord> query)
			throws Exception {
		 IDatabaseConnection c = getConnection();	
		 ITable table = 	c.createQueryTable("EXPECTED_BUNDLE","SELECT idbundle,idsubstance from bundle_substance where idbundle=1 and idsubstance=1");
		 Assert.assertEquals(1,table.getRowCount());
  		 c.close();
	}

	@Override
	protected void createVerifyNew(
			IQueryUpdate<SubstanceEndpointsBundle, SubstanceRecord> query)
			throws Exception {
		 IDatabaseConnection c = getConnection();	
		 ITable table = 	c.createQueryTable("EXPECTED_BUNDLE","SELECT idbundle,idsubstance from bundle_substance where idbundle=1 and idsubstance=1");
		 Assert.assertEquals(1,table.getRowCount());
  		 c.close();
	}

	@Override
	protected void updateVerify(
			IQueryUpdate<SubstanceEndpointsBundle, SubstanceRecord> query)
			throws Exception {
	}

	@Override
	protected void deleteVerify(
			IQueryUpdate<SubstanceEndpointsBundle, SubstanceRecord> query)
			throws Exception {
		 IDatabaseConnection c = getConnection();	
		 ITable table = 	c.createQueryTable("EXPECTED_BUNDLE","SELECT idbundle,idsubstance from bundle_substance where idbundle=1 and idsubstance=1");
		 Assert.assertEquals(0,table.getRowCount());
  		 c.close();		
	}

}
