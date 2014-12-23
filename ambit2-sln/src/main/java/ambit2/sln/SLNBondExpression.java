package ambit2.sln;

import java.util.ArrayList;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IBond;

public class SLNBondExpression 
{

	public ArrayList<SLNExpressionToken> tokens = new ArrayList<SLNExpressionToken>(); 

	public boolean matches(IBond bond) 
	{
		SLNLogicalExpression sle = new SLNLogicalExpression();
		for (int i = 0; i< tokens.size(); i++)
		{
			SLNExpressionToken tok = tokens.get(i);
			if (tok.type < SLNConst.LO)
			{	
				sle.addArgument(getArgument(tok, bond));
			}	
			else
				sle.addLogOperation(tok.type - SLNConst.LO);
		}
		return (sle.getValue()); 
	}

	boolean getArgument(SLNExpressionToken tok, IBond bond)
    {	
		switch (tok.type)
		{			
		case SLNConst.B_TYPE_1:
			if (bond.getOrder() == IBond.Order.SINGLE)
				return(true);   
			
		case SLNConst.B_TYPE_2:
			if (bond.getOrder() == IBond.Order.DOUBLE)
				return(true);   
			
		case SLNConst.B_TYPE_3:
			if (bond.getOrder() == IBond.Order.TRIPLE)
				return(true);   

		case SLNConst.B_TYPE_aromatic:
			if (bond.getFlag(CDKConstants.ISAROMATIC));
				return(true);
				
		case SLNConst.B_STEREO_C:
			//TODO
    	}
    	return true; //by default
    }
	public String toString() 
	{
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (int i = 0; i < tokens.size(); i++)
			sb.append(tokens.get(i).toString(false));
		sb.append("]");
		return sb.toString();	
	}

	public boolean isIdenticalTo(SLNBondExpression bondExpression)
	{
		// TODO 
		return true;
	}
}
