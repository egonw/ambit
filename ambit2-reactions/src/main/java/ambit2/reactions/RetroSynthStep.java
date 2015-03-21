package ambit2.reactions;


import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.reactions.rules.IRetroSynthRuleInstance;

public class RetroSynthStep 
{
	public IRetroSynthRuleInstance instance;	
	public IAtomContainer reactants;
}
