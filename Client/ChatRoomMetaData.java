public class ChatRoomMetaData {
	private Integer roomID;
	private String roomName;

	public ChatRoomMetaData(Integer roomID, String roomName) {
		this.roomID = roomID;
		this.roomName = roomName;
	}

	public Integer getRoomID() { return roomID; }
	public String getRoomName() { return roomName; }

	public static ChatRoomMetaData[] buildList(String listString) {
		String[] temp = listString.split(";");
		ChatRoomMetaData[] list = new ChatRoomMetaData[temp.length / 2];
		for (int i = 0; i < temp.length; i += 2) {
			list[i/2] = new ChatRoomMetaData(new Integer(temp[i]), temp[i+1]);
		}
		return list;
	}
}
