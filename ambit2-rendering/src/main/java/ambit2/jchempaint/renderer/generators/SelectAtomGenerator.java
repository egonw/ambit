/* $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 2008  Gilleain Torrance <gilleain.torrance@gmail.com>
 *
 *  Contact: cdk-devel@list.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package ambit2.jchempaint.renderer.generators;

import java.awt.Color;
import java.util.List;

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.OvalElement;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.generators.IGeneratorParameter;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;

import ambit2.jchempaint.renderer.selection.IncrementalSelection;

/**
 * @cdk.module rendercontrol
 */
public class SelectAtomGenerator implements IGenerator<IAtomContainer> {
	protected Color selectionColor = Color.cyan; //ok, this is a hack , because JCP and CDK again have some incompatibilities
	protected double selectionRadius = 10;
	protected double scale = 1;
	
    public double getScale() {
		return scale;
	}


	public void setScale(double scale) {
		this.scale = scale;
	}


	public double getSelectionRadius() {
		return selectionRadius;
	}


	public void setSelectionRadius(double selectionRadius) {
		this.selectionRadius = selectionRadius;
	}


	public Color getSelectionColor() {
		return selectionColor;
	}


	public void setSelectionColor(Color selectionColor) {
		this.selectionColor = selectionColor;
	}

	private boolean autoUpdateSelection = true;

    public SelectAtomGenerator() {}


    public IRenderingElement generate(IAtomContainer ac, RendererModel model) {
        IChemObjectSelection selection = model.getSelection();
        return generate(selection, selectionColor, model);
    }
    
    protected IRenderingElement generate(IChemObjectSelection selection, Color selectionColor, RendererModel model){
        ElementGroup selectionElements = new ElementGroup();

        if(selection==null)
            return selectionElements;
        if (this.autoUpdateSelection || selection.isFilled()) {
            double r = selectionRadius / scale;

            double d = 4 * r;
            IAtomContainer selectedAC = selection.getConnectedAtomContainer();
            if (selectedAC != null) {
                for (IAtom atom : selectedAC.atoms()) {
                    Point2d p = atom.getPoint2d();
                    IRenderingElement element;
                    element = new OvalElement(
                          p.x, p.y, d, false, selectionColor);
                    selectionElements.add(element);
                }
            }
        }

        if (selection instanceof IncrementalSelection) {
            IncrementalSelection sel = (IncrementalSelection) selection;
            if (!sel.isFinished())
                selectionElements.add(sel.generate(selectionColor));
        }
        return selectionElements;
    }

   @Override
	public List<IGeneratorParameter<?>> getParameters() {
		// TODO Auto-generated method stub
		return null;
	}
}
