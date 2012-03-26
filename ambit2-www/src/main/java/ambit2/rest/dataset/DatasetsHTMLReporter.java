package ambit2.rest.dataset;

import java.io.StringWriter;
import java.io.Writer;
import java.net.URLEncoder;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.SourceDataset;
import ambit2.base.exceptions.AmbitException;
import ambit2.core.processors.structure.key.IStructureKey;
import ambit2.core.processors.structure.key.IStructureKey.Matcher;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.AmbitResource;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.ResourceDoc;
import ambit2.rest.facet.DatasetChemicalsQualityStatsResource;
import ambit2.rest.facet.DatasetStrucTypeStatsResource;
import ambit2.rest.facet.DatasetStructureQualityStatsResource;
import ambit2.rest.facet.DatasetsByEndpoint;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.query.QLabelQueryResource;
import ambit2.rest.query.QueryResource;
import ambit2.rest.structure.CompoundResource;
import ambit2.rest.structure.DisplayMode;

/**Generates html page for {@link QueryDatasetResource}
 * @author nina
 *
 */
public class DatasetsHTMLReporter extends QueryHTMLReporter<ISourceDataset, IQueryRetrieval<ISourceDataset>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7959033048710547839L;
	public static String fileUploadField = "file";
	public DatasetsHTMLReporter(ResourceDoc doc) {
		this(null,DisplayMode.table,doc,false);
	}
	public DatasetsHTMLReporter(Request baseRef,DisplayMode _dmode,ResourceDoc doc,boolean headless) {
		this(baseRef,baseRef,_dmode,doc,headless);
	}
	public DatasetsHTMLReporter(Request baseRef,Request originalRef,DisplayMode _dmode,ResourceDoc doc,boolean headless) {
		super(baseRef,_dmode,doc,headless);
	}
	@Override
	protected QueryURIReporter createURIReporter(Request request, ResourceDoc doc) {
		return new DatasetURIReporter<IQueryRetrieval<ISourceDataset>>(request,doc);
	}
	@Override
	public void header(Writer w, IQueryRetrieval<ISourceDataset> query) {
		try {
			if (!headless)
			AmbitResource.writeHTMLHeader(w,query.toString(),uriReporter.getRequest(),
					getUriReporter().getResourceRef(),
					getUriReporter()==null?null:getUriReporter().getDocumentation());
	

		} catch (Exception x) {
			x.printStackTrace();
		}
		/**
		 * /dataset
		 */

		if (_dmode.isCollapsed()) { 
			uploadUI("",w, query);
			try {
				w.write(String.format("<a href='%s/query%s?%s=%s&condition=startswith' title='List datasets by endpoints'>%s</a><br>",
						uriReporter.getBaseReference(),
						DatasetsByEndpoint.resource,
						MetadatasetResource.search_features.feature_sameas,
						URLEncoder.encode("http://www.opentox.org/echaEndpoints.owl"),
						"Datasets by endpoints"));
			} catch (Exception x) {
				
			}
			
			String alphabet = "abcdefghijklmnopqrstuvwxyz";  
			try {
				w.write(String.format("<a href='?search=' title='List all datasets'>%s</a>&nbsp","All"));
				w.write(String.format("<a href='' title='Refresh this page'>%s</a>&nbsp","Refresh"));
				w.write("|&nbsp;");
				for (int i=0; i < alphabet.length(); i++) {
					String search = alphabet.substring(i,i+1);
					w.write(String.format("<a href='?search=^%s' title='Search for datasets with name staring with %s'>%s</a>&nbsp",
								search.toUpperCase(),search.toUpperCase(),search.toUpperCase()));
				}
				w.write("|&nbsp;");
				for (int i=0; i < alphabet.length(); i++) {
					String search = alphabet.substring(i,i+1);
					w.write(String.format("<a href='?search=^%s' title='Search for datasets with name staring with %s'>%s</a>&nbsp",
								search.toLowerCase(),search.toLowerCase(),search.toLowerCase()));
				}
				w.write("|&nbsp;");
				for (int i=0; i < 10; i++) {
					w.write(String.format("<a href='?search=^%s' title='Search for datasets with name staring with %s'>%s</a>&nbsp",
								i,i,i));
				}			
				w.write("<hr>");
			} catch (Exception x) {
				
			}
			
			String page = Long.toString(query.getPage());
			Form form = uriReporter.getResourceRef().getQueryAsForm();
			try {
				
				page = form.getFirstValue("page")==null?page:form.getFirstValue("page");
			} catch (Exception x) {
				page = Long.toString(query.getPage());
			}			
			String pageSize =  Long.toString(query.getPageSize());
		
			try {
				
				pageSize = form.getFirstValue("pagesize")==null? Long.toString(query.getPageSize()):form.getFirstValue("pagesize");
			} catch (Exception x) {
				pageSize = Long.toString(query.getPageSize());
			}	
			String search = "";
			try {
				
				search = form.getFirstValue("search");
			} catch (Exception x) {
				search = "";
			}			
			try {

				output.write("<div><span class=\"center\">");
			output.write("<form method='GET' action=''>");
			output.write(String.format("<b>Page:</b><input name='page' type='text' title='Page' size='10' value='%s'>\n",page==null?"0":page));
			if (search !=null) output.write(String.format("<input name='search' type='hidden' value='%s'>\n",search));
			output.write(String.format("<b>Page size:</b><input name='pagesize' type='text' title='Page size' size='10' value='%s'>\n",pageSize==null?"50":pageSize));
			output.write("<input type='submit' value='Refresh'>");			
			output.write("</form>");
			output.write("</span></div><p>");

			} catch (Exception x) {
				
			} finally {
				
			}
			

		} 
		
		
			

		/**
		 * else /dataset/{id}/metadata
		 */
		
			
	}
	@Override
	public void footer(Writer output, IQueryRetrieval<ISourceDataset> query) {
		try {
			if (!headless)
			AmbitResource.writeHTMLFooter(output,query.toString(),uriReporter.getRequest());
			output.flush();
		} catch (Exception x) {
			
		}
	}
	public void uploadUI(String uri, Writer output, IQueryRetrieval<ISourceDataset> query) {		
		try {
			output.write(String.format("<p><a href='#' onClick=\"javascript:toggleDiv('upload');\">Upload new dataset</a></p>"));
			
			output.write(String.format("<div id='%s' style='display: %s;''>","upload","none"));
			
			output.write(AmbitResource.printWidgetHeader("Upload"));
			output.write(AmbitResource.printWidgetContentHeader(""));
			output.write("<p>");
			String[][] methods = new String[][] {
					{"post","Add new dataset","Adds all compounds and data from the file, even empty structures."},
					{"put","Import properties","Import properties only for compounds from the file, which could be found in the database"}
			};
			output.write("<table width='95%' border='0' >");
			output.write("<tr>");
			for (int i=0; i < methods.length;i ++) {
				String[] method = methods[i];
				output.write("<td width='50%'>\n");
				output.write("<table border='0' width='95%'>");
				output.write("<caption>");
				output.write(String.format("<label accesskey='F' title='%s'>%s</label>",
						method[2],
						String.format("%s (SDF, MOL, SMI, CSV, TXT, ToxML (.xml) file)",method[1])
				)); 	
				output.write("</caption>");
				output.write("<tbody>");
				output.write(String.format("<form method=\"post\" action=\"%s?method=%s\" ENCTYPE=\"multipart/form-data\">",uri,method[0]));
				//file
				output.write("<tr bgcolor='#F4F2EB'>");
				output.write("<th>File<label title='Mandatory'>*</label></th>");
				output.write("<td>");
				output.write(String.format("<input type=\"file\" name=\"%s\" accept=\"%s\" title='%s' size=\"60\">",
						fileUploadField,
						ChemicalMediaType.CHEMICAL_MDLSDF.toString(),
						String.format("%s (SDF, MOL, SMI, CSV, TXT, ToxML (.xml) file)",method[1]))); 
				output.write("</td>");
				output.write("</tr>");
				//title
				output.write("<tr bgcolor='#F4F2EB'>");
				output.write("<th>Dataset name</th>");
				output.write("<td>");
				output.write(String.format("<input type=\"text\" name='title' title='%s' size=\"60\">","Dataset name (dc:title)")); 
				output.write("</td>");
				output.write("</tr>");
				//match
				output.write("<tr bgcolor='#F4F2EB'>");
				output.write("<th>Match</th>");
				output.write("<td>");
				output.write("<select name='match'>");
				for (Matcher matcher : IStructureKey.Matcher.values())
					output.write(String.format("<option title='%s' value='%s' %s>%s</option>",
							    "On import, finds the same compound in the database by matching with the selected criteria \""+matcher.getDescription()+"\"\n",
								matcher.toString(),
								IStructureKey.Matcher.CAS.equals(matcher)?"selected":"",
								(matcher.getDescription().length()>60)?matcher.getDescription().substring(0,60)+"...":matcher.getDescription()));
				output.write("</select>");
				output.write("</td>");
				output.write("</tr>");

				//URL
				output.write("<tr bgcolor='#F4F2EB'>");
				output.write("<th>URL</th>");
				output.write("<td>");
				output.write(String.format("<input type=\"text\" name='seeAlso' title='%s' size=\"60\">","Related URL (rdfs:seeAlso)")); 
				output.write("</td>");
				
				output.write("</tr>");
				
				output.write("<tr bgcolor='#F4F2EB'>");
				output.write("<th>License</th>");
				output.write("<td>");
				output.write("<select name='license'>");
				for (ISourceDataset.license license : ISourceDataset.license.values())
					output.write(String.format("<option title='%s' value='%s'>%s</option>",
							    license.getTitle(),
								license.getURI(),
								license.getURI()));
				output.write("</select>");
				output.write("</td>");
				output.write("</tr>");

				output.write("<tr bgcolor='#F4F2EB'><td align='right' colspan='2'><input type='submit' value='Submit'></td></tr>");
				output.write("</form>");
				output.write("</tbody>");
				output.write("</table>");
				
				output.write("</td>\n");
			}
			output.write("</tr>");
			output.write("</table>");
			output.write("</p>");
			output.write(AmbitResource.printWidgetContentFooter());
			output.write(AmbitResource.printWidgetFooter());
			output.write("</div>");
		} catch (Exception x) {}
		
	}
	@Override
	public Object processItem(ISourceDataset dataset) throws AmbitException {
		try {
			StringWriter w = new StringWriter();
			uriReporter.setOutput(w);
			uriReporter.processItem(dataset);
			

			String paging = "page=0&pagesize=10";
			if (_dmode.isCollapsed()) {
				
				output.write("<div id=\"div-1b\">");

				output.write("<div class=\"rowwhite\"><span class=\"left\">");
				output.write("&nbsp;");
				output.write(String.format(
						"<a href=\"%s%s?%s\"><img src=\"%s/images/table.png\" alt=\"compounds\" title=\"Browse compounds\" border=\"0\"/></a>",
						w.toString(),
						CompoundResource.compound,
						paging,
						uriReporter.getBaseReference().toString()));	
				
				output.write("&nbsp;");
				output.write(String.format(
						"<a href=\"%s%s\"><img src=\"%s/images/feature.png\" alt=\"features\" title=\"Retrieve feature definitions\" border=\"0\"/></a>",
						w.toString(),
						PropertyResource.featuredef,
						uriReporter.getBaseReference().toString()));	

				
				output.write("&nbsp;");
				output.write(String.format(
						"<a href=\"%s%s?%s\"><img src=\"%s/images/search.png\" alt=\"/smarts\" title=\"Search compounds by SMARTS\" border=\"0\"/></a>",
						w.toString(),
						"/smarts",
						paging,
						uriReporter.getBaseReference().toString()));
				output.write("&nbsp;");
				
				output.write(String.format(
						"<a href=\"%s%s?%s\"><img src=\"%s/images/search.png\" alt=\"/similarity\" title=\"Search for similar compounds within this dataset\" border=\"0\"/></a>",
						w.toString(),
						"/similarity",
						paging,
						uriReporter.getBaseReference().toString()));
				output.write("&nbsp;");				
				
				output.write("</span><span class=\"center\">");
				MediaType[] mimes = {ChemicalMediaType.CHEMICAL_MDLSDF,

						ChemicalMediaType.CHEMICAL_CML,
						ChemicalMediaType.CHEMICAL_SMILES,						
						MediaType.TEXT_URI_LIST,
						MediaType.APPLICATION_PDF,
						MediaType.TEXT_CSV,
						ChemicalMediaType.WEKA_ARFF,
						MediaType.APPLICATION_RDF_XML
						};
				String[] image = {
						"sdf.jpg",
						"cml.jpg",
						"smi.png",						
						"link.png",
						"pdf.png",
						"excel.png",
						"weka.jpg",
						"rdf.gif"
						
				};		
				for (int i=0;i<mimes.length;i++) {
					MediaType mime = mimes[i];
					output.write("&nbsp;");
					output.write(String.format(
							"<a href=\"%s%s?media=%s&%s\"  ><img src=\"%s/images/%s\" alt=\"%s\" title=\"%s\" border=\"0\"/></a>",
							w.toString(),
							"",
							//CompoundResource.compound,
							Reference.encode(mime.toString()),
							paging,
							uriReporter.getBaseReference().toString(),
							image[i],
							mime,
							mime));	
				}				
	

				output.write("&nbsp;");	

				output.write(String.format(
						"&nbsp;<a href=\"%s%s\">[Metadata]</a>",
						w.toString(),
						"/metadata"));
				output.write(String.format(
						"&nbsp;<a href=\"%s?%s\">%s</a>",
						w.toString(),
						paging,
						(dataset.getName()==null)||(dataset.getName().equals(""))?Integer.toString(dataset.getID()):dataset.getName()
						));
				output.write("</span></div>");
				output.write("</div>");
			}  else 
				renderMetadata(dataset, w.toString(),null);
	

		} catch (Exception x) {
			Context.getCurrentLogger().severe(x.getMessage());
		}
		return null;
	}

	protected Object renderMetadata(ISourceDataset dataset,String uri,IQueryRetrieval<ISourceDataset> query)  throws AmbitException  {
		try {
			//output.write("<h4>Dataset metadata</h4>");
			
			output.write(AmbitResource.printWidgetHeader(String.format("<a href='%s' target=_blank>%s</a>",uri,dataset.getName())));
					//String.format("<a href='#' onClick=\"javascript:toggleDiv('dataset_%d');\">More</a>\n",dataset.getID())));

			//	output.write(String.format("<div id='dataset_%d' style='float:right;display: %s;''>\n",dataset.getID(),"none"));		
			output.write(AmbitResource.printWidgetContentHeader(""));
			output.write("<p>");	
			output.write("<form method='post' action='?method=put'>\n");
			output.write("<table width='90%'>\n");
			output.write(String.format("<tr><th>%s</th><td>%s</td></tr>\n", "Dataset URI",uri));
			output.write(String.format("<tr><th>%s</th><td><input type='text' size='60' name='title' value='%s' %s></td></tr>\n", 
						"Dataset name",dataset.getName(),headless?"readonly":""));
			
			String licenseLabel = dataset.getLicenseURI();
			try {
				if (dataset.getLicenseURI()!=null)
				for (ISourceDataset.license license : ISourceDataset.license.values()) 
					if (license.getURI().equals(dataset.getLicenseURI())) {
						licenseLabel = license.getTitle();
					}
				
			} catch (Exception x) {}
			
			StringBuilder select = new StringBuilder();
			select.append("<select name='licenseOptions'>\n");
			ISourceDataset.license selected = null;
			for (ISourceDataset.license l : ISourceDataset.license.values()) {
				select.append(String.format("<option value='%s' %s>%s</option>\n",
						l.getURI(),
						l.getURI().equals(dataset.getLicenseURI())?"selected='selected'":"",
						l.getTitle()));
				if ((selected==null) & l.getURI().equals(dataset.getLicenseURI())) 
					selected = l;
			}
			select.append(String.format("<option value='Other' %s>Other</option>\n",selected==null?"selected='selected'":""));			
			select.append("</select>");
			
			if (dataset.getLicenseURI()!=null)
				output.write(String.format("<tr><th>%s</th><td>%s<br><input type='text' size='60' name='license' title='%s' value='%s' %s></td></tr>\n", 
							"License/Rights",
							select.toString(),
							licenseLabel==null?dataset.getLicenseURI():licenseLabel,
							dataset.getLicenseURI()==null?"":dataset.getLicenseURI(),
							headless?"readonly":""
								
							));
			else
				output.write(String.format("<tr><th>License</th><td><input type='text' size='60' name='license' title='Enter license URI' value='' %s ></td></tr>\n",
						headless?"readonly":"")); 
			
			if (dataset.getrightsHolder()!=null)
				output.write(String.format("<tr><th>%s</th><td><input type='text' size='60' title='Rights holder (URI)' name='rightsHolder' value='%s' %s ></td></tr>\n", 
							"Rights holder",
							dataset.getrightsHolder(),
							headless?"readonly":""));

			else
				output.write(String.format(
						"<tr><th>Rights holder</th><td><input type='text' size='60' title='Rights holder (URI)' name='rightsHolder' value=' ' %s ></td></tr>\n",
						headless?"readonly":"")); 			
			
			if (!headless)
			output.write(String.format("<tr><th>%s</th><td><input align='bottom' type=\"submit\" value=\"%s\"></td></tr>\n", "","Update"));								
			output.write(String.format("<tr><th>%s</th><td>%s</td></tr>\n", "Source",dataset.getSource()));
			//don't write ip addresses
			if ((dataset instanceof SourceDataset) && ((SourceDataset)dataset).getURL().startsWith("http"))
				output.write(String.format("<tr><th>%s</th><td>%s</td></tr>\n", "See also",((SourceDataset)dataset).getURL()));
			
			output.write("</table>");
			output.write("</form>");
		
			output.write(String.format("<a href='%s'>%s</a>&nbsp;\n", uri, "Browse the dataset"));
			output.write(String.format("<a href='%s/compounds'>%s</a>&nbsp;\n", uri, "Browse the compounds only"));
			output.write(String.format("<a href='%s/feature'>%s</a>&nbsp;\n", uri, "Browse the dataset features"));
			
			
			output.write(String.format("<a href='%s%s%s' target='_blank'>%s</a>&nbsp;\n", 
					uri,QueryResource.query_resource,DatasetStrucTypeStatsResource.resource,"Structure type statistics"));
			output.write(String.format("<a href='%s%s%s' target='_blank'>%s</a>&nbsp;\n", 
					uri,QueryResource.query_resource,DatasetChemicalsQualityStatsResource.resource,"Consensus label statistics"));
			output.write(String.format("<a href='%s%s%s' target='_blank'>%s</a>&nbsp;\n", 
					uri,QueryResource.query_resource,DatasetStructureQualityStatsResource.resource,"Structure quality label statistics"));
			

			output.write(String.format("<a href='%s/similarity?search=c1ccccc1' target='_blank'>%s</a>&nbsp;\n", uri, "Search for similar compounds within this dataset"));
			output.write(String.format("<a href='%s/smarts?search=c1ccccc1' target='_blank'>%s</a>&nbsp;\n", uri,"Search compounds by SMARTS"));


			output.write(String.format("<a href='%s/chart/bar?dataset_uri=%s&param=sk1024'  target='_blank'>%s</a>&nbsp;\n", 
					uriReporter.getBaseReference(),uri,"Structure fragments bar chart"));
			output.write(String.format("<a href='%s/chart/bar?dataset_uri=%s&param=fp1024' target='_blank'>%s</a>&nbsp;\n", 
					uriReporter.getBaseReference(),uri,"Hashed fingerprints bar chart"));			
			//output.write("</div>\n");
			output.write("</p>");
			output.write(AmbitResource.printWidgetContentFooter());
			output.write(AmbitResource.printWidgetFooter());
			//output.write("<h4>Add more data to this dataset</h4>");
			//uploadUI(uri,output, query);
		} catch (Exception x) {
			
		}
		return dataset;
	}

}
/*
public String getReCaptchaHtml() {
ReCaptcha recaptcha = createReCaptcha();
return recaptcha.createRecaptchaHtml("You did not type the captcha correctly", new Properties());
}

private ReCaptcha createReCaptcha() {
String publicKey = //your public key
String privateKey = //your private key
return ReCaptchaFactory.newReCaptcha(publicKey, privateKey, true);
}

@ValidationMethod(on = "submit")
public void captchaValidation(ValidationErrors errors) {
    ReCaptchaResponse response = createReCaptcha().checkAnswer(context.getRequest().getRemoteAddr(),
            context.getRequest().getParameter("recaptcha_challenge_field"),
            context.getRequest().getParameter("recaptcha_response_field"));
    if (!response.isValid()) {
        errors.add("Captcha", new SimpleError("You didn't type the captcha correctly!"));
    }
}
*/