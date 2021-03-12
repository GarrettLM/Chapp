import java.net.Socket;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Collection;

public class ConnectionHandler extends Thread {
	private Integer userID;
	private int threadID;
	private Socket clientConnection;
	private Scanner clientInput;
	private PrintWriter clientOutput;
	private Hashtable<Integer, ChatRoomServer> rooms;

	public ConnectionHandler(Socket connection) throws IOException {
		System.out.println("Starting a new thread");
		clientConnection = connection;
		clientInput = new Scanner(clientConnection.getInputStream());
		clientOutput = new PrintWriter(clientConnection.getOutputStream());
		threadID = ChappServer.getNextThreadNumber();
		rooms = new Hashtable<Integer, ChatRoomServer>();
	}

	public void run() {
		try {
			System.out.println("Starting connection: " + threadID);
			// Make sure the client and server are running the same version number.
			clientOutput.println(Configuration.getConfiguration("Version-number"));
			clientOutput.flush();
			String clientVersion = clientInput.next();
			if (!clientVersion.equals(Configuration.getConfiguration("Version-number"))) {
				System.out.println("Version numbers do not match!\nClient version: " + clientVersion);
			} else {
				String flag;
				do {
					flag = clientInput.next();
					if (flag.equals("register")) register();
					else if (flag.equals("login"))
						if (login()) handleConnection();
				} while (!flag.equals("disconnect"));
			}
			clientConnection.close();
			clientOutput.close();
			clientInput.close();
			System.out.println("Connection terminated!");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private boolean login() throws IOException {
		DatabaseConnection database = DatabaseConnection.makeConnection();
		String username = clientInput.next();
		System.out.println(username);
		String password = clientInput.next();
		System.out.println(password);
		userID = database.login(username, password);

		if (userID == null) {
			System.out.println("Failed to log in!");
			sendMessage("failure");
			return false;
		}
		System.out.println("Log in success!");
		sendMessage("success " + userID);
		while (clientInput.nextLine().equals(""));
		sendMessage(database.getRoomList(userID));
		sendMessage(database.getFriendList(userID));
		return true;
	}

	private void logout() {
		System.out.println("Logging out...");
		Collection<ChatRoomServer> temp = rooms.values();
		for (ChatRoomServer room : temp) room.exit(this);
		rooms.clear();
		userID = null;
		try {
		sendMessage("logout");
		} catch (Exception e) {}
	}

	private boolean register() throws IOException {
		String username = clientInput.next();
		System.out.println(username);
		String password = clientInput.next();
		System.out.println(password);

		if (!ChappServer.accountDB.register(username, password)) {
			System.out.println("Failed to register!");
			sendMessage("failure");
			return false;
		}
		System.out.println("Register succeeded!");
		sendMessage("success");
		return true;
	}

	private void handleConnection() {
		System.out.println("Handling connection...");
		String clientAction = clientInput.next();
		System.out.println(clientAction);
		while (!clientAction.equals("logout")) {
			//System.out.println("Handling connection...");
			switch (clientAction) {
				case "enter": enterRoom(); break;
				case "exit": exitRoom(); break;
				case "message": messageRoom(); break;
				case "create": createRoom(); break;
				/*case default:
					System.out.printf("Action %s not recognized!\n", clientAction);*/
			}
			//clientAction = clientInput.next();
			System.out.println("Handling connection...");
		clientAction = clientInput.next();
		System.out.println(clientAction);
		}
		logout();
	}

	private void enterRoom() {
		Integer roomID = new Integer(clientInput.next());
		ChatRoomServer room = ChatRoomServer.getChatRoom(roomID);
		try {
			if (room == null) sendMessage("failure enter " + roomID);
			else {
				rooms.put(roomID, room);
				room.enter(this);
			}
		} catch (IOException e) { System.out.println(e.getMessage()); }
	}

	private void exitRoom() {
		Integer roomID = new Integer(clientInput.next());
		ChatRoomServer room = rooms.remove(roomID);
		try {
			if (room == null) sendMessage("failure exit " + roomID);
			else room.exit(this);
		} catch (IOException e) { System.out.println(e.getMessage()); }
	}

	private void messageRoom() {
		Integer roomID = new Integer(clientInput.next());
		String message = clientInput.nextLine();
		rooms.get(roomID).post(userID + ":" + message);
	}

	private void createRoom(){}

	public synchronized void sendMessage(String message) throws IOException {
		clientOutput.println(message);
		clientOutput.flush();
	}

	public Integer getUserID() {
		return userID;
	}
}
