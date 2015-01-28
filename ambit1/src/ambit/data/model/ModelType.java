/**
 * Created on 2005-3-21
 *
 */
package ambit.data.model;

import ambit.data.AmbitObject;
import ambit.data.IAmbitEditor;
import ambit.ui.data.model.ModelTypeEditor;



/**
 * The type of a QSAR Model 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class ModelType extends AmbitObject {
	public static String[] modelNames =
		{"Linear regression","Non-linear regression","PLS","ANN"};	
	/**
	 * 
	 */
	public ModelType() {
		super();
	}
	public ModelType(String modeltype) {
		super(modeltype);
	}
	public ModelType(String modeltype, int idmodeltype) {
		super(modeltype,idmodeltype);
	}
	public boolean isPredefined() {
		return false;
	}
	public String[] predefinedvalues() {
		return modelNames;
	}
	public IAmbitEditor editor() {
		return new ModelTypeEditor(this);
	}
	
	
}
