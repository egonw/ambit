package ambit2.rest.model.predictor;

import java.sql.Connection;

import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.model.Algorithm.AlgorithmFormat;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.processors.DescriptorsCalculator;
import ambit2.descriptors.processors.DescriptorsFactory;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.property.PropertyURIReporter;

public class DescriptorPredictor  extends	ModelPredictor<DescriptorsCalculator,IStructureRecord> {
	DescriptorsCalculator calculator;
	public DescriptorPredictor(
			Reference applicationRootReference,
			ModelQueryResults model, 
			ModelURIReporter modelReporter,
			PropertyURIReporter propertyReporter, 
			String[] targetURI) throws ResourceException {
		super(applicationRootReference,model,modelReporter,propertyReporter,targetURI);
		structureRequired = true;
		valuesRequired = model.getPredictors().size()==0;
	}
	
	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		super.setConnection(connection);
		calculator.setConnection(connection);
	}
	@Override
	public DescriptorsCalculator createPredictor(ModelQueryResults model)
			throws ResourceException {
		if (model.getContentMediaType().equals(AlgorithmFormat.JAVA_CLASS.getMediaType()))
			try {
				Profile<Property> p = new Profile<Property>();
				Property property = DescriptorsFactory.createDescriptor2Property(model.getContent());
				property.setEnabled(true);
				p.add(property);
				
				calculator = new DescriptorsCalculator();
				calculator.setDescriptors(p);	
				return calculator;
			} catch (Exception x) {
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
			}
		else throw new ResourceException(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE,model.getContentMediaType());
	}
	@Override
	public String getCompoundURL(IStructureRecord target) throws AmbitException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object predict(IStructureRecord target) throws AmbitException {
		return calculator.process(target);
	}

}
