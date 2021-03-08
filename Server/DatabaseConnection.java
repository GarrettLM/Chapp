import java.util.Scanner;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicInteger;

/* An abstract wrapper class for database connections. */
abstract class DatabaseConnection {
	public abstract Integer login(String username, String password);
	public abstract boolean register(String username, String password);
	public abstract String getRoomList();

	public static DatabaseConnection makeConnection() {
		return new FileDatabaseConnection();
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
