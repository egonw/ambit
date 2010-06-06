package ambit2.db.reporters;

import ambit2.base.data.Profile;
import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.RetrieveGroupedValuesByAlias;
import ambit2.db.readers.RetrieveProfileValues;
import ambit2.db.readers.RetrieveStructure;
import ambit2.db.readers.RetrieveProfileValues.SearchMode;

public abstract class AbstractStructureRecordReporter<Result> extends QueryStructureReporter<IQueryRetrieval<IStructureRecord>, Result> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7744663194192122818L;
	protected Template template;
	protected Profile groupProperties;
	public Profile getGroupProperties() {
		return groupProperties;
	}
	public Template getTemplate() {
		return template;
	}
	public void setTemplate(Template template) {
		this.template = template;
	}
	public AbstractStructureRecordReporter(Profile groupedProperties,Template template) {
		this(null,groupedProperties,template);
	}
	
	public AbstractStructureRecordReporter(Result record,Profile groupedProperties,Template template) {
		super();
		try {
			setOutput(record);
		} catch ( Exception x) {
			this.output = null;
		}
		this.template = template;
		this.groupProperties = groupedProperties;
		getProcessors().clear();
		RetrieveStructure q = new RetrieveStructure();
		q.setPageSize(1);
		q.setPage(0);
		getProcessors().add(new ProcessorStructureRetrieval(q));
		
		if ((getGroupProperties()!=null) && (getGroupProperties().size()>0)) 
			getProcessors().add(new ProcessorStructureRetrieval(new RetrieveGroupedValuesByAlias(getGroupProperties())) {
				@Override
				public IStructureRecord process(IStructureRecord target)
						throws AmbitException {
					((RetrieveGroupedValuesByAlias)getQuery()).setRecord(target);
					return super.process(target);
				}
			});		
		if ((getTemplate()!=null) && (getTemplate().size()>0)) 
			getProcessors().add(new ProcessorStructureRetrieval(new RetrieveProfileValues(SearchMode.idproperty,getTemplate(),true)) {
				@Override
				public IStructureRecord process(IStructureRecord target)
						throws AmbitException {
					((RetrieveProfileValues)getQuery()).setRecord(target);
					return super.process(target);
				}
			});		
		getProcessors().add(new DefaultAmbitProcessor<IStructureRecord,IStructureRecord>() {
			public IStructureRecord process(IStructureRecord target) throws AmbitException {
				processItem(target);
				return target;
			};
		});			
	}	
	public void footer(Result output, ambit2.db.readers.IQueryRetrieval<IStructureRecord> query) {};
	public void header(Result output, ambit2.db.readers.IQueryRetrieval<IStructureRecord> query) {};
	public void open() throws DbAmbitException {}
}	