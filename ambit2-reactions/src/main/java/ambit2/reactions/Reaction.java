package ambit2.reactions;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.rules.json.JSONParsingUtils;
import ambit2.smarts.SMIRKSManager;
import ambit2.smarts.SMIRKSReaction;
import ambit2.smarts.SmartsConst;
import ambit2.smarts.SmartsParser;

public class Reaction 
{
	protected boolean FlagUse = true;
	protected String smirks = null;
	protected int id = 0;
	protected String name = null;
	protected String reactionClass = null;
	protected SMIRKSReaction smirksReaction = null;
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isFlagUse() {
		return FlagUse;
	}

	public void setFlagUse(boolean flagUse) {
		FlagUse = flagUse;
	}
	
	public SMIRKSReaction getSmirksReaction() {
		return smirksReaction;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSmirks() {
		return smirks;
	}

	public void setSmirks(String smirks) {
		this.smirks = smirks;
	}
	
	public String getReactionClass() {
		return reactionClass;
	}

	public void setReactionClass(String reactionClass) {
		this.reactionClass = reactionClass;
	}
	
	public void configure(SMIRKSManager smrkMan) throws Exception
	{
		smirksReaction = smrkMan.parse(smirks);
		if (!smrkMan.getErrors().equals(""))		
			throw (new Exception ("There are errors in SMIRKS: " + smirks + "  " + smrkMan.getErrors()));
	}
	
	public static Reaction getReactionFromJsonNode(JsonNode node) throws Exception
	{
		Reaction reaction = new Reaction();
		
		Boolean use = JSONParsingUtils.extractBooleanKeyword(node, "USE", false);
		if (use != null)
			reaction.setFlagUse(use);
		
		reaction.name = JSONParsingUtils.extractStringKeyword(node, "NAME", true);
		reaction.smirks = JSONParsingUtils.extractStringKeyword(node, "SMIRKS", true);
		reaction.reactionClass = JSONParsingUtils.extractStringKeyword(node, "CLASS", false);
		
		return reaction;
	}
	
	public List<List<IAtom>> findReactionInstances(IAtomContainer target, SMIRKSManager smrkMan)
	{
		return findReactionInstances(target, smrkMan, SmartsConst.SSM_NON_OVERLAPPING);
	}
	
	
	public List<List<IAtom>> findReactionInstances(IAtomContainer target, SMIRKSManager smrkMan, int SSMode)
	{	
		SmartsParser.prepareTargetForSMARTSSearch(smirksReaction.reactantFlags, target);
		if (smirksReaction.reactantFlags.hasRecursiveSmarts)
			smrkMan.mapRecursiveAtomsAgainstTarget(smirksReaction.reactantRecursiveAtoms, target);
		
		// It is absolutely needed that setQuery() function is called after
		// recursive atom mapping
		// because the recursive mapping calls setQuery() as well
		smrkMan.getIsomorphismTester().setQuery(smirksReaction.reactant);
		
		switch (SSMode)
		{
		case SmartsConst.SSM_NON_OVERLAPPING:
			return smrkMan.getNonOverlappingMappings(target);
		
		case SmartsConst.SSM_NON_IDENTICAL:
			return smrkMan.getNonIdenticalMappings(target);
		
		//TODO
			
		default:
			return null;
		}
		
	}
	
	public IAtomContainer applyAtInstance(IAtomContainer target, List<IAtom> instance, SMIRKSManager smrkMan) throws Exception
	{
		return applyAtInstance(target, instance, smrkMan, true);
	}
	
	public IAtomContainer applyAtInstance(IAtomContainer target, 
											List<IAtom> instance, 
											SMIRKSManager smrkMan, 
											boolean FlagClone) throws Exception
	{
		List<List<IAtom>> instances = new ArrayList<List<IAtom>>();
		instances.add(instance);
		return applyAtInstancesSimultaneously(target, instances, smrkMan, FlagClone);
	}
	
	public IAtomContainer applyAtInstancesSimultaneously(IAtomContainer target, 
											List<List<IAtom>> instances, 
											SMIRKSManager smrkMan, 
											boolean FlagClone)  throws Exception
	{
		
		IAtomContainer product = smrkMan.applyTransformationsAtLocationsWithCloning(target, instances, smirksReaction);
		//TODO handle non-clone case
		return product;
	}
	
}
