/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testtask;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.derby.jdbc.EmbeddedDriver;

/**
 * 
 * @author oleg
 */
public class Database {

	private static final String connectionURL = "jdbc:derby:ExternalURL;create=true";
	private Connection conn;

	public Database() throws SQLException, ClassNotFoundException {
		Class.forName(EmbeddedDriver.class.getName());
		conn = DriverManager.getConnection(connectionURL);
	}
	
	public void create() throws SQLException {

		String dropTable = "DROP TABLE links";

		String createTable = "CREATE TABLE links (URL VARCHAR(100) NOT NULL PRIMARY KEY, " + "NUMOFNEST CHAR(20), "
				+ "URLExtrnl CHAR(100) NOT NULL, " + "DOMENINFO VARCHAR(50))";

		Statement stmt = conn.createStatement();
		
		try {
    		stmt.executeUpdate(dropTable);
		} catch (SQLSyntaxErrorException e) {
		    System.out.println(e.getMessage());
		}
    		
		stmt.executeUpdate(createTable);
		
		stmt.close();
	}
	
	public void insert(String url, int nuOfNest, int urlExtrnl, String domenInfo) throws SQLException {
		PreparedStatement psInsert = conn.prepareStatement("INSERT INTO links VALUES (?,?,?,?)");

		psInsert.setString(1, url);
		psInsert.setInt(2, nuOfNest);
		psInsert.setInt(3, urlExtrnl);
		psInsert.setString(4, domenInfo);

		psInsert.executeUpdate();
		
		psInsert.close();
	}
	
	public List<Link> select() throws Exception {

		ArrayList<Link> links = new ArrayList<Link>();
		
		Statement stmt2 = conn.createStatement();
		ResultSet rs = stmt2.executeQuery("SELECT * FROM links");

		while (rs.next()) {
			Link link = new Link(new URL(rs.getString(1)), rs.getInt(2), rs.getInt(3), rs.getString(4));
			links.add(link);
		}
		
		rs.close();
		
		return links;
	}	
	
	public static void main(String[] args) throws Exception {
		Database database = new Database();
                Link lnk = new Link(new URL("http://www.google.com"), 0, 0, "");		
                database.create();
		database.insert(
                       lnk.getUrl().toString(), lnk.getNumOfNest(),
                        lnk.getUrlExtrnl(), lnk.getDescription().toString());
		List<Link> links = database.select();
		
		for (Link link : links) {
			System.out.println(link.toString());
		}
	}
}
