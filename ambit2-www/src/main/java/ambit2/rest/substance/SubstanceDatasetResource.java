package ambit2.rest.substance;

import java.io.StringReader;
import java.util.Iterator;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.NullNode;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.PropertyAnnotation;
import ambit2.base.data.PropertyAnnotations;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.Template;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.substance.SubstanceName;
import ambit2.base.data.substance.SubstanceProperty;
import ambit2.base.data.substance.SubstancePublicName;
import ambit2.base.data.substance.SubstanceUUID;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.processors.MasterDetailsProcessor;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.CSVReporter;
import ambit2.db.search.IQueryCondition;
import ambit2.db.substance.ReadSubstanceByOwner;
import ambit2.db.substance.study.ReadEffectRecordBySubstance;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.OutputWriterConvertor;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.StringConvertor;
import ambit2.rest.dataset.ARFF3ColResourceReporter;
import ambit2.rest.dataset.ARFFResourceReporter;
import ambit2.rest.structure.CompoundJSONReporter;
import ambit2.rest.substance.owner.SubstanceByOwnerResource;

public class SubstanceDatasetResource extends SubstanceByOwnerResource {
	protected Template template;
	protected Profile groupProperties;
	protected String[] folders;
	protected ObjectMapper dx = new ObjectMapper();
	
