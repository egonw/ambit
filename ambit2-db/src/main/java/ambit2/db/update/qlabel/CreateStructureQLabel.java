package ambit2.db.update.qlabel;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.QLabel;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.QueryParam;
import ambit2.db.update.AbstractUpdate;

/**
 * Adds a quality label per structure
 * @author nina
 *
 */
public class CreateStructureQLabel extends AbstractUpdate<IStructureRecord, QLabel> {
	protected String[] sql = {
			"insert into quality_structure (idstructure,user_name,`label`,`text`) values (?,?,?,?) on duplicate key update `label`=?,`text`=?"
	};
	protected String[] sql_defaultuser = {
			"insert into quality_structure (idstructure,user_name,`label`,`text`) values (?,SUBSTRING_INDEX(user(),'@',1),?,?) on duplicate key update `label`=?,`text`=?"
	};
	
	public CreateStructureQLabel(IStructureRecord record, QLabel label) {
		setGroup(record);
		setObject(label);
	}
	public CreateStructureQLabel() {
		this(null,null);
	}	
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if (getObject()==null) throw new AmbitException("Label not defined");
		if ((getGroup()==null) || (getGroup().getIdstructure()<=0)) throw new AmbitException("Structure not defined");
		
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		params1.add(new QueryParam<Integer>(Integer.class, getGroup().getIdstructure()));
		if (getObject().getUser()!=null) 
			params1.add(new QueryParam<String>(String.class, getObject().getUser().getName()));
		params1.add(new QueryParam<String>(String.class, getObject().getLabel().toString()));
		params1.add(new QueryParam<String>(String.class, getObject().getText()));
		params1.add(new QueryParam<String>(String.class, getObject().getLabel().toString()));
		params1.add(new QueryParam<String>(String.class, getObject().getText()));			
		return params1;
	}

	public String[] getSQL() throws AmbitException {
		return (getObject().getUser()==null)?sql_defaultuser:sql;
	}

	public void setID(int index, int id) {
		// TODO Auto-generated method stub
		
	}

}
