package ambit2.rest.task;

import java.util.BitSet;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;

import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.base.processors.ProcessorsChain;
import ambit2.core.data.model.Algorithm;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.OpenTox;
import ambit2.rest.algorithm.AlgorithmURIReporter;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.model.builder.FingerprintsModelBuilder;

public class CallableFingerprintsModelCreator<USERID> extends CallableStructuresModelCreator<List<BitSet>,FingerprintsModelBuilder,USERID>  {
	

	public CallableFingerprintsModelCreator(Form form,
			Reference applicationRootReference,Context context,
			Algorithm algorithm,
			ModelURIReporter<IQueryRetrieval<ModelQueryResults>> reporter,
			AlgorithmURIReporter alg_reporter,
			USERID token) {

		super(form,
				applicationRootReference,
				context,
				algorithm,
				reporter,alg_reporter,
				new FingerprintsModelBuilder(applicationRootReference,
						reporter,
						alg_reporter,
						OpenTox.params.target.getValuesArray(form),
						OpenTox.params.parameters.getValuesArray(form),
						OpenTox.params.confidenceOf.getFirstValue(form)==null?null:OpenTox.params.confidenceOf.getFirstValue(form).toString()
				),
				token);
	
	}	


	
	protected ProcessorsChain<IStructureRecord, IBatchStatistics, IProcessor> createProcessors() throws Exception {

		ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor> p1 = 
			new ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor>();

		p1.add(new ProcessorStructureRetrieval());
		p1.add(new MoleculeReader());
		p1.add(new FingerprintGenerator());
		p1.add(new DefaultAmbitProcessor<BitSet,BitSet>() {
			public BitSet process(BitSet target) throws AmbitException {
				builder.getTrainingData().add(target);
				return target;
			}
		});
		p1.setAbortOnError(true);
		
		return p1;
	}
	
	@Override
	protected ModelQueryResults createModel() throws Exception {
		ModelQueryResults model = super.createModel();
		if ((model != null) && (model.getTrainingInstances()==null) && (sourceReference!=null))
			model.setTrainingInstances(sourceReference.toString());
		return model;
	}

}
