/* DescriptorsCalculationProcessor.java
 * Author: nina
 * Date: Dec 28, 2008
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Ideaconsult Ltd.
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

package ambit2.descriptors.processors;



import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openscience.cdk.qsar.IMolecularDescriptor;

import ambit2.base.data.Property;

public class PropertyCalculationProcessor extends  DescriptorCalculationProcessor {
	protected Property property = null;
	protected Map<Class,IMolecularDescriptor> cache = new ConcurrentHashMap<Class, IMolecularDescriptor>(); 
	protected boolean useCache = false;
	/**
	 * 
	 */
	private static final long serialVersionUID = 4826827029980497125L;

	public PropertyCalculationProcessor() {
	}
	
	public Property getProperty() {
		return property;
	}

	public synchronized IMolecularDescriptor getCachedDescriptor(Class className) throws Exception {
		IMolecularDescriptor d = useCache?cache.get(className):null;
		if (d == null) {
			Object o = className.newInstance();
			//this is to remove swing listeners from toxtree rules
			try {
				o.getClass().getMethod(
		                "removeListener",
		                new Class[] {}).
		        invoke(o, new Object[] { });					
			} catch (Exception x) {}
			
			try {
				o.getClass().getMethod(
		                "setWeb",
		                new Class[] {Boolean.class}).
		        invoke(o, new Object[] { Boolean.TRUE});					
			} catch (Exception x) {}
			
			if (useCache && (o instanceof IMolecularDescriptor)) {
				d = (IMolecularDescriptor) o;
				cache.put(className,d);
			}
		}
		return d;
	}
	public synchronized void  setProperty(Property property) {
		this.property = property;
		if (property == null) return;
		try {
			setDescriptor(getCachedDescriptor(property.getClazz()));
		} catch (Exception x) {
			x.printStackTrace();
			setDescriptor(null);
		}
	}
	
}
