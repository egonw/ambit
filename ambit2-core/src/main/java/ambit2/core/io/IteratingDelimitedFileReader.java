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
package ambit2.core.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.io.setting.StringIOSetting;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;

/**
 * Iterating reader for delimited files.
 * 
 * @author Nina Jeliazkova <b>Modified</b> 2005-9-5
 */
public class IteratingDelimitedFileReader extends
		IteratingFilesWithHeaderReader implements IIteratingChemObjectReader {
    public static String defaultSMILESHeader = "SMILES";
	private BufferedReader input;

	private boolean nextAvailableIsKnown;

	private boolean hasNext;

	private IMolecule nextMolecule;

	protected DelimitedFileFormat format;

	protected Object[] values;



	public IteratingDelimitedFileReader(Reader in) {
		this(in, new DelimitedFileFormat()); // default format
	}

	/**
	 * 
	 */
	public IteratingDelimitedFileReader(Reader in, DelimitedFileFormat format) {
		super();
		this.format = format;

		input = new BufferedReader(in);

		nextMolecule = null;
		nextAvailableIsKnown = false;
		hasNext = false;

	}
	@Override
	protected LiteratureEntry getReference() {
		return LiteratureEntry.getInstance(getClass().getName(),getClass().getName());
	}	

	public IteratingDelimitedFileReader(InputStream in) {
		this(new InputStreamReader(in));
	}

	public IteratingDelimitedFileReader(InputStream in,
			DelimitedFileFormat format) {
		this(new InputStreamReader(in), format);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openscience.cdk.io.ChemObjectIO#getFormat()
	 */
	public IResourceFormat getFormat() {
		return format;

	}

	// TODO fix: returns false if a column without a header has some data
	// (throws exception and the returns false)

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		if (isHeaderEmpty()) {
	    	fireIOSettingQuestion(new StringIOSetting("",IOSetting.MEDIUM,Property.IO_QUESTION.IO_START.toString(),""));
			processHeader(input);
	    	fireIOSettingQuestion(new StringIOSetting("",IOSetting.MEDIUM,Property.IO_QUESTION.IO_STOP.toString(),""));
		}
		
		if (!nextAvailableIsKnown) {
			hasNext = false;

			// now try to parse the next Molecule
			try {
				if (input.ready()) {

					extractRowKeyAndData(input.readLine().trim());

					if (smilesIndex == -1)
						nextMolecule = new Molecule();
					else {
						try {
							//nextMolecule = sp.parseSmiles(values[smilesIndex].toString(),getTimeout());
						    nextMolecule = sp.parseSmiles(values[smilesIndex].toString());
						} catch (InvalidSmilesException x) {
								// do not want to break if a record is faulty
								logger.warn("Empty molecule!");
								nextMolecule = new Molecule(); // just create
								nextMolecule.setProperty("SMILES", "Invalid SMILES");
							//}
						}
						
					}

					for (int i = 0; i < values.length; i++)
						if (values[i]!=null)
						nextMolecule.setProperty(getHeaderColumn(i), 
								values[i].toString());
						else 
							nextMolecule.removeProperty(getHeaderColumn(i));

					/*
					 * if (nextMolecule.getAtomCount() > 0) { hasNext = true; }
					 * else { hasNext = false; }
					 */
					hasNext = true;
				} else {
					hasNext = false;
				}
			} catch (Exception exception) {
				exception.printStackTrace();
				logger.error("Error while reading next molecule: "
						+ exception.getMessage());
				logger.error(values);
				logger.debug(exception);
				hasNext = true;
			}
			if (!hasNext)
				nextMolecule = null;
			nextAvailableIsKnown = true;
		}
		return hasNext;

	}

	public Object next() {
		if (!nextAvailableIsKnown) {
			hasNext();
		}
		nextAvailableIsKnown = false;
		if (!hasNext) {
			throw new NoSuchElementException();
		}
		/*
		for (int i=0; i < headerOptions.length;i++) 
        if (headerOptions[i] instanceof MolPropertiesIOSetting) 
        	((MolPropertiesIOSetting) headerOptions[i]).getProperties()
        		.assign(nextMolecule);
        	*/	
		return nextMolecule;
	}

	public void close() throws IOException {
		input.close();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}


	/*
	protected IOSetting[] setHeaderOptions() {
	    Vector commandOptions = new Vector();
	    IOSetting[] options = new IOSetting[header.size()];
	    for (int i=0; i < IColumnTypeSelection._columnTypesS.length;i++)
	        commandOptions.add(IColumnTypeSelection._columnTypesS[i]);
	    
	    for (int i=0; i < header.size();i++) {
	        options[i] = new OptionIOSetting("Select column type",
	                IOSetting.HIGH,
	                header.get(i).toString(), commandOptions, 
	                IColumnTypeSelection._columnTypesS[IColumnTypeSelection._ctX]);
	    }
        return options;
	}
	*/
	protected void processHeader(BufferedReader in) {
		try {
			
			String line = in.readLine();

			StringTokenizer st = new StringTokenizer(line,new String(format.getFieldDelimiter()));
			while (st.hasMoreTokens()) {
				addHeaderColumn(st.nextToken().trim());	
			}
			for (int i=0; i < getNumberOfColumns(); i++)
				if (getHeaderColumn(i).toString().equals(defaultSMILESHeader)) {
					smilesIndex = i; break;
				}
			values = new Object[getNumberOfColumns()];
			
			
		} catch (IOException x) {
			logger.error(x);
		}
	}

	private String removeStringDelimiters(String key) {
		char textDelimiter = format.getTextDelimiter();
		String k = key.trim();
		if (k.length() == 0)
			return "";
		if (k.charAt(0) == textDelimiter) {
			k = k.substring(1);
		}
		if (k.charAt(k.length() - 1) == textDelimiter) {
			k = k.substring(0, k.length() - 1);
		}
		return k;
	}

	/**
	 * Extract values from a line
	 */
	public void extractRowKeyAndData(String line) {
		
			StringTokenizer st = new StringTokenizer(line,format.getFieldDelimiter());
			int fieldIndex = 0;
			while (st.hasMoreTokens()) {
				if (fieldIndex>=values.length) break;
				String next = st.nextToken();
				if (next != null)
					values[fieldIndex] = removeStringDelimiters(next);
				else values[fieldIndex] = "";
				fieldIndex ++;
			}

	}
    @Override
	public String toString() {
        return "Reading compounds from " + format.toString(); 
    }
}
