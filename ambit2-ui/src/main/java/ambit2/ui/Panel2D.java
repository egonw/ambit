/*
Copyright Ideaconsult Ltd. (C) 2005-2007 

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

*/
package ambit2.ui;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EventObject;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.event.ICDKChangeListener;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;

import ambit2.base.interfaces.IAmbitEditor;
import ambit2.base.interfaces.IProcessor;
import ambit2.core.data.MoleculeTools;
import ambit2.rendering.CompoundImageTools;


/**
 * 2D structure diagram
 * @author Nina Jeliazkova
 *
 */
public class Panel2D<A extends IMoleculeEditAction> extends JPanel implements ICDKChangeListener, ComponentListener, IAmbitEditor<IAtomContainer>, PropertyChangeListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6293533800645578594L;
	public enum property_name {
		panel2d_molecule,
		panel2d_selected
	};
	protected CompoundImageTools tools;
	protected IAtomContainer atomContainer;
	protected IProcessor<IAtomContainer,IChemObjectSelection> selector=null;
	public IProcessor<IAtomContainer, IChemObjectSelection> getSelector() {
		return selector;
	}
	public void setSelector(IProcessor<IAtomContainer, IChemObjectSelection> selector) {
		this.selector = selector;
		image = null;
		repaint();
	}	
	
	protected Image image=null;
	protected boolean generate2d = true;
	protected boolean editable = false;
	protected A editAction;
	protected boolean atomNumbers = false;
	
	public boolean isAtomNumbers() {
		return atomNumbers;
	}
	public void setAtomNumbers(boolean atomNumbers) {
		this.atomNumbers = atomNumbers;
	}
	public Panel2D() {
		super();
		setEditable(true);
		tools = new CompoundImageTools();
		tools.setImageSize(getPreferredSize());
		image=tools.getDefaultImage();
		addComponentListener(this);
        addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
    			super.mouseClicked(e);        		
        		if (isEditable()) {
        			if  (e.getClickCount()==1) {
        				launchEditor(e.getComponent());
        			}
        		} else {
        			atomNumbers = ! atomNumbers;
        			image = null;
        			setAtomContainer(atomContainer, true);
        		}
        	}
        });
        setPreferredSize(new Dimension(150,150));
	}
	
	protected void launchEditor(Component parentComponent) {
			if (editAction == null) try {
				
				Class clazz = Class.forName("ambit2.jchempaint.editor.MoleculeEditAction");
				editAction = (A) clazz.newInstance();
				//editAction = new MoleculeEditAction(null);
			} catch (Exception x) {
				return ; //no JCP on the classpath ?
			}
			editAction.setParentComponent(parentComponent);
			editAction.setModal(true);
			editAction.setMolecule(
					getObject()==null?MoleculeTools.newMolecule(DefaultChemObjectBuilder.getInstance()):(IMolecule)getObject());
			editAction.actionPerformed(null);
			IMolecule molecule = editAction.getMolecule();
			if (molecule != null) {
				//to force 2D generation, otherwise the image is broken
    			//Iterator<IAtom> atoms = molecule.atoms();
    			//while (atoms.hasNext()) {atoms.next().setPoint2d(null);}
			}
			setAtomContainer(molecule, true);
	}
	@Override
	public void paint(Graphics g) {
		super.paintComponent(g);    
		if (image == null) try {
			image = tools.getImage(atomContainer,getSelector(),generate2d,atomNumbers);
		} catch (Exception x) {
			g.fillRect(0, 0, tools.getImageSize().width, tools.getImageSize().width);
			return;
		}
		
		g.drawImage(image,0,0,this);
	}
	
	public void setAtomContainer(IAtomContainer mol, boolean generate2d) {
		this.generate2d = generate2d;
		image = null;
		atomContainer = mol;
		repaint();
	}	
	public void setAtomContainer(IAtomContainer mol) {
		setAtomContainer(mol,false);
	}
	public void stateChanged(EventObject e) {
		Rectangle r = ((Component)e.getSource()).getBounds();
		tools.setImageSize(new Dimension(r.width,r.height));
		image = null;
		repaint();
	}

	public void componentResized(ComponentEvent e) {
		Rectangle r = ((Component)e.getSource()).getBounds();
		tools.setImageSize(new Dimension(r.width,r.height));
		image = null;
		repaint();
		
	}

	public JComponent getJComponent() {
		return this;
	}
	public IAtomContainer getObject() {
		return atomContainer;
	}
	public boolean isEditable() {
		return editable;
	}
	public void setEditable(boolean editable) {
		this.editable = editable;
		
	}
	public void setObject(IAtomContainer object) {
		setAtomContainer(object);
		
	}
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getNewValue() instanceof IAtomContainer) {
			try {
				property_name name = property_name.valueOf(evt.getPropertyName());
				switch (name) {
				case panel2d_molecule: {
					setAtomContainer((IAtomContainer)evt.getNewValue(),true);
					break;
				}
				case panel2d_selected: {
					setSelector((IProcessor<IAtomContainer,IChemObjectSelection>)evt.getNewValue());
					break;
				}
				}
			} catch (Exception x) {
				setAtomContainer((IAtomContainer)evt.getNewValue(),true);
			}
		}
		
	}
	public boolean confirm() {
		return true;
	}	
}


