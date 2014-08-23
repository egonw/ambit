package ambit2.base.data.study;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.json.JSONUtils;

public class ProtocolApplication<PROTOCOL,PARAMS,ENDPOINT,CONDITIONS,UNIT> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 747315722852709360L;
	protected ReliabilityParams reliability = new ReliabilityParams();
	protected String interpretationCriteria;
	protected PARAMS parameters;
	protected Citation reference;
	protected List<EffectRecord<ENDPOINT,CONDITIONS,UNIT>> effects;	
	protected String companyName;
	protected PROTOCOL protocol;
	
	
	public ReliabilityParams getReliability() {
		return reliability;
	}
	public void setReliability(ReliabilityParams reliability) {
		this.reliability = reliability;
	}
	protected String referenceSubstanceUUID;
	
	public String getReferenceSubstanceUUID() {
		return referenceSubstanceUUID;
	}
	public void setReferenceSubstanceUUID(String referenceSubstanceUUID) {
		this.referenceSubstanceUUID = referenceSubstanceUUID;
	}
	protected String substanceUUID;
	protected String companyUUID;
	
	public String getCompanyUUID() {
		return companyUUID;
	}
	public void setCompanyUUID(String companyUUID) {
		this.companyUUID = companyUUID;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getSubstanceUUID() {
		return substanceUUID;
	}
	public void setSubstanceUUID(String substanceUUID) {
		this.substanceUUID = substanceUUID;
	}
	protected String interpretationResult;
	public String getInterpretationResult() {
		return interpretationResult;
	}
	public void setInterpretationResult(String interpretationResult) {
		this.interpretationResult = interpretationResult;
	}
	public String getInterpretationCriteria() {
		return interpretationCriteria;
	}
	public void setInterpretationCriteria(String interpretationCriteria) {
		this.interpretationCriteria = interpretationCriteria;
	}
	public static enum _fields {
		uuid,
		owner,
		company,
		name,
		substance,
		referencesubstanceuuid,
		protocol,
		citation,
		parameters,
		effects,
		interpretation,
		result,
		criteria,
		reliability
	}
	
	public ProtocolApplication(PROTOCOL protocol) {
		setProtocol(protocol);
	}
	protected String documentUUID;
	public String getDocumentUUID() {
		return documentUUID;
	}
	public void setDocumentUUID(String documentUUID) {
		int slashpos = documentUUID.indexOf("/");
		if (slashpos > 0)
			this.documentUUID = documentUUID.substring(0, slashpos);
		else
			this.documentUUID = documentUUID;
	}
	public PROTOCOL getProtocol() {
		return protocol;
	}
	public void setProtocol(PROTOCOL protocol) {
		this.protocol = protocol;
	}
	public PARAMS getParameters() {
		return parameters;
	}
	public void setParameters(PARAMS parameters) {
		this.parameters = parameters;
	}
	public String getReference() {
		return reference==null?null:reference.getTitle();
	}
	public void setReference(String reference) {
		if (this.reference==null) this.reference = new Citation(reference); 
		else this.reference.setTitle(reference);
	}
	public String getReferenceYear() {
		return reference==null?null:reference.getYear();
	}
	public void setReferenceYear(String year) {
		if (this.reference==null) this.reference = new Citation("",year);
		else this.reference.setYear(year);
	}
	public String getReferenceOwner() {
		return reference==null?null:reference.getOwner();
	}
	public void setReferenceOwner(String owner) {
		if (this.reference==null) this.reference = new Citation("","");
		this.reference.setOwner(owner);
	}
	public List<EffectRecord<ENDPOINT, CONDITIONS, UNIT>> getEffects() {
		return effects;
	}
	public void setEffects(List<EffectRecord<ENDPOINT, CONDITIONS, UNIT>> effects) {
		this.effects = effects;
	}
	public void addEffect(EffectRecord<ENDPOINT,CONDITIONS,UNIT> effect) {
		if (this.effects==null) effects = new ArrayList<EffectRecord<ENDPOINT,CONDITIONS,UNIT>>();
		effects.add(effect);
	}
	
	public void clear() {
		documentUUID = null;
		if (effects!=null) effects.clear();
		if (reliability!=null) reliability.clear();
		if (reference!=null) reference.clear();
		parameters = null;
		protocol = null;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("{\n");
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_fields.uuid.name())));
		b.append(":\t");
		b.append(getDocumentUUID()==null?null:JSONUtils.jsonQuote(JSONUtils.jsonEscape(getDocumentUUID().toString())));
		b.append(",\n");
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_fields.owner.name())));
		b.append(":\t{\n\t");
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_fields.substance.name())));
		b.append(":\t{");
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_fields.uuid.name())));
		b.append(":\t");
		b.append(getSubstanceUUID()==null?null:JSONUtils.jsonQuote(JSONUtils.jsonEscape(getSubstanceUUID().toString())));
		if (getReferenceSubstanceUUID()!=null) {
			b.append(",");
			b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_fields.referencesubstanceuuid.name())));
			b.append(":\t");
			b.append(getSubstanceUUID()==null?null:JSONUtils.jsonQuote(JSONUtils.jsonEscape(getReferenceSubstanceUUID().toString())));
		}
		b.append("},\n");
		
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_fields.company.name())));
		b.append(":\t{");
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_fields.uuid.name())));
		b.append(":\t");
		b.append(getCompanyUUID()==null?null:JSONUtils.jsonQuote(JSONUtils.jsonEscape(getCompanyUUID().toString())));
		
		b.append(",");
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_fields.name.name())));
		b.append(":\t");
		b.append(getCompanyName()==null?null:JSONUtils.jsonQuote(JSONUtils.jsonEscape(getCompanyName())));
		
		b.append("}\n");

		
		b.append("\n\t},\n");
		
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_fields.citation.name())));
		b.append(":\t");
		b.append((this.reference==null)?"null":this.reference.toString());
		b.append(",\n\t");
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_fields.protocol.name())));
		b.append(":\t");
		b.append(protocol==null?null:protocol.toString());
		b.append(",\n");
		//params
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_fields.parameters.name())));
		b.append(":\t");		
		b.append(getParameters()==null?"{}":getParameters().toString());
		b.append(",\n");
		//reliability
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_fields.reliability.name())));
		b.append(":\t");		
		b.append(getReliability().toString());
		b.append(",\n");
		//result
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_fields.interpretation.name())));
		b.append(":\t{\n\t");
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_fields.result.name())));
		b.append(":\t");		
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(getInterpretationResult())));		
		if (getInterpretationCriteria()!=null) {
			b.append(",");
			b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_fields.criteria.name())));
			b.append(":\t");		
			b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(getInterpretationCriteria())));
		}
		b.append("\n\t},\n");		
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_fields.effects.name())));
		b.append(":\t");
		b.append(((getEffects()==null)||(getEffects().size()==0))?"[{\"endpoint\": \"\",\"conditions\": {},  \"result\": {  }}]":getEffects().toString());
		 
		b.append("\n}");
		return b.toString();
	}
}

class Citation {
	String year;
	String title;
	String owner;
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Citation(String title) {
		this.title = title;
	}
	
	public Citation(String title,String year) {
		this(title);
		this.year = year;
	}
	public void clear() {
		title = null; year = null;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("{");
		b.append("\"title\"");
		b.append(":\t");
		b.append(getTitle()==null?null:JSONUtils.jsonQuote(JSONUtils.jsonEscape(getTitle())));
		b.append(",\t");
		b.append("\"year\"");
		b.append(":\t");
		b.append(getYear()==null?null:JSONUtils.jsonQuote(JSONUtils.jsonEscape(getYear())));
		b.append(",\t");
		b.append("\"owner\"");
		b.append(":\t");
		b.append(getOwner()==null?null:JSONUtils.jsonQuote(JSONUtils.jsonEscape(getOwner())));
		b.append("}");
		return b.toString();
	}
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
}
