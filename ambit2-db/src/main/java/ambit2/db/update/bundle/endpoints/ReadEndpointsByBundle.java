package ambit2.db.update.bundle.endpoints;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.ILiteratureEntry._type;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.data.substance.SubstanceName;
import ambit2.base.data.substance.SubstanceProperty;
import ambit2.base.data.substance.SubstancePublicName;
import ambit2.base.data.substance.SubstanceUUID;
import ambit2.db.search.StringCondition;
import ambit2.db.search.property.AbstractPropertyRetrieval;

public class ReadEndpointsByBundle  extends  AbstractPropertyRetrieval<SubstanceEndpointsBundle, SubstanceProperty, StringCondition> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 661276311247312738L;
	private static String sql = 
		"select p.topcategory,p.endpointcategory,hex(endpointhash) hash from bundle_endpoints p where idbundle=?\n"+
		"union select null,null,\"SubstancePublicName\"\n"+
		"union select null,null,\"SubstanceName\"\n"+
		"union select null,null,\"SubstanceUUID\"\n";
	//"where p.topcategory=? and p.endpointcategory=? and e.endpoint=? and endpointhash=unhex(?) limit 1";
	
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
		throw new AmbitException("Unspecified bundle");
	}

	@Override
	public Property getObject(ResultSet rs) throws AmbitException {
		try {
			Object hash = rs.getObject(3);
			if (hash==null || "".equals(hash)) {
				LiteratureEntry ref = new LiteratureEntry("","Default");
				ref.setType(_type.Substance);
				SubstanceProperty p = new SubstanceProperty(rs.getString(1),rs.getString(2),null,null,ref);
				p.setIdentifier("");
				return p;
			} else {
				if ("SubstancePublicName".equals(hash)) return new SubstancePublicName();
				else if ("SubstanceName".equals(hash)) return new SubstanceName();
				else if ("SubstanceUUID".equals(hash)) return new SubstanceUUID();
				return null;
			}
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
	
}
/**
					record.setProperty(new SubstancePublicName(),record.getPublicName());
					record.setProperty(new SubstanceName(),record.getCompanyName());
					record.setProperty(new SubstanceUUID(), record.getCompanyUUID());

*/