package ambit2.db.reporters.test;

import java.io.StringWriter;

import org.junit.Test;

import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.ProcessorsChain;
import ambit2.db.processors.test.DbUnitTest;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.SDFReporter;
import ambit2.db.search.structure.AbstractStructureQuery;
import ambit2.db.search.structure.QueryStructure;


public class QueryReporterTest extends DbUnitTest {
	@Test
	public void test() throws Exception {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/descriptors-datasets.xml");
		/*
		IStructureKey<IAtomContainer,String> queryKey = new SmilesKey();
		
		queryKey.process(target);
		*/
		AbstractStructureQuery query_property = new QueryStructure();
		query_property.setValue("F.[F-].[Na+]");
		query_property.setFieldname("smiles");
		/*
		if (queryKey.getQueryKey()!=null)
			query_property.setFieldname(queryKey.getQueryKey());
		else 
			query_property.setFieldname(null);
			*/
		SDFReporter<IQueryRetrieval<IStructureRecord>> reporter = new SDFReporter<IQueryRetrieval<IStructureRecord>>();
		reporter.setConnection(getConnection().getConnection());
		reporter.setOutput(new StringWriter());
		reporter.process(query_property);
		reporter.getOutput().flush();
		reporter.getOutput().close();
		System.out.println(reporter.getOutput().toString());
		reporter.close();
		
		
	}
}
