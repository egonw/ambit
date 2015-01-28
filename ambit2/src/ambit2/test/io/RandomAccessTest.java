/* RandomAccessTest.java
 * Author: Nina Jeliazkova
 * Date: Jul 12, 2006 
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

package ambit2.test.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;

import ambit2.io.RandomAccessReader;
import ambit2.io.RandomAccessSDFReader;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Jul 12, 2006
 */
public class RandomAccessTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(RandomAccessTest.class);
    }
    

    public void test() {
        File f = new File("data/PG_LLNA.sdf");
        System.out.println(System.getProperty("user.dir"));
        System.out.println(System.getProperty("user.name"));
        //File f = new File("src/ambit/data/molecule/ringTemplateStructures.sdf");
        FileInputStream in;
        RandomAccessReader rf;
        
        try {
            
            rf = new RandomAccessSDFReader(f,DefaultChemObjectBuilder.getInstance());
            //rf.setChemObjectReader(new MDLReader());
            for (int i=rf.getNumberOfRecords()-1; i >=0;i--) {
                try {
                    System.out.print(i);
                    System.out.print(">>");
                    if (rf.getChemObjectReader() == null) {
                        //System.out.println(rf.readRecord(i));
                        System.out.println();
                    } else {
	                    IMolecule m = (IMolecule)rf.readRecord(i);
	                    System.out.println(m.getAtomCount());
	                    assertTrue(m.getAtomCount()>0);
                    }
                } catch (Exception x) {
                    x.printStackTrace();
                }
                
            }
        } catch (IOException x) {
            x.printStackTrace();
            fail();
        } finally {
                      
        }
    }
}
