package ambit2.reactions.reactor;

import ambit2.rules.conditions.AbstractDescriptorSolver;

public class ReactorDescriptorSolver extends AbstractDescriptorSolver
{
	protected ReactorInfoPack rInfo = null;
	
	public ReactorDescriptorSolver()
	{
		setup();
	}
	
	protected void setup()
	{
		
		//Reactant descriptors
		addDescriptor("REACTION_APPLICATIONS_PER_REACTANT");
		
	}
		
	@Override
	public Object calculateDescriptor(String descrName, Object target) 
	{
		if (target instanceof ReactorInfoPack)
			rInfo = (ReactorInfoPack)target;
		else
			return null;
		
		if (descrName.equals("REACTION_APPLICATIONS_PER_REACTANT"))
			return get_REACTION_APPLICATIONS_PER_REACTANT();
				
		return null;
	}
	
	
	private Double get_REACTION_APPLICATIONS_PER_REACTANT()
	{
		if (rInfo.reaction == null || rInfo.reagent == null)
			return null;
		
		double res = 0.0;
		Object rids = rInfo.reagent.getProperty(Reactor.PropertyReactionIds);
		if (rids == null)
			return res;
		
		int reagent_rids[] = (int[]) rids;
		int n = reagent_rids.length;
		for (int i = 0; i < n; i++)
			if (reagent_rids[i] == rInfo.reaction.getId());
				res = res + 1;
		return res;
	}
	
	
	
}
