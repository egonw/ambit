package ambit2.db.test;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.xml.FlatDtdDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

public class ExtractDataForTest {
    public static void main(String[] args) throws Exception {
        // database connection
        Class driverClass = Class.forName("com.mysql.jdbc.Driver");
        Connection jdbcConnection = DriverManager.getConnection(
                "jdbc:mysql://localhost:33060/ambit2", "guest", "guest");
        IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);
        String ids = "(7,10,11, 29141)";
        // partial database export
        QueryDataSet partialDataSet = new QueryDataSet(connection);
        partialDataSet.addTable("users", "SELECT * FROM users WHERE user_name=\"guest\"");
        partialDataSet.addTable("roles", "SELECT * FROM roles");              
        partialDataSet.addTable("user_roles", "SELECT * FROM user_roles WHERE user_name=\"guest\"");
        
        partialDataSet.addTable("catalog_references", "SELECT * FROM catalog_references");
        
        partialDataSet.addTable("dictionary", "SELECT * FROM dictionary");
        partialDataSet.addTable("descriptors", "SELECT * FROM descriptors");           
        partialDataSet.addTable("dvalues", "SELECT * FROM dvalues");        
        partialDataSet.addTable("chemicals", "SELECT * FROM chemicals WHERE idchemical in "+ids);
        partialDataSet.addTable("structure", "SELECT * FROM structure join chemicals using(idchemical) WHERE idchemical in "+ids);
        
        partialDataSet.addTable("src_dataset", "SELECT * FROM src_dataset");        
        partialDataSet.addTable("struc_dataset", "SELECT * FROM struc_dataset");        
        //BIGINT serializes to long and gives errors for 
        partialDataSet.addTable("fp1024", "SELECT * FROM fp1024 join chemicals using(idchemical) WHERE idchemical in "+ids);
        partialDataSet.addTable("field_names", "SELECT * FROM field_names");            
        partialDataSet.addTable("structure_fields", "SELECT idstructure,idfieldname,value FROM structure_fields join structure using(idstructure) join chemicals using(idchemical) WHERE idchemical in "+ids);        
        FlatDtdDataSet.write(partialDataSet, new FileOutputStream("src/test/resources/ambit2/db/processors/test/partial-dataset.dtd"));
        FlatXmlDataSet.write(partialDataSet, 
        		new FileOutputStream("src/test/resources/ambit2/db/processors/test/src-datasets.xml"));
        
        /*
        // full database export
        IDataSet fullDataSet = connection.createDataSet();
        FlatXmlDataSet.write(fullDataSet, new FileOutputStream("full-dataset.xml"));
        */
    }
}
