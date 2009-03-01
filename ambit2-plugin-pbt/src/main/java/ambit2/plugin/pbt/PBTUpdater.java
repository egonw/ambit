package ambit2.plugin.pbt;

import java.beans.PropertyChangeEvent;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import ambit2.core.data.IStructureRecord;
import ambit2.core.exceptions.AmbitException;
import ambit2.plugin.pbt.processors.PBTReader;
import ambit2.workflow.DBWorkflowContext;

import com.microworkflow.events.WorkflowContextListener;
import com.microworkflow.process.WorkflowContext;

public class PBTUpdater implements WorkflowContextListener {
	protected DataSource datasource= null;
	protected WorkflowContext context = null;
	protected IStructureRecord record = null;
	
	public PBTUpdater(WorkflowContext context) {
		context.addPropertyChangeListener(DBWorkflowContext.DATASOURCE, this);
		context.addPropertyChangeListener(DBWorkflowContext.DBCONNECTION_URI, this);
		context.addPropertyChangeListener(DBWorkflowContext.RECORD, this);
		context.addPropertyChangeListener(PBTWorkBook.PBT_WORKBOOK, this);
		this.context = context;
	}
	public void propertyChange(PropertyChangeEvent event) {
		if (DBWorkflowContext.DBCONNECTION_URI.equals(event.getPropertyName())) 	
			 datasource = null;	
		if (DBWorkflowContext.DATASOURCE.equals(event.getPropertyName())) {	
			 if (event.getNewValue() instanceof DataSource) 
				 update((DataSource)event.getNewValue());
		} else 	if (DBWorkflowContext.RECORD.equals(event.getPropertyName())) {
			 if (event.getNewValue() instanceof IStructureRecord) 
				 update((IStructureRecord)event.getNewValue());
		}

    }
	protected Connection getConnection() throws SQLException, AmbitException {
		if (datasource == null)
			throw new AmbitException("Undefined datasource");
        Connection c = datasource.getConnection();     
		if (datasource == null)
			throw new AmbitException("Undefined db connection"); 
		return c;
	}
	protected void update(DataSource ds) {
		datasource = ds;
	}
	protected void update(IStructureRecord newrecord) {
		if (newrecord == null) return;
		if (record != null) {
			if ((record.getIdchemical()==newrecord.getIdchemical()) && 
					(record.getIdstructure()==newrecord.getIdstructure()))
				return; //same record
		} 
		record = newrecord;
		PBTReader reader = new PBTReader();
		Connection c = null;
		try {
			c = getConnection();
			reader.setConnection(c);
			PBTWorkBook book = reader.process(record);
			if (book == null) book = new PBTWorkBook();
			book.setRecord(record);
			book.setModified(false);
			if (book != null) {
				context.put(PBTWorkBook.PBT_WORKBOOK, null);				
				context.put(PBTWorkBook.PBT_WORKBOOK, book);
			}
			
		} catch (Exception x) {
			context.put(DBWorkflowContext.ERROR, x);
		} finally {
			try {if (reader !=null) reader.close();} catch (Exception e) {};
			try {if (c !=null) c.close();} catch (Exception e) {};
		}
		
	}
}
