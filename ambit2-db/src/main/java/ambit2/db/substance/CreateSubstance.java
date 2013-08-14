/* CreateSubstance
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

package ambit2.db.substance;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.I5Utils;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.QueryParam;
import ambit2.db.update.AbstractObjectUpdate;

/**
 * 
 * @author nina
 *
 */
public class CreateSubstance  extends AbstractObjectUpdate<SubstanceRecord> {
	
	
	public static final String[] create_sql = {
		"INSERT INTO substance (idsubstance,prefix,uuid,documentType,format,name,publicname,content,substanceType,rs_prefix,rs_uuid,owner_prefix,owner_uuid)\n" +
		"values (?,?,unhex(replace(?,'-','')),?,?,?,?,?,?,?,unhex(replace(?,'-','')),?,unhex(replace(?,'-',''))) " +
		"on duplicate key update " +
		"prefix=values(prefix)," +
		"uuid=values(uuid)," +
		"documentType=values(documentType)," +
		"format=values(format)," +
		"name=values(name)," +
		"publicname=values(publicname)," +
		"content=values(content),"+
		"substanceType=values(substanceType),"+
		"rs_prefix=values(rs_prefix)," +
		"rs_uuid=values(rs_uuid),"+
		"owner_prefix=values(owner_prefix)," +
		"owner_uuid=values(owner_uuid)"
	};

	public CreateSubstance(SubstanceRecord substance) {
		super(substance);
	}
	public CreateSubstance() {
		this(null);
	}		
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		if (getObject().getIdsubstance()<=0)
			params1.add(new QueryParam<Integer>(Integer.class, null));
		else
			params1.add(new QueryParam<Integer>(Integer.class, getObject().getIdsubstance()));
		
		String o_uuid = getObject().getCompanyUUID();
		String[] uuid = {null,o_uuid};
		if (o_uuid!=null) 
			uuid = I5Utils.splitI5UUID(o_uuid.toString());
		params1.add(new QueryParam<String>(String.class, uuid[0]));
		params1.add(new QueryParam<String>(String.class, uuid[1]));
		params1.add(new QueryParam<String>(String.class, "Substance"));
		params1.add(new QueryParam<String>(String.class, getObject().getFormat()));		
		params1.add(new QueryParam<String>(String.class, getObject().getCompanyName()));
		params1.add(new QueryParam<String>(String.class, getObject().getPublicName()));
		params1.add(new QueryParam<String>(String.class, getObject().getContent()));
		params1.add(new QueryParam<String>(String.class, getObject().getSubstancetype()));
		String rs_uuid = getObject().getReferenceSubstanceUUID();
		uuid = new String[]{null,rs_uuid};
		if (rs_uuid!=null) 
			uuid = I5Utils.splitI5UUID(rs_uuid.toString());
		params1.add(new QueryParam<String>(String.class, uuid[0]));
		params1.add(new QueryParam<String>(String.class, uuid[1]));
		
		String ownerUUID = getObject().getOwnerUUID();
		uuid = new String[]{null,ownerUUID};
		if (ownerUUID!=null) {
			uuid = I5Utils.splitI5UUID(ownerUUID.toString());
			params1.add(new QueryParam<String>(String.class, uuid[0]));
			params1.add(new QueryParam<String>(String.class, uuid[1]));
		} else {
			params1.add(new QueryParam<String>(String.class, null));
			params1.add(new QueryParam<String>(String.class, null));
		}
		return params1;
		
	}
	public void setID(int index, int id) {
		getObject().setIdsubstance(id);

	}

	public String[] getSQL() throws AmbitException {
		return create_sql;
	}
	@Override
	public boolean returnKeys(int index) {
		return true;
	}
}
