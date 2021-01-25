import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;

public class Chapp extends Application {

	public static void main(String[] args) {
		if (args.length == 1) ChatClient.makeChatClient(args[0]);
		launch(args);
	}

	public void start(Stage primaryStage) {
		System.out.println("Chapp Client");
		System.out.println("Version number: " + ChatClient.VERSION_NUMBER);
		System.out.println("Revision number: " + ChatClient.REVISION_NUMBER);

		LoginScene loginScene = LoginScene.makeLoginScene();
		primaryStage.setTitle("Login");
		primaryStage.setScene(loginScene);
		primaryStage.show();
	}
}

class LoginScene extends Scene {
	private TextField usernameTF;
	private PasswordField passwordTF;
	private Label responseLbl;
	private Button loginBtn, registerBtn;
	private ChatClient client;

	public static LoginScene makeLoginScene() {
		VBox pane = new VBox();
		return new LoginScene(pane);
	}

	private LoginScene(Pane root) {
		super(root);
		client = ChatClient.getChatClient();
		if (client == null) {
			System.out.println("Fatal error! client is null!");
			System.exit(1);
		}

		Label usernameLbl = new Label("Username: ");
		usernameTF = new TextField();
		HBox usernameBox = new HBox(usernameLbl, usernameTF);

		Label passwordLbl = new Label("Password: ");
		passwordTF = new PasswordField();
		HBox passwordBox = new HBox(passwordLbl, passwordTF);

		responseLbl = new Label();

		registerBtn = new Button("Register");
		registerBtn.setOnAction(e -> register());
		loginBtn = new Button("Login");
		loginBtn.setOnAction(e -> login());
		HBox buttons = new HBox(registerBtn, loginBtn);

		//VBox pane = new VBox(usernameBox, passwordBox, responseLbl, loginBtn);
		root.getChildren().addAll(usernameBox, passwordBox, responseLbl, buttons);
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

	private void register() {
		String username = usernameTF.getText().trim();
		String password = passwordTF.getText().trim();

		if (username.equals("") || password.equals("")) {
			responseLbl.setText("Enter a valid username and password!");
			return;
		}

		if (client.register(username, password)) {
			responseLbl.setText("Successfully registered account!");
		} else {
			responseLbl.setText("Registration failed!");
		}
	}
}
