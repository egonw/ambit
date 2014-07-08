/* DeleteStoredQuery.java
 * Author: nina
 * Date: Apr 10, 2009
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

package ambit2.db.update.storedquery;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.db.SessionID;
import ambit2.db.search.IStoredQuery;
import ambit2.db.update.AbstractUpdate;

public class DeleteStoredQuery extends AbstractUpdate<SessionID,IStoredQuery> {
	
	public static final String delete_sql = "delete query FROM query, sessions where %s %s %s and query.idsessions=sessions.idsessions and user_name=SUBSTRING_INDEX(user(),'@',1)";
	public static String where_session="query.idsessions=?";
	public static String where_query="idquery=?";
	public static String where_and="and";
	
	public DeleteStoredQuery(IStoredQuery query) {
		super(query);
	}
	public DeleteStoredQuery() {
		this(null);
	}		
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getGroup()!=null)
			params.add(new QueryParam<Integer>(Integer.class, getGroup().getId()));
		if (getObject()!=null)
			params.add(new QueryParam<Integer>(Integer.class, getObject().getId()));
		if (params.size()==0) return null;
		return params;
	}

	public String[] getSQL() throws AmbitException {
		String a1 = (getGroup()==null)?"":where_session;
		String a2 = (getObject()==null)?"":where_query;
		String a3 = ((getGroup()!=null)&&(getObject()!=null))?"and":"";
		return new String[] {String.format(delete_sql,a1,a3,a2)};
	}
	public void setID(int index, int id) {
		
	}


}
