/**
 * Created on 2005-3-23
 *
 */
package ambit2.qmrf.interfaces;

import java.io.Serializable;

import ambit2.qmrf.AmbitObject;

/**
 * A common interface for {@link AmbitObject} <br>
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public interface AmbitInterface extends Cloneable, Comparable, Serializable {
	public int getId();
	public void setId(int id);
	public String getName();
	public void setName(String name);
	public boolean hasID();
}
