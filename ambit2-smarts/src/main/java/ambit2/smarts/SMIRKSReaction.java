package ambit2.smarts;

import java.util.Vector;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;



public class SMIRKSReaction 
{	
	public String reactantsSmarts, agentsSmarts, productsSmarts;
	public SmartsFlags reactantFlags = new SmartsFlags();
	public SmartsFlags agentFlags = new SmartsFlags();
	public SmartsFlags productFlags = new SmartsFlags();
	
	//Single container representation 
	public IQueryAtomContainer reactant = new QueryAtomContainer();
	public IQueryAtomContainer agent = new QueryAtomContainer();
	public IQueryAtomContainer product = new QueryAtomContainer();
	
		
	
	//Multi-container representation
	public Vector<IQueryAtomContainer> reactants = new Vector<IQueryAtomContainer>();
	public Vector<IQueryAtomContainer> agents = new Vector<IQueryAtomContainer>();
	public Vector<IQueryAtomContainer> products = new Vector<IQueryAtomContainer>();
	
	public Vector<Integer> reactantCLG = new Vector<Integer>();
	public Vector<Integer> agentsCLG = new Vector<Integer>();
	public Vector<Integer> productsCLG = new Vector<Integer>();
	
		
	public  Vector<String> mapErrors = new Vector<String>(); 
	//HashMap<Integer, Integer> mapping = new HashMap<Integer, Integer>();
	
	//Mapping data
	//Atom numbers are described in two ways
	//1.<Global atom numbers> i.e. regardless of the reactant/product fragmentation
	//2.<Frag Num>  + <Local atom Num> 
	
	Vector<Integer> reactantMapIndex = new Vector<Integer>();  //Container with the mapping indexes as they appeared in the SMIRKS
	Vector<Integer> reactantFragAtomNum = new Vector<Integer>(); //local atom fragment number
	Vector<Integer> reactantAtomNum = new Vector<Integer>(); //global atom number
	Vector<Integer> reactantFragmentNum = new Vector<Integer>();
	Vector<Integer> reactantNotMappedAt = new Vector<Integer>(); //The global numbers of not-mapped atoms
	
	Vector<Integer> productMapIndex = new Vector<Integer>();
	Vector<Integer> productFragAtomNum = new Vector<Integer>(); //local atom fragment number
	Vector<Integer> productAtomNum = new Vector<Integer>(); //global atom number
	Vector<Integer> productFragmentNum = new Vector<Integer>();
	Vector<Integer> productNotMappedAt = new Vector<Integer>(); //The global numbers of not-mapped atoms
	
		
	//Transformation Data is described in terms of global numbers	
	//Atom transformation is designated for the mapped atoms
	Vector<Integer> reactantAtCharge = new Vector<Integer>();
	Vector<Integer> productAtCharge = new Vector<Integer>();
	//Vector<Integer> reactantAtChirality
	//Vector<Integer> productAtChirality	
	
	//Bond transformation
	Vector<Integer> reactAt1 = new Vector<Integer>();
	Vector<Integer> reactAt2 = new Vector<Integer>();	
	Vector<IBond.Order> reactBo = new Vector<IBond.Order>();
	Vector<Integer> prodAt1 = new Vector<Integer>();
	Vector<Integer> prodAt2 = new Vector<Integer>();
	Vector<IBond.Order> prodBo = new Vector<IBond.Order>();
	
	protected IChemObjectBuilder builder;
	
	public SMIRKSReaction(IChemObjectBuilder builder) {
		super();
		this.builder = builder;
	}
				
