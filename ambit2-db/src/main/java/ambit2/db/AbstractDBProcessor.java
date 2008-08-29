/*
Copyright (C) 2005-2008  

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

package ambit2.db;


import java.sql.Connection;
import java.sql.SQLException;

import ambit2.core.log.AmbitLogger;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.processors.IRepositoryAccess;

public abstract class AbstractDBProcessor<Target,Result> implements IDBProcessor<Target,Result>, IRepositoryAccess {
    protected static final AmbitLogger logger = new AmbitLogger(AbstractDBProcessor.class);
	protected SessionID sessionID = null;
	protected Connection connection; 
	protected boolean enabled = true;
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) throws DbAmbitException {
		if (this.connection != connection) try {
			close();
		} catch (SQLException x) {
		    logger.error(x);      
        }
		this.connection = connection;
	}

	public void close() throws SQLException {
    		if ((connection != null) && (!connection.isClosed()))
    			connection.close();
    		connection = null;
	}
	
	public SessionID getSession() {
		return sessionID;
	}
	public void setSession(SessionID sessionID) {
		this.sessionID = sessionID;
	}
}


