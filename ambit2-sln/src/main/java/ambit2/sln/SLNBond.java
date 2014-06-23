package ambit2.sln;


import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSBond;

public class SLNBond extends SMARTSBond
{
	private static final long serialVersionUID = 456218569809086068L;

	int bondType = 0;
	SLNBondExpression bondExpression = null;

	public boolean matches(IBond bond) 
	{
		//TODO
		return false;
	}

	public String getBTString()
	{
		switch(bondType)
		{
		case SLNConst.B_TYPE_ANY://any bond
			return "any";
		case SLNConst.B_TYPE_1:	//single bond
			return "single" ;
		case SLNConst.B_TYPE_2:	//double bond
			return "double";
		case SLNConst.B_TYPE_3:	//triple bond
			return "triple";
		case SLNConst.B_TYPE_aromatic:	//aromatic bond
			return "aromatic";

		default:		
			return "userDeff";
		}
	}

	public String toString()
	{
		if (bondExpression == null)	
			return (SLNConst.bondTypeAttributeToSLNString(bondType));
		else
			return(SLNConst.bondTypeAttributeToSLNString(bondType) + bondExpression.toString());
	}
}
