/* DatasetAddTuple.java
 * Author: nina
 * Date: Apr 3, 2009
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

package ambit2.db.update.tuple;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.SourceDataset;
import ambit2.db.update.AbstractUpdate;
import ambit2.db.update.dataset.CreateDataset;

public class DatasetAddTuple extends AbstractUpdate<SourceDataset,Integer>{

	protected CreateDataset createDataset = new CreateDataset();
	
	public static final String create_sql_byname =	
		"insert into tuples select null,id_srcdataset from src_dataset where name=?";
	public static final String create_sql_byid =	
		"insert into tuples (idtuple,id_srcdataset) values (null,?)";

	protected boolean byName = true;
	
	public DatasetAddTuple(SourceDataset dataset,Integer tuple) {
		super();
		setGroup(dataset);
		setObject(tuple);
	}
	
	public DatasetAddTuple() {
		this(null,null);
	}
	@Override
	public void setGroup(SourceDataset group) {
		super.setGroup(group);
		createDataset.setObject(group);
		byName = group==null || group.getID()<=0;
	}
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if (getGroup()==null || getGroup().getName()==null) throw new AmbitException("Dataset not defined!");
		
		if (byName) {
			if (index < createDataset.getSQL().length) 
				return createDataset.getParameters(index);
			else  {
				List<QueryParam> params1 = new ArrayList<QueryParam>();
				params1.add(new QueryParam<String>(String.class, getGroup().getName()));
				return params1;
			}
		} else {
			List<QueryParam> params1 = new ArrayList<QueryParam>();
			params1.add(new QueryParam<Integer>(Integer.class, getGroup().getID()));
			return params1;
		}
		
	}
	public void setID(int index, int id) {
		if (byName)
			try {
				String[] dataset = createDataset.getSQL();
				if (index >= dataset.length)
					setObject(id);
				else
					createDataset.setID(index, id);
			} catch (Exception x) {
				
			}
		else setObject(id);
		
	}
	@Override
	public boolean returnKeys(int index) {
		return true;
	}
	public String[] getSQL() throws AmbitException {
		if (byName) {
			String[] dataset = createDataset.getSQL();
			String[] sql = new String[dataset.length+1];
			for (int i=0; i < dataset.length;i++)
				sql[i]=dataset[i];
			sql[sql.length-1]=create_sql_byname;
			return sql;
		} else 
			return new String[] {create_sql_byid};
	}
}
