package ambit2.tautomers;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

public class CACTVSRanking 
{
	public static final double score2eV = 0.05;
	
	/**
	 * 
	 * @param mol
	 * @return tautomer score as defined in J. Comput. Aided. Mol. Des. (2010) 24:521-551 
	 * (see table2 at page 530) 
	 * additional simpler score table from J. Chem. Inf. Mod. Vol.46, No.6, 2006 (page 2349 table 2)  
	 */
	public static double calcScoreRank(IAtomContainer mol)
	{
		int nCarboxAromRing = 0;
		int nAromAt = 0;
		//int nBenzQuin = 0;
		int nOximGroup = 0;
		int nC2O = 0;
		int nN2O = 0;
		int nP2O = 0;
		int nC2X = 0; //non aromatic bond
		int nCH3 = 0;
		//int nGuanidine = 0; + guanidine group with endocyclic double bond
		int nYH = 0; //P-H, S-H, Se-H and Te-H
		int nAciNitro = 0;
		
		for (IAtom atom : mol.atoms())
		{
			//Check aromatic
			if (atom.getFlag(CDKConstants.ISAROMATIC))
			{
				nAromAt++;
				if (checkCarboxylicNeighbour(atom, mol))
					nCarboxAromRing++;
				continue;
			}
			
			
			if (checkMethylGroup(atom, mol))
			{	
				nCH3++;
			}
				
			//Check oxim group (C=N[OH]) and aci-nitro group (C=N(=O)[OH])
			int res = checkOximAciNitro(atom, mol);
			if (res == 1)
			{
				nOximGroup++;
				continue;
			}
			else
				if (res == 2)
				{
					nAciNitro++;
					continue;
				}
						
			//Check Y-H (P-H, S-H, Se-H or Te-H)
			if (checkPSSeTeH(atom, mol))
				nYH++;
		}
		
		for (IBond bond : mol.bonds())
		{
			if (bond.getOrder() == IBond.Order.DOUBLE)
			{	
				IAtom at0 = bond.getAtom(0);
				IAtom at1 = bond.getAtom(1);
				
				//bonds C2O, N2O, P2O
				if (at0.getSymbol().equals("O"))
				{
					if (at1.getSymbol().equals("C"))
						nC2O++;
					else
						if (at1.getSymbol().equals("N"))
							nN2O++;
						else
							if (at1.getSymbol().equals("P"))
								nP2O++;
								
				}
				else
					if (at1.getSymbol().equals("O"))
					{
						if (at0.getSymbol().equals("C"))
							nC2O++;
						else
							if (at0.getSymbol().equals("N"))
								nN2O++;
							else
								if (at0.getSymbol().equals("P"))
									nP2O++;
					}
				
				//Non aromatic double bond C=X
				if (!at0.getFlag(CDKConstants.ISAROMATIC) && !at1.getFlag(CDKConstants.ISAROMATIC))
				{
					if (at0.getSymbol().equals("C"))
					{
						if (!at1.getSymbol().equals("C") && !at1.getSymbol().equals("H"))
							nC2X++;
					}
					else
						if (at1.getSymbol().equals("C"))
						{
							if (!at0.getSymbol().equals("C") && !at0.getSymbol().equals("H"))
								nC2X++;
						}
				}
			}		
		}
		
		
		double rank = nAromAt*(100.0/6) + nOximGroup * 4 + (nC2O + nN2O + nP2O)*2 + nC2X + nCH3 - nYH - 4*nAciNitro;
		return rank;
	}
	
	public static double getEnergyRank(IAtomContainer mol)
	{
		return score2eV * calcScoreRank(mol);
	}
	
	public static boolean checkCarboxylicNeighbour(IAtom at, IAtomContainer mol)
	{
		//TODO
		return false;
	}
	
	public static boolean checkMethylGroup(IAtom at, IAtomContainer mol)
	{
		//TODO
		return false;
	}
	
	public static int checkOximAciNitro(IAtom at, IAtomContainer mol)
	{
		//TODO
		return 0;
	}
	
	public static boolean checkPSSeTeH(IAtom at, IAtomContainer mol)
	{
		//TODO
		return false;
	}
	
}
