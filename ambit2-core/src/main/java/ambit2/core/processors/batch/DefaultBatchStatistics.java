package ambit2.core.processors.batch;

import java.util.Observable;

/**
 * 
 * Default implementation of {@link ambit2.io.batch.IBatchStatistics}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 29, 2006
 */
public class DefaultBatchStatistics extends Observable implements
		IBatchStatistics {
	protected long[] records = {0,0,0,0};
	protected long[] time_elapsed = {0,0,0,0};
	protected long frequency = 50;
	protected static final String blank = "";
	protected boolean inProgress = true;
	protected String resultCaption = "Found";

	public DefaultBatchStatistics() {
		super();
	}
	public void clear() {
	    for (int i=0; i < records.length; i++) {
	        records[i] = 0; time_elapsed[i] = 0;
	    }
	    inProgress = true;
        setChanged();
        notifyObservers();        
	    
	}
	/* (non-Javadoc)
     * @see ambit2.io.batch.IBatchStatistics#completed()
     */
    public void completed() {
        inProgress = false;
        setChanged();
        notifyObservers();        

    }
    public long getRecords(int recordType) {
        return records[recordType];
    }
    public void setRecords(int recordType, long number) {
        records[recordType] = number;
        setChanged();
        notifyObservers();        
    }
    public void increment(int recordType) {
        records[recordType]++;
        setChanged();
        notifyObservers();
    }
    public long getTimeElapsed() {
        return time_elapsed[IBatchStatistics.RECORDS_READ]+
        time_elapsed[IBatchStatistics.RECORDS_PROCESSED]+
        time_elapsed[IBatchStatistics.RECORDS_WRITTEN]+
        time_elapsed[IBatchStatistics.RECORDS_ERROR];

    }
    public long getTimeElapsed(int recordType) {
    	return  time_elapsed[recordType];
    }
    public void setTimeElapsed(int recordType, long milliseconds) {
    	time_elapsed[recordType] = milliseconds;
        setChanged();
        notifyObservers();            	
    }
    public  void incrementTimeElapsed(int recordType, long milliseconds) {
    	time_elapsed[recordType] += milliseconds;
        setChanged();
        notifyObservers();    	
    }
    
    public String toString() {
        if (!inProgress ||
                (records[IBatchStatistics.RECORDS_READ] % frequency) == 0) {
            long t = getTimeElapsed();
            
            StringBuffer b = new StringBuffer();
            b.append(resultCaption);
            b.append(" ");
            b.append(Long.toString(records[IBatchStatistics.RECORDS_READ]));
            b.append(" records in ");
            b.append(Long.toString(t));
            b.append(" ms");
            if (records[IBatchStatistics.RECORDS_READ] > 0) {
                b.append("(");
                long s = t/records[IBatchStatistics.RECORDS_READ];
                if (s > 1000) {
		            b.append(s/1000);
		            b.append(" s per record)");                	
                } else {	
		            b.append(s);
		            b.append(" ms per record)");
                }
            }
            return b.toString();
        }	
        else return blank;
    }
	public String getResultCaption() {
		return resultCaption;
	}
	public void setResultCaption(String resultCaption) {
		this.resultCaption = resultCaption;
	}
}
