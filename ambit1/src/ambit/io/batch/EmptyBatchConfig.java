/**
 * 
 */
package ambit.io.batch;

import java.io.OutputStream;

/**
 * @author Nina Jeliazkova
 *
 */
public class EmptyBatchConfig implements IBatchConfig {

	/**
	 * 
	 */
	public EmptyBatchConfig() {
		super();
	}

	/* (non-Javadoc)
	 * @see ambit.io.batch.IBatchConfig#deleteConfigOnSuccess()
	 */
	public boolean deleteConfigOnSuccess() {
		return false;
	}

	/* (non-Javadoc)
	 * @see ambit.io.batch.IBatchConfig#getSaveFrequency()
	 */
	public long getSaveFrequency() {
		return Integer.MAX_VALUE;
	}

	/* (non-Javadoc)
	 * @see ambit.io.batch.IBatchConfig#setSaveFrequency(long)
	 */
	public void setSaveFrequency(long records) {
	}

	/* (non-Javadoc)
	 * @see ambit.io.batch.IBatchConfig#delete()
	 */
	public void delete() {
	}

	/* (non-Javadoc)
	 * @see ambit.io.batch.IBatchConfig#save(java.io.OutputStream, ambit.io.batch.IBatch)
	 */
	public void save(OutputStream out, IBatch batch)
			throws BatchProcessingException {
	}

	/* (non-Javadoc)
	 * @see ambit.io.batch.IBatchConfig#save(ambit.io.batch.IBatch)
	 */
	public void save(IBatch batch) throws BatchProcessingException {
	}

	/* (non-Javadoc)
	 * @see ambit.io.batch.IBatchConfig#load()
	 */
	public IBatch load() throws BatchProcessingException {
		return null;
	}

}