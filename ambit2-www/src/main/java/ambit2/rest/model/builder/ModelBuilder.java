package ambit2.rest.model.builder;

import org.restlet.data.Reference;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.core.data.model.Algorithm;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.algorithm.AlgorithmURIReporter;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.rdf.RDFPropertyIterator;

/**
 * Abstract class for model building
 * @author nina
 *
 * @param <A>
 * @param <T>
 * @param <TrainingInstances>
 * @param <TestInstances>
 * @param <Content>
 */
public abstract class ModelBuilder<DATA,A extends Algorithm,Model extends ModelQueryResults> 
											extends DefaultAmbitProcessor<A, Model> {
	protected ModelURIReporter<IQueryRetrieval<ModelQueryResults>> model_reporter;
	protected AlgorithmURIReporter alg_reporter;
	protected Reference applicationRootReference;
	protected String[] targetURI = null;
	protected String[] parameters = null;	
	protected DATA trainingData = null;

	public DATA getTrainingData() {
		return trainingData;
	}
	public void setTrainingData(DATA trainingData) {
		this.trainingData = trainingData;
	}
	public Reference getApplicationRootReference() {
		return applicationRootReference;
	}
	public void setApplicationRootReference(Reference applicationRootReference) {
		this.applicationRootReference = applicationRootReference;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 3721710990307131078L;
	public ModelBuilder(Reference applicationRootReference,
						ModelURIReporter<IQueryRetrieval<ModelQueryResults>> model_reporter,
						AlgorithmURIReporter alg_reporter) {
		this(applicationRootReference,model_reporter,alg_reporter,null,null);
	}
	public ModelBuilder(Reference applicationRootReference,
			ModelURIReporter<IQueryRetrieval<ModelQueryResults>> model_reporter,
			AlgorithmURIReporter alg_reporter,
			String[] targetURI,
			String[] parameters) {
		super();
		this.alg_reporter = alg_reporter;
		this.model_reporter = model_reporter;
		this.applicationRootReference = applicationRootReference;
		this.targetURI = targetURI;
		this.parameters = parameters;
	}	

	public AlgorithmURIReporter getAlgorithmReporter() {
		return alg_reporter;
	}
	public void setAlgorithmReporter(AlgorithmURIReporter alg_reporter) {
		this.alg_reporter = alg_reporter;
	}
	public ModelURIReporter<IQueryRetrieval<ModelQueryResults>> getModelReporter() {
		return model_reporter;
	}
	public void setModelReporter(
			ModelURIReporter<IQueryRetrieval<ModelQueryResults>> model_reporter) {
		this.model_reporter = model_reporter;
	}		
	
	protected Property createPropertyFromReference(Reference attribute, LiteratureEntry le) {
		RDFPropertyIterator reader = null;
		try {
			reader = new RDFPropertyIterator(attribute);
			reader.setBaseReference(applicationRootReference);
			while (reader.hasNext()) {
				return reader.next();
			}
			return null;	
		} catch (Exception x) {
			return new Property(attribute.toString(),le);
		} finally {
			try {reader.close(); } catch (Exception x) {}
		}
	}	
	
	
}
