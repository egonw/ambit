/*  $Revision: 9684 $ $Author: egonw $ $Date: 2007-12-28 23:35:34 +0200 (петък, 28 Декември 2007) $
 *
 *  Copyright (C) 1997-2007  Christoph Steinbeck <steinbeck@users.sourceforge.net>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package ambit2.core.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.io.DefaultChemObjectReader;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.MDLV2000Format;
import org.openscience.cdk.io.setting.BooleanIOSetting;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.core.groups.ISGroup;
import ambit2.core.groups.SuppleAtomContainer;

/**
 * Supports SGroups.
 * Reads a molecule from an MDL MOL or SDF file {cdk.cite DAL92}. An SD files
 * is read into a ChemSequence of ChemModel's. Each ChemModel will contain one
 * Molecule.
 *
 * <p>From the Atom block it reads atomic coordinates, element types and
 * formal charges. From the Bond block it reads the bonds and the orders.
 * Additionally, it reads 'M  CHG', 'G  ', 'M  RAD' and 'M  ISO' lines from the
 * property block.
 *
 * <p>If all z coordinates are 0.0, then the xy coordinates are taken as
 * 2D, otherwise the coordinates are read as 3D.
 *
 * <p>The title of the MOL file is read and can be retrieved with:
 * <pre>
 *   molecule.getProperty(CDKConstants.TITLE);
 * </pre>
 *
 * RGroups which are saved in the mdl file as R#, are renamed according to their appearance,
 * e.g. the first R# is named R1. With PseudAtom.getLabel() "R1" is returned (instead of R#).
 * This is introduced due to the SAR table generation procedure of Scitegics PipelinePilot.  
 *
 * cdk.module io
 * cdk.svnrev  $Revision: 9684 $
 *
 * @author     steinbeck
 * @author     Egon Willighagen
 * cdk.created    2000-10-02
 * cdk.keyword    file format, MDL molfile
 * cdk.keyword    file format, SDF
 * cdk.bug        1587283
 */
public class MDLV2000ReaderExtended extends DefaultChemObjectReader {
	
    BufferedReader input = null;
    private LoggingTool logger = null;

    private BooleanIOSetting forceReadAs3DCoords;
    
    public MDLV2000ReaderExtended() {
        this(new StringReader(""));
    }
    
	/**
	 *  Contructs a new MDLReader that can read Molecule from a given InputStream.
	 *
	 *@param  in  The InputStream to read from
	 */
	public MDLV2000ReaderExtended(InputStream in) {
		this(new InputStreamReader(in));
	}
	public MDLV2000ReaderExtended(InputStream in, Mode mode) {
		this(new InputStreamReader(in), mode);
	}

	/**
	 *  Contructs a new MDLReader that can read Molecule from a given Reader.
	 *
	 *@param  in  The Reader to read from
	 */
	public MDLV2000ReaderExtended(Reader in) {
        this(in, Mode.RELAXED);
	}
	public MDLV2000ReaderExtended(Reader in, Mode mode) {
        logger = new LoggingTool(this);
        input = new BufferedReader(in);
        initIOSettings();
        super.mode = mode;
	}

	public IResourceFormat getFormat() {
        return MDLV2000Format.getInstance();
    }

    public void setReader(Reader input) throws CDKException {
        if (input instanceof BufferedReader) {
            this.input = (BufferedReader)input;
        } else {
            this.input = new BufferedReader(input);
        }
    }

    public void setReader(InputStream input) throws CDKException {
        setReader(new InputStreamReader(input));
    }

	public boolean accepts(Class classObject) {
		Class[] interfaces = classObject.getInterfaces();
		for (int i=0; i<interfaces.length; i++) {
			if (IChemFile.class.equals(interfaces[i])) return true;
			if (IChemModel.class.equals(interfaces[i])) return true;
			if (IMolecule.class.equals(interfaces[i])) return true;
		}
		return false;
	}

