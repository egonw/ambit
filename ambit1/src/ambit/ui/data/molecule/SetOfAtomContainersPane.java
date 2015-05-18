/* SetOfAtomContainersPanel.java
 * Author: Nina Jeliazkova
 * Date: Aug 10, 2006 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Nina Jeliazkova
 * 
 * Contact: nina@acad.bg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit.ui.data.molecule;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.openscience.cdk.interfaces.ISetOfAtomContainers;

import ambit.ui.AmbitColors;
import ambit.ui.data.RandomAccessFileTableModel;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 10, 2006
 */
public class SetOfAtomContainersPane extends JScrollPane implements PropertyChangeListener {
    protected JTable table = null;
    protected TableModel model = null;
    
    /**
     * 
     */
    public SetOfAtomContainersPane(ISetOfAtomContainers atomContainers, ArrayList properties) {
        this(new SetOfAtomContainersTableModel(atomContainers,properties));
        
    }
    public SetOfAtomContainersPane(TableModel model) {
    	super();
    	this.model = model;
    	//setViewportView(addWidgets(new SortedTableModel(model)));
    	table = addWidgets(model);
    	setViewportView(table);
        setPreferredSize(new Dimension(400,300));
    }
    protected static TableColumnModel createColumnModel(TableModel model) {
        TableColumnModel cModel = new DefaultTableColumnModel();
        final int columnSize[] = {32,64,64,48,48};
        for (int i=0; i < model.getColumnCount(); i++) {
	        TableColumn column = new TableColumn(i);
	        column.setHeaderValue(model.getColumnName(i));
	        if (i<5)
	            column.setPreferredWidth(columnSize[i]);
	        cModel.addColumn(column);
        }
        return cModel;
    }
    protected JTable addWidgets(TableModel model) {

        JTable table = new JTable(model) {
        	public void createDefaultColumnsFromModel() {
                TableModel m = getModel();
                if (m != null) {
                    // Remove any current columns
                    TableColumnModel cm = getColumnModel();
                    while (cm.getColumnCount() > 0) {
                        cm.removeColumn(cm.getColumn(0));
        	    }
                    // Create new columns from the data model info
                    final int columnSize[] = {32,64,100,48,48};
                    for (int i = 0; i < m.getColumnCount(); i++) {
                        TableColumn newColumn = new TableColumn(i);
                        if (i<4) {
            	            newColumn.setPreferredWidth(columnSize[i]);
            	            newColumn.setCellRenderer(new ColorTableCellRenderer());
                        } else
                        	newColumn.setCellRenderer(new NumbersRenderer());
                        addColumn(newColumn);
                    }
                }
        	};
        };
        //table.setDefaultRenderer(Double.class, new ColorTableCellRenderer());
        

        //model.addMouseListenerToHeaderInTable(table);
        
        //table.setTableHeader(null);
        
        /*
        table.setRowHeight(cellSize.height + 24);
        */
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0,0));
        table.setSelectionBackground(AmbitColors.DarkClr);

        /*
        table.setDefaultRenderer(Compound.class, new MoleculeGridCellRenderer(cellSize));
        table.setDefaultRenderer(AmbitPoint.class, new MoleculeGridCellRenderer(cellSize));
        table.setDefaultRenderer(Image.class, new ImageCellRenderer());
        
        table.setDefaultRenderer(SourceDataset.class, new SourceDatasetEditor());
        table.setDefaultRenderer(LiteratureEntry.class, new LiteratureEntryEditor());
        table.setDefaultRenderer(JournalEntry.class, new JournalEntryEditor());
        //table.setDefaultRenderer(AuthorEntries.class, new AuthorEntriesEditor());
        
        table.setDefaultEditor(AmbitObject.class,new AmbitCellEditor());
        
        table.setPreferredScrollableViewportSize(new Dimension(cellSize.width*3, (cellSize.height+30)*2));
        */

        return table;
    }
    public void addListSelectionListener(ListSelectionListener listener) {
        table.getSelectionModel().addListSelectionListener(listener);
    }
    public int getIndex(int sortedIndex) {
    	return ((Integer)table.getValueAt(sortedIndex,0)).intValue()-1;
    }
    public void propertyChange(PropertyChangeEvent e) {
    	if (model instanceof SetOfAtomContainersTableModel) {
    		((SetOfAtomContainersTableModel)model).propertyChange(e);
    		//table.setDefaultRenderer(Double.class, new ColorTableCellRenderer());
    	}	
    }
}

class ColorTableCellRenderer extends DefaultTableCellRenderer {
	protected Color oddColor = new Color(240,240,240);

	public ColorTableCellRenderer() {
	      super();
	      setHorizontalAlignment(javax.swing.SwingConstants.LEFT);      
	    }
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    	Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
    			row, column);
    	if (value != null)
    		setToolTipText(value.toString());
    	else
    		setToolTipText("");
    	
    	if (table.getModel() instanceof RandomAccessFileTableModel) {
	    	if (((RandomAccessFileTableModel) table.getModel()).getReader().getSelectedIndex()
	    			== row) {
	    		c.setBackground(table.getSelectionBackground());
	    		return c;
	    	}
    	} else if (isSelected) {
    		c.setBackground(table.getSelectionBackground());
    		return c;
    	}
    	if ((row % 2) ==0) {
	    		c.setBackground(Color.white);
    	} else c.setBackground(oddColor);

    	return c;
    }
}

class NumbersRenderer extends ColorTableCellRenderer {
	protected NumberFormat nf = NumberFormat.getInstance();
	
    public NumbersRenderer() {
      super();
      setHorizontalAlignment(javax.swing.SwingConstants.LEFT);      
      nf.setMaximumFractionDigits(4);
    }
    

    public void setValue(Object value) {
      if ((value != null) && (value instanceof Number)) {
    	  setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        Number numberValue = (Number) value;
        value = nf.format(numberValue.doubleValue());
      } 
      super.setValue(value);
    } 

  } 
