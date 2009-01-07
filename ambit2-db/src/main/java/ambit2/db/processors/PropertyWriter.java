/*
Copyright (C) 2005-2008  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit2.db.processors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import ambit2.core.data.IStructureRecord;

/**
 * <pre>
writes properties
</pre>
 * @author nina
 *
 */
public class PropertyWriter extends AbstractRepositoryWriter<IStructureRecord,IStructureRecord> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5935037090604065891L;
	protected static final String select_property_byname = "SELECT idfieldname,name FROM FIELD_NAMES WHERE name=?";
	protected PreparedStatement ps_selectproperty;
	protected static final String insert_property_name = "INSERT IGNORE INTO FIELD_NAMES (idfieldname,name) values (null,?)";
	protected PreparedStatement ps_propertyname;
	protected static final String insert_property_value = "INSERT INTO STRUCTURE_FIELDS (idstructure,idfieldname,value) values (?,?,left(?,256))";
	protected PreparedStatement ps_propertyvalue;

	@Override
	protected void prepareStatement(Connection connection) throws SQLException {
		ps_propertyname = connection.prepareStatement(insert_property_name);
		ps_propertyvalue = connection.prepareStatement(insert_property_value);
		ps_selectproperty = connection.prepareStatement(select_property_byname);
	}

	@Override
	public IStructureRecord write(IStructureRecord record) throws SQLException {
		
		Iterator e = record.getProperties().keySet().iterator();
		while (e.hasNext()) {
			Object key = e.next();
			Object value = record.getProperties().get(key);
			ps_selectproperty.clearParameters();
			ps_selectproperty.setString(1,key.toString());
			ResultSet names = ps_selectproperty.executeQuery();
			int records = 0;
			while (names.next()) {
				writePropertyValue(record.getIdstructure(),  names.getInt(1), key.toString(), value.toString());
				records++;
			}
			names.close();
			//insert property
			if (records == 0) {
				ps_propertyname.clearParameters();
				ps_propertyname.setString(1,key.toString());
				ps_propertyname.executeUpdate();
				ResultSet rs = ps_propertyname.getGeneratedKeys();
				while (rs.next()) {
					writePropertyValue(record.getIdstructure(),  rs.getInt(1), key.toString(), value.toString());
				} 
				rs.close();				
			}
			
		}
        return record;

		
	}
	
	protected void writePropertyValue(int idstructure,int idfieldname,String key,String value ) throws SQLException {
		//System.out.println(idstructure + " " + idfieldname + " " + key + " " + value);
		ps_propertyvalue.clearParameters();
		ps_propertyvalue.setInt(1,idstructure);
		ps_propertyvalue.setInt(2,idfieldname);
		ps_propertyvalue.setString(3,value.toString());
		ps_propertyvalue.executeUpdate();		
	}
	@Override
	public void close() throws SQLException {
		ps_propertyname.close();
		ps_propertyvalue.close();
		ps_selectproperty.close();
		super.close();
	}


}


