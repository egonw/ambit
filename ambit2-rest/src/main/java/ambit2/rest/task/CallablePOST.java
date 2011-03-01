package ambit2.rest.task;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;

import ambit2.rest.OpenTox;
import ambit2.rest.task.dsl.OTAlgorithm;
import ambit2.rest.task.dsl.OTAlgorithms;
import ambit2.rest.task.dsl.OTDataset;
import ambit2.rest.task.dsl.OTSuperModel;

/**
 * Wrapper for remote tasks to be used as super service
 * @author nina
 *
 */
public class CallablePOST<USERID> extends CallableProtectedTask<USERID> {
	protected MediaType media; 
	protected Representation input;
	protected Status status;
	protected Reference applicationRootReference;

	protected OTSuperModel superModel;
	
	
	public CallablePOST(Form form,Reference root,USERID token) throws Exception {
		this(MediaType.TEXT_URI_LIST,form.getWebRepresentation(),root,token);
	}	
	

	public CallablePOST(MediaType media, 
			  Representation input,
			  Reference root,
			  USERID token) {
		this(media,input,1500,root,token);
	}
	public CallablePOST(MediaType media, 
			  Representation input,
			  long pollInterval,
			  Reference root,
			  USERID token) {
		super(token);
		this.media = media;
		this.input = input;
		this.applicationRootReference = root;
	}
	@Override
	public TaskResult doCall() throws Exception {

		//System.out.println(getClass().getName());
		long now = System.currentTimeMillis();
		Form form = new Form(input);
		String dataset_service = form.getFirstValue(OpenTox.params.dataset_service.toString());
		if (dataset_service==null) dataset_service = String.format("%s/dataset", applicationRootReference);
		else dataset_service = dataset_service.trim();
		String datasetURI = form.getFirstValue(OpenTox.params.dataset_uri.toString());
		if (datasetURI!=null) datasetURI = datasetURI.trim();
		
		String modelURI = form.getFirstValue(OpenTox.params.model_uri.toString());
		if (modelURI!=null) modelURI=modelURI.trim();
		String[] algoURIs = form.getValuesArray(OpenTox.params.algorithm_uri.toString());
		//removing algorithm_uri parameters, no need to pass to algorithms 
		form.removeAll(OpenTox.params.algorithm_uri.toString());
			
		OTDataset results = null;
		
		try { 
			if (modelURI != null) {
				//System.out.println(modelURI);
				//System.out.println(datasetURI);
				results = OTSuperModel.model(modelURI)
						.withDatasetService(dataset_service)
						.withParams(form)
						.process(OTDataset.dataset(datasetURI).withDatasetService(dataset_service));
			} else if (algoURIs != null) {
				
				OTAlgorithms algorithms = OTAlgorithms.algorithms();
				algorithms.withDatasetService(dataset_service);
				
				for (String algoUri : algoURIs)
					if (algoUri!=null) 
						algorithms.add(OTAlgorithm.algorithm(algoUri.trim()).withParams(form));
				
				results = algorithms.process(OTDataset.dataset(datasetURI).withDatasetService(dataset_service));
			}
			return new TaskResult(results.getUri().toString());
			
		} catch (Exception x) {
			//x.printStackTrace();
			throw x;
		} finally {
			//System.out.println(String.format("Elapsed %s", System.currentTimeMillis()-now));
		}
	}




}
