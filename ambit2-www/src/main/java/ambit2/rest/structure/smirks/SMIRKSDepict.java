package ambit2.rest.structure.smirks;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Vector;

import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.rest.structure.diagram.CDKDepict;
import ambit2.smarts.IAcceptable;
import ambit2.smarts.SMIRKSManager;
import ambit2.smarts.SMIRKSReaction;
import ambit2.smarts.SmartsConst;

public class SMIRKSDepict extends CDKDepict implements IAcceptable {
	public static final String resource = "/reaction/product";
	protected ArrayList<String> list = new ArrayList<String>();
	@Override
	protected BufferedImage getImage(String smiles,int w, int h) throws ResourceException {
		try {
			depict.setImageSize(new Dimension(w,h));

			SMIRKSManager smrkMan = new SMIRKSManager();
			smrkMan.setSSMode(SmartsConst.SSM_NON_IDENTICAL_FIRST);
			
			SMIRKSReaction smr = smrkMan.parse(getSmirks());
			System.out.println(smrkMan.getErrors());
			SmilesParser parser = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());
			IAtomContainer reactant = parser.parseSmiles(smiles);
			reactant = AtomContainerManipulator.removeHydrogens(reactant);	
			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(reactant);
			CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(NoNotificationChemObjectBuilder.getInstance());
			CDKHueckelAromaticityDetector.detectAromaticity(reactant); 
			adder.addImplicitHydrogens(reactant);
			AtomContainerManipulator.convertImplicitToExplicitHydrogens(reactant);
			BufferedImage image = null;
			if (smrkMan.applyTransformation(reactant, this, smr))
			    image = depict.getImage(reactant,null,true,false);
			else {
				list.add("Not applicable");
				image = depict.getImage(list);
			}
			smr = null;
			smrkMan = null;
			return image;

		} catch (ResourceException x) {throw x; 
		} catch (Exception x) { 
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x); 
		}
	}

	@Override
	public boolean accept(Vector<IAtom> atoms) {
		return true;
	}
}
