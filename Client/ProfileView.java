import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.control.TitledPane;
import java.util.ArrayList;

public class ProfileView {
	private VBox pane;
	private VBox rooms;
	private VBox friends;
	private UserProfile user;

	public ProfileView() {
		user = UserProfile.getUserProfile();
		TextField usernameBox = new TextField(user.getData().getUsername());
		usernameBox.setEditable(false);
		Button logoutBtn = new Button("logout");
		logoutBtn.setOnAction(e -> logout());
		HBox profileBox = new HBox(usernameBox, logoutBtn);

		rooms = new VBox();
		friends = new VBox();
		TitledPane friendsPane = new TitledPane("Friends", friends);
		TitledPane roomsPane = new TitledPane("Rooms", rooms);
		ArrayList<ChatRoomMetaData> roomList = user.getRoomList();
		for (ChatRoomMetaData r : roomList) {
			RoomButton btn = new RoomButton(r);
			btn.setOnAction(e -> {ChatRoomProxy.enterRoom(((RoomButton)e.getSource()).getRoomID());});
			rooms.getChildren().add(btn);
		}

		pane = new VBox(profileBox, roomsPane, friendsPane);		
	}

	public void logout() {
		user.logout();
		LoginView view = new LoginView();
		Chapp.getChapp().setRoot("Login", view.getPane());
	}

	public Pane getPane() { return pane; }
}

class RoomButton extends Button {
	private Integer roomID;

	public RoomButton(ChatRoomMetaData metaData) {
		super(metaData.getRoomName());
		this.roomID = metaData.getRoomID();
	}

	public Integer getRoomID() { return roomID; }
}
