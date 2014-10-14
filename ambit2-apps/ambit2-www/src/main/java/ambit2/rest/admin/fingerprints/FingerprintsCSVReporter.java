package ambit2.rest.admin.fingerprints;

import java.io.Writer;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;

import org.restlet.Request;

import ambit2.db.reporters.QueryReporter;
import ambit2.db.update.fp.IFingerprint;
import ambit2.db.update.fp.QueryFingerprints;
import ambit2.descriptors.processors.BitSetGenerator.FPTable;
import ambit2.rest.ResourceDoc;

public class FingerprintsCSVReporter extends QueryReporter<IFingerprint<FPTable,String>,QueryFingerprints,Writer>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1461184295760854495L;

	public FingerprintsCSVReporter(Request request, ResourceDoc doc) {
		super();
	}
	@Override
	public void footer(Writer output, QueryFingerprints query) {
		
	}

	@Override
	public void header(Writer output, QueryFingerprints query) {
		try {
		getOutput().write("Type,Frequency,Fingerprint\n");
		} catch (Exception x) {}
	}

	@Override
	public Object processItem(IFingerprint<FPTable, String> item)
			throws AmbitException {
		try {
		getOutput().write(String.format("%s,%d,%s\n",item.getType().name(),item.getFrequency(),item.getBits()));
		} catch (Exception x) {}
		return item;
	}

	@Override
	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}

}
