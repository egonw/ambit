/*
 * Created on 2005-9-5
 *
 */
package ambit.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.NoSuchElementException;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.setting.IOSetting;

import ambit.misc.AmbitCONSTANTS;

/**
 * {@link org.openscience.cdk.io.iterator.IIteratingChemObjectReader} implementation for reading delimited files {@link ambit.io.DelimitedFileFormat}.
 * 
 * @author Nina Jeliazkova <b>Modified</b> 2005-9-5
 */
public class IteratingDelimitedFileReader extends
		IteratingFilesWithHeaderReader implements IIteratingChemObjectReader {
    public static String defaultSMILESHeader = "SMILES";
	private BufferedReader input;

	private boolean nextAvailableIsKnown;

	private boolean hasNext;

	private Molecule nextMolecule;

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


		processHeader(input);
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
	    if (headerOptions == null) {
	        headerOptions = setHeaderOptions(header);
	        for (int i=0; i < headerOptions.length;i++)
	            fireIOSettingQuestion(headerOptions[i]);
	    }
		if (!nextAvailableIsKnown) {
			hasNext = false;

			// now try to parse the next Molecule
			try {
				if (input.ready()) {

					extractRowKeyAndData(input.readLine().trim(), header);

					if (smilesIndex == -1)
						nextMolecule = new Molecule();
					else {
						try {
							nextMolecule = sp.parseSmiles(values[smilesIndex].toString(),getTimeout());
						} catch (InvalidSmilesException x) {
							// this is a hack - trying to eliminate OASIS (
							// www.oasis-lmc.org) stereo SMILES so that we will
							// be able to read them ...
							/* { } are considered regexps .... doesn't work now
							String smiles = values[smilesIndex].toString();
							if (smiles.indexOf('{') > -1) {
								logger
										.warn("Trying to eliminate OASIS ( www.oasis-lmc.org) SMILES peculiarities\t"
												+ smiles);
								smiles.replaceAll("\u007B+\u007D", "");
								smiles = smiles.replaceAll("\u007B-\u007D", "");
								smiles.replaceAll("\u007BP+\u007D", "");
								smiles.replaceAll("\u007BP-\u007D", "");
								logger
										.warn("SMILES cleaned, hope it will work\t"
												+ smiles);
								try {
									nextMolecule = sp
											.parseSmiles(values[smilesIndex]
													.toString());
								} catch (InvalidSmilesException x1) {
									logger
											.warn("Can't fix it, create empty molecule!");
									nextMolecule = new Molecule(); // just
																	// create
									logger.error(x1);
								}
							} else {
						*/
								// do not want to break if a record is faulty
								logger.warn("Empty molecule!");
								nextMolecule = new Molecule(); // just create
								nextMolecule.setProperty(AmbitCONSTANTS.SMILES, "Invalid SMILES");
							//}
						}
						
					}

					for (int i = 0; i < values.length; i++)
						nextMolecule.setProperty(header.get(i), values[i]
								.toString());

					/*
					 * if (nextMolecule.getAtomCount() > 0) { hasNext = true; }
					 * else { hasNext = false; }
					 */
					hasNext = true;
				} else {
					hasNext = false;
				}
			} catch (Exception exception) {
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
			char delimiter = format.getFieldDelimiter();
			char textDelimiter = format.getTextDelimiter();
			String line = in.readLine();

			int fieldIndex = 0;
			int start = 0;
			int quote = 0;
			for (int i = 0; i < line.length(); i++) {
				if (line.charAt(i) == textDelimiter) {
					quote = (quote + 1) % 2;
					continue;
				}

				if ((quote==0) && (line.charAt(i) == delimiter)) {

					String key = line.substring(start, i);
					String val = removeStringDelimiters(key);
					header.add(val);
					if (val.equals(defaultSMILESHeader))
						smilesIndex = fieldIndex;
					start = i + 1;
					fieldIndex++;
				}
			}
			String key = line.substring(start, line.length());
			String val = removeStringDelimiters(key);
			if (val.equals(defaultSMILESHeader))
				smilesIndex = fieldIndex;
			header.add(val);
			values = new Object[header.size()];
			
			
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
	public void extractRowKeyAndData(String line, List columnKeys) {
			char delimiter = format.getFieldDelimiter();
			char textDelimiter = format.getTextDelimiter();
			int quote = 0;
			int fieldIndex = 0;
			int start = 0;
			for (int i = 0; i < values.length; i++)
				values[i] = "";
			for (int i = 0; i < line.length(); i++) {
				if (line.charAt(i) == textDelimiter) {
					quote = (quote + 1) % 2;
					continue;
				}
				if ((quote == 0) && ((line.charAt(i) == delimiter))) {
					if ((start) == i)
						values[fieldIndex] = ""; // empty field
					else
						values[fieldIndex] = removeStringDelimiters(line.substring(
								start, i));
					start = i + 1;
					fieldIndex++;
					if (fieldIndex >= values.length) break;
				}
			}
			if (fieldIndex < values.length)
				values[fieldIndex] = removeStringDelimiters(line.substring(start,
						line.length()));
			else
				logger.error("Field number\t" + fieldIndex + "\tout of\t"
						+ values.length + "\t"
						+ line.substring(start, line.length()));

	}
	/* (non-Javadoc)
     * @see org.openscience.cdk.io.ChemObjectIO#getIOSettings()
     */
    public IOSetting[] getIOSettings() {
        return headerOptions;
    }
    public String toString() {
        return "Reading compounds from " + format.toString(); 
    }
}
