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
		profileView = new ProfileView();
		pane = new HBox(profileView.getPane());
		roomViews = new Hashtable<Integer, ChatRoomView>();
	}

	/*public void setRoomView(Integer roomID) {
		ChatRoomView room = roomViews.get(Integer);
		if (room == null) {
			room = new ChatRoomView(roomID);
			String roomName = roomID.toString();
			Button roomButton = new Button(roomName);
			roomButton.setOnAction(e -> setRoomView(Integer(roomButton.getText())));
			rooms.getChildren().add(roomButton);
		}
		if (current != null) pane.getChildren().remove(currentRoom.getPane());
		currentRoom = room;
		pane.getChildren().add(room.getPane());
	}*/

	public Pane getPane() { return pane; }
}
