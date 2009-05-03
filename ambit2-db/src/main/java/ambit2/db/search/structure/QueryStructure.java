package ambit2.db.search.structure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.MFAnalyser;

import ambit2.base.data.StructureRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.processors.structure.AtomConfigurator;
import ambit2.core.processors.structure.key.SmilesKey;
import ambit2.db.processors.StructureNormalizer;
import ambit2.db.search.QueryParam;
import ambit2.db.search.StringCondition;

/**
 * Search for smiles, inchi, formula
 * @author Nina Jeliazkova nina@acad.bg
 *
 */
public class QueryStructure extends AbstractStructureQuery<String,String,StringCondition> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1446001922520148275L;
	protected SmilesKey smilesKey = new SmilesKey();
	protected AtomConfigurator configurator = new AtomConfigurator();
	
	public QueryStructure() {
		setCondition(StringCondition.getInstance("="));
	}

	
	public final static String sqlSMILES = 
		"select ? as idquery,idchemical,idstructure,1 as selected,1 as metric from structure join chemicals using(idchemical) where (%s %s ?) or (%s %s ?)";
	
	public String getSQL() throws AmbitException {
		return String.format(sqlSMILES,getFieldname(),getCondition().getSQL(),getFieldname(),getCondition().getSQL());
	}
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		params.add(new QueryParam<String>(String.class, getValue()));		
		//this is a hack; smiles generated by SmilesGenerator are not unique!
		params.add(new QueryParam<String>(String.class, normalizeValue()));
		return params;
	}
	protected String normalizeValue() {

		if ("smiles".equals(getFieldname())) try {
			SmilesParser p = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());
			IMolecule mol = p.parseSmiles(getValue());
			configurator.process(mol);
			String smiles = smilesKey.process(mol);
			if (smiles!=null && !"".equals(smiles)) return smiles;
		} catch (Exception x) {
			
		}
		else if ("formula".equals(getFieldname())) {
			MFAnalyser mfa = new MFAnalyser(getValue(),new Molecule());
			//this is to make the formula "canonical" (i.e. sorted in the right order)
			mfa = new MFAnalyser(mfa.getAtomContainer());
			String formula = mfa.getMolecularFormula();			
			if (formula!=null && !"".equals(formula)) return formula;
		}
		return getValue();

	}
	public IStructureRecord getObject(ResultSet rs) throws AmbitException {
		try {
			IStructureRecord record = new StructureRecord();
			record.setIdchemical(rs.getInt(2));
			record.setIdstructure(rs.getInt(3));
			return record;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}
	@Override
	public String toString() {
		if (getValue()==null) return "Search structure by SMILES, Inchi, Formula";
		return super.toString();
	}
}
