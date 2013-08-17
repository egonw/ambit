package ambit2.rest.substance;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import net.idea.i5.io.I5ZReader;

import org.apache.commons.fileupload.FileItem;
import org.openscience.cdk.io.IChemObjectReaderErrorHandler;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.ProcessorsChain;
import ambit2.core.io.FileInputState;
import ambit2.core.io.IInputState;
import ambit2.db.processors.AbstractBatchProcessor;
import ambit2.db.processors.BatchDBProcessor;
import ambit2.db.processors.DBProcessorsChain;
import ambit2.db.processors.DBSubstanceWriter;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.QueryExecutor;
import ambit2.db.update.dataset.ReadDataset;
import ambit2.rest.dataset.DatasetURIReporter;
import ambit2.rest.structure.ConformerURIReporter;
import ambit2.rest.task.CallableFileUpload;
import ambit2.rest.task.CallableQueryProcessor;
import ambit2.rest.task.TaskResult;

/**
 * 
 * @author nina
 *
 * @param <USERID>
 */
public class CallableSubstanceImporter<USERID> extends CallableQueryProcessor<FileInputState, IStructureRecord,USERID> {
	protected SubstanceURIReporter substanceReporter;
	protected DatasetURIReporter datasetURIReporter;
	protected SubstanceRecord importedRecord;
	protected SourceDataset dataset;
	private File file;
	protected String fileDescription;
	protected File getFile() {
		return file;
	}

	protected void setFile(File file,String description) {
		this.file = file;
		this.fileDescription = description;
	}
	
	public CallableSubstanceImporter(
				List<FileItem> items,
				String fileUploadField, 
				Reference applicationRootReference,
				Context context,
				SubstanceURIReporter substanceReporter,
				DatasetURIReporter datasetURIReporter,
				USERID token) throws Exception {
		super(applicationRootReference,null,  context,  token);
		try { processForm(items, fileUploadField); } catch (Exception x) {}
		this.substanceReporter = substanceReporter;
		this.datasetURIReporter = datasetURIReporter;
	}
	@Override
	protected void processForm(Reference applicationRootReference, Form form) {
		sourceReference = null;
	}
	
	
	protected void processForm(List<FileItem> items,String fileUploadField) throws Exception {
		CallableFileUpload upload = new CallableFileUpload(items, fileUploadField) {
			@Override
			public Reference createReference() {
				return null;
			}

			@Override
			protected void processFile(File file,String description) throws Exception {
				setFile(file,description);
			}
			@Override
			protected void processProperties(
					Hashtable<String, String> properties) throws Exception {
			}
		};
		upload.call();
	}
	

	@Override
	protected FileInputState createTarget(Reference reference) throws Exception {
		if (file==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		return new FileInputState(file);
	}
	
	@Override
	protected AbstractBatchProcessor createBatch(FileInputState target)
			throws Exception {
		final BatchDBProcessor batch = new BatchDBProcessor() {
			@Override
			public Iterator<String> getIterator(IInputState target)
					throws AmbitException {
				try {
					File file = ((FileInputState) target).getFile();
					I5ZReader reader = new I5ZReader(file);
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
				} catch (AmbitException x) {

					throw x;
				} catch (Exception x) {
					throw new AmbitException(x);
				}
			}
		};
		return batch;
	}

	@Override
	protected ProcessorsChain<IStructureRecord, IBatchStatistics, IProcessor> createProcessors()
			throws Exception {
		DBProcessorsChain chain = new DBProcessorsChain();
		dataset = DBSubstanceWriter.datasetMeta();
		importedRecord = new SubstanceRecord();
		DBSubstanceWriter writer = new DBSubstanceWriter(dataset,importedRecord);
		chain.add(writer);
		return chain;
	}

	@Override
	protected TaskResult createReference(Connection connection)
			throws Exception {

		if ((importedRecord.getIdsubstance()>0) || (importedRecord.getCompanyUUID()!=null)) {
			
			try { batch.close();	} catch (Exception xx) {}
			return new TaskResult(substanceReporter.getURI(importedRecord));				
		} else {
			SourceDataset newDataset = dataset;
			if (newDataset.getId()<=0) {
				ReadDataset q = new ReadDataset();
				q.setValue(newDataset);
				QueryExecutor<ReadDataset> x = new QueryExecutor<ReadDataset>();
				x.setConnection(connection);
				ResultSet rs = x.process(q);
	
				while (rs.next()) {
					newDataset = q.getObject(rs);
					if (newDataset.getId()>0)
					break;
				}
				x.closeResults(rs);
				x.setConnection(null);
			}
			if (newDataset == null || newDataset.getId()<=0)
				throw new ResourceException(Status.SUCCESS_NO_CONTENT);

			try { batch.close();	} catch (Exception xx) {}
			return new TaskResult(datasetURIReporter.getURI(newDataset));
		}
	}

}