	public void checkMappings()
	{
		//Register all mappings
		for (int i = 0; i < reactants.size(); i++)
			registerMappings("Reactant", reactant, reactants.get(i), i,  reactantMapIndex, reactantNotMappedAt, 
					reactantFragAtomNum, reactantAtomNum, reactantFragmentNum);
		
		for (int i = 0; i < products.size(); i++)
			registerMappings("Product", product, products.get(i), i, productMapIndex, productNotMappedAt,  
					productFragAtomNum, productAtomNum, productFragmentNum);
						
		
		//Check mapping index correctness
		for (int i = 0; i < reactantMapIndex.size(); i++)
		{
			int index = getIntegerObjectIndex(productMapIndex, reactantMapIndex.get(i));
			if (index == -1)
				mapErrors.add("Reactant Map Index " + reactantMapIndex.get(i).intValue()+ 
						" is not valid product map index!");
			
			//This should take into account the fragment number as well
			//else
			//	mapping.put(reactantAtomNum.get(i), productAtomNum.get(index));  
			//
		}
		
		for (int i = 0; i < productMapIndex.size(); i++)
		{
			int index = getIntegerObjectIndex(reactantMapIndex, productMapIndex.get(i));
			if (index == -1)
				mapErrors.add("Product Map Index " + productMapIndex.get(i).intValue()+ 
						" is not valid reactant map index!");
		}
		
		
		if (!mapErrors.isEmpty())
		{	
			//At least one mapping error is found
			//The errors may cause exceptions in the following code hence the checking is stopped    
			return;
		}
			
		SmartsToChemObject stco = new SmartsToChemObject(builder);
		
		//Checking for atom typing and some properties correctness for all mapped atoms
		
		//reactantFragAtomNum, reactantFragmentNum  and  reactantAtomNum  have the same size
		//The first two represent one way to number (access the mapped atoms)
		//The third container represents another way (global atom numbers) currently utilized 
		
		for (int i = 0; i < reactantFragAtomNum.size(); i++)       
		{
			int rAtNum = reactantFragAtomNum.get(i).intValue();
			int rNum = reactantFragmentNum.get(i).intValue();
			Integer rMapInd = reactantMapIndex.get(i); 
			IAtom ra = reactants.get(rNum).getAtom(rAtNum);
			int rGlobAtNum = reactantAtomNum.get(i).intValue();
			IAtom glob_ra = reactant.getAtom(rGlobAtNum);
			
			int pIndex = getIntegerObjectIndex(productMapIndex, rMapInd);
			int pAtNum = productFragAtomNum.get(pIndex).intValue();
			int pNum = productFragmentNum.get(pIndex).intValue();
			IAtom pa = products.get(pNum).getAtom(pAtNum);
			int pGlobAtNum = productAtomNum.get(pIndex).intValue();
			IAtom glob_pa = product.getAtom(pGlobAtNum);
			
			if (glob_ra != ra)
				mapErrors.add("Critical Error: Inconsistency between global and fragment atom treatment.");
			
			if (glob_pa != pa)
				mapErrors.add("Critical Error: Inconsistency between global and fragment atom treatment.");
			
			//System.out.println("Map #" + rMapInd.intValue() + "  P" + rNum + " A"+rAtNum + "  -->  R"+pNum+" A"+pAtNum+"  "
			//		+ "     " + rGlobAtNum + " --> " + pGlobAtNum);
			
			IAtom ra1 = stco.toAtom(ra);
			IAtom pa1 = stco.toAtom(pa);
			if (ra1 != null)
			{
				if (pa1 == null)
					mapErrors.add("Map " + rMapInd.intValue() + " atom types are inconsistent!");
				else
				{
					if (!ra1.getSymbol().equals(pa1.getSymbol()))
						mapErrors.add("Map " + rMapInd.intValue() + " atom types are inconsistent!");
				}
			}
			else
			{
				if (pa1 != null)
					mapErrors.add("Map " + rMapInd.intValue() + " atom types are inconsistent!");
			}
			
		}
		
		//Check for mapping correctness on CLG
		//TODO - not clear if it is needed for now	
	}
	
	
	void registerMappings(String compType, IQueryAtomContainer globalContainer, IQueryAtomContainer fragment, 
			int curFragNum, Vector<Integer> mapIndex, Vector<Integer> notMappedAt, 
			Vector<Integer> atFragNum, Vector<Integer> atGlobalNum, Vector<Integer> fragNum)
	{
		for (int i = 0; i < fragment.getAtomCount(); i++)
		{
			IAtom a = fragment.getAtom(i);
			Integer iObj = (Integer)a.getProperty("SmirksMapIndex");
			if (iObj != null)
			{
				if (containsInteger(mapIndex, iObj))
					mapErrors.add(compType + " Map Index " + iObj.intValue()+ " is repeated!");
				else
				{
					mapIndex.add(iObj);
					atFragNum.add(new Integer(i));
					fragNum.add(new Integer(curFragNum));
					int globalAtNum = globalContainer.getAtomNumber(a);
					atGlobalNum.add(new Integer(globalAtNum));
					//System.out.println("*** " + compType + "   Gl# " + globalAtNum + "  Map:"+iObj.intValue());
				}
			}
			else
			{	
				//This is unmapped atom
				int globalAtNum = globalContainer.getAtomNumber(a);
				notMappedAt.add(new Integer(globalAtNum));
			}
		}
	}
	
	
	void generateTransformationData()
	{
		SmartsToChemObject stco = new SmartsToChemObject(builder);
		
		//Atom Transformation. Different atom properties are handles
		generateChargeTransformation();
		
		//TODO - other atom properties transformation
		
		
		//Bond Transformation
		for (int i = 0; i < reactant.getBondCount(); i++)
		{	
			IBond rb = reactant.getBond(i);
			IBond rb0 = stco.toBond(rb);
			
			IAtom ra1 = rb.getAtom(0);
			IAtom ra2 = rb.getAtom(1);
			Integer raMapInd1 = (Integer)ra1.getProperty("SmirksMapIndex");
			Integer raMapInd2 = (Integer)ra2.getProperty("SmirksMapIndex");
			
			if (raMapInd1 == null)
			{
				int rAt1Num = -reactant.getAtomNumber(ra1);  
				if (raMapInd2 == null)
				{
					//int rAt2Num = -reactant.getAtomNumber(ra2);
					//This bond is not registered as a transformation
				}
				else
				{	
					//TODO
				}
			}
			else
			{
				if (raMapInd2 == null)
				{
					//TODO
				}
				else
				{
					int pAt1Num = getMappedProductAtom(raMapInd1);
					int pAt2Num = getMappedProductAtom(raMapInd2);
					int rAt1Num = reactant.getAtomNumber(ra1);
					int rAt2Num = reactant.getAtomNumber(ra2);
					int pbNum = product.getBondNumber(product.getAtom(pAt1Num), product.getAtom(pAt2Num));
					
					
					boolean FlagRegisterTransform = false;
					if (pbNum == -1)
					{	
						prodBo.add(null);
						FlagRegisterTransform = true;
					}	
					else
					{	
						IBond pb = product.getBond(pbNum);
						IBond pb0 = stco.toBond(pb);
						
						if (rb0 == null)
						{	
							if (pb0 == null)
							{	
							}
							else
							{
								//Handle an error!
								//TODO
							}
						}
						else
						{
							if (pb0 == null)
							{	
								//Handle an error!
								//TODO
							}
							else
							{	
							}
						}
						
						
						
						if (rb0.getOrder() != pb0.getOrder())
						{
							FlagRegisterTransform = true;
							prodBo.add(pb0.getOrder());
						}	
					}	
					
					if (FlagRegisterTransform)
					{	
						prodAt1.add(new Integer(pAt1Num));
						prodAt2.add(new Integer(pAt2Num));
						reactBo.add(rb0.getOrder());
						reactAt1.add(new Integer(rAt1Num));
						reactAt2.add(new Integer(rAt2Num));
					}
				}
			}	
				
		}
		
		
		//Check for bonds that must be created in product (these do not exist in the reactant)
		for (int i = 0; i < product.getBondCount(); i++)
		{
			IBond pb = product.getBond(i);
			IBond pb0 = stco.toBond(pb);
			if (pb0 == null)
			{
				//Handle error
				//TODO
			}
			IAtom pa1 = pb.getAtom(0);
			IAtom pa2 = pb.getAtom(1);
			int pAt1Num = product.getAtomNumber(pa1);
			int pAt2Num = product.getAtomNumber(pa2);
			Integer paMapInd1 = (Integer)pa1.getProperty("SmirksMapIndex");
			Integer paMapInd2 = (Integer)pa2.getProperty("SmirksMapIndex");
			
			if (paMapInd1 == null)
			{
				if (paMapInd2 == null)
				{	
					//This is a bond between two unmapped atoms in the product
					prodBo.add(pb0.getOrder());
					prodAt1.add(new Integer(pAt1Num));
					prodAt2.add(new Integer(pAt2Num));
					reactBo.add(null);
					reactAt1.add(new Integer(SmartsConst.SMRK_UNSPEC_ATOM));
					reactAt2.add(new Integer(SmartsConst.SMRK_UNSPEC_ATOM));
					
				}
				else
				{
					//This is a bond between unmapped atom and mapped atom in the product
					//at1 is unmapped, at2 is mapped
					prodBo.add(pb0.getOrder());
					prodAt1.add(new Integer(pAt1Num));
					prodAt2.add(new Integer(pAt2Num));
					reactBo.add(null);
					reactAt1.add(new Integer(SmartsConst.SMRK_UNSPEC_ATOM));
					int rAt2Num = getMappedReactantAtom(paMapInd2);
					reactAt2.add(new Integer(rAt2Num));	
				}
			}
			else
			{
				if (paMapInd2 == null)
				{
					//This is a bond between unmapped atom and mapped atom in the product
					//at2 is unmapped, at1 is mapped
					prodBo.add(pb0.getOrder());
					prodAt1.add(new Integer(pAt1Num));
					prodAt2.add(new Integer(pAt2Num));
					reactBo.add(null);
					int rAt1Num = getMappedReactantAtom(paMapInd1);
					reactAt1.add(new Integer(rAt1Num));
					reactAt2.add(new Integer(SmartsConst.SMRK_UNSPEC_ATOM));
				}
				else
				{
					//This is a bond between two mapped atoms in the product which is not present in the reactant
					int rAt1Num = getMappedReactantAtom(paMapInd1);
					int rAt2Num = getMappedReactantAtom(paMapInd2);
					int rbNum = reactant.getBondNumber(reactant.getAtom(rAt1Num), reactant.getAtom(rAt2Num));
					if (rbNum == -1)
					{	
						prodBo.add(pb0.getOrder());
						prodAt1.add(new Integer(pAt1Num));
						prodAt2.add(new Integer(pAt2Num));
						reactBo.add(null);
						reactAt1.add(new Integer(rAt1Num));
						reactAt2.add(new Integer(rAt2Num));
					}
					
				} 
			}
			
		}
		
	}
	
	
	void generateChargeTransformation()
	{
		SmartsToChemObject stco = new SmartsToChemObject(builder);
		
		for (int i = 0; i < reactant.getAtomCount(); i++)
		{
			IAtom a = stco.toAtom(reactant.getAtom(i));
			if (a == null)
				reactantAtCharge.add(null);
			else
				reactantAtCharge.add(a.getFormalCharge());
		}
		
		
		for (int i = 0; i < product.getAtomCount(); i++)
		{
			IAtom a = stco.toAtom(product.getAtom(i));
			if (a == null)
				productAtCharge.add(null);
			else
				productAtCharge.add(a.getFormalCharge());
		}
	}
	
	
	//Helper functions
	
