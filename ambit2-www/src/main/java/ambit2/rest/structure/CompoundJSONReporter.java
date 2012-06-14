package ambit2.rest.structure;

import java.io.IOException;
import java.io.Writer;

import org.restlet.Request;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.ResourceDoc;
import ambit2.rest.dataset.ARFFResourceReporter;
import ambit2.rest.property.PropertyJSONReporter;

/**
 * JSON
 * @author nina
 *
 * @param <Q>
 */
public class CompoundJSONReporter<Q extends IQueryRetrieval<IStructureRecord>> extends ARFFResourceReporter<Q> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 410930501401847402L;
	protected String comma = null;
	protected PropertyJSONReporter propertyJSONReporter;
	enum jsonCompound {
		URI,
		compound,
		dataset,
		dataEntry,
		values;
		
		public String jsonname() {
			return name();
		}
	}
	
	public CompoundJSONReporter(Template template, Request request,ResourceDoc doc, String urlPrefix) {
		this(template,null,request,doc,urlPrefix);
	}
	
	public CompoundJSONReporter(Template template,Profile groupedProperties, Request request,ResourceDoc doc, String urlPrefix) {
		super(template,groupedProperties,request,doc,urlPrefix);
		propertyJSONReporter = new PropertyJSONReporter(request);
	}

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
	@Override
	public Object processItem(IStructureRecord item) throws AmbitException {
		try {
			Writer writer = getOutput();
			writeHeader(writer);
			int i = 0;
			String uri = String.format("%s/compound/%d",urlPrefix,item.getIdchemical());
			if (item.getIdstructure()>0)
				uri = String.format("%s/conformer/%d",uri,item.getIdstructure());
			
			StringBuilder builder = new StringBuilder();
			if (comma!=null) builder.append(comma);
			
			builder.append("\n\t{\n");
			builder.append(String.format("\t\"%s\":{\n",jsonCompound.compound.jsonname()));
			builder.append(String.format("\t\t\"%s\":\"%s\",\n",jsonCompound.URI.jsonname(),uri));
			builder.append(String.format("\t\t\"%s\":\"%s\"","type","Compound"));
			builder.append("\n\t\t},\n");
			
			builder.append(String.format("\t\"%s\":[",jsonCompound.values.jsonname()));
			for (int j=0; j < header.size(); j++) {
				if (j>0) builder.append(",");
				Property p = header.get(j);
				builder.append("\n\t\t{\n");
				Object value = item.getProperty(p);
				builder.append(String.format("\t\t\"%s\":\"%s\",\n","feature",reporter.getURI(p)));
				if (value==null) {
					builder.append(String.format("\t\t\"%s\":null\n","value"));
				} else if (p.getClazz().equals(String.class))
					builder.append(String.format("\t\t\"%s\":\"%s\"\n","value",value));
				else {
					builder.append(String.format("\t\t\"%s\":%s\n","value",value));
				}
				/*
				if (p.getClazz()==Number.class) { 
					writer.write(String.format(",%s",
							(value==null)||(IQueryRetrieval.NaN.equals(value.toString()))?"?":value
							));
				} else
					writer.write(String.format(",%s%s%s",
							value==null?"":"\"",
							value==null?"?":
							value.toString().replace("\n", "").replace("\r",""),
							value==null?"":"\""
							));		
				*/		
				builder.append("\t\t}");
				i++;
			}
			builder.append("\n\t\t]");
			
			builder.append("\n\t}");
			writer.write(builder.toString());
			comma = ",";
		} catch (Exception x) {
			logger.error(x);
		}
		return item;
		
	}
	/*
	public Object zprocessItem(IStructureRecord item) throws AmbitException {

		try {
			String uri = cmpReporter.getURI(item);
			
			if (comma!=null) getOutput().write(comma);
			getOutput().write(String.format(
					"\n{"+
					"\n\"%s\":\"%s\"," + //uri
					"\n\"%s\":\"%s\"," + //title
					"\n\"%s\":{\n\t\"URI\":\"%s\",\n\t\"%s\":\"%s\",\n\t\"img\":\"%s\"\n}," + 					//algorithm
					"\n\"%s\":\"%s\"," + //dataset
					"\n\"%s\":\"%s\"," + //vars
					"\n\"%s\":\"%s\"," + //vars
					"\n\"%s\":\"%s\"," + //vars
					"\n\"%s\":{" + 	
					"\n\t\"%s\":\"%s\"," +
					"\n\t\"%s\":\"%s\"," +
					"\n\t\"%s\":\"%s\"," +
					"\n\t\"%s\":\"%s\" " +
					"\n\n}}",
					
					jsonModel.URI.jsonname(),uri,
					jsonModel.title.jsonname(),item.getName()

					));
			comma = ",";

		} catch (Exception x) {
			
		}
		return item;
	}
	*/
	@Override
	public void header(java.io.Writer output, Q query) {
		try {
			output.write("{\n");
			output.write("\"dataEntry\":[");
			
		} catch (Exception x) {}
	};
	@Override
	public void footer(java.io.Writer output, Q query) {
		try {
			output.write("\n]");
			output.write("\n}");
		} catch (Exception x) {}
	};
	

	
	@Override
	public String getFileExtension() {
		return null;//"json";
	}
	

	
}