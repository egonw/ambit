/* QuerySmilesByID.java
 * Author: nina
 * Date: Mar 12, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
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

package ambit2.db.search;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.openscience.cdk.CDKConstants;

import ambit2.core.data.IStructureRecord;
import ambit2.core.data.StructureRecord;
import ambit2.core.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;

public class QuerySmilesByID extends AbstractQuery<String, IStructureRecord, NumberCondition, IStructureRecord> implements IQueryRetrieval<IStructureRecord> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4024101219917942351L;
	protected IStructureRecord maxValue = null;
	public static final String sqlField="select idchemical,idstructure,smiles,inchi from structure join chemicals using(idchemical) where idstructure ";
	public QuerySmilesByID() {
		super();
		setCondition(NumberCondition.getInstance("="));
	}
	public IStructureRecord getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(IStructureRecord maxValue) {
		this.maxValue = maxValue;
	}
	public QuerySmilesByID(IStructureRecord record) {
		this();
		setValue(record);
	}
	public QuerySmilesByID(int idstructure) {
		this();
		setValue(new StructureRecord(-1,idstructure,null,null));
	}	
	public QuerySmilesByID(int idstructure1,int idstructure2) {
		this();
		setValue(new StructureRecord(-1,idstructure1,null,null));
		setMaxValue(new StructureRecord(-1,idstructure2,null,null));
		setCondition(NumberCondition.getInstance(NumberCondition.between));
	}		
	public String getSQL() throws AmbitException {
		if (NumberCondition.between.equals(getCondition().getSQL())) {
			return sqlField + getCondition().getSQL() + " ? and ?";
		}
		else return sqlField + getCondition().getSQL() + " ?";
	}

	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getValue().getIdstructure()));
		String c = getCondition().getSQL();
		if (NumberCondition.between.equals(c)) 
			params.add(new QueryParam<Integer>(Integer.class, getMaxValue().getIdstructure()));
		return params;
	}
	public IStructureRecord getObject(ResultSet rs) throws AmbitException {
        try {
            IStructureRecord r = getValue();
            r.setIdchemical(rs.getInt("idchemical"));
            r.setIdstructure(rs.getInt("idstructure"));
            r.setContent(null);
            if (r.getProperties()==null)
            	r.setProperties(new Hashtable());
            r.getProperties().put(CDKConstants.SMILES,rs.getString("smiles"));
            r.getProperties().put(CDKConstants.INCHI,rs.getString("inchi"));
            return r;
        } catch (SQLException x){
            throw new AmbitException(x);
        }
	}

}
