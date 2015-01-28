/**
 * Created on 2005-1-25
 *
 */
package ambit2.io;

import java.util.List;

import ambit2.data.descriptors.Descriptor;
import ambit2.data.descriptors.DescriptorsList;

/**
 * @deprecated
 * This is an interface to be used in data reading and writing
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 * See also {@link ambit2.io.MyIOUtilities} and {@link ambit2.domain.AllData}
 */

public interface IReadData {
	public void initialize(DescriptorsList dlist);	
	public int addRow(DescriptorsList dlist,List values);
	public int addRow();	
	public void setData(Descriptor d, String value);	
	public int getNPoints();
	public int getNDescriptors();
	public DescriptorsList getXDescriptors();
	public double getX(int row, int col);
	public double[] getX(int row);
	public void setMatch(DescriptorsList dlist);	
}
