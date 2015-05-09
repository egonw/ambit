package ambit2.rules;

import ambit2.rules.actions.IAction;

public interface IActionRule extends IBasicRule 
{
	public IAction getAction();	
	public void applyAction(Object target) throws Exception;
}
