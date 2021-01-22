import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;

public class Chapp extends Application {
	private TextField usernameTF;
	private PasswordField passwordTF;
	private Label responseLbl;
	private Button loginBtn;
	private ChatClient client;

	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage primaryStage) {
		System.out.println("Chapp Client");
		System.out.println("Version number: " + ChatClient.VERSION_NUMBER);
		System.out.println("Revision number: " + ChatClient.REVISION_NUMBER);

		client = new ChatClient();

		Label usernameLbl = new Label("Username: ");
		usernameTF = new TextField();
		HBox usernameBox = new HBox(usernameLbl, usernameTF);

		Label passwordLbl = new Label("Password: ");
		passwordTF = new PasswordField();
		HBox passwordBox = new HBox(passwordLbl, passwordTF);

		responseLbl = new Label();
		loginBtn = new Button("Login");
		loginBtn.setOnAction(e -> login());

		VBox pane = new VBox(usernameBox, passwordBox, responseLbl, loginBtn);

		Scene loginScene = new Scene(pane);
		primaryStage.setTitle("Login");
		//primaryStage.setMaximized(true);
		primaryStage.setScene(loginScene);
		primaryStage.show();
	}

	private void login() {
		String username = usernameTF.getText().trim();
		String password = passwordTF.getText().trim();

		if (username.equals("") || password.equals("")) {
			responseLbl.setText("Enter a valid username and password!");
			return;
		}

		if (client.login(username, password)) {
			responseLbl.setText("Successfully logged in!");
			client.logout();
		} else {
			responseLbl.setText("Username and password combo did not match!");
		}
	}
}
