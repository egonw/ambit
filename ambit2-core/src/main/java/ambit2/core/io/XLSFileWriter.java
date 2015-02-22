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

package ambit2.core.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.logging.Level;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.io.formats.IResourceFormat;

public class XLSFileWriter extends FileWithHeaderWriter {
	protected Workbook workbook;
	protected Sheet sheet;
	protected DataFormat dataformat;
	protected CellStyle style;
	protected OutputStream out;
	protected boolean writingStarted = false;

    public XLSFileWriter(OutputStream out,boolean hssf) throws Exception {
    	super();
		workbook  = hssf?new HSSFWorkbook():new XSSFWorkbook();
		sheet = workbook.createSheet();
		dataformat = workbook.createDataFormat();
		style = workbook.createCellStyle();
	    style.setDataFormat(dataformat.getFormat("0.00"));

		this.out = out;
	}


	protected void writeHeader() throws IOException {
		Row row     = sheet.createRow((short)0);
		for (int i=0;i<header.size();i++) {
			row.createCell((short)(i+1)).setCellValue(header.list.get(i).toString()); 
		}

		logger.fine("\tHeader written\t"+header);
	}
    public void writeMolecule(IAtomContainer molecule) {
        
        Object value;    	

        try {
        	//give it a chance to create a header just before the first write
        	if (!writingStarted) {
    	        if (header == null) setHeader(molecule.getProperties());
    	        writeHeader();
    	        writingStarted = true;
        	}
    		Row row     = sheet.createRow((short)(sheet.getLastRowNum()+1));
        	String s;
        	for (int i =0; i< header.size(); i++) {
        		value = molecule.getProperty(header.list.get(i));
        		if (i == smilesIndex) {
        			
        			if (value == null) //no SMILES available
        			try {
        				value = sg.createSMILES(molecule);
        			} catch (Exception x) {
        				logger.log(Level.WARNING,"Error while createSMILES\t",x);
        				value = "";
        			}
        		} 
        	
        		if (value != null) {
        			Cell cell = row.createCell((short)(i+1));
    				
        			if (value instanceof Number) {
        				cell.setCellStyle(style);
        				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        				cell.setCellValue(((Number)value).doubleValue());
        			} else {
        				try {
        					double d = Double.parseDouble(value.toString());
            				cell.setCellStyle(style);
            				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
            				cell.setCellValue(d);        					
        				} catch (Exception x) {
        					cell.setCellType(Cell.CELL_TYPE_STRING);
        					cell.setCellValue(value.toString());
        				}
        			}
  
        		}
        	}
        } catch(Exception x) {
            logger.log(Level.WARNING,"ERROR while writing Molecule: ", x);
        }
    }
	public void setWriter(Writer arg0) throws CDKException {
		throw new CDKException("Unsupported operation!");
	}

	public void setWriter(OutputStream arg0) throws CDKException {
		try {
			if (this.out != null) out.close();
		} catch (IOException x) {
			logger.log(Level.WARNING,"Error while writing molecule",x);
		}
		this.out = arg0;
	}

	public IResourceFormat getFormat() {
		return new XLSFileFormat();
	}

	public boolean accepts(Class classObject) {
		Class[] interfaces = classObject.getInterfaces();
		for (int i=0; i<interfaces.length; i++) {
			if (IChemFile.class.equals(interfaces[i])) return true;
			if (IAtomContainerSet.class.equals(interfaces[i])) return true;
			if (IAtomContainer.class.equals(interfaces[i])) return true;
		}
		return false;

	}

	public void close() throws IOException {
		workbook.write(out);
		out.close();
	}
	@Override
	public String toString() {
		return " Microsoft Excel Workbook (.xls)";
	}
}


