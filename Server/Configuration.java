import java.util.Scanner;
import java.io.IOException;
import java.io.File;
import java.util.Hashtable;

public class Configuration {
	private static final String CONFIGURATION_FILE = "config";
	private static final Hashtable<String, String> configurations = new Hashtable<String, String>();

	//The configure method should be called once at the start of the program and never again!
	public static void configure() throws IOException {
		Scanner configStream = new Scanner(new File(CONFIGURATION_FILE));
		while (configStream.hasNext()) {
			String line = configStream.nextLine().trim();
			if (line.startsWith(";") || line.equals("")) continue;
			String[] configuration = line.split(": ");
			configurations.put(configuration[0], configuration[1]);
		}
		configStream.close();
	}

	public static String getConfiguration(String configurationName) {
		return configurations.get(configurationName);
	}
}
