import java.net.Socket;
import java.net.InetAddress;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.IOException;

/* This class is used to handle communication with the server. */
public class ChatClient {
	public static final String VERSION_NUMBER = "0.3";
	public static final String REVISION_NUMBER = "0";
	private static final int SERVER_PORT = 22222;
	private static ChatClient client;	//Uses the singleton pattern to make a single instance of the ChatClient class

	private InetAddress serverAddr;
	private Socket serverConnection;
	private PrintWriter output;
	private Scanner input;

	public static boolean makeChatClient(String serverIPAddress) {
		if (client != null) return false;
		client = new ChatClient(serverIPAddress);
		return true;
	}

	public static ChatClient getChatClient() {
		if (client == null) client = new ChatClient();
		return client;
	}

	/*	Creates a ChatClient object and stablishes a connection with a server on the local machine. */
	private ChatClient() {
		try {
			serverAddr = InetAddress.getLocalHost();
			serverConnection = new Socket(serverAddr, SERVER_PORT);
			output = new PrintWriter(serverConnection.getOutputStream(), true);
			input = new Scanner(serverConnection.getInputStream());

			// Make sure the client and server are running the same version number.
			output.println(VERSION_NUMBER);
			String serverVersion = input.next();
			if (!serverVersion.equals(VERSION_NUMBER)) {
				System.out.println("Version numbers do not match!\nServer version: " + serverVersion);
				serverConnection.close();
				output.close();
				input.close();
				System.out.println("Connection terminated!\nShutting down...");
				System.exit(0);
			}

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private ChatClient(String serverIPAddress) {
		try {
			serverAddr = InetAddress.getByName(serverIPAddress);
			serverConnection = new Socket(serverAddr, SERVER_PORT);
			output = new PrintWriter(serverConnection.getOutputStream(), true);
			input = new Scanner(serverConnection.getInputStream());

			// Make sure the client and server are running the same version number.
			send(VERSION_NUMBER);
			String serverVersion = input.next();
			if (!serverVersion.equals(VERSION_NUMBER)) {
				System.out.println("Version numbers do not match!\nServer version: " + serverVersion);
				serverConnection.close();
				output.close();
				input.close();
				System.out.println("Connection terminated!\nShutting down...");
				System.exit(0);
			}

		} catch (IOException e) {
			System.out.println("e.getMessage()");
		}
	}

	/*	Accepts a username and password pair to send to the server.
		Returns true if the pair was accepted. */
	public AccountMetaData login(String username, String password) {
		send("login " + username + " " + password);
		System.out.println("Waiting for response");
		String responseLine = "";
		while (responseLine.equals(""))
			responseLine = input.nextLine();
		System.out.println(responseLine);
		//String[] response = input.nextLine().split(" ");
		String[] response = responseLine.split(" ");
		if (!response[0].equals("success")) return null;
		//Thread handler = new Thread(new ServerHandler(input));
		//handler.start();
		return new AccountMetaData(new Integer(response[1]), username);
	}

	public ChatRoomMetaData[] getRooms() {
		output.println("user-info rooms");
		output.flush();
		String responseLine;
		do {
			responseLine = input.nextLine();
		} while (responseLine.equals(""));
		return ChatRoomMetaData.buildList(responseLine);
	}

	public void handleConnection() {
		Thread handler = new Thread(new ServerHandler(input));
		handler.start();
	}

	/*	Accepts a username and password pair to send to the server.
		Returns true if the pair was accepted. */
	public boolean register(String username, String password) {
		send("register " + username + " " + password);
		System.out.println("Waiting for response");
		return (input.next().equals("success"));
	}

	/* Logs the client out of the chat room and shuts down the connection with the server. */
	public void logout() {
		send("logout");
		/*try {
			output.println("logout");
			output.flush();
			//serverConnection.close();
			//output.close();
			//input.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}*/
	}

	public void disconnect() {
		send("disconnet");
		try {
			output.flush();
			output.close();
			input.close();
			serverConnection.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void enterRoom(Integer roomID) {
		send("enter " + roomID.toString());
	}

	public void sendMessage(Integer roomID, String message) {
		send("message " + roomID.toString() + " " + message);
	}

	private void send(String message) {
		try {
			output.println(message);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}

class ServerHandler implements Runnable {
	private Scanner input;

	public ServerHandler(Scanner input) {
		this.input = input;
	}

	public void run() {
		try {
			String next = input.nextLine();
			while (!next.equals("logout")) {
				String[] line = next.split(";");
				switch (line[0]) {
					case "message":
						ChatRoomProxy proxy = ChatRoomProxy.getChatRoomProxy(new Integer(line[1]));
						proxy.recieveMessage(line[2]);
						break;
				}
				//WindowBridge.notify("default", next);
				next = input.nextLine();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