	public SubstanceDatasetResource() {
		super();
		setHtmlbyTemplate(true);
		template = new Template();
		groupProperties = new Profile<Property>();
	}

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] {
				ChemicalMediaType.WEKA_ARFF,
				MediaType.TEXT_CSV,
				ChemicalMediaType.THREECOL_ARFF
				});
				
	}
	@Override
	public String getTemplateName() {
		return "_datasetsubstance.ftl";
	}

	public Template getTemplate() {
		return template;
	}
	public void setTemplate(Template template) {
		this.template = (template==null)?new Template(null):template;

	}
	public Profile getGroupProperties() {
		return groupProperties;
	}
	public void setGroupProperties(Profile groupProperties) {
		this.groupProperties = groupProperties;
	}
	
	@Override
	public IProcessor<ReadSubstanceByOwner, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {
		/* workaround for clients not being able to set accept headers */
		Form acceptform = getResourceRef(getRequest()).getQueryAsForm();
		String media = acceptform.getFirstValue("accept-header");
		if (media != null) variant.setMediaType(new MediaType(media));

		String filenamePrefix = getRequest().getResourceRef().getPath();
		if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			QueryURIReporter r = (QueryURIReporter)getURIReporter();
			r.setDelimiter("\n");
			return new StringConvertor(
					r,MediaType.TEXT_URI_LIST,filenamePrefix);
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_JAVASCRIPT)) {
			return createJSONReporter(filenamePrefix);	
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
			return createJSONReporter(filenamePrefix);	
		} else if (variant.getMediaType().equals(ChemicalMediaType.WEKA_ARFF)) {
			return createARFFReporter(filenamePrefix);
		} else if (variant.getMediaType().equals(ChemicalMediaType.THREECOL_ARFF)) {
			return new OutputWriterConvertor<SubstanceRecord, ReadSubstanceByOwner>(
					new ARFF3ColResourceReporter(getTemplate(),getGroupProperties(),getRequest(),getDocumentation(),
								String.format("%s%s",getRequest().getRootRef(),"")
							),
					ChemicalMediaType.THREECOL_ARFF,filenamePrefix);				
		} else if (variant.getMediaType().equals(MediaType.TEXT_CSV)) {
			return createCSVReporter(filenamePrefix);
		} else { //json by default
			return createJSONReporter(filenamePrefix);
		}
	}	
	
	protected IProcessor getPropertyProcessors(final boolean removeIdentifiers, final boolean removeStringProperties) {
		IQueryRetrieval<EffectRecord<String, String, String>> queryP = new ReadEffectRecordBySubstance(); 
		MasterDetailsProcessor<SubstanceRecord,EffectRecord<String, String, String>,IQueryCondition> effectReader = 
							new MasterDetailsProcessor<SubstanceRecord,EffectRecord<String, String, String>,IQueryCondition>(
									new ReadEffectRecordBySubstance()) {
			@Override
			protected SubstanceRecord processDetail(SubstanceRecord master,
					EffectRecord<String, String, String> detail) throws Exception {
				if (detail != null) {
					if (detail.getTextValue() != null && detail.getTextValue().toString().startsWith("{")) {

						JsonNode node = dx.readTree(new StringReader(detail.getTextValue().toString()));
						
						Iterator<Entry<String,JsonNode>> i = node.getFields();
						while (i.hasNext()) {
							Entry<String,JsonNode> val = i.next();
							
							Property key = new SubstanceProperty(val.getKey(),detail.getSampleID());
							key.setUnits(detail.getUnit());
							key.setLabel(detail.getEndpoint());
							groupProperties.add(key);
							if (val.getValue().get("loValue")!=null) {
								master.setProperty(key, val.getValue().get("loValue").asInt());
								key.setClazz(Number.class);
							} else {
								master.setProperty(key, val.getValue().getTextValue());
								key.setClazz(String.class);
							}
						}
					} else {
						boolean isTextValue = (detail.getLoValue() == null);
						if (isTextValue && removeStringProperties) return master;
						
						JsonNode conditions = dx.readTree(new StringReader(detail.getConditions()));
						PropertyAnnotations ann = new PropertyAnnotations();
						
						Iterator<Entry<String,JsonNode>> i = conditions.getFields();
						StringBuilder b = new StringBuilder();
						b.append(detail.getEndpoint());
						
						while (i.hasNext()) {
							Entry<String,JsonNode> val = i.next();
							if (val.getValue() instanceof NullNode) continue;
							PropertyAnnotation a = new PropertyAnnotation();
							a.setPredicate(val.getKey());
							if (val.getValue().getTextValue()==null) 
								a.setObject(val.getValue().get("loValue").asText());
							else {
								a.setObject(val.getValue().getTextValue());
								try {
									if (!"".equals(val.getValue().getTextValue().trim())) {
										b.append(" [");
										b.append(val.getValue().getTextValue());
										b.append("]");
									}
								} catch (Exception x) {}
							}	
							ann.add(a);
						}

						Property key = new SubstanceProperty(b.toString(),detail.getSampleID());
						key.setUnits(detail.getUnit());
						key.setAnnotations(ann);
						key.setLabel(detail.getEndpoint());
						groupProperties.add(key);
						if (detail.getLoValue() == null) {
							master.setProperty(key, detail.getTextValue());
							key.setClazz(String.class);
						} else {
							master.setProperty(key, detail.getLoValue());
							key.setClazz(Number.class);
						}	
					}
				}
				return master;
				
			}
		};
		return effectReader;
	}
	
	protected IProcessor<ReadSubstanceByOwner, Representation> createARFFReporter(String filenamePrefix) {

		return new OutputWriterConvertor<SubstanceRecord, ReadSubstanceByOwner>(
				new ARFFResourceReporter(getTemplate(),getGroupProperties(),getRequest(),getDocumentation(),
							String.format("%s%s",getRequest().getRootRef(),"")
						) {
					@Override
					protected void configurePropertyProcessors() {
						getProcessors().add(getPropertyProcessors(true,true));
					}

				},
				ChemicalMediaType.WEKA_ARFF,filenamePrefix);				

	}
	protected IProcessor<ReadSubstanceByOwner, Representation> createARFF3ColumnReporter(String filenamePrefix) {
		return new OutputWriterConvertor<SubstanceRecord, ReadSubstanceByOwner>(
				new ARFF3ColResourceReporter(getTemplate(),getGroupProperties(),getRequest(),getDocumentation(),
							String.format("%s%s",getRequest().getRootRef(),"")
						) {
					@Override
					protected void configurePropertyProcessors() {
						getProcessors().add(getPropertyProcessors(true,false));
					}

				},
				ChemicalMediaType.THREECOL_ARFF,filenamePrefix);				

	}
	protected IProcessor<ReadSubstanceByOwner, Representation> createCSVReporter(String filenamePrefix) {
		groupProperties.add(new SubstancePublicName());
		groupProperties.add(new SubstanceName());
		groupProperties.add(new SubstanceUUID());
		return new OutputWriterConvertor<SubstanceRecord, ReadSubstanceByOwner>(
				new CSVReporter(getTemplate(),groupProperties,
						String.format("%s%s",getRequest().getRootRef(),"")
						) {
					
					@Override
					protected void configurePropertyProcessors() {
						getProcessors().add(getPropertyProcessors(false,false));
					}

				},
				MediaType.TEXT_CSV,filenamePrefix);				

	}
	protected IProcessor<ReadSubstanceByOwner, Representation> createJSONReporter(String filenamePrefix) {
		groupProperties.add(new SubstancePublicName());
		groupProperties.add(new SubstanceName());
		groupProperties.add(new SubstanceUUID());
		String jsonpcallback = getParams().getFirstValue("jsonp");
		if (jsonpcallback==null) jsonpcallback = getParams().getFirstValue("callback");
		return new OutputWriterConvertor(
				new CompoundJSONReporter(getTemplate(),getGroupProperties(),folders,getRequest(),
						getDocumentation(),
						getRequest().getRootRef().toString(),false,jsonpcallback) {
					@Override
					protected String getURI(IStructureRecord item) {
						return SubstanceRecord.getURI(urlPrefix, ((SubstanceRecord)item));
					}
					@Override
					protected void configurePropertyProcessors() {
						getProcessors().add(getPropertyProcessors(false,false));
					}
				},
				MediaType.APPLICATION_JSON,filenamePrefix);
	}
}
