package ambit2.core.data;

import org.openscience.cdk.qsar.result.IDescriptorResult;

public class AbstractDescriptorResultType<T> implements IDescriptorResult {

	protected T value;
	public T getValue() {
		return value;
	}
	public AbstractDescriptorResultType() {
		this(null);
	}
	public AbstractDescriptorResultType(T value) {
		this.value = value;
	}
	public int length() {
		return 1;
	}
	@Override
	public String toString() {
		return value.toString();
	}
}
