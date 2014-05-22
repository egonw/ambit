package ambit2.rest.substance.study;

import java.io.Writer;
import java.util.ArrayList;
import java.util.logging.Level;

import org.restlet.Request;

import ambit2.base.data.PropertyAnnotations;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.data.substance.ExternalIdentifier;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.json.JSONUtils;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.processors.MasterDetailsProcessor;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;
import ambit2.db.search.IParameterizedQuery;
import ambit2.db.search.IQueryCondition;
import ambit2.db.substance.ids.ReadSubstanceIdentifiers;
import ambit2.db.substance.study.ReadEffectRecord;
import ambit2.rest.ResourceDoc;
import ambit2.rest.substance.SubstanceURIReporter;

/**
 * Substance composition JSON serialization
 * @author nina
 *
 * @param <Q>
 */
public class SubstanceStudyJSONReporter<Q extends IQueryRetrieval<ProtocolApplication>> extends QueryReporter<ProtocolApplication,Q,Writer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 410930501401847402L;
	protected String comma = null;
	protected String jsonpCallback = null;

	protected final SubstanceURIReporter<IQueryRetrieval<SubstanceRecord>> substanceReporter;
	
	public SubstanceStudyJSONReporter(Request request, ResourceDoc doc,String jsonpCallback) {
		super();
		substanceReporter = new SubstanceURIReporter<IQueryRetrieval<SubstanceRecord>>(request, null);
		this.jsonpCallback = JSONUtils.jsonSanitizeCallback(jsonpCallback);
		getProcessors().clear();
		IQueryRetrieval<EffectRecord<String, String, String>> queryP = new ReadEffectRecord(); 
		MasterDetailsProcessor<ProtocolApplication,EffectRecord<String, String, String>,IQueryCondition> effectReader = 
							new MasterDetailsProcessor<ProtocolApplication,EffectRecord<String, String, String>,IQueryCondition>(queryP) {
			@Override
			protected void configureQuery(
					ProtocolApplication target,
					IParameterizedQuery<ProtocolApplication, EffectRecord<String, String, String>, IQueryCondition> query)
					throws AmbitException {
							super.configureQuery(target, query);
			}
			@Override
			protected ProtocolApplication processDetail(ProtocolApplication master,
					EffectRecord detail) throws Exception {
				if (detail!=null)
					master.addEffect(detail);
				return master;
			}
		};
		effectReader.setCloseConnection(false);
		getProcessors().add(effectReader);
		getProcessors().add(new DefaultAmbitProcessor<ProtocolApplication,ProtocolApplication>() {
			public ProtocolApplication process(ProtocolApplication target) throws AmbitException {
				processItem(target);
				return target;
			};
		});			
	}

	@Override
	public Object processItem(ProtocolApplication item) throws AmbitException {
		try {
			if (item==null) return null;
			if (comma!=null) getOutput().write(comma);
			getOutput().write(item.toString());
			comma = ",";
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		}
		return item;
	}

	@Override
	public void footer(Writer output, Q query) {
		try {
			output.write("\n]\n}");
			
			if (jsonpCallback!=null) {
				output.write(");");
			}			
		} catch (Exception x) {}
	};
	
	
	@Override
	public void header(Writer output, Q query) {
		try {
			if (jsonpCallback!=null) {
				output.write(jsonpCallback);
				output.write("(");
			}					
			output.write("{\"study\":[\n");
		} catch (Exception x) {
			x.printStackTrace();
		}
	};
	
	@Override
	public String getFileExtension() {
		return null;//"json";
	}
	@Override
	public void open() throws DbAmbitException {
		
	}
}
