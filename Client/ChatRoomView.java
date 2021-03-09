import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;

public class ChatRoomView implements ChatProxyViewBridge {
	private BorderPane pane;
	private VBox chatView;
	private TextField chatField;
	private Button submitMessageBtn;
	//private Integer roomID;
	private ChatRoomProxy proxy;

	public ChatRoomView(ChatRoomMetaData metaData) {
		//this.roomID = roomID;
		pane = new BorderPane();
		chatView = new VBox();
		pane.setCenter(chatView);
		chatField = new TextField();
		submitMessageBtn = new Button("Send");
		submitMessageBtn.setOnAction(e -> sendMessage());
		HBox chatInput = new HBox(chatField, submitMessageBtn);
		pane.setBottom(chatInput);
		proxy = ChatRoomProxy.enterRoom(metaData);
	}

	public void enterRoom() {
		proxy.setView(this);
		proxy.enterRoom();
	}

	public void sendMessage() {
		String message = chatField.getText().trim();
		//Only send a message if there is one to send!
		if (!message.equals("")) //ChatClient.getChatClient().postMessage(roomID, message);
			proxy.sendMessage(message);
		chatField.setText("");
	}

	public void displayMessage(String message) {
		Platform.runLater(new Runnable() {
			public void run() {
		 		chatView.getChildren().add(new Label(message));
		 	}
		});
	}

	public Pane getPane() { return pane; }
}
