/**
 * 
 */
package ambit.ui.data.molecule;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.vecmath.Vector2d;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.event.ICDKChangeListener;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.ISetOfAtomContainers;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.Renderer2D;
import org.openscience.cdk.renderer.Renderer2DModel;

import ambit.data.molecule.ListOfAtomContainers;
import ambit.ui.actions.AbstractMoleculeAction;
import ambit.ui.data.CompoundImageTools;


/**
 * 2D structure diagram
 * @author Nina Jeliazkova
 *
 */
public class Panel2D extends JPanel implements ICDKChangeListener, ComponentListener
{
	
	/**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = -5065125205427781493L;
    protected boolean explicitH = false;
    public ListOfAtomContainers molecules;
	public Renderer2DModel r2dm;
	public Renderer2D renderer;
	public String title = "Molecule Viewer";

    private Dimension preferredSize;
    private StructureDiagramGenerator sdg;
    protected AbstractMoleculeAction editAction = null;
    protected JLabel editLabel = null;
    protected int[] highlightedAtoms = null;
    protected int[] highlightedBonds = null;
    
    
    
	public Panel2D(IAtomContainer atomContainer, Renderer2DModel r2dm, Dimension size) {

		this(atomContainer,r2dm,false,size);
    }
	/**
	 *  Constructs a MoleculeViewer with a molecule to display and a Renderer2DModel containing the information on how to display it.
	 *
	 * @param  r2dm           The rendere settings determining how the molecule is displayed
	 */
    public Panel2D(IAtomContainer atomContainer, Renderer2DModel r2dm, boolean generateCoordinates,Dimension size) {	
		setLayout(new BorderLayout());
        preferredSize = size;
		editLabel = new JLabel("<html><u>Edit</u></html>");
		editLabel.setVisible(editAction != null);
		editLabel.addMouseListener(new MouseAdapter() {
	   		public void mouseClicked(MouseEvent e) {
	   			editAction.actionPerformed(new ActionEvent(this,0,""));
	   		}
	    });	  		
		
		
		add(editLabel,BorderLayout.SOUTH);
		
		sdg = new StructureDiagramGenerator();
		molecules = new ListOfAtomContainers();
		if (atomContainer != null) {
			setAtomContainers(ConnectivityChecker.partitionIntoMolecules(atomContainer));
		} ;
        
		this.r2dm = r2dm;
		r2dm.setShowAromaticity(true);
        r2dm.setBackgroundDimension(preferredSize);
		r2dm.addCDKChangeListener(this);
		renderer = new Renderer2D(r2dm);
		
		addComponentListener(this);

	}


	/**
	 *  Constructs a MoleculeViewer with a molecule to display
	 */
	public Panel2D(IAtomContainer atomContainer)
	{
		this(atomContainer, new Renderer2DModel(),new Dimension(200,200));
		
	}


	/**
	 *  Constructs a MoleculeViewer with a molecule to display
	 */
	public Panel2D()
	{
		this(null, new Renderer2DModel(), new Dimension(200,200));

	}
	
    public Panel2D(Dimension size) {
        this(null,new Renderer2DModel(),size);
    }
	/**
	 *  Sets a Renderer2DModel which determins the way a molecule is displayed
	 *
	 * @param  r2dm  The Renderer2DModel
	 */
	public void setRenderer2DModel(Renderer2DModel r2dm)
	{
		if (this.r2dm != null) r2dm.removeCDKChangeListener(this);
		this.r2dm = r2dm;
		r2dm.addCDKChangeListener(this);
		renderer = new Renderer2D(r2dm);
	}


	/**
	 *  Sets the AtomContainer to be displayed
	 *
	 * @param  atomContainer  The AtomContainer to be displayed
	 */
	
	protected void setAtomContainers(ISetOfAtomContainers atomContainers)
	{
		molecules.clear();
		if (atomContainers != null)
			for (int i=0; i < atomContainers.getAtomContainerCount();i++) 
				molecules.add(atomContainers.getAtomContainer(i));
		repaint();
	}

	public void setAtomContainer(IAtomContainer molecule,boolean generateCoordinates)
	{
  
		if (molecule != null) {
			if ((molecule ==null) || (molecule.getAtomCount() == 0)) 
				generateCoordinates=false;
			//else if (GeometryTools.has2DCoordinates(molecule))   
			  //  generateCoordinates=false;
			//System.out.println("panel 2D\t"+Boolean.toString(generateCoordinates));
			
			ISetOfAtomContainers c =  ConnectivityChecker.partitionIntoMolecules(molecule);		
			try
			{
				molecules.clear();
				IMolecule m = null;
				for (int i=0; i < c.getAtomContainerCount();i++) { 
					if (generateCoordinates) {
						sdg.setMolecule((Molecule)c.getAtomContainer(i));
						m = null;
						sdg.generateCoordinates(new Vector2d(0,1));
						m = sdg.getMolecule();
					} else m = (Molecule)c.getAtomContainer(i);
					molecules.add(m);
				}
				repaint();
			}
			catch(Exception exc)
			{
				setAtomContainers(null);
				molecules.clear();
			}
		} else setAtomContainers(null);
  }

	/**
	 *  Gets the Renderer2DModel which determins the way a molecule is displayed
	 *
	 * @return    The Renderer2DModel value
	 */
	public Renderer2DModel getRenderer2DModel()
	{
		return renderer.getRenderer2DModel();
	}


	/**
	 *  Paints the molecule onto the JPanel
	 *
	 * @param  g  The graphics used to paint with.
	 */
	public void paint(Graphics g)
	{
		super.paint(g);
		setBackground(Color.white);
        CompoundImageTools.paint(renderer, molecules, explicitH, g,highlightedAtoms,highlightedBonds);
	}



	/**
	 *  Method to notify this CDKChangeListener if something has changed in another object
	 *
	 * @param  e  The EventObject containing information on the nature and source of the event
	 */
	public void stateChanged(EventObject e)
	{
		repaint();
	}


	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
	 */
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
	 */
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
	 */
	public void componentResized(ComponentEvent e) {
		preferredSize = getSize();
		r2dm.setBackgroundDimension(preferredSize);
	}


	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
	 */
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}


	public AbstractMoleculeAction getEditAction() {
		return editAction;
	}


	public void setEditAction(AbstractMoleculeAction editAction) {
		this.editAction = editAction;
		editLabel.setVisible(editAction != null);
	}
	public int[] getHighlightedAtoms() {
		return highlightedAtoms;
	}
	public void setHighlightedAtoms(int[] highlightedAtoms) {
		this.highlightedAtoms = highlightedAtoms;
		repaint();
	}
	public int[] getHighlightedBonds() {
		return highlightedBonds;
	}
	public void setHighlightedBonds(int[] highlightedBonds) {
		this.highlightedBonds = highlightedBonds;
		repaint();
	}

}