	int getMappedProductAtom(Integer raMapInd)
	{	
		int pIndex = getIntegerObjectIndex(productMapIndex, raMapInd);
		int pGlobAtNum = productAtomNum.get(pIndex).intValue();
		return pGlobAtNum;
	}
	
	
	int getMappedReactantAtom(Integer paMapInd)
	{	
		int rIndex = getIntegerObjectIndex(reactantMapIndex, paMapInd);
		int rGlobAtNum = reactantAtomNum.get(rIndex).intValue();
		return rGlobAtNum;
	}
	
	
	//so far not used
	int getTransformationReactantBondIndex(int nBondsToCheck, int rAt1, int rAt2)
	{
		for (int i = 0; i < nBondsToCheck; i++)
		{
			if ((reactAt1.get(i).intValue() == rAt1) && (reactAt2.get(i).intValue() == rAt2))
				return (i);
			
			if ((reactAt1.get(i).intValue() == rAt2) && (reactAt2.get(i).intValue() == rAt1))
				return (i);	
		}
		
		return(-1);
	}
	
	
	boolean containsInteger(Vector<Integer> v, Integer iObj)
	{
		for (int i = 0; i < v.size(); i++)
			if (iObj.intValue() == v.get(i).intValue())
				return(true);
		
		return false;
	}
	
