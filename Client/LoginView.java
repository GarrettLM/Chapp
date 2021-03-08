import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;

public class LoginView {
	private VBox pane;
	private TextField usernameTF;
	private PasswordField passwordTF;
	private Label responseLbl;
	private Button loginBtn, registerBtn;
	private ChatClient client;

	public LoginView() {
		pane = new VBox();
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
		pane.getChildren().addAll(usernameBox, passwordBox, responseLbl, buttons);
	}

	public void login() {
		String username = usernameTF.getText().trim();
		String password = passwordTF.getText().trim();

		if (username.equals("") || password.equals("")) {
			responseLbl.setText("Enter a valid username and password!");
			return;
		}

		if (UserProfile.login(username, password) != null) {
			responseLbl.setText("Successfully logged in!");
			LoggedInView view = new LoggedInView();
			Chapp.getChapp().setRoot(UserProfile.getUserProfile().getData().getUsername(), view.getPane());
			//client.start();
			//client.logout();
		} else {
			responseLbl.setText("Username and password combo did not match!");
		}
	}

	public void register() {
		String username = usernameTF.getText().trim();
		String password = passwordTF.getText().trim();

		if (username.equals("") || password.equals("")) {
			responseLbl.setText("Enter a valid username and password!");
			return;
		}

		if (UserProfile.register(username, password)) {
			responseLbl.setText("Successfully registered account!");
		} else {
			responseLbl.setText("Registration failed!");
		}
	}

	public Pane getPane() { return pane; }
}
