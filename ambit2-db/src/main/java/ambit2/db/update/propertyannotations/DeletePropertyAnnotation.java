package ambit2.db.update.propertyannotations;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.Property;
import ambit2.base.data.PropertyAnnotation;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.QueryParam;
import ambit2.db.update.AbstractUpdate;

public class DeletePropertyAnnotation extends AbstractUpdate<Property,PropertyAnnotation> {
	protected final String[] sql = {
			"DELETE from property_annotation where idproperty=?"
	};
	@Override
	public String[] getSQL() throws AmbitException {
		return sql;
	}

	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if ((getGroup()==null) || (getGroup().getId()<=0)) throw new AmbitException("No property!");
		
		List<QueryParam> param = new ArrayList<QueryParam>();
		param.add(new QueryParam<Integer>(Integer.class,getGroup().getId()));
		return param;
	}

	@Override
	public void setID(int index, int id) {
		
		
	}

}