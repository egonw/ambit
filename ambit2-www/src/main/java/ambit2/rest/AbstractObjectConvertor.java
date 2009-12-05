package ambit2.rest;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;

/**
 * 
 * @author nina
 * Creates entire object and then converts it to a representation, no streaming
 * @param <T>
 * @param <Q>
 */
public abstract  class AbstractObjectConvertor<T,Q extends IQueryRetrieval<T>,Output> 
									extends QueryRepresentationConvertor<T,Q,Output>  {
	
	public AbstractObjectConvertor(QueryReporter<T,Q,Output> reporter) {
		this(reporter,MediaType.TEXT_PLAIN);
		if (this.reporter != null) ((QueryReporter<T,Q,Output>)this.reporter).setMaxRecords(5000);
	}
	public AbstractObjectConvertor(QueryReporter<T,Q,Output> reporter,MediaType media) {
		super(reporter,media);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 6126693410309179856L;
	
	public abstract Representation process(Output doc) throws AmbitException; 
	protected abstract Output createOutput(Q query) throws AmbitException;
	
	@Override
	public Representation process(Q query) throws AmbitException {
		reporter.setOutput(createOutput(query));
		return process(reporter.process(query));
	};

}