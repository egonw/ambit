package ambit2.db.reporters;

import java.io.IOException;
import java.io.Writer;

import org.openscience.cdk.CDKConstants;

import ambit2.core.data.IStructureRecord;
import ambit2.core.exceptions.AmbitException;
import ambit2.core.processors.DefaultAmbitProcessor;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.QuerySmilesByID;

public class SmilesReporter<Q extends IQueryRetrieval<IStructureRecord>> extends QueryReporter<IStructureRecord, Q, Writer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3648376868814044783L;

	public SmilesReporter() {
		getProcessors().clear();
		getProcessors().add(new ProcessorStructureRetrieval(new QuerySmilesByID()));
		getProcessors().add(new DefaultAmbitProcessor<IStructureRecord,IStructureRecord>() {
			public IStructureRecord process(IStructureRecord target) throws AmbitException {
				processItem(target,getOutput());
				return target;
			};
		});	
	}
	@Override
	protected void processItem(IStructureRecord item, Writer output) {
		try {
			output.write(item.getProperties().get(CDKConstants.SMILES).toString());
			output.write('\n');
			output.flush();
		} catch (IOException x) {
			x.printStackTrace();
		}
		
	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}


}
