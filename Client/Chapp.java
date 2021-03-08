import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;

public class Chapp extends Application {
	private static Chapp chapp;

	private Stage stage;

	public static void main(String[] args) {
		if (args.length == 1) ChatClient.makeChatClient(args[0]);
		launch(args);
	}

	public void start(Stage primaryStage) {
		chapp = this;
		stage = primaryStage;
		System.out.println("Chapp Client");
		System.out.println("Version number: " + ChatClient.VERSION_NUMBER);
		System.out.println("Revision number: " + ChatClient.REVISION_NUMBER);

		LoginView loginView = new LoginView();
		primaryStage.setTitle("Login");
		primaryStage.setScene(new Scene(loginView.getPane()));
		primaryStage.setOnCloseRequest(e -> close());
		primaryStage.show();
	}

	public static Chapp getChapp() {
		return chapp;
	}

	public void setRoot(String title, Parent root) {
		stage.setTitle(title);
		stage.getScene().setRoot(root);
		stage.show();
	}

	public void close() {
		UserProfile user = UserProfile.getUserProfile();
		if (user != null) user.logout();
		ChatClient.getChatClient().disconnect();
		stage.close();
	}
}
