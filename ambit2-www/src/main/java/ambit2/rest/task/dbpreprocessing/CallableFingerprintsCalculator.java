package ambit2.rest.task.dbpreprocessing;

import java.sql.Connection;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.AmbitUser;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IBatchStatistics.RECORDS_STATS;
import ambit2.base.processors.ProcessorsChain;
import ambit2.core.data.model.Algorithm;
import ambit2.db.DbReaderStructure;
import ambit2.db.processors.AbstractBatchProcessor;
import ambit2.db.processors.AbstractUpdateProcessor;
import ambit2.db.processors.BitSetGenerator;
import ambit2.db.processors.FP1024Writer;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.processors.AbstractRepositoryWriter.OP;
import ambit2.db.processors.FP1024Writer.FPTable;
import ambit2.db.processors.quality.FPStructureWriter;
import ambit2.db.readers.RetrieveStructure;
import ambit2.db.search.structure.AbstractStructureQuery;
import ambit2.db.search.structure.MissingFingerprintsQuery;
import ambit2.db.update.qlabel.CreateQLabelPair;
import ambit2.db.update.qlabel.smarts.SMARTSAcceleratorWriter;
import ambit2.rest.task.CallableQueryProcessor;
import ambit2.smarts.processors.SMARTSPropertiesGenerator;

/**
 * Dataset fingerprints
 * @author nina
 *
 */
public class CallableFingerprintsCalculator extends	CallableQueryProcessor<Object, IStructureRecord> {
	protected FPTable fingerprintsType = FPTable.fp1024;
	
	public FPTable getFingerprintsType() {
		return fingerprintsType;
	}

	public void setFingerprintsType(FPTable fingerprintsType) {
		this.fingerprintsType = fingerprintsType;
	}

	public CallableFingerprintsCalculator(Form form,
			Reference applicationRootReference,Context context,
			Algorithm algorithm) throws ResourceException {
		super(form, context);
		try {
			setFingerprintsType(FPTable.valueOf(algorithm.getContent().toString()));
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,algorithm.getContent().toString(),x);
		}

	}
	protected long batchSize = 10000;
	@Override
	protected ProcessorsChain<IStructureRecord, IBatchStatistics, IProcessor> createProcessors()
			throws Exception {
		ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor> p = 
			new ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor>();
		RetrieveStructure r = new RetrieveStructure(true);
		r.setPageSize(1);
		r.setPage(0);
		p.add(new ProcessorStructureRetrieval(r));
	

		
		switch (getFingerprintsType()) {
		case fp1024: {
			p.add(new BitSetGenerator(getFingerprintsType()));
			p.add(new FP1024Writer(getFingerprintsType()));
			break;
		}
		case fp1024_struc: {
			p.add(new BitSetGenerator(getFingerprintsType()));
			p.add(new FPStructureWriter());			
			break;
		}
		case sk1024: {
			p.add(new BitSetGenerator(getFingerprintsType()));
			p.add(new FP1024Writer(getFingerprintsType()));
			break;
		}
		case smarts_accelerator: {
			p.add(new SMARTSPropertiesGenerator());
			p.add(new SMARTSAcceleratorWriter());
			break;
		}
		}
		
		return p;
	}


	@Override
	protected Object createTarget(Reference reference) throws Exception {
		//can have combined query with a dataset query if dataset_uri is present
		MissingFingerprintsQuery q =  new MissingFingerprintsQuery(getFingerprintsType());
		q.setPageSize(batchSize);
		q.setPage(0);
		return q;
	}
	protected AbstractBatchProcessor createBatch(Object target) throws Exception{

		if (target instanceof AbstractStructureQuery) {
			DbReaderStructure reader = new DbReaderStructure();
			reader.setHandlePrescreen(true);
			return reader;
		} else throw new Exception("Can't process "+ target.toString());
	}
	
	@Override
	protected IBatchStatistics runBatch(Object target) throws Exception {
		IBatchStatistics stats;
		while (true) {
			stats = batch.process(target);
			if (stats.getRecords(RECORDS_STATS.RECORDS_PROCESSED)==0) break;
			stats.setRecords(RECORDS_STATS.RECORDS_PROCESSED, 0);
			stats.setRecords(RECORDS_STATS.RECORDS_ERROR, 0);
			stats.setRecords(RECORDS_STATS.RECORDS_READ,0);
		}
		return stats;
	}
	

	@Override
	protected Reference createReference(Connection connection) throws Exception {
		switch (getFingerprintsType()) {
		case fp1024: {
			return null;
		}
		case fp1024_struc: {
			return structureQuality(connection);
		}
		case sk1024: {
			return null;
		}
		case smarts_accelerator: {
			return null;
		}		
		default: {
			throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED,getFingerprintsType().toString());
		}
		}
	}	
	
	protected Reference structureQuality(Connection connection) throws Exception {
		try {
			CreateQLabelPair q = new CreateQLabelPair();
			AbstractUpdateProcessor<AmbitUser, String> p = new AbstractUpdateProcessor<AmbitUser, String>(OP.CREATE,q);
			p.setConnection(connection);
			p.setCloseConnection(true);
			p.process(null);
		} catch (Exception x) {
			throw x;
		} finally {
			try { connection.close(); } catch (Exception xx) {};
		}
		return null;
	}
}
