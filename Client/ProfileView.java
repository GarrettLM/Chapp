import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.control.TitledPane;
import java.util.ArrayList;

public class ProfileView {
	private LoggedInView view;
	private VBox pane;
	private VBox rooms;
	private VBox friends;
	private UserProfile user;

	public ProfileView(LoggedInView view) {
		this.view = view;
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
			btn.setOnAction(e -> {view.selectRoom(((RoomButton)e.getSource()).getRoomID());});
			rooms.getChildren().add(btn);
		}

		ArrayList<AccountMetaData> friendList = user.getFriendList();
		for (AccountMetaData f : friendList) {
			Button lbl = new Button(f.getUsername());
			//btn.setOnAction(e -> {view.selectRoom(((RoomButton)e.getSource()).getRoomID());});
			friends.getChildren().add(lbl);
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
