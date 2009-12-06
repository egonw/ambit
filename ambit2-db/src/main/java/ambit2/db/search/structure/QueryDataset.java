package ambit2.db.search.structure;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.SourceDataset;
import ambit2.db.search.QueryParam;
import ambit2.db.search.StringCondition;

/**
 * src_dataset.name = ? 
 * @author Nina Jeliazkova nina@acad.bg
 *
 */
public class QueryDataset extends AbstractStructureQuery<String,SourceDataset,StringCondition> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8329798753353233477L;
	public final static String sql = 
		"select ? as idquery,idchemical,idstructure,1 as selected,1 as metric,null as text from structure join struc_dataset using(idstructure) join src_dataset using (id_srcdataset) ";
	public final static String where = " where src_dataset.name %s ?";
	public QueryDataset(SourceDataset dataset) {
		setCondition(StringCondition.getInstance(StringCondition.C_EQ));
		setDataset(dataset);
	}
	public QueryDataset() {
		this((SourceDataset)null);
	}
	public QueryDataset(String name) {
		this(new SourceDataset(name));
	}	
	public String getSQL() throws AmbitException {
		if ((getValue()==null) || "".equals(getValue().getName()))
			return sql;
		else
			return String.format(sql+where,getCondition());
	}
	public SourceDataset getDataset() {
		return getValue();
		
	}
	public void setDataset(SourceDataset dataset) {
		setValue(dataset);
	}
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		if ((getValue()==null) || "".equals(getValue().getName()))
			;
		else
			params.add(new QueryParam<String>(String.class, getValue().getName()));
		return params;
	}
	@Override
	public String toString() {
		if (getValue()==null) return "Datasets";
		return String.format("Dataset %s %s",getCondition(),getValue().getName());
	}


}
