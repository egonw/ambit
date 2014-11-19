package ambit2.rest.bundle;

import java.sql.Connection;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.restnet.db.update.CallableDBUpdateTask;

import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.update.bundle.CreateBundle;
import ambit2.db.update.bundle.DeleteBundle;
import ambit2.db.update.bundle.UpdateBundle;
import ambit2.rest.dataset.DatasetURIReporter;



/**
 * 
 * @author nina
 *
 */
public class CallableBundleCreator extends	CallableDBUpdateTask<SubstanceEndpointsBundle, Form, String> {
	protected DatasetURIReporter<IQueryRetrieval<SubstanceEndpointsBundle>,SubstanceEndpointsBundle> reporter;
	protected SubstanceEndpointsBundle item;
	public CallableBundleCreator(SubstanceEndpointsBundle item,
			DatasetURIReporter<IQueryRetrieval<SubstanceEndpointsBundle>,SubstanceEndpointsBundle> reporter,
			Method method, Form input,Connection connection, String token) {
		super(method, input, connection, token);
		this.item = item;
		this.reporter = reporter;
	}

	@Override
	protected SubstanceEndpointsBundle getTarget(Form input) throws Exception {
		if (Method.DELETE.equals(method)) { 
			return item;
		} else if (Method.POST.equals(method)) {
			item = new SubstanceEndpointsBundle();
			parseForm(input, item);
			return item;
		} else if (Method.PUT.equals(method)) {
			parseForm(input, item);
			return item;
		}
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	protected void parseForm(Form input, SubstanceEndpointsBundle bundle) {
		String name = input.getFirstValue(ISourceDataset.fields.title.name());
		if (name!=null)
			bundle.setName(input.getFirstValue(ISourceDataset.fields.title.name()));
		String licenseURI = input.getFirstValue(ISourceDataset.fields.license.name());
		if (licenseURI!=null)
			bundle.setLicenseURI(licenseURI);
		String maintainer = input.getFirstValue(ISourceDataset.fields.maintainer.name());
		if (maintainer!=null)
			bundle.setMaintainer(maintainer);
		String rightsHolder = input.getFirstValue(ISourceDataset.fields.rightsHolder.name());
		if (rightsHolder!=null)
			bundle.setrightsHolder(rightsHolder);
		String source = input.getFirstValue(ISourceDataset.fields.source.name());
		if (source != null)
			bundle.setSource(source);
		String url = input.getFirstValue(ISourceDataset.fields.url.name());
		if (url!=null)
			bundle.setURL(url);
		try {
			String stars = input.getFirstValue(ISourceDataset.fields.stars.name());
			if (stars!= null)
				bundle.setStars(Integer.parseInt(stars));
		} catch (Exception x) { bundle.setStars(5); }
		
	}
	@Override
	protected IQueryUpdate<? extends Object, SubstanceEndpointsBundle> createUpdate(
			SubstanceEndpointsBundle target) throws Exception {
		if (Method.POST.equals(method)) {
			return item!=null?new CreateBundle(item):null; //new AddSubstancesperBundle(user, group);
		}
		else if (Method.DELETE.equals(method)) {
			return item!=null?new DeleteBundle(item):null; //new DeleteSubstancesPerBundle(user,group);
		}
		else if (Method.PUT.equals(method)) 
			return item!=null?new UpdateBundle(item):null; 
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected String getURI(SubstanceEndpointsBundle item) throws Exception {
		return reporter.getURI(item);
	}

	@Override
	public String toString() {
		if (Method.POST.equals(method)) {
			return String.format("Create %s",item==null?"":item.toString());
		} else if (Method.PUT.equals(method)) {
			return String.format("Update %s",item==null?"":item.toString());
		} else if (Method.DELETE.equals(method)) {
			return String.format("Delete %s",item==null?"":item.toString());
		}
		return item==null?"Read":item.toString();
	}
}
