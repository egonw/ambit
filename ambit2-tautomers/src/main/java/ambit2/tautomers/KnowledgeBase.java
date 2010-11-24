package ambit2.tautomers;

import java.util.Vector;

public class KnowledgeBase 
{
	public Vector<Rule> rules = new Vector<Rule>();
	
	Vector<String> errors = new Vector<String>(); 
	RuleParser ruleParser = new RuleParser();
	
	KnowledgeBase()
	{
		loadPredefinedBase();
	}
	
	
	public void loadPredefinedBase()
	{
		errors.clear();
		for (int i = 0; i < PredefinedKnowledgeBase.rules.length; i++)
			addRule(PredefinedKnowledgeBase.rules[i]);
		
		if (errors.size() > 0)
		{
			System.out.println("There are errors in the knowledge base:");
			for (int i = 0; i < errors.size(); i++)
				System.out.println("Rule " + (i+1) + ":  " + errors.get(i));
		}
	}
	
	
	public void addRule(String newRule)
	{	
		Rule rule = ruleParser.parse(newRule);
		if (rule == null)
			errors.add(ruleParser.errors);
		else
		{
			rules.add(rule);
			//System.out.println(rule.toString());
		}
	}
	
	public String getAllErrors()
	{
		//TODO
		return("");
	}
	
}

