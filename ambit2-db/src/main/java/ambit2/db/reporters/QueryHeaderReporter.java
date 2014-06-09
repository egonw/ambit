package ambit2.db.reporters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.ReadChemicalIds;
import ambit2.db.readers.RetrieveGroupedValuesByAlias;
import ambit2.db.readers.RetrieveProfileValues;
import ambit2.db.readers.RetrieveProfileValues.SearchMode;
import ambit2.db.search.QuerySmilesByID;

/**
 * Generates report from a query with header at a top (e.g. CSV columns or ARFF header)
 * @author nina
 *
 * @param <T>
 * @param <Q>
 * @param <Output>
 */
public abstract class QueryHeaderReporter<Q extends IQueryRetrieval<IStructureRecord>,Output>  extends QueryStructureReporter<Q, Output> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4653171215072211975L;
	protected boolean includeMol;
	
	public boolean isIncludeMol() {
		return includeMol;
	}

	
	protected Profile groupProperties;
	public Profile getGroupProperties() {
		return groupProperties;
	}
	public void setGroupProperties(Profile gp) {
		this.groupProperties = gp;
	}


	protected Template template;
	protected List<Property> header = null;
	
	
	public List<Property> getHeader() {
		return header;
	}
	public Template getTemplate() {
		return template;
	}
	public void setTemplate(Template template) {
		this.template = template;
	}
	
	
	protected List<Property> template2Header(Template template, boolean propertiesOnly) {
		List<Property> h = new ArrayList<Property>();
		Iterator<Property> it;
		if (groupProperties!=null) {
			it = groupProperties.getProperties(true);
			while (it.hasNext()) {
				Property t = it.next();
				h.add(t);
			}
		}			
		it = template.getProperties(true);
		while (it.hasNext()) {
			Property t = it.next();
			if (!propertiesOnly || (propertiesOnly && (t.getId()>0)))
				h.add(t);
		}
		
		Collections.sort(h,new Comparator<Property>() {
			public int compare(Property o1, Property o2) {
				return Integer.toString(o1.getId()).compareTo(Integer.toString(o2.getId()));
				//mimic URI comparison as strings
			}
		});			
		
	
		/*
		Collections.sort(h,new Comparator<Property>() {
			public int compare(Property o1, Property o2) {
				return o1.getOrder()-o2.getOrder();
			}
		});	
		*/
		return h;
	}

	
	protected void configureProcessors(boolean includeMol) {
		configurePropertyProcessors();
		getProcessors().add(new ProcessorStructureRetrieval(new QuerySmilesByID()));		
		getProcessors().add(new DefaultAmbitProcessor<IStructureRecord,IStructureRecord>() {
			public IStructureRecord process(IStructureRecord target) throws AmbitException {
				processItem(target);
				return target;
			};
		});	
	}
	protected void configurePropertyProcessors() {
		if ((getGroupProperties()!=null) && (getGroupProperties().size()>0))
			getProcessors().add(new ProcessorStructureRetrieval(new RetrieveGroupedValuesByAlias(getGroupProperties())) {
				@Override
				public IStructureRecord process(IStructureRecord target)
						throws AmbitException {
					((RetrieveGroupedValuesByAlias)getQuery()).setRecord(target);
					return super.process(target);
				}
			});
		if (getTemplate().size()>0) 
			getProcessors().add(new ProcessorStructureRetrieval(new RetrieveProfileValues(SearchMode.idproperty,getTemplate(),true)) {
				@Override
				public IStructureRecord process(IStructureRecord target)
						throws AmbitException {
					((RetrieveProfileValues)getQuery()).setRecord(target);
					return super.process(target);
				}
			});		
		
	}

}
