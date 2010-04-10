package ambit2.core.test.io;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.Iterator;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.smiles.SmilesParser;

public class TestCML 
{   protected SmilesParser parser;

    @Before
        public void setUp() throws Exception {
              parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        }
		
    protected String writeCML(IMolecule mol) throws Exception {
        StringWriter output = new StringWriter();           
        CMLWriter cmlwriter = new CMLWriter(output);
        cmlwriter.write(mol);
        cmlwriter.close();
        return output.toString();        
    }
    @Test
    public void testCML_twoPropertiesForAllAtoms() throws Exception
    {       
        final String prop1 = "PropFirst_ID";
        final String prop2 = "PropSecond_ID";
        String smiles = "CC";
        
        IMolecule mol = parser.parseSmiles(smiles);
        for (int i=0; i < mol.getAtomCount(); i++) {
            String id = Integer.toString(i+1);
            mol.getAtom(i).setProperty(prop1+id,id);
            mol.getAtom(i).setProperty(prop2+id,id);
            mol.getAtom(i).setID(id);
        }

        for (int i=0; i < mol.getAtomCount(); i++) {
            String id = Integer.toString(i+1);
            Assert.assertEquals(id,mol.getAtom(i).getProperty(prop2+id));
            Assert.assertEquals(id,mol.getAtom(i).getProperty(prop2+id));
            Assert.assertEquals(2,mol.getAtom(i).getProperties().size());
        }
        
        String cmlcode = writeCML(mol);
        System.out.println(cmlcode);
            
        IChemFile chemFile = parseCMLString(cmlcode);           
        IMolecule mol2 = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
        System.out.println(writeCML(mol2));
        
        for (int i=0; i < mol2.getAtomCount(); i++) {
            String id = mol2.getAtom(i).getID();
            Assert.assertEquals(id,mol2.getAtom(i).getProperty(prop1+id));
            Assert.assertEquals(id,mol2.getAtom(i).getProperty(prop2+id));
            Assert.assertEquals(2,mol2.getAtom(i).getProperties().size());
        }
        
        
            
    }       
    @Test
    public void testCML_singlePropertyForAllAtoms() throws Exception
    {       
        String smiles = "CCCC";
        IMolecule mol = parser.parseSmiles(smiles);
        for (int i=0; i < mol.getAtomCount(); i++) {
            String id = Integer.toString(i+1);
            mol.getAtom(i).setProperty("Prop"+id,id);
            mol.getAtom(i).setID(id);
        }

        printAtomProperties(mol);
        for (int i=0; i < mol.getAtomCount(); i++) {
            String id = Integer.toString(i+1);
            Assert.assertEquals(id,mol.getAtom(i).getProperty("Prop"+id));
            Assert.assertEquals(1,mol.getAtom(i).getProperties().size());
        }
        
        String cmlcode = writeCML(mol);
        System.out.println(cmlcode);
            
        IChemFile chemFile = parseCMLString(cmlcode);           
        IMolecule mol2 = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
        printAtomProperties(mol2);
        for (int i=0; i < mol2.getAtomCount(); i++) {
            String id = mol2.getAtom(i).getID();
            Assert.assertEquals(id,mol2.getAtom(i).getProperty("Prop"+id));
            Assert.assertEquals(1,mol2.getAtom(i).getProperties().size());
        }
        
        System.out.println(writeCML(mol2));
            
    }       
    /**
     * CDK bug closed, atom aromatic flag still not survice roundtrip
     * https://sourceforge.net/tracker/?func=detail&aid=1709130&group_id=20024&atid=120024
     * @throws Exception
     */
	@Test
	public void testCML_AromaticFlag() throws Exception 
	{		
	    String smiles = "c1ccccc1";
	    IMolecule mol = parser.parseSmiles(smiles);		
		for (IBond bond : mol.bonds())
	        Assert.assertTrue(bond.getFlag(CDKConstants.ISAROMATIC));	    
		for (IAtom atom : mol.atoms())
                Assert.assertTrue(atom.getFlag(CDKConstants.ISAROMATIC));

		String cmlcode = writeCML(mol);
			
		IChemFile chemFile = parseCMLString(cmlcode);			
		IMolecule mol2 = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
		printAromaticity(mol2);
		System.out.println(cmlcode);
		for (IBond bond : mol2.bonds())
	        Assert.assertTrue(bond.getFlag(CDKConstants.ISAROMATIC));		
		for (IAtom atom : mol2.atoms())
		        Assert.assertTrue(atom.getFlag(CDKConstants.ISAROMATIC));
	      
	}	
	
	public static void printAtomProperties(IMolecule mol) {
	    Iterator<IAtom> atoms = mol.atoms().iterator();
	    while (atoms.hasNext()) {
	        
	        printAtomProperties(atoms.next());
	    }
	    
	}
	public static void printAtomProperties(IAtom atom)
	{	
	    Iterator keys = atom.getProperties().keySet().iterator();
	    while (keys.hasNext()) {
	        Object key = keys.next();
			System.out.println(atom.getID() + " " + key.toString()+" = "+ atom.getProperties().get(key));
	    }
	}
	
	public static void printAromaticity(IMolecule mol)
	{	
		for (int i = 0; i < mol.getAtomCount(); i++)
		{
			IAtom atom = mol.getAtom(i);
			System.out.println("Atom " + i + "  aromatic = " +atom.getFlag(CDKConstants.ISAROMATIC));
		}
		
		for (int i = 0; i < mol.getBondCount(); i++)
		{
			IBond bond = mol.getBond(i);
			System.out.println("Bond " + i + "  aromatic = " +bond.getFlag(CDKConstants.ISAROMATIC));
		}
	}
	
	public static IChemFile parseCMLString(String cmlString) throws Exception 
	{
        IChemFile chemFile = null;
        CMLReader reader = new CMLReader(new ByteArrayInputStream(cmlString.getBytes()));
        chemFile = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());
        return chemFile;
    }

}
