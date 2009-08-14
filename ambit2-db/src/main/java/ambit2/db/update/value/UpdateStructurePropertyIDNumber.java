package ambit2.db.update.value;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.processors.ValueWriter;
import ambit2.db.processors.AbstractPropertyWriter.mode;
import ambit2.db.readers.PropertyValue;
import ambit2.db.search.QueryParam;
import ambit2.db.update.AbstractUpdate;

/**
	Updates property value of a structure
 * @author nina
 *
 */
public class UpdateStructurePropertyIDNumber extends AbstractUpdate<IStructureRecord, PropertyValue<Double>> {
	protected String[] sql = new String[] {
			String.format("%s %s %s",
					ValueWriter.insert_descriptorvalue,
					ValueWriter.select_number,
					ValueWriter.onduplicate_number)
	};
/*
	public static final String insert_descriptorvalue = "INSERT INTO property_values (id,idproperty,idstructure,idvalue_string,status,user_name,text,value_num,idtype) ";
	public static final String select_number = "values (null,?,?,null,?,SUBSTRING_INDEX(user(),'@',1),null,?,'NUMERIC')";
	
	public static final String onduplicate_number = " on duplicate key update value_num=?, status=?, idvalue_string=null,text=null,idtype='NUMERIC'";
	
 */
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if ((getObject().getProperty()==null) || getObject().getProperty().getId()<=0) throw new AmbitException("Undefined property");
		if ((getGroup() == null) || getGroup().getIdstructure()<=0) throw new AmbitException("Undefined structure");
		List<QueryParam> l = new ArrayList<QueryParam>();
		Double value = getObject().getValue();
		mode error = mode.UNKNOWN;
		String longText = null;
		
		if (index == 0) {

			l.add(new QueryParam<Integer>(Integer.class,getObject().getProperty().getId()));
			l.add(new QueryParam<Integer>(Integer.class,getGroup().getIdstructure()));
			l.add(new QueryParam<Integer>(Integer.class,error.ordinal()));
			l.add(new QueryParam<Double>(Double.class,value));
			l.add(new QueryParam<Integer>(Integer.class,error.ordinal()));
			l.add(new QueryParam<Double>(Double.class,value));
		} else throw new AmbitException("Undefined index"+index);
		
		return l;
	}

	public String[] getSQL() throws AmbitException {
		return sql;
	}

	public void setID(int index, int id) {
		
	}
}
