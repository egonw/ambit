/*
Copyright Ideaconsult Ltd. (C) 2005-2007 

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

/**
 * <b>Filename</b> MolAnalyser.java 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Created</b> 2005-8-2
 * <b>Project</b> toxTree
 */
package ambit2.core.processors.structure;

import java.util.Hashtable;
import java.util.Iterator;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.config.Symbols;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

import ambit2.core.config.Preferences;
import ambit2.core.exceptions.AmbitException;
import ambit2.core.processors.DefaultAmbitProcessor;


/**
 * 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-8-2
 */
public class AtomConfigurator extends DefaultAmbitProcessor<IAtomContainer,IAtomContainer>{

    protected static Hashtable<String,Integer> elements = null;
	
    /**
     * 
     */
    public AtomConfigurator() {
        super();
        getElements();
    }
    
    public static Hashtable<String,Integer> getElements() {
    	if (elements != null) return elements;
    	elements = new Hashtable<String, Integer>();
    	for (int i=0; i < Symbols.byAtomicNumber.length; i++)
    		elements.put(Symbols.byAtomicNumber[i], i);
    	elements.put("R", 0);
    	elements.put("*", 0);
    	return elements;
    	
    }
    public IAtomContainer process(IAtomContainer mol) throws AmbitException {
    	if (mol==null) throw new AmbitException("Null molecule!");
    	if (mol.getAtomCount()==0) throw new AmbitException("No atoms!");
    	
    	try {
    		logger.debug("Configuring atom types ...");
	        CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
	        Iterator<IAtom> atoms = mol.atoms();
	        while (atoms.hasNext()) {
	           IAtom atom = atoms.next();
	           IAtomType type = matcher.findMatchingAtomType(mol, atom);
	           try {
	        	   AtomTypeManipulator.configure(atom, type);
                   logger.debug("Found " + atom.getSymbol() + " of type " + type.getAtomTypeName());                   
	           } catch (Exception x) {
	        	   logger.error(x.getMessage() + " " + atom.getSymbol(),x);
                   
                   if ("true".equals(Preferences.getProperty(Preferences.STOP_AT_UNKNOWNATOMTYPES))) {
                       throw new AmbitException(atom.getSymbol(),x);
                   }
                   
	           }
	        }    	        

        	atoms = mol.atoms();
	        while (atoms.hasNext()) {
		           IAtom atom = atoms.next();
		           if (atom.getAtomicNumber() == 0) {
		        	   Integer no = elements.get(atom.getSymbol());
		        	   if (no != null)
		        		   atom.setAtomicNumber(no.intValue());
		           }	   
		        }            	
	        return mol;
    	} catch (CDKException x) {
    		throw new AmbitException(x);
    		
    	}
    }

}

