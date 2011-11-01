/* SmilesTest.java
 * Author: nina
 * Date: Apr 7, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  
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

package ambit2.core.test;


import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.DeduceBondSystemTool;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.core.data.MoleculeTools;
import ambit2.core.io.FileInputState;
import ambit2.core.test.io.RawIteratingWrapperTest;

public class SmilesTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
    //String[] smiles = {"NCCO","OCCN"};
	@Test
	public void testCanonicalSmiles() throws Exception  {

	    String[] smiles = {"c1cc(ccc1C(c2ccc(cc2)Cl)=C(Cl)Cl)Cl",
	            "Clc1ccc(cc1)C(=C(Cl)Cl)c2ccc(Cl)cc2",
	            "C(=C(Cl)Cl)(C1C=CC(=CC=1)Cl)C2=CC=C(C=C2)Cl" 
	            };
	    String[] newSmiles = {"","",""};
	    SmilesGenerator gen = new SmilesGenerator(true);
	    gen.setUseAromaticityFlag(true);
	    IMolecule m = getMolecule();
	    String m_smiles = gen.createSMILES(m);
	    Assert.assertFalse("".equals(m_smiles));
		SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
		
		int count_differences = 0;
	    for (int i=0; i < smiles.length;i++) {
				IMolecule mol = parser.parseSmiles(smiles[i]);
				Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(m,mol));
				newSmiles[i] = gen.createSMILES(mol);
				
				if (!newSmiles[i].equals(m_smiles)) {
					count_differences++;
					System.out.print(m_smiles);
					System.out.print('\t');
					System.out.println(newSmiles[i]);
				}
	    }
	    Assert.assertEquals(0,count_differences);
	}	
	public IMolecule getMolecule() {
	    
	    String sdf = 

		"\n\n\n"+ 

		 "18 19  0  0  0  0  0  0  0  0  1 V2000\n"+
		 "    4.6054   -1.9895    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		 "    4.6054   -0.6632    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		 "    3.4540   -0.0000    0.0000 Cl  0  0  0  0  0  0  0  0  0  0  0  0\n"+
		 "    5.7567   -0.0000    0.0000 Cl  0  0  0  0  0  0  0  0  0  0  0  0\n"+
		 "    5.7567   -2.6527    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		 "    6.9080   -1.9895    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		 "    8.0594   -2.6527    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		 "    8.0594   -3.9882    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		 "    6.9080   -4.6514    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		 "    5.7567   -3.9882    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		 "    9.2107   -4.6514    0.0000 Cl  0  0  0  0  0  0  0  0  0  0  0  0\n"+
		 "    3.4540   -2.6527    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		 "    3.4540   -3.9882    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		 "    2.3027   -4.6514    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		 "    1.1513   -3.9882    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		 "    1.1513   -2.6527    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		 "    2.3027   -1.9895    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		 "    0.0000   -4.6514    0.0000 Cl  0  0  0  0  0  0  0  0  0  0  0  0\n"+
		 "  1  2  2  0  0  0  0\n"+
		 "  1  5  1  0  0  0  0\n"+
		 "  1 12  1  0  0  0  0\n"+
		 "  2  3  1  0  0  0  0\n"+
		 "  2  4  1  0  0  0  0\n"+
		 "  5  6  1  0  0  0  0\n"+
		 "  5 10  2  0  0  0  0\n"+
		 "  6  7  2  0  0  0  0\n"+
		 "  7  8  1  0  0  0  0\n"+
		 "  8  9  2  0  0  0  0\n"+
		 "  8 11  1  0  0  0  0\n"+
		 "  9 10  1  0  0  0  0\n"+
		 " 12 13  2  0  0  0  0\n"+
		 " 12 17  1  0  0  0  0\n"+
		 " 13 14  1  0  0  0  0\n"+
		 " 14 15  2  0  0  0  0\n"+
		 " 15 16  1  0  0  0  0\n"+
		 " 15 18  1  0  0  0  0\n"+
		 " 16 17  2  0  0  0  0\n"+
		"M  END\n" +
		"\n" +
		"$$$$";
	    
	    MDLReader r = new MDLReader(new StringReader(sdf));
	    IMolecule m = MoleculeTools.newMolecule(DefaultChemObjectBuilder.getInstance());
	    try {
	        m = (IMolecule)r.read(m);
	    } catch (CDKException x) {
	        x.printStackTrace();
	    }
	    return m;
	}	
	@Test
	public void testAromaticityRing7() throws Exception {
		SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IMolecule mol = parser.parseSmiles("c1cccccc1");
		for (IAtom atom : mol.atoms())
			Assert.assertTrue(atom.getFlag(CDKConstants.ISAROMATIC));
	}
	
	@Test
	public void testAromaticityRing7a() throws Exception {
		SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IMolecule mol = parser.parseSmiles("c1cccccc1");
		//AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		//for (IAtom atom : mol.atoms())	Assert.assertTrue(atom.getFlag(CDKConstants.HYBRIDIZATION_SP2));		
		DeduceBondSystemTool d = new DeduceBondSystemTool();
		System.out.println(d.isOK(mol));

		for (IBond bond: mol.bonds())
			System.out.println(bond.getOrder());
	}	
	
	@Test
	public void testAromaticityRing6() throws Exception {
		SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IMolecule mol = parser.parseSmiles("c1ccccc1");
		for (IAtom atom : mol.atoms())
			Assert.assertTrue(atom.getFlag(CDKConstants.ISAROMATIC));
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		DeduceBondSystemTool d = new DeduceBondSystemTool();
		d.fixAromaticBondOrders(mol);
		for (IBond bond: mol.bonds())
			System.out.println(bond.getOrder());		
	}	
	
	
	@Test
	public void testHeteroaromaticRing() throws Exception {
		SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IMolecule mol = parser.parseSmiles("Oc1ccc(cc1)c1coc2c(c1=O)c(O)cc(c2)O");
		//AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		//CDKHueckelAromaticityDetector.detectAromaticity(mol);
		int c = 0;
		for (IAtom atom : mol.atoms())
			c += atom.getFlag(CDKConstants.ISAROMATIC)?1:0;
		
		Assert.assertEquals(16,c);
	}
	
	public static void printMol(IMolecule mol) {
		System.out.println(mol.getAtomCount());
		System.out.println(mol.getBondCount());
        for (IAtom atom : mol.atoms())
            if (atom.getFlag(CDKConstants.ISAROMATIC)) System.out.println(atom);
        for (IBond bond : mol.bonds())
            System.out.println(bond.getOrder());
	}
	public static void main(String[] args) {
		testsmiles(args);
		//testsdf(args);
	}
    public static void testsmiles(String[] args) {
        try {
                SmilesParser parser = new
                SmilesParser(SilentChemObjectBuilder.getInstance());
                IMolecule mol =
                	parser.parseSmiles("O=C5C(=NNc1ccc(c2ccccc12)S(=O)(=O)O)C=C(C(=O)C5(=NNc3ccc(c4ccccc34)S(=O)(=O)O))CO");
                printMol(mol);

                //AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
//fails with Cannot percieve atom type for the 17th atom: N
                DeduceBondSystemTool d = new DeduceBondSystemTool();
                
                //d.setTimeout(60*60*1000); //3 min
                d.fixAromaticBondOrders(mol);
                for (IBond bond: mol.bonds())
                        System.out.println(bond.getOrder());
        } catch (Exception x) {
                x.printStackTrace();
        }

    }
    @Test
    public void testChiralSmiles() {
		try {
			IIteratingChemObjectReader reader = FileInputState.getReader(
					RawIteratingWrapperTest.class.getClassLoader().getResourceAsStream(
							"ambit2/core/data/427282-1.sdf")
							,"427282-1.sdf");
			while (reader.hasNext()) {
				IMolecule mol = (IMolecule) reader.next();

				
				AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);  //fails with Cannot percieve atom type for the 17th atom: N
				CDKHueckelAromaticityDetector.detectAromaticity(mol);
				
				printMol(mol);
				int aromatic = 0;
				for (IAtom atom : mol.atoms()) {
					if (atom.getFlag(CDKConstants.ISAROMATIC)) aromatic++;
				}
				for (IBond bond : mol.bonds()) {
					if (bond.getFlag(CDKConstants.ISAROMATIC)) 
						bond.setOrder(Order.SINGLE);
				}
				
				SmilesGenerator g = new SmilesGenerator();
				System.out.println(g.createChiralSMILES(mol, new boolean[mol.getAtomCount()]));
				//[H][C@]14(COP\(=O)(O)O\[C@]4([H])(C\(O)C/(O1)N/2C\3N\C\N\C\(N)C\3(N\C\2Cl)))
			}
			reader.close();
			
		} catch (Exception x) {
			x.printStackTrace();
		}
		
	}
	public static void testsdf(String[] args) {
		try {
			IIteratingChemObjectReader reader = FileInputState.getReader(
					RawIteratingWrapperTest.class.getClassLoader().getResourceAsStream(
							"ambit2/core/data/smiles/250731.sdf")
							,"250731.sdf");
			while (reader.hasNext()) {
				IMolecule mol = (IMolecule) reader.next();

				
				AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);  //fails with Cannot percieve atom type for the 17th atom: N
				CDKHueckelAromaticityDetector.detectAromaticity(mol);
				
				printMol(mol);
				int aromatic = 0;
				for (IAtom atom : mol.atoms()) {
					if (atom.getFlag(CDKConstants.ISAROMATIC)) aromatic++;
				}
				for (IBond bond : mol.bonds()) {
					if (bond.getFlag(CDKConstants.ISAROMATIC)) 
						bond.setOrder(Order.SINGLE);
				}
				/*
				CompoundImageTools img = new CompoundImageTools(new Dimension(400,400));
				BufferedImage image = img.getImage(mol,null,false,true);
				File file = new File("kekuletest.png");
				ImageIO.write(image,"png",file);				
				*/
				DeduceBondSystemTool d = new DeduceBondSystemTool();
				//d.setTimeout(100000000*1000); //100 sec
				d.fixAromaticBondOrders(mol);
				for (IBond bond: mol.bonds())
					System.out.println(bond.getOrder());
			}
			reader.close();
			/*
			//String smiles= "O=C5C(=NNc1ccc(c2ccccc12)S(=O)(=O)O)C=C(C(=O)C5(=NNc3ccc(c4ccccc34)S(=O)(=O)O))CO";
			SmilesParser parser = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());
			IMolecule mol = //parser.parseSmiles("O=C1c2ccccc2C(=O)c3c1ccc4c3[nH]c5c6C(=O)c7ccccc7C(=O)c6c8[nH]c9c%10C(=O)c%11ccccc%11C(=O)c%10ccc9c8c45");
				//parser.parseSmiles("O1c4cccc5ccc3ccc2cccc1c2c3c45");
				//parser.parseSmiles("O=C5C=CC(=NNc1ccc(cc1)c2ccc(cc2)NN=C4C(=O)C=Cc3cc(cc(c34)S(=O)(=O)O)S(=O)(=O)O)C=C5");
				//parser.parseSmiles("c1ccc(cc1)P(c2ccccc2)c3ccccc3");OK
				//parser.parseSmiles("[O+]#[C-][Ru+2][C-]#[O+].c1ccc(cc1)P(c2ccccc2)c3ccccc3.c1ccc(cc1)P(c2ccccc2)c3ccccc3.[Cl-].[Cl-]");
				//parser.parseSmiles("[O+]#[C-][Ru+2][C-]#[O+].c1ccc(cc1)P(c2ccccc2)c3ccccc3.c1ccc(cc1)P(c2ccccc2)c3ccccc3");
				//parser.parseSmiles("O(c1ccccc1)c7cccc(Oc6cccc(Oc5cccc(Oc4cccc(Oc3cccc(Oc2ccccc2)c3)c4)c5)c6)c7"); //OK
				//parser.parseSmiles("c1ccc(cc1)P(c2ccccc2)CCP(CCP(c3ccccc3)c4ccccc4)CCP(c5ccccc5)c6ccccc6"); //OK
				//parser.parseSmiles("O=C3c5c(O)ccc(Nc1ccccc1)c5(C(=O)c4c(O)ccc(Nc2ccccc2)c34)");
				parser.parseSmiles(smiles);
			*/
			
		} catch (Exception x) {
			x.printStackTrace();
		}
		
	}
	@Test
	public void testMCS() throws Exception {
		 SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
	        IAtomContainer mol1 = sp.parseSmiles("c1ccccc1NC");
	        IAtomContainer mol2 = sp.parseSmiles("c1cccnc1");

	        org.openscience.cdk.smsd.Isomorphism mcs = new org.openscience.cdk.smsd.Isomorphism(
	                org.openscience.cdk.smsd.interfaces.Algorithm.DEFAULT, true);
	        mcs.init(mol1, mol2, true, true);
	        mcs.setChemFilters(true, true, true);

	        mol1 = mcs.getReactantMolecule();
	        IMolecule mcsmolecule = DefaultChemObjectBuilder.getInstance().newInstance(IMolecule.class, mol1);
	        List<IAtom> atomsToBeRemoved = new ArrayList<IAtom>();
	        for (IAtom atom : mcsmolecule.atoms())
	        {
	            int index = mcsmolecule.getAtomNumber(atom);
	            if (!mcs.getFirstMapping().containsKey(index))
	                atomsToBeRemoved.add(atom);
	        }

	        for (IAtom atom : atomsToBeRemoved)
	            mcsmolecule.removeAtomAndConnectedElectronContainers(atom);

	        for (int i = 0; i < mcsmolecule.getAtomCount(); i++) {
	            System.out.println("is mcs atom aromtic: " + mcsmolecule.getAtom(i).getFlag(CDKConstants.ISAROMATIC));
	            mcsmolecule.getAtom(i).setFlag(CDKConstants.ISINRING,true);
	        }

	        for (int i = 0; i < mcsmolecule.getBondCount(); i++) {
	            System.out.println("is mcs bond aromtic: " + mcsmolecule.getBond(i).getFlag(CDKConstants.ISAROMATIC));
	            mcsmolecule.getBond(i).setFlag(CDKConstants.ISINRING,true);
	        }    
	        
	        SmilesGenerator g = new SmilesGenerator();
	        g.setUseAromaticityFlag(true);
	        System.out.println("mcs smiles: " + g.createSMILES(mcsmolecule));

	}
}
