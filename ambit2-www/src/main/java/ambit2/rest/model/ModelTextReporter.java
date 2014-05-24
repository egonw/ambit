package ambit2.rest.model;

import org.restlet.Request;

import ambit2.base.exceptions.AmbitException;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.model.predictor.ModelPredictor;

public class ModelTextReporter<Q extends IQueryRetrieval<ModelQueryResults>> extends ModelURIReporter<Q> {

	public ModelTextReporter(Request baseRef) {
		super(baseRef);
	}
	@Override
	public Object processItem(ModelQueryResults model) throws AmbitException {
		try {
			ModelPredictor predictor = ModelPredictor.getPredictor(model, request);
			getOutput().write(predictor.toString());
			return null;
		
		} catch (AmbitException x) {
			throw x;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
}
