package ambit2.tautomers;

import java.util.Vector;
import java.util.List;


import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;

import ambit2.smarts.SmartsHelper;

public class RuleInstance implements IRuleInstance
{
	Rule rule;
	CombinedRuleInstance combRI;
	IAtomContainer molecule;
	int foundState = 0;
	int curState = 0;  //Current state is used to define the current position of the mobile group
	int beginState = 0;
	boolean FlagImplicitH = true;   //This flag is true when mobile h group is implicitly described
	IAtom explicitH = null;
	boolean FlagOverlapMode = false; //This is true when this instance overlaps with another one i.e. it is a part from combination
	boolean FlagGoToStateSpecialOK = true;
	
	Vector<IAtom> atoms = new  Vector<IAtom>();
	Vector<IBond> bonds = new  Vector<IBond>();
	
	
	public RuleInstance()
	{
	}
	
	public RuleInstance(RuleInstance prevRI)
	{
		rule = prevRI.rule;
		combRI = prevRI.combRI;
		molecule = prevRI.molecule;
		foundState = prevRI.foundState;
		curState = prevRI.curState; 
		beginState = prevRI.beginState;
		FlagImplicitH = prevRI.FlagImplicitH; 
		explicitH = prevRI.explicitH;
		//FlagOverlapMode = prevRI.FlagOverlapMode; 
		//FlagGoToStateSpecialOK = prevRI.FlagGoToStateSpecialOK;
	}
	
	public int firstState()
	{	
		gotoState(0);
		return(0);
	}
	
	public int nextState()
	{	
		int nextState = curState + 1;
		if (nextState == rule.nStates)
			nextState = 0;
		
		gotoState(nextState);
	
		return(nextState);
	}
	
	public int getCurrentState()
	{
		return(curState);
	}
	
	int gotoState(int state)
	{	
		if (curState == state)
			return(state); //It is already at this state
		
				
		//current state double bonds are made single
		RuleStateBondDistribution bondDistr = rule.stateBonds[curState];
		//double --> single
		for (int i = 0; i < bondDistr.DBPositions.length; i++)
		{
			int bpos = bondDistr.DBPositions[i];			
			IBond bond = bonds.get(bpos);
			bond.setOrder(Order.SINGLE);
		}
		//triple --> single
		for (int i = 0; i < bondDistr.TBPositions.length; i++)
		{
			int bpos = bondDistr.TBPositions[i];			
			IBond bond = bonds.get(bpos);
			bond.setOrder(Order.SINGLE);
		}
		
		
		//new state double bonds are set
		bondDistr = rule.stateBonds[state];
		//single --> double
		for (int i = 0; i < bondDistr.DBPositions.length; i++)
		{
			int bpos = bondDistr.DBPositions[i];			
			IBond bond = bonds.get(bpos);
			bond.setOrder(Order.DOUBLE);
		}
		//single --> triple
		for (int i = 0; i < bondDistr.TBPositions.length; i++)
		{
			int bpos = bondDistr.TBPositions[i];			
			IBond bond = bonds.get(bpos);
			bond.setOrder(Order.TRIPLE);
		}
		
		
		//mobile groups are moved
		for (int i = 0; i < rule.nMobileGroups; i++)
		{	
			if (rule.isMobileH[i] == true)
			{	
				int curHPos = rule.mobileGroupPos[i][curState];
				int newHPos = rule.mobileGroupPos[i][state];
				IAtom curAt = atoms.get(curHPos-1);
				IAtom newAt = atoms.get(newHPos-1);

				if (FlagImplicitH)
				{	
					curAt.setImplicitHydrogenCount(curAt.getImplicitHydrogenCount()-1);				
					newAt.setImplicitHydrogenCount(newAt.getImplicitHydrogenCount()+1);
				}
				else
				{	
					//handling explicit H atom
					IBond b = molecule.getBond(curAt,explicitH);
					b.setAtoms(new IAtom[]{newAt, explicitH});
				}
			}
			else
			{	
				//TODO handle other types of mobile groups 
			}	
		}
		
		
		//Handle ring closure (if present) for the new state
		if (bondDistr.hasRingClosure)
		{
			System.out.println("*** rc rule instance");
			if (curState == rule.ringClosureState)
			{
				System.out.println("**  ring --> chain");
				//The closure bond must exist in the molecule. 
				//And it is removed (ring --> chain)
				IAtom a0 = molecule.getAtom(rule.ringClosureBondFA);
				IAtom a1 = molecule.getAtom(rule.ringClosureBondSA);
				IBond b = molecule.getBond(a0, a1);
				if (b != null)
				{	
					bonds.remove(b); //Removing this bond from the bond list for this instance
					molecule.removeBond(b);
				}	
			}
			else 
			{
				System.out.println("**  chain --> ring");
				//System.out.println(SmartsHelper.moleculeToSMILES(molecule));
				
				//Molecule is in the chain state and the ring must be closed 
				//chain --> ring
				
				IAtom a0 = molecule.getAtom(rule.ringClosureBondFA);
				IAtom a1 = molecule.getAtom(rule.ringClosureBondSA);
				molecule.addBond(rule.ringClosureBondFA, rule.ringClosureBondSA, rule.ringClosureBondOrder);
				bonds.add(molecule.getBond(a0, a1)); //Adding new bond to the bonds list of this instance
				
				//System.out.println(SmartsHelper.moleculeToSMILES(molecule));
			}
		}
		
		
		curState = state;
		return(state);
	}
	
