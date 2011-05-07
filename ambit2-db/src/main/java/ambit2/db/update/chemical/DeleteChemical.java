/* DeleteChemical.java
 * Author: nina
 * Date: Mar 31, 2009
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

package ambit2.db.update.chemical;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IChemical;
import ambit2.db.search.QueryParam;
import ambit2.db.update.AbstractObjectUpdate;
import ambit2.db.update.structure.DeleteStructure;

public class DeleteChemical extends AbstractObjectUpdate<IChemical>  {
	protected boolean forceStructureDelete = false;
	public boolean isForceStructureDelete() {
		return forceStructureDelete;
	}
	public void setForceStructureDelete(boolean forceStructureDelete) {
		this.forceStructureDelete = forceStructureDelete;
	}
	
	public static final String[] forcedelete_sql_ = {
		"delete from structure where idchemical=? and user_name=(SUBSTRING_INDEX(user(),'@',1))",
		"delete from chemicals where idchemical=?"
	};
	
	public static final String[] delete_sql = {
		"delete from chemicals where idchemical=?"
	};

	public DeleteChemical(IChemical chemical) {
		super(chemical);
	}
	public DeleteChemical() {
		this(null);
	}		
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if (getObject().getIdchemical()<=0) throw new AmbitException("Chemical id not defined");
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getObject().getIdchemical()));
		return params;
		
	}

	public String[] getSQL() throws AmbitException {
		return isForceStructureDelete()?forcedelete_sql_:delete_sql;
	}
	public void setID(int index, int id) {
		
	}
}
