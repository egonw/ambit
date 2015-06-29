/**
 * Created on 2005-3-18
 *
 */
package ambit.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ambit.database.exception.DbAmbitException;


/**
 * Database API<br>
 * A parent class for the specific API classes 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class DbCore {
	protected static final String AMBIT_smtdelimiter = ";";
	protected DbConnection dbconn = null;
	protected Connection conn = null;
	protected Statement stmt = null;
	protected PreparedStatement ps = null;
	/**
	 * 
	 */
	public DbCore(DbConnection conn) {
		super();
		this.dbconn = conn;
		if (conn != null)
			this.conn = conn.getConn();
	}

	public void initialize() throws DbAmbitException {
		if (conn == null) throw new DbAmbitException(null,"[DATABASE] Not connected!");
		try {

			stmt = conn.createStatement();
		}  catch (SQLException x) {
			throw new DbAmbitException(null,"initialize",x);
		}
	}
	protected void initializePrepared(String sql) throws DbAmbitException {
		try {
		ps = conn.prepareStatement(sql);
		}  catch (SQLException x) {
			throw new DbAmbitException(null,"initialize",x);
		}
	}

	public void close() throws SQLException {
		if (stmt != null) stmt.close();
		stmt = null;
		if (ps != null) ps.close(); ps = null;
	}

	
	public Connection getConn() {
		return conn;
	}
	public void setConn(Connection conn) {
		this.conn = conn;
	}
	public int getAutoGeneratedKey(Statement stmt) throws SQLException {
		int id = -1;
		ResultSet tmpRS = stmt.getGeneratedKeys();
		if (tmpRS.next()) id = tmpRS.getInt(1);
		tmpRS.close();
		return id;
	}
	public int getAutoGeneratedKey(PreparedStatement stmt) throws SQLException {
		int id = -1;
		ResultSet tmpRS = stmt.getGeneratedKeys();
		if (tmpRS.next()) id = tmpRS.getInt(1);
		tmpRS.close();
		return id;
	}
	
}