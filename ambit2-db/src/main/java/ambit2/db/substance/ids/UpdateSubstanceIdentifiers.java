package ambit2.db.substance.ids;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.I5Utils;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.substance.ExternalIdentifier;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.QueryParam;
import ambit2.db.update.AbstractUpdate;

public class UpdateSubstanceIdentifiers<C extends SubstanceRecord> extends AbstractUpdate<C,C>  {
	private static String deleteSQL = "delete from substance_ids where prefix=? and hex(uuid)=?";
	private static String insertSQL = "insert into substance_ids (prefix,uuid,type,id) values %s";
	
	public UpdateSubstanceIdentifiers(C chemical) {
		super(chemical);
	}
	
	public UpdateSubstanceIdentifiers() {
		this(null);
	}	
	
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if (getObject()==null) throw new AmbitException("Substance not defined");
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		String o_uuid = getObject().getCompanyUUID();
		String[] uuid = {null,o_uuid};
		if (o_uuid == null) throw  new AmbitException("Substance not defined");
		uuid = I5Utils.splitI5UUID(o_uuid.toString());
		switch (index) {
		case 0: {
			params1.add(new QueryParam<String>(String.class, uuid[0]));
			params1.add(new QueryParam<String>(String.class, uuid[1].replace("-", "").toLowerCase()));
			break;
		}
		case 1: {
			if (getObject().getExternalids()!=null)
				for (ExternalIdentifier id : getObject().getExternalids()) {
					params1.add(new QueryParam<String>(String.class, uuid[0]));
					params1.add(new QueryParam<String>(String.class, uuid[1]));
					params1.add(new QueryParam<String>(String.class, id.getSystemDesignator()));
					params1.add(new QueryParam<String>(String.class, id.getSystemIdentifier()));
				}
			break;
		}
		}
		return params1;
	}

	@Override
	public String[] getSQL() throws AmbitException {

		if (getObject().getExternalids()==null || getObject().getExternalids().size()==0) return new String[] {deleteSQL};
		StringBuilder updateValues = null;	
		for (ExternalIdentifier id : getObject().getExternalids()) {
			if (updateValues==null) updateValues = new StringBuilder();
			else updateValues.append(",");
			updateValues.append("(?,unhex(replace(?,'-','')),?,?)");
		}	
		return new String[] {deleteSQL,String.format(insertSQL, updateValues.toString())};
	}
	public void setID(int index, int id) {
		
	}
}