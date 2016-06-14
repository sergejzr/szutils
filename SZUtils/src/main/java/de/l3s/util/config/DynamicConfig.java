package de.l3s.util.config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.l3s.util.file.WebUtils;



public class DynamicConfig {
	private static final Logger logger = LogManager.getLogger(DynamicConfig.class);
	
	private static String conftable = "conf";
	static Hashtable<String, String> config = new Hashtable<String, String>();
	protected static Connection con;
	protected static DBConnectionProducer connectionProducer;
	static {
		Thread t = new Thread() {

			@Override
			public void run() {
				while (true) {
					if(connectionProducer!=null){
					try {
						refreshConnection();
						StringBuffer sb = new StringBuffer();

						super.run();
						for (String key : config.keySet()) {
							if (sb.length() > 0)
								sb.append(" AND ");
							sb.append(key + "!=" + config.get(key));
						}

						Statement st = con.createStatement();
						ResultSet rs = st.executeQuery("SHOW TABLES LIKE '"
								+ conftable + "'");

						if (rs.next()) {
							rs.close();
							rs = st.executeQuery("SELECT ukey,uvalue FROM "
									+ conftable
									+ (sb.length() > 0 ? " WHERE "
											+ sb.toString() : ""));
							if (rs.next()) {
								String key = rs.getString("ukey");
								String value = rs.getString("uvalue");
								config.put(key, value);
							}
							rs.close();
						}

					} catch (SQLException e) {
						e.printStackTrace();
					}}
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
			
			protected  Connection refreshConnection() {
				int tries = 0;

				do {
					if(connectionProducer!=null){
					try {
						if (con != null && !con.isClosed()) {
							return con;
						}
						
						con=connectionProducer.getConnection();
						//con = DB.getThreadConnection();
						log().info("Connection restored " + conftable);
						return con;

					} catch (Exception e) {
						e.printStackTrace();
						log().info("Our connection was too old " + conftable);
					}
					if (tries++ > 2) {
						log().info(
								"Connection heavily damaged, will wait 30 seconds and try again"
										+ conftable);
						
						
						try {
							Thread.sleep(30000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					}else{
						
						try {
							Thread.sleep(30000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				} while (true);

			}
			public Logger log() {
				return logger;
			}
		};
		t.start();
	}

	public static String getValue(String key) {
		String ret = config.get(key);
		return ret;
	}

	public static boolean getBoolean(String key) {
		String val = getValue(key);
		if (val == null)
			return false;
		if (Integer.parseInt(val) == 0)
			return false;
		return true;
	}

	public static void setConfigTable(String tablename) {
		conftable = tablename;

	}
	public static void setConnectionProducer(DBConnectionProducer producer)
	{
		connectionProducer=producer;
	}


	
	
}
