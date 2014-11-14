package ambit2.rest.similarity.space;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.restnet.c.ResourceDoc;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.db.simiparity.space.QMap;
import ambit2.rest.QueryURIReporter;

public class QMapURIReporter  extends QueryURIReporter<QMap, IQueryRetrieval<QMap>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5366595019468858610L;
	public QMapURIReporter(Reference baseRef,ResourceDoc doc) {
		super(baseRef,doc);
	}
	public QMapURIReporter(Request ref,ResourceDoc doc) {
		super(ref,doc);
	}
	public QMapURIReporter() {
		this((Request)null,null);
	}
	@Override
	public String getURI(String ref, QMap record) {
		if (record.getId()>0)
			return String.format("%s%s/%d%s",ref,QMapResource.qmap,record.getId(),getDelimiter());
		else
			return String.format("%s%s",ref,QMapResource.qmap);

	}

}