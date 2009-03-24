package ambit2.core.processors.batch;

import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.base.exceptions.AmbitIOException;
import ambit2.core.io.IInputState;

public class DefaultInputState extends DefaultIOState implements IInputState {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8642417087199310257L;
	protected transient IIteratingChemObjectReader reader = null;
	public DefaultInputState(IIteratingChemObjectReader reader) {
		super();
		this.reader = reader;
	}

	public IIteratingChemObjectReader getReader()
			throws AmbitIOException {
		return reader;
	}

}
