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

package ambit2.base.data;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;

import ambit2.base.exceptions.AmbitIOException;

import com.jgoodies.binding.beans.Model;



public class Property extends Model implements Serializable {
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -6813235410505542235L;
	/*
	private static java.util.concurrent.CopyOnWriteArrayList<Property> properties = 
		new CopyOnWriteArrayList<Property>();	
		*/
	private static String defaultReference = "Default property reference";
	public static String AMBIT_DESCRIPTORS_ONTOLOGY = "http://ambit.sourceforge.net/descriptors.owl#%s";
	public static final String Names = "Names";
	public static final String CAS = "CasRN";
	public static final String opentox_CAS = "http://www.opentox.org/api/1.1#CASRN";
	public static final String opentox_Name = "http://www.opentox.org/api/1.1#ChemicalName";
	public static final String opentox_IupacName = "http://www.opentox.org/api/1.1#IUPACName";
	public static final String opentox_EC = "http://www.opentox.org/api/1.1#EINECS";
	public static final String opentox_InChI = "http://www.opentox.org/api/1.1#InChI";
	public static final String opentox_SMILES = "http://www.opentox.org/api/1.1#SMILES";
	public static final String opentox_TupleFeature = "http://www.opentox.org/api/1.1#TupleFeature";
	public static final String EC = "EC";
	protected boolean isNominal = false;
	public boolean isNominal() {
		return isNominal;
	}
	public void setNominal(boolean isNominal) {
		this.isNominal = isNominal;
	}
	public static synchronized Property getNameInstance() {
		return getInstance(Names, LiteratureEntry.getIUPACReference());
	}	
	public static synchronized Property getCASInstance() {
		Property p = getInstance(CAS, LiteratureEntry.getCASReference());
		p.setLabel(opentox_CAS);
		return p;
	}		
	public static synchronized Property getEINECSInstance() {
		Property p =  getInstance(EC, LiteratureEntry.getEINECSReference());
		p.setLabel(opentox_EC);
		return p;
	}			
	public static synchronized Property getInstance(String name,String reference) {
		return getInstance(name, reference,"");
	}
	public static synchronized Property getInstance(String name,ILiteratureEntry reference) {
		if (reference == null)
			return getInstance(name, defaultReference,"http://ambit.sourceforge.net");
		else
			return getInstance(name, reference.getTitle(),reference.getURL());
	}	
	public static synchronized Property getInstance(String name,String reference, String url) {
		Property p = new Property(name,LiteratureEntry.getInstance(reference,url));
		p.setLabel(name);

		return p;
	}		
	public enum IO_QUESTION  {
		IO_START,
		IO_TRANSLATE_NAME,
		IO_STOP,
	};
	protected String name = "NA";
	protected String label = "NA";
	protected String units = "";
	protected int id=-1;
	protected int order = 0;
	protected Class clazz = java.lang.String.class;
	protected boolean enabled = false;
	protected ILiteratureEntry reference = LiteratureEntry.getInstance();
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public ILiteratureEntry getReference() {
		return reference;
	}

	public  void setReference(ILiteratureEntry reference) {
		this.reference = reference;
	}
	private Property() {
		
	}
	/**
	 * public constructors for web services
	 * @param name
	 */
	public static String guessLabel(String n) {
		if (n==null) return null;
		n = n.toLowerCase();
		if (n.startsWith("cas")) return opentox_CAS;
		else if (n.contains("species")) return "Species";
		else if (n.contains("iupac")) return opentox_IupacName;	
		else if (n.contains("name")) return opentox_Name;
		else if (n.contains("title")) return opentox_Name;
		else if (n.contains("inchi")) return opentox_InChI;
		else if (n.contains("smiles")) return opentox_SMILES;
		else if (n.equals("ec-number")) return opentox_EC;
		else if (n.equals("ec_number")) return opentox_EC;
		else if (n.equals("ec")) return opentox_EC;
		else if (n.contains("synonym")) return opentox_Name;
		return null;
	}
	public Property(String name) {
		this(name,Property.guessLabel(name));
	}
	
	public Property(String name, LiteratureEntry reference) {
		this(name,"",reference);
	}	
	public Property(String name, String units, LiteratureEntry reference) {
		this(name,Property.guessLabel(name));
		setUnits(units);
		this.reference = reference;
	}		
	private Property(String name,String label) {
		this(name,label,0);
	}
	public Property(String name,String label,String units) {
		this(name,label);
		setUnits(units);
	}	
	private Property(String name,String label,int order) {
		this(name,label,order,java.lang.String.class);
	}
	
	private Property(String name,String label,int order, Class clazz) {
		this(name,label,order,clazz,false);
	}	
	
	private Property(String name,String label,int order, Class clazz, boolean enabled) {
		this.name = name;
		setLabel(label);
		setOrder(order);
		setClazz(clazz);
		setEnabled(enabled);
	}	
	
    public int hashCode() {
    	int hash = 7;
    	int var_code = (null == getName() ? 0 : getName().hashCode());
    	hash = 31 * hash + var_code; 
    	var_code = (null == getReference().getTitle() ? 0 : getReference().getTitle().hashCode());
    	hash = 31 * hash + var_code; 
	
    	return hash;
    }	
	public Class getClazz() {
		return clazz;
	}
	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getName() {
		return name;
	}

	public String getTitle() {
		return getReference().getTitle();
	}
	public String getUrl() {
		return getReference().getURL();
	}	
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	@Override
	public String toString() {
		return getName();
	}
	public boolean save(OutputStream out) throws AmbitIOException {
		return save(new OutputStreamWriter(out));
	}
	public boolean save(Writer writer) throws AmbitIOException {		
		try {
			writer.write("\n");
			writer.write("<field ");
			writer.write("name=\"");
			writer.write(getName());
			writer.write("\" units=\"");
			writer.write(getUnits());
			writer.write("\" class=");
			writer.write(getClazz().getName());
			writer.write("/>");
			return true;
		} catch ( IOException  x) {
			throw new AmbitIOException(this.getClass().getName(),x);
		}
	}	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Property) {
			return
			((Property)obj).getName().equals(getName()) &&
			((Property)obj).getReference().equals(getReference()) ; 
		} else return false;
	}
	public void assign(Property newProperty) {
		this.id = newProperty.id;
		this.name = newProperty.name;
		this.reference = new LiteratureEntry(newProperty.getTitle(),newProperty.getUrl());
		this.label = newProperty.label;
		this.clazz = newProperty.clazz;
		this.units = newProperty.units;
	}
	
	public boolean isCAS() {
		String label = getLabel();
		if (getName().equals(label)) label = guessLabel(getName());
		return opentox_CAS.equals(label);
	}
	public boolean isName() {
		String label = getLabel();
		if (getName().equals(label)) label = guessLabel(getName());
		return opentox_Name.equals(label) || opentox_IupacName.equals(label);
	}
	public boolean isEINECS() {
		String label = getLabel();
		if (getName().equals(label)) label = guessLabel(getName());
		return opentox_EC.equals(label);
	}
	
	
}
