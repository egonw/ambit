package ambit2.db.reporters;

import java.io.Writer;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.IQueryRetrieval;

/**
 * Very simple html reporter
 * @author nina
 *
 * @param <Q>
 */
public class HTMLReporter <Q extends IQueryRetrieval<IStructureRecord>> extends QueryReporter<IStructureRecord, Q, Writer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2931123688036795689L;
	public HTMLReporter() {
		getProcessors().clear();
		getProcessors().add(new ProcessorStructureRetrieval());
		getProcessors().add(new DefaultAmbitProcessor<IStructureRecord,IStructureRecord>() {
			public IStructureRecord process(IStructureRecord target) throws AmbitException {
				processItem(target,getOutput());
				return target;
			};
		});	
	}

	
	@Override
	public void processItem(IStructureRecord item, Writer writer) {

		try {
			//TODO generate PDFcontent
			writer.write("<p>");
			writer.write(item.getContent());		
		} catch (Exception x) {
			logger.error(x);
		}
		
	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
	public void footer(Writer output, Q query) {
		try {
		output.write(String.format("<html><head><title>%s</title><body>",query.toString()));
		} catch (Exception x) {
			
		}
	};
	public void header(Writer output, Q query) {
		try {
			output.write("</body></html>");
			output.flush();
		} catch (Exception x) {
			
		}
	};

}