	/**
	 *  Takes an object which subclasses IChemObject, e.g. Molecule, and will read
	 *  this (from file, database, internet etc). If the specific implementation
	 *  does not support a specific IChemObject it will throw an Exception.
	 *
	 *@param  object                              The object that subclasses
	 *      IChemObject
	 *@return                                     The IChemObject read
	 *@exception  CDKException
	 */
	public IChemObject read(IChemObject object) throws CDKException {
		if (object instanceof IChemFile) {
			return readChemFile((IChemFile)object);
        } else if (object instanceof IChemModel) {
            return readChemModel((IChemModel)object);
		} else if (object instanceof IMolecule) {
			return readMolecule((IMolecule)object);
		} else {
			throw new CDKException("Only supported are ChemFile and Molecule.");
		}
	}

    private IChemModel readChemModel(IChemModel chemModel) throws CDKException {
    	IMoleculeSet setOfMolecules = chemModel.getMoleculeSet();
        if (setOfMolecules == null) {
            setOfMolecules = chemModel.getBuilder().newMoleculeSet();
        }
        IMolecule m = readMolecule(chemModel.getBuilder().newMolecule());
		if (m != null) {
			setOfMolecules.addMolecule(m);
		}
        chemModel.setMoleculeSet(setOfMolecules);
        return chemModel;
    }

	/**
	 * Read a ChemFile from a file in MDL SDF format.
	 *
	 * @return    The ChemFile that was read from the MDL file.
	 */
    private IChemFile readChemFile(IChemFile chemFile) throws CDKException {
        IChemSequence chemSequence = chemFile.getBuilder().newChemSequence();
        
        IChemModel chemModel = chemFile.getBuilder().newChemModel();
		IMoleculeSet setOfMolecules = chemFile.getBuilder().newMoleculeSet();
		IMolecule m = readMolecule(chemFile.getBuilder().newMolecule());
		if (m != null) {
			setOfMolecules.addMolecule(m);
		}
        chemModel.setMoleculeSet(setOfMolecules);
        chemSequence.addChemModel(chemModel);
        
        setOfMolecules = chemFile.getBuilder().newMoleculeSet();
        chemModel = chemFile.getBuilder().newChemModel();
		String str;
        try {
            String line;
            while ((line = input.readLine()) != null) {
                logger.debug("line: ", line);
                // apparently, this is a SDF file, continue with 
                // reading mol files
		str = new String(line);
		if (str.equals("$$$$")) {
		    m = readMolecule(chemFile.getBuilder().newMolecule());
		    
		    if (m != null) {
			setOfMolecules.addMolecule(m);
			
			chemModel.setMoleculeSet(setOfMolecules);
			chemSequence.addChemModel(chemModel);
			
			setOfMolecules = chemFile.getBuilder().newMoleculeSet();
			chemModel = chemFile.getBuilder().newChemModel();
			
		    }
		} else {
		    // here the stuff between 'M  END' and '$$$$'
		    if (m != null) {
			// ok, the first lines should start with '>'
			String fieldName = null;
			if (str.startsWith("> ")) {
			    // ok, should extract the field name
			    str.substring(2); // String content = 
			    int index = str.indexOf("<");
			    if (index != -1) {
				int index2 = str.substring(index).indexOf(">");
				if (index2 != -1) {
				    fieldName = str.substring(
							      index+1,
							      index+index2
							      );
				}
			    }
			    // end skip all other lines
			    while ((line = input.readLine()) != null && line.startsWith(">")) {
                    logger.debug("data header line: ", line);
			    }
			}
            if (line == null) {
                throw new CDKException("Expecting data line here, but found null!");
            }
			String data = line;
			while ((line = input.readLine()) != null &&
			       line.trim().length() > 0) {
                if (line.equals("$$$$")) {
                	logger.error("Expecting data line here, but found end of molecule: ", line);
                	break;
                }
                logger.debug("data line: ", line);
			    data += line;
			    // preserve newlines, unless the line is exactly 80 chars; in that case it
			    // is assumed to continue on the next line. See MDL documentation.
			    if (line.length() < 80) data += "\n";
			}
			if (fieldName != null) {
			    logger.info("fieldName, data: ", fieldName, ", ", data);
			    m.setProperty(fieldName, data);
			}
		    }
		}
            }
        } catch (CDKException cdkexc) {
            throw cdkexc;
        } catch (Exception exception) {
            String error = "Error while parsing SDF";
            logger.error(error);
            logger.debug(exception);
            throw new CDKException(error, exception);
        }
		try {
			input.close();
		} catch (Exception exc) {
            String error = "Error while closing file: " + exc.getMessage();
            logger.error(error);
			throw new CDKException(error, exc);
		}

        chemFile.addChemSequence(chemSequence);
		return chemFile;
	}



