import java.util.ArrayList;

public class UserProfile {
	private static UserProfile user = null;

	private AccountMetaData data;
	private ArrayList<ChatRoomMetaData> rooms;
	//private ArrayList<AccountMetaData> friends;

	private UserProfile(AccountMetaData data) {
		this.data = data;
		rooms = new ArrayList<ChatRoomMetaData>();
	}

	public AccountMetaData getData() { return data; }

	public void setRoomList(ChatRoomMetaData[] list) {
		for (int i = 0; i < list.length; i++) rooms.add(list[i]);
	}

	public ArrayList<ChatRoomMetaData> getRoomList() {
		return rooms;
	}

	public boolean changeUsername(String newUsername) {
		return false;
	}

	public boolean changePassword(String currentPW, String newPW) {
		return false;
	}

	public void logout() {
		user = null;
		ChatClient.getChatClient().logout();
	}

	public static UserProfile login(String username, String password) {
		ChatClient client = ChatClient.getChatClient();
		AccountMetaData data = client.login(username, password);
		if (data == null) return null;
		AccountMetaData.register(data);
		user = new UserProfile(data);
		user.setRoomList(client.getRooms());
		client.handleConnection();
		return user;
	}

	public static boolean register(String username, String password) {
		return ChatClient.getChatClient().register(username, password);
	}

	public static UserProfile getUserProfile() { return user; }
}
