package ambit2.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.IChemObjectReaderErrorHandler;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.listener.IChemObjectIOListener;
import org.openscience.cdk.io.setting.IOSetting;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.processors.structure.MoleculeWriter;

public class RawIteratingWrapper<R extends IIteratingChemObjectReader> implements IRawReader<IStructureRecord>{
	protected R reader;
	protected MoleculeWriter writer ;
	protected LiteratureEntry reference;	
	protected final StructureRecord r = new StructureRecord();
   // protected IChemObjectReader.Mode mode = IChemObjectReader.Mode.RELAXED;
   // protected IChemObjectReaderErrorHandler errorHandler = null;
	
	public RawIteratingWrapper(R reader) {
		this.reader = reader;
		writer = new MoleculeWriter();
	}
	

	public LiteratureEntry getReference() {
		return reference;
	}
	public void setReference(LiteratureEntry reference) {
		this.reference = reference;
	}	
	public void setReader(InputStream reader) throws CDKException {
		throw new CDKException("Not implemented");
	}
	public void setReader(Reader reader) throws CDKException {
		throw new CDKException("Not implemented");
	}
	public IStructureRecord nextRecord() {
        Object o = next();
        if (o instanceof IStructureRecord) return (IStructureRecord)o;
        else try {
        	StructureRecord r = new StructureRecord(-1,-1,writer.process((IAtomContainer)o),"SDF");
        	r.setReference(reference);
        	return r;
        } catch (Exception x) {
        	StructureRecord r = new StructureRecord(-1,-1,null,"SDF");
        	r.setReference(reference);
        	return r;
        }
	}

	public boolean accepts(Class arg0) {
		return reader.accepts(arg0);
	}

	public void addChemObjectIOListener(IChemObjectIOListener arg0) {
		reader.addChemObjectIOListener(arg0);
		
	}

	public void close() throws IOException {
		reader.close();
		
	}

	public IResourceFormat getFormat() {
		return reader.getFormat();
	}

	public IOSetting[] getIOSettings() {
		return reader.getIOSettings();
	}

	public void removeChemObjectIOListener(IChemObjectIOListener arg0) {
		reader.removeChemObjectIOListener(arg0);
		
	}

	public boolean hasNext() {
		return reader.hasNext();
	}

	public Object next() {
		Object o = reader.next();
		if (o instanceof IAtomContainer) try {
			
			r.setIdchemical(-1);
			r.setIdstructure(-1);
			r.setFormat("SDF");
			r.setContent(writer.process((IAtomContainer)o));
			Object ref = ((IAtomContainer)o).getProperty("REFERENCE");
			if (ref instanceof LiteratureEntry)
				r.setReference((LiteratureEntry)ref);
			else r.setReference(getReference());
			return r;  
		} catch (Exception x) {
			r.setIdchemical(-1);
			r.setIdstructure(-1);
			r.setFormat("SDF");
			r.setContent(null);
			r.setReference(getReference());
			return r;  
		} else return o;
	}

	public void remove() {
		reader.remove();
		
	}
	

	   public void setReaderMode(ISimpleChemObjectReader.Mode mode) {
	    	reader.setReaderMode(mode);
	    }

	    /** {@inheritDoc} */
	    public void setErrorHandler(IChemObjectReaderErrorHandler handler) {
	        reader.setErrorHandler(handler);
	    }

	    /** {@inheritDoc} */
	    public void handleError(String message) throws CDKException {
	        reader.handleError(message);
	    }

	    /** {@inheritDoc} */
	    public void handleError(String message, Exception exception)
	    throws CDKException {
	        reader.handleError(message, exception);
	    }

	    /** {@inheritDoc} */
	    public void handleError(String message, int row, int colStart, int colEnd) throws CDKException {
	        reader.handleError(message, row, colStart, colEnd);
	    }

	    /** {@inheritDoc} */
	    public void handleError(String message, int row, int colStart, int colEnd, Exception exception)
	    throws CDKException {
	        reader.handleError(message, row, colStart, colEnd, exception);
	    }
	
}
