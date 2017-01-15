package defaultPackage;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

class GeneralLogger {
	static private FileHandler fileTxt;

	static void setup() throws IOException {

		// get the global logger to configure it
		Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

		LOG.setLevel(Level.WARNING);
		fileTxt = new FileHandler("Log.txt");

		// create a TXT formatter
		SimpleFormatter formatterTxt = new SimpleFormatter();
		fileTxt.setFormatter(formatterTxt);
		LOG.addHandler(fileTxt);
	}

	static void close() throws SecurityException {
		fileTxt.close();
	}}
