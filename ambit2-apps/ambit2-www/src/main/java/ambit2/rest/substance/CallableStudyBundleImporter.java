package ambit2.rest.substance;

import java.io.File;
import java.util.List;

import net.idea.modbcum.p.AbstractDBProcessor;

import org.apache.commons.fileupload.FileItem;
import org.restlet.Context;
import org.restlet.data.Reference;

import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.substance.ReadSubstanceByStudy._studysearchmode;
import ambit2.db.substance.processor.DBBundleStudyWriter;
import ambit2.rest.dataset.DatasetURIReporter;

public class CallableStudyBundleImporter<USERID> extends CallableSubstanceImporter<USERID> {
    private SubstanceEndpointsBundle bundle;
    public enum _mode { studyimport, matrixvaluedelete}; 
    protected _mode mode = _mode.studyimport;
    public _mode getMode() {
        return mode;
    }

    public void setMode(_mode mode) {
        this.mode = mode;
    }

    public SubstanceEndpointsBundle getBundle() {
	return bundle;
    }

    public void setBundle(SubstanceEndpointsBundle bundle) {
	this.bundle = bundle;
    }

    public CallableStudyBundleImporter(List<FileItem> items, String fileUploadField,
	    Reference applicationRootReference, Context context, SubstanceURIReporter substanceReporter,
	    DatasetURIReporter datasetURIReporter, USERID token) throws Exception {
	super(items, fileUploadField, applicationRootReference, context, substanceReporter, datasetURIReporter, token);
    }
    public CallableStudyBundleImporter(File file, Reference applicationRootReference, Context context,
	    SubstanceURIReporter substanceReporter, DatasetURIReporter datasetURIReporter, USERID token)
	    throws Exception {
	this(_mode.studyimport,file, applicationRootReference, context, substanceReporter, datasetURIReporter, token);
    }
    public CallableStudyBundleImporter(_mode mode,File file, Reference applicationRootReference, Context context,
	    SubstanceURIReporter substanceReporter, DatasetURIReporter datasetURIReporter, USERID token)
	    throws Exception {
	super(file, applicationRootReference, context, substanceReporter, datasetURIReporter, token);
	setMode(mode);
    }
    @Override
    protected AbstractDBProcessor<IStructureRecord, IStructureRecord> createWriter() {
	return new DBBundleStudyWriter(bundle, dataset, importedRecord);
    }
}
