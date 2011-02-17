package ambit2.db.update.model;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.QueryParam;
import ambit2.db.search.StringCondition;

public class QueryModel  extends AbstractModelQuery<String,ModelQueryResults>  implements IQueryRetrieval<ModelQueryResults> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5485707611740260825L;
	
	public QueryModel(ModelQueryResults query) {
		super();
		setValue(query);
		setCondition(StringCondition.getInstance(StringCondition.C_EQ));
	}
	@Override
	public double calculateMetric(ModelQueryResults object) {
		return 1;
	}

	@Override
	public boolean isPrescreen() {
		return false;
	}

	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		for (_models_criteria c : _models_criteria.values()) 
			if (c.isEnabled(getValue(),getFieldname())) {
				QueryParam param = c.getParam(getValue(),getFieldname());
				if (param!= null) params.add(param);
					
			}
		return params.size()==0?null:params;
	}

	public String getSQL() throws AmbitException {
		String where = "where";
		StringBuilder b = new StringBuilder();
		
		for (_models_criteria c : _models_criteria.values()) 
			if (c.isEnabled(getValue(),getFieldname())) {
				b.append(where);
				b.append(c.getSQL());
				where = " and ";
			}

		return String.format(_models_criteria.endpoint.isEnabled(getValue(),getFieldname())?sql_predicted:sql,b.toString());
	}

}
