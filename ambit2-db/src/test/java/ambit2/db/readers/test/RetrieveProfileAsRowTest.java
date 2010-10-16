package ambit2.db.readers.test;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.RetrieveProfileValuesAsRow;
import ambit2.db.results.AmbitRows;

public class RetrieveProfileAsRowTest extends RetrieveTest<IStructureRecord> {

	@Override
	protected String getTestDatabase() {
		return "src/test/resources/ambit2/db/processors/test/dataset-properties.xml";
	}
	
	@Override
	protected IQueryRetrieval<IStructureRecord> createQuery() {
		Profile<Property> profile = new Profile<Property>();
		
		for (int i=1;i < 5;i++) {
			//for the test only id matters
			Property p = Property.getInstance(String.format("test%d",i),String.format("test%d",i));
			p.setId(i);
			p.setEnabled(true);
			profile.add(p);
		}
		
		RetrieveProfileValuesAsRow q = new RetrieveProfileValuesAsRow();
		q.setFieldname(profile);
		q.setValue(new int[] {100211,100214,100215,129345});
		return q;
	}
	
	@Override
	protected void verifyRows(AmbitRows<IStructureRecord> rows) throws Exception {
	//IDatabaseConnection c = getConnection();
		Assert.assertNotNull(rows);
		//Assert.assertEquals(4,rows.size());
		while (rows.next()) {
			IStructureRecord r = rows.getObject();
			System.out.println(r);
			for (Property p:r.getProperties()) {
				System.out.println(p);
				System.out.println(r.getProperty(p));
			}
		
		}
	}	
	
	@Override
	protected AmbitRows<IStructureRecord> createRows() throws Exception {
		return new AmbitRows<IStructureRecord>();
	}

}
