import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import java.util.Hashtable;

public class LoggedInView {
	private HBox pane;
	private ProfileView profileView;
	private ChatRoomView currentRoom;
	private Hashtable<Integer, ChatRoomView> roomViews;

	public LoggedInView() {
		profileView = new ProfileView(this);
		pane = new HBox(profileView.getPane());
		roomViews = new Hashtable<Integer, ChatRoomView>();
	}

	public void selectRoom(Integer roomID) {
		ChatRoomView room = roomViews.get(roomID);
		ChatRoomMetaData metaData = ChatRoomMetaData.getRoomMetaData(roomID);
		if (room == null) {
			room = new ChatRoomView(metaData);
			roomViews.put(metaData.getRoomID(), room);
			room.enterRoom();
		}
		if (currentRoom != null) pane.getChildren().remove(currentRoom.getPane());
		currentRoom = room;
		String roomName = metaData.getRoomName();
		Chapp.getChapp().setTitle(roomName);
		pane.getChildren().add(room.getPane());
	}

	public Pane getPane() { return pane; }
}
