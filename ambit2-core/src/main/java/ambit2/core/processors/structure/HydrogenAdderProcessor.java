/* HydrogenAdderProcessor.java
 * Author: nina
 * Date: Feb 5, 2009
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

package ambit2.core.processors.structure;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.config.Preferences;
import ambit2.base.exceptions.AmbitException;

public class HydrogenAdderProcessor extends	AtomConfigurator {
	protected CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
	protected boolean addEexplicitHydrogens = true;
	public boolean isAddEexplicitHydrogens() {
		return addEexplicitHydrogens;
	}


	public void setAddEexplicitHydrogens(boolean addEexplicitHydrogens) {
		this.addEexplicitHydrogens = addEexplicitHydrogens;
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = -6788800174158558265L;
	
		
	public IAtomContainer process(IAtomContainer mol) throws AmbitException {
		super.process(mol);
		try {
	       	if ((mol instanceof IMolecule) || (mol instanceof IAtomContainer))  {
                try {
    	            adder.addImplicitHydrogens(mol);
    	            logger.debug("Adding implicit hydrogens; atom count "+mol.getAtomCount());
    	            if (isAddEexplicitHydrogens()) {
	    	            AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
	    	            logger.debug("Convert explicit hydrogens; atom count "+mol.getAtomCount());
    	            }

                } catch (Exception x) {
                    logger.error(x);
                    if ("true".equals(Preferences.getProperty(Preferences.STOP_AT_UNKNOWNATOMTYPES))) {
                        throw new AmbitException(x);
                    }
                }
        	} else {
        		IMoleculeSet moleculeSet = ConnectivityChecker.partitionIntoMolecules(mol);
        	      
        	      for (int k = 0; k < moleculeSet.getMoleculeCount(); k++) {
        	    	  IMolecule molPart = moleculeSet.getMolecule(k);
      		          adder.addImplicitHydrogens(molPart);
      		          logger.debug("Adding implicit hydrogens; atom count "+molPart.getAtomCount());
      		          if (isAddEexplicitHydrogens()) {
	    		          AtomContainerManipulator.convertImplicitToExplicitHydrogens(molPart);
	    		          logger.debug("Convert explicit hydrogens; atom count "+molPart.getAtomCount());
      		          }
        	      }
        	}
            return mol;

		} catch (CDKException x) {
			throw new AmbitException(x);
		}
	}

}
