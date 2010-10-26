/* AtomEnvironmentHammingDistanceTest.java
 * Author: Nina Jeliazkova
 * Date: Mar 13, 2007 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Nina Jeliazkova
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

package ambit2.similarity.measure.test;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.core.config.AmbitCONSTANTS;
import ambit2.core.smiles.SmilesParserWrapper;
import ambit2.descriptors.AtomEnvironment;
import ambit2.descriptors.AtomEnvironmentDescriptor;
import ambit2.descriptors.processors.AtomEnvironmentGenerator;
import ambit2.descriptors.processors.AtomEnvironmentList;
import ambit2.jchempaint.CompoundImageTools;
import ambit2.model.AtomEnvironmentListTableModel;
import ambit2.similarity.measure.AtomEnvironmentsDistance;


/**
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Mar 13, 2007
 * <b>Modified</b> Oct 26, 2010
 */
public class AtomEnvironmentDistanceTest {
	AtomEnvironmentGenerator g;
	@Before
    public void setUp() throws Exception {
        g = new AtomEnvironmentGenerator();
        g.setUseHydrogens(true);
        g.setMaxLevels(3);

    }

    /*
     * Test how AE differ by adding two methyl groups
     *
     */
   
    
    protected AtomEnvironmentList getAE(AtomEnvironmentGenerator g,IMolecule mol) throws Exception {
    	g.process(mol);
        return(AtomEnvironmentList) mol.getProperty(AmbitCONSTANTS.AtomEnvironment);
    	
    }
    @Test
    public void testHellinger() throws Exception {
	    	
	    	AtomEnvironmentList ae1 = getAE(g, MoleculeFactory.makeAlkane(3));
	    	
	    	Assert.assertTrue(ae1.size()> 1);
	    	
	    	AtomEnvironmentList ae = getAE(g, MoleculeFactory.makeAlkane(1));
	    	Assert.assertEquals(2.0, ae.hellinger(ae),1E-6);
	    	Assert.assertEquals(2.0, ae1.hellinger(ae1),1E-6);
	    	//System.out.println(ae);
	    	
	    	//System.out.println(ae1);
	    	
	    	
	    	float f = ae1.hellinger(ae);
	    	System.out.println(f);
	    	Assert.assertEquals(f,ae.hellinger(ae1));
	    	
	    	Assert.assertTrue(f< 2.0);
	    	Assert.assertTrue(f> 0.0);
	    	

    }
     /**
     * Test method for {@link ambit2.similarity.AtomEnvironmentHammingDistance#getDistance(ambit2.data.descriptors.AtomEnvironmentList, ambit2.data.descriptors.AtomEnvironmentList)}.
     */
    @Test
    public void testGetDistance() throws Exception {
        
            SmilesParserWrapper p =  SmilesParserWrapper.getInstance();
            
            IMolecule mol = p.parseSmiles("CCCCCCBr");

            AtomEnvironmentList ae = getAE(g,mol);
            Assert.assertEquals(2.0f,ae.hellinger(ae));

    }
    @Test
    public void testCH3difference() throws Exception {
    	testCH3difference(false);
    }
    public void testCH3difference(boolean display) throws Exception {    	
		IMolecularDescriptor descriptor = new AtomEnvironmentDescriptor();
		int maxLevel = 3;
		Object[] params = {null,new Integer(maxLevel),new Boolean(true),new Boolean(true)};

		    SmilesParserWrapper sp = SmilesParserWrapper.getInstance();
		    
		    //IMolecule mol = sp.parseSmiles("O=N(=O)c1ccc(O)c(N)c1");
		    //Amino-2nitro-3.4-5 hydroxy-methyl benzene (ANMB) 
		    //IMolecule mol1 = sp.parseSmiles("Cc1c(C)c(cc(N)c1(O))N(=O)=O");
		    
		    IMolecule mol = sp.parseSmiles("Nc1c(C)cccc1");
		    //Amino-2nitro-3.4-5 hydroxy-methyl benzene (ANMB) 
		    IMolecule mol1 = sp.parseSmiles("Nc1ccccc1");

		    
		    //Amino-2nitro-3.4-5 hydroxy-methyl benzene (ANMB) 
		    

		    
		    AtomEnvironmentGenerator g = new AtomEnvironmentGenerator();
		    g.setNoDuplicates(false);
		    g.setUseHydrogens(true);
		    g.setMaxLevels(maxLevel);
		   
		    g.process(mol);
		    g.process(mol1);
		    
		    AtomEnvironmentList ae = (AtomEnvironmentList) mol.getProperty(AmbitCONSTANTS.AtomEnvironment);
		    AtomEnvironmentList ae1 = (AtomEnvironmentList) mol1.getProperty(AmbitCONSTANTS.AtomEnvironment);
		    
		    Collections.sort(ae);
		    Collections.sort(ae1);
		    
		    AtomEnvironmentsDistance d = new AtomEnvironmentsDistance();
		    System.out.println("distance " + d.getDistance(ae,ae1));
		    
		    
		    Set<AtomEnvironment> set = new TreeSet<AtomEnvironment>();
		    Set<AtomEnvironment> set1 = new TreeSet<AtomEnvironment>();
		    
		    for (int i=0; i < ae.size(); i++) set.add(ae.get(i));
		    for (int i=0; i < ae1.size(); i++) set1.add(ae1.get(i));
		    
		    System.out.println(set);
		    System.out.println();
		    System.out.println(set1);
		    
		    Assert.assertNotSame(set,set1);
		    Set intersection = (Set) ((TreeSet) set1).clone();
		    intersection.retainAll(set);
		    
		    System.out.println("Intersection\t"+intersection.size());
		    System.out.println(intersection);
		    
		    if (display)
		    	displayAE(mol,ae,mol1,ae1);
		    Assert.assertNotNull(ae);
		    Assert.assertNotNull(ae1);

    }
    
