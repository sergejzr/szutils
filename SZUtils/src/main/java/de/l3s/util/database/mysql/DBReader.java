package de.l3s.util.database.mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBReader {

	private Statement st;
	private ResultSet rs;
	

	public DBReader(Connection con,String tablename) throws SQLException {

		this.st=con.createStatement();
		this.rs=st.executeQuery("SELECT * FROM "+tablename);
	}
	public ResultSet getResultSet(){return rs;}
public void close()
{
	try {
		rs.close();
		st.close();	
	} catch (SQLException e) {
		e.printStackTrace();
	}

}
}
