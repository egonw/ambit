package ambit2.waffles;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import ambit2.base.external.ShellException;
import ambit2.waffles.learn.options.WafflesLearnAlgorithm;
import ambit2.waffles.learn.options.WafflesLearnCommand;

public class ShellWafflesLearn extends ShellWaffles {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -682638346208264334L;
	
	protected WafflesLearnCommand command;
	protected WafflesLearnAlgorithm algorithm;
	
	public ShellWafflesLearn() throws ShellException {
		super();
	}
	
	public enum WafflesLearnOption {
		command,
		options,
		model,
		model_file,
		dataset,
		data_opts,
		pattern,
		labeled_set,
		training_set,
		data_opts1,
		unlabeled_set,
		test_set,
		data_opts2,
		algorithm,
		alg_opts
	}
	
	@Override
	protected void initialize() throws ShellException {
		this.tool= "waffles_learn";
		initialize(tool);
	}


	@Override
	protected synchronized List<String> prepareInput(String path, Properties in)
			throws ShellException {
		List<String> list = new ArrayList<String>();
		for (WafflesLearnOption option : WafflesLearnOption.values()) {
			String value = in.getProperty(option.name());
			if (value!=null) {
				switch (option) {
				case command: 
					try {
						this.command = WafflesLearnCommand.valueOf(value);
						list.add(value);
						break;
					} catch (IllegalArgumentException x) {
						throw new ShellException(this,String.format("Invalid command name '%s'", value));
					}
				case algorithm: 
					try {
						this.algorithm = WafflesLearnAlgorithm.valueOf(value);
						list.add(value);
						break;
					} catch (IllegalArgumentException x) {
						throw new ShellException(this,String.format("Invalid algorithm name '%s'", value));
					}
				case data_opts: {
					String[] dataOpts = value.split(" ");
					for (String dataOpt: dataOpts) list.add(dataOpt);
					break;
				}
				default: {
					list.add(value);
				}
				}		
				
			}
		}
		System.out.println(list);
		return list;	
	}

	@Override
	public synchronized Properties runShell(Properties in)
			throws ShellException {
		out.clear();
		return super.runShell(in);
	}

	/**
	 * 
	 * @param dataset
	 * @return file name of the model
	 * @throws ShellException
	 */
	public File train(File dataset,File model,WafflesLearnAlgorithm algorithm, String alg_opts) throws ShellException  {
		setOutputFile(model.getAbsolutePath());
		setOutProperty(WafflesLearnOption.model_file.name());
		Properties in = new Properties();
		in.put(WafflesLearnOption.command.name(), WafflesLearnCommand.train.name());
		in.put(WafflesLearnOption.algorithm.name(), algorithm.name());
		in.put(WafflesLearnOption.alg_opts.name(), alg_opts);
		in.put(WafflesLearnOption.dataset.name(), dataset.getAbsolutePath());
		in.put(WafflesLearnOption.data_opts.name(), "-ignore 0");
		Properties out = runShell(in);
		System.out.println(out.getProperty(getOutProperty()));
		return new File(out.getProperty(getOutProperty()));
	}
	/**
	 * 
	 * @param dataset
	 * @param model
	 * @param results file name
	 * @return results
	 * @throws ShellException
	 */
	public File predict(File dataset, File model, File results) throws ShellException  {
		setOutputFile(results.getAbsolutePath());
		setOutProperty(WafflesLearnOption.dataset.name());
		Properties in = new Properties();
		in.put(WafflesLearnOption.command.name(), WafflesLearnCommand.predict.name());
		in.put(WafflesLearnOption.model_file.name(),model.getAbsolutePath());
		in.put(WafflesLearnOption.dataset.name(), dataset.getAbsolutePath());
		in.put(WafflesLearnOption.data_opts.name(), "-ignore 0");
		Properties out = runShell(in);
		return new File(out.getProperty(getOutProperty()));
	}
}