	int getIntegerObjectIndex(Vector<Integer> v, Integer iObj)
	{
		for (int i = 0; i < v.size(); i++)
		{	
			Integer vi = v.get(i);
			if (iObj.intValue() == vi.intValue())
				return(i);
		}
		return -1;
	}
	
	
	public String transformationDataToString()
	{
		StringBuffer sb = new StringBuffer();
		
		System.out.println("Mappings:");
		for (int i = 0; i < reactantMapIndex.size(); i++)
		{	
			Integer rMapInd = reactantMapIndex.get(i);
			int rGlobAtNum = reactantAtomNum.get(i).intValue();
			IAtom glob_ra = reactant.getAtom(rGlobAtNum);
			int pIndex = getIntegerObjectIndex(productMapIndex, rMapInd);			
			int pGlobAtNum = productAtomNum.get(pIndex).intValue();
			IAtom glob_pa = product.getAtom(pGlobAtNum);
			
			System.out.println("Map #" + rMapInd.intValue()+ 
					//"  P" + rNum + " A"+rAtNum + "  -->  R"+pNum+" A"+pAtNum+"  " +
				 "     " + 
				 "at# " + rGlobAtNum + 	
				 "  Charge = " +  reactantAtCharge.get(rGlobAtNum) +
				 "   -->  " + 
				 "at# " + pGlobAtNum +
				 "  Charge = " +  productAtCharge.get(pGlobAtNum) +				  
				 "         pIndex = " + pIndex
				 );
		}
		
		
		for (int i = 0; i <  prodBo.size(); i++)
		{
			String bo;
			sb.append("BondTransform: (");
			sb.append(reactAt1.get(i).intValue() + ", ");
			sb.append(reactAt2.get(i).intValue() + ", ");
			if (reactBo.get(i) == null)
				bo = "null";
			else	
				bo = reactBo.get(i).toString();
			
			sb.append( bo + ")  -->  (");
			sb.append(prodAt1.get(i).intValue() + ", ");
			sb.append(prodAt2.get(i).intValue() + ", ");
			if (prodBo.get(i) == null)
				bo = "null";
			else	
				bo = prodBo.get(i).toString();
			sb.append(bo.toString() + ")" );
			sb.append("\n");
		}
		return(sb.toString());
		
	}
	
}
