import java.util.Scanner;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicInteger;

import java.sql.*;

/* An abstract wrapper class for database connections. */
abstract class DatabaseConnection {
	public abstract Integer login(String username, String password);
	public abstract boolean register(String username, String password);
	public abstract String getRoomList(Integer userid);
	public abstract String getFriendList(Integer userid);

	public static DatabaseConnection makeConnection() {
		String jdbcDriver = Configuration.getConfiguration("JDBC-driver");
		String url = Configuration.getConfiguration("Database-url");
		String username = Configuration.getConfiguration("Database-username");
		String password = Configuration.getConfiguration("Database-password");
		return new JDBCDatabaseConnection(jdbcDriver, url, username, password);
	}
}

/* This class allows the server to be run without a real database set up. */
class FileDatabaseConnection extends DatabaseConnection {
	private static String accountsFile = "accounts.txt";
	private static AtomicInteger nextUID;

	private Hashtable<String, String> database;
	private String databaseFile;

	public FileDatabaseConnection() {
		databaseFile = accountsFile;
		database = new Hashtable<String, String>();
		try {
			Scanner inputStream = new Scanner(new File(databaseFile));
			int uid = 1;
			while (inputStream.hasNext()) {
				String uname = inputStream.next();
				String pword = inputStream.next();
				database.put(uname, pword + ";" + uid);
				uid++;
			}
			nextUID = new AtomicInteger(uid);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public FileDatabaseConnection(String databaseFile) {
		this.databaseFile = databaseFile;
		database = new Hashtable<String, String>();
		try {
			Scanner inputStream = new Scanner(new File(databaseFile));
			while (inputStream.hasNext()) {
				String uname = inputStream.next();
				String pword = inputStream.next();
				database.put(uname, pword);
			}
			inputStream.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public Integer login(String username, String password) {
		String temp = database.get(username);
		if (temp == null) return null;
		String[] temp2 = temp.split(";");
		if (!temp2[0].equals(password)) return null;
		return new Integer(temp2[1]);
	}

	public boolean register(String username, String password) {
		if (database.get(username) != null) return false;
		try {
			PrintWriter outputStream = new PrintWriter(new FileOutputStream(databaseFile, true));
			outputStream.println(username + " " + password);
			outputStream.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return false;
		}
		database.put(username, password + ";" + nextUID.incrementAndGet());
		return true;
	}

	public String getRoomList(Integer userid) { return "1;default;2;coolroom"; }
	public String getFriendList(Integer userid) { return ";"; };
}

class JDBCDatabaseConnection extends DatabaseConnection {
	private Connection database;

	public JDBCDatabaseConnection(String jdbcDriver, String databaseURL, String username, String password) {
		try {
			System.out.println("Getting driver");
			//Class.forName(jdbcDriver);
			System.out.println("Setting up database connection");
			database = DriverManager.getConnection(databaseURL, username, password);
			System.out.println("Connected to database");
		} catch (Exception e) {
			System.out.println(e.toString());
			System.exit(1);
		}
	}

	public Integer login(String username, String password) {
		try {
			String sql = "SELECT * FROM users WHERE username='" + username + "';";
			Statement stmt = database.createStatement();
			ResultSet results = stmt.executeQuery(sql);
			if (!results.next() || !password.equals(results.getString("password"))) return null;
			return new Integer(results.getInt("userid"));
		} catch (Exception e) {
			System.out.println(e.toString());
			System.exit(1);
		}
		return null;
	}

	public boolean register(String username, String password) {
		try {
			String sql = "SELECT * FROM users WHERE username='" + username + "';";
			Statement stmt = database.createStatement();
			ResultSet results = stmt.executeQuery(sql);
			if (results.next()) return false;
			sql = "INSERT INTO users(username, password) VALUES('" + username + "', '" + password + "');";
			stmt.executeUpdate(sql);
			return true;
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return false;
	}

	public String getRoomList(Integer userid) {
		try {
			String sql = "SELECT rooms.roomid, rooms.roomname FROM roommembers, rooms WHERE roommembers.userid='"
				+ userid.toString() + "' AND roommembers.roomid=rooms.roomid;";
			Statement stmt = database.createStatement();
			ResultSet results = stmt.executeQuery(sql);
			String roomlist = "";
			while (results.next()) {
				roomlist = roomlist + results.getInt("roomid") + ";" + results.getString("roomname") + ";";
				System.out.println(roomlist);
			}
			if (roomlist.equals("")) return ";";
			return roomlist;
		} catch (Exception e) {
			System.out.println(e.toString());
		}	
		return "1;default;";
	}

	public String getFriendList(Integer userid) {
		try {
			String sql = "SELECT users.userid, users.username FROM friends, users WHERE friends.firstuserid='"
				+ userid.toString() + "' AND friends.seconduserid=users.userid;";
			Statement stmt = database.createStatement();
			ResultSet results = stmt.executeQuery(sql);
			String friendlist = "";
			while (results.next()) {
				friendlist = friendlist + results.getInt("userid") + ";" + results.getString("username") + ";";
				//System.out.println(roomlist);
			}
			sql = "SELECT users.userid, users.username FROM friends, users WHERE friends.seconduserid='"
				+ userid.toString() + "' AND friends.firstuserid=users.userid;";
			results = stmt.executeQuery(sql);
			while (results.next()) {
				friendlist = friendlist + results.getInt("userid") + ";" + results.getString("username") + ";";
				//System.out.println(roomlist);
			}
			if (friendlist.equals("")) return ";";
			return friendlist;
		} catch (Exception e) {
			System.out.println(e.toString());
		}	
		return ";";
	}
}
