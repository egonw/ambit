package ambit2.core.processors.structure.key;

public class SampleKey extends PropertyNameKey {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6702546532075828636L;

	public SampleKey() {
		super("SAMPLE");
	}	
	@Override
	protected boolean isValid(Object newkey, Object value) {
		return (value != null) && !".".equals(value);
	}
	
}
