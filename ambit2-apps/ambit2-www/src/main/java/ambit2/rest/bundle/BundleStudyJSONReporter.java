package ambit2.rest.bundle;

import net.idea.modbcum.i.IQueryCondition;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.facet.IFacet;
import net.idea.modbcum.p.DefaultAmbitProcessor;

import org.restlet.Request;

import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.facet.BundleRoleFacet;
import ambit2.db.facets.bundle.EndpointRoleByBundle;
import ambit2.db.processors.MasterDetailsProcessor;
import ambit2.db.substance.study.facet.SubstanceByCategoryFacet;
import ambit2.rest.facet.FacetJSONReporter;

public class BundleStudyJSONReporter<Q extends IQueryRetrieval<IFacet>> extends FacetJSONReporter<Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5511031336207765476L;
	protected SubstanceEndpointsBundle bundle;
	
	public BundleStudyJSONReporter(Request baseRef, String jsonp) {
		this(baseRef,jsonp,null);
	}
	public BundleStudyJSONReporter(Request request, String jsonp, SubstanceEndpointsBundle bundle) {
		super(request, jsonp);
		
		if (bundle != null) {
			getProcessors().clear();
			EndpointRoleByBundle q = new EndpointRoleByBundle(request.getRootRef().toString());
			q.setValue(bundle);
			MasterDetailsProcessor<IFacet,BundleRoleFacet,IQueryCondition> bundleReader = new MasterDetailsProcessor<IFacet,BundleRoleFacet,IQueryCondition>(q) {
				@Override
				protected IFacet processDetail(IFacet master,BundleRoleFacet detail) throws Exception {
					if (master instanceof SubstanceByCategoryFacet) {
						((SubstanceByCategoryFacet) master).setBundleRole(detail);
					}
					return master;
				}
			};
			bundleReader.setCloseConnection(false);
			getProcessors().add(bundleReader);
			getProcessors().add(new DefaultAmbitProcessor<IFacet,IFacet>() {
				public IFacet process(IFacet target) throws AmbitException {
					processItem(target);
					return target;
				};
			});			
		}
	}

}
