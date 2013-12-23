/* MoleculeReader.java
 * Author: Nina Jeliazkova
 * Date: Aug 10, 2008 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Nina Jeliazkova
 * 
 * Contact: nina@acad.bg
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

package ambit2.core.processors.structure;

import java.lang.reflect.Method;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.inchi.InChIToStructure;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.base.processors.CASProcessor;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.core.config.AmbitCONSTANTS;
import ambit2.core.data.MoleculeTools;
import ambit2.core.io.FileInputState;

public class MoleculeReader extends DefaultAmbitProcessor<IStructureRecord,IAtomContainer> {
	
	protected InChIGeneratorFactory inchiFactory = null;
	protected CASProcessor casTransformer = null;
	protected SmilesParser smiParser = null;

    /**
     * 
     */
    private static final long serialVersionUID = 1811923574213153916L;

    public IAtomContainer process(IStructureRecord target) throws AmbitException {
    		if (target.getContent()==null) return null;
    		if (target.getFormat()==null)
    			throw new AmbitException("Unknown format "+target.getFormat());
    		MOL_TYPE format = MOL_TYPE.SDF;
    		try {
    			format = MOL_TYPE.valueOf(target.getFormat());
    		} catch (Exception x) {
    			throw new AmbitException(x);
    		}
           return handleFormat(format, target);
    }

    protected IAtomContainer handleFormat(MOL_TYPE format,IStructureRecord target) throws AmbitException{
    	 switch (format) {
         case SDF: {
         	try {
         		IAtomContainer ac =  MoleculeTools.readMolfile(target.getContent());
         		if ((ac!=null) && (ac.getProperties()!=null)) {
         			Object title = ac.getProperty(CDKConstants.TITLE);
         			if (title != null) {
         				if (CASProcessor.isValidFormat(title.toString())) try {
         					if (casTransformer==null) casTransformer = new CASProcessor();
         					ac.setProperty(AmbitCONSTANTS.CASRN,casTransformer.process(title.toString()));
	            			} catch (Exception x) {}
	            			ac.removeProperty(CDKConstants.TITLE);
	            		}
	        	   		ac.removeProperty(CDKConstants.REMARK);          	
         		}
         		return ac;
             } catch (Exception x) {
                 throw new AmbitException(x);
             }
             
         }
         
        case CML:     
     	   	try {
     	   		IAtomContainer ac =  MoleculeTools.readCMLMolecule(target.getContent());
         		if ((ac!=null) && (ac.getProperties()!=null)) {
         			Object title = ac.getProperty(CDKConstants.TITLE);
         			if (title != null) {
         				if (CASProcessor.isValidFormat(title.toString())) try {
         					if (casTransformer==null) casTransformer = new CASProcessor();
         					ac.setProperty(AmbitCONSTANTS.CASRN,casTransformer.process(title.toString()));
	            			} catch (Exception x) {}
	            			ac.removeProperty(CDKConstants.TITLE);
	            		}
	        	   		ac.removeProperty(CDKConstants.REMARK);            	
         		}
     	   		return ac;
             } catch (Exception x) {
                 throw new AmbitException(x);
             }
        case INC: 
     	   try {
     		   if (target.getContent().startsWith("InChI=")) {
	     		   if (inchiFactory==null) inchiFactory = InChIGeneratorFactory.getInstance();
	     		   InChIToStructure c =inchiFactory.getInChIToStructure(target.getContent(), SilentChemObjectBuilder.getInstance());
	     		   return c.getAtomContainer();
     		   } else { //smiles
         		   if (smiParser==null) smiParser = new SmilesParser( SilentChemObjectBuilder.getInstance());
         		   return smiParser.parseSmiles(target.getContent());     			   
     		   }
     	   } catch (Exception x) {
       		   throw new AmbitException(x);
     	   }
        case NANO: try {
				Class clazz = FileInputState.class.getClassLoader().loadClass("net.idea.ambit2.rest.nano.MoleculeNanoReader");
				Method method = clazz.getMethod("nm2atomcontainer", IStructureRecord.class);
				return (IAtomContainer) method.invoke(null, target);
	  	   } catch (Exception x) {
	  		   if (x instanceof AmbitException) throw (AmbitException)x;
	  		   else throw new AmbitException(x);
	 	   }			
         default: {
         	 throw new AmbitException("Unknown format "+target.getFormat());
         }
         }
    }
}
