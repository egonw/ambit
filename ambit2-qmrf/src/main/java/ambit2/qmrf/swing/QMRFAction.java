/*
Copyright (C) 2005-2006  

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

package ambit2.qmrf.swing;

import java.awt.Container;

import javax.swing.Icon;

import ambit2.qmrf.QMRFData;
import ambit2.qmrf.swing.actions.AmbitAction;

public class QMRFAction extends AmbitAction {

	public QMRFAction(QMRFData userData, Container mainFrame) {
		super(userData, mainFrame);
	}

	public QMRFAction(QMRFData userData, Container mainFrame, String name) {
		super(userData, mainFrame, name);
	}

	public QMRFAction(QMRFData userData, Container mainFrame, String name, Icon icon) {
		super(userData, mainFrame, name, icon);
	}
	
	protected QMRFData getQMRFData() {
		return (QMRFData) userData;
	}

}


