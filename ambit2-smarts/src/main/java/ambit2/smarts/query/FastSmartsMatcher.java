package ambit2.smarts.query;

import java.util.Vector;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;

import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SmartsParser;
import ambit2.smarts.processors.SMARTSPropertiesReader;

public class FastSmartsMatcher extends AbstractSmartsPattern<IAtomContainer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -437122688052917294L;
	protected IsomorphismTester isoTester = new IsomorphismTester();
	protected SmartsParser sp = new SmartsParser();
	protected QueryAtomContainer query = null;
	protected SMARTSPropertiesReader reader = new SMARTSPropertiesReader();
	
	public FastSmartsMatcher() {

	}
	public FastSmartsMatcher(String smarts,boolean negate) throws SMARTSException {
		setSmarts(smarts);
		setNegate(negate);
	}
	public QueryAtomContainer getQuery() {
		return query;
	}

	public void setQuery(QueryAtomContainer query) {
		this.query = query;
	}

	public IAtomContainer getMatchingStructure(IAtomContainer mol)
			throws SMARTSException {
		if (query == null) return null;
		isoTester.setQuery(query);
		Vector<IAtom> index = isoTester.getIsomorphismMapping(mol);
		if (index ==null) return null;
		IAtomContainer match = NoNotificationChemObjectBuilder.getInstance().newAtomContainer();
		
		for (IAtom i: index) match.addAtom(i);
		
		for (int b=0; b < mol.getBondCount();b++) {
			
			IBond bond = mol.getBond(b);
			int count = 0;
			for (int a=0; a < bond.getAtomCount();a++)
				if (index.indexOf(bond.getAtom(a))>=0) count++;
				else break;
			if (count == bond.getAtomCount()) 
				match.addBond(bond);
		}
		return match;
	}

	public IAtomContainer getObjectToVerify(IAtomContainer mol) {
		return mol;
	}
	@Override
	public void setSmarts(String smarts) throws SMARTSException {
		query  = sp.parse(smarts);
		sp.setNeededDataFlags();
		String errorMsg = sp.getErrorMessages();
		if (!errorMsg.equals("")) {
			query = null;
			super.setSmarts(null);
			throw new SMARTSException("Smarts Parser errors:\n" + errorMsg);			
		} else super.setSmarts(smarts);						 
	}

	public int hasSMARTSPattern(IAtomContainer mol) throws SMARTSException {
		isoTester.setQuery(query);
		try {
			if (reader.process((IAtomContainer)mol) == null) { // sets properties from CMLUtilities.SMARTSProp ! 
				sp.setSMARTSData(mol); 
			}
		} catch (Exception x) {
			sp.setSMARTSData(mol);
		}
		return isoTester.hasIsomorphism(mol)?1:0;
	}

	public void useMOEvPrimitive(boolean flag)
			throws UnsupportedOperationException {

		throw new UnsupportedOperationException("useMOEvPrimitive");
		
	}

}
