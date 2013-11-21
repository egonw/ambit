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
	protected String companyName;
	
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
	protected String interpretationCriteria;
	protected PARAMS parameters;
	protected String reference;
	protected List<EffectRecord<ENDPOINT,CONDITIONS,UNIT>> effects;	
	public static enum _fields {
		uuid,
		owner,
		company,
		name,
		substance,
		referencesubstanceuuid,
		protocol,
		parameters,
		effects,
		interpretation,
		result,
		criteria
	}
	
	protected PROTOCOL protocol;
	
	public ProtocolApplication(PROTOCOL protocol) {
		setProtocol(protocol);
	}
	protected String documentUUID;
	public String getDocumentUUID() {
		return documentUUID;
	}
	public void setDocumentUUID(String documentUUID) {
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
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
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
		reference = null;
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
		
		if (getCompanyName()!=null) {
			b.append(",");
			b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_fields.name.name())));
			b.append(":\t");
			b.append(getCompanyName()==null?null:JSONUtils.jsonQuote(JSONUtils.jsonEscape(getCompanyName())));
		}
		b.append("}\n");

		
		b.append("\n\t},\n");		
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_fields.protocol.name())));
		b.append(":\t");
		b.append(protocol==null?null:protocol.toString());
		b.append(",\n");
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_fields.parameters.name())));
		b.append(":\t");		
		b.append(getParameters().toString());
		b.append(",\n");
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
		b.append(getEffects()==null?null:getEffects().toString());
		b.append("\n}");
		return b.toString();
	}
}
