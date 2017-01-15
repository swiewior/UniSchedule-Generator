package defaultPackage;

import schedule.Generator;

import java.io.IOException;

public class Main {
	public static void main(final String[] argv)  {

		// Setup Logs
		try {
			GeneralLogger.setup();
		} catch (SecurityException | IOException e) {
			e.printStackTrace(System.out);
		}

		// Run Generator
		new Generator();

		GeneralLogger.close();
	}
}

// https://google.github.io/styleguide/javaguide.html