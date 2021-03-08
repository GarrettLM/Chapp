import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.IOException;

public class ChappServer {
	private static ServerSocket chatServer;
	public static DatabaseConnection accountDB;
	public static int numberThreads = 0;

	public static void main(String[] args) {
		System.out.println("Configuring Chapp Server...");
		try {
			Configuration.configure();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
		System.out.println("Version number: " + Configuration.getConfiguration("Version-number"));
		System.out.println("Revision number: " + Configuration.getConfiguration("Revision-number"));
		System.out.println("Connection to port " + Configuration.getConfiguration("Port-number") + "...");

		try {
			chatServer = new ServerSocket(Integer.parseInt(Configuration.getConfiguration("Port-number")));
			accountDB = DatabaseConnection.makeConnection();
		} catch (IOException e) {
			System.out.println("Unable to connect to port: " + e.getMessage());
			System.exit(1);
		}

		System.out.println("Succesfully connected to port!\nStarting run...");
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

	public static synchronized int getNextThreadNumber() {
		return numberThreads++;
	}
}
