package ambit2.tautomers;

import org.openscience.cdk.interfaces.IAtomContainer;
import java.util.Vector;

public class TautomerManager 
{
	KnowledgeBase knowledgeBase; 
	IAtomContainer molecule;
	Vector<IRuleInstance> extendedRuleInstances = new Vector<IRuleInstance>(); 
	Vector<IRuleInstance> ruleInstances = new Vector<IRuleInstance>();
	Vector<IAtomContainer> resultTautomers = new Vector<IAtomContainer>();
	
	
	
	TautomerManager()
	{
		knowledgeBase = new KnowledgeBase();
		if (knowledgeBase.errors.size() > 0)
		{	
			System.out.println(knowledgeBase.getAllErrors());
		}	
	}
	
	
	public void setStructure(IAtomContainer str)
	{	
		molecule = str;
	}
	
	
	public Vector<IAtomContainer> generateTautomers()
	{
		searchAllRulePositions();
		handleOverlapedInstances();
		
		resultTautomers.clear();
		if (ruleInstances.isEmpty())
		{	
			resultTautomers.add(molecule);
			return(resultTautomers);
		}	
		
		generateRuleCombinations();
		
		return(resultTautomers);
	}
	
	void searchAllRulePositions()
	{
		extendedRuleInstances.clear();
		ruleInstances.clear();
		
		for (int i = 0; i < knowledgeBase.rules.size(); i++)
		{	
			Vector<IRuleInstance> instances = knowledgeBase.rules.get(i).applyRule(molecule); 
			extendedRuleInstances.addAll(instances);
		}	
	}
	
	void handleOverlapedInstances()
	{
		//Currently no special treatment is performed		
		ruleInstances.addAll(extendedRuleInstances);
	}
	
	void generateRuleCombinations()
	{
		for (int i = 0; i < ruleInstances.size(); i++)
			ruleInstances.get(i).firstState();
						
		int n;
		int instNumber; 
		
		do 	{
			registerTautomer();
			
			n = ruleInstances.get(0).nextState();
			instNumber = 0;
			
			while(n == 0)
			{
				instNumber++;
				if (instNumber == ruleInstances.size())
					break;
				n = ruleInstances.get(instNumber).nextState();
			}
		} while (instNumber < ruleInstances.size()); 
	}
	
	void registerTautomer()
	{	
		System.out.println("  tautomer: " + getTautomerCombination());
	}
	
	
	String getTautomerCombination()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = ruleInstances.size()-1; i >= 0; i--)
			sb.append(""+ruleInstances.get(i).getCurrentState() + " ");
		return (sb.toString());
	}
	
}
