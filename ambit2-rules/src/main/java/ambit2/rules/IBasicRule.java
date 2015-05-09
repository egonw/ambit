package ambit2.rules;

import java.util.List;

import ambit2.rules.conditions.ICondition;


public interface IBasicRule 
{
	public String getName();
	public List<ICondition> getConditions();
}
