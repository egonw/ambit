/* QueryCombinedStructure.java
 * Author: nina
 * Date: Apr 5, 2009
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

package ambit2.db.search.structure;

import ambit2.base.config.Preferences;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.QueryCombined;

public class QueryCombinedStructure extends QueryCombined<IStructureRecord> {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String getScopeSQL() {
		if (isCombine_as_and())
			return "select Q1.idquery,s.idchemical,idstructure,Q1.selected as selected,Q1.metric as metric from structure as s";
		else
			return "select QSCOPE.idquery,s.idchemical,idstructure,QSCOPE.selected as selected,QSCOPE.metric as metric from structure as s";
	
	}
	public void setMaxRecords(long records) {
		try {
			Preferences.setProperty(Preferences.MAXRECORDS,Long.toString(records));
		} catch (Exception x) {
			Preferences.setProperty(Preferences.MAXRECORDS,"2000");
		}
		
	}
	public long getMaxRecords() {
		try {
			return Integer.parseInt(Preferences.getProperty(Preferences.MAXRECORDS));
		} catch (Exception x) {
			return 2000;
		}		
	}
}
