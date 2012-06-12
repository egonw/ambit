package ambit2.rest.task;

import java.io.Writer;
import java.util.UUID;

import org.restlet.Request;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.rest.DisplayMode;
import ambit2.rest.ResourceDoc;

/**
 * Same as parent class, with custom HTML reporter for Task object
 * @author nina
 *
 * @param <USERID>
 */
public class AmbitFactoryTaskConvertor<USERID> extends
		FactoryTaskConvertor<USERID> {
	
	public AmbitFactoryTaskConvertor(ITaskStorage<USERID> storage) {
		super(storage);
	}	
	
	@Override
	public synchronized ambit2.base.processors.Reporter<java.util.Iterator<UUID>,Writer> createTaskReporterHTML(Request request, ResourceDoc doc, DisplayMode _dmode) throws AmbitException ,ResourceException {
		return	new TaskHTMLReporter<USERID>(storage,request,doc, _dmode);
	}
}
