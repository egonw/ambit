package ambit2.structure2name;

import org.openscience.cdk.interfaces.IAtomContainer;

public class IUPACNameGenerator 
{	
	protected IUPACRuleDataBase ruleDataBase = null;
	
	public IUPACNameGenerator()
	{
		ruleDataBase = IUPACRuleDataBase.getDefaultRuleDataBase();
	}
	
	public String generateIUPACName(IAtomContainer mol) throws Exception
	{
		//TODO
		return null;
	}

	public IUPACRuleDataBase getRuleDataBase() {
		return ruleDataBase;
	}

	
}