	/**
	 *  Read a Molecule from a file in MDL sd format
	 *
	 *@return    The Molecule that was read from the MDL file.
	 */
	private IMolecule readMolecule(IMolecule molecule) throws CDKException {
        logger.debug("Reading new molecule");
        int linecount = 0;
        int atoms = 0;
        int bonds = 0;
        int atom1 = 0;
        int atom2 = 0;
        int order = 0;
        int stereo = 0;
        int RGroupCounter=1;
        int Rnumber=0;
        String [] rGroup=null;
        double x = 0.0;
        double y = 0.0;
        double z = 0.0;
        double totalZ = 0.0;
        //int[][] conMat = new int[0][0];
        //String help;
        IAtom atom;
        String line = "";
        
        Hashtable<Integer,ISGroup> sgroups = null;
        try {
        	IsotopeFactory isotopeFactory = IsotopeFactory.getInstance(molecule.getBuilder());
        	
            logger.info("Reading header");
            line = input.readLine(); linecount++;
            if (line == null) {
                return null;
            }
            logger.debug("Line " + linecount + ": " + line);

            if (line.startsWith("$$$$")) {
                logger.debug("File is empty, returning empty molecule");
                return molecule;
            }
            if (line.length() > 0) {
                molecule.setProperty(CDKConstants.TITLE, line);
            }
            line = input.readLine(); linecount++;
            logger.debug("Line " + linecount + ": " + line);
            line = input.readLine(); linecount++;
            logger.debug("Line " + linecount + ": " + line);
            if (line.length() > 0) {
                molecule.setProperty(CDKConstants.REMARK, line);
            }
            
            logger.info("Reading rest of file");
            line = input.readLine(); linecount++;
            logger.debug("Line " + linecount + ": " + line);
            if (mode == Mode.STRICT) {
            	if (line.contains("V3000") || line.contains("v3000")) {
            		throw new CDKException("This file must be read with the MDLV3000Reader.");
            	} else if (!line.contains("V2000") && !line.contains("v2000")) {
            		throw new CDKException("This file must be read with the MDLReader.");
            	}
            }
            atoms = Integer.valueOf(line.substring(0,3).trim()).intValue();
            logger.debug("Atomcount: " + atoms);
            bonds = Integer.valueOf(line.substring(3,6).trim()).intValue();
            logger.debug("Bondcount: " + bonds);
            
            // read ATOM block
            logger.info("Reading atom block");
            for (int f = 0; f < atoms; f++) {
                line = input.readLine(); linecount++;
                x = new Double(line.substring( 0,10).trim()).doubleValue();
                y = new Double(line.substring(10,20).trim()).doubleValue();
                z = new Double(line.substring(20,30).trim()).doubleValue();
                totalZ += Math.abs(z); // *all* values should be zero, not just the sum
                logger.debug("Coordinates: " + x + "; " + y + "; " + z);
                String element = line.substring(31,34).trim();

                logger.debug("Atom type: ", element);
                if (isotopeFactory.isElement(element)) {
                    atom = isotopeFactory.configure(molecule.getBuilder().newAtom(element));
                } else if ("A".equals(element)) {
                	atom = molecule.getBuilder().newPseudoAtom(element);
                } else if ("Q".equals(element)) {
                	atom = molecule.getBuilder().newPseudoAtom(element);
                } else if ("*".equals(element)) {
                	atom = molecule.getBuilder().newPseudoAtom(element);
                } else if ("LP".equals(element)) {
                	atom = molecule.getBuilder().newPseudoAtom(element);
                } else if ("L".equals(element)) {
                	atom = molecule.getBuilder().newPseudoAtom(element);
                } else if (element.length() > 0 && element.charAt(0) == 'R'){
                	logger.debug("Atom ", element, " is not an regular element. Creating a PseudoAtom.");
                    //check if the element is R
                    rGroup=element.split("^R");
                    if (rGroup.length >1){
                    	try{
                    		Rnumber=new Integer(rGroup[(rGroup.length-1)]).intValue();
                    		RGroupCounter=Rnumber;
                    	}catch(Exception ex){
                    		Rnumber=RGroupCounter;
                    		RGroupCounter++;
                    	}
                    	element="R"+Rnumber;
                    }
                    atom = molecule.getBuilder().newPseudoAtom(element);
                } else {
                	if (mode == IChemObjectReader.Mode.STRICT) {
                		throw new CDKException("Invalid element type. Must be an existing element, or one in: A, Q, L, LP, *.");
                	}
                	atom = molecule.getBuilder().newPseudoAtom(element);
                }

                // store as 3D for now, convert to 2D (if totalZ == 0.0) later
                atom.setPoint3d(new Point3d(x, y, z));
                
                // parse further fields
                String massDiffString = line.substring(34,36).trim();
                logger.debug("Mass difference: ", massDiffString);
                if (!(atom instanceof IPseudoAtom)) {
                    try {
                        int massDiff = Integer.parseInt(massDiffString);
                        if (massDiff != 0) {
                            IIsotope major = IsotopeFactory.getInstance(molecule.getBuilder()).getMajorIsotope(element);
                            atom.setAtomicNumber(major.getAtomicNumber() + massDiff);
                        }
                    } catch (Exception exception) {
                        logger.error("Could not parse mass difference field");
                    }
                } else {
                    logger.error("Cannot set mass difference for a non-element!");
                }
                
                
                String chargeCodeString = line.substring(36,39).trim();
                logger.debug("Atom charge code: ", chargeCodeString);
                int chargeCode = Integer.parseInt(chargeCodeString);
                if (chargeCode == 0) {
                    // uncharged species
                } else if (chargeCode == 1) {
                    atom.setFormalCharge(+3);
                } else if (chargeCode == 2) {
                        atom.setFormalCharge(+2);
                } else if (chargeCode == 3) {
                        atom.setFormalCharge(+1);
                } else if (chargeCode == 4) {
                } else if (chargeCode == 5) {
                        atom.setFormalCharge(-1);
                } else if (chargeCode == 6) {
                        atom.setFormalCharge(-2);
                } else if (chargeCode == 7) {
                        atom.setFormalCharge(-3);
                }
                
                try {
                    // read the mmm field as position 61-63
                    String reactionAtomIDString = line.substring(60,63).trim();
                    logger.debug("Parsing mapping id: ", reactionAtomIDString);
                    try {
                        int reactionAtomID = Integer.parseInt(reactionAtomIDString);
                        if (reactionAtomID != 0) {
                            atom.setID(reactionAtomIDString);
                        }
                    } catch (Exception exception) {
                        logger.error("Mapping number ", reactionAtomIDString, " is not an integer.");
                        logger.debug(exception);
                    }
                } catch (Exception exception) {
                    // older mol files don't have all these fields...
                    logger.warn("A few fields are missing. Older MDL MOL file?");
                }
                
                //shk3: This reads shifts from after the molecule. I don't think this is an official format, but I saw it frequently 80=>78 for alk
                if(line.length()>=78){
                	double shift=Double.parseDouble(line.substring(69,80).trim());
                	atom.setProperty("first shift",new Double(shift));
                }
                if(line.length()>=87){
                	double shift=Double.parseDouble(line.substring(79,87).trim());
                	atom.setProperty("second shift",new Double(shift));
                }
                
                molecule.addAtom(atom);
            }
            
            // convert to 2D, if totalZ == 0
            if (totalZ == 0.0 && !forceReadAs3DCoords.isSet()) {
                logger.info("Total 3D Z is 0.0, interpreting it as a 2D structure");
                java.util.Iterator atomsToUpdate = molecule.atoms();
                while (atomsToUpdate.hasNext()) {
                    IAtom atomToUpdate = (IAtom)atomsToUpdate.next();
                    Point3d p3d = atomToUpdate.getPoint3d();
                    atomToUpdate.setPoint2d(new Point2d(p3d.x, p3d.y));
                    atomToUpdate.setPoint3d(null);
                }
            }
            
            // read BOND block
            logger.info("Reading bond block");
            for (int f = 0; f < bonds; f++) {
                line = input.readLine(); linecount++;
                atom1 = java.lang.Integer.valueOf(line.substring(0,3).trim()).intValue();
                atom2 = java.lang.Integer.valueOf(line.substring(3,6).trim()).intValue();
                order = java.lang.Integer.valueOf(line.substring(6,9).trim()).intValue();
                if (line.length() >= 12) {
                	if (line.length() > 12) {
                		stereo = java.lang.Integer.valueOf(line.substring(9,12).trim()).intValue();
                	} else {
                		stereo = java.lang.Integer.valueOf(line.substring(9).trim()).intValue();
                	}
                } else {
                	logger.warn("Missing expected stereo field at line: " + line);
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Bond: " + atom1 + " - " + atom2 + "; order " + order);
                }
                if (stereo == 1) {
                    // MDL up bond
                    stereo = CDKConstants.STEREO_BOND_UP;
                } else if (stereo == 6) {
                    // MDL down bond
                    stereo = CDKConstants.STEREO_BOND_DOWN;
                } else if (stereo == 4) {
                    //MDL bond undefined
                    stereo = CDKConstants.STEREO_BOND_UNDEFINED;
                }
                // interpret CTfile's special bond orders
                IAtom a1 = molecule.getAtom(atom1 - 1);
                IAtom a2 = molecule.getAtom(atom2 - 1);
                IBond newBond = null;
                if (order == 1) {
                	newBond = molecule.getBuilder().newBond(a1, a2, IBond.Order.SINGLE, stereo);
                } else if (order == 2) {
                	newBond = molecule.getBuilder().newBond(a1, a2, IBond.Order.DOUBLE, stereo);
                } else if (order == 3) {
                	newBond = molecule.getBuilder().newBond(a1, a2, IBond.Order.TRIPLE, stereo);
                } else if (order == 4) {                
                    // aromatic bond
                	newBond = molecule.getBuilder().newBond(a1, a2, IBond.Order.SINGLE, stereo);
                    // mark both atoms and the bond as aromatic
                	newBond.setFlag(CDKConstants.ISAROMATIC, true);
                    a1.setFlag(CDKConstants.ISAROMATIC, true);
                    a2.setFlag(CDKConstants.ISAROMATIC, true);
                }
                molecule.addBond(newBond);
            }
            
            // read PROPERTY block
            logger.info("Reading property block");
            while (true) {
                line = input.readLine(); linecount++;
                if (line == null) {
                	break;
                    //throw new CDKException("The expected property block is missing!"); property block is not mandatory
                }
		if (line.startsWith("M  END")) break;
		if (line.trim().equals("")) continue;
                boolean lineRead = false;
                if (line.startsWith("M  CHG")) {
                    // FIXME: if this is encountered for the first time, all
                    // atom charges should be set to zero first!
                    int infoCount = Integer.parseInt(line.substring(6,9).trim());
                    StringTokenizer st = new StringTokenizer(line.substring(9));
                    for (int i=1; i <= infoCount; i++) {
                        String token = st.nextToken();
                        int atomNumber = Integer.parseInt(token.trim());
                        token = st.nextToken();
                        int charge = Integer.parseInt(token.trim());
                        molecule.getAtom(atomNumber - 1).setFormalCharge(charge);
                    }
                }  else if (line.matches("^A    \\d+")) {
            		// Reads the pseudo atom property from the mol file
                	
                	// The atom number of the to replaced atom
            		int aliasAtomNumber = Integer.parseInt(line.replaceFirst("^A    ", "")) - RGroupCounter;
            		line = input.readLine(); linecount++;
					String[] aliasArray = line.split("\\\\");
					// name of the alias atom like R1 odr R2 etc. 
					String alias = "";
					for (int i = 0; i < aliasArray.length; i++) {
						alias += aliasArray[i];
					}
					IAtom aliasAtom = molecule.getAtom(aliasAtomNumber);
					IAtom newPseudoAtom = molecule.getBuilder().newPseudoAtom(alias);
					if(aliasAtom.getPoint2d() != null) {
						newPseudoAtom.setPoint2d(aliasAtom.getPoint2d());
					}
					if(aliasAtom.getPoint3d() != null) {
						newPseudoAtom.setPoint3d(aliasAtom.getPoint3d());
					}
					molecule.addAtom(newPseudoAtom);
					java.util.List bondsOfAliasAtom = molecule.getConnectedBondsList(aliasAtom);
					
					for (int i = 0; i < bondsOfAliasAtom.size(); i++) {
						IBond bondOfAliasAtom = (IBond) bondsOfAliasAtom.get(i);
						IAtom connectedToAliasAtom = bondOfAliasAtom.getConnectedAtom(aliasAtom);
						IBond newBond = bondOfAliasAtom.getBuilder().newBond(); 
						newBond.setAtoms(new IAtom[] {connectedToAliasAtom, newPseudoAtom});
						newBond.setOrder(bondOfAliasAtom.getOrder());
						molecule.addBond(newBond);
						molecule.removeBond(aliasAtom, connectedToAliasAtom);
					}
					molecule.removeAtom(aliasAtom);
					RGroupCounter++;

                } else if (line.startsWith("M  ISO")) {
                    try {
                        String countString = line.substring(6,9).trim();
                        int infoCount = Integer.parseInt(countString);
                        StringTokenizer st = new StringTokenizer(line.substring(9));
                        for (int i=1; i <= infoCount; i++) {
                            int atomNumber = Integer.parseInt(st.nextToken().trim());
                            int absMass = Integer.parseInt(st.nextToken().trim());
                            if (absMass != 0) { 
                                IAtom isotope = molecule.getAtom(atomNumber - 1);
                                isotope.setMassNumber(absMass);
                            }
                        }
                    } catch (NumberFormatException exception) {
                        String error = "Error (" + exception.getMessage() + ") while parsing line "
                                       + linecount + ": " + line + " in property block.";
                        logger.error(error);
                        throw new CDKException("NumberFormatException in isotope information on line: " + line, exception);
                    }
                } else if (line.startsWith("M  RAD")) {
                    try {
                        String countString = line.substring(6,9).trim();
                        int infoCount = Integer.parseInt(countString);
                        StringTokenizer st = new StringTokenizer(line.substring(9));
                        for (int i=1; i <= infoCount; i++) {
                            int atomNumber = Integer.parseInt(st.nextToken().trim());
                            int spinMultiplicity = Integer.parseInt(st.nextToken().trim());
                            if (spinMultiplicity > 1) {
                                IAtom radical = molecule.getAtom(atomNumber - 1);
                                for (int j=2; j <= spinMultiplicity; j++) {
                                    // 2 means doublet -> one unpaired electron
                                    // 3 means triplet -> two unpaired electron
                                    molecule.addSingleElectron(molecule.getBuilder().newSingleElectron(radical));
                                }
                            }
                        }
                    } catch (NumberFormatException exception) {
                        String error = "Error (" + exception.getMessage() + ") while parsing line "
                                       + linecount + ": " + line + " in property block.";
                        logger.error(error);
                        throw new CDKException("NumberFormatException in radical information on line: " + line, exception);
                    }
                } else if (line.startsWith("G  ")) {
                    try {
                        String atomNumberString = line.substring(3,6).trim();
                        int atomNumber = Integer.parseInt(atomNumberString);
                        //String whatIsThisString = line.substring(6,9).trim();
                    
                        String atomName = input.readLine();
                        
                        // convert Atom into a PseudoAtom
                        IAtom prevAtom = molecule.getAtom(atomNumber - 1);
                        IPseudoAtom pseudoAtom = molecule.getBuilder().newPseudoAtom(atomName);
                        if (prevAtom.getPoint2d() != null) {
                            pseudoAtom.setPoint2d(prevAtom.getPoint2d());
                        }
                        if (prevAtom.getPoint3d() != null) {
                            pseudoAtom.setPoint3d(prevAtom.getPoint3d());
                        }
                        AtomContainerManipulator.replaceAtomByAtom(molecule, prevAtom, pseudoAtom);
                    } catch (NumberFormatException exception) {
                        String error = "Error (" + exception.toString() + ") while parsing line "
                        + linecount + ": " + line + " in property block.";
                        logger.error(error);
                        throw new CDKException("NumberFormatException in group information on line: " + line, exception);
                    }
                } else {
                	try {
                    	SGroupMDL2000Helper.SGROUP_LINE type =  SGroupMDL2000Helper.getValue(line);
                        if (! (molecule instanceof SuppleAtomContainer)) {
                            SuppleAtomContainer sca = new SuppleAtomContainer();
                            sca.setFiltered(false);
                            sca.add(molecule);
                            molecule = sca;
                        }
                        if (molecule instanceof SuppleAtomContainer) {
                            if (sgroups == null) sgroups = new Hashtable<Integer,ISGroup>();
                            type.updateSGroups(line,(SuppleAtomContainer)molecule, sgroups);    
                        }
                             
                    	/*
                    	System.out.print(type);
                    	for (String param: params) {
                    		System.out.print(',');
                    		System.out.print(param);
                    	}
                    	System.out.println();
                    	*/
                	} catch (Exception notdefined) {
                        if (mode == Mode.STRICT) 
                        	throw new CDKException(notdefined.getMessage());
                        else
                        	logger.warn(notdefined.getMessage());
                	}
                }
                if (!lineRead) {
                    logger.warn("Skipping line in property block: ", line);
                }
            }
		} catch (CDKException exception) {
            String error = "Error while parsing line " + linecount + ": " + line + " -> " + exception.getMessage();
            logger.error(error);
            logger.debug(exception);
            throw exception;
		} catch (Exception exception) {
            String error = "Error while parsing line " + linecount + ": " + line + " -> " + exception.getMessage();
            logger.error(error);
            logger.debug(exception);
            throw new CDKException(error, exception);
		}
        if (sgroups != null) {
            Iterator<ISGroup> i = sgroups.values().iterator();
            while (i.hasNext()) 
                molecule.addAtom(i.next());
        }
		return molecule;
	}
    
    public void close() throws IOException {
        input.close();
    }
    
    private void initIOSettings() {
        forceReadAs3DCoords = new BooleanIOSetting("ForceReadAs3DCoordinates", IOSetting.LOW,
          "Should coordinates always be read as 3D?", 
          "true");
    }
    
    public void customizeJob() {
        fireIOSettingQuestion(forceReadAs3DCoords);
    }

    public IOSetting[] getIOSettings() {
        IOSetting[] settings = new IOSetting[1];
        settings[0] = forceReadAs3DCoords;
        return settings;
    }
}

