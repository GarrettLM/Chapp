import java.util.Hashtable;
import java.util.ArrayList;

public class ChatRoomProxy {
	private static Hashtable<Integer, ChatRoomProxy> roomRegistry = new Hashtable<Integer, ChatRoomProxy>();

	private ChatRoomMetaData metaData;
	private ChatClient connection;
	private ArrayList<AccountMetaData> members;
	private ChatProxyViewBridge view;

	public static ChatRoomProxy getChatRoomProxy(Integer roomID) {
		return roomRegistry.get(roomID);
	}

	public ChatRoomProxy(ChatRoomMetaData metaData, ChatClient connection) {
		this.metaData = metaData;
		this.connection = connection;
		//members = Profile.getMembersList(roomID);
	}

	public void sendMessage(String message) {
		connection.sendMessage(metaData.getRoomID(), message);
	}

	public void recieveMessage(String message) {
		//view.displayMessage(message);
		System.out.println(message);
	}

	public void setView(ChatProxyViewBridge view) {
		this.view = view;
	}

	public void enterRoom() {
		connection.enterRoom(metaData.getRoomID());
	}

	public static ChatRoomProxy enterRoom(ChatRoomMetaData metaData) {
		//ChatRoomMetaData metadata = ChatRoomMetaData.getRoomMetaData(roomID);
		ChatClient connection = ChatClient.getChatClient();
		ChatRoomProxy proxy = new ChatRoomProxy(metaData, connection);
		roomRegistry.put(metaData.getRoomID(), proxy);
		//connection.enterRoom(metaData.getRoomID());
		return proxy;
	}
}

interface ChatProxyViewBridge {
	public void displayMessage(String message);
}
