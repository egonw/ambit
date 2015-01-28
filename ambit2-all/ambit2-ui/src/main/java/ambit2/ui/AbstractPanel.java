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

package ambit2.ui;

import javax.swing.JComponent;
import javax.swing.JToolBar;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.windows.WindowsLookAndFeel;

public abstract class AbstractPanel<T> extends JToolBar {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3963305539191739942L;
	protected AbstractPanel() {
		// TODO Auto-generated constructor stub
	}
	public AbstractPanel(T object) {
		super();
		setFloatable(false);
		setRollover(false);
        putClientProperty("JToolBar.isRollover", Boolean.FALSE);
        putClientProperty(
                PlasticLookAndFeel.BORDER_STYLE_KEY,
                null);
        putClientProperty(
                WindowsLookAndFeel.BORDER_STYLE_KEY,
                null);
        putClientProperty(
                PlasticLookAndFeel.IS_3D_KEY,
                null);
		addSeparator();
		add(buildPanel(object));
	}
	public abstract JComponent buildPanel(final T object);
}
