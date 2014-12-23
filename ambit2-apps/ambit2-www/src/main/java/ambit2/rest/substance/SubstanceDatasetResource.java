package ambit2.rest.substance;

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import net.idea.modbcum.i.IQueryCondition;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.r.QueryAbstractReporter;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.convertors.OutputWriterConvertor;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.NullNode;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.PropertyAnnotation;
import ambit2.base.data.PropertyAnnotations;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.Template;
import ambit2.base.data.study.ProtocolEffectRecord;
import ambit2.base.data.substance.SubstanceName;
import ambit2.base.data.substance.SubstanceOwner;
import ambit2.base.data.substance.SubstanceProperty;
import ambit2.base.data.substance.SubstancePublicName;
import ambit2.base.data.substance.SubstanceUUID;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.processors.MasterDetailsProcessor;
import ambit2.db.reporters.CSVReporter;
import ambit2.db.substance.study.ReadEffectRecordBySubstance;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.StringConvertor;
import ambit2.rest.dataset.ARFF3ColResourceReporter;
import ambit2.rest.dataset.ARFFResourceReporter;
import ambit2.rest.structure.CompoundJSONReporter;
import ambit2.rest.substance.owner.SubstanceByOwnerResource;

public class SubstanceDatasetResource<Q extends IQueryRetrieval<SubstanceRecord>> extends SubstanceByOwnerResource<Q> {
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
	public IProcessor<Q, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {
		/* workaround for clients not being able to set accept headers */
		Form acceptform = getResourceRef(getRequest()).getQueryAsForm();
		String media = acceptform.getFirstValue("accept-header");
		if (media != null) variant.setMediaType(new MediaType(media));

		String filenamePrefix = getRequest().getResourceRef().getPath();
		if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			QueryURIReporter r = (QueryURIReporter)getURIReporter(getRequest());
			return new StringConvertor(
					r,MediaType.TEXT_URI_LIST,filenamePrefix);
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_JAVASCRIPT)) {
			return createJSONReporter(filenamePrefix);	
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
			return createJSONReporter(filenamePrefix);	
		} else if (variant.getMediaType().equals(ChemicalMediaType.WEKA_ARFF)) {
			return createARFFReporter(filenamePrefix);
		} else if (variant.getMediaType().equals(ChemicalMediaType.THREECOL_ARFF)) {
			QueryAbstractReporter reporter = 
					new ARFF3ColResourceReporter<IQueryRetrieval<IStructureRecord>>(
					getTemplate(),getGroupProperties(),getRequest(), 
					String.format("%s%s",getRequest().getRootRef(),"")
					);			
			return new OutputWriterConvertor(reporter, ChemicalMediaType.THREECOL_ARFF,filenamePrefix);	
			

							
		} else if (variant.getMediaType().equals(MediaType.TEXT_CSV)) {
			return createCSVReporter(filenamePrefix);
		} else { //json by default
			return createJSONReporter(filenamePrefix);
		}
	}	
	
	protected IQueryRetrieval<ProtocolEffectRecord<String, String, String>> getEffectQuery() {
		return new ReadEffectRecordBySubstance();
	}
	protected IProcessor getCompositionProcessors() {
		return null;
	}

	protected IProcessor getPropertyProcessors(final boolean removeIdentifiers, final boolean removeStringProperties) {
		IQueryRetrieval<ProtocolEffectRecord<String, String, String>> queryP = getEffectQuery();
		
		MasterDetailsProcessor<SubstanceRecord,ProtocolEffectRecord<String, String, String>,IQueryCondition> effectReader = 
							new MasterDetailsProcessor<SubstanceRecord,ProtocolEffectRecord<String, String, String>,IQueryCondition>(
									queryP) {
			@Override
			protected SubstanceRecord processDetail(SubstanceRecord master,
					ProtocolEffectRecord<String, String, String> detail) throws Exception {
				
				if (detail != null) {
					if (detail.getTextValue() != null && detail.getTextValue().toString().startsWith("{")) {

						JsonNode node = dx.readTree(new StringReader(detail.getTextValue().toString()));

						List<String> guideline = detail.getProtocol().getGuideline();
						ILiteratureEntry ref = LiteratureEntry.getInstance(detail.getEndpoint(),
								guideline==null?null:guideline.size()==0?null:guideline.get(0));
						
						Iterator<Entry<String,JsonNode>> i = node.getFields();
						while (i.hasNext()) {
							Entry<String,JsonNode> val = i.next();
							
							SubstanceProperty key = new SubstanceProperty(
									detail.getProtocol().getTopCategory(),
									detail.getProtocol().getCategory(),
									val.getKey(),detail.getUnit(),
									ref);
							key.setIdentifier(detail.getSampleID()+"/" + val.getKey());

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
						boolean isTextValue = ((detail.getLoValue() == null) && (detail.getUpValue()==null));
						if (isTextValue && removeStringProperties) return master;
						
						JsonNode conditions = detail.getConditions()==null?null:dx.readTree(new StringReader(detail.getConditions()));
						
						PropertyAnnotations ann = new PropertyAnnotations();
						
						Iterator<Entry<String,JsonNode>> i = conditions==null?null:conditions.getFields();
						StringBuilder b = new StringBuilder();
						b.append(detail.getEndpoint());
						
						if (i!=null)
						while (i.hasNext()) {
							Entry<String,JsonNode> val = i.next();
							if (val.getValue() instanceof NullNode) continue;
							
							if (val.getValue().getTextValue()==null) 
								try {
									PropertyAnnotation a = new PropertyAnnotation();
									String unit = val.getValue().get("unit")==null?null:val.getValue().get("unit").asText();									
									a.setPredicate(val.getKey());
									if (unit==null)
										a.setObject(val.getValue().get("loValue").asText());
									else {
										a.setObject(String.format("%s %s",val.getValue().get("loValue").asText(),unit));
									}
									ann.add(a);
								} catch (Exception x) {}
							else {
								PropertyAnnotation a = new PropertyAnnotation();
								a.setPredicate(val.getKey());
								a.setObject(val.getValue().getTextValue());
								ann.add(a);
								try {
									if (!"".equals(val.getValue().getTextValue().trim())) {
										b.append(" [");
										b.append(val.getValue().getTextValue());
										b.append("]");
									}
								} catch (Exception x) {}
							}	
							
						}

						List<String> guideline = detail.getProtocol().getGuideline();
						ILiteratureEntry ref = LiteratureEntry.getInstance(detail.getEndpoint(),
								guideline==null?null:guideline.size()==0?null:guideline.get(0));
						
						SubstanceProperty key = new SubstanceProperty(
								detail.getProtocol().getTopCategory(),
								detail.getProtocol().getCategory(),
								detail.getEndpoint(),detail.getUnit(),ref
								);
						key.setIdentifier(detail.getSampleID());
						key.setAnnotations(ann);
						groupProperties.add(key);
						if ((detail.getLoValue()) == null && (detail.getUpValue()==null)) {
							master.setProperty(key, detail.getTextValue());
							key.setClazz(String.class);
						} else {
							master.setProperty(key, detail.getLoValue()==null?detail.getUpValue():detail.getLoValue());
							key.setClazz(Number.class);
						}	
					}
				}
				return master;
				
			}
		};
		return effectReader;
	}
	
	protected IProcessor<Q, Representation> createARFFReporter(String filenamePrefix) {

		return new OutputWriterConvertor<SubstanceRecord, Q>(
				new ARFFResourceReporter(getTemplate(),getGroupProperties(),getRequest(),
							String.format("%s%s",getRequest().getRootRef(),"")
						) {
					@Override
					protected void configurePropertyProcessors() {
						getProcessors().add(getPropertyProcessors(true,true));
					}

				},
				ChemicalMediaType.WEKA_ARFF,filenamePrefix);				

	}
	protected IProcessor<Q, Representation> createARFF3ColumnReporter(String filenamePrefix) {
		return new OutputWriterConvertor<SubstanceRecord, Q>(
				new ARFF3ColResourceReporter(getTemplate(),getGroupProperties(),getRequest(),
							String.format("%s%s",getRequest().getRootRef(),"")
						) {
					@Override
					protected void configurePropertyProcessors() {
						getProcessors().add(getPropertyProcessors(true,false));
					}

				},
				ChemicalMediaType.THREECOL_ARFF,filenamePrefix);				

	}
	protected IProcessor<Q, Representation> createCSVReporter(String filenamePrefix) {
		groupProperties.add(new SubstancePublicName());
		groupProperties.add(new SubstanceName());
		groupProperties.add(new SubstanceUUID());
		return new OutputWriterConvertor<SubstanceRecord, Q>(
				new CSVReporter(getRequest().getRootRef().toString(),getTemplate(),groupProperties,
						String.format("%s%s",getRequest().getRootRef(),"")
						) {
					
					@Override
					protected void configurePropertyProcessors() {
						getProcessors().add(getPropertyProcessors(false,false));
					}

				},
				MediaType.TEXT_CSV,filenamePrefix);				

	}
	protected IProcessor<Q, Representation> createJSONReporter(String filenamePrefix) {
		groupProperties.add(new SubstancePublicName());
		groupProperties.add(new SubstanceName());
		groupProperties.add(new SubstanceUUID());
		groupProperties.add(new SubstanceOwner());
		String jsonpcallback = getParams().getFirstValue("jsonp");
		if (jsonpcallback==null) jsonpcallback = getParams().getFirstValue("callback");
		return new OutputWriterConvertor(
				new CompoundJSONReporter(getTemplate(),getGroupProperties(),folders,null,getRequest(),
						getRequest().getRootRef().toString(),false,jsonpcallback) {
					@Override
					protected String getURI(IStructureRecord item) {
						return SubstanceRecord.getURI(urlPrefix, ((SubstanceRecord)item));
					}
					@Override
					protected void configurePropertyProcessors() {
						IProcessor p = getCompositionProcessors();
						if (p!=null) getProcessors().add(p);
						getProcessors().add(getPropertyProcessors(false,false));
					}
				},
				MediaType.APPLICATION_JSON,filenamePrefix);
	}
}
