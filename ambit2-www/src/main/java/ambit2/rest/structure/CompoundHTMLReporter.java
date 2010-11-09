package ambit2.rest.structure;

import java.awt.Dimension;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.RetrieveGroupedValuesByAlias;
import ambit2.db.readers.RetrieveProfileValues;
import ambit2.db.readers.RetrieveProfileValues.SearchMode;
import ambit2.rest.AmbitResource;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.OpenTox;
import ambit2.rest.QueryStructureHTMLReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.ResourceDoc;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.property.PropertyURIReporter;
import ambit2.rest.propertyvalue.PropertyValueResource;
import ambit2.rest.query.QueryResource;
import ambit2.rest.tuple.TupleResource;

/**
Generates HTML file with links to structures . TODO - make use of a template engine 
 * @author nina
 *
 * @param <Q>
 */
public class CompoundHTMLReporter<Q extends IQueryRetrieval<IStructureRecord>> 
										extends QueryStructureHTMLReporter<Q> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7776155843790521467L;
	protected PropertyURIReporter pReporter;
	protected boolean table = true;
	protected int count = 0;
	protected String hilightPredictions = null;
	protected Dimension cellSize = new Dimension(150,150);
	protected Form featureURI = null;
	//protected RetrieveFieldPropertyValue fieldQuery;

	public CompoundHTMLReporter(Request request,ResourceDoc doc,boolean collapsed,QueryURIReporter urireporter) {
		this(request,doc,collapsed,urireporter,null);
	}
	public CompoundHTMLReporter(Request request,ResourceDoc doc,boolean collapsed,QueryURIReporter urireporter,Template template) {
		this(request,doc,collapsed,urireporter,template,null,null);
	}
	public CompoundHTMLReporter(Request request,ResourceDoc doc,boolean collapsed,QueryURIReporter urireporter,
				Template template,Profile groupedProperties,Dimension d) {
		super(request,collapsed,doc);
		
		Reference f = request.getResourceRef().clone(); 
		f.setQuery(null);
		featureURI =  new Form(); 
		
		String[] features = request.getResourceRef().getQueryAsForm().getValuesArray(OpenTox.params.feature_uris.toString());
		if ((features == null) || (features.length==0)) {
			if (f.toString().indexOf("/dataset")>0) 
				featureURI.add(OpenTox.params.feature_uris.toString(),f.addSegment("feature").toString());
			else if (f.toString().indexOf("/compound")>0) 
				featureURI.add(OpenTox.params.feature_uris.toString(),f.addSegment("feature").toString());
		} else for (String ff:features)
			featureURI.add(OpenTox.params.feature_uris.toString(),ff);
		
		if (d != null) cellSize = d; 
		setGroupProperties(groupedProperties);
		setTemplate(template==null?new Template(null):template);
		if (urireporter != null) this.uriReporter = urireporter;
		
		hilightPredictions = request.getResourceRef().getQueryAsForm().getFirstValue("model_uri");
			pReporter = new PropertyURIReporter(request,this.uriReporter==null?null:this.uriReporter.getDocumentation());
		table = collapsed;
		getProcessors().clear();
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
	
		getProcessors().add(new DefaultAmbitProcessor<IStructureRecord,IStructureRecord>() {
			public IStructureRecord process(IStructureRecord target) throws AmbitException {
				processItem(target);
				return target;
			};
		});			
	
	}
	@Override
	public Writer getOutput() throws AmbitException {
		Writer w = super.getOutput();
		pReporter.setOutput(w);
		uriReporter.setOutput(w);
		return w;
	}
	public CompoundHTMLReporter(Request request,ResourceDoc doc,boolean collapsed,Template template) {
		this(request,doc,collapsed,null,template);
	}
	public CompoundHTMLReporter(Request request,ResourceDoc doc,boolean collapsed) {
		this(request,doc,collapsed,null,null);

	}
	@Override
	protected QueryURIReporter createURIReporter(Request request,ResourceDoc doc) {
		return new CompoundURIReporter<IQueryRetrieval<IStructureRecord>>(request,doc);
	}
	
	@Override
	public Object processItem(IStructureRecord record) throws AmbitException  {
		
		try {
			if (table) {
				output.write("<tr>");
				output.write(toURITable(record));
				output.write("</tr>");		
				
			} else {
				output.write("<div id=\"div-1a\">");
				output.write(toURI(record));
				output.write("</div>");		
			}
			count++;
		} catch (Exception x) {
			Context.getCurrentLogger().severe(x.getMessage());
		}
		return null;
		
	}
	protected String content(String left, String right) throws IOException {
		return String.format(
				"<div class=\"rowwhite\"><span class=\"left\">%s</span><span class=\"center\">%s</span></div>",
				left,right);

	}
	
	protected String templates(Reference baseReference) throws IOException {
		StringBuilder w = new StringBuilder();
		w.append("<input type='submit' value='Select table columns'>");
		String[][] options= {
				{"template/All/Identifiers/view/tree","Identifiers"},
				{"template/All/Dataset/view/tree","Datasets"},
				{"template/All/Models/view/tree","Models"},
				//{"template/All/Endpoints/view/tree","Endpoints"},
				{"template/All/Descriptors/view/tree","All descriptors"},				
				//{"template/Descriptors/Adam+C.+Lee%2C+Jing-yu+Yu+and+Gordon+M.+Crippen%2C+J.+Chem.+Inf.+Model.%2C+2008%2C+48+%2810%29%2C+pp+2042%E2%80%932053","pKa"},
				//{"template/Descriptors/ambit2.descriptors.SizeDescriptor","Molecule size"},
				//{"template/Descriptors/ambit2.mopac.DescriptorMopacShell","Electronic descriptors (PM3 optimized structure)"},
				//{"template/Descriptors/ambit2.mopac.MopacOriginalStructure","Electronic descriptors (original structure)"},
				//{"template/Descriptors/template/Descriptors/Cramer+rules","Toxtree: Cramer rules"},
		};
		
		Form form = uriReporter.getRequest().getResourceRef().getQueryAsForm();
		String[] values = OpenTox.params.feature_uris.getValuesArray(form);
		w.append("<input type=CHECKBOX value=\"\">Default</option>\n");

		for (String option[]:options) {
			String checked = "";
			for (String value:values)
				if (value==null) continue;
				else if (value.equals(String.format("%s/%s", baseReference,option[0]))) 
			{ checked = "CHECKED"; break;}
			w.append(String.format("<input type=CHECKBOX %s STYLE=\"background-color: #516373;color: #99CC00;font-weight: bold;\" value=\"%s/%s\" name=\"%s\">%s</option>\n",
						checked,
						baseReference,
						option[0],
						OpenTox.params.feature_uris.toString(),option[1]));
		}
		
		for (String value:values) {
			if (value==null) continue;
			boolean add = true;
			for (String option[]:options) 
				if (value.equals(String.format("%s/%s", baseReference,option[0]))) { add = false; break;}
			
			if (add)
				w.append(String.format("<input type=CHECKBOX %s STYLE=\"background-color: #516373;color: #99CC00;font-weight: bold;\" value=\"%s\" name=\"%s\"><a href='%s' target='_blank'>%s</a></option>\n",
						"checked",
						value,
						OpenTox.params.feature_uris.toString(),value,value));				
		}
		w.append(String.format("<input type='TEXT' size='30'alt='Enter OpenTox Feature URL here, to be added as column table' name=\"%s\">\n",
				OpenTox.params.feature_uris.toString()));		
		/*
		w.append(String.format(
				"<select size='60' STYLE=\"background-color: #516373;color: #99CC00;font-weight: bold;width: 120px\" multiple name=\"%s\">\n",
				OpenTox.params.feature_uris.toString()));
		w.append("<option value=\"\">Default</option>\n");

		for (String option[]:options)
		w.append(String.format("<option value=\"%s/%s\">%s</option>\n",baseReference,option[0],option[1]));
		w.append("</select>");
		*/
		
		return w.toString();
	}	
	
	protected String downloadLinks() throws IOException {
		StringBuilder w = new StringBuilder();
		MediaType[] mimes = {ChemicalMediaType.CHEMICAL_MDLSDF,
				ChemicalMediaType.CHEMICAL_CML,
				ChemicalMediaType.CHEMICAL_SMILES,					
				MediaType.TEXT_URI_LIST,
				MediaType.TEXT_XML,
				MediaType.APPLICATION_PDF,
				MediaType.TEXT_CSV,
				MediaType.TEXT_PLAIN,
				ChemicalMediaType.WEKA_ARFF,
				MediaType.APPLICATION_RDF_XML
				};
		String[] image = {
				"sdf.jpg",
				"cml.jpg",
				"smi.png",					
				"link.png",
				"xml.png",
				"pdf.png",
				"excel.png",
				"excel.png",
				"weka.jpg",
				"rdf.gif"
				
		};
		String q=uriReporter.getRequest().getOriginalRef().getQuery();
		for (int i=0;i<mimes.length;i++) {
			MediaType mime = mimes[i];
			w.append("&nbsp;");
			w.append(String.format(
					"<a href=\"?%s%saccept-header=%s\"  ><img src=\"%s/images/%s\" alt=\"%s\" title=\"%s\" border=\"0\"/></a>",
					q==null?"":q,
					q==null?"":"&",
					Reference.encode(mime.toString()),
					uriReporter.getBaseReference().toString(),
					image[i],
					mime,
					mime));	

		}			
		return w.toString();
	}
	public String resultsForm(Q query) {
		StringBuilder w = new StringBuilder();
		w.append(String.format("<form method=\"post\" action=\"/query\">",""));
		w.append(String.format("<input type=\"text\" name=\"name\" value=\"%s\" size=\"30\">&nbsp;",query.toString()));
		w.append(String.format("<input type=\"hidden\" value='%s' name='queryURI'>\n",uriReporter.getRequest().getOriginalRef()));
		w.append("<input type=\"submit\" value='Save search results'>&nbsp;");
		//output.write("</form>");
		//output.write(String.format("<form method=\"post\" action=\"%s/model\">",uriReporter.getBaseReference()));
		//output.write("<input type=\"submit\" value='Predict an endpoint'>&nbsp;");
		
		//output.write(String.format("<form method=\"post\" action=\"%s/algorithm\">",uriReporter.getBaseReference()));
		//output.write("<input type=\"submit\" value='Build a model&nbsp;'>&nbsp;");
		//output.write("<input type=\"submit\" value='Find similar compounds&nbsp;'>&nbsp;");
		//output.write("<input type=\"submit\" value='Search within results&nbsp;'>&nbsp;");
		
		w.append("</form>");	
		return w.toString();
	}
	public void header(Writer w, Q query) {
		try {

			Reference baseReference = uriReporter.getBaseReference();
			
			AmbitResource.writeTopHeader(w,
					collapsed?"Chemical compounds":"Chemical compound"
					,
					uriReporter.getRequest(),
					"",
					uriReporter.getDocumentation()
					);
			
			w.write("<table width='100%' bgcolor='#ffffff'>");
		
			w.write("<tr>");
			w.write("<td align='left' width='256px'>");
			w.write(String.format("<a href=\"http://ambit.sourceforge.net/intro.html\"><img src='%s/images/ambit-logo.png' width='256px' alt='%s' title='%s' border='0'></a>\n",baseReference,"AMBIT",baseReference));
			w.write("</td>");
			w.write("<td align='center'>");
			String query_smiles = "";
			String query_text = "";
			Form form = uriReporter.getRequest().getResourceRef().getQueryAsForm();
			try {
				
				query_text = form.getFirstValue("text");
			} catch (Exception x) {
				query_text = "";
			}			
			try {
				
				query_smiles = form.getFirstValue(QueryResource.search_param);
			} catch (Exception x) {
				query_smiles = "";
			}
			String query_property = "";
			try {

				query_property = form.getFirstValue("property");
			} catch (Exception x) {
				query_property = "";
			}
			String query_threshold = "";
			try {

				query_threshold = form.getFirstValue("threshold");
			} catch (Exception x) {
				query_threshold = "0.9";
			}
			String maxrecords = "";
			try {

				maxrecords = form.getFirstValue(QueryResource.max_hits);
			} catch (Exception x) {
				maxrecords = "1000";
			}		
			
			/** This determines if similarity searching will be done via smiles or via URL **/
			String type = "";
			try {

				type = form.getFirstValue("type");
			} catch (Exception x) {
				type = "smiles";
			}				
			w.write("<form action='' name='form' method='get'>\n");
			w.write(String.format("<input name='type' type='hidden' value='%s'>\n",type==null?"smiles":type));
			
			
			String hint= "";
			if (uriReporter.getRequest().getOriginalRef().toString().indexOf("similarity")>0) {
				w.write(String.format("<label for='%s'>SMILES</label>&nbsp;",QueryResource.search_param));
				w.write(String.format("<input name='%s' type='text' size='40' title='Enter SMILES' value='%s'>\n",QueryResource.search_param,query_smiles==null?"":query_smiles));
				w.write(String.format("&nbsp;<input type='button' value='Draw molecule' onClick='startEditor(\"%s\");'>",
						uriReporter.getBaseReference()));
				w.write("&nbsp;");
				w.write(String.format("<label for='threshold'>Threshold</label>&nbsp;"));
				w.write(String.format("<input name='threshold' type='text' title='Tanimoto coefficient threshold [0,1], default 0.9' size='20' value='%s'>\n",query_threshold==null?"0.9":query_threshold));
				
				hint = "Draw structure and search for similar compounds";
				//w.write("<input type='submit' value='Search'><br>");

			} else if (uriReporter.getRequest().getOriginalRef().toString().indexOf("compound")>0) {
				w.write(String.format("<input name='property' type='text' title='Enter property name (optional)'  size='20' value='%s'>\n",query_property==null?"":query_property));
				w.write("&nbsp;");
				w.write(String.format("<input name='%s' type='text' title='Enter molecule identifier, name or property value (e.g. benzene)'  size='40' value='%s'>\n",QueryResource.search_param,query_smiles==null?"":query_smiles));
				hint = "Search by property or identifier name (optional) and value";
				//w.write("<input type='submit' value='Search'><br>");
			} else {
				w.write("<table border='0'>");

				w.write("<tr><th>");
				w.write(String.format("<label for='%s' title='Substructure pattern defined by SMARTS language. Enter manually, or use Draw button on the right'>SMARTS</label>&nbsp;",QueryResource.search_param));
				w.write("</th><td>");
				w.write(String.format("<input name='%s' type='text'   size='60' value='%s'>\n",QueryResource.search_param,query_smiles==null?"":query_smiles));
				w.write(String.format("&nbsp;<input type='button' value='Draw substructure' onClick='startEditor(\"%s\");'>",
						uriReporter.getBaseReference()));	

				w.write("</td></tr>");
				w.write("<tr><th>");
				w.write(String.format("<label for='text' title='Any text, compound identifiers, property names and values, test names and values'>Keywords</label>"));
				w.write("</th><td>");
				
				w.write(String.format("<input name='text' type='text' title='Enter text to search for'  size='60' value='%s'><br>\n",query_text==null?"":query_text));
				w.write("</td></tr>");				
				hint = "Search for substructure and properties";				
				w.write("</table>");
			}
			
			
			
			//w.write(templates(baseReference));
			
			//w.write(baseReference.toString());

			//w.write("</form>\n"); moved in footer
			w.write(hint);		
			w.write("<br><b><i>This site and AMBIT REST services are under development!</i></b>");		
			w.write("</td>");
			w.write("<td align='left' valign='center' width='256px'>");
			//w.write(String.format("<a href=\"http://opentox.org\"><img src=\"%s/images/logo.png\" width=\"256\" alt=\"%s\" title='%s' border='0'></a>\n",baseReference,"AMBIT",baseReference));
			w.write("<input type='submit' value='Search'>");
			w.write("</td></tr>");
			w.write("</table>");		
			w.write("<hr>");	
			
			if (table) {
				
				output.write(String.format("<div><span class=\"left\">%s</span></div>",templates(baseReference)));
				
				output.write("<div class=\"rowwhite\"><span class=\"center\">");
					
				output.write(AmbitResource.jsTableSorter("results","pager"));
				output.write("<table class='tablesorter' id='results' border='0' cellpadding='0' cellspacing='1'>"); 
				
				output.write(String.format("<CAPTION CLASS=\"results\">Search results <input type='text' value='%s' readonly> &nbsp;Download as %s&nbsp;Max number of hits:%s</CAPTION>",
						query.toString(),
						downloadLinks(),
						String.format("<input name='max' type='text' title='Maximum number of hits' size='10' value='%s'>\n",maxrecords==null?"100":maxrecords)));//resultsForm(query)
						//,resultsForm(query)
				output.write("<thead><tr>");
				output.write(String.format("<th width='20'>#</th><th width='%d' bgcolor='#99CC00'>Compound</th>",cellSize.width)); //ECB42C
				List<Property> props = template2Header(getTemplate(),true);
				int hc = 0;
				for(Property p: props) {
					hc++;
					int max=10;
					int dot = 0;
					int end = p.getTitle().indexOf("Descriptor");
					if (end > 0) {
						dot = p.getTitle().lastIndexOf(".");
						if (dot<0) dot = 0; else dot++;
					} else end = p.getTitle().length();
					if ((end-dot)>max) end = dot + max;
					
					output.write(
						String.format("<th width='%d'><a href='%s' title='%s'>%s</a></th>",
								max,
								//(hc %2)==1?"class=\"results\"":"class=\"results_odd\"",
						p.getUrl(),p.getTitle(),p.getTitle().substring(dot,end)));
				}	
				output.write("</tr></thead><tbody><tr class=\"results\">");
				output.write("<th ></th><th ></th>");
				hc = 0;
				for(Property p: props) {
					hc++;
					output.write(
						String.format("<th align='center'><a href='%s' title='%s'>%s %s</a></th>",
						pReporter.getURI(p),
						p.getName(),
						p.getName(),p.getUnits()));
				}
			
				output.write("</tr>");
			}
			else {
				w.write(downloadLinks());
				w.append("<h4><div class=\"actions\"><span class=\"right\">");				
				w.write(resultsForm(query));
				w.append("</span></div></h4>\n");	

				output.write("<div id=\"div-1\">");
			}
			
			

		} catch (Exception x) {
			x.printStackTrace();
		}		
	};
	public void footer(Writer output, Q query) {
		try {
			if (table) {
				output.write("</tbody></table>");
				output.write("</span></div>");
			}
			else output.write("</div>");
			output.write("</form>\n");
			//output.write("</form>");			
			AmbitResource.writeHTMLFooter(output,
					"",
					uriReporter.getRequest()
					);
			output.flush();			
		} catch (Exception x) {}		
	};

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
	public String toURITable(IStructureRecord record) {
		String w = uriReporter.getURI(record);
		StringBuilder b = new StringBuilder();
		
		
		b.append(String.format("<tr class=\"results_%s\">",((count % 2)==0)?"even":"odd"));
		
		b.append(String.format("<td >%d<br>%s<br>%s</td>",
				count+1,
				String.format("<a href='%s/query/similarity?search=%s&type=url&threshold=0.85' title='Find similar compounds'><img src=\"%s/images/search.png\" border='0' alt='Find similar' title='Find similar'></a>",
							uriReporter.getBaseReference(),Reference.encode(w),uriReporter.getBaseReference()),
				String.format("<a href='%s/query/smarts?search=%s&type=url&max=100' title='Find substructure'><img src=\"%s/images/search.png\" border='0' alt='Find substructure' title='Find substructure'></a>",
							uriReporter.getBaseReference(),Reference.encode(w),uriReporter.getBaseReference())							
						));
		
		String imguri;
		
		if (hilightPredictions!= null) {
			imguri= String.format("%s?%s=%s&media=image/png",hilightPredictions,OpenTox.params.dataset_uri.toString(),w);
		}
		
		else imguri = w + "?media=image/png";		
				
		b.append(String.format(
				"<td ><a href=\"%s?media=text/html%s\"><img src=\"%s&w=%d&h=%d\" width='%d' height='%d' alt=\"%s\" title=\"%d\"/></a></td>",
				
				w,
				hilightPredictions==null?"":String.format("&%s=%s",OpenTox.params.model_uri,hilightPredictions),
				imguri, 
				cellSize.width,cellSize.height,
				cellSize.width,cellSize.height,
				w, record.getIdchemical()));


			List<Property> props = template2Header(getTemplate(),true);
			int col = 0;
			
			for(Property property: props) {
				col++;
				Object value = record.getProperty(property);
				//System.out.println(String.format("%s [%s] %s",property.getName(),property.getTitle(),value==null?null:value.toString()));
					
					boolean isLong = (value==null)?false:value.toString().length()>255;
					b.append(String.format("<td %s width='%s'>",
							//"class='results_col'",
							((count%2)==0)?
								((col % 2)==0)?"class='results'":"":
								((col % 2)==0)?"":"class='results_col'",
							isLong?"100":"100")) ; //"#EBEEF1"

					boolean isHTML = (value == null)?false:value.toString().indexOf("<html>")>=0;
					String searchValue = (value==null)?null:
						(
						isHTML?null:
						value.toString().length()>255?value.toString().substring(0,255):value.toString()
								);
					/*
					if (searchValue != null) {
						
						//?property=MW&search=100+..+200
						b.append(String.format("<a href=\"%s/compound?features=%s/feature_definition/%d&property=%s&search=%s\"><img src=\"%s/images/search.png\" border='0' alt='Search for %s=%s' title='Search for %s=%s'></a>", 
								uriReporter.getBaseReference(),
								uriReporter.getBaseReference(),
								property.getId(),
								Reference.encode(property.getName()),
								Reference.encode(value.toString()),
								uriReporter.getBaseReference(),		
								property.getName(),
								value.toString(),
								property.getName(),
								value.toString()							
						));
						//?property=MW&search=100+..+200
						b.append(String.format("&nbsp;<a href=\"%s/compound?features=%s/feature_definition/%d&search=%s\"><img src=\"%s/images/search.png\" border='0' alt='Relaxed search for %s' title='Relaxed search for %s'></a>", 
								uriReporter.getBaseReference(),
								uriReporter.getBaseReference(),
								property.getId(),
								Reference.encode(value.toString()),
								uriReporter.getBaseReference(),		
								value.toString(),
								value.toString()							
						));				
				
					}
					*/
					b.append("<div>");
					
					value = value==null?"":value.toString();
					/*
							value.toString().length()<40?value.toString():
							value.toString().length()<255?value.toString().substring(0,40):
							"See more";
							*/
					/* Edit link		
					b.append(String.format("<a href=\"%s\">%s</a>",
							String.format("%s/feature/compound/%d/feature_definition/%d", uriReporter.getBaseReference(),record.getIdchemical(),property.getId()),
							value));
					*/
					StringBuilder f = new StringBuilder();
					for (String ff: featureURI.getValuesArray(OpenTox.params.feature_uris.toString())) {
						f.append(String.format("&%s=%s", OpenTox.params.feature_uris,ff));
					}
					b.append(String.format("<a href=\"%s/compound?%s=%s%s/%d&property=%s&search=%s%s\">%s</a>", 
						uriReporter.getBaseReference(),
						OpenTox.params.feature_uris.toString(),
						uriReporter.getBaseReference(),
						PropertyResource.featuredef,
						property.getId(),
						Reference.encode(property.getName()),
						searchValue==null?"":Reference.encode(searchValue.toString()),
								
						f,
						value
					));
									
					b.append("</div>");
					b.append("</td>");
				}

			b.append("</tr>");		
	
		return b.toString();
	}		
	
	public String toURI(IStructureRecord record) {
		String w = uriReporter.getURI(record);
		StringBuilder b = new StringBuilder();
		
		
		String imguri;
		if (hilightPredictions!= null)
			imguri= String.format("%s?%s=%s&media=image/png",
				hilightPredictions,
				OpenTox.params.dataset_uri.toString(),w);
		else imguri=w+"?media=image/png";		
		
		b.append(String.format("<div id=\"div-1b1\"><input type=checkbox name=\"compound[]\" checked value=\"%d\"></div>",record.getIdchemical()));
		
		b.append(String.format(
				"<a href=\"%s\"><img src=\"%s&w=%d&h=%d\" width='%d' height='%d' alt=\"%s\" title=\"%d\"/></a>",
				
				w, imguri, 
				cellSize.width,cellSize.height,
				cellSize.width,cellSize.height,
				w, record.getIdchemical()));
		b.append("<div id=\"div-1d\">");

		String[][] s = new String[][] {
				{PropertyValueResource.featureKey,Property.opentox_CAS,"CAS RN"},
				{PropertyValueResource.featureKey,Property.opentox_EC,"EINECS"},
				{PropertyValueResource.featureKey,Property.opentox_Name,"Chemical name(s)"},
				{PropertyValueResource.featureKey,null,"All available feature values"},
		};
		for (String[] n:s)
				b.append(String.format("<a href=\"%s%s/%s\">%s</a><br>",w,n[0],n[1]==null?"":Reference.encode(n[1]),n[2]));
		
		s = new String[][] {
				{"/template",null,"Feature values by groups"},
				{TupleResource.resourceTag,null,"Feature values by dataset"},
				{PropertyResource.featuredef,null,"Features"},
				{null,null,"Model predictions",String.format("%s/model/null/predicted",uriReporter.getBaseReference().toString())},
		};		
		for (String[] n:s)
			if (n[0]==null)
				b.append(String.format("<a href=\"%s?%s=%s\">%s</a><br>",w,OpenTox.params.feature_uris.toString(),Reference.encode(n[3]),n[2]));
			else
				b.append(String.format("<a href=\"%s%s/%s\">%s</a><br>",w,n[0],n[1]==null?"":n[1],n[2]));
			
			List<Property> props = template2Header(getTemplate(),true);

			for(Property property: props) 
				if (property.getId()>0) {
					Object value = record.getProperty(property);
					b.append(String.format("<b>%s</b>&nbsp;%s<br>",
							property.getName(),value==null?"":
								value.toString()));
				}
			//b.append("</table>");
			b.append("</div>");		
	
		return b.toString();
	}		

	@Override
	public void close() throws SQLException {
		super.close();
	}
	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		super.setConnection(connection);
	}
}
