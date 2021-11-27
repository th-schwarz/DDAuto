package codes.thischwa.ddauto;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Updates the command line parameters, if required. Tasks:
 * <ul>
 * <li>Disables springdoc, is <code>--swagger.enabled=true</code> isn't set.
 * <li>Discover log4j2 config files and set the corrisponding arguments.
 * </ul>
 */
class CommandlineArgsProcessor {
	
	private static final String const_swagger_enabled_cli = "--swagger.enabled=true";

	private static final String const_log4j2_cli = "--logging.log4j2.config.override";

	private static final String const_log4j2_name = "log4j2.xml";
	private static final String const_log4j2_zone_name = "log4j2_zone.xml";
	
	// format string to generate an argument like: --logging.log4j2.config.override[1]=log4j2_ddauto-zone.xml
	private static final String const_log4j2_param_format = const_log4j2_cli +"[%d]=%s";

	static List<String> process(String[] orgArgs) {
		List<String> cmdArgs = new ArrayList<>(Arrays.asList(orgArgs));
		// map swagger property
		if(!cmdArgs.remove(const_swagger_enabled_cli)) 
			cmdArgs.add("--springdoc.api-docs.enabled=false");
		
		// check and add the log4j config
		int log4j2ArgsCount = cmdArgs.stream().filter(arg -> arg.startsWith(const_log4j2_cli))
				.collect(Collectors.toList()).size();
		if(Files.exists(Paths.get(const_log4j2_name)))
			cmdArgs.add(String.format(const_log4j2_param_format, log4j2ArgsCount++, const_log4j2_name));
		if(Files.exists(Paths.get(const_log4j2_zone_name)))
			cmdArgs.add(String.format(const_log4j2_param_format, log4j2ArgsCount++, const_log4j2_zone_name));
		return cmdArgs;
	}
}
