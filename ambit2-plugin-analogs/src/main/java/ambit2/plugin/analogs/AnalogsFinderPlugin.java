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

package ambit2.plugin.analogs;

import java.beans.PropertyChangeEvent;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ImageIcon;

import nplugins.shell.INPluginUI;
import nplugins.shell.INanoPlugin;
import nplugins.shell.application.Utils;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.DBWorkflowPlugin;
import ambit2.workflow.ui.QueryResultsPanel;
import ambit2.workflow.ui.UserInteractionEvent;
import ambit2.workflow.ui.WorkflowOptionsLauncher;

import com.microworkflow.process.Workflow;

public class AnalogsFinderPlugin extends DBWorkflowPlugin {
	protected WorkflowOptionsLauncher contextListener;
	public AnalogsFinderPlugin() {
		super();
		contextListener = new WorkflowOptionsLauncher(null);
		Vector<String> props = new Vector<String>();		
		props.add(UserInteractionEvent.PROPERTYNAME);
		props.add(DBWorkflowContext.ERROR);
		props.add(DBWorkflowContext.LOGININFO);
		props.add(DBWorkflowContext.DBCONNECTION_URI);
		props.add(DBWorkflowContext.DATASOURCE);
        props.add(DBWorkflowContext.DATASET);		
		contextListener.setProperties(props);
		contextListener.setWorkflowContext(getWorkflowContext());
		
	}
	@Override
	protected Workflow createWorkflow() {
		return new AnalogsFinderWorkflow();
	}
	public INPluginUI<INanoPlugin> createMainComponent() {
	    QueryResultsPanel results = new QueryResultsPanel(getWorkflowContext());
		Vector<String> p = new Vector<String>();
		p.add(DBWorkflowContext.STOREDQUERY);
		p.add(DBWorkflowContext.ERROR);
		results.setProperties(p);
		results.setAnimate(true);
		return results;
	}
	public ImageIcon getIcon() {
	    return Utils.createImageIcon("ambit2/plugin/analogs/images/molecule_16.png");
	}

	public int getOrder() {
		return 2;
	}

	public ResourceBundle getResourceBundle() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setParameters(String[] args) {
		// TODO Auto-generated method stub

	}

	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub

	}

	public int compareTo(INanoPlugin o) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public String toString() {
	    return "Find Analogs";
	}
}
