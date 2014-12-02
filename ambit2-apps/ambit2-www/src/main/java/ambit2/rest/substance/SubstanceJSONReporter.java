package ambit2.rest.substance;

import java.io.Writer;
import java.util.ArrayList;
import java.util.logging.Level;

import net.idea.modbcum.i.IParameterizedQuery;
import net.idea.modbcum.i.IQueryCondition;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;

import org.restlet.Request;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.substance.ExternalIdentifier;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.facet.BundleRoleFacet;
import ambit2.base.json.JSONUtils;
import ambit2.db.facets.bundle.SubstanceRoleByBundle;
import ambit2.db.processors.MasterDetailsProcessor;
import ambit2.db.substance.ids.ReadSubstanceIdentifiers;

public class SubstanceJSONReporter<Q extends IQueryRetrieval<SubstanceRecord>> extends SubstanceURIReporter<Q> {
	protected String comma = null;
	protected String jsonpCallback = null;
	protected SubstanceEndpointsBundle[] bundles;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2315457985592934727L;
	public SubstanceJSONReporter(Request request, String jsonpCallback,SubstanceEndpointsBundle[] bundles) {
		super(request);
		this.bundles = bundles;
		this.jsonpCallback = JSONUtils.jsonSanitizeCallback(jsonpCallback);
		
		getProcessors().clear();
		IQueryRetrieval<ExternalIdentifier> queryP = new ReadSubstanceIdentifiers(); 
		MasterDetailsProcessor<SubstanceRecord,ExternalIdentifier,IQueryCondition> idReader = 
							new MasterDetailsProcessor<SubstanceRecord,ExternalIdentifier,IQueryCondition>(queryP) {
			@Override
			protected void configureQuery(
							SubstanceRecord target,
							IParameterizedQuery<SubstanceRecord, ExternalIdentifier, IQueryCondition> query)
							throws AmbitException {
							query.setFieldname(target);
			}
			@Override
			protected SubstanceRecord processDetail(
							SubstanceRecord target,
							ExternalIdentifier detail)	throws Exception {
				if (target.getExternalids()==null) target.setExternalids(new ArrayList<ExternalIdentifier>());
						target.getExternalids().add(detail);
				return target;
			}
		};
		idReader.setCloseConnection(false);
		getProcessors().add(idReader);
		
		
		if (bundles!=null && bundles.length>0) {
			SubstanceRoleByBundle q = new SubstanceRoleByBundle(request.getRootRef().toString());
			q.setValue(bundles[0]);
			MasterDetailsProcessor<SubstanceRecord,BundleRoleFacet,IQueryCondition> bundleReader = new MasterDetailsProcessor<SubstanceRecord,BundleRoleFacet,IQueryCondition>(q) {
				@Override
				protected SubstanceRecord processDetail(SubstanceRecord master,BundleRoleFacet detail) throws Exception {
					master.clearFacets();
					master.addFacet(detail);
					return master;
				}
			};
			bundleReader.setCloseConnection(false);
			getProcessors().add(bundleReader);
		}			
		
		getProcessors().add(new DefaultAmbitProcessor<SubstanceRecord,SubstanceRecord>() {
			public SubstanceRecord process(SubstanceRecord target) throws AmbitException {
				processItem(target);
				return target;
			};
		});			
	}
	public void header(java.io.Writer output, Q query) {
		try {
			if (jsonpCallback!=null) {
				output.write(jsonpCallback);
				output.write("(");
			}
			output.write("{\n");
			output.write("\n\"substance\":[");
			
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		}
	};
	
	public void footer(java.io.Writer output, Q query) {
		try {
			output.write("\n\t]");
			
		} catch (Exception x) {}
		try {
			output.write("\n}\n");
			
			if (jsonpCallback!=null) {
				output.write(");");
			}
		} catch (Exception x) {}
	};
	
	@Override
	public Object processItem(SubstanceRecord item) throws AmbitException {
		try {
			Writer writer = getOutput();
			if (comma!=null) writer.write(comma);
			writer.write(item.toJSON(getBaseReference().toString()));
			comma = ",";
		} catch (Exception x) {
			logger.log(java.util.logging.Level.SEVERE,x.getMessage(),x);
		}
		return item;
	}

}
