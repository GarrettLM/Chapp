import java.util.Hashtable;
import java.util.ArrayList;

public class ChatRoomProxy {
	private static Hashtable<Integer, ChatRoomProxy> roomRegistry = new Hashtable<Integer, ChatRoomProxy>();

	private ChatRoomMetaData metaData;
	private ChatClient connection;
	private ArrayList<AccountMetaData> members;

	public static ChatRoomProxy getChatRoomProxy(Integer roomID) {
		return roomRegistry.get(roomID);
	}

	public ChatRoomProxy(ChatRoomMetaData metaData, ChatClient connection) {
		this.metaData = metaData;
		this.connection = connection;
		//members = Profile.getMembersList(roomID);
	}

	public void sendMessage(String message) {
		//connection.sendMessage(metaData.getRoomID(), message);
	}

	public void recieveMessage(String message) {
	}
	public static  void enterRoom(Integer roomID) {}
}
