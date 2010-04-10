package ambit2.core.external.test;

import java.io.File;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.external.CommandShell;
import ambit2.base.log.AmbitLogger;
import ambit2.core.external.ShellSDFoutput;
import ambit2.core.smiles.OpenBabelShell;

public class CommandShellTest {
	protected CommandShell shell;
	@Before
	public void setUp() throws Exception {
		shell = new ShellSDFoutput() {
			@Override
			protected Object transform(Object mol) {
				return mol;
			}
		};
		AmbitLogger.configureLog4j(true);
		File homeDir = new File(System.getProperty("user.home") +"/.ambit2/*");
		homeDir.delete();
		

	}

	@Test	
	public void testOpenBabel() throws Exception {
		String testfile = "babel_test.sdf";
		OpenBabelShell babel = new OpenBabelShell();
		babel.setOutputFile(testfile);
		IMolecule newmol = babel.runShell("c1ccccc1");

		IAtomContainer c = AtomContainerManipulator.removeHydrogensPreserveMultiplyBonded(newmol);
		
		IMolecule mol = MoleculeFactory.makeBenzene();
		/*
		for (int i=0; i < newmol.getBondCount(); i++) {
			System.out.println(newmol.getBond(i).getOrder());
		}
		*/	
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(mol,c));
		new File(babel.getOutputFile()).delete();
	}	


}

