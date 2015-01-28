package ambit2.db.search.structure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;

import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.processors.structure.AtomConfigurator;
import ambit2.core.processors.structure.key.ExactStructureSearchMode;
import ambit2.core.processors.structure.key.SmilesKey;
import ambit2.db.search.StringCondition;

/**
 * Search for smiles, inchi, formula
 * @author Nina Jeliazkova nina@acad.bg
 *
 */
public class QueryStructure extends AbstractStructureQuery<ExactStructureSearchMode,String,StringCondition> {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1446001922520148275L;
	protected SmilesKey smilesKey = new SmilesKey();
	protected AtomConfigurator configurator = new AtomConfigurator();
	
	public QueryStructure() {
		setChemicalsOnly(false);
		setCondition(StringCondition.getInstance("="));
	}

	protected final static String sql_chemical = "select ? as idquery,idchemical,-1 as idstructure,0 as selected,1 as metric,inchi as text from chemicals where (%s %s ?) %s";
	//protected final static String sql_structure = "select ? as idquery,idchemical,idstructure,if(type_structure='NA',0,1) as selected,preference as metric,inchi as text from chemicals join structure using(idchemical) where (%s %s ?)";
	protected final static  String sql_prefered = 
		"select ? as idquery,idchemical,idstructure,if(type_structure='NA',0,1) as selected,preference as metric,null as text\n"+
		"from structure where idchemical = "+
		"(select idchemical from chemicals where (%s %s ?) %s limit 1)\n"+
		"order by idchemical,preference,idstructure";
	
	public String getSQL() throws AmbitException {
		String sql = isChemicalsOnly()?sql_chemical:sql_prefered;
		switch (getFieldname()) {
		case smiles: {
			return String.format(sql,getFieldname(),getCondition(),String.format(" or (%s %s ?) ",getFieldname(),getCondition()));
		}
		default:
			return String.format(sql,getFieldname(),getCondition(),"");
		}
	}
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		params.add(new QueryParam<String>(String.class, getValue()));		
		//this is a hack; smiles generated by SmilesGenerator are not unique!
		switch (getFieldname()) {
		case smiles: {
			try {
				params.add(new QueryParam<String>(String.class, normalizeValue()));
			} catch (Exception x) {
				params.add(new QueryParam<String>(String.class, getValue()));
			}
		}
		}
		return params;
	}
	protected String normalizeValue() throws AmbitException, InvalidSmilesException {
		switch (getFieldname()) {
		case smiles: {
			SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());
			IMolecule mol = p.parseSmiles(getValue());
			configurator.process(mol);
			String smiles = smilesKey.process(mol);
			if (smiles!=null && !"".equals(smiles)) return smiles;
			else return getValue();
		}
		case formula: {
			
			IMolecularFormula formula = MolecularFormulaManipulator.getMolecularFormula(getValue(),SilentChemObjectBuilder.getInstance());

			//this is to make the formula "canonical" (i.e. sorted in the right order)

			if (formula!=null) return MolecularFormulaManipulator.getString(formula);
			else return getValue();
		}
		default: 
			return getValue();
		}

	}
	@Override
	public IStructureRecord getObject(ResultSet rs) throws AmbitException {
		try {
			IStructureRecord record = new StructureRecord();
			
			record.setIdchemical(rs.getInt(2));
			record.setIdstructure(rs.getInt(3));
			retrieveStrucType(record, rs);
			record.setInchi(rs.getString(6));
			//metric
			retrieveMetric(record, rs);
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
