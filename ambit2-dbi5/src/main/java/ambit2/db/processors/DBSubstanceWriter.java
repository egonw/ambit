package ambit2.db.processors;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.ILiteratureEntry._type;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.STRUC_TYPE;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.core.processors.structure.key.ReferenceSubstanceUUID;
import ambit2.db.AbstractDBProcessor;
import ambit2.db.UpdateExecutor;
import ambit2.db.substance.CreateSubstance;
import ambit2.db.substance.ids.UpdateSubstanceIdentifiers;
import ambit2.db.substance.relation.UpdateSubstanceRelation;
import ambit2.db.substance.study.DeleteEffectRecords;
import ambit2.db.substance.study.DeleteStudy;
import ambit2.db.substance.study.UpdateEffectRecords;
import ambit2.db.substance.study.UpdateSubstanceStudy;

/**
 * Writes IUCLID5 substances
 * @author nina
 *
 */
public class DBSubstanceWriter  extends AbstractDBProcessor<IStructureRecord, IStructureRecord> {
    /**
	 * 
	 */
	private static final long serialVersionUID = -2237399197958151808L;
	private CreateSubstance q;
	private UpdateSubstanceIdentifiers qids;
    private UpdateSubstanceRelation qr;
    private UpdateSubstanceStudy qss;
    private UpdateEffectRecords qeffr;
    private DeleteEffectRecords deffr;
    private UpdateExecutor x;
    private DeleteStudy deleteStudy;
    private RepositoryWriter writer;
    protected boolean clearMeasurements;
    
    protected boolean splitRecord = true;
    
	public boolean isSplitRecord() {
		return splitRecord;
	}

	public void setSplitRecord(boolean splitRecord) {
		this.splitRecord = splitRecord;
	}

	protected SubstanceRecord importedRecord;

    
	public SubstanceRecord getImportedRecord() {
		return importedRecord;
	}

	public void setImportedRecord(SubstanceRecord importedRecord) {
		this.importedRecord = importedRecord;
	}

	public SourceDataset getDataset() {
		return writer.getDataset();
	}

	public DBSubstanceWriter(SourceDataset dataset,SubstanceRecord importedRecord,boolean clearMeasurements) {
		super();
	    q = new CreateSubstance();
	    qids = new UpdateSubstanceIdentifiers();
	    qr = new UpdateSubstanceRelation();
	    deleteStudy = new DeleteStudy();
	    x = new UpdateExecutor();
	    x.setCloseConnection(false);
	    writer = new RepositoryWriter();
	    writer.setCloseConnection(false);
	    writer.setPropertyKey(new ReferenceSubstanceUUID());
	    writer.setUseExistingStructure(true);
		writer.setDataset(dataset==null?datasetMeta():dataset);
		writer.setBuild2D(true);
		this.importedRecord = importedRecord;
		this.clearMeasurements = clearMeasurements;
	}
	
	public static SourceDataset datasetMeta() {
		ILiteratureEntry reference = LiteratureEntry.getI5UUIDReference();
		reference.setType(_type.Dataset);
		SourceDataset dataset = new SourceDataset("IUCLID5 .i5z file",reference);
		dataset.setLicenseURI(null);
		dataset.setrightsHolder(null);
		return dataset;
	}
	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		super.setConnection(connection);
		x.setConnection(connection);
		writer.setConnection(connection);
	}
	@Override
	public void close() throws SQLException {
		try {x.close();} catch (Exception x) {}
		try {writer.close();} catch (Exception x) {}
		super.close();
	}
	
	protected void importSubstanceMeasurements(SubstanceRecord substance) throws Exception {
		if (substance.getMeasurements()==null) return;
		for (ProtocolApplication papp : substance.getMeasurements()) {
 			if (qss==null) qss = new UpdateSubstanceStudy(importedRecord.getCompanyUUID(), papp);
 			else {
 				qss.setGroup(importedRecord.getCompanyUUID());
 				qss.setObject(papp);
 			}
 			x.process(qss);
 			//delete effects records for this document, if any
 			if (deffr==null) deffr = new DeleteEffectRecords();
 			deffr.setGroup(papp.getDocumentUUID());
 			x.process(deffr);
 			//and add the new ones
 			if ( papp.getEffects()!=null)
 			for (Object effect : papp.getEffects()) 
 				if (effect instanceof EffectRecord) {
 					if (qeffr==null) qeffr = new UpdateEffectRecords(papp.getDocumentUUID(),(EffectRecord)effect);
 					else {qeffr.setGroup(papp.getDocumentUUID()); qeffr.setObject((EffectRecord)effect);}
 					x.process(qeffr);
 				}
 		}		
	}
	protected void importSubstanceRecord(SubstanceRecord substance) throws Exception {
     	q.setObject(substance);
     	x.process(q);
     	qids.setObject(substance);
     	x.process(qids);
 		importedRecord.setCompanyUUID(substance.getCompanyUUID());
 		importedRecord.setIdsubstance(substance.getIdsubstance());
     	if (substance.getRelatedStructures()!=null)
         	for (CompositionRelation rel : substance.getRelatedStructures()) {
         		Object i5uuid = rel.getSecondStructure().getProperty(Property.getI5UUIDInstance());

         		if (rel.getSecondStructure().getIdchemical()<=0) {
         			writer.create(rel.getSecondStructure());		
         		}
         		rel.getSecondStructure().setProperty(Property.getI5UUIDInstance(),i5uuid);			         		
         		qr.setCompositionRelation(rel);
         		x.process(qr);
         	}
     
     	if (clearMeasurements) try {
     		deleteStudy.setGroup(substance.getCompanyUUID());
     		x.process(deleteStudy);
     	} catch (Exception x) {
     		logger.log(Level.WARNING,x.getMessage());
     	}
	}
	@Override
	public IStructureRecord process(IStructureRecord record) throws AmbitException {
		 try {
			 if (record==null) return record;
			 if (record instanceof SubstanceRecord) {
	         	SubstanceRecord substance = (SubstanceRecord) record;
	         	if (isSplitRecord()) {
		         	if (substance.getMeasurements()!=null) 
		         		importSubstanceMeasurements(substance);
		         	else 
		         		importSubstanceRecord(substance);
	         	} else {
	         		importSubstanceRecord(substance);
	         		importedRecord = substance;
	         		importSubstanceMeasurements(substance);
	         	}
	         	
	         } else if (record instanceof IStructureRecord) {
	        	 if (STRUC_TYPE.NA.equals(((IStructureRecord)record).getType())) {
	        		 writer.create(record); //with the current settings, if the structure is already there, it will be used
	        	 } else {
	        		 writer.update(record);  //with the current settings, if the structure is already there, it will be updated
	        	 }	 
	         }
			 return record;
		 } catch (AmbitException x) {
			 throw x;
		 } catch (Exception x) {
			 x.printStackTrace();
			 throw new AmbitException(x);
		 }
	}

	@Override
	public void open() throws DbAmbitException {
		
	}
	
}