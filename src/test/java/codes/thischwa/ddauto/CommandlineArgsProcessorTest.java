package codes.thischwa.ddauto;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

class CommandlineArgsProcessorTest {
	
	final List<String> testArgs = Collections.unmodifiableList(Arrays.asList("--logging.log4j2.config.override=log.xml", "--swagger.enabled=true"));

	@Test
	final void testProcess_swagger() {
		List<String> args = CommandlineArgsProcessor.process(testArgs.toArray(new String[testArgs.size()]));
		assertFalse(args.contains("--swagger.enabled=true"));
		assertFalse(args.contains("--springdoc.api-docs.enabled=false"));
		
		List<String> newArgs = new ArrayList<>(testArgs);
		newArgs.remove("--swagger.enabled=true");
		args = CommandlineArgsProcessor.process(newArgs.toArray(new String[newArgs.size()]));
		assertTrue(args.contains("--springdoc.api-docs.enabled=false"));
	}

	@Test
	final void testProcess_log4j2() throws Exception {
		System.out.println("PWD: " + CommandlineArgsProcessor.workingDir);
		CommandlineArgsProcessor.workingDir = "test-dir";
		List<String> args = CommandlineArgsProcessor.process(testArgs.toArray(new String[testArgs.size()]));
		if(!Files.exists(Paths.get("test-dir/log4j2.xml")))
			throw new FileNotFoundException("missing test-dir/log4j2.xml");
		assertTrue(args.contains("--logging.log4j2.config.override[1]=test-dir/log4j2.xml"));
		assertTrue(args.contains("--logging.log4j2.config.override[2]=test-dir/log4j2_zone.xml"));
	}
}
