package ambit2.db.chart;

import java.awt.image.BufferedImage;
import java.sql.SQLException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.PieDataset;
import org.jfree.data.jdbc.JDBCPieDataset;

import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.exceptions.DbAmbitException;

/**
 * Generates pie chart with number of chemicals per group
 * @author nina
 *
 */
public class PieChartGenerator extends ChartGenerator<SourceDataset> {
	protected Property property;
	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}
	/*
	protected static final String sql = 
		"SELECT value,count(distinct(query_results.idchemical)) as num_chemicals\n"+
		"FROM query_results join property_values using(idstructure) join property_string using(idvalue_string) join properties using(idproperty)\n"+
		"where name='%s' and idquery=%d\n"+
		"group by value\n";
		*/
	
	protected static final String sql = 
		"SELECT ifnull(value_num,value) as v,count(distinct(structure.idchemical)) as num_chemicals\n"+
		"FROM struc_dataset join structure using(idstructure) join property_values using(idstructure) " +
		"left join property_string using(idvalue_string) join properties using(idproperty)\n"+
		"where idproperty=%d and id_srcdataset=%d\n"+
		"group by v\n";	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4301713904733096802L;

	public BufferedImage process(SourceDataset target) throws AmbitException {
		  if (property == null) throw new AmbitException("Property not defined");
		  JFreeChart pieChart = null;

	      try
	      {
	         PieDataset pieDataset =  new JDBCPieDataset(getConnection(),
	        		 String.format(sql,property.getId(),target.getId())); 
	         

	         pieChart =
	            ChartFactory.createPieChart3D( property.getName() , // chart title
	                                         pieDataset,
	                                         true,      // legend displayed
	                                         true,      // tooltips displayed
	                                         false );   // no URLs
	         return pieChart.createBufferedImage(width,height);

	      }
	      catch (SQLException sqlEx)    // checked exception
	      {
	         System.err.println("Error trying to acquire JDBCPieDataset.");
	         System.err.println("Error Code: " + sqlEx.getErrorCode());
	         System.err.println("SQLSTATE:   " + sqlEx.getSQLState());
	         sqlEx.printStackTrace();
	         return null;
	      }

	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}

}
