package ambit2.db.facet.test;

import java.sql.ResultSet;

import junit.framework.Assert;
import ambit2.base.facet.IFacet;
import ambit2.db.facets.datasets.DatasetByPrefixNameFacetQuery;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.test.QueryTest;

public class DatasetPrefixFacet extends QueryTest<IQueryRetrieval<ambit2.db.facets.datasets.DatasetPrefixFacet>>  {

	@Override
	public String getDbFile() {
		setDbFile("src/test/resources/ambit2/db/processors/test/query-datasets-string.xml");
		return super.getDbFile();
	}
	
	@Override
	protected IQueryRetrieval<ambit2.db.facets.datasets.DatasetPrefixFacet> createQuery()
			throws Exception {
		DatasetByPrefixNameFacetQuery q = new DatasetByPrefixNameFacetQuery(null);
		return q;
	}
	/**
	 */
	@Override
	protected void verify(IQueryRetrieval<ambit2.db.facets.datasets.DatasetPrefixFacet> query, ResultSet rs)
			throws Exception {
		int count = 0;
		while (rs.next()) {
			IFacet<String> record = query.getObject(rs);
			Assert.assertEquals("S",record.getValue());
			Assert.assertEquals(1,record.getCount());
			count++;
		}
		Assert.assertEquals(1,count);
		
	}

}
