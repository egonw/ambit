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

package ambit2.db.processors;

import java.sql.Connection;
import java.sql.SQLException;

import ambit2.base.processors.ProcessorsChain;
import ambit2.db.IDBProcessor;
import ambit2.db.SessionID;
import ambit2.db.exceptions.DbAmbitException;

public class DBProcessorsChain<Target,Result,P extends IDBProcessor> extends ProcessorsChain<Target,Result,P> implements IDBProcessor<Target,Result> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2269380236406696708L;
	protected Connection connection;
	protected SessionID sessionID = null;
	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) throws DbAmbitException {
		this.connection = connection;
		for (int i=0; i < size();i++)
			get(i).setConnection(connection);

	}
	@Override
	public void add(int index, P element) {
		try {
			element.setConnection(getConnection());
			element.setSession(getSession());
			} catch (DbAmbitException x) {
				x.printStackTrace();
			}
		super.add(index, element);
	}
	@Override
	public boolean add(P o) {
		try {
		o.setConnection(getConnection());
		o.setSession(getSession());
		} catch (DbAmbitException x) {
			x.printStackTrace();
		}
		return super.add(o);
	}
	public SessionID getSession() {
		return sessionID;
	}

	public void open() throws DbAmbitException {
		for (int i=0; i < size();i++)
			get(i).open();

	}
	@Override
	public void close() {
		for (int i=0; i < size();i++)
			try {
			get(i).close();
			} catch (SQLException x) {
				x.printStackTrace();
			}
		super.close();
	}
	public void setSession(SessionID session) {
		sessionID = session;
		for (int i=0; i < size();i++)
			get(i).setSession(session);
	}

}
