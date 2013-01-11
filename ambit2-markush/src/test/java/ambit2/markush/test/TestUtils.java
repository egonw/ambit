package ambit2.markush.test;

import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import java.io.InputStream;
import java.io.FileInputStream;

import ambit2.core.data.MoleculeTools;
import ambit2.core.io.MDLV2000ReaderExtended;
import ambit2.core.groups.*;

import ambit2.markush.*;

public class TestUtils 
{
	static String path = "/ambit2-all/ambit2-core/src/test/resources/ambit2/core/data/";
	
	public static void main(String[] args) throws Exception
	{
		//testSuppleAtomFromMLD(path + "mdl/rgroup.mol");
		testSuppleAtomFromMLD(path + "M__RGP/marvin_sketch.mol");
	}
	

	static protected IChemObject readMDLV2000Extended(String file) throws Exception 
	{	
		InputStream in = new FileInputStream(file);
		
		MDLV2000ReaderExtended reader = new MDLV2000ReaderExtended(in, IChemObjectReader.Mode.RELAXED);
		
		IMolecule mol = MoleculeTools.newMolecule(SilentChemObjectBuilder.getInstance());
		IChemObject newMol = reader.read(mol);
		reader.close();
		return newMol;
	}
	
	static void testSuppleAtomFromMLD(String mdlFile) throws Exception
	{
		IChemObject o = readMDLV2000Extended(mdlFile);
		SuppleAtomContainer sac = (SuppleAtomContainer) o;
		
		if (sac == null)
		{
			System.out.println("null object read from file");
			return;
		}
		
		System.out.println(MarkushHelpers.toStringExhaustive(sac));
	}
	

}
