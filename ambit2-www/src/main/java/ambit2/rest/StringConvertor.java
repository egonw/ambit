package ambit2.rest;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.restlet.data.MediaType;
import org.restlet.resource.DomRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.StringRepresentation;
import org.w3c.dom.Document;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.processors.Reporter;

public class StringConvertor<T,Q, R extends Reporter<Q,Writer> >  extends RepresentationConvertor<T,Q,Writer,R> {
	public StringConvertor(R reporter) {
		super(reporter);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 6126693410309179856L;
	
	public Representation process(Document doc) throws AmbitException {
			return new DomRepresentation(MediaType.TEXT_XML,doc);
	}
	public StringConvertor(R reporter,MediaType mediaType) {
		super(reporter,mediaType);
	}
	@Override
	public Representation process(Q query) throws AmbitException {
		try {
			reporter.setOutput(new StringWriter());
			Writer writer = reporter.process(query);
			writer.flush();
			return new StringRepresentation(writer.toString(),getMediaType());
		} catch (IOException x) {
			throw new AmbitException(x);
		}
		
	};

}
