package codes.thischwa.ddauto;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Updates the command line parameters, if required. Tasks:
 * <ul>
 * <li>Disables springdoc, is <code>--swagger.enabled=true</code> isn't set.
 * <li>Discover logback config files and set the corresponding argument,
 * e.g. <code>--logging.config=/opt/ddauto/logback.xml</code>.
 * </ul>
 */
abstract class CommandlineArgsProcessor {

	private static final String const_swagger_enabled_cli = "--swagger.enabled=true";

	private static final String working_dir = System.getProperty("user.dir");

	private static final String const_logback_name = "logback.xml";
	
	private CommandlineArgsProcessor() {
	}

	static List<String> process(String[] orgArgs) {
		return process(orgArgs, working_dir);
	}
	
	static List<String> process(String[] orgArgs, String workingDir) {
		List<String> cmdArgs = new ArrayList<>(Arrays.asList(orgArgs));
		// map swagger property
		if(!cmdArgs.remove(const_swagger_enabled_cli))
			cmdArgs.add("--springdoc.api-docs.enabled=false");

		// logback config
		Path logConfPath = Paths.get(workingDir, const_logback_name);
		if(Files.exists(logConfPath))
			cmdArgs.add("--logging.config=" + logConfPath.toFile());
		return cmdArgs;
	}
}
