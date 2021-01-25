import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.IOException;

public class ChappServer {
	public static final String VERSION_NUMBER = "0.2";
	public static final String REVISION_NUMBER = "0";
	public static final int PORT_NUMBER = 22222;

	private static ServerSocket chatServer;
	public static DatabaseConnection accountDB;
	public static int numberThreads = 0;

	public static void main(String[] args) {
		System.out.println("Chapp Server");
		System.out.println("Version number: " + VERSION_NUMBER);
		System.out.println("Revision number: " + REVISION_NUMBER);

		try {
			chatServer = new ServerSocket(PORT_NUMBER);
			accountDB = new FileDatabaseConnection();
		} catch (IOException e) {
			System.out.println("Unable to connect to port: " + e.getMessage());
			System.exit(1);
		}

		run();
	}

	private static void run() {
		while (true) {
			Socket clientConnection;

			//Waits for clients to connect
			try {
				clientConnection = chatServer.accept();
				ConnectionHandler clientHandler = new ConnectionHandler(clientConnection);
				clientHandler.start();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}
}

class ConnectionHandler extends Thread {
	private int threadID;
	private Socket clientConnection;
	private Scanner clientInput;
	private PrintWriter clientOutput;

	public ConnectionHandler(Socket connection) throws IOException {
		System.out.println("Starting a new thread");
		clientConnection = connection;
		clientInput = new Scanner(clientConnection.getInputStream());
		clientOutput = new PrintWriter(clientConnection.getOutputStream());
		threadID = ChappServer.numberThreads++;
	}

	public void run() {
		try {
			System.out.println("Starting connection: " + threadID);
			// Make sure the client and server are running the same version number.
			clientOutput.println(ChappServer.VERSION_NUMBER);
			clientOutput.flush();
			String clientVersion = clientInput.next();
			if (!clientVersion.equals(ChappServer.VERSION_NUMBER)) {
				System.out.println("Version numbers do not match!\nClient version: " + clientVersion);
				clientConnection.close();
				clientOutput.close();
				clientInput.close();
				System.out.println("Connection terminated!");
				return;
			}

			String clientAction = clientInput.next();
			while (!clientAction.equals("logout")) {
				switch (clientAction) {
					case "register":
						register();
						break;
					case "login":
						login();
						break;
				}
				clientAction = clientInput.next();
			}

			clientConnection.close();
			clientInput.close();
			clientOutput.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private void login() throws IOException {
		String username = clientInput.next();
		System.out.println(username);
		String password = clientInput.next();
		System.out.println(password);

		if (!ChappServer.accountDB.login(username, password)) {
			System.out.println("Failed to log in!");
			clientOutput.println("failure");
			clientOutput.flush();
			return;
		}
		System.out.println("Log in success!");
		clientOutput.println("success");
		clientOutput.flush();
	}

	private void register() throws IOException {
		String username = clientInput.next();
		System.out.println(username);
		String password = clientInput.next();
		System.out.println(password);

		if (!ChappServer.accountDB.register(username, password)) {
			System.out.println("Failed to register!");
			clientOutput.println("failure");
			clientOutput.flush();
			return;
		}
		System.out.println("Register succeeded!");
		clientOutput.println("success");
		clientOutput.flush();
	}
}
