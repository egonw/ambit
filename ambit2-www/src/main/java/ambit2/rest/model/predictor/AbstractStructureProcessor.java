package ambit2.rest.model.predictor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

import org.restlet.data.Reference;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.UpdateExecutor;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.update.structure.UpdateStructure;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.property.PropertyURIReporter;

public abstract class AbstractStructureProcessor<Predictor>  extends	ModelPredictor<Predictor,IStructureRecord>  {
	protected UpdateStructure updateStructure = new UpdateStructure();
	protected UpdateExecutor exec = new UpdateExecutor();
	/**
	 * 
	 */
	private static final long serialVersionUID = 861458621962435019L;

	public AbstractStructureProcessor(Reference applicationRootReference,
			ModelQueryResults model, ModelURIReporter modelReporter,
			PropertyURIReporter propertyReporter, String[] targetURI)
			throws ResourceException {
		super(applicationRootReference, model, modelReporter, propertyReporter,
				targetURI);
	}
	
	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		super.setConnection(connection);
		exec.setConnection(connection);
	}
	@Override
	public void close() throws SQLException {
		try {exec.close();} catch (Exception x) {}
		super.close();
	}

	
	@Override
	public String getCompoundURL(IStructureRecord target) throws AmbitException {
		return null;
	}
	
	@Override
	public void assignResults(IStructureRecord record, Object value)
			throws AmbitException {
	}
	
	@Override
	public Object predict(IStructureRecord target) throws AmbitException {
		try {
			if (target!= null) {
					updateStructure.setObject(target);
					exec.process(updateStructure);
			}
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		}
		return target;
	}	
}
