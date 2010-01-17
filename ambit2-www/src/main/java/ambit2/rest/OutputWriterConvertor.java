package ambit2.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;

import ambit2.base.exceptions.NotFoundException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;

public class OutputWriterConvertor<T,Q extends IQueryRetrieval<T>>  extends QueryRepresentationConvertor<T,Q,Writer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7974532412944774457L;
	
	public OutputWriterConvertor(QueryReporter<T, Q, Writer> reporter,MediaType mediaType) {
		super(reporter,mediaType);
	}

	public Representation process(final Q query) throws ambit2.base.exceptions.AmbitException {
		 return new OutputRepresentation(mediaType) {
	            @Override
	            public void write(OutputStream stream) throws IOException {
            		OutputStreamWriter writer = null;          	
	            	try {
	            		writer = new OutputStreamWriter(stream,"UTF-8");	  
	            		getReporter().setOutput(writer);
	            		getReporter().process(query);
	            		//writer.flush();
	            		//stream.flush();
	            	} catch (NotFoundException x) {
	            		;
	            	} catch (Exception x) {
	            		Throwable ex = x;
	            		while (ex!=null) {
	            			if (ex instanceof IOException) 
	            				throw (IOException)ex;
	            			ex = ex.getCause();
	            		}
	            		Context.getCurrentLogger().warning(x.getMessage()==null?x.toString():x.getMessage());

	            	} finally {
	            		try {if (writer !=null) writer.flush(); } catch (Exception x) { }
	            		try {if (stream !=null) stream.flush(); } catch (Exception x) { }
	            		try {getReporter().close(); } catch (Exception x) { x.printStackTrace();}
	            	}
	            }
	        };		
	};	


}