	public int getNumberOfStates()
	{
		return rule.nStates;
	}
	
	
	public int checkCurStateInstanceValidity()
	{
		//General idea for this check is the fact that
		//at a particular point this instance could be no longer valid since.
		//When other overlapping instances are shifted to another state 
		//the bonds in this instance may have changed as well
		
		
		boolean isSingleBond[] = new boolean[bonds.size()]; 
		for (int i = 0; i < isSingleBond.length; i++)
			isSingleBond[i] = true;
		
		RuleStateBondDistribution rsbd = rule.stateBonds[curState];
		
		//check of the double bonds
		for (int i = 0; i < rsbd.DBPositions.length; i++)
		{	
			int pos = rsbd.DBPositions[i];
			isSingleBond[pos] = false;
			if (bonds.get(pos).getOrder() != IBond.Order.DOUBLE)
				return -2;
		}
		
		
		
		//check of the triple bonds
		if (rsbd.TBPositions != null)
			for (int i = 0; i < rsbd.TBPositions.length; i++)
			{	
				int pos = rsbd.TBPositions[i];
				isSingleBond[pos] = false;
				if (bonds.get(pos).getOrder() != IBond.Order.TRIPLE)
					return -3;
			}
		
		//check of the single bonds 
		for (int i = 0; i < isSingleBond.length; i++)
			if (isSingleBond[i])
			{	
				if (bonds.get(i).getOrder() != IBond.Order.SINGLE)
					return -1;
			}	
		
		return 0; //The state is OK
	}
	
	
	
	
	
	/*
	
	//Utilities for generation of combination of states -----------------------------
	
	
	public int firstStateSpecial()
	{	
		gotoStateSpecial(0);
		return(0);
	}
	
	public int nextStateSpecial()
	{	
		int nextState = curState + 1;
		if (nextState == rule.nStates)
			nextState = 0;
		
		gotoStateSpecial(nextState);
	
		return(nextState);
	}
	
	
	int gotoStateSpecial(int state)
	{
		FlagGoToStateSpecialOK = true;
		
		if (curState == state)
			return(state); //It is already at this state
				
		int nMissingDB = 0;
		
		//current state double bonds are made single
		RuleStateBondDistribution bondDistr = rule.stateBonds[curState];
		for (int i = 0; i < bondDistr.DBPositions.length; i++)
		{
			int bpos = bondDistr.DBPositions[i];
			IBond bond = bonds.get(bpos);
			if (overlappedBonds.contains(bond))
			{
				if (bond.getOrder() == Order.DOUBLE)
					bond.setOrder(Order.SINGLE);
				else
					nMissingDB++;
			}
			else			
				bond.setOrder(Order.SINGLE);
		}
		
		
		
		return(state);
	}
	
	*/
	
	
	
	public String debugInfo(IAtomContainer mol)
	{
		StringBuffer sb = new StringBuffer();
		//sb.append(rule.name);
		sb.append(rule.smartsStates[curState]);
		sb.append(" at");
		for (int i = 0; i < atoms.size(); i++)
			sb.append(" "+mol.getAtomNumber(atoms.get(i)));
		return(sb.toString());
	}
	
	
	
	
}
