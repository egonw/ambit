/* UpdateSubstanceRelation
 * Author: nina
 * Date: Aug 06, 2013
 * 
 * Copyright (C) 2005-2013  Ideaconsult Ltd.
 * 
 * Contact: www.ideaconsult.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit2.db.substance.relation;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.STRUCTURE_RELATION;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.base.relation.composition.Proportion;
import ambit2.db.chemrelation.AbstractUpdateStructureRelation;
import ambit2.db.search.QueryParam;

public class UpdateSubstanceRelation extends AbstractUpdateStructureRelation<SubstanceRecord,IStructureRecord,STRUCTURE_RELATION,Proportion> {

	public static final String[] create_sql = {
		"INSERT INTO substance_relation (idsubstance,idchemical,relation,`function`, " +
		"proportion_real_lower,proportion_real_lower_value,proportion_real_upper,proportion_real_upper_value,proportion_real_unit,\n"+
		"proportion_typical,proportion_typical_value,proportion_typical_unit)\n"+
		"values(?,?,?,?,?,?,?,?,?,?,?,?) on duplicate key update\n"+
		"proportion_real_lower=values(proportion_real_lower),proportion_real_lower_value=values(proportion_real_lower_value)," +
		"proportion_real_upper=values(proportion_real_upper),proportion_real_upper_value=values(proportion_real_upper_value)," +
		"proportion_real_unit=values(proportion_real_unit),proportion_typical=values(proportion_typical),\n"+
		"proportion_typical_value=values(proportion_typical_value),proportion_typical_unit=values(proportion_typical_unit)"
	};
	public UpdateSubstanceRelation(CompositionRelation relation) {
		this(relation.getFirstStructure(),relation.getSecondStructure(),relation.getRelationType(),relation.getRelation());
	}
	public UpdateSubstanceRelation() {
		this(null,null,null,null);
	}
	public UpdateSubstanceRelation(SubstanceRecord structure1,IStructureRecord structure2,STRUCTURE_RELATION relation) {
		this(structure1,structure2,relation,null);
	}
	public UpdateSubstanceRelation(SubstanceRecord structure1,IStructureRecord structure2,STRUCTURE_RELATION relation,Proportion metric) {
		super(structure1,structure2,relation,metric);
	}
	public void setCompositionRelation(CompositionRelation relation) {
		setGroup(relation.getFirstStructure());
		setObject(relation.getSecondStructure());
		setMetric(relation.getRelation());
		setRelation(relation.getRelationType());
	}

	public void setID(int index, int id) {
	}

	public String[] getSQL() throws AmbitException {
		return create_sql;
	}
	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		if (getGroup()==null || getGroup().getIdsubstance()<=0) throw new AmbitException("Empty substance id");
		if (getObject()==null || getObject().getIdchemical()<=0) throw new AmbitException("Empty chemical id");
		params1.add(new QueryParam<Integer>(Integer.class, getGroup().getIdsubstance()));
		params1.add(new QueryParam<Integer>(Integer.class, getObject().getIdchemical()));
		params1.add(new QueryParam<String>(String.class, getRelation().name()));
		if (getMetric().getFunction()!=null && getMetric().getFunction().length()>45)
			params1.add(new QueryParam<String>(String.class, getMetric().getFunction().substring(0,44)));
		else params1.add(new QueryParam<String>(String.class, getMetric().getFunction()));
		params1.add(new QueryParam<String>(String.class, getMetric().getReal_lower()));
		params1.add(new QueryParam<Double>(Double.class, getMetric().getReal_lowervalue()));
		params1.add(new QueryParam<String>(String.class, getMetric().getReal_upper()));
		params1.add(new QueryParam<Double>(Double.class, getMetric().getReal_uppervalue()));
		params1.add(new QueryParam<String>(String.class, getMetric().getReal_unit()));
		params1.add(new QueryParam<String>(String.class, getMetric().getTypical()));
		params1.add(new QueryParam<Double>(Double.class, getMetric().getTypical_value()));
		params1.add(new QueryParam<String>(String.class, getMetric().getTypical_unit()));
		return params1;
	}
}
