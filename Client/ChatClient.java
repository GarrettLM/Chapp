import java.net.Socket;
import java.net.InetAddress;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.IOException;

/* This class is used to handle communication with the server. */
public class ChatClient {
	private static final int SERVER_PORT = 22222;

	private InetAddress serverAddr;
	private Socket serverConnection;
	private PrintWriter output;
	private Scanner input;

	/*	Creates a ChatClient object and stablishes a connection with a server on the local machine. */
	public ChatClient() {
		try {
			serverAddr = InetAddress.getLocalHost();
			serverConnection = new Socket(serverAddr, SERVER_PORT);
			output = new PrintWriter(serverConnection.getOutputStream());
			input = new Scanner(serverConnection.getInputStream());
		} catch (IOException e) {
			System.out.println("e.getMessage()");
		}
	}

	/*	Accepts a username and password pair to send to the server.
		Returns true if the pair was accepted. */
	public boolean login(String username, String password) {
		output.println(username);
		output.flush();
		output.println(password);
		output.flush();
		System.out.println("Waiting for response");
		return (input.next().equals("success"));
	}

	/* Logs the client out of the chat room and shuts down the connection with the server. */
	public void logout() {
		try {
			serverConnection.close();
			output.close();
			input.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
