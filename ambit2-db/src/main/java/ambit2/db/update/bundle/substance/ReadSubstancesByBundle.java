package ambit2.db.update.bundle.substance;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.substance.AbstractReadSubstance;

public class ReadSubstancesByBundle  extends AbstractReadSubstance<SubstanceEndpointsBundle,SubstanceRecord> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 661276311247312738L;
	private static String sql = "select idsubstance,prefix,hex(uuid) as huuid,documentType,format,name,publicname,content,substanceType,rs_prefix,hex(rs_uuid) as rs_huuid,owner_prefix,hex(owner_uuid) as owner_huuid,owner_name from substance join bundle_substance using(idsubstance) where idbundle=?\n";
	
	@Override
	public String getSQL() throws AmbitException {
		return sql;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		if (getFieldname()!=null) {
			params1.add(new QueryParam<Integer>(Integer.class, getFieldname().getID()));
			return params1;
		}
		throw new AmbitException("Unspecified substance");
	}

	@Override
	protected SubstanceRecord getRecord() {
		return (getValue()==null)?new SubstanceRecord():getValue();
	}

}
