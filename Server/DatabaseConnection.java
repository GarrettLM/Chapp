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
	public abstract String getRoomList();

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

	public String getRoomList() { return "1;default;2;coolroom"; }
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
			System.out.println(e.getMessage());
			System.exit(1);
		}
		return null;
	}

	public boolean register(String username, String password) {
		String sql = "INSERT INTO users(username, password) VALUES('" + username + "', '" + password + "');";
		return false;
	}

	public String getRoomList() { return "1;default"; }
}
