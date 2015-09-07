package ambit2.rules.conditions;

import ambit2.rules.conditions.value.IValue;
import ambit2.rules.conditions.value.Value;

public class ValueCondition implements IValueCondition
{
	private Value value = null;
	
	public ValueCondition (Value value)
	{
		setValue(value);
	}
		
	@Override
	public boolean isTrue(Object target) {
		if (target instanceof Double)
			return isTrue((Double) target);
		//TODO some other cases Integer, ...
		return false;
	}

	@Override
	public boolean isTrue(Double target) {
		if (value == null)
			return false;
		return value.getRelation().check(value.getValue(), target);
	}

	@Override
	public IValue getValue() {
		return value;
	}

	@Override
	public void setValue(IValue value) {
		if (value instanceof Value)
			this.value = (Value)value;
	}
	
	public void setValue(Value value) {
		this.value = value;
	}
}
