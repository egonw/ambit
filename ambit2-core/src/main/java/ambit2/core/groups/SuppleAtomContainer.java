/* SupleAtomContainer.java
 * Author: Nina Jeliazkova
 * Date: Aug 2, 2008 
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

package ambit2.core.groups;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.ChemObject;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomParity;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.interfaces.IBond.Order;

public class SuppleAtomContainer extends ChemObject implements 
        IMolecule,IChemObjectListener, Serializable, Cloneable, IFiltered<IAtom>{
    /**
     * 
     */
    private static final long serialVersionUID = -3228354237961385902L;
   
    protected FilteredList<IAtom> atoms;
    protected FilteredList<IBond> bonds;
    protected List<ILonePair> lonePairs;
    /**
     *  Internal array of single electrons.
     */
    protected List<ISingleElectron> singleElectrons;

    /**
     * Internal list of atom parities.
     */
    protected Map<IAtom, IAtomParity> atomParities;
    
    public SuppleAtomContainer() {
        super();
        init();
    }
    protected void init() {
        atoms = new FilteredList<IAtom>();
        bonds = new FilteredList<IBond>();
        lonePairs = new ArrayList<ILonePair>();
        singleElectrons = new ArrayList<ISingleElectron>();
        atomParities = new Hashtable<IAtom, IAtomParity>();        
    }
    public void add(IAtomContainer atomContainer) {
		for (int f = 0; f < atomContainer.getAtomCount(); f++)
		{
			if (!contains(atomContainer.getAtom(f)))
			{
				addAtom(atomContainer.getAtom(f));
			}
		}
		for (int f = 0; f < atomContainer.getBondCount(); f++)
		{
			if (!contains(atomContainer.getBond(f)))
			{
				addBond(atomContainer.getBond(f));
			}
		}
		for (int f = 0; f < atomContainer.getLonePairCount(); f++)
		{
			if (!contains(atomContainer.getLonePair(f)))
			{
				addLonePair(atomContainer.getLonePair(f));
			}
		}
		for (int f = 0; f < atomContainer.getSingleElectronCount(); f++)
		{
			if (!contains(atomContainer.getSingleElectron(f)))
			{
				addSingleElectron(atomContainer.getSingleElectron(f));
			}
		}
		notifyChanged();    	
    }

    public void addAtom(IAtom atom) {
        if (contains(atom)) return;
        atom.addListener(this);
        atoms.add(atom);
        addToFilter(atom);
        notifyChanged();        
    }

    public void addAtomParity(IAtomParity parity) {
        atomParities.put(parity.getAtom(), parity);
    }

    public void addBond(IBond bond) {
    	bond.addListener(this);
        bonds.add(bond);
        addToFilter(bond);
        notifyChanged();
        
    }

    public void addBond(int atom1, int atom2, Order order, int stereo) {
        IBond bond = getBuilder().newBond(getAtom(atom1), getAtom(atom2), order, stereo);
        if (contains(bond)) return;
        addBond(bond);
    }

    public void addBond(int atom1, int atom2, Order order) {
        IBond bond = getBuilder().newBond(getAtom(atom1), getAtom(atom2), order);
        if (contains(bond)) return;
        addBond(bond);
    }

    public void addElectronContainer(IElectronContainer electronContainer) {
        if (electronContainer instanceof IBond) this.addBond((IBond)electronContainer);
        if (electronContainer instanceof ILonePair) this.addLonePair((ILonePair)electronContainer);
        if (electronContainer instanceof ISingleElectron) this.addSingleElectron((ISingleElectron)electronContainer);
    }

    public void addLonePair(ILonePair lonePair) {
        lonePairs.add(lonePair);
        notifyChanged();
    }

    public void addLonePair(int atomID) {
        ILonePair lonePair = getBuilder().newLonePair(getAtom(atomID));
        lonePair.addListener(this);
        addLonePair(lonePair);
    }

    public void addSingleElectron(ISingleElectron singleElectron) {
        singleElectrons.add(singleElectron);
        notifyChanged();
    }

    public void addSingleElectron(int atomID) {
        ISingleElectron singleElectron = getBuilder().newSingleElectron(getAtom(atomID));
        singleElectron.addListener(this);
        addSingleElectron(singleElectron);
    }

    public Iterator<IAtom> atoms() {
        return atoms.iterator();
    }

    public Iterator<IBond> bonds() {
        return bonds.iterator();
    }

    public boolean contains(IAtom atom) {
        return atoms.contains(atom);
    }

    public boolean contains(IBond bond) {
        return bonds.contains(bond);
    }

    public boolean contains(ILonePair lonePair) {
        return contains(lonePair.getAtom()) && lonePairs.contains(lonePair);
    }

    public boolean contains(ISingleElectron singleElectron) {
        return contains(singleElectron.getAtom()) && singleElectrons.contains(singleElectron);
    }

    public boolean contains(IElectronContainer electronContainer) {
        if (electronContainer instanceof IBond) return contains((IBond)electronContainer);
        if (electronContainer instanceof ILonePair) return contains((ILonePair)electronContainer);
        if (electronContainer instanceof ISingleElectron) return contains((ISingleElectron)electronContainer);
        return false;

    }

    public Iterator<IElectronContainer> electronContainers()
    {
        return new ElectronContainerIterator();
    }
    
    /**
     * The inner ElectronContainerIterator class.
     *
    */ 
    private class ElectronContainerIterator implements Iterator<IElectronContainer> {

    	private int pointer = 0;
        public boolean hasNext() {
            return pointer < (bonds.size() + lonePairs.size() + singleElectrons.size());
        }

        public IElectronContainer next() {
            if (pointer < bonds.size()) return bonds.get(pointer++);
            else if (pointer < bonds.size()+lonePairs.size()) return lonePairs.get((pointer++)-bonds.size());
            else if (pointer < bonds.size()+lonePairs.size()+singleElectrons.size()) 
            	return singleElectrons.get((pointer++)-bonds.size()-lonePairs.size());
            return null;
        }

        public void remove() {
            if (pointer <= bonds.size()) removeBond(--pointer);
            else if (pointer <= bonds.size()+lonePairs.size()) removeLonePair((--pointer)-bonds.size());
            else if (pointer <= bonds.size()+lonePairs.size()+singleElectrons.size()) 
            	removeSingleElectron((--pointer)- bonds.size()-lonePairs.size());
        }
    }

    public IAtom getAtom(int index) {
        return atoms.get(index);
    }

    public int getAtomCount() {
        return atoms.size();
    }

    public int getAtomNumber(IAtom atom) {
        return atoms.getNumber(atom);
	}    

    public IAtomParity getAtomParity(IAtom atom) {
        return atomParities.get(atom);
    }

    public IBond getBond(int arg0) {
        return bonds.get(arg0);
    }

    public IBond getBond(IAtom atom1, IAtom atom2) {
        Iterator<IBond> i = bonds();
        while (i.hasNext()) {
            IBond bond = i.next();
            if (bond.contains(atom1) &&
                    bond.getConnectedAtom(atom1) == atom2) {
                    return bond;
                }
        }
        return null;

    }

    public int getBondCount() {
        return bonds.size();
    }

    public int getBondNumber(IBond bond) {
        return bonds.getNumber(bond);
    }

    public int getBondNumber(IAtom atom1, IAtom atom2) {
        return (getBondNumber(getBond(atom1, atom2)));
    }

    public double getBondOrderSum(IAtom atom) {
		double count = 0;
		Iterator<IBond> b = bonds.iterator();
		while (b.hasNext())
		{
			IBond bond = b.next();
			if (bond.contains(atom)) {
				if (bond.getOrder() == IBond.Order.SINGLE) {
					count += 1;
				} else if (bond.getOrder() == IBond.Order.DOUBLE) {
					count += 2;
				} else if (bond.getOrder() == IBond.Order.TRIPLE) {
					count += 3;
				} else if (bond.getOrder() == IBond.Order.QUADRUPLE) {
					count += 4;
				}
			}
		}
		return count;
    }

    public int getConnectedAtomsCount(IAtom atom) {
		int count = 0;
		Iterator<IBond> b = bonds.iterator();
		while (b.hasNext()) {
			if (b.next().contains(atom))  ++count;
		}
		return count;

    }

    public List<IAtom> getConnectedAtomsList(IAtom atom) {
		List<IAtom> atomsList = new ArrayList<IAtom>();
		Iterator<IBond> b = bonds.iterator();
		while (b.hasNext()) {
			IBond bond = b.next();
			if (bond.contains(atom)) atomsList.add(bond.getConnectedAtom(atom));
		}
		return atomsList;

    }

    public int getConnectedBondsCount(IAtom atom) {
    	return getConnectedAtomsCount(atom);
    }

    public int getConnectedBondsCount(int atomNumber) {
    	return getConnectedAtomsCount(getAtom(atomNumber));
    }

    public List<IBond> getConnectedBondsList(IAtom atom) {
		List<IBond> bondsList = new ArrayList<IBond>();
		Iterator<IBond> b = bonds.iterator();
		while (b.hasNext()) {
			IBond bond = b.next();
			if (bond.contains(atom)) bondsList.add(bond);
		}
		return bondsList;
    }

    public List<IElectronContainer> getConnectedElectronContainersList(
            IAtom atom) {
		List<IElectronContainer> lps = new ArrayList<IElectronContainer>();
		Iterator<IBond> b = bonds.iterator();
		while (b.hasNext()) {
			IBond bond = b.next();
			if (bond.contains(atom)) lps.add(bond);
		}
		Iterator<ILonePair> lp = lonePairs.iterator();
		while (lp.hasNext()) {
			ILonePair p = lp.next();
			if (p.contains(atom))  lps.add(p);
		}		
		for (int i = 0; i < getSingleElectronCount(); i++)
		{
			if (singleElectrons.get(i).contains(atom)) lps.add(singleElectrons.get(i));
		}
		return lps;
    }

    public int getConnectedLonePairsCount(IAtom atom) {
		int count = 0;
		Iterator<ILonePair> lp = lonePairs.iterator();
		while (lp.hasNext()) {
			if (lp.next().contains(atom)) ++count;
		}
		return count;

    }

    public List<ILonePair> getConnectedLonePairsList(IAtom atom) {
        List<ILonePair> lps = new ArrayList<ILonePair>();
		Iterator<ILonePair> lp = lonePairs.iterator();
		while (lp.hasNext()) {
			ILonePair p = lp.next();
			if (p.contains(atom))  lps.add(p);
		}		
        return lps;

    }

    public int getConnectedSingleElectronsCount(IAtom atom) {
		int count = 0;
		Iterator<ISingleElectron> i = singleElectrons();
		while (i.hasNext())	{
			ISingleElectron se = i.next();
			if (se.contains(atom)) ++count;
		}
		return count;

    }

    public List<ISingleElectron> getConnectedSingleElectronsList(IAtom atom) {
		List<ISingleElectron> lps = new ArrayList<ISingleElectron>();
		Iterator<ISingleElectron> i = singleElectrons();
		while (i.hasNext())	{
			ISingleElectron se = i.next();
			if (se.contains(atom)) lps.add(se);
		}

		return lps;

    }

    public IElectronContainer getElectronContainer(int number) {
		if (number < bonds.size()) return bonds.get(number);
		number -= bonds.size();
		if (number < lonePairs.size()) return lonePairs.get(number);
		number -= lonePairs.size();
		if (number < singleElectrons.size()) return singleElectrons.get(number);
		return null;
    }

    public int getElectronContainerCount() {
    	return bonds.size() + lonePairs.size() + singleElectrons.size();
    }

    public IAtom getFirstAtom() {
    	return atoms.get(0);
    }

    public IAtom getLastAtom() {
    	return getAtomCount() > 0 ? atoms.get(getAtomCount() - 1) : null;
    }

    public ILonePair getLonePair(int number) {
        return lonePairs.get(number);
    }

    public int getLonePairCount() {
        return lonePairs.size();
    }

    public int getLonePairNumber(ILonePair lonePair) {
		for (int f = 0; f < lonePairs.size(); f++)
		{
			if (lonePairs.get(f) == lonePair) return f;
		}
		return -1;

    }

    public Order getMaximumBondOrder(IAtom atom) {
		IBond.Order max = IBond.Order.SINGLE;
		Iterator<IBond> b = bonds.iterator();
		while (b.hasNext()) {
			IBond bond = b.next();
			if (bond.contains(atom) && 
				bond.getOrder().ordinal() > max.ordinal()) {
				max = bond.getOrder();
			}
		}
		return max;
    }

    public Order getMinimumBondOrder(IAtom atom) {
		IBond.Order min = IBond.Order.QUADRUPLE;
		Iterator<IBond> b = bonds.iterator();
		while (b.hasNext()) {
			IBond bond = b.next();
			if (bond.contains(atom) && 
				bond.getOrder().ordinal() < min.ordinal()) {
				min = bond.getOrder();
			}
		}
		return min;
    }

    public ISingleElectron getSingleElectron(int index) {
    	return singleElectrons.get(index);
    }

    public int getSingleElectronCount() {
        return singleElectrons.size();
    }

    public int getSingleElectronNumber(ISingleElectron singleElectron) {
        return singleElectrons.indexOf(singleElectron);
    }

    public Iterator<ILonePair> lonePairs() {
        return lonePairs.iterator();
    }

    public void remove(IAtomContainer atomContainer) {
        Iterator<IAtom> ia = atomContainer.atoms(); 
        while (ia.hasNext()) {
            removeAtom(ia.next());
        }
        Iterator<IBond> ib = atomContainer.bonds(); 
        while (ib.hasNext()) {
            removeBond(ib.next());
        }        
        Iterator<ILonePair> ilp = atomContainer.lonePairs(); 
        while (ilp.hasNext()) {
            removeLonePair(ilp.next());
        }
        Iterator<ISingleElectron> ise = atomContainer.singleElectrons(); 
        while (ise.hasNext()) {
            removeSingleElectron(ise.next());
        }                
    }

    public void removeAllBonds() {
        Iterator<IBond> ib = bonds(); 
        while (ib.hasNext()) {
            ib.next().removeListener(this);
        }
        bonds.clear();
        notifyChanged();

    }

    public void removeAllElectronContainers() {
        removeAllBonds();
        Iterator<ILonePair> lp = lonePairs();
        while (lp.hasNext())
            lp.next().removeListener(this);    
        Iterator<ISingleElectron> se = singleElectrons();
        while (se.hasNext())
            se.next().removeListener(this);
        
        lonePairs.clear();
        singleElectrons.clear();

        notifyChanged();

    }

    public void removeAllElements() {
        removeAllElectronContainers();
        Iterator<IAtom> ia = atoms(); 
        while (ia.hasNext()) {
            ia.next().removeListener(this);
        }
        atoms.clear();
        notifyChanged();
    }

    public void removeAtom(int position) {
        IAtom atom = getAtom(position);
        removeAtom(atom);
    }

    public void removeAtom(IAtom atom) {
        atom.removeListener(this);
        boolean ok = atoms.remove(atom);
        //System.out.println(ok);
    }

    public void removeAtomAndConnectedElectronContainers(IAtom atom) {
		int position = getAtomNumber(atom);
		if (position != -1)
		{
			for (int i = getBondCount()-1; i >=0; i--)
				if (bonds.get(i).contains(atom)) 
					removeBond(i);
			
			for (int i = getLonePairCount()-1; i >=0; i--)
				if (lonePairs.get(i).contains(atom)) 
					removeLonePair(i);
			
			for (int i = getSingleElectronCount()-1; i >=0; i--)
				if (singleElectrons.get(i).contains(atom)) 
					removeSingleElectron(i);
			removeAtom(position);
		}
		notifyChanged();

    }

    public IBond removeBond(int position) {
        IBond bond = getBond(position);
        removeBond(bond);
        return bond;
    }

    public void removeBond(IBond bond) {
        bond.removeListener(this);
        bonds.remove(bond);
    }

    public IBond removeBond(IAtom atom1, IAtom atom2) {
        IBond bond = getBond(atom1, atom2);
        removeBond(bond);
        return bond;
    }

    public IElectronContainer removeElectronContainer(int number) {
    	IElectronContainer el = getElectronContainer(number);
    	removeElectronContainer(el);
        /*
        if (number < this.bondCount) return removeBond(number);
        number -= this.bondCount;
        if (number < this.lonePairCount) return removeLonePair(number);
        number -= this.lonePairCount;
        if (number < this.singleElectronCount) return removeSingleElectron(number);
        */
    	return el;
    }

    public void removeElectronContainer(IElectronContainer electronContainer) {
        if (electronContainer instanceof IBond) removeBond((IBond) electronContainer);
        else if (electronContainer instanceof ILonePair) removeLonePair((ILonePair) electronContainer);
        else
        if (electronContainer instanceof ISingleElectron) removeSingleElectron((ISingleElectron) electronContainer);
    }

    public ILonePair removeLonePair(int position) {
        ILonePair lp = lonePairs.get(position);
        lp.removeListener(this);
        lonePairs.remove(lp);
        notifyChanged();
        return lp;
    }

    public void removeLonePair(ILonePair lp) {
        lp.removeListener(this);
        lonePairs.remove(lp);
        notifyChanged();
    }

    public ISingleElectron removeSingleElectron(int position) {
        ISingleElectron se = singleElectrons.get(position);
        se.removeListener(this);
        singleElectrons.remove(position);
        notifyChanged();
        return se;
    }

    public void removeSingleElectron(ISingleElectron se) {
        se.removeListener(this);
        singleElectrons.remove(se);
        notifyChanged();
    }

    public void setAtom(int number, IAtom atom) {
    	while (number >= atoms.size()) {
    		atoms.add(null);
    	}
    	if (number < atoms.size()) {
	        IAtom oldAtom = atoms.get(number);
	        if (oldAtom != null)
	        	oldAtom.removeListener(this);
	        atom.addListener(this);
            atoms.set(number, atom);
	        notifyChanged();
    	} 
    }

    public void setAtoms(IAtom[] newatoms) {
        Iterator<IAtom> ia = atoms(); 
        while (ia.hasNext()) {
            removeAtom(ia.next());
        }        
        for (IAtom atom : newatoms) {
            addAtom(atom);
        }
        notifyChanged();

    }

    public void setBonds(IBond[] newbonds) {
        Iterator<IBond> ib = bonds(); 
        while (ib.hasNext()) {
            removeBond(ib.next());
        }        
        for (IBond bond : newbonds) {
            addBond(bond);
        }
    }

    public Iterator<ISingleElectron> singleElectrons() {
        return singleElectrons.iterator();
    }

     public void stateChanged(IChemObjectChangeEvent event) {
        notifyChanged(event);
        if (event.getSource() instanceof IAtom) {
        	IAtom atom = (IAtom) event.getSource();
        	Object groupVisible = atom.getProperty(ISGroup.SGROUP_VISIBLE);
        	if (groupVisible != null) {
        		if (((Boolean)groupVisible).booleanValue()) 
        			addToFilter(atom);
        		else	
        			removeFromFilter(atom);
        	}
        }
        if (event.getSource() instanceof IBond) {
        	IBond bond = (IBond) event.getSource();
        	Object groupVisible = bond.getProperty(ISGroup.SGROUP_VISIBLE);
        	if (groupVisible != null) {
        		if (((Boolean)groupVisible).booleanValue()) 
        			addToFilter(bond);
        		else	
        			removeFromFilter(bond);
        	}
        }        
    }
    @Override
    public Object clone() throws CloneNotSupportedException {
		IAtom[] newAtoms;
		IAtomContainer clone = (IAtomContainer) super.clone();
        // start from scratch
        if (clone instanceof SuppleAtomContainer)
            ((SuppleAtomContainer) clone).init();
        else
            clone.removeAllElements();
        // clone all atoms
		for (int f = 0; f < getAtomCount(); f++) {
			clone.addAtom((IAtom) getAtom(f).clone());
		}
        // clone bonds
		IBond bond;
		IBond newBond;
		for (int i = 0; i < getBondCount(); ++i) {
			bond = getBond(i);
			newBond = (IBond)bond.clone();
			newAtoms = new IAtom[bond.getAtomCount()];
			for (int j = 0; j < bond.getAtomCount(); ++j) {
				newAtoms[j] = clone.getAtom(getAtomNumber(bond.getAtom(j)));
			}
			newBond.setAtoms(newAtoms);
			clone.addBond(newBond);
		}
		ILonePair lp;
		ILonePair newLp;
		for (int i = 0; i < getLonePairCount(); ++i) {
			lp = getLonePair(i);
			newLp = (ILonePair)lp.clone();
			newLp.setAtom(clone.getAtom(getAtomNumber(lp.getAtom())));
			clone.addLonePair(newLp);
		}
		ISingleElectron se;
		ISingleElectron newSe;
		for (int i = 0; i < getSingleElectronCount(); ++i) {
			se = getSingleElectron(i);
			newSe = (ISingleElectron)se.clone();
			newSe.setAtom(clone.getAtom(getAtomNumber(se.getAtom())));
			clone.addSingleElectron(newSe);
		}

		return clone;
    }
    public void addToFilter(IAtom atom) {
        atoms.addToFilter(atom);
        
    }
    public void addToFilter(IBond bond) {
        bonds.addToFilter(bond);
        //System.out.println("add "+bond);
    }
    
    public void clearFilter() {
        atoms.clearFilter();
        bonds.clearFilter();
        
    }
    public boolean isVisible(IAtom atom) {
        return atoms.isVisible(atom);
    }
    public boolean isFiltered(IAtom atom) {
        return atoms.isFiltered(atom);
    }
    public boolean isFiltered(IBond bond) {
        return bonds.isFiltered(bond);
    }    
    public void removeFromFilter(IAtom atom) {
        atoms.removeFromFilter(atom);
    }
    public void removeFromFilter(IBond bond) {
        bonds.removeFromFilter(bond);
        //System.out.println("remove "+bond);
    }    
    public boolean isFiltered() {
        return atoms.isFiltered();
    }
    public void setFiltered(boolean filtered) {
        atoms.setFiltered(filtered);
        bonds.setFiltered(filtered);
    }
    /*    
    public void addSGroup(ISGroup group) {
        setFiltered(false);
        atoms.add(group);
    }
    
    public ISGroup getGroupByNumber(int number) {
        for (int i=0; i < atoms.size(); i++)
            if (getAtom(i) instanceof ISGroup)
                if (((ISGroup)getAtom(i)).getNumber()==number) return (ISGroup)getAtom(i);
        return null;
    }
    */    
    public void addGroupAtom(ISGroup group, IAtom atom) {
        group.addAtom(atom);
    }
    public void addGroupBond(ISGroup group, IBond bond) {
        IBond newbond = group.addBond(bond);
        setFiltered(false);
        if (!bonds.contains(newbond))
            addBond(newbond);
    }    
}

