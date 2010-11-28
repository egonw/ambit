package ambit2.db.search.structure.pairwise;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.QueryParam;

public class QueryStructurePairsRDataset extends	QueryStructurePairs<IStoredQuery, IStoredQuery> {

	protected final String sql =
		"SELECT\n"+
		"if(s1.idstructure <s2.idstructure,s1.idstructure,s2.idstructure) id1,\n"+
		"if(s1.idstructure >s2.idstructure,s1.idstructure,s2.idstructure) id2\n"+
		"FROM query_results s1\n"+
		"join\n"+
		"query_results s2\n"+
		"where\n"+
		"s1.idquery=?\n"+
		"and\n"+
		"s2.idquery=?\n"+
		"and\n"+
		"s1.idstructure != s2.idstructure\n"+
		"group by id1,id2\n"+
		"order by id1";
	/**
	 * 
	 */
	private static final long serialVersionUID = -526806487145457640L;



	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		if (getFieldname() == null) throw new AmbitException("Properties not defined!");
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getFieldname().getId()));
		params.add(new QueryParam<Integer>(Integer.class, getValue().getId()));
		return params;
	}

	@Override
	public String getSQL() throws AmbitException {
		return sql;
	}


}