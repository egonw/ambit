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

package ambit2.ui.test;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.junit.BeforeClass;
import org.junit.Test;

import ambit2.core.data.Profile;
import ambit2.core.data.Property;
import ambit2.ui.editors.SelectFieldsPanel;


/**
 * Test for {@link SelectFieldsPanel}
 * @author nina
 *
 */

public class SelectFieldsPanelTest {
    @BeforeClass public static void setUp() {

    }

    @Test public void test() throws Exception {
    	Profile props = new Profile();
    	props.add(new Property("CAS"));
    	props.add(new Property("CAS","CASNO"));
    	props.add(new Property("Name"));
    	Property p = new Property("Inchi");
    	p.setEnabled(true);
    	props.add(p);
    	props.add(new Property("XLogP"));
    	props.add(new Property("pKA"));
    	props.add(new Property("eHOMO"));
    	props.add(new Property("eLUMO"));
    	props.add(new Property("A"));
    	props.add(new Property("B"));
    	props.add(new Property("V"));
    	props.add(new Property("D"));
    	props.add(new Property("X"));
    	props.add(new Property("Y"));
    	props.add(new Property("Z"));
    	String help = "All available fields that can be exported are listed in the left pane. Select those fields you want to be exported and click \">\" button to move them to right pane."+
    		"You can move exported columns up and down to change the order they will be saved in .csv file. You can also specify Target Column Name if you wish to set some other caption than default.";
    	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());     
    	SelectFieldsPanel panel = new SelectFieldsPanel();
    	panel.setHelp(help);
    	panel.setObject(props);
    	panel.setBorder(BorderFactory.createEtchedBorder());
    	
    	JOptionPane.showMessageDialog(null,panel,panel.getClass().getName(),JOptionPane.PLAIN_MESSAGE);
    }
}
