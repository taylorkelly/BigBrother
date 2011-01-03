public class BBData {
	public void initialize() {
		/*if(!tableExists()) {
		System.out.println("Table doesn't exist... creating");
		createTable();
	} else {
		System.out.println("Table exists!");

	}
	test();*/
	}
/*static String SQLdriver = "com.mysql.jdbc.Driver";
	static String SQLusername = "root";
	static String SQLpassword = "root";
	static String SQLdb = "jdbc:mysql://localhost:3306/minecraft";

	static String BB_TABLE = "CREATE TABLE `bigbrother` (`id` int(15) NOT NULL AUTO_INCREMENT, `date` datetime NOT NULL DEFAULT '0000-00-00 00:00:00', `player` varchar(30) NOT NULL DEFAULT 'Player', `action` int(15) NOT NULL DEFAULT '0', `x` int(10) NOT NULL DEFAULT '0', `y` int(10) NOT NULL DEFAULT '0', `z` int(10) NOT NULL DEFAULT '0', `data` varchar(50) NOT NULL DEFAULT '', PRIMARY KEY (`id`));";

	private boolean tableExists()
	{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DriverManager.getConnection(SQLdb,
					SQLusername, SQLpassword);
			DatabaseMetaData dbm = conn.getMetaData();
			rs = dbm.getTables(null, null, "bigbrother", null);
			if (!rs.next())
			{
				return false;
			}
			return true;
		} catch (SQLException ex) {
			log.log(Level.SEVERE, name + " SQL exception", ex);
			return false;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				log.log(Level.SEVERE, name + " SQL exception on close", ex);
			}
		}
	}
	
	public void createTable() {
		    try {
		      Connection conn = DriverManager.getConnection(SQLdb, SQLusername, 
		        SQLpassword);
		      conn.setAutoCommit(false);
		      try {
		        Statement st = conn.createStatement();

		        st.executeUpdate(BB_TABLE);

		        conn.close();
		      } catch (SQLException localSQLException) {
			      log.log(Level.SEVERE, "Could not create the table", localSQLException);
		      }
		    } catch (Exception e) {
		      log.log(Level.SEVERE, "Could not create the table", e);
		    }
		  }
	
	public void test() {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DriverManager.getConnection(SQLdb,
					SQLusername, SQLpassword);
			ps = conn
					.prepareStatement(
							"INSERT INTO bigbrother (date, player, action, x, y, z, data) VALUES (now(),?,?,?,?,?,?)",
							1);
			ps.setString(1, "tkelly");
			ps.setInt(2, 2);
			ps.setInt(3, 1);
			ps.setInt(4, 2);
			ps.setInt(5, 3);
			ps.setString(6, "you messed up");

			ps.executeUpdate();
		} catch (SQLException ex) {
			log.log(Level.SEVERE, "Unable to add protection into SQL", ex);
			try {
				if (ps != null) {
					ps.close();
				}
				if (rs != null) {
					rs.close();
				}
				if (conn != null)
					conn.close();
			} catch (SQLException er) {
				log.log(Level.SEVERE, "Could not close connection to SQL", er);
			}
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (rs != null) {
					rs.close();
				}
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				log.log(Level.SEVERE, "Could not close connection to SQL", ex);
			}
		}
	}*/
}