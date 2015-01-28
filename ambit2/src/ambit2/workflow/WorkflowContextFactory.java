/*
Copyright (C) 2005-2008  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit2.workflow;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import ambit2.database.DatasourceFactory;
import ambit2.repository.IQuery;
import ambit2.repository.QueryID;
import ambit2.repository.QueryParam;

import com.microworkflow.process.WorkflowContext;
import com.microworkflow.ui.IWorkflowContextFactory;

public class WorkflowContextFactory implements IWorkflowContextFactory {
	protected WorkflowContext workflowContext;	
	public WorkflowContextFactory() {
		workflowContext = new DBWorkflowContext();
		IQuery q = new QueryID() {
			@Override
			public String getSQL() {
				return "insert ignore into query_results (idquery,idstructure,selected) select ?,idstructure,1 from structure limit 10";
			}
			@Override
			public List<QueryParam> getParameters() {
				List<QueryParam> p = new ArrayList<QueryParam>();
				p.add(new QueryParam<Integer>(Integer.class,getId()));
				return p;
			}
		};
		q.setName("test");
		workflowContext.put("Query",q);
		
		try {
			DataSource datasource = DatasourceFactory.getDataSource(
					DatasourceFactory.getConnectionURI("jdbc:mysql", "localhost", "3306", "ambit2", "guest","guest" ));
			
			workflowContext.put(DBWorkflowContext.DATASOURCE,datasource);
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
	
	public WorkflowContext getWorkflowContext() {
		return workflowContext;
	}


}


