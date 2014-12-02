package ambit2.db.facets.bundle;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.facet.AbstractFacetQuery;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.facet.BundleRoleFacet;
import ambit2.db.search.StringCondition;

public class EndpointRoleByBundle  extends AbstractFacetQuery<SubstanceRecord,SubstanceEndpointsBundle,StringCondition,BundleRoleFacet>  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2183619142564101671L;
	/**
	 * 
	 */
	
	private final static String sql = "SELECT idbundle,idsubstance from bundle_chemicals where idbundle=? and idsubstance=?"; 
	protected BundleRoleFacet record;
	
	public EndpointRoleByBundle(String facetURL) {
		super(facetURL);
		record = createFacet(facetURL);
	}
	@Override
	public boolean isPrescreen() {
		return false;
	}

	@Override
	public double calculateMetric(BundleRoleFacet object) {
		return 1;
	}

	@Override
	public String getSQL() throws AmbitException {
		return sql;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		if ((getFieldname()==null) || (getFieldname().getIdsubstance()<=0)) throw new AmbitException("Substance not defined");
		if (getValue()==null || getValue().getID()<=0)  throw new AmbitException("Bundle not defined");
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class,getValue().getID()));		
		params.add(new QueryParam<Integer>(Integer.class,getFieldname().getIdsubstance()));		
		return params;
	}
	
	@Override
	protected BundleRoleFacet createFacet(String facetURL) {
		return new BundleRoleFacet(facetURL);
	}

	@Override
	public BundleRoleFacet getObject(ResultSet rs) throws AmbitException {
		if (record == null) {
			record = createFacet(null);
		} else record.clear();
		try {
			record.setTag(null);
			record.setRemarks(null);
			record.setValue(getValue());
			record.setCount(1);
			return record;
		} catch (Exception x) {
			record.setValue(getValue());
			record.setCount(-1);
			return record;
		}
	}	
	
}