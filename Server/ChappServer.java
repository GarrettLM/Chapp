import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.IOException;

public class ChappServer {
	public static final int VERSION_NUMBER = 1;
	public static final int REVISION_NUMBER = 0;
	public static final int PORT_NUMBER = 22222;

	private static ServerSocket chatServer;
	private static AccountDatabase accountDB;

	public static void main(String[] args) {
		System.out.println("Chapp Server");
		System.out.println("Version number: " + VERSION_NUMBER);
		System.out.println("Revision number: " + REVISION_NUMBER);

		try {
			chatServer = new ServerSocket(PORT_NUMBER);
			accountDB = new AccountDatabase();
		} catch (IOException e) {
			System.out.println("Unable to connect to port!");
			System.exit(1);
		}

		run();
	}

	private static void run() {
		while (true) {
			Socket clientConnection;
			Scanner clientInput;
			PrintWriter clientOutput;

			//Waits for clients to connect
			try {
				login = chatServer.accept();
				clientInput = new Scanner(login.getInputStream());
				clientOutput = new PrintWriter(login.getOutputStream());

				String username = clientInput.next();
				System.out.println(username);
				String password = clientInput.next();
				System.out.println(password);

				while (!accountDB.login(username, password)) {
					System.out.println("Failed to log in!");
					clientOutput.println("failure");
					clientOutput.flush();
					username = clientInput.next();
					password = clientInput.next();
				}
				System.out.println("Log in success!");
				clientOutput.println("success");
				clientOutput.flush();
				login.close();
				clientInput.close();
				clientOutput.close();
			} catch (IOException e) {
				System.out.println("Error!");
			}
		}
	}
}

class AccountDatabase {
	private Hashtable<String, String> database; 

	public AccountDatabase() {
		database = new Hashtable<String, String>();
		database.put("John", "Password");
	}

	public boolean login(String username, String password) {
		String temp = database.get(username);
		return (temp != null && temp.equals(password));
	}
}
