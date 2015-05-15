package ambit2.rules.conditions;

import ambit2.rules.conditions.value.IValueInterval;

public interface IValueIntervalCondition extends ICondition
{
	public boolean isTrue(Double target);
	public IValueInterval getValueInterval();
}
