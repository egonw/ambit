package ambit2.rest.structure;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.SDFWriter;
import org.restlet.Request;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.facet.IFacet;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.db.facets.compounds.CollectionsByChemical;
import ambit2.db.processors.MasterDetailsProcessor;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.RetrieveStructure;
import ambit2.db.reporters.CSVReporter;
import ambit2.db.search.IQueryCondition;
import ambit2.rest.ResourceDoc;
import ambit2.rest.json.JSONUtils;
import ambit2.rest.property.PropertyJSONReporter;

/**
 * JSON
 * @author nina
 *
 * @param <Q>
 */
public class CompoundJSONReporter<Q extends IQueryRetrieval<IStructureRecord>> extends CSVReporter<Q> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 410930501401847402L;
	protected String comma = null;
	protected String jsonpCallback = null;
	protected PropertyJSONReporter propertyJSONReporter;
	protected String hilightPredictions = null;
	protected MoleculeReader reader = null;


	public String getHilightPredictions() {
		return hilightPredictions;
	}

	public void setHilightPredictions(String hilightPredictions) {
		this.hilightPredictions = hilightPredictions;
	}



	enum jsonCompound {
		URI,
		mol,
		structype,
		compound,
		dataset,
		dataEntry,
		values,
		facets;
		
		public String jsonname() {
			return name();
		}
	}
	
	public CompoundJSONReporter(Template template, Request request,ResourceDoc doc, String urlPrefix,Boolean includeMol, String jsonpCallback) {
		this(template,null,null,request,doc,urlPrefix,includeMol,jsonpCallback);
	}
	
	public CompoundJSONReporter(Template template,
					Profile groupedProperties,
					String[] folders,
					Request request,ResourceDoc doc, 
					String urlPrefix,
					Boolean includeMol,
					String jsonpCallback) {
		super(template,groupedProperties,folders,urlPrefix,includeMol);
		this.jsonpCallback = jsonpCallback;
		
		propertyJSONReporter = new PropertyJSONReporter(request);
		hilightPredictions = request.getResourceRef().getQueryAsForm().getFirstValue("model_uri");
	}
	@Override
	protected void configureProcessors(boolean includeMol) {
		if (includeMol) {
			RetrieveStructure r = new RetrieveStructure();
			r.setPage(0);
			r.setPageSize(1);
			getProcessors().add(new ProcessorStructureRetrieval(r));
		}
		configurePropertyProcessors();
		configureCollectionProcessors();
		getProcessors().add(new DefaultAmbitProcessor<IStructureRecord,IStructureRecord>() {
			public IStructureRecord process(IStructureRecord target) throws AmbitException {
				processItem(target);
				return target;
			};
		});	
	};
	@Override
	protected void configureCollectionProcessors() {
		if (folders==null || folders.length==0) return;
		CollectionsByChemical collections = new CollectionsByChemical(null);
		collections.setValue(folders);
		MasterDetailsProcessor<IStructureRecord,IFacet<String>,IQueryCondition> facetReader = new MasterDetailsProcessor<IStructureRecord,IFacet<String>,IQueryCondition>(collections) {
			@Override
			protected IStructureRecord processDetail(IStructureRecord master,
					IFacet<String> detail) throws Exception {
				master.clearFacets();
				master.addFacet(detail);
				return master;
			}
		};
		facetReader.setCloseConnection(false);
		getProcessors().add(facetReader);
	}
	@Override
	public void setOutput(Writer output) throws AmbitException {
		super.setOutput(output);
		propertyJSONReporter.setOutput(output);
	}
	@Override
	protected void writeHeader(Writer writer) throws IOException {
		if (header == null) {
			header = template2Header(template,true);
			/*
			writer.write("@attribute URI string\n");
			for (Property p : header) {
				writer.write(getPropertyHeader(p));
			}
			
			writer.write("\n@data\n");
			*/
		}
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
	@Override
	public Object processItem(IStructureRecord item) throws AmbitException {
		try {
			Writer writer = getOutput();
			writeHeader(writer);
			int i = 0;
			String uri = getURI(item);
						
			StringBuilder builder = new StringBuilder();
			if (comma!=null) builder.append(comma);
			
			builder.append("\n\t{\n");
			builder.append(String.format("\t\"%s\":{\n",jsonCompound.compound.jsonname()));
			builder.append(String.format("\t\t\"%s\":\"%s\",\n",jsonCompound.URI.jsonname(),uri));
			builder.append(String.format("\t\t\"%s\":\"%s\",\n",jsonCompound.structype.jsonname(),item.getType().name()));
			if (includeMol)
				if (item.getContent()==null)
					builder.append(String.format("\t\t\"%s\":null,\n",jsonCompound.mol.jsonname()));
				else	
				builder.append(String.format("\t\t\"%s\":\"%s\",\n",jsonCompound.mol.jsonname(),JSONUtils.jsonEscape(getSDFContent(item))));
			//similarity
			Object similarityValue = null;
			for (Property p : item.getProperties()) 
				if ("metric".equals(p.getName())) {
					similarityValue = item.getProperty(p);
					break;
				}
			builder.append(String.format("\t\t\"metric\":%s,",similarityValue));
			
			builder.append(String.format("\t\t\"%s\":\"\",","name")); //placeholders
			builder.append(String.format("\t\t\"%s\":\"\",","cas"));
			builder.append(String.format("\t\t\"%s\":\"\"","einecs"));
			

			builder.append("\n\t\t},\n");
			
			builder.append(String.format("\t\"%s\":{\n",jsonCompound.values.jsonname()));
			String comma1 = null;
			for (int j=0; j < header.size(); j++) {

				Property p = header.get(j);
				Object value = item.getProperty(p);
				
				String key = propertyJSONReporter.getURI(p);
				if (key.contains("cdk:Title") || key.contains("cdk:Formula")) continue;
				if (key.contains("SMARTSProp")) continue;
				if (value==null) {
					continue; //builder.append(String.format("\t\t\"%s\":null",key));
				} 
				if (comma1!=null) {
					builder.append(comma1);
					builder.append("\n");
				}
				if (value instanceof Double) 
					builder.append(String.format(Locale.ENGLISH,"\t\t\"%s\":%6.3f",key,(Double)value));
				else if (value instanceof Integer) 
					builder.append(String.format("\t\t\"%s\":%d",key,(Integer)value));
				else if (value instanceof Long) 
					builder.append(String.format("\t\t\"%s\":%l",key,(Long)value));
				else 
					builder.append(String.format("\t\t\"%s\":\"%s\"",key,JSONUtils.jsonEscape(value.toString().replace("\n","|"))));				
				i++;
				comma1 = ",";
			}
			builder.append("\n\t\t},\n");
			
			builder.append(String.format("\t\"%s\":[\n",jsonCompound.facets.jsonname()));
			Iterable<IFacet> facets = item.getFacets();
			String delimiter = "";
			if (facets!=null) for (IFacet facet : facets) {
				if (facet.getValue()==null) continue;
				builder.append(delimiter);
				builder.append(String.format("\t\t{\"%s\":%d}",facet.getValue()==null?"":facet.getValue(),facet.getCount()));
				delimiter=",";
			}
			builder.append("\n\t\t]");
			
			builder.append("\n\t}");
			writer.write(builder.toString());
			comma = ",";
		} catch (Exception x) {
			logger.log(java.util.logging.Level.SEVERE,x.getMessage(),x);
		}
		return item;
		
	}
	
	@Override
	public void header(java.io.Writer output, Q query) {
		try {
			if (jsonpCallback!=null) {
				output.write(jsonpCallback);
				output.write("(");
			}
			output.write("{\n");
			output.write("\"query\": {");
			output.write("\n\t\"summary\":");
			output.write("\"");
			output.write(query==null?"":JSONUtils.jsonEscape(query.toString()));
			output.write("\"");
			output.write("\n},");
			output.write("\n\"dataEntry\":[");
			
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		}
	};
	/**
	 * "{"f1":"feature1","f2":{"uri":"feature2","smth":"smb"}}"
	 */
	@Override
	public void footer(java.io.Writer output, Q query) {
		try {
			output.write("\n],");
		} catch (Exception x) {}
		
		try {
			if (hilightPredictions==null)
				output.write(String.format("\n\"%s\":null,","model_uri"));
			else
				output.write(String.format("\n\"%s\":\"%s\",","model_uri",hilightPredictions));
			output.write("\n\"feature\":{\n");
			if (header!=null)
			for (int j=0; j < header.size(); j++) 
				propertyJSONReporter.processItem(header.get(j));
			
		} catch (Exception x) {
			//x.printStackTrace();
		} finally {
			try {output.write("}\n");} catch (Exception x) {}
		}
		
		
		try {
			output.write("}\n");
			
			if (jsonpCallback!=null) {
				output.write(");");
			}
		} catch (Exception x) {}

	};
	

	
	@Override
	public String getFileExtension() {
		return null;//"json";
	}
	
	protected String getSDFContent(IStructureRecord item) throws AmbitException {
		//most common case
		if (MOL_TYPE.SDF.toString().equals(item.getFormat())) return item.getContent();
		//otherwise
		if (reader==null) reader = new MoleculeReader();
		try {
			StringWriter w = new StringWriter();
			SDFWriter sdfwriter = new SDFWriter(w); 
			IAtomContainer ac = reader.process(item);
			ac.getProperties().clear();
			sdfwriter.write(ac);
			sdfwriter.close();
			return w.toString();
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}

	

	
}
