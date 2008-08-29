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

package ambit2.plugin.usermgr;

import java.beans.PropertyChangeEvent;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

import com.microworkflow.process.Workflow;

import nplugins.shell.INPluginUI;
import nplugins.shell.INanoPlugin;
import nplugins.shell.PluginMainPanel;
import ambit2.workflow.DBWorkflowPlugin;

public class UserManagerPlugin extends DBWorkflowPlugin {
	
	@Override
	protected Workflow createWorkflow() {
		return new UserManagerWorkflow();
	}


	public INPluginUI<INanoPlugin> createMainComponent() {
		// TODO Auto-generated method stub
		return null;
	}
	public ImageIcon getIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getOrder() {
		// TODO Auto-generated method stub
		return 0;
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

}