    public void  displayAE(IMolecule mol1, AtomEnvironmentList aelist1,IMolecule mol2, AtomEnvironmentList aelist2) {
    	
    	AEComparator aec = new AEComparator();
    	Collections.sort(aelist1,aec);
    	Collections.sort(aelist2,aec);
    	final AtomEnvironmentListTableModel m1 = new AtomEnvironmentListTableModel(aelist1);
    	final AtomEnvironmentListTableModel m2 = new AtomEnvironmentListTableModel(aelist2);
    	
    	CompoundImageTools tools = new CompoundImageTools();
    	
    	final JLabel p1 = new JLabel();
    	p1.setIcon(new ImageIcon(tools.getImage(mol1)));
    	p1.setPreferredSize(new Dimension(100,100));
    	/*
    	
    	p1.r2dm.setColorAtomsByType(true);
    	p1.r2dm.setHighlightedAtom(mol1.getAtom(0));
    	p1.r2dm.setShowAromaticity(true);
    	p1.setPreferredSize(new Dimension(100,100));

    	*/

    	final JLabel p2 = new JLabel();
    	p2.setIcon(new ImageIcon(tools.getImage(mol2)));
    	p2.setPreferredSize(new Dimension(100,100));
    	
    	final JTable t1 = new JTable(m1);
    	t1.addMouseListener(new MouseAdapter() {
    		@Override
    		public void mouseClicked(MouseEvent e) {
    			int row = t1.rowAtPoint(e.getPoint());
    			int natom = ((Integer)m1.getValueAt(row, 0)).intValue()-1;
    			    		
    			/*
    			p1.setHighlightedAtoms(new int[] {
    					natom }
    					);
    			p1.repaint();
    			*/
    		};
    		
    	});
    	JTable t2 = new JTable(m2);
    	
    	JOptionPane.showMessageDialog(null,
    			new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
    					
    			new JSplitPane(JSplitPane.VERTICAL_SPLIT,
    					p1,
    					new JScrollPane(t1)
    			),
    			new JSplitPane(JSplitPane.VERTICAL_SPLIT,
    					p2,
    					new JScrollPane(t2,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS)
    			)
    			));
    }
    
    class AEComparator implements Comparator<AtomEnvironment> {

    	public int compare(AtomEnvironment o1, AtomEnvironment o2) {
    		int r = o1.getAtomno() - o2.getAtomno();
    		if (r == 0) return o1.getSublevel() - o2.getSublevel();
    		else return r;
    	}

    	
    }
    
    public static void main(String[] args) {
    	AtomEnvironmentDistanceTest test = new AtomEnvironmentDistanceTest();
    	try {
    		test.testCH3difference(true);
    	} catch (Exception x) {
    		x.printStackTrace();
    	}
    }
}
