package me.taylorkelly.bigbrother.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * <b>Purpose:</b>Realizes a connection pool for all JDBC connections.<br>
 * <b>Description:</b>http://java.sun.com/developer/onlineTraining/Programming/
 * JDCBook/ conpool.html<br>
 * <b>Copyright:</b>Licensed under the Apache License, Version 2.0.
 * http://www.apache.org/licenses/LICENSE-2.0<br>
 * <b>Company:</b>SIMPL<br>
 *
 * @author schneimi
 * @version $Id$<br>
 * @link http://code.google.com/p/simpl09/
 */
public final class ConnectionService {
	private List<JDCConnection> connections;
	private String url, user, password;
	private static final long TIMEOUT = 60000;
	private ConnectionReaper reaper;
	private static final int POOLSIZE = 10;

	public ConnectionService(String url, String user, String password) {
		this.url = url;
		this.user = user;
		this.password = password;
		connections = new ArrayList<JDCConnection>(POOLSIZE);
		reaper = new ConnectionReaper(this);
		reaper.start();
	}

	public synchronized void reapConnections() {
		long stale = System.currentTimeMillis() - TIMEOUT;
		if (connections != null && !connections.isEmpty()) {
			for (JDCConnection conn : connections) {
				if ((conn.inUse()) && (stale > conn.getLastUse())
						&& (!conn.validate())) {
					removeConnection(conn);
				}
			}
		}
	}

	public synchronized void closeConnections() {
		if (connections != null && !connections.isEmpty()) {
			for (JDCConnection conn : connections) {
				removeConnection(conn);
			}
		}
	}

	private synchronized void removeConnection(JDCConnection conn) {
		connections.remove(conn);
	}

	public synchronized Connection getConnection() throws SQLException {
		JDCConnection c;

		for (int i = 0; i < connections.size(); i++) {
			c = connections.get(i);
			if (c.lease()) {
				return c;
			}
		}

		Connection conn = DriverManager.getConnection(url, user, password);
		c = new JDCConnection(conn, this);
		c.lease();
		connections.add(c);

		return c.getConnection();
	}

	public synchronized void returnConnection(JDCConnection conn) {
		conn.expireLease();
	}
}

class ConnectionReaper extends Thread {
	private ConnectionService pool;
	private static final long DELAY = 300000;

	ConnectionReaper(ConnectionService pool) {
		this.pool = pool;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(DELAY);
			} catch (InterruptedException e) {
			}
			pool.reapConnections();
		}
	}
}
