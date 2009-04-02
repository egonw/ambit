/* TemplateAddProperty.java
 * Author: nina
 * Date: Apr 1, 2009
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

package ambit2.db.update.dictionary;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.Dictionary;
import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.QueryParam;
import ambit2.db.update.AbstractUpdate;
import ambit2.db.update.property.CreateProperty;

public class TemplateAddProperty extends AbstractUpdate<Dictionary,Property> {
	protected CreateDictionary createDictionary;
	protected CreateProperty createProperty;
	
	public static final String create_sql = 
		"INSERT IGNORE INTO template_def " +
		"SELECT idtemplate,idproperty,idproperty FROM template join properties join catalog_references using(idreference) "+
		"where properties.name=? and catalog_references.title=? and template.name=?";


	public TemplateAddProperty(Dictionary template,Property property) {
		super();
		setGroup(template);
		setObject(property);
	}
	
	public TemplateAddProperty() {
		this(null,null);
	}
	@Override
	public void setGroup(Dictionary group) {
		super.setGroup(group);
		if (createDictionary==null) createDictionary = new CreateDictionary();
		createDictionary.setObject(group);
	}
	@Override
	public void setObject(Property object) {
		super.setObject(object);
		if (createProperty==null) createProperty = new CreateProperty();
		createProperty.setObject(object);
	}
	public List<QueryParam> getParameters(int index) throws AmbitException {
		int dict = createDictionary.getSQL().length;
		if (index < dict) 
			return createDictionary.getParameters(index);
		else if (index < (dict + createProperty.getSQL().length)) 
			return createProperty.getParameters(index-dict);		
		else  {
			List<QueryParam> params1 = new ArrayList<QueryParam>();
			params1.add(new QueryParam<String>(String.class, getObject().getName()));
			params1.add(new QueryParam<String>(String.class, getObject().getReference().getTitle()));
			params1.add(new QueryParam<String>(String.class, getGroup().getTemplate()));
			return params1;
		}
		
	}
	public void setID(int index, int id) {
		try {
		int dict = createDictionary.getSQL().length;
		if (index < dict) 
			return ;
		else if (index < (dict + createProperty.getSQL().length)) 
			createProperty.setID(index-dict, id);
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	public String[] getSQL() throws AmbitException {
		String[] dictionary = createDictionary.getSQL();
		String[] property = createProperty.getSQL();
		String[] sql = new String[dictionary.length+property.length+1];
		for (int i=0; i < dictionary.length;i++)
			sql[i]=dictionary[i];
		for (int i=dictionary.length; i < (dictionary.length+property.length);i++)
			sql[i]=property[i-dictionary.length];		
		sql[sql.length-1]=create_sql;
		return sql;
	}

}
