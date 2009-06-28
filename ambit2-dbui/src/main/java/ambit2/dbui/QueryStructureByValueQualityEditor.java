package ambit2.dbui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.ListModel;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.openscience.cdk.CDKConstants;

import ambit2.base.data.Property;
import ambit2.base.data.QLabel;
import ambit2.base.data.QLabel.QUALITY;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.results.AmbitRows;
import ambit2.db.results.RowsModel;
import ambit2.db.search.StringCondition;
import ambit2.db.search.property.RetrieveFieldNamesByAlias;
import ambit2.db.search.structure.QueryStructureByValueQuality;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;

public class QueryStructureByValueQualityEditor extends
		QueryEditor<String, QLabel, StringCondition,IStructureRecord, QueryStructureByValueQuality> {


	/**
	 * 
	 */
	private static final long serialVersionUID = 4778074204901236208L;

	
	
	@Override
	protected JComponent createConditionComponent() {
		JComboBox box = BasicComponentFactory.createComboBox(
                new SelectionInList<StringCondition>(
                		new StringCondition[] {
                			StringCondition.getInstance(StringCondition.C_EQ),
                			StringCondition.getInstance(StringCondition.C_LIKE),
                			StringCondition.getInstance(StringCondition.C_NOTLIKE),
                			StringCondition.getInstance(StringCondition.C_REGEXP),
                			StringCondition.getInstance(StringCondition.C_SOUNDSLIKE),
                			StringCondition.getInstance(StringCondition.C_STARTS_WITH)
                		},
                		presentationModel.getModel("condition")));
		AutoCompleteDecorator.decorate(box);
		return box;
	}

	@Override
	protected JComponent createValueComponent() {
		JComboBox box = BasicComponentFactory.createComboBox(
                new SelectionInList<QLabel>(
                		new QLabel[] {
                			new QLabel(QUALITY.OK),
                			new QLabel(QUALITY.ProbablyOK),
                			new QLabel(QUALITY.Unknown),
                			new QLabel(QUALITY.ProbablyERROR),
                			new QLabel(QUALITY.ERROR)
                		},
                		presentationModel.getModel("value")));
		AutoCompleteDecorator.decorate(box);
		return box;
	}
	@Override
	protected JComponent createFieldnameComponent() {
		
		JComboBox box = BasicComponentFactory.createComboBox(
                new SelectionInList<String>(
                		new String[] {
                			CDKConstants.CASRN
                		},
                		presentationModel.getModel("fieldname")));
		AutoCompleteDecorator.decorate(box);
		return box;		

	}

	public void open() throws DbAmbitException {

		
	}

}
