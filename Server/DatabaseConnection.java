import java.util.Scanner;
import java.io.PrintWriter;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

/* An abstract wrapper class for database connections. */
abstract class DatabaseConnection {
	public abstract boolean login(String username, String password);
	public abstract boolean register(String username, String password);
}

/* This class allows the server to be run without a real database set up. */
class FileDatabaseConnection extends DatabaseConnection {
	private static String accountsFile = "accounts.txt";

	private Hashtable<String, String> database;
	private String databaseFile;

	public FileDatabaseConnection() {
		databaseFile = accountsFile;
		database = new Hashtable<String, String>();
		try {
			Scanner inputStream = new Scanner(new File(databaseFile));
			while (inputStream.hasNext()) {
				String uname = inputStream.next();
				String pword = inputStream.next();
				database.put(uname, pword);
			}
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

	public boolean login(String username, String password) {
		String temp = database.get(username);
		return (temp != null && temp.equals(password));
	}

	public boolean register(String username, String password) {
		if (database.get(username) != null) return false;
		try {
			PrintWriter outputStream = new PrintWriter(new File(databaseFile));
			outputStream.println(username + " " + password);
			outputStream.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return false;
		}
		database.put(username, password);
		return true;
	}
}
