import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;

public class ChatRoomServer {
	private final static ConcurrentHashMap<Integer, ChatRoomServer> activeRooms = new ConcurrentHashMap<Integer, ChatRoomServer>();

	private ArrayList<ConnectionHandler> members;
	private int roomID;

	public static ChatRoomServer getChatRoom(Integer roomID) {
		ChatRoomServer server = activeRooms.get(roomID);
		if (server == null) {
			server = new ChatRoomServer(roomID.intValue());
			ChatRoomServer temp = activeRooms.putIfAbsent(roomID, server);
			if (temp != null) server = temp;
		}
		return server;
	}

	public ChatRoomServer(int roomID) {
		this.roomID = roomID;
		members = new ArrayList<ConnectionHandler>();
	}

	public synchronized boolean enter(ConnectionHandler client) {
		String enterMsg = "event-entered\n" + roomID + "\n" + client.getUserID();
		synchronized(client) {
			try {
				client.sendMessage(enterMsg);
				client.sendMessage((new Integer(members.size())).toString());
			} catch (IOException e) {
				System.out.println(e.getMessage());
				return false;
			}
			for(ConnectionHandler member : members) {
				try {
					member.sendMessage(enterMsg);
					client.sendMessage(member.getUserID().toString());
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
			}
			members.add(client);
			return true;
		}
		//Send the messages in the room to the client
	}

	public synchronized void exit(ConnectionHandler member) {
		String exitMsg = "event-exited\n" + roomID + "\n" + member.getUserID();
		for (ConnectionHandler temp : members) {
			try {
				temp.sendMessage(exitMsg);
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
		members.remove(member);
	}

	public synchronized void post(String message) {
		String dataMsg = "message\n" + roomID + "\n" + message;
		for (ConnectionHandler temp : members) {
			try {
				temp.sendMessage(dataMsg);
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}
}

class ChatRoomMetaData {
	public final String roomname;
	public final int roomID;

	public ChatRoomMetaData(String roomname, int roomID) {
		this.roomname = roomname;
		this.roomID = roomID;
	}

	public String toString() {
		return roomname + ";" + roomID; 
	}
}
